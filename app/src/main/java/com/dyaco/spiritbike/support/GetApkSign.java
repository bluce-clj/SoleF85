package com.dyaco.spiritbike.support;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Log;

import com.dyaco.spiritbike.support.download.DownloadUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class GetApkSign {

//    public static void main(String[] args) throws IOException, Exception {
//
//      //  String path = "E:\\android\\workspace\\trunk\\ccplay_2016\\CCPlay_Market\\build\\outputs\\apk\\CCPlay_Market-debug.apk";// apk的路径
//        String path = DownloadUtil.PATH_CHALLENGE_VIDEO + "/Sole.apk";
//        byte[] bytes = getSignaturesFromApk(path);
//        System.out.println(hexDigest(bytes));
//    }

    public String getApkMd5(String path) {
        String fileMd5 = "@";
      //  String path = DownloadUtil.PATH_CHALLENGE_VIDEO + "/Sole.apk";
        try {
            byte[] bytes = getSignaturesFromApk(path);
            fileMd5 = hexDigest(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileMd5;
    }

    public String hexDigest(byte[] bytes) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        byte[] md5Bytes = md5.digest(bytes);
        StringBuilder hexValue = new StringBuilder();
        for (byte md5Byte : md5Bytes) {
            int val = ((int) md5Byte) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    /**
     * 從APK中讀取簽名
     *
     * @param strFile strFile
     * @return
     * @throws IOException
     */
    private byte[] getSignaturesFromApk(String strFile) throws IOException {
        File file = new File(strFile);
        JarFile jarFile = new JarFile(file);
        try {
            JarEntry je = jarFile.getJarEntry("AndroidManifest.xml");
            byte[] readBuffer = new byte[8192];
            Certificate[] certs = loadCertificates(jarFile, je, readBuffer);
            if (certs != null) {
                for (Certificate c : certs) {
                    return c.getEncoded();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 加載簽名
     *
     * @param jarFile
     * @param je
     * @param readBuffer
     * @return
     */
    private Certificate[] loadCertificates(JarFile jarFile, JarEntry je, byte[] readBuffer) {
        try {
            InputStream is = jarFile.getInputStream(je);
            while (is.read(readBuffer, 0, readBuffer.length) != -1) {
            }
            is.close();
            return je != null ? je.getCertificates() : null;
        } catch (IOException e) {
        }
        return null;
    }

}