package com.tactile.tact.services.events;

import com.tactile.tact.consts.TactConst;

/**
 * Created by leyan on 11/6/14.
 */
public class EventInitialSyncError extends EventTactBase {
    public EventInitialSyncError(boolean isCritical, int code, String message) {
        this.setError(new TactBaseError(isCritical, TactConst.TactError.NetworkingError, code, String.valueOf(code), message));
    }

    public EventInitialSyncError(TactBaseError error) {
        this.setError(error);
    }
}
