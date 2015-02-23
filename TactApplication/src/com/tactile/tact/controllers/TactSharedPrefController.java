package com.tactile.tact.controllers;

import android.util.Base64;

import com.tactile.tact.activities.TactApplication;
import com.tactile.tact.consts.TactConst;

import java.util.ArrayList;

/**
 * Class to handle app shared preferences
 * Created by leyan on 10/24/14.
 */
public class TactSharedPrefController {

    public static boolean isLoggedIn() {
        if (TactSharedPrefController.isSharedPreferenceValid(TactConst.CREDENTIAL_USERNAME_KEY) &&
            TactSharedPrefController.isSharedPreferenceValid(TactConst.CREDENTIAL_PASSWORD_KEY)) {
            return true;
        }
        return false;
    }

    public static boolean isTempLoggedIn() {
        if (TactSharedPrefController.isSharedPreferenceValid(TactConst.CREDENTIAL_TEMP_USERNAME_KEY) &&
            TactSharedPrefController.isSharedPreferenceValid(TactConst.CREDENTIAL_TEMP_PASSWORD_KEY) &&
            TactSharedPrefController.isSharedPreferenceValid(TactConst.CREDENTIAL_TEMP_UUID_KEY)) {
            return true;
        }
        return false;
    }

    /**
     * Add a key and value to shared preference
     * @param preferenceKey
     * @param preferenceValue
     */
    public static void addSharedPreference(String preferenceKey, String preferenceValue){
        TactApplication.getInstance().getSecuredPrefs().put(preferenceKey, preferenceValue);
    }

    /**
     * Remove a pair (key, value) from shared preferences
     * @param preferenceKey
     */
    public static void removeSharedPreference(String preferenceKey){
        if (TactSharedPrefController.isSharedPreferenceExist(preferenceKey)){
            TactApplication.getInstance().getSecuredPrefs().removeValue(preferenceKey);
        }
    }

    /**
     * Verify if a shared preference exist
     * @param preferenceKey
     * @return
     */
    public static boolean isSharedPreferenceExist(String preferenceKey){
        return TactApplication.getInstance().getSecuredPrefs().containsKey(preferenceKey);
    }

    /**
     * Verify if a shared preference exist and have a valid value
     * @param preferenceKey
     * @return
     */
    public static boolean isSharedPreferenceValid(String preferenceKey){
        return TactSharedPrefController.isSharedPreferenceExist(preferenceKey) &&
               TactApplication.getInstance().getSecuredPrefs().getString(preferenceKey) != null &&
               !TactApplication.getInstance().getSecuredPrefs().getString(preferenceKey).isEmpty() &&
               !TactApplication.getInstance().getSecuredPrefs().getString(preferenceKey).equals("");
    }

    /**
     * Store the credentials
     * @param username - Username
     * @param password - Password
     */
    public static void storeCredentials(String username, String password) {
        TactApplication.getInstance().getSecuredPrefs().put(TactConst.CREDENTIAL_USERNAME_KEY, username);
        TactApplication.getInstance().getSecuredPrefs().put(TactConst.CREDENTIAL_PASSWORD_KEY, password);
    }

    /**
     * Store the temporal credentials
     * @param username - Username
     * @param password - Password
     */
    public static void storeTempCredentials(String username, String password) {
        TactApplication.getInstance().getSecuredPrefs().put(TactConst.CREDENTIAL_TEMP_USERNAME_KEY, username);
        TactApplication.getInstance().getSecuredPrefs().put(TactConst.CREDENTIAL_TEMP_PASSWORD_KEY, password);
    }

    /**
     * Store the temporal credentials
     * @param username - Username
     * @param password - Password
     * @param uuid - server uuid
     */
    public static void storeTempCredentials(String username, String password, String uuid) {
        TactApplication.getInstance().getSecuredPrefs().put(TactConst.CREDENTIAL_TEMP_USERNAME_KEY, username);
        TactApplication.getInstance().getSecuredPrefs().put(TactConst.CREDENTIAL_TEMP_PASSWORD_KEY, password);
        TactApplication.getInstance().getSecuredPrefs().put(TactConst.CREDENTIAL_TEMP_UUID_KEY, uuid);
    }

