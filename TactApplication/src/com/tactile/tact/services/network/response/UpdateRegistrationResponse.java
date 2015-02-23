package com.tactile.tact.services.network.response;

import com.tactile.tact.services.network.APIResponse;
import com.tactile.tact.services.network.APIResult;
import com.google.gson.annotations.SerializedName;


public class UpdateRegistrationResponse extends APIResponse {
    @SerializedName("ok")
    boolean ok;
        
    public boolean isSuccess() {
    	if(ok)
    		return true;
    	return false;
    }
    
    @Override
    public APIResult getResult() {
        return null;
    }
    
    public UpdateRegistrationResponse() {
    }
}
