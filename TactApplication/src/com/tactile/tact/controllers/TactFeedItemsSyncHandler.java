package com.tactile.tact.controllers;

import android.os.Handler;
import android.os.Message;

import com.tactile.tact.activities.TactApplication;
import com.tactile.tact.database.DatabaseManager;
import com.tactile.tact.database.TactDataSource;
import com.tactile.tact.database.entities.FrozenFeedItem;
import com.tactile.tact.database.entities.GlobalSyncStatus;
import com.tactile.tact.database.model.FeedItemEvent;
import com.tactile.tact.database.model.SyncCUDLPacket;
import com.tactile.tact.database.model.SyncCUDLPacketMetadata;
import com.tactile.tact.services.events.EventNotifyCalendarChanges;
import com.tactile.tact.utils.CalendarAPI;
import com.tactile.tact.utils.Log;
import com.tactile.tact.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

/**
 * Created by leyan on 9/15/14.
 * Class that handle the synchronization of feed items. It exist in application context so its available at all time
 */
public class TactFeedItemsSyncHandler {

    private ArrayList<FrozenFeedItem> items;
    private Timer timerFullSync;
    private Timer timerNextPackage;
    private SyncCUDLPacketMetadata syncCUDLPacketMetadata;
    private volatile boolean syncRunning = true;
    private long latestTimestamp;
    private int lastPackage = 0;
    private SyncCUDLPacket syncCUDLPacket;

    //Current request id
    private String requestId;
    //List of packages arrived
    private ArrayList<String> packages;

    private static final int HANDLE_PACKAGE = 1;

    private int nextPackageAttempts = 0;

