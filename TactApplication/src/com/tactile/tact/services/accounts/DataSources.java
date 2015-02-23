package com.tactile.tact.services.accounts;

public class DataSources {
    static final String TAG = "DataSources";
    
    private static DataSources instance;
    
    // Global variable
    private static ExchangeAccountInfo mExchangeInfo;
    private static GoogleAccountInfo mGoogleAccountInfo;
    private static SalesforceAccountInfo mSaleforceAccountInfo;
    
    // Restrict the constructor from being instantiated
    private DataSources(){
    	mExchangeInfo           = new ExchangeAccountInfo();
    	mGoogleAccountInfo      = new GoogleAccountInfo();
    	mSaleforceAccountInfo   = new SalesforceAccountInfo();
    }

    public static synchronized DataSources getInstance(){
      if(instance==null){
        instance=new DataSources();
      }
      return instance;
    }
    
    public void setExchangeAccountInfo(
    		String hostname,
			String domain,
			String email,
			String username,
			String password,
			boolean skipSSLVerify){
    	mExchangeInfo.setExchangeAccountInfo(hostname, domain, email, username, password, skipSSLVerify);
    }
    
    public ExchangeAccountInfo getExchangeAccountInfoHandle(){
      return this.mExchangeInfo;
    }
    
    public void setSalesforceAccountInfo(
    		String salesForceID,
			String accessToken,
			String refreshToken,
			String instanceURL){
    	mSaleforceAccountInfo.setSalesforceAccountInfo(salesForceID, accessToken, refreshToken, instanceURL);
    }
    
    public SalesforceAccountInfo getSalesforceAccountInfoHandle(){
      return this.mSaleforceAccountInfo;
    }
    
    public void setGoogleAccountInfo(
    		String accountFullName,
    		String username,
            String googleId,
			String accessToken,
			String refreshToken){
    	mGoogleAccountInfo.setGoogleAccountInfo(accountFullName, username, googleId, accessToken, refreshToken);
    }
    
    public GoogleAccountInfo getGoogleAccountInfoHandle(){
      return this.mGoogleAccountInfo;
    }
}
