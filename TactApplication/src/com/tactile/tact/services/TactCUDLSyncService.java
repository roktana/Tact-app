package com.tactile.tact.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PrivateChannelEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import com.pusher.client.util.HttpAuthorizer;
import com.tactile.tact.activities.TactApplication;
import com.tactile.tact.consts.TactConst;
import com.tactile.tact.controllers.TactFeedItemsSyncHandler;
import com.tactile.tact.controllers.TactSharedPrefController;
import com.tactile.tact.controllers.TactSyncHandler;
import com.tactile.tact.database.DatabaseManager;
import com.tactile.tact.database.TactDataSource;
import com.tactile.tact.database.entities.Contact;
import com.tactile.tact.database.entities.FrozenFeedItem;
import com.tactile.tact.database.entities.GlobalSyncStatus;
import com.tactile.tact.database.model.ContactField;
import com.tactile.tact.database.model.FeedItemEvent;
import com.tactile.tact.services.events.EventCUDLSyncError;
import com.tactile.tact.services.events.EventConfigError;
import com.tactile.tact.services.events.EventConfigSuccess;
import com.tactile.tact.services.events.EventContactSyncError;
import com.tactile.tact.services.events.EventContactSyncSuccess;
import com.tactile.tact.services.events.EventFeedItemSyncSuccess;
import com.tactile.tact.services.events.EventNotifyCalendarChanges;
import com.tactile.tact.services.events.EventSyncCUDLProcessPackage;
import com.tactile.tact.services.events.EventSyncCUDLProcessPackageError;
import com.tactile.tact.services.events.EventSyncError;
import com.tactile.tact.services.events.EventSyncSuccess;
import com.tactile.tact.services.jobs.ProcessCUDLSyncPackageJob;
import com.tactile.tact.services.jobs.ProcessContactSyncPackageJob;
import com.tactile.tact.services.network.TactNetworking;
import com.tactile.tact.utils.CalendarAPI;
import com.tactile.tact.utils.Log;
import com.tactile.tact.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;

/**
 * Created by leyan on 11/17/14.
 * Service for CUDL sync
 */
public class TactCUDLSyncService extends Service {

    private Pusher pusher;
    private static final ScheduledExecutorService connectionAttemptsWorker = Executors.newSingleThreadScheduledExecutor();
    private int failedConnectionAttempts = 0;
    private int failedSubscribeAttempts = 0;
    private int failedSyncAttempts = 0;
    private static int MAX_RETRIES = 10;
    private Channel suscribedChannel = null;

    private AsyncStartPusher asyncStartPusher;

    private String pusherAPIKey;

    ScheduledExecutorService syncFeedItemsScheduler;
    ScheduledExecutorService syncScheduler;

    private volatile boolean isSyncing = false;
    private volatile boolean isContactSyncing = false;
    private ArrayList<String> pusherPackages;


    @Override
    public void onCreate() {
        super.onCreate();
        //register events for the service
        EventBus.getDefault().register(this);
        syncFeedItemsScheduler = Executors.newSingleThreadScheduledExecutor();
        syncScheduler = Executors.newSingleThreadScheduledExecutor();
        pusherPackages = new ArrayList<>();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //unregister event for the service
        EventBus.getDefault().unregister(this);
        syncFeedItemsScheduler.shutdown();
        syncFeedItemsScheduler = null;
        syncScheduler.shutdown();
        syncScheduler = null;
        System.gc();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //start connecting the pusher
        checkPusherConnectivity();

        return Service.START_STICKY;

    }

    /**
     * Event that handle a success response from Config web service
     * @param eventConfigSuccess - Event object
     */
    public void onEvent(EventConfigSuccess eventConfigSuccess) {
        this.asyncStartPusher = null;
        //if the call bring the pusher key start the pusher
        if (eventConfigSuccess.getPusherAPIKey() != null && !eventConfigSuccess.getPusherAPIKey().isEmpty()) {
            this.pusherAPIKey = eventConfigSuccess.getPusherAPIKey();
            startPusher();
        } else {
            //if there was an error try again from scratch
            checkPusherConnectivity();
        }
    }

