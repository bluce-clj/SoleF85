package com.dyaco.spiritbike.support;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.PaintDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.utils.widget.ImageFilterButton;
import androidx.core.content.ContextCompat;

import com.allenliu.badgeview.BadgeFactory;
import com.allenliu.badgeview.BadgeView;
import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.product_flavors.InitProduct;
import com.dyaco.spiritbike.product_flavors.ModeEnum;
import com.dyaco.spiritbike.support.banner.util.LogUtils;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.math.RoundingMode;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static android.view.MotionEvent.ACTION_UP;
import static com.dyaco.spiritbike.MyApplication.MODE;
import static com.dyaco.spiritbike.MyApplication.UNIT_E;
import static com.dyaco.spiritbike.MyApplication.getInstance;

public class CommonUtils {

//    public static Drawable changeDrawableColor
//            (Context context, int icon, int newColor) {
//
//        Drawable mDrawable = ContextCompat
//                .getDrawable(context, icon)
//                .mutate();
//
//        mDrawable.setColorFilter(
//                new PorterDuffColorFilter(newColor, PorterDuff.Mode.SRC_IN));
//
//        return mDrawable;
//    }

    public static int incF2I(double inc) {
        return (int) (inc * 2d);
    }

    public static double incI2F(int inc) {
        return (inc) / 2d;
    }


