package com.tactile.tact.activities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.tactile.tact.R;
import com.tactile.tact.consts.TactConst;
import com.tactile.tact.controllers.TactSharedPrefController;
import com.tactile.tact.database.TactDataSource;
import com.tactile.tact.services.TactInitialSyncService;
import com.tactile.tact.services.events.EventGoHome;
import com.tactile.tact.services.events.EventInitialSyncError;
import com.tactile.tact.services.events.EventInitialSyncFinished;
import com.tactile.tact.services.events.EventLogout;
import com.tactile.tact.services.events.EventNoInternetSyncService;
import com.tactile.tact.services.events.EventUnZipError;
import com.tactile.tact.services.events.EventUnZipSuccess;
import com.tactile.tact.utils.TactDialogHandler;
import com.tactile.tact.utils.Utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.greenrobot.event.EventBus;

/**
 * Created by Sebastian Muñoz on 8/8/14.
 * Manage all initial synchronization when the User login to App
 */
public class LoginSyncActivity extends TactBaseActivity {

    public enum CurrentStatus{SyncingDatabase, DownloadingDatabase}
    private FrameLayout syncing;
    private LinearLayout downloading;
    private ProgressBar progressBar;

    TactDialogHandler dialogHandler;

    private AsyncDownload downloadTask;

    RelativeLayout mainLayout;

    //*********************  UTILITIES *********************\\

    /**
     * Sets the references & actions for the View elements
     */
    private void setVisualElements(CurrentStatus status){

        this.syncing     = (FrameLayout) findViewById(R.id.syncing_linear_layout);
        this.downloading = (LinearLayout) findViewById(R.id.downloading);
        this.progressBar = (ProgressBar) findViewById(R.id.progress);

        this.syncing.setVisibility(View.GONE);
        this.downloading.setVisibility(View.GONE);

        switch (status){
            case SyncingDatabase:
                this.syncing.setVisibility(View.VISIBLE);
                break;


            case DownloadingDatabase:
                this.downloading.setVisibility(View.VISIBLE);
                break;
        }

    }

    //*********************  OVERRIDES METHODS  *********************\\

    private boolean isStarting = true;

    /**
     * Method that start the processing of the intent to handel the way activity will behave
     * @param intent
     */
    private void handleNewIntent(Intent intent) {
        if (TactSharedPrefController.isInitialReadySync()) {
            //Ready to download the database
            if (TactSharedPrefController.isInitialDownloadURL()) {
                startDownload(TactSharedPrefController.getInitialDownloadURL());
            } else {
                //Error without the URL can't download the database
                EventBus.getDefault().post(new EventInitialSyncError(true, 0, "Can't get the database download URL"));
            }
        } else {
            //We are starting the sync
            this.setVisualElements(CurrentStatus.SyncingDatabase);
            Intent intentService = new Intent(this, TactInitialSyncService.class);
            startService(intentService);
        }
    }

    private void startDownload(String url) {
            this.setVisualElements(CurrentStatus.DownloadingDatabase);
            try {
                NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancelAll();
            } catch (Exception e) {
                //Do nothing, no notification
            }
        if (!isDownloading()) {
            downloadTask = new AsyncDownload(LoginSyncActivity.this);
            downloadTask.execute(url);
        } else {
            downloadTask.setActivity(this);
        }
    }

    public void onEventMainThread(EventInitialSyncFinished eventInitialSyncFinished) {
        if (eventInitialSyncFinished != null && eventInitialSyncFinished.getUrl() != null && !eventInitialSyncFinished.getUrl().isEmpty()) {
            startDownload(eventInitialSyncFinished.getUrl());
        } else {
            //Error without the URL can't download the database
            EventBus.getDefault().post(new EventInitialSyncError(true, 0, "Can't get the database download URL"));
        }
    }

    public void onEventMainThread(EventInitialSyncError eventInitialSyncError) {
        if (eventInitialSyncError != null && eventInitialSyncError.getError() != null && eventInitialSyncError.getError().getMessage() != null) {
            Log.w("SYNC", eventInitialSyncError.getError().getMessage());
        }
        else {
            Log.w("SYNC", "Error on initial sync");
        }
        dialogHandler.showConfirmation("Synchronization error. Do you want to retry or cancel. If you cancel the progress will be loosed", "Retry", "Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TactSharedPrefController.setInitialDownloadURL("");
                TactSharedPrefController.setNoSynced();
                handleNewIntent(new Intent());
                dialogHandler.dismiss();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new EventLogout());
            }
        });
    }

    public void onEvent(EventUnZipError eventUnZipError) {
        EventBus.getDefault().post(new EventInitialSyncError(true, 0, "Error downloading the database with message: " + eventUnZipError.error_msg));
    }

    public void onEvent(EventUnZipSuccess eventUnZipSuccess) {
        TactSharedPrefController.setSyncStatus(TactConst.SHARED_PREF_SYNC_STATUS_SYNCED);
        TactSharedPrefController.cleanOnboardingSharedPreferences();
        //go to Home
        EventBus.getDefault().post(new EventGoHome());

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                finish();
            }
        }, 2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.login_sync);

        mainLayout  = (RelativeLayout)findViewById(R.id.sync_data_main);
        mainLayout.setBackground(new BitmapDrawable(Utils.decodeSampledBitmapFromResource(this.getResources(), R.drawable.splash, Utils.getDisplaySize(this)[0], Utils.getDisplaySize(this)[0])));

