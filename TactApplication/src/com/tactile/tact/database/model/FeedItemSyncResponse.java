package com.tactile.tact.database.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by leyan on 12/1/14.
 */
public class FeedItemSyncResponse implements Serializable {

    private static final long serialVersionUID = 3935745911237583172L;

    @SerializedName("top_version")
    private int topVersion;
    private Bounds bounds;
    @SerializedName("request_id")
    private String requestId;

    public int getTopVersion() {
        return topVersion;
    }

    public void setTopVersion(int topVersion) {
        this.topVersion = topVersion;
    }

    public Bounds getBounds() {
        return bounds;
    }

    public void setBounds(Bounds bounds) {
        this.bounds = bounds;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
