package com.dyaco.spiritbike.settings.appupdate;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.dyaco.spiritbike.support.banner.util.LogUtils;
import com.dyaco.spiritbike.support.download.ApiHelper;
import com.dyaco.spiritbike.support.download.ApiInterface;
import com.dyaco.spiritbike.support.download.DownloadListener;
import com.dyaco.spiritbike.support.download.DownloadUtil;
import com.dyaco.spiritbike.support.download.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.reactivex.annotations.NonNull;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DownLoadAppUtil extends DownloadUtil {
    private static final String TAG = "DownloadUtil";
    public static final String PATH_CHALLENGE_VIDEO = Environment.getExternalStorageDirectory() + "/SoleDownloadFile";
    protected ApiInterface mApi;
    private Call<ResponseBody> mCall;
    private File mFile;
    private Thread mThread;
    private String mVideoPath;


    public DownLoadAppUtil() {
        if (mApi == null) {
            mApi = ApiHelper.getInstance().buildRetrofit("https://github.com/")
                    .createService(ApiInterface.class);
        }
    }

    public void downloadFile(String url ,String packageName,  DownloadListener downloadListener) {
        //      String name = url;
        if (FileUtils.createOrExistsDir(PATH_CHALLENGE_VIDEO)) {
//            int i = name.lastIndexOf('/');//找最後一個'/'出現的位置
//            if (i != -1) {
//                name = name.substring(i);
//              //  mVideoPath = PATH_CHALLENGE_VIDEO + name;
//                mVideoPath = PATH_CHALLENGE_VIDEO + "/Sole.apk";
//            }
            mVideoPath = PATH_CHALLENGE_VIDEO + File.separator + packageName;
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
                            LogUtils.d("mCall ->" +"message:" + response.message() +"/code:" + response.code());
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
        }    }

    private void writeFile2Disk(Response<ResponseBody> response, File file, DownloadListener downloadListener) {
        downloadListener.onStart();
        long currentLength = 0;
        OutputStream os = null;

       LogUtils.d("writeFile2Disk ->" + response.message() +"/code:" + response.code());

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


    @Override
    public void deleteFile() {
        File deleteFile = new File(DownLoadAppUtil.PATH_CHALLENGE_VIDEO);
        RecursionDeleteFile(deleteFile);
        Log.d("UPDATE@@@", "deleteFile111: " + deleteFile.exists()); // false
        if (!deleteFile.exists()) {
            boolean d = deleteFile.mkdirs();
            Log.d("UPDATE@@@", "deleteFile222: " + d); //true
        }
    }
}
