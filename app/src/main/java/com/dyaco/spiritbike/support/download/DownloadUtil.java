package com.dyaco.spiritbike.support.download;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.Locale;

import okhttp3.ResponseBody;
import io.reactivex.annotations.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DownloadUtil {
    private static final String TAG = "DownloadUtil";
    public static final String PATH_CHALLENGE_VIDEO = Environment.getExternalStorageDirectory() + "/SoleDownloadFile";
    protected ApiInterface mApi;
    private Call<ResponseBody> mCall;
    private File mFile;
    private Thread mThread;
    private String mVideoPath;

    public DownloadUtil() {
        if (mApi == null) {
            mApi = ApiHelper.getInstance().buildRetrofit("https://github.com/")
                    .createService(ApiInterface.class);
        }
    }

    public void downloadFile(String url, final DownloadListener downloadListener) {
        //      String name = url;
        if (FileUtils.createOrExistsDir(PATH_CHALLENGE_VIDEO)) {
//            int i = name.lastIndexOf('/');//找最後一個'/'出現的位置
//            if (i != -1) {
//                name = name.substring(i);
//              //  mVideoPath = PATH_CHALLENGE_VIDEO + name;
//                mVideoPath = PATH_CHALLENGE_VIDEO + "/Sole.apk";
//            }
            mVideoPath = PATH_CHALLENGE_VIDEO + File.separator + "Sole.apk";
        }
        Log.d("UPDATE@@@", "updateAPP: " + mVideoPath);
        if (TextUtils.isEmpty(mVideoPath)) {
            //資料夾創建失敗
            downloadListener.onFailure("PATH NULL");
            return;
        }

        mFile = new File(mVideoPath);

        if (!FileUtils.isFileExists(mFile) && FileUtils.createOrExistsFile(mFile)) {
            if (mApi == null) {
                downloadListener.onFailure("mApi == null");
                return;
            }
            mCall = mApi.downloadFile(url);
            mCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull final Response<ResponseBody> response) {
                    mThread = new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            //保存到本地
                            writeFile2Disk(response, mFile, downloadListener);
                        }
                    };
                    mThread.start();
                }

                @Override
                public void onFailure(@androidx.annotation.NonNull Call<ResponseBody> call, @androidx.annotation.NonNull Throwable t) {
                    downloadListener.onFailure("網路錯誤！");
                }
            });
        } else {
            Log.d("UPDATE@@@", "downloadFile: finish ");
            downloadListener.onFinish(mVideoPath);
        }
    }

    public void stop() {
        if (mCall != null) mCall.cancel();
    }

    private void writeFile2Disk(Response<ResponseBody> response, File file, DownloadListener downloadListener) {
        downloadListener.onStart();
        long currentLength = 0;
        OutputStream os = null;

        if (response.body() == null) {
            downloadListener.onFailure("source error！");
            return;
        }
        InputStream is = response.body().byteStream();
        long totalLength = response.body().contentLength();

        try {
            os = new FileOutputStream(file);
            int len;
            byte[] buff = new byte[1024];
            while ((len = is.read(buff)) != -1) {
                os.write(buff, 0, len);
                currentLength += len;
                //   Log.e(TAG, "當前進度: " + currentLength);
                downloadListener.onProgress((int) (100 * currentLength / totalLength), currentLength, totalLength);
                if ((int) (100 * currentLength / totalLength) == 100) {
                    downloadListener.onFinish(mVideoPath);
                }
            }
        } catch (FileNotFoundException e) {
            downloadListener.onFailure("file not found");
            e.printStackTrace();
        } catch (IOException e) {
            downloadListener.onFailure("");
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void deleteFile() {

        File deleteFile = new File(DownloadUtil.PATH_CHALLENGE_VIDEO);
        RecursionDeleteFile(deleteFile);
        Log.d("UPDATE@@@", "deleteFile111: " + deleteFile.exists()); // false
        if (!deleteFile.exists()) {
            boolean d = deleteFile.mkdirs();
            Log.d("UPDATE@@@", "deleteFile222: " + d); //true
        }
    }

    public void RecursionDeleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                RecursionDeleteFile(f);
            }
            file.delete();
        }
    }

    public void installAPK(Context context, String filePath) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 廣播裡面操作需要加上這句，存在於一個獨立的棧裡

        Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", new File(filePath));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");

        context.startActivity(intent);
        Log.d("安裝", "installAPK: ");

//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            /* Android N 写法*/
//            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            Uri contentUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", new File(filePath));
//            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
//        } else {
//            /* Android N之前的老版本写法*/
//            intent.setDataAndType(Uri.fromFile(new File("apk地址")), "application/vnd.android.package-archive");
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        }
//        startActivity(intent);

    }

    public String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public String getDownloadTime(NetworkCapabilities networkCapabilities, long total, long current) {

        String time;

        double x = total - current;

        int downSpeed = (networkCapabilities.getLinkDownstreamBandwidthKbps() / 8);

        time = String.format(Locale.getDefault(), "%.1f%s", (x / downSpeed) / 180, " MIN");

        return time;
    }


}