package com.tactile.tact.activities;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tactile.tact.R;
import com.tactile.tact.consts.TactConst;
import com.tactile.tact.controllers.TactSharedPrefController;
import com.tactile.tact.fragments.FragmentGoogleWebview;
import com.tactile.tact.fragments.FragmentOnboardingCalendars;
import com.tactile.tact.fragments.FragmentOnboardingContacts;
import com.tactile.tact.fragments.FragmentOnboardingExchange;
import com.tactile.tact.fragments.FragmentOnboardingExchangeManually;
import com.tactile.tact.fragments.FragmentOnboardingGoogle;
import com.tactile.tact.fragments.FragmentOnboardingLocalSources;
import com.tactile.tact.fragments.FragmentOnboardingSalesforce;
import com.tactile.tact.fragments.FragmentOnboardingEmailSources;
import com.tactile.tact.fragments.FragmentOnboardingTactPasswordRegistration;
import com.tactile.tact.fragments.FragmentOnboardingTactUserRegistration;
import com.tactile.tact.fragments.FragmentSalesforceWebview;
import com.tactile.tact.services.events.EventAddSourceOnboarding;
import com.tactile.tact.services.events.EventContactsUploadDetailsAmazonSuccess;
import com.tactile.tact.services.events.EventContactsUploadDetailsSuccess;
import com.tactile.tact.services.events.EventDeviceInformationSuccess;
import com.tactile.tact.services.events.EventFinalizeLocalSources;
import com.tactile.tact.services.events.EventHandleProgress;
import com.tactile.tact.services.events.EventMoveNextFragmentOnboarding;
import com.tactile.tact.services.events.EventOnboardingChanges;
import com.tactile.tact.services.network.TactNetworking;
import com.tactile.tact.services.network.response.DeviceContactsUploadDataSourceResponse;
import com.tactile.tact.utils.TactDialogHandler;
import com.tactile.tact.utils.Utils;

import java.util.HashMap;

import de.greenrobot.event.EventBus;

/**
 * Created by sebafonseca on 10/27/14.
 */
public class OnBoardingActivity extends TactBaseActivity {

    private Fragment currentFragment;
    private Boolean loggedInSalesforce      = TactSharedPrefController.getOnboardingLoggedInSalesforce(); //false
    private Boolean loggedInGoogle          = TactSharedPrefController.getOnboardingLoggedInGoogle(); //false;
    private Boolean loggedInExchange        = TactSharedPrefController.getOnboardingLoggedInExchange(); // false;
    private Boolean syncGoogle              = TactSharedPrefController.getOnboardingSyncGoogle(); // false;
    private Boolean syncExchange            = TactSharedPrefController.getOnboardingSyncExchange(); //false;
    private Boolean syncContacts            = TactSharedPrefController.getOnboardingSyncContacts(); //true;
    private Boolean syncCalendars           = TactSharedPrefController.getOnboardingSyncCalendars(); // true;
    private Boolean syncContactsProcessing  = TactSharedPrefController.getOnboardingProcessingContactSync(); //false;
    private Boolean exchangeManuallyEnabled = TactSharedPrefController.getOnboardingExchangeManuallyEnabled(); // false;
    private HashMap<String, Integer> sources;


    //Handle State of Exchange Manually Connection
    private int stageExchangeManually       = TactSharedPrefController.getOnboardingStageExchangeManually(); //0;
    private String emailExchangeManually    = TactSharedPrefController.getOnboardingEmailExchangeManually(); // "";
    private String serverExchangeManually   = TactSharedPrefController.getOnboardingServerExchangeManually(); // "";
    private String domainExchangeManually   = TactSharedPrefController.getOnboardingDomainExchangeManually(); // "";
    private String usernameExchangeManually = TactSharedPrefController.getOnboardingUsernameExchangeManually(); // "";
    private String passwordExchangeManually = TactSharedPrefController.getOnboardingPasswordExchangeManually(); // "";

    private TactDialogHandler dialog;

    enum FragmentHandler {
        SALESFORCE,
        SALESFORCE_WEBVIEW,
        EMAIL,
        GOOGLE,
        GOOGLE_WEBVIEW,
        EXCHANGE,
        EXCHANGE_MANUALLY,
        LOCAL_SOURCES,
        CONTACTS,
        CALENDARS,
        USER_REGISTRATION,
        PASSWORD_REGISTRATION
    }