//        ImageView downloadingImg = (ImageView)findViewById(R.id.landing_img);
//        downloadingImg.setImageBitmap(Utils.decodeSampledBitmapFromResource(this.getResources(), R.drawable.downloading_data, Utils.getDisplaySize(this)[0], Utils.getDisplaySize(this)[0]));

        dialogHandler = new TactDialogHandler(this);

        handleNewIntent(this.getIntent());

    }

    //this method is called when a notification call the activity and it's already created
    //as the activity is marked in the manifest as launchMode=singleTask it will not create a new instance
    //but will call this method with the intent that call the activity
    @Override
    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
        handleNewIntent(intent);
    }

    /**
     * If the user press Back button, this should close app
     */
    @Override
    public void onBackPressed(){
        Log.w("SYNC", "Cancel sync request by user");
        dialogHandler.showConfirmation("Do you want to cancel the synchronization process?. If you cancel the progress will be loosed", "Continue", "Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w("SYNC", "Sync process cancelled by the user");
                stopService(new Intent(LoginSyncActivity.this, TactInitialSyncService.class));
                EventBus.getDefault().post(new EventLogout());
            }
        }, dialogHandler.mDismissClickListener);
    }

    private boolean isDownloading() {
        return downloadTask != null && (downloadTask.getStatus().equals(AsyncTask.Status.RUNNING) || downloadTask.getStatus().equals(AsyncTask.Status.PENDING));
    }

    @Override
    public void onResume(){
        super.onResume();
        if (TactSharedPrefController.isInitialReadySync()) {
            //Ready to download the database
            if (TactSharedPrefController.isInitialDownloadURL()) {
                startDownload(TactSharedPrefController.getInitialDownloadURL());
            } else {
                //Error without the URL can't download the database
                EventBus.getDefault().post(new EventInitialSyncError(true, 0, "Can't get the database download URL"));
            }
        }
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onStart(){super.onStart();}

    @Override
    public void onStop(){super.onStop();}

    public void onAsyncDownloadCompleted(String _response){
        try {
            if (_response != null){
                EventBus.getDefault().post(new EventInitialSyncError(true, 0, "Error downloading the database with message: " + _response));
            } else {
                //now unzip file
                Utils.unZip(TactApplication.getDatabasePath(), TactConst.INITIAL_DB_FILENAME, TactApplication.getDatabasePath(), true);
                //DELETE ALL DATA FROM FEEDITEMS DATABASE
                TactDataSource.clearAllDatabaseData();
                TactDataSource.getInitialActivities();
            }
        } catch (Exception e) {
            EventBus.getDefault().post(new EventInitialSyncError(true, 0, "Error downloading the database with message: " + _response));
        }

    }



    public void onEventMainThread(EventNoInternetSyncService eventNoInternetSyncService){
        EventBus.getDefault().removeStickyEvent(eventNoInternetSyncService);

        if (!dialogHandler.isShowing()) {
            dialogHandler.showConfirmation(getResources().getString(R.string.sync_error), getResources().getString(R.string.message_title_info),
                    getResources().getString(R.string.retry), getResources().getString(R.string.cancel),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Retry pressed
                            stopService(new Intent(LoginSyncActivity.this, TactInitialSyncService.class));
                            handleNewIntent(new Intent());
                            dialogHandler.dismiss();
                        }
                    },
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Cancel pressed
                            stopService(new Intent(LoginSyncActivity.this, TactInitialSyncService.class));
                            EventBus.getDefault().post(new EventLogout());
                            dialogHandler.dismiss();
                        }
                    });
        }
    }


    public class AsyncDownload extends AsyncTask<String, Integer, String> {
        // Maintain attached activity for states change propose
        private LoginSyncActivity activity;
        // Keep the response
        private String _response;
        // Flag that keep async task completed status
        private boolean completed;

        private PowerManager.WakeLock mWakeLock;

        // Constructor
        private AsyncDownload(LoginSyncActivity activity) {
            this.activity = activity;
        }

        // Pre execution actions
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager)activity.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
            mWakeLock.acquire();
        }

        protected String doInBackground(String... sUrl) {
            InputStream input            = null;
            OutputStream output          = null;
            HttpURLConnection connection = null;
            try {
                URL url     = new URL(sUrl[0]);
                connection  = (HttpURLConnection) url.openConnection();
                connection.connect();

                //expect HTTP 200 OK, so we don't mistakenly save error report instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(TactApplication.getDatabasePath() + "/" + TactConst.INITIAL_DB_FILENAME);

                byte data[] = new byte[4096];
                long total = 0;
                int count;

                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;

                    // publishing the progress....
                    if (fileLength > 0) { // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    }
                    output.write(data, 0, count);
                }
            }

            catch (Exception e) {
                return e.toString();
            }

            finally {
                try {
                    if (output != null) output.close();
                    if (input != null) input.close();
                }
                catch (IOException ignored){}

                if (connection != null) connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            progressBar.setIndeterminate(false);
            progressBar.setMax(100);
            progressBar.setProgress(progress[0]);
        }

        // Post execution actions
        @Override
        protected void onPostExecute(String response) {
            mWakeLock.release();
            // Set task completed and notify the activity
            completed = true;
            _response = response;
            notifyActivityTaskCompleted();
        }

        // Notify activity of async task complete
        private void notifyActivityTaskCompleted() {
            if (null != activity) {
                activity.onAsyncDownloadCompleted(_response);
            }
        }

        // Sets the current activity to the async task
        public void setActivity(LoginSyncActivity activity) {
            this.activity = activity;
            if (completed) {
                notifyActivityTaskCompleted();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        syncing = null;
        downloading = null;
        progressBar = null;
        dialogHandler = null;
        downloadTask = null;
        TactApplication.unbindDrawables(mainLayout);
    }
}