    private class PackageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == HANDLE_PACKAGE && msg.obj != null) {

            }
        }
    }

    public void setPackage(String packageData) {
        if (this.packages == null) {
            this.packages = new ArrayList<String>();
        }
        packages.add(packageData);
    }

    /**
     * Task that cancel the current sync, act like timeout for now in 10 minutes timer
     */
    class CancelSyncTask extends TimerTask {
        public void run() {
            Log.w("SYNCPROCESS----------", "Cancelling task coz timer expire");
            cancelCurrentSync();
        }
    }

    class GetNextPackageTask extends TimerTask {
        public void run() {
            Log.w("SYNCPROCESS----------", "Calling getNextPackage from timer after 2 sec");
            getNexPackage();
        }
    }

    public TactFeedItemsSyncHandler() {
        items = new ArrayList<FrozenFeedItem>();
        packages = new ArrayList<String>();
        syncRunning = false;
    }

    public void startASync(String requestId) {
        Log.w("SYNCPROCESS----------", "Starting a sync with request id = " + requestId);
        if (requestId != null && !requestId.isEmpty()) {
            timerFullSync = new Timer();
            timerFullSync.schedule(new CancelSyncTask(), 300000);
            this.requestId = requestId;
            this.syncRunning = true;
            Log.w("SYNCPROCESS----------", "Calling getNextPackage");
            getNexPackage();
        } else {
            Log.w("SYNCPROCESS----------", "Request id was null or empty, cancelling the sync");
            cancelCurrentSync();
        }
    }

    private void getNexPackage() {
        try {
            Log.w("SYNCPROCESS----------", "getting next package...");
            if (timerNextPackage != null) {
                Log.w("SYNCPROCESS----------", "Next package timer was not null, cancelling timer");
                timerNextPackage.cancel();
            }
            if (nextPackageAttempts > 10) {
                Log.w("SYNCPROCESS----------", "Cancel after 10 call to get next package");
                cancelCurrentSync();
            }
            //Get the next package in the list
            if (packages != null && packages.size() > 0) {
                nextPackageAttempts = 0;
                Log.w("SYNCPROCESS----------", "The list have packages waiting");
//            Message msg = Message.obtain(this.packageHandler, HANDLE_PACKAGE, packages.get(0));
//            packageHandler.sendMessageDelayed(msg, 2000);
                Log.w("SYNCPROCESS----------", "Calling processPackage with the first element of the list");
                String currentPackage = packages.get(0);
                packages.remove(0);
                processPackage(currentPackage);
            } else {
                nextPackageAttempts++;
                Log.w("SYNCPROCESS----------", "List was empty, starting the 2 sec timer to call getNextPackage again");
                timerNextPackage = new Timer();
                timerNextPackage.schedule(new GetNextPackageTask(), 2000);
            }
        } catch (Exception e) {
            Log.w("SYNCPROCESS----------", "Exception in getNextPackage with message: " + e.getMessage());
        }
    }

    public void cancelCurrentSync() {
        Log.w("SYNCPROCESS----------", "Cancelling sync...");
        nextPackageAttempts = 0;
        this.packages = new ArrayList<String>();
        timerFullSync.cancel();
        timerFullSync = null;
        if (timerNextPackage != null) {
            timerNextPackage.cancel();
            timerNextPackage = null;
        }
        items = new ArrayList<FrozenFeedItem>();
        latestTimestamp = 0;
        this.syncRunning = false;
        System.gc();
    }

    private void finishCurrentSync() {
        try {
            Log.w("SYNCPROCESS----------", "Finishing current sync...");
            syncStatus();
            databaseSync();
            Log.w("SYNCPROCESS----------", "Calling cancel to clear all data");
            cancelCurrentSync();
        } catch (Exception e) {
            Log.w("SYNCPROCESS----------", "There was an error trying to save in the database with message: " + e.getMessage());
        }
    }

    private void processPackage(String packageData) throws Exception{
        try {
            Log.w("SYNCPROCESS----------", "Entering process package");
            if (packageData != null) {
                Log.w("SYNCPROCESS----------", "Procesing package with data: " + packageData);
                syncCUDLPacketMetadata = null;
                syncCUDLPacketMetadata = TactDataSource.parseSyncResponseMetadata(packageData);
                if (syncCUDLPacketMetadata != null) {
                    //Check if is a package for the current sync
                    if (syncCUDLPacketMetadata.getRequestId().equals(requestId)) {
                        Log.w("SYNCPROCESS----------", "Is same request id");
                        //Check if this is the package that correspond in the list
                        if (lastPackage + 1 == syncCUDLPacketMetadata.getCurrentPacket() || syncCUDLPacketMetadata.isLastPackage()) {
                            Log.w("SYNCPROCESS----------", "Is corresponding package");
                            lastPackage = syncCUDLPacketMetadata.getCurrentPacket();
                            Log.w("SYNCPROCESS----------", "Getting objects");
                            syncCUDLPacket = TactDataSource.parseCudlObjects(packageData);
                            if (syncCUDLPacket != null) {
                                Log.w("SYNCPROCESS----------", "Current latest timestamp is: " + String.valueOf(latestTimestamp));
                                if (latestTimestamp == 0 || Utils.getDateFromTimestamp(latestTimestamp).before(Utils.getDateFromTimestamp(syncCUDLPacket.getLatestTimestamp()))) {
                                    latestTimestamp = syncCUDLPacket.getLatestTimestamp();
                                    Log.w("SYNCPROCESS----------", "Latest timestamp set to: " + String.valueOf(latestTimestamp));
                                }
                                if (syncCUDLPacket.getFrozenFeedItems() != null && syncCUDLPacket.getFrozenFeedItems().size() > 0) {
                                    Log.w("SYNCPROCESS----------", "Items list size is: " + String.valueOf(syncCUDLPacket.getFrozenFeedItems().size()));
                                    items.addAll(syncCUDLPacket.getFrozenFeedItems());
                                }
                                if (syncCUDLPacketMetadata.isLastPackage()) {
                                    Log.w("SYNCPROCESS----------", "Is last package, calling finish sync");
                                    finishCurrentSync();
                                } else {
                                    getNexPackage();
                                }
                            } else throw new Exception("syncCUDLPacket was null after trying to parse data.");
                        }  else {
                            getNexPackage();
                        }
                    } else {
                        cancelCurrentSync();
                    }
                } else {
                    Log.w("SYNCPROCESS----------", "syncCUDLPacketMetadata was null after trying to parse data");
                    throw new Exception("syncCUDLPacketMetadata was null after trying to parse data");
                }
            } else {
                Log.w("SYNCPROCESS----------", "Data came null to the process package");
                throw new Exception("Data can't be null.");
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }


    /**
     * Return if a sync is running currently
     * @return -True if a sync is running, False otherwise
     */
    public boolean isSyncing() {
        return syncRunning;
    }

    /**
     * Execute the database sync of the synced objects
     */
    private void databaseSync() {
        Log.w("SYNCPROCESS----------", "Saving items to database");
        if (items != null && items.size() > 0) {
            Log.w("SYNCPROCESS----------", "There is items to save with size: " + String.valueOf(items.size()));
            Log.w("SYNCPROCESS----------", "Start saving items....");
            ArrayList<Long> dates = new ArrayList<Long>();
            EventNotifyCalendarChanges eventNotifyCalendarChanges = new EventNotifyCalendarChanges();
            for (FrozenFeedItem item : items) {
                if (item != null && TactApplication.daoSession != null) {
                    if (item.getType().equals("Event") && item.getSourceId() == Integer.parseInt(DatabaseManager.getAccountId("tactapp"))) {
                        FrozenFeedItem exitingEvent = TactDataSource.getLocalEventFromDatabase((FeedItemEvent)item.getFeedItem());
                        if (exitingEvent != null) {
                            exitingEvent.setServerId(item.getServerId());
                            exitingEvent.setNeedSync(1);
                            TactApplication.daoSession.update(exitingEvent);
                        }
                    } else
                    if (!item.itemExist()) {
                        Log.w("SYNCPROCESS----------", "Item not exist, making a insert..");
                        item.setNeedSync(0);
                        item.setId(TactApplication.daoSession.insert(item));
                        Log.w("SYNCPROCESS----------", "Saving relations");
                        item.updateRelations();
                    } else {
                        FrozenFeedItem itemDB = TactDataSource.getItemInDB(item);
                        if (itemDB != null) {
                            Log.w("SYNCPROCESS----------", "Item exist, making a update..");
                            item.setId(itemDB.getId());
                            item.__setDaoSession(TactApplication.getInstance().daoSession);
                            item.setNeedSync(0);
                            TactApplication.getInstance().daoSession.update(item);
                            Log.w("SYNCPROCESS----------", "Saving relations");
                            item.updateRelations();
                        }
                    }
                    if (item.getType().equals("Event") && !dates.contains(CalendarAPI.getDateOnly(item.getTimestamp()))){
                        dates.add(CalendarAPI.getDateOnly(item.getTimestamp()));
                    }
                }
            }
            if (dates.size() > 0) {
                eventNotifyCalendarChanges.setSyncEventsOrTasks(true);
                eventNotifyCalendarChanges.setDates(dates);
                EventBus.getDefault().postSticky(eventNotifyCalendarChanges);
            }
        }
    }

    /**
     * Execte the sync status update over the databas
     */
    private void syncStatus() throws  Exception {
        Log.w("SYNCPROCESS----------", "Saving sync status...");
        if (syncCUDLPacketMetadata != null) {
            try {
                //Get the sync status table row if exist
                GlobalSyncStatus syncStatus = null;
                Log.w("SYNCPROCESS----------", "Getting current syncStatus from DB");
                List<GlobalSyncStatus> syncStatusList = TactApplication.daoSession.getGlobalSyncStatusDao().queryBuilder().limit(1).list();
                if (syncStatusList != null && syncStatusList.size() > 0) {
                    Log.w("SYNCPROCESS----------", "syncStatus from DB return results, get the first row");
                    syncStatus = syncStatusList.get(0);
                }
                //if there is no row in sync status create a new one
                if (syncStatus == null) {
                    Log.w("SYNCPROCESS----------", "syncStatus from DB is null, creating new object");
                    syncStatus = new GlobalSyncStatus();
                }
                //set the new sync status data
                if (syncCUDLPacketMetadata.getBounds() != null) {
                    syncStatus.setLowerBound(syncCUDLPacketMetadata.getBounds().getLower());
                    syncStatus.setUpperBound(syncCUDLPacketMetadata.getBounds().getUpper());
                }
                if (syncStatus.getTimestamp() == null || syncStatus.getTimestamp() == 0 || Utils.getDateFromTimestamp(syncStatus.getTimestamp()).before(Utils.getDateFromTimestamp(latestTimestamp))) {
                    Log.w("SYNCPROCESS----------", "Setting latest timestamp to: " + String.valueOf(latestTimestamp));
                    syncStatus.setTimestamp(latestTimestamp);
                }
                Log.w("SYNCPROCESS----------", "setting version to: " + syncCUDLPacketMetadata.getVersion());
                syncStatus.setVersion(syncCUDLPacketMetadata.getVersion());
                //update or insert
                if (syncStatus.getId() == null || syncStatus.getId() < 0) {
                    Log.w("SYNCPROCESS----------", "saving into database");
                    TactApplication.daoSession.insert(syncStatus);
                } else {
                    Log.w("SYNCPROCESS----------", "updating into database");
                    TactApplication.daoSession.update(syncStatus);
                }
            } catch (Exception e) {
                throw new Exception("Can't save the current sync status with message: " + e.getMessage());
            }
        } else throw new Exception("Can't save the current sync status");
    }
}