    private static final String CONST_LOGGED_IN_SALESFORCE              = "loggedInSalesforce";
    private static final String CONST_LOGGED_IN_GOOGLE                  = "loggedInGoogle";
    private static final String CONST_LOGGED_IN_EXCHANGE                = "loggedInExchange";
    private static final String CONST_SYNC_GOOGLE                       = "syncGoogle";
    private static final String CONST_SYNC_EXCHANGE                     = "syncExchange";
    private static final String CONST_SYNC_CONTACTS                     = "syncContacts";
    private static final String CONST_SYNC_CALENDARS                    = "syncCalendars";
    private static final String CONST_SYNC_CONTACTS_PROCESSING          = "syncContactsProcessing";
    private static final String CONST_SYNC_EXCHANGE_MANUALLY            = "exchangeManuallyEnabled";
    private static final String CONST_SYNC_EXCHANGE_MANUALLY_STAGE      = "stageExchangeManually";
    private static final String CONST_SYNC_EXCHANGE_MANUALLY_EMAIL      = "emailExchangeManually";
    private static final String CONST_SYNC_EXCHANGE_MANUALLY_SERVER     = "serverExchangeManually";
    private static final String CONST_SYNC_EXCHANGE_MANUALLY_DOMAIN     = "domainExchangeManually";
    private static final String CONST_SYNC_EXCHANGE_MANUALLY_USERNAME   = "usernameExchangeManually";
    private static final String CONST_SYNC_EXCHANGE_MANUALLY_PWD        = "passwordExchangeManually";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sources = new HashMap<String, Integer>();

        if (TactSharedPrefController.getOnboardingTactAppSource() != null) {
            sources.put("tactapp", Integer.parseInt(TactSharedPrefController.getOnboardingTactAppSource()));
        }
        if (TactSharedPrefController.getOnboardingSalesforceSource() != null) {
            sources.put("salesforce", Integer.parseInt(TactSharedPrefController.getOnboardingSalesforceSource()));
        }
        if (TactSharedPrefController.getOnboardingExchangeSource() != null) {
            sources.put("exchange", Integer.parseInt(TactSharedPrefController.getOnboardingExchangeSource()));
        }
        if (TactSharedPrefController.getOnboardingGoogleSource() != null) {
            sources.put("google", Integer.parseInt(TactSharedPrefController.getOnboardingGoogleSource()));
        }

        dialog = new TactDialogHandler(this);

        setContentView(R.layout.onboarding_main);

        LinearLayout back_btn = (LinearLayout) findViewById(R.id.back_btn);
        back_btn.setOnClickListener(onFragmentBackClick());

