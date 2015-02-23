package com.tactile.tact.services.events;

import org.json.JSONObject;

/**
 * Created by sebafonseca on 10/30/14.
 */
public class EventContactsUploadDetailsSuccess extends EventTactBase {
    public JSONObject response;

    public EventContactsUploadDetailsSuccess(JSONObject response){
        this.response = response;
    }
}
