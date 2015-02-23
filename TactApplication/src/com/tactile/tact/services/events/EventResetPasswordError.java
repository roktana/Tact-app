package com.tactile.tact.services.events;

import com.tactile.tact.consts.TactConst;

/**
 * Created by leyan on 10/31/14.
 */
public class EventResetPasswordError extends EventTactBase {
    public EventResetPasswordError(boolean isCritical, int code, String message) {
        this.setError(new TactBaseError(isCritical, TactConst.TactError.NetworkingError, code, String.valueOf(code), message));
    }
}
