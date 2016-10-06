package com.ks.dblab.kshelper.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.SystemClock;
import android.telephony.TelephonyManager;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Administrator on 2016-03-13.
 */
public class SysUtill {

    public static String getPackageName(Context context) {
        return getPackageName(context, null);
    }

    public static String getPackageName(Context context, String apkName) {
        try {
            if (SysUtill.isNull(apkName)) {
                return context.getPackageName();
            } else {
                return context.getPackageManager().getPackageArchiveInfo(apkName, 0).packageName;
            }
        } catch (Exception e) {
            return "unknown";
        }
    }

    public static String getVersionName(Context context) {
        return getVersionName(context, null);
    }

    public static String getVersionName(Context context, String apkName) {
        try {
            if (SysUtill.isNull(apkName)) {
                return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            } else {
                return context.getPackageManager().getPackageArchiveInfo(apkName, 0).versionName;
            }
        } catch (Exception e) {
            return "unknown";
        }
    }

    public static String getVersionNameByPackageName(Context context, String packageName) {
        try {
            return context.getPackageManager().getPackageInfo(packageName, 0).versionName;
        } catch (Exception e) {
            return "unknown";
        }
    }

    public static int getVersionCode(Context context) {
        return getVersionCode(context, null);
    }

    public static int getVersionCode(Context context, String apkName) {
        try {
            if (SysUtill.isNull(apkName)) {
                return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
            } else {
                return context.getPackageManager().getPackageArchiveInfo(apkName, 0).versionCode;
            }
        } catch (Exception e) {
            return -1;
        }
    }

    public static int getVersionCodeByPackageName(Context context, String packageName) {
        try {
            return context.getPackageManager().getPackageInfo(packageName, 0).versionCode;
        } catch (Exception e) {
            return -1;
        }
    }

