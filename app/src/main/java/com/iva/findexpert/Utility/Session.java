package com.iva.findexpert.Utility;

import android.content.Context;
import android.content.SharedPreferences;

import com.iva.findexpert.Common.Constant;

/**
 * Created by LENOVO on 10/23/2016.
 */

public class Session {

    public static int getInt(Context context, String tag)
    {
        SharedPreferences pref = context.getSharedPreferences(Constant.APP_KEY, 0);
        return pref.getInt(tag, 0);
    }

    public static void putInt(Context context, String tag, int value)
    {
        SharedPreferences pref = context.getSharedPreferences(Constant.APP_KEY, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(tag, value);
        editor.commit();
    }

    public static long getLong(Context context, String tag)
    {
        SharedPreferences pref = context.getSharedPreferences(Constant.APP_KEY, 0);
        return pref.getLong(tag, 0);
    }

    public static void putLong(Context context, String tag, long value)
    {
        SharedPreferences pref = context.getSharedPreferences(Constant.APP_KEY, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(tag, value);
        editor.commit();
    }

    public static String getString(Context context, String tag)
    {
        SharedPreferences pref = context.getSharedPreferences(Constant.APP_KEY, 0);
        return pref.getString(tag, "");
    }

    public static void putString(Context context, String tag, String value)
    {
        SharedPreferences pref = context.getSharedPreferences(Constant.APP_KEY, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(tag, value);
        editor.commit();
    }

    public static void remove(Context context,String tag)
    {
        SharedPreferences pref = context.getSharedPreferences(Constant.APP_KEY, 0);
        if(pref.contains(tag))
        {
            SharedPreferences.Editor editor = pref.edit();
            editor.remove(tag);
            editor.commit();
        }
    }

}
