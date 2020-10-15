package com.sgtc.launcher.applications;

import android.content.Intent;
import android.graphics.drawable.Drawable;

public class ApplicationModel {
    public Drawable icon;
    public Intent intent;
    public String title;
    public String packageName;

    public ApplicationModel(Drawable icon, Intent intent, String title, String packageName) {
        this.icon = icon;
        this.intent = intent;
        this.title = title;
        this.packageName = packageName;
    }

    public ApplicationModel() {
    }
}
