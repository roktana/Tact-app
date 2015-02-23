package com.tactile.tact.services.events;

/**
 * Created by leyan on 11/24/14.
 */
public class EventCUDLSyncError extends EventTactBase {
    private int errorType;

    public EventCUDLSyncError(int errorType) {
        this.errorType = errorType;
    }

    public int getErrorType() {
        return errorType;
    }
}
