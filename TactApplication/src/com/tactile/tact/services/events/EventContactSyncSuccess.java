package com.tactile.tact.services.events;

import com.tactile.tact.database.model.ContactField;
import com.tactile.tact.database.model.SyncContactsPackageMetadata;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by leyan on 1/30/15.
 */
public class EventContactSyncSuccess extends EventTactBase {
    private SyncContactsPackageMetadata syncContactsPackageMetadata;
    private Map<String, ArrayList<ContactField>> fields;
    private int currentContactsPackagesCount;
    private int currentOpportunitiesCount;

    public EventContactSyncSuccess(SyncContactsPackageMetadata syncContactsPackageMetadata, Map<String, ArrayList<ContactField>> fields, int currentContactsPackagesCount, int currentOpportunitiesCount) {
        this.setSyncContactsPackageMetadata(syncContactsPackageMetadata);
        this.setFields(fields);
        this.setCurrentContactsPackagesCount(currentContactsPackagesCount);
        this.setCurrentOpportunitiesCount(currentOpportunitiesCount);
    }

    public SyncContactsPackageMetadata getSyncContactsPackageMetadata() {
        return syncContactsPackageMetadata;
    }

    public void setSyncContactsPackageMetadata(SyncContactsPackageMetadata syncContactsPackageMetadata) {
        this.syncContactsPackageMetadata = syncContactsPackageMetadata;
    }

    public Map<String, ArrayList<ContactField>> getFields() {
        return fields;
    }

    public void setFields(Map<String, ArrayList<ContactField>> fields) {
        this.fields = fields;
    }

    public int getCurrentContactsPackagesCount() {
        return currentContactsPackagesCount;
    }

    public void setCurrentContactsPackagesCount(int currentContactsPackagesCount) {
        this.currentContactsPackagesCount = currentContactsPackagesCount;
    }

    public int getCurrentOpportunitiesCount() {
        return currentOpportunitiesCount;
    }

    public void setCurrentOpportunitiesCount(int currentOpportunitiesCount) {
        this.currentOpportunitiesCount = currentOpportunitiesCount;
    }
}
