package com.sgtc.launcher;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;


import com.sgtc.launcher.ClockSkin.Model.ClockSkin;
import com.sgtc.launcher.applications.ApplicationUtil;
import com.sgtc.launcher.util.Broadcast;
import com.sgtc.launcher.util.NetCenter;
import com.sgtc.launcher.util.PM;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.sgtc.launcher.Config.CLOCK_ENGINE_ASSETS_FOLDER_NAME;
import static com.sgtc.launcher.Config.CLOCK_ENGINE_MANIFEST_FILE;
import static com.sgtc.launcher.Config.CLOCK_ENGINE_SUPPORT_TYPES;


public class LauncherApplication extends Application {
    private static final String TAG = "KWART_DEBUG";
    private static Context context;
    public static NetCenter netCenter;
    public static List<ClockSkin> clockSkinList;
    private Runnable mainThread;
    private Handler handler;
    private long updateInterval = TimeUnit.SECONDS.toMillis(15);
    private ContentObserver awardObserver;
    private Broadcast weatherBroadcast;

    @SuppressLint("ResourceType")
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        handler = new Handler();
        PM.init(this, "KWART");
        ApplicationUtil.init(this);
        netCenter = new NetCenter(this);
        clockSkinList = new ArrayList<>();
        clockSkinList.clear();
        searchClockSkinAsset();
        awardObserver = new ContentObserver(handler) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                startActivity(new Intent(context, AwardActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        };
        weatherBroadcast = new Broadcast(context, new String[]{"ACTION_WEATHER_UPDATED"}) {
            @Override
            public void handleChanged(Intent intent) {
                PM.put(Config.WEATHER_TEMP, intent.getStringExtra("temp"));
                PM.put(Config.WEATHER_ICON, intent.getStringExtra("type"));
            }
        };
        weatherBroadcast.setListening(true);
        getContentResolver().registerContentObserver(NetCenter.COMMAND_URI, false, awardObserver);
        mainThread = new Runnable() {
            @Override
            public void run() {
                PM.put(Config.CURRENT_STEPS, netCenter.getInt(NetCenter.STEPS_KEY, 0));


                if (Config.DEBUG) {
                    Log.d(TAG, "NetCenter: [key_qq_btn] {" + netCenter.getInt("key_qq_btn", 0) + "}");
                    Log.d(TAG, "NetCenter: [key_ht_btn] {" + netCenter.getInt("key_ht_btn", 0) + "}");
                    Log.d(TAG, "NetCenter: [key_lk_value] {" + netCenter.getInt("key_lk_value", 0) + "}");
                    Log.d(TAG, "NetCenter: [key_fall_sos] {" + netCenter.getInt("key_fall_sos", 0) + "}");
                    Log.d(TAG, "NetCenter: [key_server_host] {" + netCenter.getString("key_server_host", "0") + "}");
                    Log.d(TAG, "NetCenter: [key_debug_server_host] {" + netCenter.getString("key_debug_server_host", "0") + "}");
                    Log.d(TAG, "NetCenter: [key_total_turn_count_time] {" + netCenter.getString("key_total_turn_count_time", "0") + "}");
                    Log.d(TAG, "NetCenter: [key_total_count_time] {" + netCenter.getString("key_total_count_time", "0") + "}");
                    Log.d(TAG, "NetCenter: [key_app_url] {" + netCenter.getString("key_app_url", "0") + "}");
                    Log.d(TAG, "NetCenter: [key_curr_location] {" + netCenter.getString("key_curr_location", "0") + "}");
                    Log.d(TAG, "NetCenter: [key_yj_mode] {" + netCenter.getString("key_yj_mode", "0") + "}");
                    Log.d(TAG, "NetCenter: [key_cell_loc] {" + netCenter.getString("key_cell_loc", "0") + "}");
                    Log.d(TAG, "NetCenter: [key_server_port] {" + netCenter.getInt("key_server_port", 0) + "}");
                    Log.d(TAG, "NetCenter: [key_satellite_count] {" + netCenter.getInt("key_satellite_count", 0) + "}");
                    Log.d(TAG, "NetCenter: [key_low_battery] {" + netCenter.getBoolean("key_low_battery", false) + "}");
                    Log.d(TAG, "NetCenter: [key_lowbat_state] {" + netCenter.getBoolean("key_lowbat_state", false) + "}");
                    Log.d(TAG, "NetCenter: [key_smssend_switch] {" + netCenter.getBoolean("key_smssend_switch", false) + "}");
                    Log.d(TAG, "NetCenter: [key_avoid_disturb] {" + netCenter.getBoolean("key_avoid_disturb", false) + "}");
                    Log.d(TAG, "NetCenter: [key_set_profile] {" + netCenter.getInt("key_set_profile", 0) + "}");
                    Log.d(TAG, "NetCenter: [key_turn_count_] {" + netCenter.getInt("key_turn_count_" + new SimpleDateFormat("yyyyMMdd").format(new Date()), 0) + "}");
                    Log.d(TAG, "NetCenter: [key_walk_count_] {" + netCenter.getInt("key_walk_count_" + new SimpleDateFormat("yyyyMMdd").format(new Date()), 0) + "}");
                    Log.d(TAG, "NetCenter: [key_total_turn_count] {" + netCenter.getInt("key_total_turn_count", 0) + "}");
                    Log.d(TAG, "NetCenter: [key_total_count] {" + netCenter.getInt("key_total_count", 0) + "}");
                    Log.d(TAG, "NetCenter: [key_lk_value] {" + netCenter.getInt("key_lk_value", 0) + "}");
                    Log.d(TAG, "NetCenter: [key_curr_location] {" + netCenter.getString("key_curr_location", "0") + "}");
                    Log.d(TAG, "NetCenter: [key_yj_mode] {" + netCenter.getString("key_yj_mode", "0") + "}");
                    Log.d(TAG, "NetCenter: [key_fall_sos] {" + netCenter.getInt("key_fall_sos", 0) + "}");
                    Log.d(TAG, "NetCenter: [key_battery_level] {" + netCenter.getInt("key_battery_level", 0) + "}");
                    Log.d(TAG, "NetCenter: [key_gsm_signal] {" + netCenter.getInt("key_gsm_signal", 0) + "}");
                }
                handler.postDelayed(mainThread, updateInterval);
            }
        };
        handler.post(mainThread);
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        try {
            wallpaperManager.setResource(R.drawable.wallpaper);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        getContentResolver().unregisterContentObserver(awardObserver);
        weatherBroadcast.setListening(false);
    }

    public static Context getContext() {
        return context;
    }

    public static NetCenter getNetCenter() {
        return netCenter;
    }

    public static List<ClockSkin> getClockSkinList() {
        if (clockSkinList.isEmpty()) {
            clockSkinList.clear();
            searchClockSkinAsset();
        }
        Log.d("TAGTAG", "getClockSkinList: " + clockSkinList.size());
        return clockSkinList;
    }

    public static void searchClockSkinAsset() {
        try {
            String[] clockAssetList = getContext().getAssets().list(CLOCK_ENGINE_ASSETS_FOLDER_NAME);
            for (String item : Objects.requireNonNull(clockAssetList)) {
                String[] list = getContext().getAssets().list(CLOCK_ENGINE_ASSETS_FOLDER_NAME + File.separator + item);
                assert list != null;
                if (list.length > 0) {
                    for (String fileName : list) {
                        for (String type : CLOCK_ENGINE_SUPPORT_TYPES) {
                            if (fileName.equals(type)) {
                                clockSkinList.add(new ClockSkin.Builder()
                                        .clockPath(CLOCK_ENGINE_ASSETS_FOLDER_NAME + File.separator + item)
                                        .manifestPath(CLOCK_ENGINE_ASSETS_FOLDER_NAME + File.separator + item + File.separator + CLOCK_ENGINE_MANIFEST_FILE)
                                        .previewPath(CLOCK_ENGINE_ASSETS_FOLDER_NAME + File.separator + item + File.separator + type)
                                        .isExternalStorage(false)
                                        .isDownloadButton(false)
                                        .build());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
