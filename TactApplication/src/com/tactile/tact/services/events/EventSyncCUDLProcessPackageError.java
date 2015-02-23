package com.tactile.tact.services.events;

/**
 * Created by leyan on 1/22/15.
 */
public class EventSyncCUDLProcessPackageError extends EventTactBase {
    private String errorMessage;

    public EventSyncCUDLProcessPackageError(String errorMessage) {
        this.setErrorMessage(errorMessage);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
