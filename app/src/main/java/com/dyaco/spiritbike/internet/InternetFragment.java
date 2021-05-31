package com.dyaco.spiritbike.internet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;

import android.os.Looper;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.MainActivity;
import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.mirroring.FloatingDashboardService;
import com.dyaco.spiritbike.settings.UpdateBean;
import com.dyaco.spiritbike.settings.UpdateSoftwareActivity;
import com.dyaco.spiritbike.settings.appupdate.AppUpadteActivity;
import com.dyaco.spiritbike.settings.appupdate.AppUpdateData;
import com.dyaco.spiritbike.settings.appupdate.AppUpdateEvent;
import com.dyaco.spiritbike.settings.appupdate.AppUpdateManager;
import com.dyaco.spiritbike.support.BaseFragment;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.LogS;
import com.dyaco.spiritbike.support.RxTimer;
import com.dyaco.spiritbike.support.banner.util.LogUtils;
import com.dyaco.spiritbike.support.rxtimer.PackageManagerUtils;
import com.dyaco.spiritbike.webapi.BaseApi;
import com.dyaco.spiritbike.webapi.IServiceApi;
import com.dyaco.spiritbike.workout.WorkoutDashboardActivity;
import com.google.gson.Gson;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import im.delight.android.webview.AdvancedWebView;

import static android.content.Context.WINDOW_SERVICE;
import static com.dyaco.spiritbike.MyApplication.getInstance;
import static com.dyaco.spiritbike.MyApplication.updateNotify;
import static com.dyaco.spiritbike.support.CommonUtils.checkSwVersion;
import static com.dyaco.spiritbike.support.CommonUtils.convertSwVersion;
import static com.dyaco.spiritbike.support.CommonUtils.isConnected;
import static com.dyaco.spiritbike.support.CommonUtils.showToastAlert;


public class InternetFragment extends BaseFragment implements AdvancedWebView.Listener {
    private ViewStub viewStub;
    private ProgressBar progressBar;
    private TextView tvTitle;
    // private WebView m_wvBrowser;
    private AdvancedWebView m_wvBrowser;
    private Button m_btFullScreenExit_InternetDashboard;
    private ConstraintLayout m_clViewTopBar_InternetDashboard;
    private ConstraintLayout m_clView_InternetDashboard;
    private ConstraintLayout m_clList_InternetDashboard;
    private boolean m_bOnClick;
    private int m_nLastX, m_nLastY;
    private long m_lStartTime;
    private int m_nScreenWidth;
    private int m_nScreenHeight;
    private boolean isWorkout;

    private Group groupV;
    private ImageButton btNetflix_InternetDashboard;
    private ImageButton btHulu_InternetDashboard;
    private ImageButton btYoutube_InternetDashboard;
    private ImageButton btABC_InternetDashboard;
    private ImageButton btNBC_InternetDashboard;
    private ImageButton btCNN_InternetDashboard;
    private ImageButton btFoxNews_InternetDashboard;
    private WebSettings webSettings;

    private String mNetflixUrl;
    private MyChrome myChrome;

    private PackageManagerUtils packageManagerUtils;
    private AppUpdateData appUpdateData;
    private List<AppUpdateData.AppUpdateBean> appUpdateBeans;

    private AppUpdateReceiver appUpdateReceiver = new AppUpdateReceiver();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isWorkout = getArguments().getBoolean("isWorkout", false);
            LogUtils.d(getCurrentFragmentName() + "isWorkout:" + isWorkout);


        }

        packageManagerUtils = new PackageManagerUtils();
        initTestData();
        LogUtils.d(getCurrentFragmentName() + "onCreate()");
    }


    @Override
    public void onAttach(@NonNull Activity activity) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        activity.registerReceiver(appUpdateReceiver, intentFilter);
        super.onAttach(activity);
    }


