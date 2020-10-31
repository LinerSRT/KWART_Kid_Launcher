package com.sgtc.launcher;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.sgtc.launcher.Config.CLOCK_ENGINE_ASSETS_FOLDER_NAME;
import static com.sgtc.launcher.Config.CLOCK_ENGINE_MANIFEST_FILE;
import static com.sgtc.launcher.Config.CLOCK_ENGINE_SUPPORT_TYPES;


public class LauncherApplication extends Application {
    private static Context context;
    public static NetCenter netCenter;
    public static List<ClockSkin> clockSkinList;
    private Runnable mainThread;
    private Handler handler;
    private long updateInterval = TimeUnit.SECONDS.toMillis(20);
    private ContentObserver awardObserver;
    private Broadcast weatherBroadcast;
    private Broadcast localeBroadcast;

    @SuppressLint("ResourceType")
    @Override
    public void onCreate() {
        super.onCreate();
        try {
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
            localeBroadcast = new Broadcast(context, new String[]{
                    Intent.ACTION_LOCALE_CHANGED
            }) {
                @Override
                public void handleChanged(Intent intent) {
                    restart();
                }
            };
            localeBroadcast.setListening(true);
            weatherBroadcast.setListening(true);
            getContentResolver().registerContentObserver(NetCenter.COMMAND_URI, false, awardObserver);
            mainThread = new Runnable() {
                @Override
                public void run() {
                    PM.put(Config.CURRENT_STEPS, netCenter.getInt(NetCenter.STEPS_KEY, 0));
                    handler.postDelayed(mainThread, updateInterval);
                }
            };
            handler.postDelayed(mainThread, 15000);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        getContentResolver().unregisterContentObserver(awardObserver);
        weatherBroadcast.setListening(false);
        localeBroadcast.setListening(false);
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

    public static void restart() {
        Intent mStartActivity = new Intent(context, Launcher.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        int id = 123456;
        PendingIntent pendingIntent = PendingIntent.getActivity(context, id, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 200, pendingIntent);
        System.exit(0);
    }
}