    public static String subZeroAndDot(String s) {
        try {
            if (s.indexOf(".") > 0) {
                s = s.replaceAll("0 ?$", "");//去掉多餘的0
                s = s.replaceAll("[.]$", "");//如最後一位是.則去掉
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }


    public static String addZero(String s) {

        try {
            if (s.indexOf(".") > 0) {
                s = s.replaceAll("0 ?$", "");//去掉多餘的0
                s = s.replaceAll("[.]$", "");//如最後一位是.則去掉
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return String.format(Locale.getDefault(), "%02d", Long.parseLong(s));
    }

    public static int null2Zero(String s) {

        if (s == null || s.equals("")) {
            s = "0";
        }

        return Integer.parseInt(s);
    }


    public static int getAvatarResourceId(int id) {
        int avatarIcon = 0;

        if (id == R.id.rbAvatar01_AvatarIcon) {
            avatarIcon = R.drawable.avatar_create_1_inactive;
        } else if (id == R.id.rbAvatar02_AvatarIcon) {
            avatarIcon = R.drawable.avatar_create_2_inactive;
        } else if (id == R.id.rbAvatar03_AvatarIcon) {
            avatarIcon = R.drawable.avatar_create_3_inactive;
        } else if (id == R.id.rbAvatar04_AvatarIcon) {
            avatarIcon = R.drawable.avatar_create_4_inactive;
        } else if (id == R.id.rbAvatar05_AvatarIcon) {
            avatarIcon = R.drawable.avatar_create_5_inactive;
        } else if (id == R.id.rbAvatar06_AvatarIcon) {
            avatarIcon = R.drawable.avatar_create_6_inactive;
        } else if (id == R.id.rbAvatar07_AvatarIcon) {
            avatarIcon = R.drawable.avatar_create_7_inactive;
        } else if (id == R.id.rbAvatar08_AvatarIcon) {
            avatarIcon = R.drawable.avatar_create_8_inactive;
        } else if (id == R.id.rbAvatar09_AvatarIcon) {
            avatarIcon = R.drawable.avatar_create_9_inactive;
        } else if (id == R.id.rbAvatar10_AvatarIcon) {
            avatarIcon = R.drawable.avatar_create_10_inactive;
        }

        return avatarIcon;
    }


    public static int getAvatarHeaderResourceId(int id) {
        int avatarIcon;

        if (id == R.drawable.avatar_create_1_inactive) {
            avatarIcon = R.drawable.avatar_header_1;
        } else if (id == R.drawable.avatar_create_2_inactive) {
            avatarIcon = R.drawable.avatar_header_2;
        } else if (id == R.drawable.avatar_create_3_inactive) {
            avatarIcon = R.drawable.avatar_header_3;
        } else if (id == R.drawable.avatar_create_4_inactive) {
            avatarIcon = R.drawable.avatar_header_4;
        } else if (id == R.drawable.avatar_create_5_inactive) {
            avatarIcon = R.drawable.avatar_header_5;
        } else if (id == R.drawable.avatar_create_6_inactive) {
            avatarIcon = R.drawable.avatar_header_6;
        } else if (id == R.drawable.avatar_create_7_inactive) {
            avatarIcon = R.drawable.avatar_header_7;
        } else if (id == R.drawable.avatar_create_8_inactive) {
            avatarIcon = R.drawable.avatar_header_8;
        } else if (id == R.drawable.avatar_create_9_inactive) {
            avatarIcon = R.drawable.avatar_header_9;
        } else if (id == R.drawable.avatar_create_10_inactive) {
            avatarIcon = R.drawable.avatar_header_10;
        } else {
            avatarIcon = R.drawable.avatar_header_guest;
        }

        return avatarIcon;
    }

    public static int getAvatarProfileResourceId(int id) {
        int avatarIcon = 0;

        if (id == R.drawable.avatar_create_1_inactive) {
            avatarIcon = R.drawable.avatar_profile_1;
        } else if (id == R.drawable.avatar_create_2_inactive) {
            avatarIcon = R.drawable.avatar_profile_2;
        } else if (id == R.drawable.avatar_create_3_inactive) {
            avatarIcon = R.drawable.avatar_profile_3;
        } else if (id == R.drawable.avatar_create_4_inactive) {
            avatarIcon = R.drawable.avatar_profile_4;
        } else if (id == R.drawable.avatar_create_5_inactive) {
            avatarIcon = R.drawable.avatar_profile_5;
        } else if (id == R.drawable.avatar_create_6_inactive) {
            avatarIcon = R.drawable.avatar_profile_6;
        } else if (id == R.drawable.avatar_create_7_inactive) {
            avatarIcon = R.drawable.avatar_profile_7;
        } else if (id == R.drawable.avatar_create_8_inactive) {
            avatarIcon = R.drawable.avatar_profile_8;
        } else if (id == R.drawable.avatar_create_9_inactive) {
            avatarIcon = R.drawable.avatar_profile_9;
        } else if (id == R.drawable.avatar_create_10_inactive) {
            avatarIcon = R.drawable.avatar_profile_10;
        } else {
            avatarIcon = R.drawable.avatar_profile_1;
        }

        return avatarIcon;
    }

    public static String getAgeByBirth(String birthdayStr) {

        if (birthdayStr == null || "".equals(birthdayStr)) return "0";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Date birthday = null;
        try {
            birthday = sdf.parse(birthdayStr);
        } catch (ParseException | NullPointerException e) {
            e.printStackTrace();
        }
        //Calendar：日歷
        /*從Calendar對象中或得一個Date對象*/
        Calendar cal = Calendar.getInstance();
        /*把出生日期放入Calendar類型的bir對象中，進行Calendar和Date類型進行轉換*/
        Calendar bir = Calendar.getInstance();
        if (birthday != null) {
            bir.setTime(birthday);
        } else {
            return "";
        }

        /*如果生日大於當前日期，則拋出異常：出生日期不能大於當前日期*/
        if (cal.before(birthday)) {
            throw new IllegalArgumentException("The birthday is before Now,It‘s unbelievable");
        }

        /*取出當前年月日*/
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayNow = cal.get(Calendar.DAY_OF_MONTH);
        /*取出出生年月日*/
        int yearBirth = bir.get(Calendar.YEAR);
        int monthBirth = bir.get(Calendar.MONTH);
        int dayBirth = bir.get(Calendar.DAY_OF_MONTH);
        /*大概年齡是當前年減去出生年*/
        int age = yearNow - yearBirth;
        /*如果出當前月小與出生月，或者當前月等於出生月但是當前日小於出生日，那麽年齡age就減一歲*/
        if (monthNow < monthBirth || (monthNow == monthBirth && dayNow < dayBirth)) {
            age--;
        }
        return String.valueOf(age);
    }

    public static String textCheckNull(String str) {
        if (str == null || "".equals(str) || str.length() == 0) {
            str = "0";
        }
        return str;
    }

    public static double convertUnit(UnitEnum unitEnum, int thisUnit, double num) {
        double x = 0;
        switch (unitEnum) {
            case DISTANCE:
            case SPEED:
                if (UNIT_E == thisUnit) {
                    x = num;
                } else {
                    x = thisUnit == 0 ? km2mi(num) : mi2km(num);
                }
                break;
            case WEIGHT:
                if (UNIT_E == thisUnit) {
                    x = num;
                } else {
                    x = thisUnit == 0 ? kg2lb((int) num) : lb2kg((int) num);
                }
                break;
            case HEIGHT:
                if (UNIT_E == thisUnit) {
                    x = num;
                } else {
                    x = thisUnit == 0 ? cm2in((int) num) : in2cm((int) num);
                }
                break;
        }

        return x;
    }

    public static int kg2lb(int weight) {
        return (int) Math.round(weight * 2.20462262);
    }

    public static int lb2kg(int weight) {
        return (int) Math.round(weight * 0.45359237);
    }

    public static int cm2in(int height) {
        return (int) Math.round(height * 0.39370079);
    }

    public static int in2cm(int height) {
        return (int) Math.round(height * 2.54);
    }

    public static int in2sin(int heightImperial) {
        double a = heightImperial / 12f;
        a = a - (long) a;
        return (int) Math.round(a * 12);
    }

    public static int in2ft(int heightImperial) {
        return heightImperial / 12;
    }

    public static double km2mi(double km) {

        return km / 1.609344;
    }

    public static double mi2km(double mi) {

        return mi / 0.62137;
    }

//    public static double kmh2mph(double kmh) {
//
//        return kmh / 0.62137;
//    }
//
//    public static double mph2kmh(double mph) {
//
//        return mph / 1.61;
//    }


    public static String setTime() {
//        int hours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
//        int minutes = Calendar.getInstance().get(Calendar.MINUTE);

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();


        //  if ("24".equals(getInstance().getDeviceSettingBean().getTime_unit())) {
        if (24 == getInstance().getDeviceSettingBean().getTime_unit()) {
            SimpleDateFormat dateFormat24 = new SimpleDateFormat("HH:mm", Locale.getDefault());
            return dateFormat24.format(date);
//            int h1 = hours / 10;
//            int h2 = hours - h1 * 10;
//            int m1 = minutes / 10;
//            int m2 = minutes - m1 * 10;
//            return String.format(Locale.getDefault(), "%d%d:%d%d", h1, h2, m1, m2);
        } else {
            SimpleDateFormat dateFormat12 = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            return dateFormat12.format(date);

//            if ("PM".equalsIgnoreCase(getInstance().getDeviceSettingBean().getTimeAM_PM())) {
//
//                if (hours > 12) {
//                    hours = hours - 12;
//                }
//
//                int h1 = hours / 10;
//                int h2 = hours - h1 * 10;
//                int m1 = minutes / 10;
//                int m2 = minutes - m1 * 10;
//
//                return String.format(Locale.getDefault(), "%d%d:%d%d PM", h1, h2, m1, m2);
//            } else {
//
//                if (hours == 0) {
//                    hours = 12;
//                }
//                int h1 = hours / 10;
//                int h2 = hours - h1 * 10;
//                int m1 = minutes / 10;
//                int m2 = minutes - m1 * 10;
//
//                return String.format(Locale.getDefault(), "%d%d:%d%d AM", h1, h2, m1, m2);
//            }
        }
    }


    private LayerDrawable getDiagramDrawable(Context context, int[] arrayNum) {
        final List<Drawable> images = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            PaintDrawable drawable = new PaintDrawable();
            drawable.getPaint().setColor(context.getColor(R.color.colorB4BEC7));
            images.add(drawable);
        }
        final LayerDrawable layerDrawable = new LayerDrawable(images.toArray(new Drawable[0]));

        int n = 0;
        for (int i = 0; i < 20; i++) {

            layerDrawable.setLayerGravity(i, Gravity.START | Gravity.BOTTOM);
            //  layerDrawable.setLayerSize(i, 50, 20 * i);
            layerDrawable.setLayerSize(i, 50, 60 * arrayNum[i]);
            layerDrawable.setLayerInsetLeft(i, n);
            n += 70;
        }

        return layerDrawable;
    }

    public byte[] getImageBytes(Context context, String num, int height) {

        int[] arrayNum = Arrays.stream(num.split("#", -1))
                .mapToInt(Integer::parseInt)
                .toArray();

        final List<Drawable> images = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            PaintDrawable drawable = new PaintDrawable();
            drawable.getPaint().setColor(context.getColor(R.color.colorB4BEC7));
            images.add(drawable);
        }
        final LayerDrawable layerDrawable = new LayerDrawable(images.toArray(new Drawable[0]));

        int n = 0;
        for (int i = 0; i < 20; i++) {
            layerDrawable.setLayerGravity(i, Gravity.START | Gravity.BOTTOM);
            layerDrawable.setLayerSize(i, 50, 60 * arrayNum[i]);
            layerDrawable.setLayerInsetLeft(i, n);
            n += 80;
        }
        Bitmap bitmap = Bitmap.createBitmap(layerDrawable.getIntrinsicWidth(), height, Bitmap.Config.ARGB_8888);
        layerDrawable.setBounds(0, 0, layerDrawable.getIntrinsicWidth(), height);
        layerDrawable.draw(new Canvas(bitmap));
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 10, stream);

        return stream.toByteArray();
    }

    public static byte[] getImageBytes2(Context context, String num, int segmentHeight, int imageHeight) {

        int[] arrayNum = Arrays.stream(num.split("#", -1))
                .mapToInt(Integer::parseInt)
                .toArray();

        final List<Drawable> images = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            PaintDrawable drawable = new PaintDrawable();
            drawable.getPaint().setColor(context.getColor(R.color.colorB4BEC7));
            images.add(drawable);
        }
        final LayerDrawable layerDrawable = new LayerDrawable(images.toArray(new Drawable[0]));

        int n = 0;
        for (int i = 0; i < 20; i++) {
            layerDrawable.setLayerGravity(i, Gravity.START | Gravity.BOTTOM);
            layerDrawable.setLayerSize(i, 50, segmentHeight * arrayNum[i]);
            layerDrawable.setLayerInsetLeft(i, n);
            n += 80;
        }

        Bitmap bitmap = Bitmap.createBitmap(layerDrawable.getIntrinsicWidth(), imageHeight, Bitmap.Config.ARGB_8888);
        layerDrawable.setBounds(0, 0, layerDrawable.getIntrinsicWidth(), imageHeight);
        layerDrawable.draw(new Canvas(bitmap));
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 10, stream);

