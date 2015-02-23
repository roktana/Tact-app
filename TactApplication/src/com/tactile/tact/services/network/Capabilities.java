package com.tactile.tact.services.network;

import com.google.gson.annotations.SerializedName;

public class Capabilities {
    @SerializedName("is_pushable")
    private boolean is_pushable;

    @SerializedName("is_refreshable")
    private boolean is_refreshable;

    protected boolean isRefreshable() {
        return is_refreshable;
    }

    protected boolean isPushable() {
        return is_pushable;
    }
}