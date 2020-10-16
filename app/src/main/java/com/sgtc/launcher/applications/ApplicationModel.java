package com.sgtc.launcher.applications;

import android.content.Intent;
import android.graphics.drawable.Drawable;

public class ApplicationModel {
    public Drawable icon;
    public Intent intent;
    public String title;
    public String packageName;
    public boolean requirePassword;

    public ApplicationModel(Drawable icon, Intent intent, String title, String packageName, boolean requirePassword) {
        this.icon = icon;
        this.intent = intent;
        this.title = title;
        this.packageName = packageName;
        this.requirePassword = requirePassword;
    }

    public ApplicationModel() {
    }
}
