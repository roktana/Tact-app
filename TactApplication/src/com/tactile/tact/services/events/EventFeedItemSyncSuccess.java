package com.tactile.tact.services.events;

import com.google.gson.Gson;
import com.tactile.tact.database.model.FeedItemSyncResponse;

import org.json.JSONObject;

/**
 * Created by leyan on 11/20/14.
 */
public class EventFeedItemSyncSuccess extends EventTactBase {

    private FeedItemSyncResponse feedItemSyncResponse;

    public EventFeedItemSyncSuccess(JSONObject jsonObject) {
        try {
            if (jsonObject != null) {
                Gson gson = new Gson();
                this.setFeedItemSyncResponse(gson.fromJson(jsonObject.toString(), FeedItemSyncResponse.class));
            }
        } catch (Exception e) {

        }
    }

    public FeedItemSyncResponse getFeedItemSyncResponse() {
        return feedItemSyncResponse;
    }

    public void setFeedItemSyncResponse(FeedItemSyncResponse feedItemSyncResponse) {
        this.feedItemSyncResponse = feedItemSyncResponse;
    }
}
