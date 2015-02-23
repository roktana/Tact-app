package com.tactile.tact.services.events;

/**
 * Created by leyan on 10/27/14.
 */
public class EventTactBase {
    private TactBaseError error;
    private boolean shouldModifyUi = false;

    public EventTactBase() {

    }

    public EventTactBase(TactBaseError error) {
        this.setError(error);
    }

    public void handleError() {
        if (this.getError() != null) {
            switch (this.getError().getType()) {
                case NetworkingError:
            }
        }
    }

    public TactBaseError getError() {
        return error;
    }

    public void setError(TactBaseError error) {
        this.error = error;
    }

    public boolean isShouldModifyUi() {
        return shouldModifyUi;
    }

    public void setShouldModifyUi(boolean shouldModifyUi) {
        this.shouldModifyUi = shouldModifyUi;
    }
}
