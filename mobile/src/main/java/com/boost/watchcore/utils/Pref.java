package com.boost.watchcore.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.boost.watchcore.R;

/**
 * Created by BruSD on 28.04.2015.
 */
public class Pref {

    public static String PREFS_NAME;

    public static String START_LAST_UPDATE_TIMESTAMP;
    public static String END_LAST_UPDATE_TIMESTAMP;
    public static String IS_VOTED = "IS_VOTED";
    public static String ENABLE_ANALYTICS = "ENABLE_ANALYTICS";
    public static String RUN_COUNT;

    private static SharedPreferences settings;
    private static Context context = null;

    public static SharedPreferences getPref(Context _context) {

        context = _context;

        intPrefKeys();
        SharedPreferences settingsTemp = context.getSharedPreferences(PREFS_NAME, 0);
        return settingsTemp;
    }

    private static void intPrefKeys() {
        PREFS_NAME = context.getResources().getString(R.string.pref_name);

        START_LAST_UPDATE_TIMESTAMP = context.getResources().getString(R.string.start_last_update_timestamp);
        END_LAST_UPDATE_TIMESTAMP = context.getResources().getString(R.string.end_last_update_timestamp);
    }


    public static long getStartTimeStamp(Context context) {
        settings = getPref(context);
        return settings.getLong(START_LAST_UPDATE_TIMESTAMP, 0);
    }
    public static void setStartTimeStamp(Context context, long timestam) {
        settings = getPref(context);

        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(START_LAST_UPDATE_TIMESTAMP, timestam);
        // Commit the edits!
        editor.apply();
    }

    public static long getEndTimeStamp(Context context) {
        settings = getPref(context);
        return settings.getLong(END_LAST_UPDATE_TIMESTAMP, 0);
    }
    public static void setEndTimeStamp(Context context, long timestam) {
        settings = getPref(context);

        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(END_LAST_UPDATE_TIMESTAMP, timestam);
        // Commit the edits!
        editor.apply();
    }

    public static int getRunCount(Context context) {
        settings = getPref(context);
        return settings.getInt(RUN_COUNT, 1);
    }
    public static void setSetRunCount(Context context, int runed) {
        settings = getPref(context);

        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(RUN_COUNT, runed);
        // Commit the edits!
        editor.apply();
    }
    public static void setVoted(Context context) {
        settings = getPref(context);

        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(IS_VOTED, true);
        // Commit the edits!
        editor.apply();
    }
    public static boolean isVoted(Context context) {
        settings = getPref(context);
        return settings.getBoolean(IS_VOTED, false);
    }

    public static void setEnableAnalytics(Context context, boolean enable) {
        settings = getPref(context);

        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(ENABLE_ANALYTICS, enable);
        editor.apply();
    }

    public static boolean isEnableAnalytics(Context context) {
        settings = getPref(context);
        return settings.getBoolean(ENABLE_ANALYTICS, false);
    }
}
