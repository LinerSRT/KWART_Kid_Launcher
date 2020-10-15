package com.sgtc.launcher.settings;

import android.content.Intent;
import android.graphics.drawable.Drawable;

public class SettingsItem {
    public Drawable icon;
    public String title;
    public Intent action;

    public SettingsItem(Drawable icon, String title, Intent action) {
        this.icon = icon;
        this.title = title;
        this.action = action;
    }

    public SettingsItem() {
    }
}
