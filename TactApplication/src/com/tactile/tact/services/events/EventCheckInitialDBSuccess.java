package com.tactile.tact.services.events;


import com.google.gson.Gson;
import com.tactile.tact.controllers.TactSharedPrefController;
import com.tactile.tact.services.network.response.DataSourceAddedResponse;

import org.json.JSONObject;

import de.greenrobot.event.EventBus;

/**
 * Handle the response from the checkDatabase call
 * Created by leyan on 10/28/14.
 */
public class EventCheckInitialDBSuccess extends EventTactBase {
    private JSONObject response;
    //Says that this request is just a check to the server, don't care about the response
    private boolean isJustCheck = false;
    private DataSourceAddedResponse objectResponse;

    public EventCheckInitialDBSuccess(JSONObject response) {
        this.response = response;
        handleResponse();
    }

    public EventCheckInitialDBSuccess(JSONObject response, boolean isJustCheck) {
        this.response = response;
        this.setJustCheck(isJustCheck);
        handleResponse();
    }

    /**
     * Handle the web service response
     */
    private void handleResponse() {
        try {
            if (response != null) {
                Gson gson = new Gson();
                DataSourceAddedResponse objectResponse = gson.fromJson(response.toString(), DataSourceAddedResponse.class);
                this.setObjectResponse(objectResponse);
                //try get the push channel if not exist already
                try {
                    if (objectResponse != null && objectResponse.getPushChannel() != null && !objectResponse.getPushChannel().isEmpty() && !TactSharedPrefController.isPushChannel()) {
                        TactSharedPrefController.setPushChannel(objectResponse.getPushChannel());
                    }
                } catch (Exception e) {
                    //do nothing, we can get the channel later
                }
            }
        } catch (Exception e) {
            EventBus.getDefault().postSticky(new EventCheckInitialDBError(true, false, 0, e.getMessage()));
        }
    }

    public DataSourceAddedResponse getObjectResponse() {
        return objectResponse;
    }

    public void setObjectResponse(DataSourceAddedResponse objectResponse) {
        this.objectResponse = objectResponse;
    }

    public boolean isJustCheck() {
        return isJustCheck;
    }

    public void setJustCheck(boolean isJustCheck) {
        this.isJustCheck = isJustCheck;
    }
}
