package com.tactile.tact.services.events;

/**
 * Created by sebafonseca on 11/11/14.
 */
public class EventHandleProgress extends EventTactBase {

    private boolean showProgress;
    private boolean transparent = false;

    public EventHandleProgress(boolean showProgress, boolean transparent){
        this.showProgress = showProgress;
    }

    public boolean getShowProgress() {
        return showProgress;
    }

    public boolean isTransparent() {
        return transparent;
    }
}
