package com.tactile.tact.services.jobs;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;
import com.tactile.tact.activities.TactApplication;
import com.tactile.tact.consts.JobGroup;
import com.tactile.tact.consts.Priority;
import com.tactile.tact.database.TactDataSource;
import com.tactile.tact.database.entities.FrozenFeedItem;
import com.tactile.tact.database.model.SyncCUDLPacket;
import com.tactile.tact.database.model.SyncCUDLPacketMetadata;
import com.tactile.tact.services.events.EventSyncCUDLProcessPackage;
import com.tactile.tact.services.events.EventSyncCUDLProcessPackageError;
import com.tactile.tact.utils.Log;

import de.greenrobot.event.EventBus;

/**
 * Created by leyan on 1/22/15.
 */
public class ProcessCUDLSyncPackageJob extends Job {

    private String currentPackage;
    private String requestId;
    private int packageCount;
    private String errorMessage = "";

    public ProcessCUDLSyncPackageJob(String currentPackage, String requestId, int packageCount) {
        // This job should be persisted in case the application exits before job is completed.
        super(new Params(Priority.HIGH).persist().groupBy(JobGroup.SYNC_CUDL_PROCESS_PACKAGE));
        this.currentPackage = currentPackage;
        this.requestId = requestId;
        this.packageCount = packageCount;
    }

    @Override
    public void onRun() throws Throwable {
        //If the package is null or empty is a critical error so trigger the error to finish the current sync process
        if (currentPackage == null || currentPackage.isEmpty()) {
            throw new Throwable("Current package is empty.");
        } else {
            //Get the current package metadata
            SyncCUDLPacketMetadata packageMetadata = TactDataSource.parseSyncResponseMetadata(currentPackage);
            //Check for package metadata integrity
            if (packageMetadata == null || !packageMetadata.checkPackageMetadataIntegrity()) {
                throw new Throwable("Current package metadata is null or have integrity problems.");
            }
            //Check if this package correspond with the current sync process
            if (!requestId.equals(packageMetadata.getRequestId())) {
                throw new Throwable("Current package don't belong to the current request: Current package id -> " + packageMetadata.getRequestId() + " and expected is -> " + requestId);
            }
            //Check if this is the corresponding package in the package count
            if (packageCount + 1 != packageMetadata.getCurrentPacket() && !packageMetadata.isLastPackage()) {
                throw new Throwable("Current package is not the corresponding one in the packages count.");
            }
            //Get package data
            SyncCUDLPacket syncPackageData = TactDataSource.parseCudlObjects(currentPackage);
            Log.w("SYNC", "latestTimestamp: " + syncPackageData.getLatestTimestamp());
            //Check if the package have data and the data state is correct
            if (syncPackageData == null) {
                throw new Throwable("Package data was empty or corrupted");
            }
            EventBus.getDefault().postSticky(new EventSyncCUDLProcessPackage(syncPackageData.getLatestTimestamp(), syncPackageData.getFrozenFeedItems(), packageMetadata.getCurrentPacket(), packageMetadata.isLastPackage(), packageMetadata.getBounds(), packageMetadata.getVersion(), packageMetadata.getTotalPacket()));
        }
    }

    @Override
    public void onAdded() {

    }

    @Override
    protected void onCancel() {
        EventBus.getDefault().postSticky(new EventSyncCUDLProcessPackageError(errorMessage));
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        errorMessage = throwable.getMessage();
        return false;
    }
}
