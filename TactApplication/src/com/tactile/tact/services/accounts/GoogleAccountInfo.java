package com.tactile.tact.services.accounts;

public class GoogleAccountInfo {
    static final String TAG = "GoogleAccountInfo";
    
    private static String accountFullName;
    private static String username;
    private static String googleId;
    private static String accessToken;
    private static String refreshToken;
    private static boolean sourceAdded;
    
    public static void setGoogleAccountInfo(String accountFullName,
    							String username,
                                String googleId,
    							String accessToken,
    							String refreshToken)
    {
    	GoogleAccountInfo.accountFullName = accountFullName;
    	GoogleAccountInfo.username = username;
        GoogleAccountInfo.googleId = googleId;
    	GoogleAccountInfo.accessToken = accessToken;
    	GoogleAccountInfo.refreshToken = refreshToken;
    	GoogleAccountInfo.sourceAdded = false;
    }
    
    public static String getUsername() { return GoogleAccountInfo.username; }
    public static String getAccessToken() { return GoogleAccountInfo.accessToken; }
    public static String getRefreshToken() { return GoogleAccountInfo.refreshToken; }
    public static String getGoogleId() { return GoogleAccountInfo.googleId; }
    
    public static boolean isAGoogleSourceAdded() {
    	return GoogleAccountInfo.sourceAdded;
    }
    
    public static void markAGoogleSourceAdded(boolean flag) {
    	GoogleAccountInfo.sourceAdded = flag;
    }

    public void resetGoogleAccountInfo(){
        GoogleAccountInfo.accountFullName = "";
        GoogleAccountInfo.username = "";
        GoogleAccountInfo.googleId = "";
        GoogleAccountInfo.accessToken = "";
        GoogleAccountInfo.refreshToken = "";
        GoogleAccountInfo.sourceAdded = false;
    }
    
}
