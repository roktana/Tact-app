package com.tactile.tact.database.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.tactile.tact.activities.TactApplication;
import com.tactile.tact.database.entities.*;
import com.tactile.tact.utils.FeedItemExclusionStrategy;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by leyan on 9/11/14.
 */
public class FeedItem implements Serializable{
    private static final long serialVersionUID = 3745745856285583172L;

    //we need a reference to the FrozenFeedItem that contains the item for relations references
    private transient long parentId;
    @SerializedName("id")
    private String serverId;
    @SerializedName("source_type")
    private String sourceType;
    @SerializedName("remote_id")
    private String remoteId;
    private Integer version;
    @SerializedName("source_id")
    private Integer sourceId;
    private Long timestamp;
    private FeedItemMetadata metadata;

    public void setValues(FeedItem feedItem) {
        this.setMetadata(feedItem.getMetadata());
        this.setTimestamp(feedItem.getTimestamp());
        this.setRemoteId(feedItem.getRemoteId());
        this.setServerId(feedItem.getServerId());
        this.setVersion(feedItem.getVersion());
        this.setSourceId(feedItem.getSourceId());
        this.setSourceType(feedItem.getSourceType());
    }

    public FeedItem getMasterFeedItem() {
        if (this.getParent().getMaster() != null) {
            return (FeedItem)this.getParent().getMaster().getFeedItem();
        }
        return null;
    }

    public String toJson() {
        try {
            if (this instanceof FeedItemEvent) {
                return ((FeedItemEvent)this).toJSON();
            } else if (this instanceof FeedItemLog) {
                return ((FeedItemLog)this).toJSON();
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public JSONObject feedItemToJSON() {
        try {
            Gson gson = new GsonBuilder().serializeNulls().create();
            return new JSONObject(gson.toJson(this));
        } catch (Exception e) {
            return null;
        }
    }

    public JSONObject feedItemToJSON(Class excludeClass) {
        try {
            Gson gson = new GsonBuilder().setExclusionStrategies(new FeedItemExclusionStrategy(excludeClass)).serializeNulls().create();
            return new JSONObject(gson.toJson(this));
        } catch (Exception e) {
            return null;
        }
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public FrozenFeedItem getParent() {
        return TactApplication.getInstance().daoSession.getFrozenFeedItemDao().load(this.getParentId());
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public FeedItemMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(FeedItemMetadata metadata) {
        this.metadata = metadata;
    }
}
