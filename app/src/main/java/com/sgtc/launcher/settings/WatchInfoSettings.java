package com.sgtc.launcher.settings;

import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sgtc.launcher.R;
import com.sgtc.launcher.util.SystemProperties;


public class WatchInfoSettings extends AppCompatActivity {
    private TextView model;
    private TextView imei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_info_settings);
        model = findViewById(R.id.watchModel);
        imei = findViewById(R.id.watchIMEI);
        model.setText(SystemProperties.get(this, "ro.product.manufacturer")+" "+SystemProperties.get(this, "ro.product.device"));
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        imei.setText(telephonyManager.getDeviceId());
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_POWER) {
           finish();
        }
        return super.onKeyUp(keyCode, event);
    }
}