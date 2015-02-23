package com.tactile.tact.services.events;

/**
 * Created by sebafonseca on 10/28/14.
 */
public class EventUnZipError extends EventTactBase {

    public String error_msg;
    public EventUnZipError(String msg) {
        this.error_msg = msg;
    }
}
