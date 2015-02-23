package com.tactile.tact.services.events;

import com.tactile.tact.consts.TactConst;

/**
 * Created by leyan on 10/27/14.
 */
public class EventTempLoginError extends EventTactBase {

    public EventTempLoginError(String message) {
        this.setError(new TactBaseError(true, TactConst.TactError.NetworkingError, -1, null, message));
    }
}
