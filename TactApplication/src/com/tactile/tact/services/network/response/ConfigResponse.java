package com.tactile.tact.services.network.response;

import com.google.gson.annotations.SerializedName;
import com.tactile.tact.services.network.APIResponse;
import com.tactile.tact.services.network.APIResult;

/**
 * Created by leyan on 8/15/14.
 */
public class ConfigResponse extends APIResponse {

    @SerializedName("bcc_email_domain")
    private String bbcEmailDomain;

    @SerializedName("pusher_key")
    private String pusherAPIKey;

    public void setBbcEmailDomain(String bbcEmailDomain) {
        this.bbcEmailDomain = bbcEmailDomain;
    }

    public String getBbcEmailDomain() {
        return this.bbcEmailDomain;
    }

    public void setPusherAPIKey(String pusherAPIKey) {
        this.pusherAPIKey = pusherAPIKey;
    }

    public String getPusherAPIKey() {
        return this.pusherAPIKey;
    }

    @Override
    public APIResult getResult() {
        // TODO Auto-generated method stub
        return null;
    }
}
