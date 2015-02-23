package com.tactile.tact.services.network;

import com.google.gson.annotations.SerializedName;

public class ErrorResponse extends APIResponse {

    private APIResult result = null;

    @SerializedName("error_code")
    String error_code;

    @SerializedName("error_msg")
    String error_msg;

    @SerializedName("error_title")
    String error_title;

    public ErrorResponse(String errorMessage) {
        this.result = new ErrorResult(errorMessage);
    }

    public String getErrorMessage() {
        return error_msg;
    }

    @Override
    public APIResult getResult() {
        return this.result;
    }
	
}
