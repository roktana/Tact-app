package com.tactile.tact.services.events;

import com.tactile.tact.database.entities.FrozenFeedItem;
import com.tactile.tact.database.model.Bounds;

import java.util.ArrayList;

/**
 * Created by leyan on 1/22/15.
 */
public class EventSyncCUDLProcessPackage extends EventTactBase {
    private long packageTimestamp;
    private ArrayList<FrozenFeedItem> items;
    private int packageNumber;
    private boolean isLastPackage;
    private Bounds bounds;
    private Integer version;
    private Integer totalPackageCount;

    public EventSyncCUDLProcessPackage(long packageTimestamp, ArrayList<FrozenFeedItem> items, int packageNumber, boolean isLastPackage, Bounds bounds, Integer version, Integer totalPackageCount) {
        this.setPackageTimestamp(packageTimestamp);
        this.setPackageNumber(packageNumber);
        this.setItems(items);
        this.setLastPackage(isLastPackage);
        this.setBounds(bounds);
        this.setVersion(version);
        this.totalPackageCount = totalPackageCount;
    }

    public long getPackageTimestamp() {
        return packageTimestamp;
    }

    public void setPackageTimestamp(long packageTimestamp) {
        this.packageTimestamp = packageTimestamp;
    }

    public ArrayList<FrozenFeedItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<FrozenFeedItem> items) {
        this.items = items;
    }

    public int getPackageNumber() {
        return packageNumber;
    }

    public void setPackageNumber(int packageNumber) {
        this.packageNumber = packageNumber;
    }

    public boolean isLastPackage() {
        return isLastPackage;
    }

    public void setLastPackage(boolean isLastPackage) {
        this.isLastPackage = isLastPackage;
    }

    public Bounds getBounds() {
        return bounds;
    }

    public void setBounds(Bounds bounds) {
        this.bounds = bounds;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getTotalPackageCount() {
        return totalPackageCount;
    }

    public void setTotalPackageCount(Integer totalPackageCount) {
        this.totalPackageCount = totalPackageCount;
    }
}
