package com.tactile.tact.services.events;

/**
 * Created by sebafonseca on 10/31/14.
 */
public class EventTactRegistrationSuccess extends EventTactBase {

    public Object response;

    public EventTactRegistrationSuccess(Object jsonObject){
        this.response = jsonObject;
    }
}
