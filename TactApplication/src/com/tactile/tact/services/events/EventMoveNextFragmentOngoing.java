package com.tactile.tact.services.events;

/**
 * Created by sebafonseca on 10/27/14.
 */
public class EventMoveNextFragmentOngoing extends EventTactBase {

    Object[] args;

    public EventMoveNextFragmentOngoing(Object... args){
        this.args = args;
    }

    public Object[] getArgs(){
        return args;
    }
}