        //The first fragment is Salesforce
        //startFragment(FragmentHandler.SALESFORCE);
        startFragment(getStep(TactSharedPrefController.getOnboardingStep()));
    }

    /**
     * Sets the correct title text and the correct progress/remaining on the complete flow of onboarding for the fragment
     * @param fragment the fragment to display
     */
    private void setUpActionBar(FragmentHandler fragment){
        switch (fragment){
            case SALESFORCE:case SALESFORCE_WEBVIEW:
                setValuesActionBar(1f, 5f, getResources().getString(R.string.onboarding_title_salesforce));
                break;
            case EMAIL:
                setValuesActionBar(2f, 4f, getResources().getString(R.string.onboarding_title_email_sources));
                break;
            case GOOGLE:case GOOGLE_WEBVIEW:
                setValuesActionBar(3f, 3f, getResources().getString(R.string.connect_gmail_title));
                break;
            case EXCHANGE:case EXCHANGE_MANUALLY:
                setValuesActionBar(3f, 3f, getResources().getString(R.string.connect_exchange_title));
                break;
            case LOCAL_SOURCES:
                setValuesActionBar(4f, 2f, getResources().getString(R.string.get_local_sources));
                break;
            case CONTACTS:
                setValuesActionBar(5f, 1f, getResources().getString(R.string.connect_contacts));
                break;
            case CALENDARS:
                setValuesActionBar(5f, 1f, getResources().getString(R.string.connect_calendars));
                break;
        }
    }

    /**
     * Sets the progress, remaining and title for the title bar
     * @param progress the progress of the wizard
     * @param remaining the remaining of the wizard
     * @param title the text of the title bar
     */
    private void setValuesActionBar(float progress, float remaining, String title){
        RelativeLayout breadcrumb_progress  = (RelativeLayout) findViewById(R.id.breadcrumb_progress);
        RelativeLayout breadcrumb_remaining = (RelativeLayout) findViewById(R.id.breadcrumb_remaining);
        TextView title_text                 = (TextView) findViewById(R.id.title_text);
        //Set the progress
        breadcrumb_progress.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, progress));
        //Set the remaining
        breadcrumb_remaining.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, remaining));
        //Set the title
        title_text.setText(title);
    }

    /**
     * Starts a new fragment
     * @param type indicates the new fragment to start
     */
    private void startFragment(FragmentHandler type, Object ...args) {

        currentFragment     = null;
        String fragment_tag = null;
        Bundle bundle       = null;

        TactSharedPrefController.setOnboardingStep(getFragmentId(type));

        switch(type){
            case SALESFORCE:
                currentFragment = new FragmentOnboardingSalesforce();
                bundle          = new Bundle();
                bundle.putBoolean(CONST_LOGGED_IN_SALESFORCE, loggedInSalesforce); // needed if we want to display that we are logged in salesforce
                fragment_tag    = TactConst.FRAGMENT_ONBOARDING_SALESFORCE;
                break;
            case SALESFORCE_WEBVIEW:
                currentFragment = new FragmentSalesforceWebview();
                fragment_tag    = TactConst.FRAGMENT_ONBOARDING_SALESFORCE_WEBVIEW;
                break;
            case EMAIL:
                currentFragment = new FragmentOnboardingEmailSources();
                bundle          = new Bundle();
                bundle.putBoolean(CONST_LOGGED_IN_EXCHANGE,   loggedInExchange);
                bundle.putBoolean(CONST_LOGGED_IN_GOOGLE,     loggedInGoogle);
                bundle.putBoolean(CONST_LOGGED_IN_SALESFORCE, loggedInSalesforce); // needed if we want to skip this view
                bundle.putBoolean(CONST_SYNC_GOOGLE,          syncGoogle); // needed if we already select to sync or not to sync google
                bundle.putBoolean(CONST_SYNC_EXCHANGE,        syncExchange); // needed if we already select to sync or not to sync exchange
                fragment_tag    = TactConst.FRAGMENT_ONBOARDING_EMAIL;
                break;
            case GOOGLE:
                currentFragment = new FragmentOnboardingGoogle();
                bundle          = new Bundle();
                bundle.putBoolean(CONST_LOGGED_IN_EXCHANGE,   loggedInExchange);
                bundle.putBoolean(CONST_LOGGED_IN_GOOGLE,     loggedInGoogle); // needed if we want to display that we are logged in google
                bundle.putBoolean(CONST_LOGGED_IN_SALESFORCE, loggedInSalesforce);
                bundle.putBoolean(CONST_SYNC_EXCHANGE,    syncExchange); // needed if we already select to sync or not to sync exchange
                fragment_tag    = TactConst.FRAGMENT_ONBOARDING_GOOGLE;
                break;
            case GOOGLE_WEBVIEW:
                currentFragment = new FragmentGoogleWebview();
                fragment_tag    = TactConst.FRAGMENT_ONBOARDING_GOOGLE_WEBVIEW;
                break;
            case EXCHANGE:
                currentFragment = new FragmentOnboardingExchange();
                bundle          = new Bundle();
                bundle.putBoolean(CONST_LOGGED_IN_SALESFORCE,  loggedInSalesforce); // if we want to skip exchange we must be logged in salesforce or google
                bundle.putBoolean(CONST_LOGGED_IN_GOOGLE,      loggedInGoogle); // if we want to skip exchange we must be logged in salesforce or google
                bundle.putBoolean(CONST_LOGGED_IN_EXCHANGE,    loggedInExchange); // needed if we want to display that we are logged in exchange
                bundle.putBoolean(CONST_SYNC_EXCHANGE_MANUALLY, exchangeManuallyEnabled);
                bundle.putString(CONST_SYNC_EXCHANGE_MANUALLY_EMAIL, emailExchangeManually);
                fragment_tag    = TactConst.FRAGMENT_ONBOARDING_GOOGLE;
                break;
            case EXCHANGE_MANUALLY:
                currentFragment = new FragmentOnboardingExchangeManually();
                bundle          = new Bundle();
                bundle.putBoolean(CONST_LOGGED_IN_SALESFORCE, loggedInSalesforce);
                bundle.putBoolean(CONST_LOGGED_IN_GOOGLE, loggedInGoogle);
                bundle.putInt(CONST_SYNC_EXCHANGE_MANUALLY_STAGE, (Integer)args[1]);
                bundle.putString(CONST_SYNC_EXCHANGE_MANUALLY_EMAIL, (String)args[0]);
                bundle.putString(CONST_SYNC_EXCHANGE_MANUALLY_SERVER, serverExchangeManually);
                bundle.putString(CONST_SYNC_EXCHANGE_MANUALLY_DOMAIN, domainExchangeManually);
                bundle.putString(CONST_SYNC_EXCHANGE_MANUALLY_USERNAME, usernameExchangeManually);
                bundle.putString(CONST_SYNC_EXCHANGE_MANUALLY_PWD, passwordExchangeManually);
                fragment_tag    = TactConst.FRAGMENT_ONBOARDING_EXCHANGE_MANUALLY;
                break;
            case LOCAL_SOURCES:
                currentFragment = new FragmentOnboardingLocalSources();
                bundle          = new Bundle();
                bundle.putBoolean(CONST_SYNC_CONTACTS,   syncContacts); // needed if we already select to sync or not to sync contacts
                bundle.putBoolean(CONST_SYNC_CALENDARS,  syncCalendars); // needed if we already select to sync or not to sync calendars
                fragment_tag    = TactConst.FRAGMENT_ONBOARDING_LOCAL_SOURCES;
                break;
            case CONTACTS:
                currentFragment = new FragmentOnboardingContacts();
                bundle          = new Bundle();
                bundle.putBoolean(CONST_SYNC_CALENDARS,             syncCalendars);
                bundle.putBoolean(CONST_SYNC_CONTACTS_PROCESSING,   syncContactsProcessing);
                fragment_tag    = TactConst.FRAGMENT_ONBOARDING_CONTACTS;
                break;
            case CALENDARS:
                currentFragment = new FragmentOnboardingCalendars();
                bundle = new Bundle();
                bundle.putBoolean(CONST_SYNC_CONTACTS,   syncContacts);
                bundle.putString("sourceName", "tactapp");
                bundle.putInt("sourceId", getSources().get("tactapp"));
                fragment_tag    = TactConst.FRAGMENT_ONBOARDING_CALENDARS;
                break;
            case USER_REGISTRATION:
                disableBackButton();
                currentFragment = new FragmentOnboardingTactUserRegistration();
                fragment_tag    = TactConst.FRAGMENT_ONBOARDING_TACT_USER_REGISTRATION;
                break;
            case PASSWORD_REGISTRATION:
                disableBackButton();
                currentFragment = new FragmentOnboardingTactPasswordRegistration();
                fragment_tag    = TactConst.FRAGMENT_ONBOARDING_TACT_PASSWORD_REGISTRATION;
                break;
        }

        //Setup the header of the fragment
        setUpActionBar(type);
        if (bundle != null){
            currentFragment.setArguments(bundle);
        }
        if(currentFragment != null){
            getFragmentManager().beginTransaction().replace(R.id.onboarding_content_frame, currentFragment, fragment_tag).commit();
        }
    }

    private void disableBackButton(){
        LinearLayout back_btn = (LinearLayout) findViewById(R.id.back_btn);
        back_btn.setVisibility(View.INVISIBLE);
    }

    public View.OnClickListener onFragmentBackClick(){
        return new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                processBackFragment();
            }
        };
    }

    @Override
    public void onBackPressed(){
        processBackFragment();
    }

    /**
     * Go back from current fragment
     */
    private void processBackFragment(){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);
        if (currentFragment instanceof FragmentOnboardingSalesforce){ // move to landing activity
            TactSharedPrefController.setOnboardingStep(-1);
            startActivity(new Intent(OnBoardingActivity.this, LandingActivity.class));
            finish();
        }
        else if (currentFragment instanceof FragmentSalesforceWebview){ // move to salesforce fragment
            startFragment(FragmentHandler.SALESFORCE);
        }
        else if (currentFragment instanceof FragmentOnboardingEmailSources){ // move to salesforce fragment
            startFragment(FragmentHandler.SALESFORCE);
        }
        else if (currentFragment instanceof FragmentOnboardingGoogle){ //  move to email sources fragment
            startFragment(FragmentHandler.EMAIL);
        }
        else if (currentFragment instanceof FragmentGoogleWebview){ // move to google fragment
            startFragment(FragmentHandler.GOOGLE);
        }
        else if (currentFragment instanceof FragmentOnboardingExchange){
            if (syncGoogle){
                startFragment(FragmentHandler.GOOGLE); // move to google fragment
            }
            else {
                startFragment(FragmentHandler.EMAIL); // move to email sources fragment
            }
        }
        else if (currentFragment instanceof FragmentOnboardingLocalSources){
            startFragment(FragmentHandler.EMAIL); // move to email sources fragment
        }
        else if (currentFragment instanceof FragmentOnboardingContacts){
            startFragment(FragmentHandler.LOCAL_SOURCES); // move to local sources fragment
        }
        else if (currentFragment instanceof FragmentOnboardingCalendars){
            startFragment(FragmentHandler.LOCAL_SOURCES); // move to local sources fragment
        }
        else if (currentFragment instanceof FragmentOnboardingExchangeManually){
            if (stageExchangeManually == 0) {
                startFragment(FragmentHandler.EXCHANGE); // move to exchange fragment
            }
            else {
                startFragment(FragmentHandler.EXCHANGE_MANUALLY, emailExchangeManually, stageExchangeManually - 1); // move to exchange manually fragment
            }
        }
    }

    /**
     * Handle the next fragment to move on
     * @param nextFragmentOnboarding the fragment that send the move event
     */
    public void onEventMainThread(EventMoveNextFragmentOnboarding nextFragmentOnboarding) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);
        EventBus.getDefault().removeStickyEvent(nextFragmentOnboarding);
        if (nextFragmentOnboarding.getArgs()[0].equals(TactConst.FRAGMENT_ONBOARDING_SALESFORCE)){ //move to salesforce fragment

            startFragment(FragmentHandler.SALESFORCE);
        }
        else if (nextFragmentOnboarding.getArgs()[0].equals(TactConst.FRAGMENT_ONBOARDING_SALESFORCE_WEBVIEW)){ //move to salesforce webview

            startFragment(FragmentHandler.SALESFORCE_WEBVIEW);
        }
        else if (nextFragmentOnboarding.getArgs()[0].equals(TactConst.FRAGMENT_ONBOARDING_EMAIL)){ //move to email flow

            startFragment(FragmentHandler.EMAIL);
        }
        else if (nextFragmentOnboarding.getArgs()[0].equals(TactConst.FRAGMENT_ONBOARDING_CONNECT_EMAIL)){ //move to google and/or exchange fragment

            if (syncGoogle){
                startFragment(FragmentHandler.GOOGLE); // move to google fragment
            }

            else if (syncExchange){
                startFragment(FragmentHandler.EXCHANGE); // move to exchange fragment
            }

            else if (loggedInSalesforce){
                startFragment(FragmentHandler.LOCAL_SOURCES); // move to sync local sources flow
            }
        }
        else if (nextFragmentOnboarding.getArgs()[0].equals(TactConst.FRAGMENT_ONBOARDING_GOOGLE_WEBVIEW)){ //move to google webview

            startFragment(FragmentHandler.GOOGLE_WEBVIEW);
        }
        else if (nextFragmentOnboarding.getArgs()[0].equals(TactConst.FRAGMENT_ONBOARDING_GOOGLE)) { //move to google fragment
            startFragment(FragmentHandler.GOOGLE);
        }
        else if (nextFragmentOnboarding.getArgs()[0].equals(TactConst.FRAGMENT_ONBOARDING_EXCHANGE)) { //move to exchange fragment
            startFragment(FragmentHandler.EXCHANGE);
        }
        else if (nextFragmentOnboarding.getArgs()[0].equals(TactConst.FRAGMENT_ONBOARDING_LOCAL_SOURCES)) { //move to local sources
            startFragment(FragmentHandler.LOCAL_SOURCES);
        }
        else if (nextFragmentOnboarding.getArgs()[0].equals(TactConst.FRAGMENT_ONBOARDING_CONTACTS)){ //move to contacts fragment
            startFragment(FragmentHandler.CONTACTS);
        }
        else if (nextFragmentOnboarding.getArgs()[0].equals(TactConst.FRAGMENT_ONBOARDING_CALENDARS)){ // move to calendars fragment
            startFragment(FragmentHandler.CALENDARS);
        }
        else if (nextFragmentOnboarding.getArgs()[0].equals(TactConst.FRAGMENT_ONBOARDING_TACT_PASSWORD_REGISTRATION)){
            startFragment(FragmentHandler.PASSWORD_REGISTRATION);
        }
        else if (nextFragmentOnboarding.getArgs()[0].equals(TactConst.FRAGMENT_ONBOARDING_TACT_REGISTRATION)){ // move to login activity
            startActivity(new Intent(this, LoginSyncActivity.class));
            finish();
        }
        else if (nextFragmentOnboarding.getArgs()[0].equals(TactConst.FRAGMENT_ONBOARDING_EXCHANGE_MANUALLY)){ // move to exchange manually connection
            setUsernameExchangeManually((String)(nextFragmentOnboarding.getArgs()[1]));
            setPasswordExchangeManually((String)(nextFragmentOnboarding.getArgs()[2]));
            startFragment(FragmentHandler.EXCHANGE_MANUALLY, nextFragmentOnboarding.getArgs()[1], stageExchangeManually);
        }
    }

    /*********** EVENTS FOR HANDLE LOCAL SOURCE CALLS ***********/
    /**
     * Event for handle the finalize of local sources sync
     * @param eventFinalizeLocalSources the current event
     */
    public void onEvent(EventFinalizeLocalSources eventFinalizeLocalSources){
        EventBus.getDefault().removeStickyEvent(eventFinalizeLocalSources);
        TactNetworking.callDeviceInformation(this); // send device information to Tact Server
    }

    /**
     * Event for handle the device information call success
     * @param eventDeviceInformationSuccess the current event
     */
    public void onEvent(EventDeviceInformationSuccess eventDeviceInformationSuccess){
        EventBus.getDefault().removeStickyEvent(eventDeviceInformationSuccess);
        TactNetworking.callContactsUploadDetails(this); // capture send device information success and get the contacts uploads details
    }

    /**
     * Event fir handle the contacts upload details success
     * @param eventContactsUploadDetailsSuccess the current event
     */
    public void onEvent(EventContactsUploadDetailsSuccess eventContactsUploadDetailsSuccess){
        EventBus.getDefault().removeStickyEvent(eventContactsUploadDetailsSuccess);
        EventBus.getDefault().postSticky(new EventHandleProgress(true, false));
        // process response of callContactsUploadSuccess and send data to amazon
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        DeviceContactsUploadDataSourceResponse response = gson.fromJson(eventContactsUploadDetailsSuccess.response.toString(), DeviceContactsUploadDataSourceResponse.class);
        String filename_calendar = "initialActivities.json";
        String filename_contacts = "initialContacts.json";
        final String zipFileName = "initialData.zip";
        //Create the zip with both files
        try{
            Utils.zip(this, zipFileName, filename_calendar, filename_contacts);
            Utils.deleteFile(this, filename_contacts);
            Utils.deleteFile(this, filename_calendar);
        } catch(Exception e) {
            Utils.Log("Exception in zip: " + e.getMessage());
        }
        String files_with_path = TactApplication.getAppFilesPath() + "/" + zipFileName;
        TactNetworking.callContactsUploadDetailsAmazon(this, files_with_path, response);
    }

    /**
     * Event for handle contact upload succes to amazon
     * @param eventContactsUploadDetailsAmazonSuccess the current event
     */
    public void onEventMainThread(EventContactsUploadDetailsAmazonSuccess eventContactsUploadDetailsAmazonSuccess){
        EventBus.getDefault().removeStickyEvent(eventContactsUploadDetailsAmazonSuccess);
        startFragment(FragmentHandler.USER_REGISTRATION);
    }

    /*********** END OF EVENTS FOR HANDLE LOCAL SOURCE CALLS ***********/

    /**
     * Event for handle the progress bar state
     * @param eventHandleProgress the current event
     */
    public void onEventMainThread(EventHandleProgress eventHandleProgress)
    {
        EventBus.getDefault().removeStickyEvent(eventHandleProgress);
        if (eventHandleProgress.getShowProgress() && !dialog.isShowing())
        {
            dialog.showProgress(eventHandleProgress.isTransparent());
        }
        else if (!eventHandleProgress.getShowProgress() && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    /**
     * Event for handle the activity vars
     * @param eventOnboardingChanges the current event
     */
    public void onEvent(EventOnboardingChanges eventOnboardingChanges)
    {
        EventBus.getDefault().removeStickyEvent(eventOnboardingChanges);
        if (eventOnboardingChanges.getType() == EventOnboardingChanges.ChangeType.LOGGED_IN_SALESFORCE)
        {
            setLoggedInSalesforce((Boolean) eventOnboardingChanges.getNewValue());
        }
        else if (eventOnboardingChanges.getType() == EventOnboardingChanges.ChangeType.SYNC_GOOGLE)
        {
            changeSyncGoogle((Boolean) eventOnboardingChanges.getNewValue());
        }
        else if (eventOnboardingChanges.getType() == EventOnboardingChanges.ChangeType.SYNC_EXCHANGE)
        {
            changeSyncExchange((Boolean) eventOnboardingChanges.getNewValue());
        }
        else if (eventOnboardingChanges.getType() == EventOnboardingChanges.ChangeType.LOGGED_IN_GOOGLE)
        {
            setLoggedInGoogle((Boolean) eventOnboardingChanges.getNewValue());
        }
        else if (eventOnboardingChanges.getType() == EventOnboardingChanges.ChangeType.GOOGLE_PROGRESS)
        {
            setGoogleProgress((Integer) eventOnboardingChanges.getNewValue());
        }
        else if (eventOnboardingChanges.getType() == EventOnboardingChanges.ChangeType.SYNC_EXCHANGE_EMAIL)
        {
            setEmailExchangeManually((String) eventOnboardingChanges.getNewValue());
        }
        else if (eventOnboardingChanges.getType() == EventOnboardingChanges.ChangeType.LOGGED_IN_EXCHANGE)
        {
            setLoggedInExchange((Boolean) eventOnboardingChanges.getNewValue());
        }
        else if (eventOnboardingChanges.getType() == EventOnboardingChanges.ChangeType.SYNC_EXCHANGE_MANUALLY)
        {
            eneableExchangeManually((Boolean) eventOnboardingChanges.getNewValue());
        }
        else if (eventOnboardingChanges.getType() == EventOnboardingChanges.ChangeType.SYNC_EXCHANGE_STAGE)
        {
            setStageExchangeManually((Integer) eventOnboardingChanges.getNewValue());
        }
        else if (eventOnboardingChanges.getType() == EventOnboardingChanges.ChangeType.SYNC_EXCHANGE_SERVER)
        {
            setServerExchangeManually((String) eventOnboardingChanges.getNewValue());
        }
        else if (eventOnboardingChanges.getType() == EventOnboardingChanges.ChangeType.SYNC_EXCHANGE_DOMAIN)
        {
            setDomainExchangeManually((String) eventOnboardingChanges.getNewValue());
        }
        else if (eventOnboardingChanges.getType() == EventOnboardingChanges.ChangeType.SYNC_EXCHANGE_USERNAME)
        {
            setUsernameExchangeManually((String) eventOnboardingChanges.getNewValue());
        }
        else if (eventOnboardingChanges.getType() == EventOnboardingChanges.ChangeType.SYNC_EXCHANGE_PWD)
        {
            setPasswordExchangeManually((String) eventOnboardingChanges.getNewValue());
        }
        else if (eventOnboardingChanges.getType() == EventOnboardingChanges.ChangeType.SYNC_CONTACTS)
        {
            changeSyncContacts((Boolean) eventOnboardingChanges.getNewValue());
        }
        else if (eventOnboardingChanges.getType() == EventOnboardingChanges.ChangeType.SYNC_CALENDARS)
        {
            changeSyncCalendars((Boolean) eventOnboardingChanges.getNewValue());
        }
        else if (eventOnboardingChanges.getType() == EventOnboardingChanges.ChangeType.SYNC_CONTACTS_PROCESSING)
        {
            syncContactsProcessing((Boolean) eventOnboardingChanges.getNewValue());
        }
        else if (eventOnboardingChanges.getType() == EventOnboardingChanges.ChangeType.REGISTER_CHANGE_TITLE)
        {
            changeTitle((String) eventOnboardingChanges.getNewValue());
        }
    }

    public void onEvent(EventAddSourceOnboarding eventAddSourceOnboarding){
        if (sources.containsKey(eventAddSourceOnboarding.source)){
            sources.remove(eventAddSourceOnboarding.source);
        }
        if (eventAddSourceOnboarding.source.equals("tactapp")){
            TactSharedPrefController.setOnboardingTactAppSource(eventAddSourceOnboarding.source_id.toString());
        }
        else if (eventAddSourceOnboarding.source.equals("salesforce")){
            TactSharedPrefController.setOnboardingSalesforceSource(eventAddSourceOnboarding.source_id.toString());
        }
        else if (eventAddSourceOnboarding.source.equals("exchange")){
            TactSharedPrefController.setOnboardingExchangeSource(eventAddSourceOnboarding.source_id.toString());
        }
        else if (eventAddSourceOnboarding.source.equals("google")){
            TactSharedPrefController.setOnboardingGoogleSource(eventAddSourceOnboarding.source_id.toString());
        }
        sources.put(eventAddSourceOnboarding.source, eventAddSourceOnboarding.source_id);
    }

    /**
     * Display the view for make a question to tact
     * @param view the current view
     */
    public void doQuestion(View view){
//        Utils.doQuestion(view);
        String tactEmail    = "Tact Support <feedback@tactile.com>";
        String tactSubject  = getResources().getString(R.string.question_subject) + " #" + TactSharedPrefController.getUUID() + "#";
        //String tactBody     = "\nUser ID: " + LocalStorage.getInstance().getUuid() + "\n--------\n\n\n";
        String tactBody     = "";

        // Try to send the email using Gmail
        Intent gmail        = new Intent(Intent.ACTION_VIEW);
        gmail.setClassName("com.google.android.gm","com.google.android.gm.ComposeActivityGmail");
        gmail.putExtra(Intent.EXTRA_EMAIL, new String[] { tactEmail });
        gmail.setData(Uri.parse(tactEmail));
        gmail.putExtra(Intent.EXTRA_SUBJECT, tactSubject);
        gmail.setType("plain/text");
        gmail.putExtra(Intent.EXTRA_TEXT, tactBody);
        try {
            startActivity(gmail);
        }
        catch (Exception e){
            // Send the email with other client
            Intent intent   = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL  , new String[]{ tactEmail });
            intent.putExtra(Intent.EXTRA_SUBJECT, tactSubject);
            intent.putExtra(Intent.EXTRA_TEXT   , tactBody);
            try {
                startActivity(Intent.createChooser(intent, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(view.getContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Store if we want to sync google
     * @param syncGoogle true/false
     */
    private void changeSyncGoogle(Boolean syncGoogle){
        this.syncGoogle = syncGoogle;
        TactSharedPrefController.setOnboardingSyncGoogle(syncGoogle);
    }

    /**
     * Store if we want to sync exchange
     * @param syncExchange  true/false
     */
    private void changeSyncExchange(Boolean syncExchange){
        this.syncExchange = syncExchange;
        TactSharedPrefController.setOnboardingSyncExchange(syncExchange);
    }

    /**
     * Store that the user is logged in salesforce
     * @param loggedInSalesforce true/false
     */
    private void setLoggedInSalesforce(Boolean loggedInSalesforce){
        this.loggedInSalesforce = loggedInSalesforce;
        TactSharedPrefController.setOnboardingLoggedInSalesforce(loggedInSalesforce);
    }

    private void setGoogleProgress(int progress){
        this.setProgress(progress * 1000);
    }

    /**
     * Store that the user is logged in google
     * @param loggedInGoogle true/false
     */
    private void setLoggedInGoogle(Boolean loggedInGoogle){
        this.loggedInGoogle = loggedInGoogle;
        TactSharedPrefController.setOnboardingLoggedInGoogle(loggedInGoogle);
    }

    /**
     * Store that the user is logged in exchange
     * @param loggedInExchange true/false
     */
    private void setLoggedInExchange(Boolean loggedInExchange){
        this.loggedInExchange = loggedInExchange;
        TactSharedPrefController.setOnboardingLoggedInExchange(loggedInExchange);
    }

    /**
     * Store if we want to sync contacts
     * @param syncContacts true/false
     */
    private void changeSyncContacts(Boolean syncContacts){
        this.syncContacts = syncContacts;
        TactSharedPrefController.setOnboardingSyncContacts(syncContacts);
    }

    /**
     * Store if we want to sync calendars
     * @param syncCalendars true/false
     */
    private void changeSyncCalendars(Boolean syncCalendars){
        this.syncCalendars = syncCalendars;
        TactSharedPrefController.setOnboardingSyncCalendars(syncCalendars);
    }

    /**
     * Store if the system is in syncing contacts processing
     * @param syncContactsProcessing true/false
     */
    private void syncContactsProcessing(Boolean syncContactsProcessing){
        this.syncContactsProcessing = syncContactsProcessing;
        TactSharedPrefController.setOnboardingProcessingContactSync(syncContactsProcessing);
    }

    /**
     * Change title for registration fragment
     * @param title the new text to display
     */
    private void changeTitle(String title){
        setValuesActionBar(6f, 0f, title);
    }

    /**
     * Store that exchange manually connection is enabled or not
     * @param eneableExchangeManually true/false
     */
    private void eneableExchangeManually(Boolean eneableExchangeManually){
        this.exchangeManuallyEnabled = eneableExchangeManually;
        TactSharedPrefController.setOnboardingExchangeManuallyEnabled(eneableExchangeManually);
    }

    /**
     * Store stage of the exchange manually connection
     * @param stageExchangeManually int
     */
    private void setStageExchangeManually(int stageExchangeManually){
        this.stageExchangeManually = stageExchangeManually;
        TactSharedPrefController.setOnboardingStageExchangeManually(Integer.toString(stageExchangeManually));
    }

    /**
     * Store email for the exchange manually connection
     * @param emailExchangeManually string
     */
    private void setEmailExchangeManually(String emailExchangeManually){
        this.emailExchangeManually = emailExchangeManually;
        TactSharedPrefController.setOnboardingEmailExchangeManually(emailExchangeManually);
    }

    /**
     * Store server for the exchange manually connection
     * @param serverExchangeManually string
     */
    private void setServerExchangeManually(String serverExchangeManually){
        this.serverExchangeManually = serverExchangeManually;
        TactSharedPrefController.setOnboardingServerExchangeManually(serverExchangeManually);
    }

    /**
     * Store domain for the exchange manually connection
     * @param domainExchangeManually string
     */
    private void setDomainExchangeManually(String domainExchangeManually){
        this.domainExchangeManually = domainExchangeManually;
        TactSharedPrefController.setOnboardingDomainExchangeManually(domainExchangeManually);
    }

    /**
     * Store username for the exchange manually connection
     * @param usernameExchangeManually string
     */
    private void setUsernameExchangeManually(String usernameExchangeManually){
        this.usernameExchangeManually = usernameExchangeManually;
        TactSharedPrefController.setOnboardingUsernamExchangeManually(usernameExchangeManually);
    }

    /**
     * Store password for the exchange manually connection
     * @param passwordExchangeManually string
     */
    private void setPasswordExchangeManually(String passwordExchangeManually){
        this.passwordExchangeManually = passwordExchangeManually;
        TactSharedPrefController.setOnboardingPasswordExchangeManually(passwordExchangeManually);
    }

    public HashMap<String, Integer> getSources(){
        return sources;
    }

    private FragmentHandler getStep(Integer step){
        switch (step){
            case -1: case 0: return FragmentHandler.SALESFORCE;
            case 1: return FragmentHandler.SALESFORCE_WEBVIEW;
            case 2: return FragmentHandler.EMAIL;
            case 3: return FragmentHandler.GOOGLE;
            case 4: return FragmentHandler.GOOGLE_WEBVIEW;
            case 5: return FragmentHandler.EXCHANGE;
            case 6: return FragmentHandler.EXCHANGE_MANUALLY;
            case 7: return FragmentHandler.LOCAL_SOURCES;
            case 8: return FragmentHandler.CONTACTS;
            case 9: return FragmentHandler.CALENDARS;
            case 10: return FragmentHandler.USER_REGISTRATION;
            case 11: return FragmentHandler.PASSWORD_REGISTRATION;
        }
        return FragmentHandler.SALESFORCE;
    }

    private Integer getFragmentId(FragmentHandler handler){
        switch (handler){
            case SALESFORCE:            return 0;
            case SALESFORCE_WEBVIEW:    return 1;
            case EMAIL:                 return 2;
            case GOOGLE:                return 3;
            case GOOGLE_WEBVIEW:        return 4;
            case EXCHANGE:              return 5;
            case EXCHANGE_MANUALLY:     return 6;
            case LOCAL_SOURCES:         return 7;
            case CONTACTS:              return 8;
            case CALENDARS:             return 9;
            case USER_REGISTRATION:     return 10;
            case PASSWORD_REGISTRATION: return 11;
        }
        return 0;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        TactApplication.unbindDrawables(findViewById(R.id.drawer_layout));
        dialog = null;
        currentFragment = null;
        sources = null;
        System.gc();
    }
}
