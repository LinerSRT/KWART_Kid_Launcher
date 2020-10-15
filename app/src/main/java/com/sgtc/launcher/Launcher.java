package com.sgtc.launcher;

import android.content.Intent;
import android.content.res.Configuration;
import android.provider.Settings;
import android.os.Bundle;
import android.view.KeyEvent;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.sgtc.launcher.fragments.AppsFragment;
import com.sgtc.launcher.fragments.DEBUGFragment;
import com.sgtc.launcher.fragments.WatchfaceFragment;
import com.sgtc.launcher.viewpager.FragmentAdapter;
import com.sgtc.launcher.viewpager.GateTransformation;

import java.util.ArrayList;

public class Launcher extends FragmentActivity {
    private ViewPager viewPager;
    private FragmentAdapter fragmentAdapter;
    private ArrayList<Class<? extends Fragment>> pages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.viewPager);
        pages = new ArrayList<>();
        if (Config.DEBUG)
            pages.add(DEBUGFragment.class);
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
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Settings.Global.putInt(Launcher.this.getContentResolver(), "lock_invalid", 1);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        recreate();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_POWER) {
            if(viewPager.getCurrentItem() == 1){

            }
            viewPager.setCurrentItem(1);
        }
        return super.onKeyUp(keyCode, event);
    }
}