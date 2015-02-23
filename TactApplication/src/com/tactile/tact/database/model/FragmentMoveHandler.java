package com.tactile.tact.database.model;

import java.util.Calendar;

/**
 * Created by leyan on 12/10/14.
 */
public class FragmentMoveHandler {
    
    private boolean isPrimary;
    private int fragmentTag;
    private int position = 0;
    private long time = Calendar.getInstance().getTimeInMillis();
    private Object object = null;
    private int page = 0;
    private int next_week_range = 2;
    private int back_week_range = 2;
    private String text;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public int getFragmentTag() {
        return fragmentTag;
    }

    public void setFragmentTag(int fragmentTag) {
        this.fragmentTag = fragmentTag;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getNext_week_range() {
        return next_week_range;
    }

    public void setNext_week_range(int next_week_range) {
        this.next_week_range = next_week_range;
    }

    public int getBack_week_range() {
        return back_week_range;
    }

    public void setBack_week_range(int back_week_range) {
        this.back_week_range = back_week_range;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
