package com.tactile.tact.services.events;

import com.tactile.tact.database.model.FragmentMoveHandler;

/**
 * Created by leyan on 12/9/14.
 */
public class EventMoveToFragment extends EventTactBase {

    private FragmentMoveHandler fragmentMoveHandler;
    private boolean isBack;

    public EventMoveToFragment(FragmentMoveHandler fragmentMoveHandler) {
        this.setFragmentMoveHandler(fragmentMoveHandler);
    }

    public EventMoveToFragment(FragmentMoveHandler fragmentMoveHandler, boolean isBack) {
        this.setFragmentMoveHandler(fragmentMoveHandler);
        this.isBack = isBack;
    }

    public EventMoveToFragment(int fragmentTag, int position, long time, Object object, int page, boolean isPrimary) {
        this.fragmentMoveHandler = new FragmentMoveHandler();
        this.fragmentMoveHandler.setFragmentTag(fragmentTag);
        this.fragmentMoveHandler.setObject(object);
        this.fragmentMoveHandler.setPosition(position);
        this.fragmentMoveHandler.setTime(time);
        this.fragmentMoveHandler.setPrimary(isPrimary);
    }

    public EventMoveToFragment(int fragmentTag, int position, long time, Object object, int page) {
        this.fragmentMoveHandler = new FragmentMoveHandler();
        this.fragmentMoveHandler.setFragmentTag(fragmentTag);
        this.fragmentMoveHandler.setObject(object);
        this.fragmentMoveHandler.setPosition(position);
        this.fragmentMoveHandler.setTime(time);
        this.fragmentMoveHandler.setPrimary(false);
    }

    public FragmentMoveHandler getFragmentMoveHandler() {
        return fragmentMoveHandler;
    }

    public void setFragmentMoveHandler(FragmentMoveHandler fragmentMoveHandler) {
        this.fragmentMoveHandler = fragmentMoveHandler;
    }

    public boolean isBack() {
        return isBack;
    }
}
