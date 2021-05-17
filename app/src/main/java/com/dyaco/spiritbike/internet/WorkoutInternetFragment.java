package com.dyaco.spiritbike.internet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;

import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.mirroring.FloatingDashboardService;
import com.dyaco.spiritbike.mirroring.FloatingWorkoutDashboardService;
import com.dyaco.spiritbike.support.BaseFragment;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.MsgEvent;
import com.dyaco.spiritbike.support.RxBus;
import com.dyaco.spiritbike.support.RxTimer;
import com.dyaco.spiritbike.workout.WorkoutDashboardActivity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import es.dmoral.toasty.Toasty;
import im.delight.android.webview.AdvancedWebView;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

import static android.content.Context.WINDOW_SERVICE;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_EXIT_FULL_SCREEN;
import static com.dyaco.spiritbike.MyApplication.getInstance;
import static com.dyaco.spiritbike.support.CommonUtils.isConnected;
import static com.dyaco.spiritbike.support.CommonUtils.showToastAlert;


public class WorkoutInternetFragment extends BaseFragment implements AdvancedWebView.Listener {
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
    private Button btNetflix_InternetDashboard;
    private Button btHulu_InternetDashboard;
    private Button btYoutube_InternetDashboard;
    private Button btABC_InternetDashboard;
    private Button btNBC_InternetDashboard;
    private Button btCNN_InternetDashboard;
    private Button btFoxNews_InternetDashboard;
    private WebSettings webSettings;
    private MyChrome myChrome;
    private String mNetflixUrl;

