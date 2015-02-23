package com.tactile.tact.services.network.response;

import com.tactile.tact.services.network.APIResponse;
import com.tactile.tact.services.network.APIResult;
import com.google.gson.annotations.SerializedName;

public class ServerResponse extends APIResponse {

	public ServerResponse() {
	}
	
	@SerializedName("message")
	public String message;
	
	@SerializedName("success")
	public String success;
	
	@SerializedName("token")
	public String token;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	@Override
	public APIResult getResult() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
