package com.tactile.tact.database.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by leyan on 12/10/14.
 */
public class FeedItemLogRelated implements Serializable {

    private static final long serialVersionUID = 3745712767675281172L;

    @SerializedName("type")
    private String type;

    @SerializedName("identifier")
    private String identifier;

    public FeedItemLogRelated() {

    }

    public FeedItemLogRelated(String type, String id) {
        this.type = type;
        this.identifier = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