    public static String getUUID() {
        return TactApplication.getInstance().getSecuredPrefs().getString(TactConst.CREDENTIAL_TEMP_UUID_KEY);
    }

    /**
     * Get the stored username
     * @return - Stored username
     */
    public static String getCredentialUsername() {
        return TactApplication.getInstance().getSecuredPrefs().getString(TactConst.CREDENTIAL_USERNAME_KEY);
    }

    /**
     * Get the stored user password
     * @return - Stored user Password
     */
    public static String getCredentialPassword() {
        return TactApplication.getInstance().getSecuredPrefs().getString(TactConst.CREDENTIAL_PASSWORD_KEY);
    }

    /**
     * Get the stored username
     * @return - Stored username
     */
    public static String getTempCredentialUsername() {
        return TactApplication.getInstance().getSecuredPrefs().getString(TactConst.CREDENTIAL_TEMP_USERNAME_KEY);
    }

    /**
     * Get the stored user password
     * @return - Stored user Password
     */
    public static String getTempCredentialPassword() {
        return TactApplication.getInstance().getSecuredPrefs().getString(TactConst.CREDENTIAL_TEMP_PASSWORD_KEY);
    }

    /**
     * Check the credentials for the user
     * @param username - Current username
     * @param password - Current user password
     * @return - True if credentials match, False otherwise
     */
    public static boolean checkCredentials(String username, String password) {
        try {
            return TactSharedPrefController.getCredentialUsername().equals(username) && TactSharedPrefController.getCredentialPassword().equals(password);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isNeedSync() {
        try {
            return TactApplication.getInstance().getSecuredPrefs().getString(TactConst.SHARED_PREF_NEED_SYNC_KEY).equals(TactConst.SHARED_PREF_NEED_SYNC);
        } catch (Exception e) {
            return false;
        }
    }

    public static void setNeedSync() {
        try {
            TactSharedPrefController.addSharedPreference(TactConst.SHARED_PREF_NEED_SYNC_KEY, TactConst.SHARED_PREF_NEED_SYNC);
        } catch (Exception e) {

        }
    }

    public static void setNoNeedSync() {
        try {
            TactSharedPrefController.addSharedPreference(TactConst.SHARED_PREF_NEED_SYNC_KEY, TactConst.SHARED_PREF_NO_NEED_SYNC);
        } catch (Exception e) {

        }
    }

    /**
     * Return the credentials header param for authenticated user
     * @return String
     * @throws Exception
     */
    public static String getCredentialsHeader() throws Exception {
        try {
            if (TactSharedPrefController.isSharedPreferenceValid(TactConst.CREDENTIAL_USERNAME_KEY) &&
                    TactSharedPrefController.isSharedPreferenceValid(TactConst.CREDENTIAL_PASSWORD_KEY)) {
                String credentials = TactSharedPrefController.getCredentialUsername() + ":" + TactSharedPrefController.getCredentialPassword();
                return "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            } else throw new Exception("No credentials stored");
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Return the credentials header for login
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    public static String getCredentialsHeader(String username, String password) throws Exception {
        try {
            String credentials = username + ":" + password;
             return "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Return the credentials header param for authenticated user
     * @return String
     * @throws Exception
     */
    public static String getTempCredentialsHeader() throws Exception {
        try {
            if (TactSharedPrefController.isSharedPreferenceValid(TactConst.CREDENTIAL_TEMP_USERNAME_KEY) &&
                TactSharedPrefController.isSharedPreferenceValid(TactConst.CREDENTIAL_TEMP_PASSWORD_KEY)) {
                String credentials = TactSharedPrefController.getTempCredentialUsername() + ":" + TactSharedPrefController.getTempCredentialPassword();
                return "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            } else throw new Exception("No credentials stored");
        } catch (Exception e) {
            throw e;
        }
    }

    public static void setPushChannel(String pushChannel) {
        TactSharedPrefController.addSharedPreference(TactConst.PUSH_CHANNEL, pushChannel);
    }

    public static String getPushChannel() {
        return TactApplication.getInstance().getSecuredPrefs().getString(TactConst.PUSH_CHANNEL);
    }

    public static boolean isPushChannel() {
        return TactSharedPrefController.isSharedPreferenceValid(TactConst.PUSH_CHANNEL);
    }

    public static void resetCredentials(){
        removeSharedPreference(TactConst.CREDENTIAL_USERNAME_KEY);
        removeSharedPreference(TactConst.CREDENTIAL_PASSWORD_KEY);
    }

    public static void setSyncStatus(String syncStatus) {
        TactSharedPrefController.addSharedPreference(TactConst.SHARED_PREF_SYNC_STATUS_KEY, syncStatus);
    }

    public static String getSyncStatus() {
        if (!TactSharedPrefController.isSharedPreferenceExist(TactConst.SHARED_PREF_SYNC_STATUS_KEY)) {
            setSyncStatus(TactConst.SHARED_PREF_SYNC_STATUS_NO_SYNC);
        }
        return TactApplication.getInstance().getSecuredPrefs().getString(TactConst.SHARED_PREF_SYNC_STATUS_KEY);
    }

    public static boolean isInitialSynced() {
        return TactSharedPrefController.isSharedPreferenceExist(TactConst.SHARED_PREF_SYNC_STATUS_KEY) &&
               TactSharedPrefController.getSyncStatus().equals(TactConst.SHARED_PREF_SYNC_STATUS_SYNCED);
    }

    public static boolean isInitialReadySync() {
        return TactSharedPrefController.isSharedPreferenceExist(TactConst.SHARED_PREF_SYNC_STATUS_KEY) &&
               TactSharedPrefController.getSyncStatus().equals(TactConst.SHARED_PREF_SYNC_STATUS_READY_SYNC);
    }

    public static void setReadyToSync() {
        TactSharedPrefController.setSyncStatus(TactConst.SHARED_PREF_SYNC_STATUS_READY_SYNC);
    }

    public static void setNoSynced() {
        TactSharedPrefController.setSyncStatus(TactConst.SHARED_PREF_SYNC_STATUS_NO_SYNC);
    }

    public static void setSynced() {
        TactSharedPrefController.setSyncStatus(TactConst.SHARED_PREF_SYNC_STATUS_SYNCED);
    }

    public static void setInitialDownloadURL(String url) {
        TactSharedPrefController.addSharedPreference(TactConst.SHARED_PREF_SYNC_DOWNLOAD_URL_KEY, url);
    }

    public static String getInitialDownloadURL() {
        if (!TactSharedPrefController.isSharedPreferenceExist(TactConst.SHARED_PREF_SYNC_DOWNLOAD_URL_KEY)) {
            setInitialDownloadURL("");
        }
        return TactApplication.getInstance().getSecuredPrefs().getString(TactConst.SHARED_PREF_SYNC_DOWNLOAD_URL_KEY);
    }

    public static boolean isInitialDownloadURL() {
        return !TactSharedPrefController.getInitialDownloadURL().equals("");
    }

    public static void addOnboardingCalendarId(String id){
        if (!TactSharedPrefController.isSharedPreferenceExist(TactConst.ONBOARDING_SYNCED_CALENDARS)){
            TactSharedPrefController.addSharedPreference(TactConst.ONBOARDING_SYNCED_CALENDARS, id);
        }
        else {
            String old_ids = TactApplication.getInstance().getSecuredPrefs().getString(TactConst.ONBOARDING_SYNCED_CALENDARS);
            TactSharedPrefController.addSharedPreference(TactConst.ONBOARDING_SYNCED_CALENDARS, old_ids + "," + id);
        }
    }

    public static ArrayList<String> getOnboardingCalendarIds(){
        if (!TactSharedPrefController.isSharedPreferenceExist(TactConst.ONBOARDING_SYNCED_CALENDARS)){
            return new ArrayList<String>();
        }
        else {
            ArrayList<String> result = new ArrayList<String>();
            String ids = TactApplication.getInstance().getSecuredPrefs().getString(TactConst.ONBOARDING_SYNCED_CALENDARS);
            if (!ids.contains(",")){
                result.add(ids);
            }
            else {
                String[] idsArray = ids.split(",");
                for (String id: idsArray){
                    result.add(id);
                }
            }
            return result;
        }
    }

    public static void removeOnboardingCalendarIds(){
        if (TactSharedPrefController.isSharedPreferenceExist(TactConst.ONBOARDING_SYNCED_CALENDARS)) {
            TactSharedPrefController.removeSharedPreference(TactConst.ONBOARDING_SYNCED_CALENDARS);
        }
    }

    /***********************|
    |*SAVE ONBOARDING STATE*|
    |***********************/

    public static void setOnboardingLoggedInSalesforce(Boolean loggedInSalesforce){
        TactSharedPrefController.addSharedPreference(TactConst.ONBOARDING_LOGGED_IN_SALESFORCE, loggedInSalesforce.toString());
    }

    public static Boolean getOnboardingLoggedInSalesforce(){
        if (TactSharedPrefController.isSharedPreferenceExist(TactConst.ONBOARDING_LOGGED_IN_SALESFORCE)){
            return getSharedPreference(TactConst.ONBOARDING_LOGGED_IN_SALESFORCE).contains("true");
        }
        else {
            return false;
        }
    }

    public static void setOnboardingLoggedInGoogle(Boolean loggedInGoogle){
        TactSharedPrefController.addSharedPreference(TactConst.ONBOARDING_LOGGED_IN_GOOGLE, loggedInGoogle.toString());
    }

    public static Boolean getOnboardingLoggedInGoogle(){
        if (TactSharedPrefController.isSharedPreferenceExist(TactConst.ONBOARDING_LOGGED_IN_GOOGLE)){
            return getSharedPreference(TactConst.ONBOARDING_LOGGED_IN_GOOGLE).contains("true");
        }
        else {
            return false;
        }
    }

    public static void setOnboardingLoggedInExchange(Boolean loggedInExchange){
        TactSharedPrefController.addSharedPreference(TactConst.ONBOARDING_LOGGED_IN_EXCHANGE, loggedInExchange.toString());
    }

    public static Boolean getOnboardingLoggedInExchange(){
        if (TactSharedPrefController.isSharedPreferenceExist(TactConst.ONBOARDING_LOGGED_IN_EXCHANGE)){
            return getSharedPreference(TactConst.ONBOARDING_LOGGED_IN_EXCHANGE).contains("true");
        }
        else {
            return false;
        }
    }

    public static void setOnboardingSyncGoogle(Boolean syncGoogle){
        TactSharedPrefController.addSharedPreference(TactConst.ONBOARDING_SYNC_GOOGLE, syncGoogle.toString());
    }

    public static Boolean getOnboardingSyncGoogle(){
        if (TactSharedPrefController.isSharedPreferenceExist(TactConst.ONBOARDING_SYNC_GOOGLE)){
            return getSharedPreference(TactConst.ONBOARDING_SYNC_GOOGLE).contains("true");
        }
        else {
            return false;
        }
    }

    public static void setOnboardingSyncExchange(Boolean syncExchange){
        TactSharedPrefController.addSharedPreference(TactConst.ONBOARDING_SYNC_EXCHANGE, syncExchange.toString());
    }

    public static Boolean getOnboardingSyncExchange(){
        if (TactSharedPrefController.isSharedPreferenceExist(TactConst.ONBOARDING_SYNC_EXCHANGE)){
            return getSharedPreference(TactConst.ONBOARDING_SYNC_EXCHANGE).contains("true");
        }
        else {
            return false;
        }
    }

    public static void setOnboardingSyncContacts(Boolean syncContacts){
        TactSharedPrefController.addSharedPreference(TactConst.ONBOARDING_SYNC_CONTACTS, syncContacts.toString());
    }

    public static Boolean getOnboardingSyncContacts(){
        if (TactSharedPrefController.isSharedPreferenceExist(TactConst.ONBOARDING_SYNC_CONTACTS)){
            return getSharedPreference(TactConst.ONBOARDING_SYNC_CONTACTS).contains("true");
        }
        else {
            return true;
        }
    }

    public static void setOnboardingSyncCalendars(Boolean syncCalendars){
        TactSharedPrefController.addSharedPreference(TactConst.ONBOARDING_SYNC_CALENDARS, syncCalendars.toString());
    }

    public static Boolean getOnboardingSyncCalendars(){
        if (TactSharedPrefController.isSharedPreferenceExist(TactConst.ONBOARDING_SYNC_CALENDARS)){
            return getSharedPreference(TactConst.ONBOARDING_SYNC_CALENDARS).contains("true");
        }
        else {
            return true;
        }
    }

    public static void setOnboardingProcessingContactSync(Boolean processingContactSync){
        TactSharedPrefController.addSharedPreference(TactConst.ONBOARDING_PROCESSING_CONTACT_SYNC, processingContactSync.toString());
    }

    public static Boolean getOnboardingProcessingContactSync(){
        if (TactSharedPrefController.isSharedPreferenceExist(TactConst.ONBOARDING_PROCESSING_CONTACT_SYNC)){
            return getSharedPreference(TactConst.ONBOARDING_PROCESSING_CONTACT_SYNC).contains("true");
        }
        else {
            return false;
        }
    }

    public static void setOnboardingExchangeManuallyEnabled(Boolean exchangeManuallyEnabled){
        TactSharedPrefController.addSharedPreference(TactConst.ONBOARDING_EXCHANGE_MANUALLTY_ENABLED, exchangeManuallyEnabled.toString());
    }

    public static Boolean getOnboardingExchangeManuallyEnabled(){
        if (TactSharedPrefController.isSharedPreferenceExist(TactConst.ONBOARDING_EXCHANGE_MANUALLTY_ENABLED)){
            return getSharedPreference(TactConst.ONBOARDING_EXCHANGE_MANUALLTY_ENABLED).contains("true");
        }
        else {
            return false;
        }
    }

    public static void setOnboardingTactAppSource(String tactAppSource){
        TactSharedPrefController.addSharedPreference(TactConst.ONBOARDING_TACTAPP_SOURCE, tactAppSource);
    }

    public static String getOnboardingTactAppSource(){
        if (TactSharedPrefController.isSharedPreferenceExist(TactConst.ONBOARDING_TACTAPP_SOURCE)){
            return getSharedPreference(TactConst.ONBOARDING_TACTAPP_SOURCE);
        }
        else {
            return null;
        }
    }

    public static void setOnboardingSalesforceSource(String salesforceSource){
        TactSharedPrefController.addSharedPreference(TactConst.ONBOARDING_SALESFORCE_SOURCE, salesforceSource);
    }

    public static String getOnboardingSalesforceSource(){
        if (TactSharedPrefController.isSharedPreferenceExist(TactConst.ONBOARDING_SALESFORCE_SOURCE)){
            return getSharedPreference(TactConst.ONBOARDING_SALESFORCE_SOURCE);
        }
        else {
            return null;
        }
    }

    public static void setOnboardingExchangeSource(String exchangeSource){
        TactSharedPrefController.addSharedPreference(TactConst.ONBOARDING_EXCHANGE_SOURCE, exchangeSource);
    }

    public static String getOnboardingExchangeSource(){
        if (TactSharedPrefController.isSharedPreferenceExist(TactConst.ONBOARDING_EXCHANGE_SOURCE)){
            return getSharedPreference(TactConst.ONBOARDING_EXCHANGE_SOURCE);
        }
        else {
            return null;
        }
    }

    public static void setOnboardingGoogleSource(String googleSource){
        TactSharedPrefController.addSharedPreference(TactConst.ONBOARDING_GOOGLE_SOURCE, googleSource);
    }

    public static String getOnboardingGoogleSource(){
        if (TactSharedPrefController.isSharedPreferenceExist(TactConst.ONBOARDING_GOOGLE_SOURCE)){
            return getSharedPreference(TactConst.ONBOARDING_GOOGLE_SOURCE);
        }
        else {
            return null;
        }
    }

    public static void setOnboardingStageExchangeManually(String stageExchangeManually){
        TactSharedPrefController.addSharedPreference(TactConst.ONBOARDING_STAGE_EXCHANGE_MANUALLY, stageExchangeManually);
    }

    public static Integer getOnboardingStageExchangeManually(){
        if (TactSharedPrefController.isSharedPreferenceExist(TactConst.ONBOARDING_STAGE_EXCHANGE_MANUALLY)){
            return Integer.parseInt(getSharedPreference(TactConst.ONBOARDING_STAGE_EXCHANGE_MANUALLY));
        }
        else {
            return 0;
        }
    }

    public static void setOnboardingEmailExchangeManually(String emailExchangeManually){
        TactSharedPrefController.addSharedPreference(TactConst.ONBOARDING_EMAIL_EXCHANGE_MANUALLY, emailExchangeManually);
    }

    public static String getOnboardingEmailExchangeManually(){
        if (TactSharedPrefController.isSharedPreferenceExist(TactConst.ONBOARDING_EMAIL_EXCHANGE_MANUALLY)){
            return getSharedPreference(TactConst.ONBOARDING_EMAIL_EXCHANGE_MANUALLY);
        }
        else {
            return "";
        }
    }

    public static void setOnboardingServerExchangeManually(String serverExchangeManually){
        TactSharedPrefController.addSharedPreference(TactConst.ONBOARDING_SERVER_EXCHANGE_MANUALLY, serverExchangeManually);
    }

    public static String getOnboardingServerExchangeManually(){
        if (!TactSharedPrefController.isSharedPreferenceExist(TactConst.ONBOARDING_SERVER_EXCHANGE_MANUALLY)){
            return getSharedPreference(TactConst.ONBOARDING_SERVER_EXCHANGE_MANUALLY);
        }
        else {
            return "";
        }
    }

    public static void setOnboardingDomainExchangeManually(String domainExchangeManually){
        TactSharedPrefController.addSharedPreference(TactConst.ONBOARDING_DOMAIN_EXCHANGE_MANUALY, domainExchangeManually);
    }

    public static String getOnboardingDomainExchangeManually(){
        if (!TactSharedPrefController.isSharedPreferenceExist(TactConst.ONBOARDING_DOMAIN_EXCHANGE_MANUALY)){
            return getSharedPreference(TactConst.ONBOARDING_DOMAIN_EXCHANGE_MANUALY);
        }
        else {
            return "";
        }
    }

    public static void setOnboardingUsernamExchangeManually(String usernamExchangeManually){
        TactSharedPrefController.addSharedPreference(TactConst.ONBOARDING_USERNAME_EXCHANGE_MANUALLY, usernamExchangeManually);
    }

    public static String getOnboardingUsernameExchangeManually(){
        if (!TactSharedPrefController.isSharedPreferenceExist(TactConst.ONBOARDING_USERNAME_EXCHANGE_MANUALLY)){
            return getSharedPreference(TactConst.ONBOARDING_USERNAME_EXCHANGE_MANUALLY);
        }
        else {
            return "";
        }
    }

    public static void setOnboardingPasswordExchangeManually(String passwordExchangeManually){
        TactSharedPrefController.addSharedPreference(TactConst.ONBOARDING_PASSWORD_EXCHANGE_MANUALLY, passwordExchangeManually);
    }

    public static String getOnboardingPasswordExchangeManually(){
        if (!TactSharedPrefController.isSharedPreferenceExist(TactConst.ONBOARDING_PASSWORD_EXCHANGE_MANUALLY)){
            return getSharedPreference(TactConst.ONBOARDING_PASSWORD_EXCHANGE_MANUALLY);
        }
        else {
            return "";
        }
    }

    public static void setOnboardingStep(Integer step){
        TactSharedPrefController.addSharedPreference(TactConst.ONBOARDING_STEP, step.toString());
    }

    public static Integer getOnboardingStep(){
        if (TactSharedPrefController.isSharedPreferenceExist(TactConst.ONBOARDING_STEP)){
            return Integer.parseInt(getSharedPreference(TactConst.ONBOARDING_STEP));
        }
        else {
            return -1;
        }
    }

    public static void setOnboardingUserSelection(Boolean userSelection){
        TactSharedPrefController.addSharedPreference(TactConst.ONBOARDING_USER_SELECTION, userSelection.toString());
    }

    public static Boolean getOnboardingUserSelection(){
        if (TactSharedPrefController.isSharedPreferenceExist(TactConst.ONBOARDING_USER_SELECTION)){
            return getSharedPreference(TactConst.ONBOARDING_USER_SELECTION).contains("true");
        }
        else {
            return true;
        }
    }

    //Pre-condition: the shared preference exists!
    public static String getSharedPreference(String key){
        return TactApplication.getInstance().getSecuredPrefs().getString(key);
    }

    public static void cleanOnboardingSharedPreferences(){
        TactSharedPrefController.removeSharedPreference(TactConst.ONBOARDING_LOGGED_IN_SALESFORCE);
        TactSharedPrefController.removeSharedPreference(TactConst.ONBOARDING_LOGGED_IN_GOOGLE);
        TactSharedPrefController.removeSharedPreference(TactConst.ONBOARDING_LOGGED_IN_EXCHANGE);
        TactSharedPrefController.removeSharedPreference(TactConst.ONBOARDING_SYNC_GOOGLE);
        TactSharedPrefController.removeSharedPreference(TactConst.ONBOARDING_SYNC_EXCHANGE);
        TactSharedPrefController.removeSharedPreference(TactConst.ONBOARDING_SYNC_CONTACTS);
        TactSharedPrefController.removeSharedPreference(TactConst.ONBOARDING_SYNC_CALENDARS);
        TactSharedPrefController.removeSharedPreference(TactConst.ONBOARDING_PROCESSING_CONTACT_SYNC);
        TactSharedPrefController.removeSharedPreference(TactConst.ONBOARDING_EXCHANGE_MANUALLTY_ENABLED);
        TactSharedPrefController.removeSharedPreference(TactConst.ONBOARDING_TACTAPP_SOURCE);
        TactSharedPrefController.removeSharedPreference(TactConst.ONBOARDING_SALESFORCE_SOURCE);
        TactSharedPrefController.removeSharedPreference(TactConst.ONBOARDING_EXCHANGE_SOURCE);
        TactSharedPrefController.removeSharedPreference(TactConst.ONBOARDING_GOOGLE_SOURCE);
        TactSharedPrefController.removeSharedPreference(TactConst.ONBOARDING_STAGE_EXCHANGE_MANUALLY);
        TactSharedPrefController.removeSharedPreference(TactConst.ONBOARDING_EMAIL_EXCHANGE_MANUALLY);
        TactSharedPrefController.removeSharedPreference(TactConst.ONBOARDING_SERVER_EXCHANGE_MANUALLY);
        TactSharedPrefController.removeSharedPreference(TactConst.ONBOARDING_DOMAIN_EXCHANGE_MANUALY);
        TactSharedPrefController.removeSharedPreference(TactConst.ONBOARDING_USERNAME_EXCHANGE_MANUALLY);
        TactSharedPrefController.removeSharedPreference(TactConst.ONBOARDING_PASSWORD_EXCHANGE_MANUALLY);
        TactSharedPrefController.removeSharedPreference(TactConst.ONBOARDING_USER_SELECTION);
        TactSharedPrefController.removeSharedPreference(TactConst.ONBOARDING_STEP);

        TactSharedPrefController.removeSharedPreference(TactConst.CREDENTIAL_TEMP_USERNAME_KEY);
        TactSharedPrefController.removeSharedPreference(TactConst.CREDENTIAL_TEMP_PASSWORD_KEY);
        TactSharedPrefController.removeSharedPreference(TactConst.CREDENTIAL_TEMP_UUID_KEY);
    }
}
