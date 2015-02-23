package com.tactile.tact.services.events;

/**
 * Created by leyan on 1/30/15.
 */
public class EventContactSyncError extends EventTactBase {
    private String errorMessage;

    public EventContactSyncError(String errorMessage) {
        this.setErrorMessage(errorMessage);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
