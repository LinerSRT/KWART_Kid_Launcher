package com.sgtc.launcher;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.sgtc.launcher.fragments.AppsFragment;
import com.sgtc.launcher.fragments.ControllFragment;
import com.sgtc.launcher.fragments.WatchfaceFragment;
import com.sgtc.launcher.util.Broadcast;
import com.sgtc.launcher.util.PM;
import com.sgtc.launcher.viewpager.FragmentAdapter;
import com.sgtc.launcher.viewpager.GateTransformation;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Launcher extends FragmentActivity {
    private ViewPager viewPager;
    private FragmentAdapter fragmentAdapter;
    private ArrayList<Class<? extends Fragment>> pages;
    private Broadcast screenReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        screenReceiver = new Broadcast(this, new String[]{
                Intent.ACTION_SCREEN_ON,
                Intent.ACTION_SCREEN_OFF
        }) {
            @Override
            public void handleChanged(Intent intent) {
                if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                    viewPager.setCurrentItem(1);
                }
                Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, (Integer) PM.get("screen_timeout", 5000));
            }
        };
        viewPager = findViewById(R.id.viewPager);
        pages = new ArrayList<>();
        pages.add(ControllFragment.class);
        pages.add(WatchfaceFragment.class);
        pages.add(AppsFragment.class);
        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), this, pages);
        viewPager.setAdapter(fragmentAdapter);
        viewPager.setCurrentItem(1);
        viewPager.setPageTransformer(false, new GateTransformation());
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == 1) {
                    sendBroadcast(new Intent(getPackageName() + ".SCROLL_APPS_TO_TOP"));
                    try {
                        Settings.Global.putInt(Launcher.this.getContentResolver(), "lock_invalid", 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Settings.Global.putInt(Launcher.this.getContentResolver(), "lock_invalid", 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        screenReceiver.setListening(true);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        screenReceiver.setListening(false);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LauncherApplication.restart();
    }

    @Override
    public void onBackPressed() {
        viewPager.setCurrentItem(1);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_POWER) {
            if (viewPager.getCurrentItem() == 1) {
                PM.put("screen_timeout", Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 60000));
                Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, (int) TimeUnit.SECONDS.toMillis(1));
            }
            viewPager.setCurrentItem(1);
        }
        return super.onKeyUp(keyCode, event);
    }
}