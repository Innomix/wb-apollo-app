package me.fmtech.apollo.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefUtil {

    private static final String PREFERENCES = "PREF";

    public static void put(Context ctx, String key, String value) {
        SharedPreferences sh = ctx.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sh.edit();

        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(Context ctx, String key) {
        SharedPreferences sh = ctx.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        return sh.getString(key, "");
    }

    public static void put(Context ctx, String key, boolean value) {
        SharedPreferences sh = ctx.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sh.edit();

        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBoolean(Context ctx, String key) {
        SharedPreferences sh = ctx.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        return sh.getBoolean(key, false);
    }

    public static void put(Context ctx, String key, int value) {
        SharedPreferences sh = ctx.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sh.edit();

        editor.putInt(key, value);
        editor.apply();
    }

    public static int getInt(Context ctx, String key) {
        SharedPreferences sh = ctx.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        return sh.getInt(key, 0);
    }

    public static void put(Context ctx, String key, long value) {
        SharedPreferences sh = ctx.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sh.edit();

        editor.putLong(key, value);
        editor.apply();
    }

    public static long getLong(Context ctx, String key) {
        SharedPreferences sh = ctx.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        return sh.getLong(key, 0);
    }
}
