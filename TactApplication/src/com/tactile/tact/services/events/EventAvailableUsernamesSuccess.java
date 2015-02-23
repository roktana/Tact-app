package com.tactile.tact.services.events;

/**
 * Created by sebafonseca on 12/18/14.
 */
public class EventAvailableUsernamesSuccess extends EventTactBase{
    public Object response;

    public EventAvailableUsernamesSuccess(Object jsonObject){
        this.response = jsonObject;
    }
}