        return stream.toByteArray();
    }

    public Bitmap getImageBitmap(Context context, String num, int segmentHeight, int imageHeight) {

        Bitmap bitmap = null;
        try {

            int[] arrayNum = Arrays.stream(num.split("#", -1))
                    .mapToInt(Integer::parseInt)
                    .toArray();

            final List<Drawable> images = new ArrayList<>();

            for (int i = 0; i < 20; i++) {
                PaintDrawable drawable = new PaintDrawable();
                drawable.getPaint().setColor(context.getColor(R.color.colorB4BEC7));
                images.add(drawable);
            }
            final LayerDrawable layerDrawable = new LayerDrawable(images.toArray(new Drawable[0]));

            int n = 0;
            for (int i = 0; i < 20; i++) {
                layerDrawable.setLayerGravity(i, Gravity.START | Gravity.BOTTOM);
                layerDrawable.setLayerSize(i, 50, segmentHeight * arrayNum[i]);
                layerDrawable.setLayerInsetLeft(i, n);
                n += 80;
            }

            bitmap = Bitmap.createBitmap(layerDrawable.getIntrinsicWidth(), imageHeight, Bitmap.Config.ARGB_8888);
            layerDrawable.setBounds(0, 0, layerDrawable.getIntrinsicWidth(), imageHeight);
            layerDrawable.draw(new Canvas(bitmap));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public Bitmap getDiagramBitmapXX(Context context, String num) {

        Bitmap bitmap = null;
        try {
            int[] numArray = Arrays.stream(num.split("#"))
                    .mapToInt(Integer::parseInt)
                    .toArray();

            int maxNum = findMaxInt(numArray);

            if (maxNum <= 1) {
                maxNum = 10;
            }

            double present = maxNum * 0.01;
            //  int max = (int) Math.round(mmm / mm);
            int max = 100;

            StringBuilder newNum = new StringBuilder();
            for (int dNum : numArray) {
                if (dNum == 0) {
                    newNum.append(1).append("#");
                } else {
                    newNum.append(Math.round(dNum / present)).append("#");
                }
            }

            newNum.setLength(newNum.length() - 1);

            int[] newNumArray = Arrays.stream(newNum.toString().split("#", -1))
                    .mapToInt(Integer::parseInt)
                    .toArray();

            //  Log.d("JJJJJJ", "LEVEL: " + Arrays.toString(newNumArray));

            final List<Drawable> segmentImage = new ArrayList<>();

            for (int i = 0; i < 20; i++) {
                PaintDrawable drawable = new PaintDrawable();
                drawable.getPaint().setColor(context.getColor(R.color.colorB4BEC7));
                segmentImage.add(drawable);
            }
            final LayerDrawable layerDrawable = new LayerDrawable(segmentImage.toArray(new Drawable[0]));

            int n = 0;
            for (int i = 0; i < 20; i++) {
                layerDrawable.setLayerGravity(i, Gravity.START | Gravity.BOTTOM);
                layerDrawable.setLayerSize(i, 20, newNumArray[i]);
                layerDrawable.setLayerInsetLeft(i, n);
                n += 25;
            }

            bitmap = Bitmap.createBitmap(layerDrawable.getIntrinsicWidth(), max, Bitmap.Config.ARGB_8888);
            layerDrawable.setBounds(0, 0, layerDrawable.getIntrinsicWidth(), max);
            layerDrawable.draw(new Canvas(bitmap));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public Bitmap getDiagramBitmapSmall(Context context, String num) {

        Bitmap bitmap = null;
        try {
            int[] numArray = Arrays.stream(num.split("#"))
                    .mapToInt(Integer::parseInt)
                    .toArray();

            int maxNum = findMaxInt(numArray);

            Log.d("JJJJJJ", "maxNum: " + maxNum);

            if (maxNum < 1) {
                maxNum = 1;
            }

            double present = maxNum * 0.01;
            //  int max = (int) Math.round(mmm / mm);
            int max = 100;

            Log.d("JJJJJJ", "present: " + present);

            StringBuilder newNum = new StringBuilder();
            for (int dNum : numArray) {
                if (dNum == 0) {
                    //  newNum.append(1).append("#");
                    newNum.append(5).append("#");
                } else {
                    newNum.append(Math.round(dNum / present)).append("#");
                    Log.d("JJJJJJ", "Math.round(dNum / present)" + Math.round(dNum / present));
                }
            }

            newNum.setLength(newNum.length() - 1);

            int[] newNumArray = Arrays.stream(newNum.toString().split("#", -1))
                    .mapToInt(Integer::parseInt)
                    .toArray();

            Log.d("JJJJJJ", "incline: " + Arrays.toString(newNumArray));

            final List<Drawable> segmentImage = new ArrayList<>();

            for (int i = 0; i < 20; i++) {
                PaintDrawable drawable = new PaintDrawable();
                drawable.getPaint().setColor(context.getColor(R.color.colorB4BEC7));
                segmentImage.add(drawable);
            }
            final LayerDrawable layerDrawable = new LayerDrawable(segmentImage.toArray(new Drawable[0]));

            int n = 0;
            for (int i = 0; i < 20; i++) {
                layerDrawable.setLayerGravity(i, Gravity.START | Gravity.BOTTOM);
                layerDrawable.setLayerSize(i, 10, newNumArray[i]);
                //  layerDrawable.setLayerSize(i, 20, newNumArray[i]);
                layerDrawable.setLayerInsetLeft(i, n);
                n += 15;
                //   n += 30;
            }

            bitmap = Bitmap.createBitmap(layerDrawable.getIntrinsicWidth(), max, Bitmap.Config.ARGB_8888);
            layerDrawable.setBounds(0, 0, layerDrawable.getIntrinsicWidth(), max);
            layerDrawable.draw(new Canvas(bitmap));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

//    public static Bitmap readBitMap(Context context, Drawable resId) {
//        BitmapFactory.Options opt = new BitmapFactory.Options();
//        opt.inPreferredConfig = Bitmap.Config.RGB_565;
//        opt.inPurgeable = true;
//        opt.inInputShareable = true;
//        ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);
//        return BitmapFactory.decodeStream(is, null, opt);
//    }


    /**
     * New MaxL / Old Max L =New L / Old L
     * New MaxL =  New L* Old MaxL / Old L
     * New L= New MaxL * Old L / Old MaxL
     */


    /**
     * @param currentLevel    2
     * @param newLevel        3
     * @param currentMaxLevel 13
     * @return 19
     */
    public static int getNewMaxLevel(float currentLevel, float newLevel, float currentMaxLevel) {

        //   int newMaxLevel = Math.round((newLevel * currentMaxLevel) / currentLevel);
        //if (newMaxLevel > 20) newMaxLevel = (int)currentMaxLevel;
        //  newMaxLevel = MathUtils.clamp(newMaxLevel, 5, 20);

        int newMaxLevel = (int) Math.floor((newLevel * currentMaxLevel) / currentLevel);
        return newMaxLevel;
    }

    public static int getNewMaxIncline(float currentLevel, float newLevel, float currentMaxLevel) {
        //   return incF2I((newLevel * currentMaxLevel) / currentLevel);
        //  return incF2I((newLevel * currentMaxLevel) / currentLevel);
        int newMaxLevel = (int) Math.floor((newLevel * currentMaxLevel) / currentLevel);
        Log.d("KKKKKKK", "getNewMaxIncline: (" + newLevel + " * " + currentMaxLevel + ") / (原始INCLINE)" + currentLevel + " = " + ((int) Math.floor((newLevel * currentMaxLevel) / currentLevel)));
        return newMaxLevel;
    }

    /**
     * @param newMaxLevel     自己設定的 MAX LEVEL       13      19
     * @param currentLevel    Program的預設Level        2    2
     * @param currentMaxLevel 7                      7      13
     * @return newLevel
     */
    public static int calcCurrentLevel(float newMaxLevel, float currentLevel, float currentMaxLevel) {
        // Log.i("COMMOn", "getCurrentLevel: " + newMaxLevel +","+ currentLevel +","+ currentMaxLevel +","+ Math.round((newMaxLevel * currentLevel) / currentMaxLevel));
        //  return Math.round((newMaxLevel * currentLevel) / currentMaxLevel);
        return (int) Math.floor((newMaxLevel * currentLevel) / currentMaxLevel);
    }

    public static int convertDpToPixel(float dpValue, Context context) {
        float scale = getDensity(context);
        return (int) (dpValue * scale + 0.5F);
    }

    /**
     * Covert px to dp
     *
     * @param px
     * @param context
     * @return dp
     */
    public static float convertPixelToDp(float px, Context context) {
        return px / getDensity(context);
    }

    /**
     * 取得螢幕密度
     * 120dpi = 0.75
     * 160dpi = 1 (default)
     * 240dpi = 1.5
     *
     * @param context
     * @return
     */
    public static float getDensity(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.density;
    }

    public static boolean isInteger(String str) {
        // Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        Pattern pattern = Pattern.compile("^[-]?[\\d]*$");
        return pattern.matcher(str).matches();
    }


    /**
     * WATT
     *
     * @param weight
     * @param targetHR
     * @param rpm
     * @return WATT
     */
    public static double getWATT(int weight, int targetHR, int rpm) {
        return 0.1667 * weight * ((0.05 * targetHR) - (rpm * 0.1)) / 1.8;
    }

    /**
     * 在REACHING狀態，第60秒HR值HR60若不等於0，則做ROR1的計算若HR等於0則強制等待(HOLD在第60秒)HR不等於0時做ROR1的計算 。
     *
     * @param targetHR 目標心跳
     * @param HR0      Program開始後的第一次心跳
     * @param HR60     Program開始後第60秒的心跳
     * @return ROR1
     */
    public static int getROR1(int targetHR, int HR0, int HR60) {
        return (targetHR - HR0) - (targetHR - HR60);
    }

    /**
     * GOAL
     *
     * @param ROR      ROR1/ROR2
     * @param HrSec    Program開始後第 60/75秒的心跳
     * @param targetHR 目標心跳
     * @return GOAL
     */
    public static int getGOAL(int ROR, int HrSec, int targetHR) {
        return ROR * 3 + HrSec - targetHR;
    }


    /**
     * 在 REACHING 狀態，第75秒起含第75秒,若HR不等於0做ROR2的計算,若HR等於0則強制等待(HOLD在第75秒)HR不等於0做ROR2的計算 。
     *
     * @param HR75 Program開始後第75秒的心跳
     * @param HR60 Program開始後第60秒的心跳
     * @return ROR2
     */
    public static int getROR2(int HR75, int HR60) {
        return HR75 - HR60;
    }

    public static String convertPace(double paceS) {

        String pace = String.format(Locale.getDefault(), "%.0f", paceS);

        int tSec, cSec, cMin;
        tSec = Integer.parseInt(pace);
        cSec = tSec % 60;
        cMin = tSec / 60;
        String s;
        if (cMin < 10) {
            s = "0" + cMin;
        } else {
            s = "" + cMin;
        }
        if (cSec < 10) {
            s = s + ":0" + cSec;
        } else {
            s = s + ":" + cSec;
        }
        return s;
    }

    public static String formatSecToM(long sec) {
        long millisUntilFinished = sec * 1000;
        String mm = String.format(Locale.getDefault(), "%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished));
        String ss = String.format(Locale.getDefault(), "%02d", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

        return String.format("%s:%s", mm, ss);
    }

    public static String formatSec2H(long sec) {
        String time;
        if (sec >= 3600) {
            LocalTime timeOfDay = LocalTime.ofSecondOfDay(sec);
            time = timeOfDay.toString();
        } else {
            time = formatSecToM(sec);
        }
        return time;
    }

    /**
     * 保留小數，不四捨五入
     *
     * @param value 數值
     * @param keep  保留位數
     * @return string
     */
    public static String formatDecimal(double value, int keep) {
        final DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(keep);
        format.setGroupingSize(0);
        format.setRoundingMode(RoundingMode.FLOOR);
        return format.format(value);
    }

    /**
     * 保留兩位小數，四捨五入
     *
     * @param value 數值
     * @return string
     */
    public static String formatDouble(String value) {
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(Double.valueOf(value));
    }


    public Bitmap createQRCode(String qrUrl, int size, int padding) {
        Bitmap bitmapQR = null;
        try {
            Hashtable<EncodeHintType, String> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            BitMatrix matrix = new QRCodeWriter().encode(qrUrl,
                    BarcodeFormat.QR_CODE, size, size, hints);
            int width = matrix.getWidth();
            int height = matrix.getHeight();
            int[] pixels = new int[width * height];
            //
            boolean isFirstBlackPoint = false;
            int startX = 0;
            int startY = 0;
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    if (matrix.get(x, y)) {
                        if (!isFirstBlackPoint) {
                            isFirstBlackPoint = true;
                            startX = x;
                            startY = y;
                        }
                        pixels[y * size + x] = 0xff000000;
                    } else {
                        pixels[y * size + x] = 0xffffffff;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(size, size,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, size, 0, 0, size, size);

            // 剪切中间的二维码区域，减少padding区域
            if (startX <= padding) {
                return bitmap;
            }

            int x1 = startX - padding;
            int y1 = startY - padding;
            if (x1 < 0 || y1 < 0) {
                return bitmap;
            }

            int w1 = width - x1 * 2;
            int h1 = height - y1 * 2;

            bitmapQR = Bitmap.createBitmap(bitmap, x1, y1, w1, h1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmapQR;
    }

    public Bitmap generateQRCode(String QRCodeContent, Context context) {

        try {
            // QR code 的內容
            //QRCodeContent
            Bitmap bitmap;
            // QR code 寬度
            //   int QRCodeWidth = 185;
            int QRCodeWidth = 250;
            // QR code 高度
            //   int QRCodeHeight = 185;
            int QRCodeHeight = 250;
            // QR code 內容編碼
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);

            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            MultiFormatWriter writer = new MultiFormatWriter();
            // 容錯率姑且可以將它想像成解析度，分為 4 級：L(7%)，M(15%)，Q(25%)，H(30%)
            // 設定 QR code 容錯率為 H
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);

            // 建立 QR code 的資料矩陣
            BitMatrix result = writer.encode(QRCodeContent, BarcodeFormat.QR_CODE, QRCodeWidth, QRCodeHeight, hints);
            // ZXing 還可以生成其他形式條碼，如：BarcodeFormat.CODE_39、BarcodeFormat.CODE_93、BarcodeFormat.CODE_128、BarcodeFormat.EAN_8、BarcodeFormat.EAN_13...

            //建立點陣圖
            bitmap = Bitmap.createBitmap(QRCodeWidth, QRCodeHeight, Bitmap.Config.ARGB_8888);
            // 將 QR code 資料矩陣繪製到點陣圖上
            for (int y = 0; y < QRCodeHeight; y++) {
                for (int x = 0; x < QRCodeWidth; x++) {
                    bitmap.setPixel(x, y, result.get(x, y) ? Color.BLACK : ContextCompat.getColor(context, R.color.colorF6F8F9));
                }
            }
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean checkStr(String str) {
        return !"".equals(str) && str != null;
    }

    public static boolean checkInt(Integer i) {
        return i != null && i != 0;
    }


    private static final int MIN_DELAY_TIME = 1000;  // 兩次點選間隔不能少於1000ms
    private static long lastClickTime;

    /**
     * 判斷是否快速點選
     */
    public static boolean isFastClick() {
        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) >= MIN_DELAY_TIME) {
            flag = false;
        }
        lastClickTime = currentClickTime;
        return flag;
    }

    public static int findMaxInt(int[] array) {
        int max = 0;
        for (int value : array) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

//    public File readFileFromAssets(Context ctx) {
//        File f = new File(ctx.getCacheDir() + "/fff");
//        try {
//            InputStream inputStream = ctx.getAssets().open("fff.txt");
//
//            FileOutputStream fos = new FileOutputStream(f);
//            byte[] buffer = new byte[1024];
//            inputStream.read(buffer);
//            inputStream.close();
//            fos.close();
//            inputStream.close();
//
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//        return f;
//    }

    public String getLocalVersionName(Context ctx) {
        String localVersion = "";
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    public long getLocalVersionCode() {
        long localVersion = 0;
        try {
            PackageInfo packageInfo = getInstance()
                    .getPackageManager()
                    .getPackageInfo(getInstance().getPackageName(), 0);
            localVersion = packageInfo.getLongVersionCode();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }


//    // 需要点击几次 就设置几
//    private static long[] mHits = null;
//    private static final int COUNTS = 5;//点击次数
//    private static final long DURATION = TimeUnit.SECONDS.toMillis(3);//规定有效时间
//
//    public static boolean onSwitchMonitor() {
//        if (mHits == null) {
//            mHits = new long[COUNTS];
//        }
//        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);//把从第二位至最后一位之间的数字复制到第一位至倒数第一位
//        mHits[mHits.length - 1] = SystemClock.uptimeMillis();//记录一个时间
//        if (SystemClock.uptimeMillis() - mHits[0] <= DURATION) {//一秒内连续点击。
//            //进来以后需要还原状态，否则如果点击过快，第六次，第七次 都会不断进来触发该效果。重新开始计数即可
//            mHits = null;
//            return true;
//        }
//        return false;
//    }

    public static int setWifiImage(int level, boolean isDark) {
        int wifiImage = 0;
        switch (level) {
            case 0:
                wifiImage = isDark ? R.drawable.icon_wifi_inversion_lv3 : R.drawable.icon_wifi_lv3;
                break;
            case 1:
                wifiImage = isDark ? R.drawable.icon_wifi_inversion_lv2 : R.drawable.icon_wifi_lv2;
                break;
            case 2:
                wifiImage = isDark ? R.drawable.icon_wifi_inversion_lv1 : R.drawable.icon_wifi_lv1;
                break;
            case 3:
                wifiImage = isDark ? R.drawable.icon_wifi_inversion_lv4 : R.drawable.icon_wifi_lv4;
                break;
            case -1:
                wifiImage = isDark ? R.drawable.icon_wifi_inversion_none : R.drawable.icon_wifi_none;
                break;
        }
        return wifiImage;
    }

    /**
     * 判斷網絡是否連接
     *
     * @param context context
     * @return 判斷網絡是否連接
     */
    public static boolean isConnected(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        }

        @SuppressLint("MissingPermission")
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null;
    }

    public int getWifiLevel(Context context) {

        int signalLevel;
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getBSSID() != null) {
            signalLevel = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 4);
        } else {
            signalLevel = -1;
        }

        if (!isConnected(context)) signalLevel = -1;

        return signalLevel;
    }

    public String getSSID(Context context) {

        try {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String ssid = wifiInfo != null ? wifiInfo.getSSID() : "";
            return ssid.replace("\"", "").replace("<unknown ssid>", "");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

//            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//            if (wifiInfo != null) {
//                return wifiInfo.getSSID();
//            }
        //   return null;
    }

    public static void showToastAlert(String str, Activity activity) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_alert, activity.findViewById(R.id.toast_layout));
        TextView text = layout.findViewById(R.id.toastText);
        text.setText(str);
        Toast toast = new Toast(activity.getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0); //顯示位置
        toast.setDuration(Toast.LENGTH_SHORT); //顯示時間長短
        toast.setView(layout);
        toast.show();
    }

    RxTimer longTimer;

    @SuppressLint("ClickableViewAccessibility")
    public void addAutoClick(ImageButton button) {

        button.setOnLongClickListener(v -> {
            if (longTimer != null) longTimer.cancel();
            longTimer = new RxTimer();
            longTimer.interval3(200, n -> button.callOnClick());
            return true;
        });

        button.setOnTouchListener((v, event) -> {
            if (event.getAction() == ACTION_UP) {
                if (longTimer != null) longTimer.cancel();
            }
            return false;
        });
    }

    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CoreStar/Dyaco/Spirit/default_settings.json";

    public String getSettingFile(Context context) {
        try {
            File file = new File(path);
            FileInputStream fileInputStream = new FileInputStream(file);
            String fileData = new CommonUtils().readFromFileInputStream(fileInputStream);
            if (fileData.length() > 0) {
                return fileData;
            } else {
                Log.d("SETTING_FILE", "讀取設定檔資料失敗，設定檔內無資料");
                return createSettingFile();
            }
        } catch (FileNotFoundException ex) {
            Log.d("SETTING_FILE", "讀取設定檔資料 失敗: " + ex.getLocalizedMessage());
            return createSettingFile();
            // return null;
        }
    }

    public String readFromFileInputStream(FileInputStream fileInputStream) {
        StringBuilder retBuf = new StringBuilder();
        try {
            if (fileInputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String lineData = bufferedReader.readLine();
                while (lineData != null) {
                    retBuf.append(lineData);
                    lineData = bufferedReader.readLine();
                }
            }
        } catch (IOException ex) {
            Log.e("SETTING_FILE", "讀設定檔IOException:" + ex.getMessage(), ex);
        }
        return retBuf.toString();
    }

    public String createSettingFile() {
        try {
            File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/CoreStar/Dyaco/Spirit/");
            if (!root.exists()) {
                boolean b = root.mkdirs();
            }
            File file = new File(root, "default_settings.json");
            FileWriter writer = new FileWriter(file);
            ModeEnum modeEnum;
            if (getInstance().getDeviceSettingBean().getModel_name() != null) {
                modeEnum = ModeEnum.getMode(getInstance().getDeviceSettingBean().getModel_code());
                Log.d("SETTING_FILE", "裝置資料庫的機型為: " + getInstance().getDeviceSettingBean().getModel_name());
            } else {
                modeEnum = ModeEnum.XE395ENT;
                Log.d("SETTING_FILE", "裝置資料庫無資料，建立機型" + modeEnum);
            }

            new InitProduct(getInstance()).setProductDefault(modeEnum);
            String settingData = new Gson().toJson(getInstance().getDeviceSettingBean());
            writer.append(settingData);
            writer.flush();
            writer.close();
            Log.d("SETTING_FILE", "重新建立 " + modeEnum + " 設定檔 成功");
            return settingData;
        } catch (IOException e) {
            Log.d("SETTING_FILE", "重新建立設定檔失敗: " + e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static String getMacAddress() {
        String macAddress = "";
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iF = interfaces.nextElement();

                byte[] address = iF.getHardwareAddress();
                if (address == null || address.length == 0) {
                    continue;
                }

                StringBuilder buf = new StringBuilder();
                for (byte b : address) {
                    buf.append(String.format("%02X:", b));
                }
                if (buf.length() > 0) {
                    buf.deleteCharAt(buf.length() - 1);
                }
                String mac = buf.toString();
                //  Log.d("mac", "interfaceName="+iF.getName()+", mac="+mac);

                if (TextUtils.equals(iF.getName(), "wlan0")) {
                    return mac;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            return macAddress;
        }

        return macAddress;
    }

    public static void closePackage(Context context) {
        Log.d("CLOSE", "closePackage: ");
        try {
            ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(ACTIVITY_SERVICE);
            Method method = activityManager.getClass().getMethod("forceStopPackage", String.class);
            method.setAccessible(true);

            method.invoke(activityManager, "com.netflix.mediaclient");
            method.invoke(activityManager, "com.hulu.plus");
            method.invoke(activityManager, "com.abc.abcnews");
            method.invoke(activityManager, "com.zumobi.msnbc");
            method.invoke(activityManager, "com.cnn.mobile.android.phone");
            method.invoke(activityManager, "com.foxnews.android");
            method.invoke(activityManager, "com.android.settings");
            method.invoke(activityManager, "com.mediatek.factorymode");
            method.invoke(activityManager, "com.android.browser");
            method.invoke(activityManager, "com.redstone.ota.ui");
            //  method.invoke(activityManager, "com.android.launcher3");
        } catch (Exception e) {
            LogUtils.d("closePackage -> e:" + e.toString());
            e.printStackTrace();
        }
    }


    /**
     * F215U.Q0.V4.69.PBJ-V30.8839.lcm.10.1 > 30
     *
     * @return
     */
    public static int checkSwVersion() {
        int v;
        try {
            //F215U.Q0.V4.69.PBJ-V30.8839.lcm.10.1
            String sw_version = SysProp.get("ro.build.display.id", "ro.build.display.id");
            String[] x = sw_version.split("\\.");
            String o = x[4];
            MyApplication.SW_VERSION = Integer.parseInt(o.substring(5));
            v = Integer.parseInt(o.substring(5));
        } catch (Exception e) {
            e.printStackTrace();
            MyApplication.SW_VERSION = 0;
            v = 0;
        }
        //  Log.d("更新", "設備SwVersion: " + MyApplication.SW_VERSION);
        return v;
    }


    /**
     * v30 > 30
     * 去掉V
     *
     * @param sv
     * @return
     */
    public static int convertSwVersion(String sv) {

        int v = 0;
        try {
            v = Integer.parseInt(sv.substring(1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        MyApplication.NEW_SW_VERSION = v;
        //    Log.d("更新", "新SwVersion: " + MyApplication.NEW_SW_VERSION);
        return v;
    }


    public static int toCeilInt(double num) {
        int i = 0;
        try {
            i = (int) Math.ceil(num);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }


    public static void setBadger(Context context, ImageButton imageButton, int badgeCount) {
        BadgeFactory.create(context)
                .setTextColor(Color.WHITE)
                .setWidthAndHeight(25, 25)
                .setBadgeBackground(Color.RED)
                .setTextSize(10)
                .setBadgeGravity(Gravity.RIGHT | Gravity.TOP)
                .setMargin(0, 3, 3, 0)
                .setBadgeCount(badgeCount)
                .setShape(BadgeView.SHAPE_CIRCLE)
                .setSpace(10, 10)
                .bind(imageButton);
    }


}