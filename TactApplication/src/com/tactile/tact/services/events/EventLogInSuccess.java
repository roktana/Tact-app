package com.tactile.tact.services.events;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tactile.tact.controllers.TactSharedPrefController;
import com.tactile.tact.services.network.response.LoginResponse;

import org.json.JSONObject;

import de.greenrobot.event.EventBus;

/**
 * Created by leyan on 10/31/14.
 */
public class EventLogInSuccess extends EventTactBase {

    LoginResponse loginResponse;

    public EventLogInSuccess(String username, String password, JSONObject response) {
        TactSharedPrefController.storeCredentials(username, password);
        if (response != null) {
            Gson gson = new Gson();
            this.loginResponse = gson.fromJson(response.toString(), LoginResponse.class);
        }

    }
}
