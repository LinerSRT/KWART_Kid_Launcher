package com.sgtc.launcher.ClockSkin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sgtc.launcher.Config;
import com.sgtc.launcher.LauncherApplication;
import com.sgtc.launcher.R;
import com.sgtc.launcher.util.PM;


public class ClockEngineChooseSkin extends AppCompatActivity {
    public static ClockEnginePager clockChooseRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_choose_clock_skin);
        clockChooseRecyclerView = findViewById(R.id.clock_skin_choose_recyleview);
        clockChooseRecyclerView.initLayoutManager(LinearLayoutManager.HORIZONTAL, false);
        clockChooseRecyclerView.setFlingVelocity(1);
        //ClockSkinChooseAdapter clockSkinChooseAdapter = new ClockSkinChooseAdapter(this, LFileManager.getInstance(this).getClockSkinList(false), this);
        ClockSkinChooseAdapter clockSkinChooseAdapter = new ClockSkinChooseAdapter(this, LauncherApplication.getClockSkinList(), this);
        clockChooseRecyclerView.setInitPosition((Integer) PM.get(Config.WATCHFACE_SELECTED_INDEX, 0));
        clockChooseRecyclerView.setAdapter(clockSkinChooseAdapter);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LauncherApplication.getContext().getPackageName()+".SELECT_LAST_USED_WATCHFACE");
        sendBroadcast(intent);
        super.onBackPressed();
    }
}
