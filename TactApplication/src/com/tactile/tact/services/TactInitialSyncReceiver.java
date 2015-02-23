package com.tactile.tact.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.tactile.tact.R;
import com.tactile.tact.activities.LoginSyncActivity;
import com.tactile.tact.activities.TactApplication;
import com.tactile.tact.consts.TactConst;
import com.tactile.tact.controllers.TactSharedPrefController;
import com.tactile.tact.services.events.EventCheckInitialDBError;
import com.tactile.tact.services.events.EventInitialSyncError;
import com.tactile.tact.services.events.EventInitialSyncFinished;

import de.greenrobot.event.EventBus;

/**
 * Created by leyan on 11/6/14.
 */
public class TactInitialSyncReceiver extends BroadcastReceiver {

    private Notification getForegroundNotification(Context context, String url) {

        Intent intentResult = new Intent(context, LoginSyncActivity.class);
        TactSharedPrefController.setSyncStatus(TactConst.SHARED_PREF_SYNC_STATUS_READY_SYNC);
        TactSharedPrefController.setInitialDownloadURL(url);
        intentResult.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intentResult.setAction(TactConst.NOTIFICATION_ACTION_SYNC);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentResult, 0);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context)
                .setContentTitle("Tact")
                .setTicker("Tact Synchronization Finished")
                .setContentText("Tact Synchronization Finished")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pendingIntent)
                .setOngoing(true);

        return notification.build();
    }



    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle == null) {
                //Error the intents always need to bring the data referred to activity status
                EventBus.getDefault().post(new EventInitialSyncError(true, 0, "Can't get the intent data"));
            } else {
                //Check for error
                EventCheckInitialDBError eventCheckInitialDBError = (EventCheckInitialDBError)bundle.getSerializable(TactConst.SERVICE_SYNC_ERROR);
                if (eventCheckInitialDBError != null) {
                    //Error from the service
                    EventBus.getDefault().post(new EventInitialSyncError(eventCheckInitialDBError.getError()));

                } else {
                    //get the download url
                    String url = bundle.getString(TactConst.SERVICE_SYNC_DOWNLOAD_DATABASE_URL);
                    if (url == null || url.isEmpty()) {
                        //Error the intents always need to bring the data referred to activity status
                        EventBus.getDefault().post(new EventInitialSyncError(true, 0, "Can't get the intent data"));
                    } else {
                        if (TactApplication.isActivityVisible()) {
                            TactSharedPrefController.setSyncStatus(TactConst.SHARED_PREF_SYNC_STATUS_READY_SYNC);
                            TactSharedPrefController.setInitialDownloadURL(url);
                            EventBus.getDefault().post(new EventInitialSyncFinished(url));
                        } else {
                            NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.notify(TactConst.NOTIFICATION_ID_INITIAL_SYNC_FINISHED, getForegroundNotification(context, url));
                        }
                    }
                }
            }
        }
    }
}
