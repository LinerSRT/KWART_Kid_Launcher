package com.sgtc.launcher.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NetCenter {
    private SharedPreferences sharedPreferences;
    private Context context;
    public static final String STEPS_KEY = "key_walk_count_" + new SimpleDateFormat("yyyyMMdd").format(new Date());
    public static final Uri COMMAND_URI = Uri.parse("content://netcenter/command");
    public static final Uri WEATHER_URI = Uri.parse("content://weather/lockscreen");


    public NetCenter(Context context) {
        this.context = context;
        try {
            this.sharedPreferences = context.createPackageContext("com.sgtc.netcenter", Context.CONTEXT_IGNORE_SECURITY).getSharedPreferences("net_center_app_lib", Context.MODE_MULTI_PROCESS);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            this.sharedPreferences = null;
        }
    }

    public int getInt(String key, int defValue) {
        if (sharedPreferences == null)
            return defValue;
        return sharedPreferences.getInt(key, defValue);
    }

    public String getString(String key, String defValue) {
        if (sharedPreferences == null)
            return defValue;
        return sharedPreferences.getString(key, defValue);
    }

    public boolean getBoolean(String key, boolean defValue) {
        if (sharedPreferences == null)
            return defValue;
        return sharedPreferences.getBoolean(key, defValue);
    }

    public float getFloat(String key, float defValue) {
        if (sharedPreferences == null)
            return defValue;
        return sharedPreferences.getFloat(key, defValue);
    }

    public int getAwardCount(){
        int count = 0;
        Cursor cursor = context.getContentResolver().query(NetCenter.COMMAND_URI, new String[]{"cmd_content"}, "cmd_key = ?", new String[]{"FLOWER"}, null);
        if(cursor != null && cursor.moveToFirst()){
            count = cursor.getInt(cursor.getColumnIndex("cmd_content"));
            if(count < 0)
                count = 0;
            if(!cursor.isFirst())
                cursor.close();
        }
        return count;
    }

    public int getWeatherTemp(){
        int count = 0;
        Cursor cursor = context.getContentResolver().query(NetCenter.WEATHER_URI, null, null, null, null);
        if(cursor != null && cursor.moveToFirst()) {
            count = Integer.parseInt(cursor.getString(cursor.getColumnIndex("temp")));
            if(!cursor.isFirst())
                cursor.close();
        }
        return count;
    }

}