    private View iv_workout_top_line;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isWorkout = getArguments().getBoolean("isWorkout", false);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_internet_workout, container, false);
    }

    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    private void initDelay(View view) {

        Looper.myQueue().addIdleHandler(() -> {

            iv_workout_top_line = view.findViewById(R.id.iv_workout_top_line);

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

            m_btHome_InternetDashboard.setAlpha(0.9f);
            m_btFullScreenExit_InternetDashboard.setAlpha(0.9f);

            m_clList_InternetDashboard.setVisibility(View.VISIBLE);
            m_clView_InternetDashboard.setVisibility(View.INVISIBLE);
            m_clViewTopBar_InternetDashboard.setVisibility(View.VISIBLE);

            m_btFullScreen_InternetDashboard.setOnClickListener(btInternetOnClick);
            m_btFullScreenExit_InternetDashboard.setOnClickListener(btInternetOnClick);
            m_btFullScreenExit_InternetDashboard.setOnTouchListener(btInternetOnTouch);
            m_btHome_InternetDashboard.setOnClickListener(btInternetOnClick);


            View iv_vsContent = viewStub.inflate();
            m_wvBrowser = iv_vsContent.findViewById(R.id.wbBrowser_InternetDashboard);
            m_wvBrowser.setListener(mActivity, this);

            m_wvBrowser.setLayerType(View.LAYER_TYPE_HARDWARE, null);

//            m_wvBrowser.setMixedContentAllowed(false);

            m_wvBrowser.setWebViewClient(new WebViewClient());
            m_wvBrowser.setScrollbarFadingEnabled(false);
            //   String newUA = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12) AppleWebKit/602.1.50 (KHTML, like Gecko) Version/10.0 Safari/602.1.50";
            myChrome = new MyChrome();
            m_wvBrowser.setWebChromeClient(myChrome);
            webSettings = m_wvBrowser.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            webSettings.setAllowFileAccess(true);
            webSettings.setAppCacheEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setLoadsImagesAutomatically(false);
            //  webSettings.setUserAgentString(newUA);

            //    webSettings.setBlockNetworkImage(true);


            btNetflix_InternetDashboard.setOnClickListener(btInternetOnClick);
            btHulu_InternetDashboard.setOnClickListener(btInternetOnClick);
            btYoutube_InternetDashboard.setOnClickListener(btInternetOnClick);
            btABC_InternetDashboard.setOnClickListener(btInternetOnClick);
            btNBC_InternetDashboard.setOnClickListener(btInternetOnClick);
            btCNN_InternetDashboard.setOnClickListener(btInternetOnClick);
            btFoxNews_InternetDashboard.setOnClickListener(btInternetOnClick);

            return false;
        });
    }


    /**
     * system app 的 WebView
     */
    @SuppressLint({"PrivateApi", "DiscouragedPrivateApi"})
    private void initHookWebView() {
        // 如果是非系統程序則按正常程式走
//        if (Process.myUid() != Process.SYSTEM_UID) {
//            return;
//        }
        if (ApplicationInfo.FLAG_SYSTEM != 1) {
            return;
        }

        int sdkInt = Build.VERSION.SDK_INT;
        try {
            Class<?> factoryClass = Class.forName("android.webkit.WebViewFactory");
            Field field = factoryClass.getDeclaredField("sProviderInstance");
            field.setAccessible(true);
            Object sProviderInstance = field.get(null);
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
            } else {
                Log.e("HOOK_WEB", "Hook failed!");
            }
        } catch (Exception e) {
            Log.e("HOOK_WEB", e.getLocalizedMessage());
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!isConnected(mActivity)) {
            showToastAlert(getString(R.string.no_internet_toast_text), mActivity);
        }

        initHookWebView();
        initDelay(view);
    }

    private void openBrowser(boolean open) {

        groupV.setVisibility(open ? View.VISIBLE : View.INVISIBLE);
        tvTitle.setVisibility(open ? View.INVISIBLE : View.VISIBLE);

        m_clList_InternetDashboard.setVisibility(open ? View.INVISIBLE : View.VISIBLE);
        m_clView_InternetDashboard.setVisibility(open ? View.VISIBLE : View.INVISIBLE);
        m_wvBrowser.setVisibility(open ? View.VISIBLE : View.INVISIBLE);
    }

    //開啟跟關閉
    private void fullScreen(boolean full) {

        try {
            ((WorkoutDashboardActivity) requireActivity()).isWebViewFull = full;
            ((WorkoutDashboardActivity) mActivity).makeFullScreen(full);

            if (full) {
                ((WorkoutDashboardActivity) requireActivity()).levelFloat();
                ((WorkoutDashboardActivity) requireActivity()).inclineFloat();
                iv_workout_top_line.setVisibility(View.INVISIBLE);
            } else {
                ((WorkoutDashboardActivity) requireActivity()).removeInclineView();
                ((WorkoutDashboardActivity) requireActivity()).removeLevelView();
                iv_workout_top_line.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        groupV.setVisibility(full ? View.INVISIBLE : View.VISIBLE);

        m_clViewTopBar_InternetDashboard.setVisibility(full ? View.INVISIBLE : View.VISIBLE);
        m_btFullScreenExit_InternetDashboard.setVisibility(full ? View.VISIBLE : View.INVISIBLE);
    }

    private final Button.OnTouchListener btInternetOnTouch = new Button.OnTouchListener() {

        @SuppressLint("ClickableViewAccessibility")
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

            CommonUtils.closePackage(requireActivity());
            if (view.getId() == R.id.btNetflix_InternetDashboard) {
                MyApplication.SSEB = false;
                ((WorkoutDashboardActivity) mActivity).startFloatingDashboard(4);
                new RxTimer().timer(100, number -> {
                    try {

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setClassName("com.netflix.mediaclient", "com.netflix.mediaclient.ui.launch.UIWebViewActivity");
                        //  intent.setData(Uri.parse("https://www.netflix.com/tw/"));
                        startActivity(intent);
                        mActivity.overridePendingTransition(0, 0);
                    } catch (Exception e) {
                        ((WorkoutDashboardActivity) mActivity).removeFloatView();
                        Toasty.warning(getInstance(), "NO Netflix APP", Toasty.LENGTH_LONG).show();
                    }
                });
            } else if (view.getId() == R.id.btHulu_InternetDashboard) {
                MyApplication.SSEB = false;
                ((WorkoutDashboardActivity) mActivity).startFloatingDashboard(4);
                //   new RxTimer().timer(500, number -> {
                try {

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    //  intent.setPackage("com.hulu.plus");
                    intent.setClassName("com.hulu.plus", "com.hulu.features.splash.SplashActivity");
                    startActivity(intent);
                    mActivity.overridePendingTransition(0, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                    ((WorkoutDashboardActivity) mActivity).removeFloatView();
                    Toasty.warning(getInstance(), "NO hulu APP", Toasty.LENGTH_LONG).show();
                }
                //    });
            } else if (view.getId() == R.id.btYoutube_InternetDashboard) {
                openBrowser(true);
                if (m_wvBrowser.getUrl() != null && m_wvBrowser.getUrl().contains("youtube.com/")) {
                    return;
                }
                m_wvBrowser.loadUrl("https://www.youtube.com/");
            } else if (view.getId() == R.id.btABC_InternetDashboard) {
                MyApplication.SSEB = false;
                ((WorkoutDashboardActivity) mActivity).startFloatingDashboard(4);
                //    new RxTimer().timer(500, number -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setClassName("com.abc.abcnews", "com.abc.abcnews.ui.navigation.MainNavigationActivity");
                    startActivity(intent);
                    mActivity.overridePendingTransition(0, 0);
                } catch (Exception e) {
                    ((WorkoutDashboardActivity) mActivity).removeFloatView();
                    Toasty.warning(getInstance(), "NO abc NEWS APP", Toasty.LENGTH_LONG).show();
                }
                //    });
            } else if (view.getId() == R.id.btNBC_InternetDashboard) {
                MyApplication.SSEB = false;
                ((WorkoutDashboardActivity) mActivity).startFloatingDashboard(4);
                //   new RxTimer().timer(500, number -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setClassName("com.zumobi.msnbc", "com.nbc.activities.MainActivity");
                    startActivity(intent);
                    mActivity.overridePendingTransition(0, 0);
                } catch (Exception e) {
                    ((WorkoutDashboardActivity) mActivity).removeFloatView();
                    Toasty.warning(getInstance(), "NO NBC NEWS APP", Toasty.LENGTH_LONG).show();
                }
                //   });
            } else if (view.getId() == R.id.btCNN_InternetDashboard) {
                MyApplication.SSEB = false;
                ((WorkoutDashboardActivity) mActivity).startFloatingDashboard(4);
                //   new RxTimer().timer(500, number -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setClassName("com.cnn.mobile.android.phone", "com.cnn.mobile.android.phone.features.splash.SplashActivity");
                    startActivity(intent);
                    mActivity.overridePendingTransition(0, 0);
                } catch (Exception e) {
                    ((WorkoutDashboardActivity) mActivity).removeFloatView();
                    Toasty.warning(getInstance(), "NO CNN APP", Toasty.LENGTH_LONG).show();
                }
                //    });
            } else if (view.getId() == R.id.btFoxNews_InternetDashboard) {
                MyApplication.SSEB = false;
                ((WorkoutDashboardActivity) mActivity).startFloatingDashboard(4);
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    //    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClassName("com.foxnews.android", "com.foxnews.android.corenav.StartActivity");
                    startActivity(intent);
                    mActivity.overridePendingTransition(0, 0);
                } catch (Exception e) {
                    ((WorkoutDashboardActivity) mActivity).removeFloatView();
                    Toasty.warning(getInstance(), "NO FOX NEWS APP", Toasty.LENGTH_LONG).show();
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
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {
    }

    @Override
    public void onExternalPageRequest(String url) {
    }

    private class MyChrome extends WebChromeClient {

        private View mCustomView;
        private CustomViewCallback mCustomViewCallback;
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
        public void onShowCustomView(View paramView, CustomViewCallback paramCustomViewCallback) {
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
            Log.d(TAG, "onShowCustomView:@@@@@@@ ");
            ((WorkoutDashboardActivity) requireActivity()).isWebViewFull = true;
            ((WorkoutDashboardActivity) requireActivity()).levelFloat();
            ((WorkoutDashboardActivity) requireActivity()).inclineFloat();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private static final String TAG = "InternetFragment";

    @Override
    public void onDestroy() {
        if (m_wvBrowser != null) m_wvBrowser.onDestroy();
        ((WorkoutDashboardActivity) requireActivity()).isWebViewFull = false;

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

        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (m_wvBrowser != null) m_wvBrowser.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (groupV != null) groupV = null;
        ((WorkoutDashboardActivity) requireActivity()).isWebViewFull = false;
    }

//    private void startFloatingDashboard() {
//
//        MyApplication.SSEB = false;
//        if (!FloatingWorkoutDashboardService.isStarted) {
//            Intent serviceIntent = new Intent(getInstance(), FloatingWorkoutDashboardService.class);
//            serviceIntent.putExtra("TYPE", 4);
//            mActivity.getApplicationContext().startService(serviceIntent);
//        }
//
//    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }


    private Button btnFullScreenExit;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private boolean floatButtonIsStarted;

    @SuppressLint("ClickableViewAccessibility")
    private void showBtnFullScreenExit() {
        Log.d("PPPPPPP", "showBtnFullScreenExit");
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
                    Log.d("PPPPPPP", "showBtnFullScreenExit:CLICK ");
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

    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;

        @SuppressLint("ClickableViewAccessibility")
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
                        windowManager.updateViewLayout(view, layoutParams);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

    public void webViewExitRemove() {
        try {
            if (windowManager != null) {
                windowManager.removeView(btnFullScreenExit);
                floatButtonIsStarted = false;
                windowManager = null;
                btnFullScreenExit = null;
                if (myChrome != null) myChrome.onHideCustomView();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}