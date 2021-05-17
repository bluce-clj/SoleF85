package com.dyaco.spiritbike.support;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.dyaco.spiritbike.support.download.DownloadUtil.PATH_CHALLENGE_VIDEO;

public class Write {

    public Write() {
    }

    public static void WriteFile(String message) {
        FileOutputStream fop = null;
        File file;

        try {
             file = new File(PATH_CHALLENGE_VIDEO + File.separator + "SoleF85_Log.txt");
          //  file = new File(sdcard, MyApplication.getInstance().getResources().getString(R.string.app_name) + "Log.txt"); //輸出檔案位置
            Log.i("Write File:", file + "");
            fop = new FileOutputStream(file);

            if (!file.exists()) { // 如果檔案不存在，建立檔案
                file.createNewFile();
            }

            byte[] contentInBytes = message.getBytes();// 取的字串內容bytes

            fop.write(contentInBytes); //輸出
            fop.flush();
            fop.close();

            // Toast.makeText(context, "輸出完成", Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            Log.i("Write E:", e + "");
            e.printStackTrace();
        } finally {
            try {
                if (fop != null) {
                    fop.close();
                }
            } catch (IOException e) {
                Log.i("Write IOException", e + "");
                e.printStackTrace();
            }
        }
    }
}