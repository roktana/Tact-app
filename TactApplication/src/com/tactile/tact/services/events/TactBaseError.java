package com.tactile.tact.services.events;

import com.tactile.tact.consts.TactConst;

import java.io.Serializable;

/**
 * Created by leyan on 10/27/14.
 */
public class TactBaseError implements Serializable {

    private static final long serialVersionUID = 8115733996285583172L;
    private boolean isCritical = false;
    private TactConst.TactError type;
    private String message;
    private String codeString;
    private int codeInt;

    public TactBaseError() {

    }

    public TactBaseError(boolean isCritical, TactConst.TactError type, int codeInt, String codeString, String message) {
        this.isCritical = isCritical;
        this.type = type;
        this.codeInt = codeInt;
        this.codeString = codeString;
        this.message = message;
    }


    public boolean isCritical() {
        return isCritical;
    }

    public void setCritical(boolean isCritical) {
        this.isCritical = isCritical;
    }

    public TactConst.TactError getType() {
        return type;
    }

    public void setType(TactConst.TactError type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCodeString() {
        return codeString;
    }

    public void setCodeString(String codeString) {
        this.codeString = codeString;
    }

    public int getCodeInt() {
        return codeInt;
    }

    public void setCodeInt(int codeInt) {
        this.codeInt = codeInt;
    }
}
