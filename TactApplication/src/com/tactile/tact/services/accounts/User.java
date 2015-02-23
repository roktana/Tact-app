package com.tactile.tact.services.accounts;

import android.util.Base64;

import com.tactile.tact.activities.TactApplication;
import com.tactile.tact.consts.TactConst;
import com.tactile.tact.utils.Utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class User {
//    static final String TAG = "User";
//
//    private static User instance;
//
//    // Global variable
//    private static boolean isLoggedIn;
//    private static String basicAuth;
//    private static String uuid;
//
//    private static int lastRevisionSynced;
//
//    private static String cudlPushChannel;
//    private static String cudlVersion;
//    private static String cudlTimestamp;
//
//    private static boolean remindMeLaterForSalesforce;
//    private static boolean remindMeLaterForGoogle;
//    private static boolean remindMeLaterForExchange;
//    private static boolean remindMeLaterForLinkedIn;
//
//    private static boolean pendingUpdate;
//
//    private static DataSources datasources;
//    private static LocalOnboardingState localOnboardingState;
//    private static File mInitialDBFolderPath = null;
//    private static File mInitialDBFile = null;
//    private static final String mInitialDBFileName = "tact_db.zip";
//
//    private static String pushChannel = null;
//
//    // Restrict the constructor from being instantiated
//    private User() {
//        remindMeLaterForSalesforce = false;
//        remindMeLaterForGoogle = false;
//        remindMeLaterForExchange = false;
//        remindMeLaterForLinkedIn = false;
//
//        datasources = DataSources.getInstance();
//        uuid = "";
//        isLoggedIn = false;
//    }
//
//    public static synchronized User getInstance() {
//        if (instance == null) {
//            instance = new User();
//        }
//        return instance;
//    }
//
//    public void initializeDatabaseFolders(String dataDir) {
//        mInitialDBFolderPath = new File(dataDir + "/databases");
//        //create if necessary
//        if(!(mInitialDBFolderPath.exists() && mInitialDBFolderPath.isDirectory())) {
//            mInitialDBFolderPath.mkdir();
//        }
//        mInitialDBFile = new File(mInitialDBFolderPath, mInitialDBFileName);
//    }
//
//    public File getInitialDBFolderPath() { return User.mInitialDBFolderPath;}
//    public File getInitialDBFile() { return User.mInitialDBFile;}
//    public String getInitialDBFileName() { return User.mInitialDBFileName;}
//
//    public static DataSources getDataSources() {
//        return datasources.getInstance();
//    }
//
//    public void resetUserCredentials(String email, String password) {
//        String credentials = email + ":" + password;
////        TactApplication.getInstance().User(email, password);
//        User.basicAuth = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
//        Utils.storeLoginCredentials(User.basicAuth);
//    }
//
//    public static Map<String, String> getBasicAuthHeaders() {
//        Map<String, String> headers = new HashMap<String, String>();
//        headers.put("Authorization", "Basic " + User.getBase64EncodedCredentials());
//        return headers;
//    }
//
//    public static String getBase64EncodedCredentials() {
//        if (User.basicAuth != null && !User.basicAuth.equals("")) {
//            return User.basicAuth;
//        }
//        return Utils.retrieveLoginCredentials();
//    }
//
//    public static void setUUID(String uuid) {
//    	User.uuid = uuid;
//    }
//
//    public static String getUUID(){
//        return User.uuid;
//    }
//
//    public boolean isLoggedIn() {
//        return Utils.retrieveLoggedInState();
//    }
//
//    public void markLogInStatus(boolean status)
//    {
//        User.isLoggedIn = status;
//        Utils.storeLoggedInState(status);
//    }
//
//    public static LocalOnboardingState.TactUserLocalOnboardingState getLocalOnboardingState() {
//        return User.localOnboardingState.getCurrentLocalOnboardingState();
//    }
//
//    public static void setLocalOnboardingState(LocalOnboardingState.TactUserLocalOnboardingState state) {
//        User.localOnboardingState.setCurrentLocalOnboardingState(state);
//    }
//
//    public void DeleteRecursive(File fileOrDirectory) {
//        /*if (fileOrDirectory.isDirectory())
//            for (File child : fileOrDirectory.listFiles())
//                DeleteRecursive(child);
//
//        fileOrDirectory.delete();*/
//        if (fileOrDirectory.isDirectory()) {
//            String[] children = fileOrDirectory.list();
//            for (int i = 0; i < children.length; i++) {
//                new File(fileOrDirectory, children[i]).delete();
//            }
//        }
//
////        setPushChannel(null);
//    }
//
//    public void setPushChannel(String channel) {
//        TactApplication.getInstance().getSecuredPrefs().put(TactConst.PUSH_CHANNEL, channel);
//    }
//
//    public String getPushChannel() {
//        return TactApplication.getInstance().getSecuredPrefs().getString(TactConst.PUSH_CHANNEL);
//    }
}
