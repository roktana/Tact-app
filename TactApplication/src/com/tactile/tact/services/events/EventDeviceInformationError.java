package com.tactile.tact.services.events;

import com.tactile.tact.consts.TactConst;

/**
 * Created by sebafonseca on 10/30/14.
 */
public class EventDeviceInformationError extends EventTactBase {

    public EventDeviceInformationError(boolean isCritical, int code, String message) {
        this.setError(new TactBaseError(isCritical, TactConst.TactError.NetworkingError, code, String.valueOf(code), message));
    }
}
