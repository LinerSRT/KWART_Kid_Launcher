package com.sgtc.launcher.util;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Iterator;
import java.util.Set;

public class Intents {

    public static void dumpIntent(Intent i){
        Bundle bundle = i.getExtras();
        if (bundle != null) {
            Set<String> keys = bundle.keySet();
            Iterator<String> it = keys.iterator();
            Log.e("TAGTAG","Dumping Intent start");
            while (it.hasNext()) {
                String key = it.next();
                Log.e("TAGTAG","[" + key + "=" + bundle.get(key)+"]");
            }
            Log.e("TAGTAG","Dumping Intent end");
        }
    }
}
