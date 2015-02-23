package com.tactile.tact.services.events;

/**
 * Created by sebafonseca on 10/27/14.
 */
public class EventMoveNextFragmentOnboarding extends EventTactBase {

    Object[] args;

    public EventMoveNextFragmentOnboarding(Object ...args){
        this.args = args;
    }

    public Object[] getArgs(){
        return args;
    }
}
