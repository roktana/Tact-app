package com.tactile.tact.database.model;

import com.tactile.tact.consts.TactConst;

import java.io.Serializable;

/**
 * Created by leyan on 9/22/14.
 */
public class FeedItemEmailState implements Serializable {

    private static final long serialVersionUID = 3645745856875583172L;

    public FeedItemEmailState(int position, TactConst.EmailCurrentState state) {
        this.state = state;
        this.position = position;
    }

    private int position;
    private TactConst.EmailCurrentState state;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public TactConst.EmailCurrentState getState() {
        return state;
    }

    public void setState(TactConst.EmailCurrentState state) {
        this.state = state;
    }
}
