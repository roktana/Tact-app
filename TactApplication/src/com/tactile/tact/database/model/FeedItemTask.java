package com.tactile.tact.database.model;

import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.tactile.tact.activities.TactApplication;
import com.tactile.tact.database.entities.FrozenFeedItem;

import java.io.Serializable;

/**
 * Created by leyan on 1/26/15.
 */
public class FeedItemTask extends FeedItem implements Serializable {

    private static final long serialVersionUID = 7765765393315581172L;

    @SerializedName("due_at")
    private Long dueAt;
    @SerializedName("start_at")
    private Long startAt;
    @SerializedName("completed_at")
    private Long completeAt;
    @SerializedName("completed")
    private Boolean completed;
    @SerializedName("is_recurrence")
    private Boolean isRecurrence;
    @SerializedName("priority")
    private String priority;
    @SerializedName("assigned_to")
    private String assignedTo;

    @SerializedName("subject")
    private String subject;
    @SerializedName("WhatId_tact")
    private FeedItemLogRelated companyRelated;
    @SerializedName("WhoId_tact")
    private FeedItemLogRelated contactRelated;
    @SerializedName("description")
    private String description;
    @SerializedName("all_day")
    private Boolean allDay;
    @SerializedName("created_at")
    private Long createdAt;
    @SerializedName("is_recurring")
    private Boolean isRecurring;
    @SerializedName("related_to")
    private String relatedTo;


    public Long getDueAt() {
        return dueAt;
    }

    public void setDueAt(Long dueAt) {
        this.dueAt = dueAt;
    }

    public Long getStartAt() {
        return startAt;
    }

    public void setStartAt(Long startAt) {
        this.startAt = startAt;
    }

    public Long getCompleteAt() {
        return completeAt;
    }

    public void setCompleteAt(Long completeAt) {
        this.completeAt = completeAt;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Boolean getIsRecurrence() {
        return isRecurrence;
    }

    public void setIsRecurrence(Boolean isRecurrence) {
        this.isRecurrence = isRecurrence;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public FeedItemLogRelated getCompanyRelated() {
        return companyRelated;
    }

    public void setCompanyRelated(FeedItemLogRelated companyRelated) {
        this.companyRelated = companyRelated;
    }

    public FeedItemLogRelated getContactRelated() {
        return contactRelated;
    }

    public void setContactRelated(FeedItemLogRelated contactRelated) {
        this.contactRelated = contactRelated;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getAllDay() {
        return allDay;
    }

    public void setAllDay(Boolean allDay) {
        this.allDay = allDay;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getIsRecurring() {
        return isRecurring;
    }

    public void setIsRecurring(Boolean isRecurring) {
        this.isRecurring = isRecurring;
    }

    public String getRelatedTo() {
        return relatedTo;
    }

    public void setRelatedTo(String relatedTo) {
        this.relatedTo = relatedTo;
    }

    public void updateOnDB(){
        FrozenFeedItem feedItem = this.getParent();
        try {
            feedItem.setFeedItem(this, 1);
            TactApplication.daoSession.update(feedItem);
            feedItem.updateRelations();
        }
        catch (Exception e){
            Log.w("TASK UPDATE", "Error updating: " + e.getMessage());
        }
    }
}