//    https://ucaremedi-ugym.s3-ap-northeast-1.amazonaws.com/spirit/production/update.json
    private void initTestData() {
        List<AppUpdateData.AppUpdateBean> appUpdateBeanList = new ArrayList<>();
        appUpdateBeanList.add(new AppUpdateData.AppUpdateBean(" 7.87.2", 0, AppUpdateManager.PACKAGE_NAME_NETFLIX, "", "https://ucaremedi-ugym.s3-ap-northeast-1.amazonaws.com/spirit/production/netflix_v7.87.2.apk", "YES", "netflix_v7.87.2.apk"));
        appUpdateBeanList.add(new AppUpdateData.AppUpdateBean("4.26.0", 5673, AppUpdateManager.PACKAGE_NAME_HULU, "", "https://ucaremedi-ugym.s3-ap-northeast-1.amazonaws.com/spirit/production/hulu_v4.26.0.apk", "NO", "hulu_v4.26.0.apk"));
        appUpdateBeanList.add(new AppUpdateData.AppUpdateBean("5.5.9", 53300099, AppUpdateManager.PACKAGE_NAME_ABC_NEWS, "", "https://ucaremedi-ugym.s3-ap-northeast-1.amazonaws.com/spirit/production/abc_news_v5.5.9.apk", "NO", "abc_news_v5.5.9.apk"));
        appUpdateBeanList.add(new AppUpdateData.AppUpdateBean("6.0.21", 1720210072, AppUpdateManager.PACKAGE_NAME_MSNBC, "", "https://ucaremedi-ugym.s3-ap-northeast-1.amazonaws.com/spirit/production/nbc_news_v6.0.21.apk", "NO", "nbc_news_v6.0.21.apk"));
        appUpdateBeanList.add(new AppUpdateData.AppUpdateBean("6.15.2", 0, AppUpdateManager.PACKAGE_NAME_CNN, "", "https://ucaremedi-ugym.s3-ap-northeast-1.amazonaws.com/spirit/production/cnn_v6.15.2.apk", "YES", "cnn_v6.15.2.apk"));
        appUpdateBeanList.add(new AppUpdateData.AppUpdateBean("4.27.0", 2021051901, AppUpdateManager.PACKAGE_NAME_FOX_NEWS, "", "https://ucaremedi-ugym.s3-ap-northeast-1.amazonaws.com/spirit/production/fox_news_v4.27.0.apk", "NO", "fox_news_v4.27.0.apk"));



//        appUpdateBeanList.add(new AppUpdateData.AppUpdateBean(" 7.87.2", 0, AppUpdateManager.PACKAGE_NAME_NETFLIX, "", "https://dl5.apksum.com/download/com.netflix.mediaclient-7.87.2-free?dv=e17c044eec5b0983d55f9e62eda4ede9&st=1622427108", "YES", "netflix_v7.87.2.apk"));
//        appUpdateBeanList.add(new AppUpdateData.AppUpdateBean("4.26.0", 5673, AppUpdateManager.PACKAGE_NAME_HULU, "", "https://www.apkmirror.com/wp-content/uploads/2021/05/01/60aee0df30dac/com.hulu.plus_4.26.0_5673-google-4005673_minAPI21(nodpi)_apkmirror.com.apk?verify=1622428519-ifJSGxgovKPuvxykSii9EfcionU81WhaX2jE4gsGCb0", "NO", "hulu_v4.26.0.apk"));
//        appUpdateBeanList.add(new AppUpdateData.AppUpdateBean("5.5.9", 53300099, AppUpdateManager.PACKAGE_NAME_ABC_NEWS, "", "https://www.apkmirror.com/wp-content/uploads/2021/05/53/60abad90761d4/com.abc.abcnews_5.5.9-53300099_minAPI21(arm64-v8a,armeabi-v7a,x86,x86_64)(nodpi)_apkmirror.com.apk?verify=1622428718-BfRY_7fJX3uY-n9m4w0OqoaoRHudsI-CZ_-Q6eujV7g", "NO", "abc_news_v5.5.9.apk"));
//        appUpdateBeanList.add(new AppUpdateData.AppUpdateBean("6.0.21", 1720210072, AppUpdateManager.PACKAGE_NAME_MSNBC, "", "https://www.apkmirror.com/wp-content/uploads/2021/03/20/604121a8ce104/com.zumobi.msnbc_6.0.21-1720210072_minAPI21(nodpi)_apkmirror.com.apk?verify=1622428919-JADLaM3EYCA5n9sSf8r4h2oJaxgYhKiZwz-V0d9FHXs", "NO", "nbc_news_v6.0.21.apk"));
//        appUpdateBeanList.add(new AppUpdateData.AppUpdateBean("6.15.2", 0, AppUpdateManager.PACKAGE_NAME_CNN, "", "https://dl5.apksum.com/download/com.cnn.mobile.android.phone-6.15.2-free?dv=5a57ebd9af0c74c2ad07cbce3b7f5a25&st=1622427155", "YES", "cnn_v6.15.2.apk"));
//        appUpdateBeanList.add(new AppUpdateData.AppUpdateBean("4.27.0", 2021051901, AppUpdateManager.PACKAGE_NAME_FOX_NEWS, "", "https://www.apkmirror.com/wp-content/uploads/2021/05/68/60ae68c902752/com.foxnews.android_4.27.0-2021051901_minAPI23(arm64-v8a,armeabi-v7a,x86,x86_64)(nodpi)_apkmirror.com.apk?verify=1622429182-NkTLtvDI6gwqcjqhceKcPTZ8X3O7auwct_ccRZKLDNQ", "NO", "fox_news_v4.27.0.apk"));

        AppUpdateData testData = new AppUpdateData(appUpdateBeanList);
        String dataJson = new Gson().toJson(testData);
//        com.netflix.mediaclient

        appUpdateData = new Gson().fromJson(dataJson, AppUpdateData.class);
        appUpdateBeans = appUpdateData.getAppUpdateBeans();
//        MyApplication.getInstance().setAppUpdateData(appUpdateData);


        String testNetflix = AppUpdateManager.getInstance(getActivity()).getAppUpdateVersion(appUpdateBeans, AppUpdateManager.PACKAGE_NAME_NETFLIX);
        String testDownloadUrl = AppUpdateManager.getInstance(getActivity()).getAppUpdateDownloadUrl(appUpdateBeans, AppUpdateManager.PACKAGE_NAME_NETFLIX);
        boolean testIsHultForceUpdates = AppUpdateManager.getInstance(getActivity()).isForceUpdates(appUpdateBeans, AppUpdateManager.PACKAGE_NAME_HULU);
        LogUtils.d("testNetflix:" + testNetflix + "/testDownloadUrl:" + testDownloadUrl + ", testIsHultForceUpdates:" + testIsHultForceUpdates);

        LogUtils.d("dataJson:" + dataJson);
        LogUtils.d("appUpdateData:" + appUpdateBeans.get(0).getVersion());
        LogUtils.d("appUpdateData:" + appUpdateBeans.get(1).getVersion());
//        LogUtils.d("MyApplication.getInstance().getAppUpdateData():"  + MyApplication.getInstance().getAppUpdateData().getAppUpdateBeans().get(0).getPackageName());
//        LogUtils.d("MyApplication.getInstance().getAppUpdateData():"  + MyApplication.getInstance().getAppUpdateData().getAppUpdateBeans().get(1).getPackageName());
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {//加上判断
            EventBus.getDefault().register(this);
        }
        LogUtils.d(getCurrentFragmentName() + "onStart()");
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        View rootView;
//        if (isWorkout) {
//            rootView = inflater.inflate(R.layout.fragment_internet_workout, container, false);
//        } else {
//            rootView = inflater.inflate(R.layout.fragment_internet, container, false);
//        }
        return inflater.inflate(R.layout.fragment_internet, container, false);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initDelay(View view) {

        Looper.myQueue().addIdleHandler(() -> {

            groupV = view.findViewById(R.id.group);

            viewStub = view.findViewById(R.id.viewstub);

            progressBar = view.findViewById(R.id.progress);

            tvTitle = view.findViewById(R.id.text_title);
            DisplayMetrics dm = getResources().getDisplayMetrics();
            m_nScreenWidth = dm.widthPixels;
            m_nScreenHeight = dm.heightPixels - 50;

            m_clList_InternetDashboard = view.findViewById(R.id.clList_InternetDashboard);
            m_clViewTopBar_InternetDashboard = view.findViewById(R.id.clViewTopBar_InternetDashboard);
            m_clView_InternetDashboard = view.findViewById(R.id.clView_InternetDashboard);

            Button m_btFullScreen_InternetDashboard = view.findViewById(R.id.btFullScreen_InternetDashboard);
            m_btFullScreenExit_InternetDashboard = view.findViewById(R.id.btFullScreenExit_InternetDashboard);
            Button m_btHome_InternetDashboard = view.findViewById(R.id.btHome_InternetDashboard);


            btNetflix_InternetDashboard = view.findViewById(R.id.btNetflix_InternetDashboard);
            btHulu_InternetDashboard = view.findViewById(R.id.btHulu_InternetDashboard);
            btYoutube_InternetDashboard = view.findViewById(R.id.btYoutube_InternetDashboard);
            btABC_InternetDashboard = view.findViewById(R.id.btABC_InternetDashboard);
            btNBC_InternetDashboard = view.findViewById(R.id.btNBC_InternetDashboard);
            btCNN_InternetDashboard = view.findViewById(R.id.btCNN_InternetDashboard);
            btFoxNews_InternetDashboard = view.findViewById(R.id.btFoxNews_InternetDashboard);


//            initBtnBadgeView();
            checkAppUpdate();

            m_btHome_InternetDashboard.setAlpha(0.9f);
            m_btFullScreenExit_InternetDashboard.setAlpha(0.9f);

            m_clList_InternetDashboard.setVisibility(View.VISIBLE);
            m_clView_InternetDashboard.setVisibility(View.INVISIBLE);
            m_clViewTopBar_InternetDashboard.setVisibility(View.VISIBLE);

            m_btFullScreen_InternetDashboard.setOnClickListener(btInternetOnClick);
            m_btFullScreenExit_InternetDashboard.setOnClickListener(btInternetOnClick);
            m_btFullScreenExit_InternetDashboard.setOnTouchListener(btInternetOnTouch);
            m_btHome_InternetDashboard.setOnClickListener(btInternetOnClick);


            if (isWorkout) {
                //   m_btHome_InternetDashboard.setBackgroundResource(R.drawable.btn_icon_inversion_home);
                //  m_btFullScreen_InternetDashboard.setBackgroundResource(R.drawable.btn_icon_inversion_screen_full);

            } else {
                ((DashboardActivity) mActivity).changeTopWidgetStyle(false);
                ((DashboardActivity) mActivity).changeSignOutToBack(false, false, 0, -1);

                LogUtils.d(getCurrentFragmentName() + "initDelay-> isWorkout:" + isWorkout);
            }


            View iv_vsContent = viewStub.inflate();
            m_wvBrowser = iv_vsContent.findViewById(R.id.wbBrowser_InternetDashboard);
            m_wvBrowser.setListener(mActivity, this);

            //  m_wvBrowser.setLayerType(View.LAYER_TYPE_HARDWARE, null);

//            m_wvBrowser.setMixedContentAllowed(false);

            m_wvBrowser.setWebViewClient(new WebViewClient());

            //  m_wvBrowser.setScrollbarFadingEnabled(false);
            //   String newUA = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12) AppleWebKit/602.1.50 (KHTML, like Gecko) Version/10.0 Safari/602.1.50";

            myChrome = new MyChrome();
            m_wvBrowser.setWebChromeClient(myChrome);
            m_wvBrowser.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

            webSettings = m_wvBrowser.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            //  webSettings.setAllowFileAccess(true);
            webSettings.setAppCacheEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setLoadsImagesAutomatically(false);
            webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
            webSettings.setUseWideViewPort(true);
            webSettings.setSaveFormData(true);
            webSettings.setSavePassword(true);

            //  webSettings.setUserAgentString(newUA);

            //    webSettings.setBlockNetworkImage(true);


            btNetflix_InternetDashboard.setOnClickListener(btInternetOnClick);
            btHulu_InternetDashboard.setOnClickListener(btInternetOnClick);
            btYoutube_InternetDashboard.setOnClickListener(btInternetOnClick);
            btABC_InternetDashboard.setOnClickListener(btInternetOnClick);
            btNBC_InternetDashboard.setOnClickListener(btInternetOnClick);
            btCNN_InternetDashboard.setOnClickListener(btInternetOnClick);
            btFoxNews_InternetDashboard.setOnClickListener(btInternetOnClick);

            LogUtils.d(getCurrentFragmentName() + "initDelay:");


            return false;
        });
    }

    //解決 WebView異常BUG
    private void initHookWebView() {
        // 如果是非系統程序則按正常程式走
//        if (Process.myUid() != Process.SYSTEM_UID) {
//            return;
//        }


        if (ApplicationInfo.FLAG_SYSTEM != 1) {
            LogUtils.d(getCurrentFragmentName() + "initHookWebView() -> ApplicationInfo.FLAG_SYSTEM != 1 , ApplicationInfo.FLAG_SYSTEM:" + ApplicationInfo.FLAG_SYSTEM);
            return;
        }


        int sdkInt = Build.VERSION.SDK_INT;
        try {
            Class<?> factoryClass = Class.forName("android.webkit.WebViewFactory");
            Field field = factoryClass.getDeclaredField("sProviderInstance");
            field.setAccessible(true);
            Object sProviderInstance = field.get(null);

            LogUtils.d(getCurrentFragmentName() + "sProviderInstance:" + field.get(null));
            if (sProviderInstance != null) {
                Log.d("HOOK_WEB", "sProviderInstance isn't null");
                return;
            }

            Method getProviderClassMethod;
            if (sdkInt > 22) {
                getProviderClassMethod = factoryClass.getDeclaredMethod("getProviderClass");
            } else if (sdkInt == 22) {
                getProviderClassMethod = factoryClass.getDeclaredMethod("getFactoryClass");
            } else {
                Log.d("HOOK_WEB", "Don't need to Hook WebView");
                return;
            }
            getProviderClassMethod.setAccessible(true);
            Class<?> factoryProviderClass = (Class<?>) getProviderClassMethod.invoke(factoryClass);
            Class<?> delegateClass = Class.forName("android.webkit.WebViewDelegate");
            Constructor<?> delegateConstructor = delegateClass.getDeclaredConstructor();
            delegateConstructor.setAccessible(true);
            if (sdkInt < 26) {//低於Android O版本
                Constructor<?> providerConstructor = factoryProviderClass.getConstructor(delegateClass);
                if (providerConstructor != null) {
                    providerConstructor.setAccessible(true);
                    sProviderInstance = providerConstructor.newInstance(delegateConstructor.newInstance());

                    LogUtils.d(getCurrentFragmentName() + "providerConstructor != null" + " ,sProviderInstance:" + sProviderInstance.toString());
                }
            } else {
                Field chromiumMethodName = factoryClass.getDeclaredField("CHROMIUM_WEBVIEW_FACTORY_METHOD");
                chromiumMethodName.setAccessible(true);
                String chromiumMethodNameStr = (String) chromiumMethodName.get(null);
                if (chromiumMethodNameStr == null) {
                    chromiumMethodNameStr = "create";
                }
                Method staticFactory = factoryProviderClass.getMethod(chromiumMethodNameStr, delegateClass);
                if (staticFactory != null) {
                    sProviderInstance = staticFactory.invoke(null, delegateConstructor.newInstance());
                }
            }

            if (sProviderInstance != null) {
                field.set("sProviderInstance", sProviderInstance);
                Log.e("HOOK_WEB", "Hook success!");
                LogUtils.d(getCurrentFragmentName() + "HOOK_WEB ,Hook success!");
            } else {
                Log.e("HOOK_WEB", "Hook failed!");
                LogUtils.d(getCurrentFragmentName() + "HOOK_WEB ,Hook Fail!");
            }
        } catch (Exception e) {
            Log.e("HOOK_WEB", e.getLocalizedMessage());
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtils.d("internet ->onViewCreated ->");
        //判斷是否有網路,沒有的話警示彈窗
        if (!isConnected(mActivity)) {
            showToastAlert(getString(R.string.no_internet_toast_text), mActivity);
        }

        initHookWebView();
        initDelay(view);

//        Looper.myQueue().addIdleHandler(() -> {
//            initBtnBadgeView();
//            return false;
//        });

        checkAppUpdate();
//        getLog();
    }

    private void initBtnBadgeView() {
        PackageInfo netflixInfo = AppUpdateManager.getInstance(getActivity()).getNetflixInfo();
        PackageInfo huluInfo = AppUpdateManager.getInstance(getActivity()).getHuluInfo();
        PackageInfo abcInfo = AppUpdateManager.getInstance(getActivity()).getAbcNewsInfo();
        PackageInfo nbcInfo = AppUpdateManager.getInstance(getActivity()).getNbcInfo();
        PackageInfo cnnInfo = AppUpdateManager.getInstance(getActivity()).getCnnInfo();
        PackageInfo foxInfo = AppUpdateManager.getInstance(getActivity()).getFoxNewsInfo();

        LogUtils.d("initBtnBadgeView ->" + packageManagerUtils.getPackageInfo(getActivity(), AppUpdateManager.PACKAGE_NAME_NETFLIX).versionName);
        LogUtils.d(
                "netflixInfo :" + netflixInfo.versionName + "\n" +
                        "huluInfo :" + huluInfo.versionName + "\n" +
                        "abcInfo :" + abcInfo.versionName + "\n" +
                        "nbcInfo :" + nbcInfo.versionName + "\n" +
                        "cnnInfo :" + cnnInfo.versionName + "\n" +
                        "foxInfo :" + foxInfo.versionName + "\n"
        );


        if (appUpdateBeans != null) {

            for (int i = 0; i < appUpdateBeans.size(); i++) {
                String packageName = appUpdateBeans.get(i).getPackageName();
                String forceUpdates = appUpdateBeans.get(i).getForceUpdate();
                String version = appUpdateBeans.get(i).getVersion();

                if (packageName.equals(AppUpdateManager.PACKAGE_NAME_NETFLIX)) {
                    LogUtils.d("PACKAGE_NAME_NETFLIX ->");

                    if (packageManagerUtils.isUpgrade(AppUpdateManager.getInstance(getActivity()).getNetflixInfo().versionName, version) && forceUpdates.equals("YES")) {
                        CommonUtils.setBadger(getActivity(), btNetflix_InternetDashboard, 1);
                    } else {

                    }

                } else if (packageName.equals(AppUpdateManager.PACKAGE_NAME_HULU)) {
                    LogUtils.d("PACKAGE_NAME_HULU ->");
                    if (packageManagerUtils.isUpgrade(AppUpdateManager.getInstance(getActivity()).getHuluInfo().versionName, version) && forceUpdates.equals("YES")) {
                        CommonUtils.setBadger(getActivity(), btHulu_InternetDashboard, 1);
                    } else {

                    }

                } else if (packageName.equals(AppUpdateManager.PACKAGE_NAME_ABC_NEWS)) {
                    LogUtils.d("PACKAGE_NAME_ABC_NEWS ->");
                    if (packageManagerUtils.isUpgrade(AppUpdateManager.getInstance(getActivity()).getAbcNewsInfo().versionName, version) && forceUpdates.equals("YES"))
                        CommonUtils.setBadger(getActivity(), btABC_InternetDashboard, 1);


                } else if (packageName.equals(AppUpdateManager.PACKAGE_NAME_MSNBC)) {
                    LogUtils.d("PACKAGE_NAME_MSNBC ->");
                    if (packageManagerUtils.isUpgrade(AppUpdateManager.getInstance(getActivity()).getNbcInfo().versionName, version) && forceUpdates.equals("YES")) {
                        CommonUtils.setBadger(getActivity(), btNBC_InternetDashboard, 1);
                    } else {

                    }


                } else if (packageName.equals(AppUpdateManager.PACKAGE_NAME_CNN)) {
                    LogUtils.d("PACKAGE_NAME_CNN ->");
                    if (packageManagerUtils.isUpgrade(AppUpdateManager.getInstance(getActivity()).getCnnInfo().versionName, version) && forceUpdates.equals("YES")) {
                        CommonUtils.setBadger(getActivity(), btCNN_InternetDashboard, 1);
                    } else {

                    }


                } else if (packageName.equals(AppUpdateManager.PACKAGE_NAME_FOX_NEWS)) {
                    LogUtils.d("PACKAGE_NAME_FOX_NEWS ->");
                    if (packageManagerUtils.isUpgrade(AppUpdateManager.getInstance(getActivity()).getFoxNewsInfo().versionName, version) && forceUpdates.equals("YES")) {
                        CommonUtils.setBadger(getActivity(), btFoxNews_InternetDashboard, 1);
                    } else {
                    }

                }
            }
        }
    }

    private void getLog() {
//        PackageManagerUtils.instance(getActivity()).getPackageSystemDataLog();
//        getPackageSystemDataLog();
    }

    public void getPackageSystemDataLog() {
        final PackageManager pm = getActivity().getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0)
                LogUtils.d(getCurrentFragmentName() + "Installed package (System) :" + packageInfo.packageName);
            else
                LogUtils.d(getCurrentFragmentName() + "Installed package (User) :" + packageInfo.packageName);
        }
    }

    private void openBrowser(boolean open) {

        m_clList_InternetDashboard.setVisibility(open ? View.INVISIBLE : View.VISIBLE);
        m_clView_InternetDashboard.setVisibility(open ? View.VISIBLE : View.INVISIBLE);
        m_wvBrowser.setVisibility(open ? View.VISIBLE : View.INVISIBLE);
    }

    //開啟跟關閉
    private void fullScreen(boolean full) {
        if (isWorkout) {
            ((WorkoutDashboardActivity) mActivity).makeFullScreen(full);
            groupV.setVisibility(full ? View.INVISIBLE : View.VISIBLE);
        } else {
            ((DashboardActivity) mActivity).makeFullScreen(full);
        }

        m_clViewTopBar_InternetDashboard.setVisibility(full ? View.INVISIBLE : View.VISIBLE);
        m_btFullScreenExit_InternetDashboard.setVisibility(full ? View.VISIBLE : View.INVISIBLE);
    }

    private final Button.OnTouchListener btInternetOnTouch = new Button.OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    m_nLastX = (int) motionEvent.getRawX();
                    m_nLastY = (int) motionEvent.getRawY();
                    m_bOnClick = false;
                    m_lStartTime = System.currentTimeMillis();
                    break;

                case MotionEvent.ACTION_MOVE:
                    m_bOnClick = true;
                    int rx = (int) (motionEvent.getRawX() - m_nLastX);
                    int ry = (int) (motionEvent.getRawY() - m_nLastY);
                    int left = view.getLeft() + rx;
                    int top = view.getTop() + ry;
                    int right = view.getRight() + rx;
                    int bottom = view.getBottom() + ry;
                    if (left < 0) {
                        left = 0;
                        right = left + view.getWidth();
                    }
                    if (right > m_nScreenWidth) {
                        right = m_nScreenWidth;
                        left = right - view.getWidth();
                    }
                    if (top < 0) {
                        top = 0;
                        bottom = top + view.getHeight();
                    }
                    if (bottom > m_nScreenHeight) {
                        bottom = m_nScreenHeight;
                        top = bottom - view.getHeight();
                    }
                    view.layout(left, top, right, bottom);
                    m_nLastX = (int) motionEvent.getRawX();
                    m_nLastY = (int) motionEvent.getRawY();
                    break;
                case MotionEvent.ACTION_UP:
                    long m_lEndTime = System.currentTimeMillis();
                    m_bOnClick = (m_lEndTime - m_lStartTime) > 0.1 * 5000L;
                    break;
            }

            return m_bOnClick;
        }
    };

    private final Button.OnClickListener btInternetOnClick = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {

            CommonUtils.closePackage(mActivity);
            if (view.getId() == R.id.btNetflix_InternetDashboard) {
                //  if (CommonUtils.isFastClick()) return;

                if (appUpdateBeans != null) {
                    String appUpdateNetflixVersion = AppUpdateManager.getInstance(getActivity()).getAppUpdateVersion(appUpdateBeans, AppUpdateManager.PACKAGE_NAME_NETFLIX);
                    String appUpdateNetflixDownloadUrl = AppUpdateManager.getInstance(getActivity()).getAppUpdateDownloadUrl(appUpdateBeans, AppUpdateManager.PACKAGE_NAME_NETFLIX);
                    String packageNetflixVersion = AppUpdateManager.getInstance(getActivity()).getNetflixInfo().versionName;
                    String packageNetflixName = AppUpdateManager.getInstance(getActivity()).getNetflixInfo().packageName;
                    boolean isForceUpdates = AppUpdateManager.getInstance(getActivity()).isForceUpdates(appUpdateBeans, AppUpdateManager.PACKAGE_NAME_NETFLIX);

                    if (packageManagerUtils.isUpgrade(packageNetflixVersion, appUpdateNetflixVersion) && isForceUpdates) {
                        //新版本可以更新,且有設定更新選項
                        intentToAppUpdate(packageNetflixName, appUpdateNetflixDownloadUrl, isForceUpdates);

                    } else {
                        startFloatingDashboard();
                        new RxTimer().timer(100, number -> {
                            try {

                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setClassName("com.netflix.mediaclient", "com.netflix.mediaclient.ui.launch.UIWebViewActivity");
                                //  intent.setData(Uri.parse("https://www.netflix.com/tw/"));
                                startActivity(intent);
                                mActivity.overridePendingTransition(0, 0);
                            } catch (Exception e) {
                                removeFloatView();
                                Toasty.warning(getInstance(), "NO Netflix APP", Toasty.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            } else if (view.getId() == R.id.btHulu_InternetDashboard) {
                if (appUpdateBeans != null) {
                    String appUpdateHuluVersion = AppUpdateManager.getInstance(getActivity()).getAppUpdateVersion(appUpdateBeans, AppUpdateManager.PACKAGE_NAME_HULU);
                    String appUpdateHuluDownloadUrl = AppUpdateManager.getInstance(getActivity()).getAppUpdateDownloadUrl(appUpdateBeans, AppUpdateManager.PACKAGE_NAME_HULU);
                    String packageHuluVersion = AppUpdateManager.getInstance(getActivity()).getHuluInfo().versionName;
                    String packageHuluName = AppUpdateManager.getInstance(getActivity()).getHuluInfo().packageName;
                    boolean isForceUpdates = AppUpdateManager.getInstance(getActivity()).isForceUpdates(appUpdateBeans, AppUpdateManager.PACKAGE_NAME_HULU);

                    if (packageManagerUtils.isUpgrade(packageHuluVersion, appUpdateHuluVersion) && isForceUpdates) {
                        //新版本可以更新,且有設定更新選項
                        intentToAppUpdate(packageHuluName, appUpdateHuluDownloadUrl, isForceUpdates);

                    } else {
                        //不用更新
                        startFloatingDashboard();
                        //   new RxTimer().timer(500, number -> {
                        try {

                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            //  intent.setPackage("com.hulu.plus");
                            intent.setClassName("com.hulu.plus", "com.hulu.features.splash.SplashActivity");
                            startActivity(intent);
                            mActivity.overridePendingTransition(0, 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                            removeFloatView();
                            Toasty.warning(getInstance(), "NO hulu APP", Toasty.LENGTH_LONG).show();
                        }
                    }


                }


                //    });
            } else if (view.getId() == R.id.btYoutube_InternetDashboard) {
                openBrowser(true);
                if (m_wvBrowser.getUrl() != null && m_wvBrowser.getUrl().contains("youtube.com/")) {
                    return;
                }
                m_wvBrowser.loadUrl("https://www.youtube.com/");
            } else if (view.getId() == R.id.btABC_InternetDashboard) {
                if (appUpdateBeans != null) {
                    String appUpdateAbcVersion = AppUpdateManager.getInstance(getActivity()).getAppUpdateVersion(appUpdateBeans, AppUpdateManager.PACKAGE_NAME_ABC_NEWS);
                    String appUpdateAbcDownloadUrl = AppUpdateManager.getInstance(getActivity()).getAppUpdateDownloadUrl(appUpdateBeans, AppUpdateManager.PACKAGE_NAME_ABC_NEWS);
                    String packageAbcVersion = AppUpdateManager.getInstance(getActivity()).getAbcNewsInfo().versionName;
                    String packageAbcName = AppUpdateManager.getInstance(getActivity()).getAbcNewsInfo().packageName;
                    boolean isForceUpdates = AppUpdateManager.getInstance(getActivity()).isForceUpdates(appUpdateBeans, AppUpdateManager.PACKAGE_NAME_ABC_NEWS);

                    if (packageManagerUtils.isUpgrade(packageAbcVersion, appUpdateAbcVersion) && isForceUpdates) {
                        //新版本可以更新,且有設定更新選項
                        MyApplication.SSEB = false;
                        Intent intent = new Intent(getActivity(), AppUpadteActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("fileUrl", appUpdateAbcDownloadUrl);
                        bundle.putString("md5", "a6343896628ec486cdc5ce673c981e7e");
                        bundle.putBoolean("isForce", isForceUpdates);
                        bundle.putString("packageName", packageAbcName);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        startFloatingDashboard();
                        //    new RxTimer().timer(500, number -> {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setClassName("com.abc.abcnews", "com.abc.abcnews.ui.navigation.MainNavigationActivity");
                            startActivity(intent);
                            mActivity.overridePendingTransition(0, 0);
                        } catch (Exception e) {
                            removeFloatView();
                            Toasty.warning(getInstance(), "NO abc NEWS APP", Toasty.LENGTH_LONG).show();
                        }
                        //    });
                    }
                } else {

                }
            } else if (view.getId() == R.id.btNBC_InternetDashboard) {
                if (appUpdateBeans != null) {
                    String appUpdateNbcVersion = AppUpdateManager.getInstance(getActivity()).getAppUpdateVersion(appUpdateBeans, AppUpdateManager.PACKAGE_NAME_MSNBC);
                    String appUpdateNbcDownloadUrl = AppUpdateManager.getInstance(getActivity()).getAppUpdateDownloadUrl(appUpdateBeans, AppUpdateManager.PACKAGE_NAME_MSNBC);
                    String packageNbcVersion = AppUpdateManager.getInstance(getActivity()).getNbcInfo().versionName;
                    String packageNbcName = AppUpdateManager.getInstance(getActivity()).getNbcInfo().packageName;
                    boolean isForceUpdates = AppUpdateManager.getInstance(getActivity()).isForceUpdates(appUpdateBeans, AppUpdateManager.PACKAGE_NAME_MSNBC);

                    if (packageManagerUtils.isUpgrade(packageNbcVersion, appUpdateNbcVersion) && isForceUpdates) {
                        //新版本可以更新,且有設定更新選項
                        MyApplication.SSEB = false;
                        Intent intent = new Intent(getActivity(), AppUpadteActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("fileUrl", appUpdateNbcDownloadUrl);
                        bundle.putString("md5", "a6343896628ec486cdc5ce673c981e7e");
                        bundle.putBoolean("isForce", isForceUpdates);
                        bundle.putString("packageName", packageNbcName);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        startFloatingDashboard();
                        //   new RxTimer().timer(500, number -> {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setClassName("com.zumobi.msnbc", "com.nbc.activities.MainActivity");
                            startActivity(intent);
                            mActivity.overridePendingTransition(0, 0);
                        } catch (Exception e) {
                            removeFloatView();
                            Toasty.warning(getInstance(), "NO NBC NEWS APP", Toasty.LENGTH_LONG).show();
                        }
                        //   });
                    }
                }
            } else if (view.getId() == R.id.btCNN_InternetDashboard) {
                if (appUpdateBeans != null) {
                    String appUpdateCnnVersion = AppUpdateManager.getInstance(getActivity()).getAppUpdateVersion(appUpdateBeans, AppUpdateManager.PACKAGE_NAME_CNN);
                    String appUpdateCnnDownloadUrl = AppUpdateManager.getInstance(getActivity()).getAppUpdateDownloadUrl(appUpdateBeans, AppUpdateManager.PACKAGE_NAME_CNN);
                    String packageCnnVersion = AppUpdateManager.getInstance(getActivity()).getCnnInfo().versionName;
                    String packageCnnName = AppUpdateManager.getInstance(getActivity()).getCnnInfo().packageName;
                    boolean isForceUpdates = AppUpdateManager.getInstance(getActivity()).isForceUpdates(appUpdateBeans, AppUpdateManager.PACKAGE_NAME_CNN);

                    if (packageManagerUtils.isUpgrade(packageCnnVersion, appUpdateCnnVersion) && isForceUpdates) {
                        //新版本可以更新,且有設定更新選項
                        MyApplication.SSEB = false;
                        Intent intent = new Intent(getActivity(), AppUpadteActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("fileUrl", appUpdateCnnDownloadUrl);
                        bundle.putString("md5", "a6343896628ec486cdc5ce673c981e7e");
                        bundle.putBoolean("isForce", isForceUpdates);
                        bundle.putString("packageName", packageCnnName);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        startFloatingDashboard();
                        //   new RxTimer().timer(500, number -> {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setClassName("com.cnn.mobile.android.phone", "com.cnn.mobile.android.phone.features.splash.SplashActivity");
                            startActivity(intent);
                            mActivity.overridePendingTransition(0, 0);
                        } catch (Exception e) {
                            removeFloatView();
                            Toasty.warning(getInstance(), "NO CNN APP", Toasty.LENGTH_LONG).show();
                        }
                    }
                }
            } else if (view.getId() == R.id.btFoxNews_InternetDashboard) {
                if (appUpdateBeans != null) {
                    String appUpdateFoxVersion = AppUpdateManager.getInstance(getActivity()).getAppUpdateVersion(appUpdateBeans, AppUpdateManager.PACKAGE_NAME_FOX_NEWS);
                    String appUpdateFoxDownloadUrl = AppUpdateManager.getInstance(getActivity()).getAppUpdateDownloadUrl(appUpdateBeans, AppUpdateManager.PACKAGE_NAME_FOX_NEWS);
                    String packageFoxVersion = AppUpdateManager.getInstance(getActivity()).getFoxNewsInfo().versionName;
                    String packageFoxName = AppUpdateManager.getInstance(getActivity()).getFoxNewsInfo().packageName;
                    boolean isForceUpdates = AppUpdateManager.getInstance(getActivity()).isForceUpdates(appUpdateBeans, AppUpdateManager.PACKAGE_NAME_FOX_NEWS);

                    if (packageManagerUtils.isUpgrade(packageFoxVersion, appUpdateFoxVersion) && isForceUpdates) {
                        //新版本可以更新,且有設定更新選項
                        MyApplication.SSEB = false;
                        Intent intent = new Intent(getActivity(), AppUpadteActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("fileUrl", appUpdateFoxDownloadUrl);
                        bundle.putString("md5", "a6343896628ec486cdc5ce673c981e7e");
                        bundle.putBoolean("isForce", isForceUpdates);
                        bundle.putString("packageName", packageFoxName);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        startFloatingDashboard();
                        //      new RxTimer().timer(500, number -> {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            //    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setClassName("com.foxnews.android", "com.foxnews.android.corenav.StartActivity");
                            startActivity(intent);
                            mActivity.overridePendingTransition(0, 0);
                        } catch (Exception e) {
                            removeFloatView();
                            Toasty.warning(getInstance(), "NO FOX NEWS APP", Toasty.LENGTH_LONG).show();
                        }
                        //     });
                    }
                }
            } else if (view.getId() == R.id.btHome_InternetDashboard) {
                //HOME按鈕
                openBrowser(false);
            } else if (view.getId() == R.id.btFullScreen_InternetDashboard) {
                //全螢幕 按鈕
                fullScreen(true);
            } else if (view.getId() == R.id.btFullScreenExit_InternetDashboard) {
                //離開全螢幕
                fullScreen(false);
            }
        }
    };

    private void intentToAppUpdate(String packageName, String downloadUrl, boolean isForceUpdates) {
        LogUtils.d("intentToAppUpdate -> + packageName : + " + packageName + "/downloadUrl:" + downloadUrl);
        MyApplication.SSEB = false;
        Intent intent = new Intent(getActivity(), AppUpadteActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("fileUrl", downloadUrl);
        bundle.putString("md5", "a6343896628ec486cdc5ce673c981e7e");
        bundle.putBoolean("isForce", isForceUpdates);
        bundle.putString("packageName", packageName);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPageFinished(String url) {

        if (!webSettings.getLoadsImagesAutomatically()) {
            webSettings.setLoadsImagesAutomatically(true);
        }

        progressBar.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        Log.d(TAG, "onPageError: " + failingUrl + "," + description);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {
    }

    @Override
    public void onExternalPageRequest(String url) {
    }

    private class MyChrome extends WebChromeClient {

        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        MyChrome() {
        }

        public Bitmap getDefaultVideoPoster() {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(mActivity.getApplicationContext().getResources(), 2130837573);
        }

        @Override
        public void onHideCustomView() {

            ((FrameLayout) mActivity.getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            mActivity.getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            mActivity.setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        @Override
        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback) {

            Log.d("WEBBBBBB", "onShowCustomView: ");
            if (this.mCustomView != null) {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = mActivity.getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = mActivity.getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout) mActivity.getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            mActivity.getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

            showBtnFullScreenExit();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private static final String TAG = "InternetFragment";

    @Override
    public void onDestroy() {

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        try {
            if (windowManager != null && btnFullScreenExit != null) {
                windowManager.removeView(btnFullScreenExit);
            }
            floatButtonIsStarted = false;

            btnFullScreenExit = null;
            windowManager = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (m_wvBrowser != null) m_wvBrowser.onDestroy();
        removeFloatView();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
//        getActivity().getApplicationContext().unregisterReceiver(appUpdateReceiver);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (m_wvBrowser != null) m_wvBrowser.onResume();

        try {
            if (!isWorkout)
                ((DashboardActivity) requireActivity()).canSleep = false;
        } catch (Exception e) {
            e.printStackTrace();
        }


//        Looper.myQueue().addIdleHandler(() -> {
//            initBtnBadgeView();
//            return false;
//        });


        LogUtils.d(getCurrentFragmentName() + "onResume()");
    }

    public void removeFloatView() {

        ((DashboardActivity) mActivity).removeFloatView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        removeFloatView();

        viewStub = null;
        progressBar = null;
        tvTitle = null;
        m_wvBrowser = null;
        m_btFullScreenExit_InternetDashboard = null;
        m_clViewTopBar_InternetDashboard = null;
        m_clView_InternetDashboard = null;
        m_clList_InternetDashboard = null;
        groupV = null;
        webSettings = null;
        myChrome = null;
    }

    private Button btnFullScreenExit;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private boolean floatButtonIsStarted;

    @SuppressLint("ClickableViewAccessibility")
    private void showBtnFullScreenExit() {


        if (floatButtonIsStarted) return;

        floatButtonIsStarted = true;
        windowManager = (WindowManager) mActivity.getSystemService(WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        layoutParams.gravity = Gravity.START | Gravity.TOP;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = isWorkout ? 114 : 172;
        layoutParams.height = isWorkout ? 114 : 172;
        layoutParams.x = 1000;
        layoutParams.y = 0;

        if (Settings.canDrawOverlays(mActivity)) {

            btnFullScreenExit = new Button(mActivity.getApplicationContext());
            btnFullScreenExit.setAlpha(0.9f);
            btnFullScreenExit.setBackgroundResource(isWorkout ? R.drawable.btn_icon_inversion_screenfull_exit : R.drawable.btn_icon_screenfull_exit);
            windowManager.addView(btnFullScreenExit, layoutParams);

            btnFullScreenExit.setOnTouchListener(new FloatingOnTouchListener());

            btnFullScreenExit.setOnClickListener(v -> {
                try {
                    fullScreen(false);
                    windowManager.removeView(btnFullScreenExit);
                    floatButtonIsStarted = false;
                    myChrome.onHideCustomView();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    windowManager = null;
                    btnFullScreenExit = null;
                }
            });
        }
    }

    public void startFloatingDashboard() {

        MyApplication.SSEB = false;
        if (!FloatingDashboardService.isStarted) { //FloatingDashboardService 尚未開啟，重建
            Intent serviceIntent = new Intent(getInstance(), FloatingDashboardService.class);
            serviceIntent.putExtra("TYPE", 4);
            mActivity.startService(serviceIntent);
        }

    }

    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    m_bOnClick = false;
                    m_lStartTime = System.currentTimeMillis();
                    break;
                case MotionEvent.ACTION_MOVE:
                    m_bOnClick = true;
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    layoutParams.x = layoutParams.x + movedX;
                    layoutParams.y = layoutParams.y + movedY;

                    try {
                        if (windowManager != null)
                            windowManager.updateViewLayout(view, layoutParams);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //  windowManager.updateViewLayout(view, layoutParams);
                    break;
                case MotionEvent.ACTION_UP:
                    long m_lEndTime = System.currentTimeMillis();
                    m_bOnClick = (m_lEndTime - m_lStartTime) > 0.1 * 5000L;
                    break;
                default:
                    break;
            }
            return m_bOnClick;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAppUpdateComplete(AppUpdateEvent appUpdateEvent) {
        LogUtils.d("onAppUpdateComplete->" + appUpdateEvent.getEventType());

        int installPackageStatus = appUpdateEvent.getEventType();

//        Looper.myQueue().addIdleHandler(() -> {
//            initBtnBadgeView();
//            return false;
//        });

        checkAppUpdate();

        switch (installPackageStatus) {
            case AppUpdateManager.REPLACED_STATUS_NETFLIX:
                startFloatingDashboard();
                new RxTimer().timer(100, number -> {
                    try {
                        getPackageSystemDataLog();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setClassName("com.netflix.mediaclient", "com.netflix.mediaclient.ui.launch.UIWebViewActivity");
                        //  intent.setData(Uri.parse("https://www.netflix.com/tw/"));
                        startActivity(intent);
                        mActivity.overridePendingTransition(0, 0);
                    } catch (Exception e) {
                        removeFloatView();
                        Toasty.warning(getInstance(), "NO Netflix APP", Toasty.LENGTH_LONG).show();
                    }
                });
                break;

            case AppUpdateManager.REPLACED_STATUS_HULU:
                startFloatingDashboard();
                new RxTimer().timer(500, number -> {
                    try {

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        //  intent.setPackage("com.hulu.plus");
                        intent.setClassName("com.hulu.plus", "com.hulu.features.splash.SplashActivity");
                        startActivity(intent);
                        mActivity.overridePendingTransition(0, 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                        removeFloatView();
                        Toasty.warning(getInstance(), "NO hulu APP", Toasty.LENGTH_LONG).show();
                    }
                });
                break;

            case AppUpdateManager.REPLACED_STATUS_ABC_NEWS:
                startFloatingDashboard();
                //    new RxTimer().timer(500, number -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setClassName("com.abc.abcnews", "com.abc.abcnews.ui.navigation.MainNavigationActivity");
                    startActivity(intent);
                    mActivity.overridePendingTransition(0, 0);
                } catch (Exception e) {
                    removeFloatView();
                    Toasty.warning(getInstance(), "NO abc NEWS APP", Toasty.LENGTH_LONG).show();
                }
                break;

            case AppUpdateManager.REPLACED_STATUS_MSNBC:
                startFloatingDashboard();
                //   new RxTimer().timer(500, number -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setClassName("com.zumobi.msnbc", "com.nbc.activities.MainActivity");
                    startActivity(intent);
                    mActivity.overridePendingTransition(0, 0);
                } catch (Exception e) {
                    removeFloatView();
                    Toasty.warning(getInstance(), "NO NBC NEWS APP", Toasty.LENGTH_LONG).show();
                }
                //   });
                break;

            case AppUpdateManager.REPLACED_STATUS_CNN:
                startFloatingDashboard();
                //   new RxTimer().timer(500, number -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setClassName("com.cnn.mobile.android.phone", "com.cnn.mobile.android.phone.features.splash.SplashActivity");
                    startActivity(intent);
                    mActivity.overridePendingTransition(0, 0);
                } catch (Exception e) {
                    removeFloatView();
                    Toasty.warning(getInstance(), "NO CNN APP", Toasty.LENGTH_LONG).show();
                }
                break;

            case AppUpdateManager.REPLACED_STATUS_FOX_NEWS:
                startFloatingDashboard();
                //      new RxTimer().timer(500, number -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    //    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClassName("com.foxnews.android", "com.foxnews.android.corenav.StartActivity");
                    startActivity(intent);
                    mActivity.overridePendingTransition(0, 0);
                } catch (Exception e) {
                    removeFloatView();
                    Toasty.warning(getInstance(), "NO FOX NEWS APP", Toasty.LENGTH_LONG).show();
                }
                //     });
                break;

        }

//
    }

    //安裝廣播監聽
    public static class AppUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.d("AppUpdateReceiver -> onReceive -> action:" + intent.getAction());

            try {
                //有重新安裝的action
                if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {

                    String packageName = intent.getDataString();
                    LogUtils.d("AppUpdateReceiver -> ACTION_PACKAGE_REPLACED packageName:" + packageName);

                    if (packageName.contains(AppUpdateManager.PACKAGE_NAME_NETFLIX)) {
                        EventBus.getDefault().post(new AppUpdateEvent(AppUpdateManager.REPLACED_STATUS_NETFLIX));
                        LogUtils.d("AppUpdateReceiver -> PACKAGE_NAME_NETFLIX:" + packageName);

                    } else if (packageName.contains(AppUpdateManager.PACKAGE_NAME_HULU)) {
                        EventBus.getDefault().post(new AppUpdateEvent(AppUpdateManager.REPLACED_STATUS_HULU));
                        LogUtils.d("AppUpdateReceiver -> PACKAGE_NAME_HULU:" + packageName);

                    } else if (packageName.contains(AppUpdateManager.PACKAGE_NAME_ABC_NEWS)) {
                        EventBus.getDefault().post(new AppUpdateEvent(AppUpdateManager.REPLACED_STATUS_ABC_NEWS));
                        LogUtils.d("AppUpdateReceiver -> PACKAGE_NAME_ABC_NEWS:" + packageName);

                    } else if (packageName.contains(AppUpdateManager.PACKAGE_NAME_MSNBC)) {
                        EventBus.getDefault().post(new AppUpdateEvent(AppUpdateManager.REPLACED_STATUS_MSNBC));
                        LogUtils.d("AppUpdateReceiver -> PACKAGE_NAME_MSNBC:" + packageName);

                    } else if (packageName.contains(AppUpdateManager.PACKAGE_NAME_CNN)) {
                        EventBus.getDefault().post(new AppUpdateEvent(AppUpdateManager.REPLACED_STATUS_CNN));
                        LogUtils.d("AppUpdateReceiver -> PACKAGE_NAME_CNN:" + packageName);

                    } else if (packageName.contains(AppUpdateManager.PACKAGE_NAME_FOX_NEWS)) {
                        EventBus.getDefault().post(new AppUpdateEvent(AppUpdateManager.REPLACED_STATUS_FOX_NEWS));
                        LogUtils.d("AppUpdateReceiver -> PACKAGE_NAME_FOX_NEWS:" + packageName);

                    }

                }


            } catch (Exception e) {
                LogUtils.d("AppUpdateReceiver -> e:" + e.toString());
            }
        }
    }


    private void checkAppUpdate() {
        LogUtils.d("checkAppUpdate: ");
        BaseApi.request(BaseApi.createApi2(IServiceApi.class).apiCheckAppUpdate(),
                new BaseApi.IResponseListener<AppUpdateData>() {
                    @Override
                    public void onSuccess(AppUpdateData data) {
                        LogS.printJson("checkAppUpdate ->", new Gson().toJson(data), "");
                        try {
                            appUpdateBeans = data.getAppUpdateBeans();
                            if (appUpdateBeans != null) {
                                for (int i = 0; i < appUpdateBeans.size(); i++) {
                                    String packageName = appUpdateBeans.get(i).getPackageName();
                                    String forceUpdates = appUpdateBeans.get(i).getForceUpdate();
                                    String version = appUpdateBeans.get(i).getVersion();

                                    if (packageName.equals(AppUpdateManager.PACKAGE_NAME_NETFLIX)) {
                                        LogUtils.d("PACKAGE_NAME_NETFLIX ->");

                                        if (packageManagerUtils.isUpgrade(AppUpdateManager.getInstance(getActivity()).getNetflixInfo().versionName, version) && forceUpdates.equals("YES")) {
                                            CommonUtils.setBadger(getActivity(), btNetflix_InternetDashboard, 1);
                                        } else {

                                        }

                                    } else if (packageName.equals(AppUpdateManager.PACKAGE_NAME_HULU)) {
                                        LogUtils.d("PACKAGE_NAME_HULU ->");
                                        if (packageManagerUtils.isUpgrade(AppUpdateManager.getInstance(getActivity()).getHuluInfo().versionName, version) && forceUpdates.equals("YES")) {
                                            CommonUtils.setBadger(getActivity(), btHulu_InternetDashboard, 1);
                                        } else {

                                        }

                                    } else if (packageName.equals(AppUpdateManager.PACKAGE_NAME_ABC_NEWS)) {
                                        LogUtils.d("PACKAGE_NAME_ABC_NEWS ->");
                                        if (packageManagerUtils.isUpgrade(AppUpdateManager.getInstance(getActivity()).getAbcNewsInfo().versionName, version) && forceUpdates.equals("YES"))
                                            CommonUtils.setBadger(getActivity(), btABC_InternetDashboard, 1);


                                    } else if (packageName.equals(AppUpdateManager.PACKAGE_NAME_MSNBC)) {
                                        LogUtils.d("PACKAGE_NAME_MSNBC ->");
                                        if (packageManagerUtils.isUpgrade(AppUpdateManager.getInstance(getActivity()).getNbcInfo().versionName, version) && forceUpdates.equals("YES")) {
                                            CommonUtils.setBadger(getActivity(), btNBC_InternetDashboard, 1);
                                        } else {

                                        }


                                    } else if (packageName.equals(AppUpdateManager.PACKAGE_NAME_CNN)) {
                                        LogUtils.d("PACKAGE_NAME_CNN ->");
                                        if (packageManagerUtils.isUpgrade(AppUpdateManager.getInstance(getActivity()).getCnnInfo().versionName, version) && forceUpdates.equals("YES")) {
                                            CommonUtils.setBadger(getActivity(), btCNN_InternetDashboard, 1);
                                        } else {

                                        }


                                    } else if (packageName.equals(AppUpdateManager.PACKAGE_NAME_FOX_NEWS)) {
                                        LogUtils.d("PACKAGE_NAME_FOX_NEWS ->");
                                        if (packageManagerUtils.isUpgrade(AppUpdateManager.getInstance(getActivity()).getFoxNewsInfo().versionName, version) && forceUpdates.equals("YES")) {
                                            CommonUtils.setBadger(getActivity(), btFoxNews_InternetDashboard, 1);
                                        } else {
                                        }

                                    }
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                                LogUtils.d("checkAppUpdate" +"checkUpdate Exception:" + e.toString());


                        }
                    }

                    @Override
                    public void onFail() {
                        Log.d("WEB_API-checkUpdate", "失敗");
                        LogUtils.d("checkAppUpdate" +"checkUpdate error:" );
                    }
                });
    }

}