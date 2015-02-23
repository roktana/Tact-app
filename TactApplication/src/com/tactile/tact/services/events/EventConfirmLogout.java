package com.tactile.tact.services.events;

/**
 * Created by sebafonseca on 11/11/14.
 */
public class EventConfirmLogout extends EventTactBase
{
    private String message;

    public EventConfirmLogout(String message)
    {
        this.message = message;
    }

    public String getMessage()
    {
        return this.message;
    }
}
