package com.dyaco.spiritbike.settings;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.engineer.EngineerActivity;
import com.dyaco.spiritbike.product_flavors.InstallResultReceiver;
import com.dyaco.spiritbike.support.BaseAppCompatActivity;
import com.dyaco.spiritbike.support.BtnExitFullScreen;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.GetApkSign;
import com.dyaco.spiritbike.support.LoadingDialog;
import com.dyaco.spiritbike.support.RxTimer;
import com.dyaco.spiritbike.support.download.DownloadListener;
import com.dyaco.spiritbike.support.download.DownloadUtil;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.support.kProgressHudUtil;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.tbruyelle.rxpermissions3.RxPermissions;
import com.tencent.mmkv.MMKV;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

import es.dmoral.toasty.Toasty;
import io.reactivex.rxjava3.disposables.Disposable;

import static com.dyaco.spiritbike.MyApplication.getInstance;

public class UpdateSoftwareActivity extends BaseAppCompatActivity {
    private SeekBar download_progress;
    private DownloadUtil downloadUtil;
    private TextView tv_min;
    private long startTime;
    private NetworkCapabilities nc;
    private Disposable disposable;
    private String fileUrl;
    private String md5;
    private boolean isForce;
    private ProgressBar pb_install;
    Button bt_close;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_software);

        Log.d("休眠", "onCreate: UPDATE_SOFT_ACTIVITY");

        MMKV.defaultMMKV().encode("CheckUpdateIng", true);

        CommonUtils.closePackage(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        fileUrl = bundle.getString("fileUrl", "");
        md5 = bundle.getString("md5", "");
        isForce = bundle.getBoolean("isForce", false);

        Log.d("UPDATE@@@", "onCreate: " + fileUrl + "," + md5 + "," + isForce);

        download_progress = findViewById(R.id.download_progress);

        tv_min = findViewById(R.id.tv_text);

        pb_install = findViewById(R.id.pb_install);

        bt_close = findViewById(R.id.bt_close);

        if (isForce) {
            bt_close.setEnabled(false);
        } else {
            bt_close.setEnabled(true);
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission")
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        if (netInfo != null) {
            nc = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            getPermission();
        } else {
            Toast.makeText(getInstance(), "no internet", Toast.LENGTH_SHORT).show();
            bt_close.setEnabled(true);
            MyApplication.SSEB = false;
            finish();
        }

    }

    private void getPermission() {
        disposable = new RxPermissions(this)
                .requestEachCombined(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .subscribe(permission -> {
                    if (permission.granted) {
                        updateAPP();
                    }
                });
    }

    public void onCloseE(View view) {
//        if (downloadUtil != null) downloadUtil.stop();
//
//        MyApplication.SSEB = false;
//        finish();
    }

    private void updateAPP() {

        Log.d("UPDATE@@@", "updateAPP: ");

        //  String PICTURE_URL = "http://212.183.159.230/200MB.zip";
        //  String PICTURE_URL = "http://212.183.159.230/5MB.zip";
        //   String PICTURE_URL = "https://filetransfer.io/data-package/alfqmy5r/download";

        downloadUtil = new DownloadUtil();
        downloadUtil.deleteFile();
        downloadUtil.downloadFile(fileUrl, new DownloadListener() {
            @Override
            public void onStart() {
                Log.d("更新", "開始安裝: ");
            }

            @Override
            public void onProgress(final int currentLength, final long count, final long total) {

                runOnUiThread(() -> {
                    download_progress.setProgress(currentLength);
                    // tv_min.setText(String.format(Locale.getDefault(),"%d/%d", count, total));
                    //   tv_min.setText(String.format("%s/%s", downloadUtil.readableFileSize(count), downloadUtil.readableFileSize(total)));

                  //  Log.d("休眠", "onProgress: count:" + count +"/total:"+ total);
                    String x = downloadUtil.getDownloadTime(nc, total, count);
                    tv_min.setText(x);
                });
            }

            @Override
            public void onFinish(final String localPath) {

                runOnUiThread(() -> {
                    pb_install.setVisibility(View.VISIBLE);
                    download_progress.setProgress(100);
                    tv_min.setText("DONE");
                    bt_close.setEnabled(false);
                });

                //   Log.i("UPDATE@@@", "onFinish: urlMd5: " + md5 + ", fileMd5:" + new GetApkSign().getApkMd5(localPath));
                MyApplication.SSEB = false;
                Log.d("休眠", "onFinish: ");
                if (md5.equals(new GetApkSign().getApkMd5(localPath))) {
                    Log.d("休眠", "installAPK: ");

                    boolean isInstall = install(UpdateSoftwareActivity.this, localPath);
                    if (isInstall) {
                     //   finishAffinity();
                     //   runOnUiThread(() -> Toasty.success(UpdateSoftwareActivity.this, "The Installation is Complete", Toasty.LENGTH_LONG).show());
                    } else {
                        runOnUiThread(() -> Toasty.error(UpdateSoftwareActivity.this, "Installation Failed", Toasty.LENGTH_LONG).show());
                        finish();
                    }

//                        Intent intent = new Intent(Intent.ACTION_VIEW);
//                        Uri apkUri = FileProvider.getUriForFile(UpdateSoftwareActivity.this, getPackageName() + ".fileprovider", new File(localPath));
//                     //   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
//                        startActivityForResult(intent, 0x666);

                } else {
                    Log.d("休眠", "installAPK: " +md5 +","+ new GetApkSign().getApkMd5(localPath) );
                    downloadUtil.deleteFile();
                    runOnUiThread(() -> Toasty.error(getInstance(), "File Signature Verification Failed or Corruption", Toast.LENGTH_LONG).show());
                    finish();
                }

                //  downloadUtil.installAPK(UpdateSoftwareActivity.this, "/storage/emulated/0/SoleDownloadFile/aa.apk");
            }

            @Override
            public void onFailure(final String errorInfo) {
                runOnUiThread(() -> {
                    download_progress.setProgress(0);
                    tv_min.setText(errorInfo);
                    bt_close.setEnabled(true);
                    MyApplication.SSEB = false;

                    finish();
                    if (!"".equals(errorInfo)) {
                        Toasty.error(UpdateSoftwareActivity.this, "Download ERROR:" + errorInfo, Toasty.LENGTH_LONG).show();
                    }
                    Log.d("休眠", "onFailure: " + errorInfo);
                });
            }
        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 0x666) { //cancel > resultCode 0
//            MyApplication.SSEB = false;
//            finish();
//        }
//        Log.d("安裝", "onActivityResult: " + requestCode + "," + resultCode + ",");
//    }

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

//    private BtnExitFullScreen btnExitFullScreen = new BtnExitFullScreen(this);

    @Override
    protected void onPause() {
        super.onPause();
//        btnExitFullScreen.showBtnFullScreenExit(DashboardActivity.class, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        btnExitFullScreen.removeFloatView();
//        btnExitFullScreen = null;

        if (disposable != null) disposable.dispose();
        if (downloadUtil != null) downloadUtil.stop();
        MMKV.defaultMMKV().encode("CheckUpdateIng", false);
        Log.d("休眠", "UPDATE_onDestroy: ");
    }
}