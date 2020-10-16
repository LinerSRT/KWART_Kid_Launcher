package com.sgtc.launcher.applications;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;

import androidx.core.content.ContextCompat;

import com.sgtc.launcher.Config;
import com.sgtc.launcher.LauncherApplication;
import com.sgtc.launcher.R;
import com.sgtc.launcher.settings.SettingsActivity;
import com.sgtc.launcher.sync.DownloadAppActivity;
import com.sgtc.launcher.util.Callback;
import com.sgtc.launcher.util.PM;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ApplicationLoader extends AsyncTask<Void, List<ApplicationModel>, List<ApplicationModel>> {
    private List<ApplicationModel> applicationList;
    private Callback<ApplicationModel> callback;
    private PackageManager packageManager;

    public ApplicationLoader(Callback<ApplicationModel> callback) {
        this.callback = callback;
        applicationList = new ArrayList<>();
        packageManager = LauncherApplication.getContext().getPackageManager();
        execute();
    }

    @Override
    protected List<ApplicationModel> doInBackground(Void... voids) {
        applicationList.add(
                new ApplicationModel(
                        ContextCompat.getDrawable(LauncherApplication.getContext(), R.drawable.settings),
                        new Intent(LauncherApplication.getContext(), SettingsActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                        LauncherApplication.getContext().getString(R.string.settings_title),
                        "com.kwart.customsettings",
                        false
                )
        );
        applicationList.add(
                new ApplicationModel(
                        ContextCompat.getDrawable(LauncherApplication.getContext(), R.drawable.qr_code2),
                        new Intent(LauncherApplication.getContext(), DownloadAppActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                        LauncherApplication.getContext().getString(R.string.assist),
                        "com.kwart.customsettings2",
                        false
                )
        );
        Intent query = new Intent(Intent.ACTION_MAIN);
        query.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(query, 0);
        for (ResolveInfo resolveInfo : resolveInfoList) {
            String pkg = resolveInfo.activityInfo.packageName;
            boolean showOtherApps = PM.get(Config.KEY_SHOW_OTHER_APPS, false);
            boolean showInternetApps = PM.get(Config.KEY_SHOW_INTERNET_APPS, true);
            if (!ApplicationUtil.hiddenApps.contains(pkg)) {
                if (!ApplicationUtil.allApps.contains(pkg) && showOtherApps) {
                    if (ApplicationUtil.internetApps.contains(pkg) && showInternetApps) {
                        addModel(pkg);
                    } else if (!ApplicationUtil.internetApps.contains(pkg)) {
                        addModel(pkg);
                    }
                } else if (ApplicationUtil.allApps.contains(pkg)) {
                    if (ApplicationUtil.internetApps.contains(pkg) && showInternetApps) {
                        addModel(pkg);
                    } else if (!ApplicationUtil.internetApps.contains(pkg)) {
                        addModel(pkg);
                    }
                }
            }
        }
        return applicationList;
    }

    private void addModel(String pkg) {
        ApplicationModel model = new ApplicationModel();
        model.intent = packageManager.getLaunchIntentForPackage(pkg);
        model.title = ApplicationUtil.getName(pkg);
        model.icon = ApplicationUtil.getIcon(pkg);
        model.packageName = pkg;
        model.requirePassword = (ApplicationUtil.protectedApps.contains(pkg));
        applicationList.add(model);
    }

    @Override
    protected void onPostExecute(List<ApplicationModel> applicationModels) {
        super.onPostExecute(applicationModels);
        Collections.sort(applicationList, new Comparator<ApplicationModel>() {
            Collator collator = Collator.getInstance(LauncherApplication.getContext().getResources().getConfiguration().locale);

            @Override
            public int compare(ApplicationModel o1, ApplicationModel o2) {
                return collator.compare(o1.title, o2.title);
            }
        });
        callback.onDataLoaded(applicationModels);
    }

}
