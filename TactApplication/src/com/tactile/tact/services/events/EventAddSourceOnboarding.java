package com.tactile.tact.services.events;

/**
 * Created by sebafonseca on 11/27/14.
 */
public class EventAddSourceOnboarding extends EventTactBase {

    public String source;
    public Integer source_id;

    public EventAddSourceOnboarding(String source, Integer source_id){
        this.source = source;
        this.source_id = source_id;
    }
}
