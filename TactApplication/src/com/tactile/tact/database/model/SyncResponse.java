package com.tactile.tact.database.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by leyan on 12/1/14.
 */
public class SyncResponse implements Serializable {
    private static final long serialVersionUID = 3915825773684445172L;

    @SerializedName("call_id")
    private String callID;
    private Integer revision;
    @SerializedName("opportunities_revision")
    private Integer opportunitiesRevision;
    @SerializedName("pending_jobs")
    private Boolean pendingJobs;

    public String getCallID() {
        return callID;
    }

    public void setCallID(String callID) {
        this.callID = callID;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    public Integer getOpportunitiesRevision() {
        return opportunitiesRevision;
    }

    public void setOpportunitiesRevision(Integer opportunitiesRevision) {
        this.opportunitiesRevision = opportunitiesRevision;
    }

    public Boolean getPendingJobs() {
        return pendingJobs;
    }

    public void setPendingJobs(Boolean pendingJobs) {
        this.pendingJobs = pendingJobs;
    }
}
