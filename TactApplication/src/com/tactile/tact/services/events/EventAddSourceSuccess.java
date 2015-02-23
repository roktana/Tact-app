package com.tactile.tact.services.events;

import org.json.JSONArray;

/**
 * Created by sebafonseca on 10/28/14.
 */
public class EventAddSourceSuccess extends EventTactBase {

    public JSONArray response;

    public EventAddSourceSuccess(JSONArray response) {
        this.response = response;
    }
}
