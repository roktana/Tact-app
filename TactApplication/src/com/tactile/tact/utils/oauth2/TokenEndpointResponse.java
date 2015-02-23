package com.tactile.tact.utils.oauth2;

import android.util.Log;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Created by sebafonseca on 10/21/14.
 */
public class TokenEndpointResponse {
    public String authToken;
    public String refreshToken;
    public String instanceUrl;
    public String idUrl;
    public String idUrlWithInstance;
    public String orgId;
    public String userId;
    public String code;

    /**
     * Constructor used during login flow
     * @param callbackUrlParams
     */
    public TokenEndpointResponse(Map<String, String> callbackUrlParams) {
        try {
            authToken = callbackUrlParams.get("access_token");
            refreshToken = callbackUrlParams.get("refresh_token");
            instanceUrl = callbackUrlParams.get("instance_url");
            idUrl = callbackUrlParams.get("id");
            code = callbackUrlParams.get("code");
            computeOtherFields();
        } catch (Exception e) {
            Log.w("TokenEndpointResponse:contructor", "", e);
        }
    }

    private void computeOtherFields() throws URISyntaxException {
        idUrlWithInstance = idUrl.replace(new URI(idUrl).getHost(), new URI(instanceUrl).getHost());
        String[] idUrlFragments = idUrl.split("/");
        userId = idUrlFragments[idUrlFragments.length - 1];
        orgId = idUrlFragments[idUrlFragments.length - 2];
    }
}
