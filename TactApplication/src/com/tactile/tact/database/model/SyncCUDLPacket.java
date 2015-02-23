package com.tactile.tact.database.model;

import com.tactile.tact.database.entities.FrozenFeedItem;
import com.tactile.tact.utils.Utils;

import java.util.ArrayList;

/**
 * Created by leyan on 11/20/14.
 */
public class SyncCUDLPacket {
    private ArrayList<FrozenFeedItem> FrozenFeedItems;
    private Long latestTimestamp = new Long(0);

    public void addFrozenFeedItem(FrozenFeedItem item) {
        if (this.FrozenFeedItems == null) {
            this.FrozenFeedItems = new ArrayList<FrozenFeedItem>();
        }
        this.FrozenFeedItems.add(item);
    }

    public void checkLatestTimestamp(Long timestamp) {
        if (this.latestTimestamp == null || this.latestTimestamp == 0) {
            setLatestTimestamp(timestamp);
        } else {
            if (getLatestTimestamp() < timestamp) {
                setLatestTimestamp(timestamp);
            }
//            if (Utils.getDateFromTimestamp(getLatestTimestamp()).before(Utils.getDateFromTimestamp(timestamp))) {
//                setLatestTimestamp(timestamp);
//            }
        }
    }

    public ArrayList<FrozenFeedItem> getFrozenFeedItems() {
        return FrozenFeedItems;
    }

    public void setFrozenFeedItems(ArrayList<FrozenFeedItem> frozenFeedItems) {
        FrozenFeedItems = frozenFeedItems;
    }

    public Long getLatestTimestamp() {
        return latestTimestamp;
    }

    public void setLatestTimestamp(Long latestTimestamp) {
        this.latestTimestamp = latestTimestamp;
    }
}
