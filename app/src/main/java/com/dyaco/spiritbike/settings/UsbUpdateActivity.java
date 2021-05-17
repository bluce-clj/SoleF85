package com.dyaco.spiritbike.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.corestar.libs.device.Device;
import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.engineer.EngineerActivity;
import com.dyaco.spiritbike.engineer.RxSettingBean;
import com.dyaco.spiritbike.support.BaseAppCompatActivity;
import com.dyaco.spiritbike.support.BtnExitFullScreen;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.GetApkSign;
import com.dyaco.spiritbike.support.MsgEvent;
import com.dyaco.spiritbike.support.RxBus;
import com.dyaco.spiritbike.support.RxTimer;
import com.dyaco.spiritbike.support.download.DownloadUtil;
import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

import static android.net.http.HttpResponseCache.install;
import static com.dyaco.spiritbike.MyApplication.ON_USB_MODE_SET;
import static com.dyaco.spiritbike.MyApplication.getInstance;

public class UsbUpdateActivity extends BaseAppCompatActivity {
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String md5;
    private TextView tv_text;
    private TextView tvVersion;
    private Button btn_osOTA;
    private RotateAnimation am;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usb_update);

        getInstance().commandSetUsbMode(Device.USB_MODE.DATA);

        tv_text = findViewById(R.id.tv_text);
        tvVersion = findViewById(R.id.tv_version);
        btn_osOTA = findViewById(R.id.btn_osOTA);

        tvVersion.setText(new CommonUtils().getLocalVersionName(this));

        findViewById(R.id.bt_close).setOnClickListener(v -> {
            MyApplication.SSEB = false;
            finish();
        });

        setUsbReader();

        Disposable d = RxBus.getInstance().toObservable(MsgEvent.class).subscribe(msg -> {
            //Device.MCU_SET
            if (msg.getType() == ON_USB_MODE_SET) {
                runOnUiThread(() -> {
                    if (msg.getObj() == Device.MCU_SET.OK) {
                        btn_osOTA.setEnabled(true);
                    } else {
                        Toasty.error(getInstance(),"USB_MODE_ERROR", Toasty.LENGTH_LONG).show();
                    }
                });
            }
        });

        compositeDisposable.add(d);
    }

    private List<UsbReader.CSUsbDevice> mCSUsbDevice = new ArrayList<>();
    private UsbReader usbReader;

    private static final String TAG = "UsbUpdateActivity";

    private void setUsbReader() {
        usbReader = new UsbReader(this);
        Log.d(TAG, "setUsbReader: "+ usbReader);
        usbReader.setListener(new UsbReader.UsbReaderListener() {
            @Override
            public void onRequestPermission(UsbReader.CSUsbDevice csUsbDevice) {
                //   String log = "on request permission, device: " +csUsbDevice.getName() + ", granted: " + csUsbDevice.isPermissionGranted();
                //   Log.d(TAG, "onRequestPermission: ");
                findFile("update.json");
            }

            @Override
            public void onFindFile(String file, UsbReader.FILE_STATUS status, UsbReader.FILE_TYPE type, String data) {
                String log = "on find file, file name: " + file + ", status: " + status + ", type: " + type + ", data: " + data;

                Log.d(TAG, "onFindFile1: " + log);
                try {
                    UpdateBean updateBean;
                    if ((type == UsbReader.FILE_TYPE.JSON) && status == UsbReader.FILE_STATUS.FILE_FOUND) {
                        Log.d(TAG, "onFindFile2: ");
                        usbReader.isFileFinding = false;
                        updateBean = new Gson().fromJson(data, UpdateBean.class);
                        md5 = updateBean.getMD5();
                        //    Log.d(TAG, "onFindFile: " + (updateBean.getVersionCode() > new CommonUtils().getLocalVersionCode(mActivity)));
                        // TODO: 2/2/21  
                        if (updateBean.getVersionCode() >= new CommonUtils().getLocalVersionCode()) {
                            Log.d(TAG, "onFindFile3: ");
                            String fileName = updateBean.getPATH();
//                            int i = file.lastIndexOf('/');
//                            if (i != -1) fileName = file.substring(file.lastIndexOf('/'));
                            runOnUiThread(() -> {
                                tv_text.setText("There is a newer version available");
                                tvVersion.setText(updateBean.getVersion());
                            });

                            findFile(fileName);
                        } else {
                            runOnUiThread(() -> {
                                tv_text.setText("Your software version is the latest");
                                clearDialog();
                            });
                        }
                    } else if ((type == UsbReader.FILE_TYPE.APK) && status == UsbReader.FILE_STATUS.FILE_FOUND) {
                        Log.d(TAG, "onFindFile4: ");
                        if (md5.equalsIgnoreCase(new GetApkSign().getApkMd5(data))) {
                            Log.d(TAG, "onFindFile6: ");
                            boolean isInstall = install(UsbUpdateActivity.this, data);

                            if (isInstall) {
                                runOnUiThread(() -> Toasty.success(UsbUpdateActivity.this, "The Installation is Complete", Toasty.LENGTH_LONG).show());
                            } else {
                                runOnUiThread(() -> Toasty.error(UsbUpdateActivity.this, "Installation Failed", Toasty.LENGTH_LONG).show());
                                finish();
                            }


                         //   new DownloadUtil().installAPK(UsbUpdateActivity.this, data);
                        } else {
                            Log.d(TAG, "onFindFile5: ");
                            runOnUiThread(() -> Toast.makeText(getInstance(), "APK ERROR", Toast.LENGTH_LONG).show());
                        }

                        clearDialog();
                    } else {
                        Log.d(TAG, "onFindFile7: ");
                        clearDialog();
                        runOnUiThread(() -> Toast.makeText(getInstance(), "FILE NOT FOUND", Toast.LENGTH_LONG).show());
                    }
                } catch (Exception e) {
                    Log.d(TAG, "onFindFile8: ");
                    e.printStackTrace();
                    clearDialog();
                    runOnUiThread(() -> Toast.makeText(getInstance(), "Update.json Error:" + e.toString(), Toast.LENGTH_LONG).show());
                }
            }

            @Override
            public void onDeviceAttached(String name) {
                String log = "on device attached, device name: " + name;
                Log.d(TAG, "onDeviceAttached: " + log);
                Log.d(TAG, "setUsbReader: "+ usbReader);
                if (findDevice()) {
                    rxTimer.timer(1000,number -> requestPermission());
//                    requestPermission();
                }
            }

            @Override
            public void onDeviceDetached(String name) {
                String log = "on device detached, device name: " + name;

                Log.d(TAG, "onDeviceDetached: " + log);
            }
        });


        btn_osOTA.setOnClickListener(v -> {
            if (findDevice()) {
                requestPermission();
            }
        });
    }

    private RxTimer rxTimer = new RxTimer();

    //1
    private boolean findDevice() {
        mCSUsbDevice = usbReader.getUsbDevice();
        return mCSUsbDevice.size() > 0;
    }

    //2
    private void requestPermission() {
        usbReader.requestPermission(mCSUsbDevice.get(0));
    }

    private void findFile(String fileName) {

        if (mCSUsbDevice.size() > 0) {

            Log.d(TAG, "setUsbReader: "+ usbReader);
            usbReader.findFile(mCSUsbDevice.get(0), fileName);
        } else {
            clearDialog();
        }

        showDialogProgress();
    }

    private void clearDialog() {
        if (dialog2 != null) {
            am = null;
            dialog2.dismiss();
            dialog2 = null;
        }
    }

    private Dialog dialog2;

    public void showDialogProgress() {
        runOnUiThread(() -> {

            try {
                if (dialog2 == null) {
                    //  dialog = new Dialog(requireActivity(), android.R.style.ThemeOverlay);
                    //  dialog = new Dialog(requireActivity(), android.R.style.Theme_Translucent);
                    dialog2 = new Dialog(this, R.style.DialogProgressTheme);
                    dialog2.setCanceledOnTouchOutside(true);
                    dialog2.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                    dialog2.getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    dialog2.getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
                    dialog2.setContentView(R.layout.dialog_progress);
                    ImageView imageView = dialog2.findViewById(R.id.image);
                    imageView.setBackgroundResource(R.drawable.icon_loading);
                    am = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    am.setDuration(800);
                    LinearInterpolator lir = new LinearInterpolator();
                    am.setInterpolator(lir);
                    am.setRepeatCount(Animation.INFINITE);
                    imageView.setAnimation(am);
                    am.startNow();
                    dialog2.show();
                    dialog2.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearDialog();
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
            compositeDisposable = null;
        }

        if (rxTimer != null) {
            rxTimer.cancel();
            rxTimer = null;
        }
        usbReader.unRegister();
        getInstance().commandSetUsbMode(Device.USB_MODE.CHARGER);
    }

    public boolean install(Context context, String apkPath) {
        PackageInstaller packageInstaller = context.getPackageManager().getPackageInstaller();
        PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL);
        String pkgName = getApkPackageName(context, apkPath);
        Log.d("安裝", "pkgName:" + pkgName);
        if (pkgName == null) {
            return false;
        }
        params.setAppPackageName(pkgName);
        try {
            Method allowDowngrade = PackageInstaller.SessionParams.class.getMethod("setAllowDowngrade", boolean.class);
            allowDowngrade.setAccessible(true);
            allowDowngrade.invoke(params, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        OutputStream os = null;
        InputStream is = null;
        try {
            int sessionId = packageInstaller.createSession(params);
            PackageInstaller.Session session = packageInstaller.openSession(sessionId);
            os = session.openWrite(pkgName, 0, -1);
            is = new FileInputStream(apkPath);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            session.fsync(os);
            os.close();
            os = null;
            is.close();
            is = null;
            session.commit(PendingIntent.getBroadcast(context, sessionId,
                    new Intent(Intent.ACTION_MAIN), 0).getIntentSender());
        } catch (Exception e) {
            Log.d("安裝", e.getMessage());
            return false;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    /**
     * 获取apk的包名
     */
    public String getApkPackageName(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath, 0);
        if (info != null) {
            return info.packageName;
        } else {
            return null;
        }
    }
}