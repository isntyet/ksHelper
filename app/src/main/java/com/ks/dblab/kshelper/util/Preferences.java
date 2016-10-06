package com.ks.dblab.kshelper.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016-03-08.
 */
public class Preferences {

    protected SharedPreferences preferences;
    protected SharedPreferences.Editor editor;

    public Preferences(Context context) {
        this.preferences = context.getSharedPreferences("KsHelperData", context.MODE_PRIVATE);
        this.editor = this.preferences.edit();
    }

    public boolean getBoolean(String key) {
        return this.preferences.getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return this.preferences.getBoolean(key, defValue);
    }

    public void putBoolean(String key, boolean value) {
        this.editor.putBoolean(key, value);
        this.editor.commit();
    }

    public String getString(String key) {
        return this.preferences.getString(key, "");
    }

    public String getString(String key, String defValue) {
        return this.preferences.getString(key, defValue);
    }

    public void putString(String key, String value) {
        this.editor.putString(key, value);
        this.editor.commit();
    }

    public int getInt(String key) {
        return this.preferences.getInt(key, 0);
    }

    public int getInt(String key, int defValue) {
        return this.preferences.getInt(key, defValue);
    }

    public void putInt(String key, int value) {
        this.editor.putInt(key, value);
        this.editor.commit();
    }
    /////////////////////////////////////////////////////////

    public boolean isPushIdInsert() {
        return this.getBoolean("PUSH_STATE_INSERT", false);
    }

    public void setPushIdInsert(boolean value) {
        this.putBoolean("PUSH_STATE_INSERT", value);
    }

    public String getMainWebUrl() {
        return this.getString("MAIN_WEB_URL", "https://m.facebook.com/profile.php?id=1415423212062917");
    }

    public void setMainWebUrl(String value) {
        this.putString("MAIN_WEB_URL", value);
    }

    //////////////////////////////////////////////////////////////////////////////////////////

    public String getKakaoId() {
        return this.getString("KAKAO_ID", "");
    }

    public void setKakaoId(String value) {
        this.putString("KAKAO_ID", value);
    }

    public String getKakaoNickName() {
        return this.getString("KAKAO_NICKNAME", "");
    }

    public void setKakaoNickName(String value) {
        this.putString("KAKAO_NICKNAME", value);
    }

    public String getKakaoImage() {
        return this.getString("KAKAO_IMAGE", "");
    }

    public void setKakaoImage(String value) {
        this.putString("KAKAO_IMAGE", value);
    }


    //////////////////////////////////////////////////////////////////////////////////////////
    public String getSchedule(int i, int j) {
        return this.getString("SCHEDULE_" + i + "_" + j, "");
    }

    public void setSchedule(String value, int i, int j) {
        this.putString("SCHEDULE_" + i + "_" + j, value);
    }

    public int getScheduleColor(int i, int j) {
        return this.getInt("SCHEDULE_COLOR" + i + "_" + j, 0);
    }

    public void setScheduleColor(int value, int i, int j) {
        this.putInt("SCHEDULE_COLOR" + i + "_" + j, value);
    }

    //////////////////////////////////////////////////////////////////////////////////////////

    //푸쉬알림 받기 안받기 설정
    //true = on = 안받는다
    //false = off = 받는다
    public boolean isPushAlarm() {
        return this.getBoolean("PUSH_ALARM", false);
    }

    public void setPushAlarm(boolean value) {
        this.putBoolean("PUSH_ALARM", value);
    }

}