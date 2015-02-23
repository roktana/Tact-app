package com.tactile.tact.services.events;

/**
 * Created by sebafonseca on 12/12/14.
 */
public class EventFilterContacts extends EventTactBase {
    public String filter;
    public EventFilterContacts(String filter){
        this.filter = filter;
    }
}
