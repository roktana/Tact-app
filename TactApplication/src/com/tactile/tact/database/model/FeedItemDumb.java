package com.tactile.tact.database.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by leyan on 1/26/15.
 */
public class FeedItemDumb extends FeedItem implements Serializable {

    private static final long serialVersionUID = 3745342755283381172L;

    @SerializedName("dumb_field")
    private String dumbField;

    public String getDumbField() {
        return dumbField;
    }

    public void setDumbField(String dumbField) {
        this.dumbField = dumbField;
    }
}
