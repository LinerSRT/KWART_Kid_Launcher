package com.sgtc.launcher;

import android.app.Activity;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sgtc.launcher.util.NetCenter;


public class AwardActivity extends Activity {
    private TextView awardCount;
    private ImageView awardExit;
    private NetCenter netCenter;

    protected final ContentObserver awardObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            updateAwards();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_award);
        netCenter = new NetCenter(this);
        awardCount = findViewById(R.id.awardCount);
        awardExit = findViewById(R.id.awardExit);
        awardExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        updateAwards();
        getContentResolver().registerContentObserver(NetCenter.COMMAND_URI, false, awardObserver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(awardObserver);
    }

    private void updateAwards(){
        awardCount.setText(String.valueOf(netCenter.getAwardCount()));
    }
}