package com.tactile.tact.database.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by leyan on 8/28/14.
 */
public class Bounds {

    private long upper;
    private long lower;


    public long getUpper() {
        return upper;
    }

    public void setUpper(long upper) {
        this.upper = upper;
    }

    public long getLower() {
        return lower;
    }

    public void setLower(long lower) {
        this.lower = lower;
    }
}
