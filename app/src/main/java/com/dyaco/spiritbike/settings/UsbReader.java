package com.dyaco.spiritbike.settings;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Environment;
import android.util.ArrayMap;
import android.util.Log;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.fs.UsbFileStreamFactory;
import com.github.mjdev.libaums.partition.Partition;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UsbReader {
    private static final String TAG = UsbReader.class.getSimpleName();

    enum  FILE_TYPE {
        JSON, APK
    }

    enum FILE_STATUS {
        FILE_NOT_FOUND, FILE_FOUND, WRITE_FAIL
    }

    private static final String ACTION_USB_PERMISSION = "corestar.usb.permission";
    private static final String ACTION_USB_DEVICE_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED";
    private static final String ACTION_USB_DEVICE_DETACHED = "android.hardware.usb.action.USB_DEVICE_DETACHED";

    private Context context;

    private UsbManager usbManager;

    private IntentFilter filter;
    private BroadcastReceiver broadcastReceiver;

    private Map<String, CSUsbDevice> mCSUsbDevices = new ArrayMap<>();

    private UsbReaderListener mListener;

    public boolean isFileFinding = false;

    private File dir;

    public UsbReader(Context context) {
        this.context = context;
        this.usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        this.filter = getFilter();
        this.broadcastReceiver = getBroadcastReceiver();
        this.dir = context.getCacheDir(); // or new File("target root directory")
   //     this.dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SoleDownloadFile");
//        this.dir = context.getExternalFilesDir("/sam_files");
    }

    public boolean isFileFinding() {
        return isFileFinding;
    }

    public String getFilePath() {
        return dir.getPath();
    }

    public void findFile(CSUsbDevice device, String fileName) {
        if (device == null || fileName == null) return;

        FILE_TYPE file_type;

        if (fileName.endsWith(".json")) {
            file_type = FILE_TYPE.JSON;
        } else if (fileName.endsWith(".apk")) {
            file_type = FILE_TYPE.APK;
        } else {
            Log.w(TAG, "Unknown file type.");
            return;
        }
        if (!isFileFinding) {
            isFileFinding = true;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    findFileInBackground(device, fileName, file_type);
                }
            }).start();
        }
    }

    private void findFileInBackground(CSUsbDevice device, String fileName, FILE_TYPE type) {
        // TODO check device process

        if (!device.isPermissionGranted) {
            Log.w(TAG, "usb device permission not granted.");
            isFileFinding = false;
            return;
        }

        FILE_STATUS file_status = FILE_STATUS.FILE_NOT_FOUND;

        UsbFile srcFile;
        File targetFile;

        boolean isFileFound = false;

        String copyResult = null;

        try {
            device.getDevice().init();

            Partition partition = device.getDevice().getPartitions().get(0);
            FileSystem fileSystem = partition.getFileSystem();

            UsbFile root = fileSystem.getRootDirectory();
            UsbFile[] files = root.listFiles();

            for (UsbFile file : files) {
                if (!file.isDirectory()) {
                    if (file.getName().equals(fileName)) {
//                        Log.d(TAG, "file found: " + file.getAbsolutePath() + ", size: " + file.getLength());

                        isFileFound = true;

                        srcFile = file;

                        targetFile = new File(dir, fileName);

                        file_status = FILE_STATUS.FILE_FOUND;

                        copyResult = copyFile(fileSystem, srcFile, targetFile, fileSystem.getChunkSize(), type);

                        if (copyResult == null) {
                            file_status = FILE_STATUS.WRITE_FAIL;
                        }

                        break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (mListener != null) {
                mListener.onFindFile(fileName, file_status, type, copyResult);
            }

            device.getDevice().close();

            isFileFinding = false;
        }
    }

    private String copyFile(FileSystem fileSystem, UsbFile fromFile, File toFile, int chunksize, FILE_TYPE type) {
        if (toFile == null) {
            Log.w(TAG, "target file is null.");
            return null;//dont make a copy for a folder itself
        }
        Log.d("UsbUpdateActivity", "copyFile: " + chunksize +","+ type);
        BufferedInputStream fosfrom; //using bufferedinputstream for performance
        BufferedOutputStream fosto;

        String result = null;

        byte[] json = null;
        if (type == FILE_TYPE.JSON) {
            json = new byte[(int) fromFile.getLength()];
        }

        try {

            fosfrom = UsbFileStreamFactory.createBufferedInputStream(fromFile, fileSystem);
            fosto = new BufferedOutputStream(new FileOutputStream(toFile), chunksize);

            byte[] buffer = new byte[chunksize];

            int c;
            int total = 0;

            while ((c = fosfrom.read(buffer)) > 0) {
                // TODO append when needed

                fosto.write(buffer, 0, c);

                if (type == FILE_TYPE.JSON) {
                    System.arraycopy(buffer, 0, json, total, c);
                }

                total += c;
            }

            fosfrom.close();
            fosto.close();

            if (type == FILE_TYPE.JSON) {
                result = new String(json);
            }

            if (type == FILE_TYPE.APK) {
                result = toFile.getAbsolutePath();
            }
            Log.d("UsbUpdateActivity", "resultresult: " +result);
            return result;
        } catch (Exception ex) {
            Log.d("UsbUpdateActivity", "EXCEPTION copyFile: " + ex.getLocalizedMessage());
            ex.printStackTrace();
            return result;
        }
    }

    public void setListener(UsbReaderListener listener) {
        this.mListener = listener;

        if (mListener != null) {
            context.registerReceiver(broadcastReceiver, filter);
        }
    }

    public void unRegister() {
        context.unregisterReceiver(broadcastReceiver);
    }

    public void requestPermission(CSUsbDevice csUsbDevice) {
        if (csUsbDevice == null) return;
        if (mCSUsbDevices.containsKey(csUsbDevice.getName())) {
            PendingIntent permissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
            usbManager.requestPermission(csUsbDevice.getDevice().getUsbDevice(), permissionIntent);
        } else {
            Log.w(TAG, "device not exists or unmounted.");
        }
    }

    public List<CSUsbDevice> getUsbDevice() {
        mCSUsbDevices.clear(); // maybe call by function

        UsbMassStorageDevice[] usbMassStorageDevices = UsbMassStorageDevice.getMassStorageDevices(context);
        for (UsbMassStorageDevice device : usbMassStorageDevices) {
            mCSUsbDevices.put(device.getUsbDevice().getDeviceName(), new CSUsbDevice(device));
        }

        return new ArrayList<>(mCSUsbDevices.values());
    }

    private IntentFilter getFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(ACTION_USB_DEVICE_DETACHED);

        return filter;
    }

    private BroadcastReceiver getBroadcastReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Log.d(TAG, "action received. " + action);

                if (action.equals(ACTION_USB_PERMISSION)) {

                    synchronized (this) {
                        UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                        boolean isPermissionGranted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);

                        CSUsbDevice csUsbDevice = mCSUsbDevices.get(device.getDeviceName());
                        if (csUsbDevice != null) {
                            csUsbDevice.setPermissionGranted(isPermissionGranted);

                            if (mListener != null) {
                                mListener.onRequestPermission(csUsbDevice);
                            }
                        }
                    }
                }

                if (action.equals(ACTION_USB_DEVICE_ATTACHED)) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    Log.d(TAG, "usb device attached." + device.getDeviceName()); // works

                    if (mListener != null) {
                        mListener.onDeviceAttached(device.getDeviceName());
                    }
                }

                if (action.equals(ACTION_USB_DEVICE_DETACHED)) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    Log.d(TAG, "usb device detached." + device.getDeviceName());

                    if (mListener != null) {
                        mListener.onDeviceDetached(device.getDeviceName());
                    }
                }
            }
        };
    }

    public interface UsbReaderListener {
        void onRequestPermission(CSUsbDevice csUsbDevice);
        void onFindFile(String file, FILE_STATUS status, FILE_TYPE type, String data);
        //        void onFindFile(String file, FILE_STATUS status, byte[] data);
        void onDeviceAttached(String name);
        void onDeviceDetached(String name);
    }

    static class CSUsbDevice {
        private boolean isPermissionGranted = false;
        //        private boolean isMounted = false;
        private UsbMassStorageDevice usbMassStorageDevice;
        private String name;

        public CSUsbDevice(UsbMassStorageDevice usbMassStorageDevice) {
            this.usbMassStorageDevice = usbMassStorageDevice;
            this.name = usbMassStorageDevice.getUsbDevice().getDeviceName();
        }

        public boolean isPermissionGranted() {
            return isPermissionGranted;
        }

        public void setPermissionGranted(boolean permissionGranted) {
            isPermissionGranted = permissionGranted;
        }

        public String getName() {
            return name;
        }

        public UsbMassStorageDevice getDevice() {
            return usbMassStorageDevice;
        }
    }
}
