package com.sgtc.launcher.applications;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.sgtc.launcher.LauncherApplication;
import com.sgtc.launcher.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ApplicationUtil {
    public static final HashMap<String, Integer> iconSet = new HashMap<>();
    public static final HashMap<String, String> nameSet = new HashMap<>();
    public static final List<String> hiddenApps = new ArrayList<>();

    public static void init(Context context) {
        iconSet.put("com.sgtc.wechat", R.drawable.chat);
        iconSet.put("com.sgtc.weather", R.drawable.weather);
        iconSet.put("com.android.settings", R.drawable.settings);
        iconSet.put("com.sgtc.stepcalculate", R.drawable.steps);
        iconSet.put("com.sg.gallery", R.drawable.gallery);
        iconSet.put("com.android.camera2", R.drawable.camera);
        iconSet.put("com.sgtc.contact", R.drawable.contacts);
        iconSet.put("com.android.dialer", R.drawable.contacts);
        iconSet.put("com.example.sgtcappma", R.drawable.qr_code);
        iconSet.put("com.example.sgtczxing", R.drawable.qr_code2);
        iconSet.put("com.sgtc.ppmf", R.drawable.friends);
        iconSet.put("com.tc3g.helpervoice", R.drawable.voice);
        iconSet.put("com.mediatek.filemanager", R.drawable.filemanager);
        iconSet.put("com.sprd.fileexplorer", R.drawable.filemanager);
        iconSet.put("com.example.freakmath", R.drawable.math);
        iconSet.put("com.sgtc.sgclass", R.drawable.school);
        iconSet.put("org.crazyit.link", R.drawable.link);
        iconSet.put("com.sg.sgphone", R.drawable.dialer);
        iconSet.put("com.booyue.watch_hht", R.drawable.music);
        iconSet.put("zaojiao", R.drawable.education);
        iconSet.put("com.sg.vcall", R.drawable.videochat);
        iconSet.put("com.sgtc.sprd.friendwechat", R.drawable.chat);
        hiddenApps.add("com.android.deskclock");
        //hiddenApps.add("com.android.contacts");
        hiddenApps.add("com.android.dialer");
        hiddenApps.add("com.android.mms");
        hiddenApps.add("com.example.sgtcappma");
        hiddenApps.add("com.example.sgtczxing");
        hiddenApps.add("com.sgtc.introduce");
        hiddenApps.add("com.android.settings");
        hiddenApps.add(context.getPackageName());
        nameSet.put("com.sg.gallery", context.getString(R.string.gallery_string));
        nameSet.put("com.sgtc.sgclass", "Расписание");
        nameSet.put("com.android.contacts", "Контакты");
        nameSet.put("com.sg.vcall", "Видео-чат");
        nameSet.put("com.sprd.fileexplorer", "Файлы");
        nameSet.put("com.sg.sgphone", "Телефон");
    }


    public static Drawable getIcon(String packageName) {
        if (iconSet.containsKey(packageName)) {
            return ContextCompat.getDrawable(LauncherApplication.getContext(), iconSet.get(packageName));
        }
        try {
            return LauncherApplication.getContext().getPackageManager().getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return ContextCompat.getDrawable(LauncherApplication.getContext(), R.drawable.ic_baseline_warning_24);
        }
    }

    public static String getName(String packageName) {
        if (nameSet.containsKey(packageName)) {
            return nameSet.get(packageName);
        }
        try {
            return (String) LauncherApplication.getContext().getPackageManager().getApplicationLabel(LauncherApplication.getContext().getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "Null";
        }
    }
}
