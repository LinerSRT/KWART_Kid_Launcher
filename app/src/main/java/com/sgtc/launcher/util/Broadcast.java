package com.sgtc.launcher.util;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public abstract class Broadcast extends BroadcastReceiver implements Listenable {
    private String[] actions;
    private Context context;

    public Broadcast(Context context, String[] actions) {
        this.actions = actions;
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        handleChanged(intent);
    }

    public abstract void handleChanged(Intent intent);

    @Override
    public void setListening(boolean listening) {
        if(listening){
            IntentFilter intentFilter = new IntentFilter();
            for(String action:actions)
                intentFilter.addAction(action);
            context.registerReceiver(this, intentFilter);
        } else {
            try {
                context.unregisterReceiver(this);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
