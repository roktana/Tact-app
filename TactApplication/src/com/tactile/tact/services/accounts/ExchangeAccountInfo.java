package com.tactile.tact.services.accounts;

public class ExchangeAccountInfo {
    static final String TAG = "ExchangeAccountInfo";
    
    private static String hostname;
    private static String domain;
    private static String email;
    private static String username;
    private static String password;
    private static boolean skipSSLVerify;
    private static boolean sourceAdded;
    
    public static void setExchangeAccountInfo(String hostname,
    							String domain,
    							String email,
    							String username,
    							String password,
    							boolean  skipSSLVerify)
    {
        if(hostname != null)
    	    ExchangeAccountInfo.hostname = hostname;
    	else
            ExchangeAccountInfo.hostname = "";

        if(domain != null)
            ExchangeAccountInfo.domain = domain;
        else
            ExchangeAccountInfo.domain = "";

        if(email != null)
            ExchangeAccountInfo.email = email;
        else
            ExchangeAccountInfo.email = "";

        if(username != null)
            ExchangeAccountInfo.username = username;
        else
            ExchangeAccountInfo.username = "";

        if(password != null)
            ExchangeAccountInfo.password = password;
        else
            ExchangeAccountInfo.password = "";

    	ExchangeAccountInfo.skipSSLVerify = skipSSLVerify;
    	ExchangeAccountInfo.sourceAdded = false;
    }
    
    public static boolean isAnExchangeSourceAdded() {
    	return ExchangeAccountInfo.sourceAdded;
    }
    
    public static void markAnExchangeSourceAdded(boolean flag) {
    	ExchangeAccountInfo.sourceAdded = flag;
    }

    public static String getHostname(){
        return ExchangeAccountInfo.hostname;
    }

    public static String getDomain(){
        return ExchangeAccountInfo.domain;
    }

    public static String getEmail(){
        return ExchangeAccountInfo.email;
    }
    public static String getUsername(){
        return ExchangeAccountInfo.username;
    }
    public static String getPassword(){
        return ExchangeAccountInfo.password;
    }

    public static boolean isSkipSSLVerify(){
        return ExchangeAccountInfo.skipSSLVerify;
    }
}
