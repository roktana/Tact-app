package com.tactile.tact.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import com.tactile.tact.consts.TactConst;
import com.tactile.tact.services.events.EventCheckInitialDBError;
import com.tactile.tact.services.events.EventCheckInitialDBSuccess;
import com.tactile.tact.services.events.EventSourcesAddedError;
import com.tactile.tact.services.events.EventSourcesAddedSuccess;
import com.tactile.tact.services.network.TactNetworking;
import com.tactile.tact.utils.Log;

import de.greenrobot.event.EventBus;

/**
 * Created by leyan on 11/5/14.
 */
public class TactInitialSyncService extends Service {

    private final IBinder mBinder = new MyBinder();

    //Try 3 times to make the first check before returning error
    private final int maxFirstCheckIntents = 3;
    //Kind of timeout for the sync process, 100k calls should be sufficient for the server to sync the data
    private final int maxCyclicCheckIntents = 100000;
    //try 3 times if non critical error in calling the server
    private final int maxCyclicErrorCheckIntents = 3;
    //Make 2 intents to call the sources added web service before returning error
    private final int maxSourcesAddedIntents = 2;

    //Counters for the retry services calls, THE RETRY DEPEND OF THE ERROR RETURNED!!!
    private int firstCheckNumberOfIntents = 0;
    private int sourcesAddedNumberOfIntents = 0;
    private int cyclicCheckNumberOfIntents = 0;
    private int cyclicErrorCheckNumberOfIntents = 0;

    public static final String NOTIFICATION = "com.tactile.tact.services.sync.initial";

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * Capture the success message in calling CheckInitialDB service
     * @param eventCheckInitialDBSuccess - Success event object
     */
    public void onEvent(EventCheckInitialDBSuccess eventCheckInitialDBSuccess) {
        EventBus.getDefault().removeStickyEvent(eventCheckInitialDBSuccess);
        if (cyclicCheckNumberOfIntents <= maxCyclicCheckIntents) {
            if (eventCheckInitialDBSuccess != null) {
                if (eventCheckInitialDBSuccess.isJustCheck()) {
                    //First call to CheckDB finish correctly, call to SourcesAdded then
                    TactNetworking.callSourcesAdded(this);
                } else {
                    if (eventCheckInitialDBSuccess.getObjectResponse().isInitialDBReadyToDownload()) {
                        //Get the download URL
                        RedirectURLGetter urlGetter = new RedirectURLGetter();
                        urlGetter.execute();
                    } else {
                        cyclicCheckNumberOfIntents++;
                        postDelayedCheckDatabaseStatus(false);
                    }
                }
            } else {
                EventBus.getDefault().postSticky(new EventCheckInitialDBError(eventCheckInitialDBSuccess.isJustCheck(), true, 0, "The success event not returning values."));
            }
        } else {
            EventBus.getDefault().postSticky(new EventCheckInitialDBError(eventCheckInitialDBSuccess.isJustCheck(), true, 0, "The call to check database is taking too much, handle as a timeout"));
        }
    }

    /**
     * Capture the error message in calling the CheckInitialDB service
     * @param eventCheckInitialDBError - Error event object
     */
    public void onEvent(EventCheckInitialDBError eventCheckInitialDBError) {
        EventBus.getDefault().removeStickyEvent(eventCheckInitialDBError);
        //if the error object is null can't continue with processing so throw a critical error to stop
        if (eventCheckInitialDBError != null && eventCheckInitialDBError.getError() != null && eventCheckInitialDBError.getError().getMessage() != null) {
            Log.w("EVENT_CHECKDB_ERROR", eventCheckInitialDBError.getError().getMessage());
            //If the error is marked as critical the execution is finished
            if (eventCheckInitialDBError.getError().isCritical()) {
                sendResponse(null, eventCheckInitialDBError);
            } else {
                //If we are making the first call to teh service that is just a check call
                if (eventCheckInitialDBError.isJustCheck()) {
                    //If the retry intents is reached trigger a critical error to finish the execution
                    if (firstCheckNumberOfIntents > maxFirstCheckIntents) {
                        EventBus.getDefault().post(new EventCheckInitialDBError(eventCheckInitialDBError.isJustCheck(), true, 0, "The first call to checkDatabase reach its max try count with error."));
                    } else {
                        firstCheckNumberOfIntents++;
                        postDelayedCheckDatabaseStatus(true);
                    }
                } else {
                    //If the retry intents is reached trigger a critical error to finish the execution
                    if (firstCheckNumberOfIntents > maxFirstCheckIntents) {
                        EventBus.getDefault().post(new EventCheckInitialDBError(eventCheckInitialDBError.isJustCheck(), true, 0, "The cyclic call to checkDatabase reach its max try count with error."));
                    } else {
                        cyclicErrorCheckNumberOfIntents++;
                        postDelayedCheckDatabaseStatus(false);
                    }
                }
            }

        } else {
            EventBus.getDefault().post(new EventCheckInitialDBError(eventCheckInitialDBError.isJustCheck(), true, 0, "The success event not returning values."));
        }
    }

    /**
     * Capture the success event in calling SourcesAdded service
     * @param eventSourcesAddedSuccess - Success event object
     */
    public void onEvent(EventSourcesAddedSuccess eventSourcesAddedSuccess) {
        postDelayedCheckDatabaseStatus(false);
    }

    /**
     * Capture the error message in calling the SourcesAdded service
     * @param eventSourcesAddedError - Error event object
     */
    public void onEvent(EventSourcesAddedError eventSourcesAddedError) {
        //If there was an error ask if the retry reach the max if not try again.
        if (sourcesAddedNumberOfIntents > maxSourcesAddedIntents) {
            EventBus.getDefault().postSticky(new EventCheckInitialDBError(false, true, 0, "The SourcesAdded call return error even after retry."));
        } else {
            sourcesAddedNumberOfIntents++;
            TactNetworking.callSourcesAdded(this);
        }
    }

    /**
     * Make the Check Database Status Call delayed
     * @param justCheck
     */
    private void postDelayedCheckDatabaseStatus(final Boolean justCheck){
        final Context context = this;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                TactNetworking.callCheckDatabaseStatus(justCheck, context);
            }

        }, 5000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        postDelayedCheckDatabaseStatus(true);
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    public class MyBinder extends Binder {
        TactInitialSyncService getService() {
            return TactInitialSyncService.this;
        }
    }

    private void sendResponse(String downloadURL, EventCheckInitialDBError eventCheckInitialDBError) {
        Intent intent = new Intent(NOTIFICATION);
        Bundle bundle = new Bundle();
        bundle.putString(TactConst.SERVICE_SYNC_DOWNLOAD_DATABASE_URL, downloadURL);
        bundle.putSerializable(TactConst.SERVICE_SYNC_ERROR, eventCheckInitialDBError);
        intent.putExtras(bundle);
        sendBroadcast(intent);

        this.stopSelf();
    }

    public class RedirectURLGetter extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            return TactNetworking.getDownloadDatabaseURL();
        }

        @Override
        protected void onPostExecute(String s) {
            if (s == null || s.isEmpty()) {
                EventBus.getDefault().postSticky(new EventCheckInitialDBError(true, true, 0, "Unable to get the database download URL."));
            } else {
                sendResponse(s, null);
            }
        }
    }

}
