package com.tactile.tact.database.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by leyan on 12/1/14.
 */
public class SyncContactsPackageMetadata implements Serializable {
    private static final long serialVersionUID = 3915745726384453172L;

    @SerializedName("call_id")
    private String callId;
    @SerializedName("contact_count")
    private int contactCount;
    @SerializedName("opportunity_count")
    private int opportunityCount;
    @SerializedName("opportunities_revision")
    private int opportunitiesRevision;
    @SerializedName("pending_jobs")
    private Boolean pendingJobs;
    private int revision;
    private String type;

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public int getContactCount() {
        return contactCount;
    }

    public void setContactCount(int contactCount) {
        this.contactCount = contactCount;
    }

    public int getOpportunityCount() {
        return opportunityCount;
    }

    public void setOpportunityCount(int opportunityCount) {
        this.opportunityCount = opportunityCount;
    }

    public int getOpportunitiesRevision() {
        return opportunitiesRevision;
    }

    public void setOpportunitiesRevision(int opportunitiesRevision) {
        this.opportunitiesRevision = opportunitiesRevision;
    }

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getPendingJobs() {
        return pendingJobs;
    }

    public void setPendingJobs(Boolean pendingJobs) {
        this.pendingJobs = pendingJobs;
    }
}
