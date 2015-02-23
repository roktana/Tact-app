package com.tactile.tact.services.network.response;

import java.util.List;

import com.tactile.tact.services.network.APIResponse;
import com.tactile.tact.services.network.APIResult;
import com.google.gson.annotations.SerializedName;


public class LoginResponse extends APIResponse {
    @SerializedName("current_onboarding_state")
    String current_onboarding_state;

    @SerializedName("app_version")
    int app_version;
    
    @SerializedName("revision")
    int revision;
    
    @SerializedName("uuid")
    String uuid;

    @SerializedName("initial_db_current")
    boolean initial_db_current;

    @SerializedName("enable_extended_search")
    boolean enable_extended_search;

    @SerializedName("logged_items_count")
    int logged_items_count;

    @SerializedName("all_emails")
    List<String> all_emails;
    
    @SerializedName("created_at")
    String created_at;
    
    @SerializedName("push_channel")
    String push_channel;
    
    public boolean isAlreadyOnboarded() {
        return (current_onboarding_state.contains("initial_db_finished"));
    }
    
    public boolean isSuccess() {
    	if(uuid != null && uuid.length() > 0 )
    		return true;
    	return false;
    }
    
    @Override
    public APIResult getResult() {
        return null;
    }

    public String getPushChannel() {
        return push_channel;
    }

    public LoginResponse() {
    }
}
