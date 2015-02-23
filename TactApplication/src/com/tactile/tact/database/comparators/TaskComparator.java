package com.tactile.tact.database.comparators;

import com.tactile.tact.database.entities.FrozenFeedItem;

import java.util.Comparator;

/**
 * Created by admin on 9/30/14.
 */
public class TaskComparator implements Comparator<FrozenFeedItem> {
    @Override
    public int compare(FrozenFeedItem zfrozenfeeditem, FrozenFeedItem zfrozenfeeditem2) {
        return zfrozenfeeditem.getTimestamp().compareTo(zfrozenfeeditem2.getTimestamp());
    }
}
