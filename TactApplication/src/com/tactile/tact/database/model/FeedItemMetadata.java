package com.tactile.tact.database.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by leyan on 11/26/14.
 */
public class FeedItemMetadata implements Serializable {

    private static final long serialVersionUID = 3745345816215583164L;

    @SerializedName("opportunity_id")
    private String opportunityId;
    @SerializedName("is_activity")
    private Boolean isActivity;
    @SerializedName("created_at")
    private Long createdAt;
    @SerializedName("updated_at")
    private Long updatedAt;
    @SerializedName("master_id")
    private String masterId;
    @SerializedName("original_id")
    private String originalId;
    private String type;
    @SerializedName("contact_ids")
    private ArrayList<String> relatedContacts;
    @SerializedName("related_ids")
    private ArrayList<String> relatedItems;
    @SerializedName("push_to_source_ids")
    private ArrayList<Integer> pushToSources;
    @SerializedName("unpush_from_source_ids")
    private ArrayList<Integer> unpushFromSources;
    @SerializedName("pushed_source_ids")
    private ArrayList<Integer> pushedSources;

    public FeedItemMetadata clone() {
        FeedItemMetadata feedItemMetadata = new FeedItemMetadata();
        feedItemMetadata.setOriginalId(this.getOriginalId());
        feedItemMetadata.setRelatedItems(this.getRelatedItems());
        feedItemMetadata.setMasterId(this.getMasterId());
        feedItemMetadata.setRelatedContacts(this.getRelatedContacts());
        feedItemMetadata.setCreatedAt(this.getCreatedAt());
        feedItemMetadata.setIsActivity(this.getIsActivity());
        feedItemMetadata.setOpportunityId(this.getOpportunityId());
        feedItemMetadata.setPushedSources(this.getPushedSources());
        feedItemMetadata.setPushToSources(this.getPushToSources());
        feedItemMetadata.setType(this.getType());
        feedItemMetadata.setUnpushFromSources(this.getUnpushFromSources());
        feedItemMetadata.setUpdatedAt(this.getUpdatedAt());
        return feedItemMetadata;
    }


    public String getOpportunityId() {
        return opportunityId;
    }

    public void setOpportunityId(String opportunityId) {
        this.opportunityId = opportunityId;
    }

    public Boolean getIsActivity() {
        return isActivity;
    }

    public void setIsActivity(Boolean isActivity) {
        this.isActivity = isActivity;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getMasterId() {
        return masterId;
    }

    public void setMasterId(String masterId) {
        this.masterId = masterId;
    }

    public String getOriginalId() {
        return originalId;
    }

    public void setOriginalId(String originalId) {
        this.originalId = originalId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<String> getRelatedContacts() {
        return relatedContacts;
    }

    public void setRelatedContacts(ArrayList<String> relatedContacts) {
        this.relatedContacts = relatedContacts;
    }

    public ArrayList<String> getRelatedItems() {
        return relatedItems;
    }

    public void setRelatedItems(ArrayList<String> relatedItems) {
        this.relatedItems = relatedItems;
    }

    public ArrayList<Integer> getPushToSources() {
        return pushToSources;
    }

    public void setPushToSources(ArrayList<Integer> pushToSources) {
        this.pushToSources = pushToSources;
    }

    public ArrayList<Integer> getUnpushFromSources() {
        return unpushFromSources;
    }

    public void setUnpushFromSources(ArrayList<Integer> unpushFromSources) {
        this.unpushFromSources = unpushFromSources;
    }

    public ArrayList<Integer> getPushedSources() {
        return pushedSources;
    }

    public void setPushedSources(ArrayList<Integer> pushedSources) {
        this.pushedSources = pushedSources;
    }
}
