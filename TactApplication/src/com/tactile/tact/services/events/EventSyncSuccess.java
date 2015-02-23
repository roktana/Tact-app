package com.tactile.tact.services.events;

import com.google.gson.Gson;
import com.tactile.tact.database.model.SyncResponse;

import org.json.JSONObject;

/**
 * Created by leyan on 12/1/14.
 */
public class EventSyncSuccess extends EventTactBase {
    private SyncResponse syncResponse;

    public EventSyncSuccess(JSONObject jsonObject) {
        try {
            if (jsonObject != null) {
                Gson gson = new Gson();
                setSyncResponse(gson.fromJson(jsonObject.toString(), SyncResponse.class));
            }
        } catch (Exception e) {

        }
    }

    public SyncResponse getSyncResponse() {
        return syncResponse;
    }

    public void setSyncResponse(SyncResponse syncResponse) {
        this.syncResponse = syncResponse;
    }
}
