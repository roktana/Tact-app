package com.tactile.tact.services.network.response;

import com.google.gson.annotations.SerializedName;
import com.tactile.tact.services.network.APIResponse;
import com.tactile.tact.services.network.APIResult;

/**
 * Created by admin on 8/6/14.
 */
public class DeviceContactsUploadDataSourceResponse extends APIResponse {

    @SerializedName("AWSAccessKeyId")
    String accessKeyId;

    @SerializedName("key")
    String key;

    @SerializedName("policy")
    String policy;

    @SerializedName("signature")
    String signature;

    @SerializedName("Secure")
    String secure;

    @SerializedName("success_action_status")
    String successActionStatus;

    @SerializedName("x-amz-security-token")
    String securityToken;

    @SerializedName("url")
    String url;

    @Override
    public APIResult getResult() {
        return null;
    }

    public String getAccessKeyId(){
        return this.accessKeyId;
    }

    public String getKey(){
        return this.key;
    }

    public String getPolicy(){
        return this.policy;
    }

    public String getSignature(){
        return this.signature;
    }

    public String getSecure(){
        return this.secure;
    }

    public String getSuccessActionStatus(){
        return this.successActionStatus;
    }

    public String getSecurityToken() {
        return this.securityToken;
    }

    public String getUrl(){
        return this.url;
    }

}
