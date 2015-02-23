package com.tactile.tact.services.events;

import org.json.JSONObject;

/**
 * Created by sebafonseca on 10/30/14.
 */
public class EventContactsUploadDetailsAmazonSuccess extends EventTactBase {
    public String response;

    public EventContactsUploadDetailsAmazonSuccess(String response){
        this.response = response;
    }
}
