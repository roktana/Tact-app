package com.tactile.tact.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;

/**
 * Created by admin on 8/13/14.
 */
public abstract class AppBaseBroadcast extends FragmentActivity {
    public static final String FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION = "com.tactile.tact.FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION";
    private BaseActivityReceiver baseActivityReceiver = new BaseActivityReceiver();
    public static final IntentFilter INTENT_FILTER = createIntentFilter();

    private static IntentFilter createIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION);
        return filter;
    }

    protected void registerBaseActivityReceiver() {
        registerReceiver(baseActivityReceiver, INTENT_FILTER);
    }

    protected void unRegisterBaseActivityReceiver() {
        unregisterReceiver(baseActivityReceiver);
    }

    public class BaseActivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION)) {
                finish();
            }
        }
    }

    protected void closeAllActivities() {
        sendBroadcast(new Intent(FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION));
    }

}
