package com.tactile.tact.services.events;

/**
 * Event that triggers when no connection found
 * Created by leyan on 10/27/14.
 */
public class EventNoInternet {

    private boolean isCritical = false;

    public EventNoInternet(boolean isCritical) {
        this.isCritical = isCritical;
    }

    public boolean isCritical() {
        return this.isCritical;
    }
}
