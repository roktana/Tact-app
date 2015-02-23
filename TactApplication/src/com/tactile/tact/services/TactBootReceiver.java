package com.tactile.tact.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.tactile.tact.controllers.TactSharedPrefController;


/**
 * Created by leyan on 11/21/14.
 */
public class TactBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (TactSharedPrefController.isLoggedIn() && TactSharedPrefController.isInitialSynced()) {
            //After device finish boot wait 2 minutes and start the sync service,
            //the wait time is for the device start all the process it needs and not overload
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    Intent intentService = new Intent(context, TactCUDLSyncService.class);
                    context.startService(intentService);
                }
            }, 2000);
        }
    }

}
