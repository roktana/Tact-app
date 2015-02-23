package com.tactile.tact.database.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by sebafonseca on 2/5/15.
 */
public class FeedItemNote extends FeedItem implements Serializable {
    private static final long serialVersionUID = 7363765193115581172L;

    @SerializedName("body")
    private String description;
    @SerializedName("subject")
    private String subject;
    @SerializedName("ParentId_tact")
    private FeedItemLogRelated parentIdTact;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public FeedItemLogRelated getParentIdTact() {
        return parentIdTact;
    }

    public void setParentIdTact(FeedItemLogRelated parentIdTact) {
        this.parentIdTact = parentIdTact;
    }
}
