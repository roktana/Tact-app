package com.tactile.tact.database.model;

/**
 * Created by leyan on 11/20/14.
 */
public class SyncCUDLPacketMetadata {
    private int version;
    private Bounds bounds;
    private String requestId;
    private int totalPacket;
    private int currentPacket;

    public SyncCUDLPacketMetadata() {

    }

    public SyncCUDLPacketMetadata(int version, Bounds bounds, String requestId, int totalPacket, int currentPacket) {
        this.version = version;
        this.bounds = bounds;
        this.requestId = requestId;
        this.totalPacket = totalPacket;
        this.currentPacket = currentPacket;
    }

    public boolean checkPackageMetadataIntegrity() {
        if (this.getBounds() == null || this.getRequestId() == null || this.getRequestId().isEmpty()) {
           return false;
        }
        return true;
    }

    public boolean isLastPackage() {
        return this.currentPacket == this.totalPacket;
    }


    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Bounds getBounds() {
        return bounds;
    }

    public void setBounds(Bounds bounds) {
        this.bounds = bounds;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public int getTotalPacket() {
        return totalPacket;
    }

    public void setTotalPacket(int totalPacket) {
        this.totalPacket = totalPacket;
    }

    public int getCurrentPacket() {
        return currentPacket;
    }

    public void setCurrentPacket(int currentPacket) {
        this.currentPacket = currentPacket;
    }
}
