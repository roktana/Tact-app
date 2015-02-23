package com.tactile.tact.services.events;

import com.google.gson.Gson;
import com.tactile.tact.services.network.response.ConfigResponse;

import org.json.JSONObject;

import de.greenrobot.event.EventBus;

/**
 * Created by leyan on 11/17/14.
 */
public class EventConfigSuccess extends EventTactBase {
    private JSONObject response;
    private String pusherAPIKey;

    public EventConfigSuccess(JSONObject response) {
        this.response = response;
        handleResponse();
    }

    public String getPusherAPIKey() {
        return this.pusherAPIKey;
    }

    /**
     * Handle the web service response
     */
    private void handleResponse() {
        try {
            if (response != null) {
                Gson gson = new Gson();
                ConfigResponse objectResponse = gson.fromJson(response.toString(), ConfigResponse.class);
                if (objectResponse != null && objectResponse.getPusherAPIKey() != null && !objectResponse.getPusherAPIKey().isEmpty()) {
                    this.pusherAPIKey = objectResponse.getPusherAPIKey();
                } else {
                    EventBus.getDefault().post(new EventConfigError(true, false, 0, "No pusher key in Config call"));
                }
            } else {
                EventBus.getDefault().post(new EventConfigError(true, false, 0, "Null response in Config call"));
            }
        } catch (Exception e) {
            EventBus.getDefault().post(new EventConfigError(true, false, 0, e.getMessage()));
        }
    }
}
