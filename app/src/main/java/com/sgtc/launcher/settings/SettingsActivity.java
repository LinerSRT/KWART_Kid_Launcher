package com.sgtc.launcher.settings;

import android.content.Intent;
import android.provider.Settings;
import android.os.Bundle;
import android.view.KeyEvent;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sgtc.launcher.R;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    private List<SettingsItem> settingsItemList;
    private RecyclerView recyclerView;
    private SettingsAdapter settingsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        recyclerView = findViewById(R.id.settingsRecycler);
        recyclerView.setWillNotDraw(false);
        settingsItemList = new ArrayList<>();
        settingsItemList.add(
                new SettingsItem(
                        ContextCompat.getDrawable(this, R.drawable.wifi_settings),
                        "WiFi",
                        new Intent(Settings.ACTION_WIFI_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
        );
        settingsItemList.add(
                new SettingsItem(
                        ContextCompat.getDrawable(this, R.drawable.bluetooth_settings),
                        "Bluetooth",
                        new Intent(Settings.ACTION_BLUETOOTH_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
        );
        settingsItemList.add(
                new SettingsItem(
                        ContextCompat.getDrawable(this, R.drawable.sim_settings),
                        "Мобильная сеть",
                        new Intent(Settings.ACTION_WIRELESS_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
        );
        settingsItemList.add(
                new SettingsItem(
                        ContextCompat.getDrawable(this, R.drawable.date_settings),
                        "Дата и время",
                        new Intent(Settings.ACTION_DATE_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
        );
        settingsItemList.add(
                new SettingsItem(
                        ContextCompat.getDrawable(this, R.drawable.language_settings),
                        "Язык",
                        new Intent(Settings.ACTION_LOCALE_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
        );
        settingsItemList.add(
                new SettingsItem(
                        ContextCompat.getDrawable(this, R.drawable.info_settings),
                        "О часах",
                        new Intent(this, WatchInfoSettings.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
        );
        settingsAdapter = new SettingsAdapter(settingsItemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(settingsAdapter);
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_POWER) {
            finish();
        }
        return super.onKeyUp(keyCode, event);
    }
}