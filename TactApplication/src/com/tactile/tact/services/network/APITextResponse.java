package com.tactile.tact.services.network;

public class APITextResponse extends APIResponse {
	
	public APITextResponse(String responseText) {
		this.responseText = responseText;
	}
	
	public APIResult getResult() {
		return null;
	}

	public String responseText = null;
}
