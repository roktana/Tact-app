package com.tactile.tact.services.events;

import com.tactile.tact.consts.TactConst;

/**
 * Created by leyan on 12/1/14.
 */
public class EventSyncError extends EventTactBase {
    public EventSyncError(boolean isCritical, int code, String message) {
        this.setError(new TactBaseError(isCritical, TactConst.TactError.NetworkingError, code, String.valueOf(code), message));
    }
}
