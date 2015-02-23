package com.tactile.tact.controllers;

import com.tactile.tact.activities.TactApplication;
import com.tactile.tact.database.DatabaseManager;
import com.tactile.tact.database.TactDataSource;
import com.tactile.tact.database.model.SyncContactsPackageMetadata;
import com.tactile.tact.utils.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by leyan on 12/1/14.
 */
public class TactSyncHandler {
    private String callID;
    private Timer timer;
    private SyncContactsPackageMetadata syncContactsPackageMetadata;
    private int packageCount = 0;
    private boolean syncRunning = false;

    /**
     * Start a sync, initialize vars and timer, parse the first package
     * @param data - Json data of the package
     */
    private void startSync(String data) {
        syncRunning = true;
        timer = new Timer();
        //5 minutes for timeout the current sync
        timer.schedule(new CancelSyncTask(), 300000);
        if (data == null || data.isEmpty()) {
            finishSync(true);
        } else {
            syncContactsPackageMetadata = TactDataSource.parseSyncMetadata(data);
            if (syncContactsPackageMetadata != null &&
                syncContactsPackageMetadata.getCallId() != null &&
                !syncContactsPackageMetadata.getCallId().isEmpty() &&
                syncContactsPackageMetadata.getCallId().equals(callID)) {
                //TODO: PARSE DATA!!!!!
            } else {
                finishSync(true);
            }
        }
    }


    /**
     * Task that cancel the current sync, act like timeout for now in 10 minutes timer
     */
    class CancelSyncTask extends TimerTask {
        public void run() {
            finishSync(true);
        }
    }

    /**
     * Method that finish a sync process
     * @param isCancel - If is cancel is true there will be no saves the current status, the sync will be cancelled
     */
    private void finishSync(boolean isCancel) {
        if (!isCancel) {
            try {
                DatabaseManager.setSyncRevision(syncContactsPackageMetadata.getRevision(), syncContactsPackageMetadata.getOpportunitiesRevision());
            } catch (Exception e) {
                Log.w("SYNC", "Error trying to save current sync status to DB");
            }
        }
        //clear variables
        syncRunning = false;
        packageCount = 0;
        timer.cancel();
    }

    /**
     * Return if a sync is running currently
     * @return -True if a sync is running, False otherwise
     */
    public boolean isSyncing() {
        return isSyncRunning();
    }

    public String getCallID() {
        return callID;
    }

    public void setCallID(String callID) {
        this.callID = callID;
    }

    public int getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(int packageCount) {
        this.packageCount = packageCount;
    }

    public boolean isSyncRunning() {
        return syncRunning;
    }

    public void setSyncRunning(boolean syncRunning) {
        this.syncRunning = syncRunning;
    }
}
