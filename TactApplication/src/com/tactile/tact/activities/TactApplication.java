/*
 * Copyright (c) 2011, salesforce.com, inc.
 * All rights reserved.
 * Redistribution and use of this software in source and binary forms, with or
 * without modification, are permitted provided that the following conditions
 * are met:
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * - Neither the name of salesforce.com, inc. nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission of salesforce.com, inc.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.tactile.tact.activities;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;
import com.path.android.jobqueue.log.CustomLogger;
import com.tactile.tact.consts.TactConst;
import com.tactile.tact.database.dao.DaoMaster;
import com.tactile.tact.database.dao.DaoSession;
import com.tactile.tact.services.ContactChangesReceiver;
import com.tactile.tact.database.model.FragmentMoveHandler;
import com.tactile.tact.utils.LruBitmapCache;
import com.tactile.tact.utils.TactSecuredPreferences;

import java.io.File;
import java.util.ArrayList;

/**
 * Application class for our application.
 */
public class TactApplication extends Application {

    //TODO: THIS IS FOR DEVELOPING PUT YOUR ENVIROMENT HERE
    public static TactConst.URLs enviroment = TactConst.URLs.TactStagingEnvironment;

    //Tag for the Application class
    private static final String TAG = TactApplication.class.getSimpleName();
    //Database unique instances
    public static DaoMaster.DevOpenHelper dbHelper;
    public static SQLiteDatabase database;
    public static DaoMaster daoMaster;
    public static DaoSession daoSession;
    //Singelton
    private static TactApplication mInstance = null;
    //Secured shared preferences
    private static TactSecuredPreferences mSecuredPrefs;
    //Networking volley
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private JobManager jobManager;

    private String pusherKey = null;

    private ArrayList<FragmentMoveHandler> fragmentBackTack;

    //Singelton
    public static synchronized TactApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Singelton
        mInstance = this;
        //Database Instances
        dbHelper = new DaoMaster.DevOpenHelper(this, "feeditems", null);
        database = dbHelper.getWritableDatabase();
        daoMaster = new DaoMaster(database);
        daoSession = daoMaster.newSession();
        //Instance of secured shared pref
        mSecuredPrefs = new TactSecuredPreferences(this, TactConst.SECURE_PREF_KEY, false);

        getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true,new ContactChangesReceiver());

        fragmentBackTack = new ArrayList<FragmentMoveHandler>();

        configureJobManager();
    }

    private void configureJobManager() {
        Configuration configuration = new Configuration.Builder(this)
                .customLogger(new CustomLogger() {
                    private static final String TAG = "JOBS";

                    @Override
                    public boolean isDebugEnabled() {
                        return true;
                    }

                    @Override
                    public void d(String text, Object... args) {
                        Log.d(TAG, String.format(text, args));
                    }

                    @Override
                    public void e(Throwable t, String text, Object... args) {
                        Log.e(TAG, String.format(text, args), t);
                    }

                    @Override
                    public void e(String text, Object... args) {
                        Log.e(TAG, String.format(text, args));
                    }
                })
                .minConsumerCount(1)//always keep at least one consumer alive
                .maxConsumerCount(3)//up to 3 consumers at a time
                .loadFactor(3)//3 jobs per consumer
                .consumerKeepAlive(120)//wait 2 minute
                .build();
        jobManager = new JobManager(this.getApplicationContext(), configuration);
    }

    public JobManager getJobManager() {
        return jobManager;
    }

    public void setFragmentBackTack(FragmentMoveHandler fragmentMoveHandler) {
        fragmentBackTack.add(0, fragmentMoveHandler);
    }

    public FragmentMoveHandler getFromFragmentBackStack() {
        if (fragmentBackTack != null && fragmentBackTack.size() > 0) {
            FragmentMoveHandler fragmentMoveHandler = fragmentBackTack.get(0);
            fragmentBackTack.remove(0);
            return fragmentMoveHandler;
        }
        return null;
    }

    public void clearFragmentBackStack() {
        fragmentBackTack.clear();
    }

    public static String getDatabasePath(){
        File directory = new File(TactApplication.getAppFilesPath(), "databases");
        if (!directory.exists()){
            directory.mkdirs();
        }
        return TactApplication.getAppFilesPath() + "/databases";
    }

    public static String getAppFilesPath() {
        return TactApplication.getInstance().getFilesDir().getAbsolutePath();
    }

    /**
     * Access method to get the request queue instance of Volley
     * @return RequestQueue instance
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    /**
     * Access method to get the ImageLoader instance of Volley
     * @return ImageLoader instance
     */
    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    /**
     * Add a networking Volley request to the queue
     * @param req Request Volley
     * @param tag Tag to identify the task
     * @param <T> Request type
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    /**
     * Add a networking Volley request to the queue
     * @param req Request Volley
     */
    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    /**
     * Cancel a pending request by tag
     * @param tag - Tag to identify the request
     */
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public void setPusherKey(String channel) {
        this.pusherKey = channel;
    }

    public String getPusherKey() {
        return this.pusherKey;
    }


    /**
     * Access method to get the secured shared preferences instance
     * @return TactSecuredPreferences
     */
    public TactSecuredPreferences getSecuredPrefs() {
        return mSecuredPrefs;
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    private static boolean activityVisible;

    /**
     * Stop option_menu activity from executing scheduled task when not in foreground
     */
    public void onHomeActivityPaused() {
//        if (homeRefreshScheduler != null)
//            homeRefreshScheduler.shutdown();
//        homeRefreshScheduler = null;
//        if (hotSyncScheduler != null) {
//            hotSyncScheduler.shutdown();
//        }
//        hotSyncScheduler = null;
//        if (feedItemSyncScheduler != null) {
//            feedItemSyncScheduler.shutdown();
//        }
//        feedItemSyncScheduler = null;
//        if (syncScheduler != null){
//            syncScheduler.shutdown();
//        }
//        syncScheduler = null;
    }

//    /**
//     * Execute sync task
//     * @throws Exception
//     */
//    private void startSync() throws Exception {
//        HashMap<String, Integer> revision = DatabaseContentProvider.getSyncRevision(this.getContentResolver());
//        TactServerHTTPRequestOperation operation = new TactServerAPI(this).sync(revision.get("opportunities_revision"), revision.get("revision"));
//        Log.w("SYNC", "A sync requested with parameters: opp_revision:" + revision.get("opportunities_revision").toString() +
//                " - revision: " + revision.get("revision").toString());
//        operation.setOnSuccessMethod(new Invokable() {
//            @Override
//            public void run(APIResponse param) {
//
//            }
//        });
//        operation.setOnFailedMethod(new Invokable() {
//            @Override
//            public void run(APIResponse param) {
//                try{
//                    String jsonResult = ((ErrorResult) ((ServerErrorResponse) param).getResult()).getErrorList().get(0);
//                    JSONObject response = new JSONObject(jsonResult);
//                    if (response.get("error_title").equals("Source Added")) {
//                        Log.w("SYNC", "New db available to download");
//                    }
//                }
//                catch (Exception e){
//                    Log.w("Sync", "Error parsing the response");
//                }
//            }
//        });
//        operation.start();
//    }
//
//    /**
//     * Execute a hotSync task
//     * @throws Exception
//     */
//    private void startHotSync() throws Exception {
//        TactServerHTTPRequestOperation operation = new TactServerAPI(this).hotSync(TactDataSource.getHotSyncRequest());
//        Log.w("HOTSYNC", "A hotsync submited with body : " + TactDataSource.getHotSyncRequest());
//        operation.setOnSuccessMethod(new Invokable() {
//            @Override
//            public void run(APIResponse param) {
//                HotSyncResponse response = (HotSyncResponse) param;
////                TactApplication.getInstance().getSyncHandler().cudlObjectSync(response., TactConst.SYNC_TYPE_HOTSYNC);
//            }
//        });
//        operation.setOnFailedMethod(new Invokable() {
//            @Override
//            public void run(APIResponse param) {
//                Log.w("HOTSYNC", "Calling hotSync failed with : " + param);
//            }
//        });
//        operation.start();
//    }
//
//    /**
//     * Execute a feedItemSync task
//     * @throws Exception
//     */
//    private void startFeedItemSync(ArrayList<ZFROZENFEEDITEM> items) throws Exception {
//        if (items != null && items.size() > 0) {
//            TactServerHTTPRequestOperation operation = new TactServerAPI(this).feedItemSync(TactDataSource.getFeedItemsSyncRequest(items));
//            Log.w("FISYNC", "A feedItemSync submited with body : " + TactDataSource.getFeedItemsSyncRequest(items));
//            operation.setOnSuccessMethod(new Invokable() {
//                @Override
//                public void run(APIResponse param) {
//                    String response = ((APITextResponse)param).responseText;
////                    TactApplication.getInstance().getSyncHandler().cudlObjectSync(response, TactConst.SYNC_TYPE_FISYNC);
//                }
//            });
//            operation.setOnFailedMethod(new Invokable() {
//                @Override
//                public void run(APIResponse param) {
//                    Log.w("FISYNC", "Calling feedItemSync failed with : " + param);
//                }
//            });
//            operation.start();
//        }
//    }

    /**
     * Start a schedule for needed tasks when option_menu activity is in foreground
     * @param homeActivity - HomeActivity instance
     */
    public void onHomeActivityResumed(final HomeActivity homeActivity) {
//        homeRefreshScheduler = Executors.newSingleThreadScheduledExecutor();
//        homeRefreshScheduler.scheduleWithFixedDelay
//                (new Runnable() {
//                    public void run() {
//                        try {
//                            homeActivity.refreshVisibleFragment();
//                        } catch (Exception e) {
//                            Log.w("HOME", "Exception in refreshFragment with message: " + e.getMessage());
//                        }
//                    }
//                }, 0, 10, TimeUnit.SECONDS);
//
//        hotSyncScheduler = Executors.newSingleThreadScheduledExecutor();
//        hotSyncScheduler.scheduleWithFixedDelay
//                (new Runnable() {
//                    public void run() {
//                        try {
//                            startHotSync();
//                        } catch (Exception e) {
//                            Log.w("HOME", "Exception in hotsync with message: " + e.getMessage());
//                        }
//                    }
//                }, 10, 30, TimeUnit.SECONDS);
//
//        //TODO: CHANGE FEEDITEMS SYNC TO 3 MINUTES, 1 NOW FOR TEST PROPOUSE
//        feedItemSyncScheduler = Executors.newSingleThreadScheduledExecutor();
//        feedItemSyncScheduler.scheduleWithFixedDelay
//                (new Runnable() {
//                    public void run() {
//                        try {
//                            startFeedItemSync(TactDataSource.getItemsNeedSync());
//                        } catch (Exception e) {
//                            Log.w("HOME", "Exception in feedItemSync with message: " + e.getMessage());
//                        }
//                    }
//                }, 1, 1, TimeUnit.MINUTES);
//        syncScheduler = Executors.newSingleThreadScheduledExecutor();
//        syncScheduler.scheduleWithFixedDelay
//                (new Runnable() {
//                    public void run() {
//                        try {
//                            startSync();
//                        } catch (Exception e) {
//                            Log.w("HOME", "Exception in Sync with message: " + e.getMessage());
//                        }
//                    }
//                }, 1, 1, TimeUnit.MINUTES);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Toast.makeText(this, "Out of memory", Toast.LENGTH_SHORT).show();
    }

    public static void unbindDrawables(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
        System.gc();
    }
}
