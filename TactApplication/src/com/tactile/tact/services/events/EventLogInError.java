package com.tactile.tact.services.events;

import com.tactile.tact.consts.TactConst;

/**
 * Created by leyan on 10/31/14.
 */
public class EventLogInError extends EventTactBase {

    public EventLogInError(boolean isCritical, int code, String message) {
        this.setError(new TactBaseError(isCritical, TactConst.TactError.NetworkingError, code, String.valueOf(code), message));
    }
}
