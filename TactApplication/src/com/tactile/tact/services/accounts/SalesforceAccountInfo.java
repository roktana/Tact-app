package com.tactile.tact.services.accounts;

import java.util.concurrent.atomic.AtomicBoolean;

public class SalesforceAccountInfo {
    static final String TAG = "SalesforceAccountInfo";
    
    private static String salesForceID;
    private static String accessToken;
    private static String refreshToken;
    private static String instanceURL;
    private static AtomicBoolean sourceAdded;
    
    public void setSalesforceAccountInfo(String salesForceID,
    							String accessToken,
    							String refreshToken,
    							String instanceURL)
    {
        SalesforceAccountInfo.salesForceID = salesForceID;
        SalesforceAccountInfo.accessToken = accessToken;
        SalesforceAccountInfo.refreshToken = refreshToken;
        SalesforceAccountInfo.instanceURL = instanceURL;
        SalesforceAccountInfo.sourceAdded = new AtomicBoolean(false);
    }

    public static String getInstanceURL() { return SalesforceAccountInfo.instanceURL; }
    public static String getAccessToken() { return SalesforceAccountInfo.accessToken; }
    public static String getRefreshToken() { return SalesforceAccountInfo.refreshToken; }
    public static String getSalesForceID() {return  SalesforceAccountInfo.salesForceID;}

    public static boolean isASalesforceSourceAdded() {
        if(SalesforceAccountInfo.sourceAdded != null)
            return SalesforceAccountInfo.sourceAdded.get();
        return false;
    }

    public static void markASalesforceSourceAdded() {
        SalesforceAccountInfo.sourceAdded = new AtomicBoolean(true);
    }
}
