package com.sgtc.launcher.util;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;

public abstract class SystemSettings extends ContentObserver implements Listenable {
    private Context context;
    private String settingsKey;
    private Object defaultValue;

    public SystemSettings(Context context, String settingsKey, Object defaultValue) {
        super(new Handler());
        this.context = context;
        this.settingsKey = settingsKey;
        this.defaultValue = defaultValue;
    }

    public Object get(){
        if(defaultValue instanceof String){
            return Settings.System.getString(context.getContentResolver(), settingsKey);
        } else if( defaultValue instanceof Integer){
            return Settings.System.getInt(context.getContentResolver(), settingsKey, (Integer) defaultValue);
        } else if( defaultValue instanceof Float){
            return Settings.System.getFloat(context.getContentResolver(), settingsKey, (Float) defaultValue);
        } else if( defaultValue instanceof Long){
            return Settings.System.getLong(context.getContentResolver(), settingsKey, (Long) defaultValue);
        } else if( defaultValue instanceof Uri){
            return Settings.System.getString(context.getContentResolver(), settingsKey);
        }
        return defaultValue;
    }

    public boolean set(Object value){
        try {
            if(value instanceof String){
                Settings.System.putString(context.getContentResolver(), settingsKey, (String) value);
            } else if( value instanceof Integer){
                Settings.System.putInt(context.getContentResolver(), settingsKey, (Integer) value);
            } else if( value instanceof Float){
                Settings.System.putFloat(context.getContentResolver(), settingsKey, (Float) value);
            } else if( value instanceof Long){
                Settings.System.putLong(context.getContentResolver(), settingsKey, (Long) value);
            }
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void setListening(boolean listening) {
        if(listening)
            context.getContentResolver().registerContentObserver(Settings.System.getUriFor(settingsKey), false, this);
        else {
            context.getContentResolver().unregisterContentObserver(this);
        }
    }

    public abstract void handleChanged(Object value);

    @Override
    public void onChange(boolean selfChange) {
        handleChanged(get());
    }
}
