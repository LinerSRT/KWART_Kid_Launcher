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
    public static final List<String> protectedApps = new ArrayList<>();
    public static final List<String> internetApps = new ArrayList<>();
    public static final List<String> allApps = new ArrayList<>();

    public static void init(Context context) {
        iconSet.put("com.sgtc.wechat", R.drawable.chat);
        iconSet.put("com.sgtc.weather", R.drawable.weather);
        iconSet.put("com.android.settings", R.drawable.settings);
        iconSet.put("com.sgtc.stepcalculate", R.drawable.steps);
        iconSet.put("com.sg.gallery", R.drawable.gallery);
        iconSet.put("com.android.camera2", R.drawable.camera);
        iconSet.put("com.sgtc.contact", R.drawable.contacts);
        iconSet.put("com.android.dialer", R.drawable.contacts);
        iconSet.put("com.android.contacts", R.drawable.contacts);
        iconSet.put("com.example.sgtcappma", R.drawable.qr_code);
        iconSet.put("com.example.sgtczxing", R.drawable.qr_code);
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
        hiddenApps.add("com.android.dialer");
        hiddenApps.add("com.android.mms");
        hiddenApps.add("com.example.sgtcappma");
        hiddenApps.add("com.sgtc.introduce");
        hiddenApps.add("com.android.settings");
        hiddenApps.add("ru.yandex.androidkeyboard");
        hiddenApps.add(context.getPackageName());
        nameSet.put("com.sg.gallery", context.getString(R.string.gallery_string));
        nameSet.put("com.sgtc.sgclass", context.getString(R.string.schedule));
        nameSet.put("com.sgtc.contact", context.getString(R.string.contacts_se));
        nameSet.put("com.android.contacts", context.getString(R.string.contacts));
        nameSet.put("com.sg.vcall", context.getString(R.string.video_chat));
        nameSet.put("com.sprd.fileexplorer", context.getString(R.string.files));
        nameSet.put("com.sg.sgphone", context.getString(R.string.phone));
        nameSet.put("com.example.sgtczxing", context.getString(R.string.connect));
        protectedApps.add("com.example.freakmath");
        protectedApps.add("com.android.contacts");
        protectedApps.add("com.android.contacts");
        protectedApps.add("com.whatsapp");
        protectedApps.add("ru.yandex.searchplugin");
        internetApps.add("com.whatsapp");
        internetApps.add("ru.yandex.searchplugin");
        internetApps.add("com.sprd.fileexplorer");
        allApps.add("com.sgtc.wechat");
        allApps.add("com.sgtc.weather");
        allApps.add("com.android.settings");
        allApps.add("com.sgtc.stepcalculate");
        allApps.add("com.sg.gallery");
        allApps.add("com.android.camera2");
        allApps.add("com.sgtc.contact");
        allApps.add("com.android.dialer");
        allApps.add("com.example.sgtcappma");
        allApps.add("com.example.sgtczxing");
        allApps.add("com.sgtc.ppmf");
        allApps.add("com.tc3g.helpervoice");
        allApps.add("com.mediatek.filemanager");
        allApps.add("com.sprd.fileexplorer");
        allApps.add("com.example.freakmath");
        allApps.add("com.sgtc.sgclass");
        allApps.add("org.crazyit.link");
        allApps.add("com.sg.sgphone");
        allApps.add("com.booyue.watch_hht");
        allApps.add("zaojiao");
        allApps.add("com.sg.vcall");
        allApps.add("com.sgtc.sprd.friendwechat");
        allApps.add("com.whatsapp");
        allApps.add("ru.yandex.searchplugin");
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
