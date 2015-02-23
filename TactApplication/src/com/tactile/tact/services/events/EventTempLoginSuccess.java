package com.tactile.tact.services.events;

import com.tactile.tact.controllers.TactSharedPrefController;

import org.json.JSONObject;

import de.greenrobot.event.EventBus;

/**
 * Event to handle the success response from the temp login
 * Created by leyan on 10/27/14.
 */
public class EventTempLoginSuccess extends EventTactBase {
    private JSONObject response;

    //Constructor getting the JSON response
    public EventTempLoginSuccess(JSONObject response) {
        this.response = response;
        handleResponse();
    }

    /**
     * Handle the response, insert the uuid that the response returns and try getting the pusher channel
     */
    private void handleResponse() {
        try {
            //get the uuid from the response
            if (response != null && response.has("uuid")) {
                TactSharedPrefController.storeTempCredentials(TactSharedPrefController.getTempCredentialUsername(),
                        TactSharedPrefController.getTempCredentialPassword(),
                        response.getString("uuid"));
            }
            //try get the push channel, if fails do nothing coz we can get it later
            try {
                if (response != null && response.has("push_channel")) {
                    TactSharedPrefController.setPushChannel(response.getString("push_channel"));
                }
            } catch (Exception e) {
                //do nothing, we can get the channel later
            }
        } catch (Exception e) {
            //There was an error parsing the response json, trigger a error event for the register tem user call
            EventBus.getDefault().postSticky(new EventTempLoginError(e.getMessage()));
        }
    }

}
