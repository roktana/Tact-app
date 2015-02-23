package com.tactile.tact.services.jobs;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;
import com.tactile.tact.consts.JobGroup;
import com.tactile.tact.consts.Priority;
import com.tactile.tact.database.TactDataSource;
import com.tactile.tact.database.model.ContactField;
import com.tactile.tact.database.model.SyncContactsPackageMetadata;
import com.tactile.tact.services.events.EventContactSyncError;
import com.tactile.tact.services.events.EventContactSyncSuccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

/**
 * Created by leyan on 1/30/15.
 */
public class ProcessContactSyncPackageJob extends Job {

    String currentPackage;
    private String errorMessage = "";
    private String currentCallId;
//    private int contactsCount;
//    private int opportunitiesCount;
    private Map<String, ArrayList<ContactField>> fields;
    private int currentContactsPackagesCount;
    private int currentOpportunitiesCount;

    public ProcessContactSyncPackageJob(String currentPackage, String currentCallId, int contactCount, int opportunitiesCount, Map<String, ArrayList<ContactField>> fields, int currentContactsPackagesCount, int currentOpportunitiesCount) {
        // This job should be persisted in case the application exits before job is completed.
        super(new Params(Priority.HIGH).persist().groupBy(JobGroup.SYNC_CONTACTS_PROCESS_PACKAGE));
        this.currentPackage = currentPackage;
        this.currentCallId = currentCallId;
        this.fields = fields;
        this.currentContactsPackagesCount = currentContactsPackagesCount;
        this.currentOpportunitiesCount = currentOpportunitiesCount;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        //If the package is null or empty is a critical error so trigger the error to finish the current sync process
        if (currentPackage == null || currentPackage.isEmpty()) {
            throw new Throwable("Current package is empty.");
        } else {
            SyncContactsPackageMetadata syncContactsPackageMetadata = TactDataSource.parseSyncMetadata(currentPackage);
            if (syncContactsPackageMetadata == null) {
                throw new Throwable("Current package metadata is null or have integrity problems.");
            }
            //Check if this package correspond with the current sync process
            if (!currentCallId.equals(syncContactsPackageMetadata.getCallId())) {
                throw new Throwable("Current package don't belong to the current request: Current package id -> " + syncContactsPackageMetadata.getCallId() + " and expected is -> " + currentCallId);
            }
            if (syncContactsPackageMetadata.getType().equals("contacts")) {
                currentContactsPackagesCount++;
            }
            if (syncContactsPackageMetadata.getType().equals("opportunities")) {
                currentOpportunitiesCount++;
            }
            if (fields == null) {
                fields = new HashMap<>();
            }
            if (syncContactsPackageMetadata.getType().equals("contacts")) {
                fields = TactDataSource.parseContactSync(currentPackage, fields);
                if (fields == null) {
                    throw new Throwable("Package data was empty or corrupted");
                }
            }
            EventBus.getDefault().postSticky(new EventContactSyncSuccess(syncContactsPackageMetadata, fields, currentContactsPackagesCount, currentOpportunitiesCount));
        }
    }

    @Override
    protected void onCancel() {
        EventBus.getDefault().postSticky(new EventContactSyncError(errorMessage));
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        errorMessage = throwable.getMessage();
        return false;
    }
}