    /**
     * Event triggered after a sync web service call succeed
     * @param eventSyncSuccess - Event object
     */
    public void onEvent(EventSyncSuccess eventSyncSuccess) {
        if (eventSyncSuccess != null &&
            eventSyncSuccess.getSyncResponse() != null &&
            eventSyncSuccess.getSyncResponse().getCallID() != null &&
            !eventSyncSuccess.getSyncResponse().getCallID().isEmpty()) {
            if (!isContactSyncing) {
                this.currentCallId = eventSyncSuccess.getSyncResponse().getCallID();
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        Log.w("SYNC", "There was no contact sync running, starting a new one");
                        startContactSync();
                    }
                };
                thread.start();
            }
        } else {
            EventBus.getDefault().post(new EventSyncError(false, 0, "No success response"));
        }
    }

    /**
     * Event that handle errors in Sync web service calls
     * @param eventSyncError - Event object
     */
    public void onEvent(EventSyncError eventSyncError) {
        if (eventSyncError != null && eventSyncError.getError() != null) {
            if (eventSyncError.getError().getCodeInt() == 412) {
                //The server says a initial sync is needed, set the flag and stop the service
                TactSharedPrefController.setNeedSync();
                this.stopSelf();
            } else {
                Log.w("SYNC_ERROR_EVENT", "Contact sync failed with error: " + eventSyncError.getError().getMessage());
            }
        }
    }

    /**
     * Event that handle errors in Config web service calls
     * @param eventConfigError - Event object
     */
    public void onEvent(EventConfigError eventConfigError) {
        EventBus.getDefault().post(new EventCUDLSyncError(TactConst.SERVICE_CUDL_ERROR_SUBSCRIBE));
    }

    /**
     * Event triggered after a sync web service call succeed
     * @param eventFeedItemSyncSuccess - Event object
     */
    public void onEvent(EventFeedItemSyncSuccess eventFeedItemSyncSuccess) {
        this.currentRequestId = eventFeedItemSyncSuccess.getFeedItemSyncResponse().getRequestId();
        if (!isSyncing) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    Log.w("SYNC", "There was no sync running, starting a new one");
                    startSyncProcess();
                }
            };
            thread.start();
        }
    }

    /**
     * Event that handle errors in sync process
     * @param eventCUDLSyncError - Event object
     */
    public void onEvent(EventCUDLSyncError eventCUDLSyncError) {
        this.asyncStartPusher = null;
        switch (eventCUDLSyncError.getErrorType()) {
            case TactConst.SERVICE_CUDL_ERROR_CONNECTION_RETRY: {
                //this is a complete error, after all the retry attempts the pusher is unable to connect
                //TODO: IMPLEMENT SOME KIND OF NOTIFICATION INSIDE APP FOR SYNC ERRORS
            }
            case TactConst.SERVICE_CUDL_ERROR_SUBSCRIBE: {
                if (failedSubscribeAttempts == MAX_RETRIES) {
                    //this is critical, all the subscribe attempts failed
                    //TODO: IMPLEMENT SOME KIND OF NOTIFICATION INSIDE APP FOR SYNC ERRORS
                } else {
                    //try again from scratch
                    failedSubscribeAttempts++;
                    pusher = null;
                    pusherAPIKey = null;
                    checkPusherConnectivity();
                }
            }
            case TactConst.SERVICE_CUDL_ERROR_SYNC: {
                if (failedSyncAttempts == MAX_RETRIES) {
                    //this is critical, all the sync attempts over the same version failed
                    //TODO: IMPLEMENT SOME KIND OF NOTIFICATION INSIDE APP FOR SYNC ERRORS
                } else {
                    //try again in next call
                    failedSubscribeAttempts++;
                }
            }
        }
    }

    /**
     * Async class for starting the pusher
     */
    public class AsyncStartPusher extends AsyncTask<Void, Void, Object> {
        // Maintain attached service for states change propose
        private TactCUDLSyncService context;

        // Keep the response
        private Object _response;
        // Flag that keep async task completed status
        private boolean completed;

        private Pusher pusher;

        // Constructor
        private AsyncStartPusher(TactCUDLSyncService context, Pusher pusher) {
            this.pusher = pusher;
            this.context = context;
        }

        // Pre execution actions
        @Override
        protected void onPreExecute() {

        }

        // Execution of the async task
        protected Object doInBackground(Void... params) {
            try {
                if (pusher != null) {
                    //If pusher instance exist and its disconnected the connect it
                    if (pusher.getConnection().getState() != ConnectionState.CONNECTED) {
                        achieveExpectedConnectionState(ConnectionState.CONNECTED);
                    }
                } else if (pusherAPIKey != null && !pusherAPIKey.isEmpty() && TactSharedPrefController.isPushChannel()) {
                    //if the pusher is null but all the data  to start it is available then start the pusher
                    startPusher();
                } else {
                    try {
                        //Id there is not all the data
                        TactNetworking.callConfig(context);
                    } catch (Exception e) {
                        EventBus.getDefault().post(new EventConfigError(true, false, 0, e.getMessage()));
                    }
                }
                return  pusher;
            } catch (Exception e) {
                return e;
            }
        }

        // Post execution actions
        @Override
        protected void onPostExecute(Object response) {
            // Set task completed and notify the service
            completed = true;
            _response = response;
            notifyActivityTaskCompleted();
        }


        // Sets the current service to the async task
        public void setActivity(TactCUDLSyncService context) {
            this.context = context;
            if (completed) {
                notifyActivityTaskCompleted();
            }
        }

        // Notify service of async task complete
        private void notifyActivityTaskCompleted() {
            if (null != context) {
                context.onAsyncStartPusherCompleted(_response);
            }
        }

    }

    private void startAsyncStartPusher() {
        asyncStartPusher = new AsyncStartPusher(this, pusher);
        asyncStartPusher.execute();
    }

    private boolean isAsyncStartPusherRunningOrPending() {
        return asyncStartPusher != null && asyncStartPusher.getStatus().equals(AsyncTask.Status.FINISHED);
    }

    private void onAsyncStartPusherCompleted(Object response) {
        if (response != null && response instanceof String) {
            this.pusher = (Pusher)response;
        }
        asyncStartPusher = null;
    }

    private void achieveExpectedConnectionState(ConnectionState targetState) {
        ConnectionState currentState = pusher.getConnection().getState();
        if (currentState == targetState) {
            // do nothing, we're there.
            failedConnectionAttempts = 0;
        } else if (targetState == ConnectionState.CONNECTED && failedConnectionAttempts == MAX_RETRIES) {
            targetState = ConnectionState.DISCONNECTED;
            Log.w("PUSHER", "failed to connect after " + failedConnectionAttempts + " attempts. Reconnection attempts stopped.");
            EventBus.getDefault().post(new EventCUDLSyncError(TactConst.SERVICE_CUDL_ERROR_CONNECTION_RETRY));
        }
        else if (currentState == ConnectionState.DISCONNECTED && targetState == ConnectionState.CONNECTED) {
            Runnable task = new Runnable() {
                public void run() {
                    pusher.connect(new com.pusher.client.connection.ConnectionEventListener() {
                        @Override
                        public void onConnectionStateChange(ConnectionStateChange change) {
                            Log.w("PUSHER", "State changed to " + change.getCurrentState() + " from " + change.getPreviousState());

                            if (change.getCurrentState() == ConnectionState.CONNECTED && TactSharedPrefController.isPushChannel()) {
                                if (suscribedChannel == null) {
                                    suscribedChannel = pusher.subscribePrivate(TactSharedPrefController.getPushChannel(), new PrivateChannelEventListener() {
                                        @Override
                                        public void onAuthenticationFailure(String s, Exception e) {
                                            Log.w("PUSHER", "Channel subscription authorization failed");
                                            suscribedChannel = null;
                                            pusher = null;
                                            EventBus.getDefault().post(new EventCUDLSyncError(TactConst.SERVICE_CUDL_ERROR_SUBSCRIBE));
                                        }

                                        @Override
                                        public void onSubscriptionSucceeded(String s) {
                                            Log.w("PUSHER", "Channel subscription authorization succeeded");
                                            try {
                                                syncFeedItemsScheduler.scheduleWithFixedDelay
                                                        (new Runnable() {
                                                            public void run() {
                                                                try {
                                                                    if (!isSyncing) {
                                                                        TactNetworking.callSyncFeedItems(TactCUDLSyncService.this);
                                                                    }
                                                                } catch (Exception e) {
                                                                    Log.w("CUDLSERVICE", "Exception in hotsync with message: " + e.getMessage());
                                                                    EventBus.getDefault().post(new EventCUDLSyncError(TactConst.SERVICE_CUDL_ERROR_SYNC));
                                                                }
                                                            }
                                                        }, 10, 30, TimeUnit.SECONDS);
                                            }
                                            catch (Exception e){
                                                Log.w("CUDLSERVICE", "Exception in hotsync with message: " + e.getMessage());
                                            }
                                            try {
                                                syncScheduler.scheduleWithFixedDelay
                                                        (new Runnable() {
                                                            public void run() {
                                                                try {
                                                                    if (!isContactSyncing) {
                                                                        TactNetworking.callSync(TactCUDLSyncService.this);
                                                                    }
                                                                } catch (Exception e) {
                                                                    Log.w("CUDLSERVICE", "Exception in contact sync sync with message: " + e.getMessage());
                                                                    EventBus.getDefault().post(new EventSyncError(false, 0, e.getMessage()));
                                                                }
                                                            }
                                                        }, 1, 1, TimeUnit.MINUTES);
                                            } catch (Exception e){
                                                Log.w("SYNCSERVICE", "Exception in contactsync with message: " + e.getMessage());
                                            }

                                        }

                                        @Override
                                        public void onEvent(String s, String eventName, final String data) {
//                                            Log.w("PUSHER", "An event with name " + eventName + " was delivered!! WITH DATA: " + data);
                                            if (eventName.equals("cudl-objects")) {
                                                if (pusherPackages == null) {
                                                    pusherPackages = new ArrayList<String>();
                                                }
                                                pusherPackages.add(data);
//                                                Log.w("SYNC", "Package added, packages to proccess: " + pusherPackages.size());
                                            } else if (eventName.equals("cudl-contacts")) {
//                                                if (data.contains("record_id"))
                                                    Log.w("PUSHER", "Contact object arrive with data: " + data);
                                                if (contactsSyncPackages == null) {
                                                    contactsSyncPackages = new ArrayList<String>();
                                                }
                                                contactsSyncPackages.add(data);
                                            }
                                        }
                                    }, "cudl-objects", "cudl-contacts");
                                }
                            }
                        }

                        @Override
                        public void onError(String message, String code, Exception e) {
                            Log.w("PUSHER", "There was a problem connecting with code " + code + " and message " + message );
                        }
                    }, ConnectionState.ALL);
                }
            };
            Log.w("PUSHER", "Connecting in " + failedConnectionAttempts + " seconds");
            connectionAttemptsWorker.schedule(task, (failedConnectionAttempts), TimeUnit.SECONDS);
            ++failedConnectionAttempts;
        }
        else if (currentState == ConnectionState.CONNECTED && targetState == ConnectionState.DISCONNECTED) {
            pusher.disconnect();
        }
    }

    public void checkPusherConnectivity() {
        if (pusher == null || (pusher.getConnection().getState() == ConnectionState.DISCONNECTED && !isAsyncStartPusherRunningOrPending())) {
            Log.w("CONNECTIONS", "HomeActivity:checkPusherConnectivity - The pusher is not connected, start asynctask");
            startAsyncStartPusher();
        }
    }

    private void startPusher() {
        Log.w("CONNECTIONS", "HomeActivity:startPusher - Pusher was null so initializing");
        HttpAuthorizer authorizer = new HttpAuthorizer(TactNetworking.getURL() + TactConst.END_POINT_PUSHER_AUTH);
        authorizer.setHeaders(TactNetworking.getMapAuthorizationHeaders(this));
        PusherOptions options = new PusherOptions().setEncrypted(true).setAuthorizer(authorizer);

        pusher = new Pusher(pusherAPIKey, options);

        Log.w("CONNECTIONS", "HomeActivity:startPusher - Connect called from startPusher");
        achieveExpectedConnectionState(ConnectionState.CONNECTED);

    }

    private volatile long latestTimestamp = 0;
    ArrayList<FrozenFeedItem> syncItems;
    private String currentRequestId;

    private void startSyncProcess() {
        Log.w("SYNC", "Sync process starting");
        isSyncing = true;
        pusherPackages.clear();
        latestTimestamp = 0;
        syncItems = new ArrayList<>();
        try {
            TactApplication.getInstance().getJobManager().addJob(new ProcessCUDLSyncPackageJob(getNextPackage(), currentRequestId, 0));
        } catch (Exception e) {
            EventBus.getDefault().postSticky(new EventSyncCUDLProcessPackageError(e.getMessage()));
        }
    }

    /**
     * Event that control the response of a package sync
     * @param eventSyncCUDLProcessPackage
     */
    public void onEvent(EventSyncCUDLProcessPackage eventSyncCUDLProcessPackage) {
        try {
            Log.w("SYNC", "A sync package finish with data: " +
                    "currentPackageNumber: " + eventSyncCUDLProcessPackage.getPackageNumber() +
                    ", totalPackagesCount: " + eventSyncCUDLProcessPackage.getTotalPackageCount() +
                    ", packageTimestamp: " + eventSyncCUDLProcessPackage.getPackageTimestamp() +
                    ", version: " + eventSyncCUDLProcessPackage.getVersion());
            if (eventSyncCUDLProcessPackage != null) {
//                if (latestTimestamp == 0 || Utils.getDateFromTimestamp(latestTimestamp).before(Utils.getDateFromTimestamp(eventSyncCUDLProcessPackage.getPackageTimestamp()))) {
//                    latestTimestamp = eventSyncCUDLProcessPackage.getPackageTimestamp();
//                }
                if (eventSyncCUDLProcessPackage.getPackageTimestamp() > 0 && (latestTimestamp == 0 || latestTimestamp < eventSyncCUDLProcessPackage.getPackageTimestamp())) {
                    latestTimestamp = eventSyncCUDLProcessPackage.getPackageTimestamp();
                    Log.w("SYNC", "latestTimestamp set to: " + latestTimestamp);
                }
                if (eventSyncCUDLProcessPackage.getItems() != null && eventSyncCUDLProcessPackage.getItems().size() > 0) {
                    syncItems.addAll(eventSyncCUDLProcessPackage.getItems());
                }
                if (eventSyncCUDLProcessPackage.isLastPackage()) {
                    Log.w("SYNC", "Is last package, finishing sync");
                    syncDatabase(eventSyncCUDLProcessPackage);
                    finishSyncProcess();
                } else {
                    TactApplication.getInstance().getJobManager().addJob(new ProcessCUDLSyncPackageJob(getNextPackage(), currentRequestId, eventSyncCUDLProcessPackage.getPackageNumber()));
                }
            } else {
                throw new Exception("Error processing package. Package arrive to event was null");
            }
        } catch (Exception e) {
            EventBus.getDefault().postSticky(new EventSyncCUDLProcessPackageError(e.getMessage()));
        }
    }

    public void onEvent(EventSyncCUDLProcessPackageError eventSyncCUDLProcessPackageError) {
        android.util.Log.e("SYNC ERROR", eventSyncCUDLProcessPackageError.getErrorMessage());
        finishSyncProcess();
    }

    private synchronized String getNextPackage() throws Exception {
        String newPackage = null;
        Log.w("SYNC", "Trying to get new package..");
        for (int i = 0; i <= 10; i++) {
            Log.w("SYNC", "get package attempt " + i);
            if (pusherPackages != null && pusherPackages.size() > 0) {
                newPackage = pusherPackages.get(0);
                pusherPackages.remove(0);
                Log.w("SYNC", "Returning a new package");
                break;
            } else {
                Log.w("SYNC", "No package, wait and repeat..");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException r) {

                }
            }
        }
        if (newPackage != null) {
            return newPackage;
        } else throw new Exception("Get package don't return any result, this will finish the current sync with error.");
    }

    private void syncDatabase(EventSyncCUDLProcessPackage eventSyncCUDLProcessPackage) {
        //Save sync status
        try {
            //Get the sync status table row if exist
            GlobalSyncStatus syncStatus = null;
            List<GlobalSyncStatus> syncStatusList = TactApplication.daoSession.getGlobalSyncStatusDao().queryBuilder().limit(1).list();
            if (syncStatusList != null && syncStatusList.size() > 0) {
                syncStatus = syncStatusList.get(0);
            }
            //if there is no row in sync status create a new one
            if (syncStatus == null) {
                syncStatus = new GlobalSyncStatus();
            }
            syncStatus.setLowerBound(eventSyncCUDLProcessPackage.getBounds().getLower());
            syncStatus.setUpperBound(eventSyncCUDLProcessPackage.getBounds().getUpper());
            if (syncStatus.getTimestamp() != null && syncStatus.getTimestamp() != 0) {
                if (latestTimestamp > syncStatus.getTimestamp())
                    syncStatus.setTimestamp(latestTimestamp);
            } else {
                syncStatus.setTimestamp(latestTimestamp);
            }

            syncStatus.setVersion(eventSyncCUDLProcessPackage.getVersion());
            //update or insert
            if (syncStatus.getId() == null || syncStatus.getId() < 0) {
                TactApplication.daoSession.insert(syncStatus);
            } else {
                TactApplication.daoSession.update(syncStatus);
            }
            Log.w("SYNC", "GSS saved with data: " + syncStatus.toString());

            //Insert items
            if (syncItems != null && syncItems.size() > 0) {
                ArrayList<Long> dates = new ArrayList<Long>();
                for (FrozenFeedItem item : syncItems) {
                    if (item != null && TactApplication.daoSession != null) {
                        //This is a validation for events to handle the matching with events in the device and avoid repetition
                        if (item.getType().equals("Event") && item.getSourceId() == Integer.parseInt(DatabaseManager.getAccountId("tactapp"))) {
                            FrozenFeedItem exitingEvent = TactDataSource.getLocalEventFromDatabase((FeedItemEvent) item.getFeedItem());
                            if (exitingEvent != null) {
                                exitingEvent.setServerId(item.getServerId());
                                exitingEvent.setNeedSync(1);
                                TactApplication.daoSession.update(exitingEvent);
                            }
                        } else if (!item.itemExist()) {
                            item.setNeedSync(0);
                            item.setId(TactApplication.daoSession.insert(item));
                            item.updateRelations();
                        } else {
                            FrozenFeedItem itemDB = TactDataSource.getItemInDB(item);
                            if (itemDB != null) {
                                item.setId(itemDB.getId());
                                item.__setDaoSession(TactApplication.getInstance().daoSession);
                                item.setNeedSync(0);
                                TactApplication.getInstance().daoSession.update(item);
                                item.updateRelations();
                            }
                        }
                        if (item.getType().equals("Event") && !dates.contains(CalendarAPI.getDateOnly(item.getTimestamp()))){
                            dates.add(CalendarAPI.getDateOnly(item.getTimestamp()));
                        }
                    }
                }
                if (dates.size() > 0) {
                    EventNotifyCalendarChanges eventNotifyCalendarChanges = new EventNotifyCalendarChanges();
                    eventNotifyCalendarChanges.setSyncEventsOrTasks(true);
                    eventNotifyCalendarChanges.setDates(dates);
                    EventBus.getDefault().postSticky(eventNotifyCalendarChanges);
                }
                Log.w("SYNC", syncItems.size() + " items saved.");
            }
        } catch (Exception e) {
            EventBus.getDefault().postSticky(new EventSyncCUDLProcessPackageError("Can't save into database with message: " + e.getMessage()));
        }
    }

    private void finishSyncProcess() {
        isSyncing = false;
        pusherPackages.clear();
        currentRequestId = null;
        latestTimestamp = 0;
        syncItems = null;
        Log.w("SYNC", "Sync finished");
        System.gc();
    }

    /****************************CONTACTS SYNC****************************************************/

    private String currentCallId;
    private ArrayList<String> contactsSyncPackages;
    private int contactsCount;
    private int opportunitiesCount;
    private Map<String, ArrayList<ContactField>> fields;

    public void onEvent(EventContactSyncSuccess eventContactSyncSuccess) {
        if (eventContactSyncSuccess != null && eventContactSyncSuccess.getSyncContactsPackageMetadata() != null) {
            Log.w("SYNC", "A contact sync package finish with " +
                    "type: " + eventContactSyncSuccess.getSyncContactsPackageMetadata().getType() +
                    ", currentContactPackageCount: " + eventContactSyncSuccess.getCurrentContactsPackagesCount() + " of totalContactPackagesCount: " + eventContactSyncSuccess.getSyncContactsPackageMetadata().getContactCount() +
                    ", currentOpportunitiesPackageCount: " + eventContactSyncSuccess.getCurrentOpportunitiesCount() + " of totalOpportunitiesPackagesCount: " + eventContactSyncSuccess.getSyncContactsPackageMetadata().getOpportunityCount());
            contactsCount = eventContactSyncSuccess.getSyncContactsPackageMetadata().getContactCount();
            opportunitiesCount = eventContactSyncSuccess.getSyncContactsPackageMetadata().getOpportunityCount();
            if (eventContactSyncSuccess.getSyncContactsPackageMetadata().getContactCount() == eventContactSyncSuccess.getCurrentContactsPackagesCount() &&
                    eventContactSyncSuccess.getSyncContactsPackageMetadata().getOpportunityCount() == eventContactSyncSuccess.getCurrentOpportunitiesCount()) {
                syncContactsDatabase(eventContactSyncSuccess);
            } else {
                try {
                    fields.putAll(eventContactSyncSuccess.getFields());
                    TactApplication.getInstance().getJobManager().addJob(new ProcessContactSyncPackageJob(getContactSyncNextPackage(), currentCallId, contactsCount, opportunitiesCount, fields, eventContactSyncSuccess.getCurrentContactsPackagesCount(), eventContactSyncSuccess.getCurrentOpportunitiesCount()));
                } catch (Exception e) {
                    EventBus.getDefault().postSticky(new EventContactSyncError(e.getMessage()));
                }
            }
        } else {
            EventBus.getDefault().postSticky(new EventContactSyncError("A success event came with empty package data"));
        }
    }

    public void onEvent(EventContactSyncError eventContactSyncError) {
        android.util.Log.e("CONTACT SYNC ERROR", eventContactSyncError.getErrorMessage());
        finishContactSync();
    }

    private void startContactSync() {
        isContactSyncing = true;
        opportunitiesCount = 0;
        contactsCount = 0;
        contactsSyncPackages = new ArrayList<>();
        fields = new HashMap<>();
        try {
            TactApplication.getInstance().getJobManager().addJob(new ProcessContactSyncPackageJob(getContactSyncNextPackage(), currentCallId, contactsCount, opportunitiesCount, fields, 0, 0));
        } catch (Exception e) {
            EventBus.getDefault().postSticky(new EventContactSyncError(e.getMessage()));
        }
    }

    private void syncContactsDatabase(EventContactSyncSuccess eventContactSyncSuccess) {
        try {
            if (eventContactSyncSuccess != null && eventContactSyncSuccess.getSyncContactsPackageMetadata() != null) {
                //try sync status save
                DatabaseManager.setSyncRevision(eventContactSyncSuccess.getSyncContactsPackageMetadata().getRevision(), eventContactSyncSuccess.getSyncContactsPackageMetadata().getOpportunitiesRevision());
                //Save contacts
                if (fields != null && fields.size() > 0) {
                    for (Map.Entry<String, ArrayList<ContactField>> entry : fields.entrySet()) {
                        Contact contact = DatabaseManager.getContactByRecordId(entry.getKey());
                        if (contact == null) {
                            contact = new Contact();
                        }
                        contact.setValuesByFields(entry.getValue());
                        DatabaseManager.saveContact(contact);
                    }
                }
            } else {
                EventBus.getDefault().postSticky(new EventContactSyncError("A success event came with empty package data"));
            }
        } catch (Exception e) {
            EventBus.getDefault().postSticky(new EventContactSyncError("Error saving into database with message: " + e.getMessage()));
        }
    }

    private synchronized String getContactSyncNextPackage() throws Exception {
        String newPackage = null;
        for (int i = 0; i <= 10; i++) {
            if (contactsSyncPackages != null && contactsSyncPackages.size() > 0) {
                newPackage = contactsSyncPackages.get(0);
                contactsSyncPackages.remove(0);
                break;
            } else {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException r) {

                }
            }
        }
        if (newPackage != null) {
            return newPackage;
        } else throw new Exception("Get package don't return any result, this will finish the current sync with error.");
    }

    private void finishContactSync() {
        currentCallId = null;
        isContactSyncing = false;
        opportunitiesCount = 0;
        contactsCount = 0;
        contactsSyncPackages = null;
        fields = null;
        System.gc();
    }

}
