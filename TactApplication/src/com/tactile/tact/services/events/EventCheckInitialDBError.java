package com.tactile.tact.services.events;

import com.tactile.tact.consts.TactConst;

import java.io.Serializable;

/**
 * Created by leyan on 10/28/14.
 */
public class EventCheckInitialDBError extends EventTactBase implements Serializable {

    private static final long serialVersionUID = 3745142256283583172L;

    private boolean isJustCheck = false;

    public EventCheckInitialDBError(boolean isJustCheck, boolean isCritical, int code, String message) {
        this.setError(new TactBaseError(isCritical, TactConst.TactError.NetworkingError, code, String.valueOf(code), message));
        this.isJustCheck = isJustCheck;
    }

    public boolean isJustCheck() {
        return isJustCheck;
    }

}
