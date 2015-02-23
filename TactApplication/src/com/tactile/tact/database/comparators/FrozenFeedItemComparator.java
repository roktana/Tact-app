package com.tactile.tact.database.comparators;

import com.tactile.tact.database.entities.FrozenFeedItem;
import com.tactile.tact.utils.Utils;

import java.util.Comparator;

/**
 * Created by leyan on 9/15/14.
 */
public class FrozenFeedItemComparator implements Comparator<FrozenFeedItem> {
    @Override
    public int compare(FrozenFeedItem o1, FrozenFeedItem o2) {
        return Utils.getDateFromTimestamp(o2.getTimestamp()).compareTo(Utils.getDateFromTimestamp(o1.getTimestamp()));
    }
}
