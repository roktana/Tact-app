package com.tactile.tact.services.events;

/**
 * Created by leyan on 11/6/14.
 */
public class EventInitialSyncFinished extends EventTactBase {
    private String url;

    public EventInitialSyncFinished(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
