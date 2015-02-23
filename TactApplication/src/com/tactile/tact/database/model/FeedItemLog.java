package com.tactile.tact.database.model;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by admin on 9/23/14.
 */
public class FeedItemLog extends FeedItem implements Serializable {

    private static final long serialVersionUID = 3745742767285281172L;

    private String subject;
    private String description;
    @SerializedName("ActivityDate")
    private Long date;
    @SerializedName("WhoId_tact")
    private FeedItemLogRelated contact;
    @SerializedName("WhatId_tact")
    private FeedItemLogRelated related;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public FeedItemLogRelated getContact() {
        return contact;
    }

    public void setContact(FeedItemLogRelated contact) {
        this.contact = contact;
    }

    public FeedItemLogRelated getRelated() {
        return related;
    }

    public void setRelated(FeedItemLogRelated related) {
        this.related = related;
    }

    public String toJSON() {
        try {
            JSONObject jObject = feedItemToJSON(FeedItemLog.class);
            if (jObject != null) {
                JSONObject jData = feedItemToJSON(FeedItem.class);
                jData.put("Subject", this.getSubject());
                jData.put("Description", this.getDescription());
                jObject.put("data", jData);
                return jObject.toString();
            } else return null;
        } catch (Exception e) {
            return null;
        }
    }

}