    public static String getSimOperatorName(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                return telephonyManager.getSimOperatorName();
            } else {
                return "unknown";
            }
        } catch (Exception e) {
            return "unknown";
        }
    }

    public static String getNetworkOperatorName(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                return telephonyManager.getNetworkOperatorName();
            } else {
                return "unknown";
            }
        } catch (Exception e) {
            return "unknown";
        }
    }

    public static String getNetworkType(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            switch (telephonyManager.getNetworkType()) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return "1xRTT";
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return "CDMA";
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return "EDGE";
                case 14:// TelephonyManager.NETWORK_TYPE_EHRPD:
                    return "eHRPD";
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return "EVDO rev. 0";
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return "EVDO rev. A";
                case 12:// TelephonyManager.NETWORK_TYPE_EVDO_B:
                    return "EVDO rev. B";
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return "GPRS";
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return "HSDPA";
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return "HSPA";
                case 15:// TelephonyManager.NETWORK_TYPE_HSPAP:
                    return "HSPA+";
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return "HSUPA";
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return "iDen";
                case 13:// TelephonyManager.NETWORK_TYPE_LTE:
                    return "LTE";
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return "UMTS";
                default:
                    return "unknown";
            }
        } else {
            return "unknown";
        }
    }

    public static String getLineNumber(Context context) {
        String lineNumber = "";

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if ((telephonyManager != null) && (telephonyManager.getLine1Number() != null)) {
            String line1Number = telephonyManager.getLine1Number();

            if (!SysUtill.isNull(line1Number)) {
                if (line1Number.startsWith("+82")) {
                    line1Number = "0" + line1Number.substring("+82".length());
                } else if (line1Number.startsWith("82")) {
                    line1Number = "0" + line1Number.substring("82".length());
                }
                lineNumber = line1Number;
            }
        }

        return lineNumber;
    }

    public static String getIMEI(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                return telephonyManager.getDeviceId();
            } else {
                return "unknown";
            }
        } catch (Exception e) {
            return "unknown";
        }
    }

    public static String getPhoneSerial() {
        try {
            return (String) Build.class.getField("SERIAL").get(null);
        } catch (Exception e) {
            return "unknown";
        }
    }

    @SuppressLint("DefaultLocale")
    public static String getPhoneUptime() {
        try {
            long millis = SystemClock.elapsedRealtime();
            return String.format("%d:%02d:%02d", (millis / (1000 * 60 * 60)), (millis / (1000 * 60)) % 60,
                    (millis / 1000) % 60);
        } catch (Exception e) {
            return "";
        }
    }

    public static String getSwVersion(Context context) {
        String version = "unknown";

        try {
            Class<?> systemProperties = context.getClassLoader().loadClass("android.os.SystemProperties");
            Class<?>[] types = new Class[1];
            types[0] = String.class;
            Object[] params = new Object[1];
            params[0] = new String("ro.unc.swversion");

            Method get = systemProperties.getMethod("get", types);
            version = (String) get.invoke(systemProperties, params);
            if (SysUtill.isNull(version)) {
                params[0] = new String("ro.custom.build.version");

                get = systemProperties.getMethod("get", types);
                version = (String) get.invoke(systemProperties, params);
            }
        } catch (Exception e) {
        }

        return version;
    }

    public static String getManufacturer() {
        String phoneModel = getPhoneModel();

        if ("DH-A101K".equalsIgnoreCase(phoneModel) || "DH-B201K".equalsIgnoreCase(phoneModel)) {
            return "unicair";
        } else {
            return android.os.Build.MANUFACTURER;
        }
    }

    public static String getAndroidVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    public static String getPhoneModel() {
        return android.os.Build.MODEL;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public static boolean isWifiOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo() != null) && cm.getActiveNetworkInfo().isConnectedOrConnecting()
                && cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
    }

    public static boolean isNull(String value) {
        return (value == null) || value.equals("");
    }

    public static String fmtAmt(long amt) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(amt);
    }

    public static String removeFmtAmt(String str) {
        return str.replaceAll("\\,", "");
    }

    public static String objToStr(Object value) {
        try {
            return value.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public static String boolToStr(boolean value) {
        try {
            return Boolean.toString(value);
        } catch (Exception e) {
            return "";
        }
    }

    public static boolean strToBool(String value) {
        try {
            return Boolean.parseBoolean(value);
        } catch (Exception e) {
            return false;
        }
    }

    public static String intToStr(int value) {
        try {
            return Integer.toString(value);
        } catch (Exception e) {
            return "";
        }
    }

    public static int strToInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }

    public static String longToStr(long value) {
        try {
            return Long.toString(value);
        } catch (Exception e) {
            return "";
        }
    }

    public static long strToLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            return 0;
        }
    }

    public static String floatToStr(float value) {
        try {
            return Float.toString(value);
        } catch (Exception e) {
            return "";
        }
    }

    public static float strToFloat(String value) {
        try {
            return Float.parseFloat(value);
        } catch (Exception e) {
            return 0;
        }
    }

    public static String doubleToStr(double value) {
        try {
            return Double.toString(value);
        } catch (Exception e) {
            return "";
        }
    }

    public static double strToDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return 0;
        }
    }

    public static String dttmToStr(Date dttm) {
        return dttmToStr(dttm, "yyyyMMddHHmmss");
    }

    public static String dttmToStr(Date dttm, String pattern) {
        SimpleDateFormat sdf1 = new SimpleDateFormat(pattern, Locale.KOREA);
        try {
            return sdf1.format(dttm);
        } catch (Exception e1) {
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
            try {
                return sdf1.format(sdf2.parse("19000101000000"));
            } catch (Exception e2) {
                return "";
            }
        }
    }

    public static String dttmToStr(String dttm) {
        return dttmToStr(dttm, "yyyyMMddHHmmss");
    }

    public static String dttmToStr(String dttm, String pattern) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA);
        SimpleDateFormat sdf2 = new SimpleDateFormat(pattern, Locale.KOREA);
        try {
            return sdf2.format(sdf1.parse(dttm));
        } catch (Exception e1) {
            try {
                return sdf2.format(sdf1.parse("19000101000000"));
            } catch (Exception e2) {
                return "";
            }
        }
    }

    public static Date strToDttm(String dttm) {
        return strToDttm(dttm, "yyyyMMddHHmmss");
    }

    public static Date strToDttm(String dttm, String pattern) {
        SimpleDateFormat sdf1 = new SimpleDateFormat(pattern, Locale.KOREA);
        try {
            return sdf1.parse(dttm);
        } catch (Exception e1) {
            try {
                return sdf1.parse("19000101000000");
            } catch (Exception e2) {
                return null;
            }
        }
    }

    public static String dateToStr(Date date) {
        return dateToStr(date, "yyyyMMdd");
    }

    public static String dateToStr(Date date, String pattern) {
        SimpleDateFormat sdf1 = new SimpleDateFormat(pattern, Locale.KOREA);
        try {
            return sdf1.format(date);
        } catch (Exception e1) {
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
            try {
                return sdf1.format(sdf2.parse("19000101"));
            } catch (Exception e2) {
                return "";
            }
        }
    }

    public static String dateToStr(String date) {
        return dateToStr(date, "yyyyMMdd");
    }

    public static String dateToStr(String date, String pattern) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        SimpleDateFormat sdf2 = new SimpleDateFormat(pattern, Locale.KOREA);
        try {
            return sdf2.format(sdf1.parse(date));
        } catch (Exception e1) {
            try {
                return sdf2.format(sdf1.parse("19000101"));
            } catch (Exception e2) {
                return "";
            }
        }
    }

    public static Date strToDate(String date) {
        return strToDate(date, "yyyyMMdd");
    }

    public static Date strToDate(String date, String pattern) {
        SimpleDateFormat sdf1 = new SimpleDateFormat(pattern, Locale.KOREA);
        try {
            return sdf1.parse(date);
        } catch (Exception e1) {
            try {
                return sdf1.parse("19000101");
            } catch (Exception e2) {
                return null;
            }
        }
    }

    public static Date getDate(int year, int month, int day) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day);

            return cal.getTime();
        } catch (Exception e) {
            return null;
        }
    }

    public static Date getDate(int year, int month, int day, int hour, int minute) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day);
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, minute);

            return cal.getTime();
        } catch (Exception e) {
            return null;
        }
    }

    public static String fmtPhoneNumber(String phoneNumber) {
        try {
            if ((phoneNumber == null) || (phoneNumber.length() < 8)) {
                return "";
            }

            String telNo1 = "", telNo2 = "", telNo3 = "";

            if (phoneNumber.startsWith("02")) {
                telNo1 = phoneNumber.substring(0, 2);
            } else if (phoneNumber.startsWith("0130") || (phoneNumber.length() == 8)) {
                telNo1 = phoneNumber.substring(0, 4);
            } else {
                telNo1 = phoneNumber.substring(0, 3);
            }

            telNo2 = phoneNumber.substring(telNo1.length(), phoneNumber.length() - 4);
            if (telNo2.length() > 0) {
                telNo2 = String.format("-%s", telNo2);
            }

            telNo3 = phoneNumber.substring(phoneNumber.length() - 4);

            return String.format("%s%s-%s", telNo1, telNo2, telNo3);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getCurrentYearMonthToStr() {
        String year = intToStr(new GregorianCalendar().get(GregorianCalendar.YEAR));
        String month = intToStr(new GregorianCalendar().get(GregorianCalendar.MONTH) + 1);
        if (month.length() < 2) {
            month = "0" + month;
        }

        return year + month;
    }

    public static String getCurrentMonthToStr() {
        String month = intToStr(new GregorianCalendar().get(GregorianCalendar.MONTH) + 1);

        return month;
    }

    public static String getCurrentDayToStr() {
        String dayOfMonth = intToStr(new GregorianCalendar().get(GregorianCalendar.DAY_OF_MONTH));

        return dayOfMonth;
    }

    //요일 가져오기
    public static String getDayKor(){
        Calendar cal = Calendar.getInstance();
        int cnt = cal.get(Calendar.DAY_OF_WEEK) - 1;
        String[] week = {"일", "월", "화", "수", "목", "금", "톸"};

        return week[cnt];
    }

    //drawable을 비트맵파일로
    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

}

