package com.moallem.stu.data;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsHelper {
    private static final String PREFERENCES_NAME = "app_prefs";
    private SharedPreferences mPrefs;
    private static PrefsHelper sInstance = null;

    private static final String KEY_showDialog = "key_showDialog";
    private boolean showDialog;

    private static final String KEY_counterToshowDialog = "key_counter";
    private int counterToshowDialog = 0;


    private static final String KEY_userType = "key_userType";
    private String userType;

    private static final String KEY_countrycode = "key_countrycode";
    private String countryCode;


    private PrefsHelper(Context context) {
        mPrefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static PrefsHelper getInstance(Context context) {
        if (sInstance == null) {
            synchronized (PrefsHelper.class) {
                if (sInstance == null) {
                    sInstance = new PrefsHelper(context);
                }
            }
        }
        return sInstance;
    }

    public void setShowDialog(boolean showDialog) {
        this.showDialog = showDialog;
        mPrefs.edit().putBoolean(KEY_showDialog, showDialog).apply();

    }

    public boolean getShowDialog() {
        showDialog = mPrefs.getBoolean(KEY_showDialog, true);
        return showDialog;
    }

    public void incremntCounterToshowDialog() {
        this.counterToshowDialog = getCounterToshowDialog()+1;
        mPrefs.edit().putInt(KEY_counterToshowDialog, counterToshowDialog).apply();

    }

    public void resetCounterToshowDialog() {
        this.counterToshowDialog = 0;
        mPrefs.edit().putInt(KEY_counterToshowDialog, counterToshowDialog).apply();

    }


    public int getCounterToshowDialog() {
        counterToshowDialog = mPrefs.getInt(KEY_counterToshowDialog,0);
        return counterToshowDialog;
    }


    public void setUserType(String userType) {
        this.userType = userType;
        mPrefs.edit().putString(KEY_userType,userType).apply();

    }

    public String getUserType() {
        userType = mPrefs.getString(KEY_userType,"none");
        return userType;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
        mPrefs.edit().putString(KEY_countrycode,countryCode).apply();

    }

    public String getCountryCode() {
        countryCode = mPrefs.getString(KEY_countrycode,"none");
        return countryCode;
    }


    public void destroy() {
        mPrefs.edit().clear().apply();
        userType = null;

    }

}
