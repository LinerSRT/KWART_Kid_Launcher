package com.sgtc.launcher.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sgtc.launcher.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DEBUGFragment extends Fragment {
    private Button devSettings;
    private Button recent;
    private Button expandSB;
    private Button debug_reboot;
    private Button debug_poweroff;
    private Button debug_clear;
    private Button test;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.debug_fragment, container, false);
        devSettings = view.findViewById(R.id.debug_dev_settings);
        recent = view.findViewById(R.id.debug_recent);
        expandSB = view.findViewById(R.id.debug_expand_sb);
        debug_reboot = view.findViewById(R.id.debug_reboot);
        debug_poweroff = view.findViewById(R.id.debug_poweroff);
        debug_clear = view.findViewById(R.id.debug_clear);
        test = view.findViewById(R.id.debug_test);


        devSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
            }
        });
        recent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Class serviceManagerClass = Class.forName("android.os.ServiceManager");
                    Method getService = serviceManagerClass.getMethod("getService", String.class);
                    IBinder retbinder = (IBinder) getService.invoke(serviceManagerClass, "statusbar");
                    Class statusBarClass = Class.forName(retbinder.getInterfaceDescriptor());
                    Object statusBarObject = statusBarClass.getClasses()[0].getMethod("asInterface", IBinder.class).invoke(null, new Object[]{retbinder});
                    Method clearAll = statusBarClass.getMethod("toggleRecentApps");
                    clearAll.setAccessible(true);
                    clearAll.invoke(statusBarObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        expandSB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    }
                }, 6000);
            }
        });
        debug_reboot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().sendBroadcast(new Intent("android.intent.action.sgtc_reboot"));
            }
        });
        debug_poweroff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().sendBroadcast(new Intent("android.intent.action.sgtc_shutdown"));
            }
        });
        debug_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().sendBroadcast(new Intent("android.intent.action.sgtc_recoverysystem"));
            }
        });
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(getActivity().getPackageManager().getLaunchIntentForPackage(" com.sprd.fileexplorer"));
            }
        });
        return view;
    }

}
