package com.tactile.tact.consts;

import com.tactile.tact.activities.TactApplication;

/**
 * Created by leyan on 8/15/14.
 */
public class TactConst {

    //**********************Credentials names***************************************************
    public static String CREDENTIAL_USERNAME_KEY = "username";
    public static String CREDENTIAL_PASSWORD_KEY = "password";
    public static String CREDENTIAL_TEMP_USERNAME_KEY = "usernametemp";
    public static String CREDENTIAL_TEMP_PASSWORD_KEY = "passwordtemp";
    public static String CREDENTIAL_TEMP_UUID_KEY = "uuidtemp";
    public static String SECURE_PREF_KEY = "tACtPr3f3r3nce5";
    public static String PUSH_CHANNEL = "pushChannel";
    public static String PUSH_KEY = "pushAPIKey";

    //***********************Fragments tags***************************************************

    public static final int FRAGMENT_CALENDAR_TAG = 0;
    public static final int FRAGMENT_CONTACT_DETAIL_TAG = 1;
    public static final int FRAGMENT_CONTACTS_TAG = 2;
    public static final int FRAGMENT_EVENT_DETAIL_TAG = 3;
    public static final int FRAGMENT_LOG_CREATE_TAG = 4;
    public static final int FRAGMENT_LOG_DETAIL_TAG = 5;
    public static final int FRAGMENT_LOG_LIST_TAG = 6;
    public static final int FRAGMENT_CONTACT_PICK_TAG = 7;
    public static final int FRAGMENT_TASK_CREATE_TAG = 8;
    public static final int FRAGMENT_TASK_DETAILS_TAG = 9;
    public static final int FRAGMENT_NOTE_CREATE_TAG = 10;
    public static final int FRAGMENT_NOTE_DETAIL_TAG = 11;
    public static final int FRAGMENT_NOTEBOOK_TAG = 12;
    

    public static String FRAGMENT_EMAIL_TAG = "fragment_email";
    public static String FRAGMENT_AGENDA_TAG = "fragment_agenda";
//    public static String FRAGMENT_CONTACTS_TAG = "fragment_contacts";
    public static String FRAGMENT_INNER_CONTACTS_TAG = "fragment_inner_contacts";
    public static String FRAGMENT_EMAIL_DETAILS_TAG = "fragment_email_details";
    public static String FRAGMENT_SETTINGS_TAG = "fragment_settings";
    public static String FRAGMENT_NEW_EDIT_NOTE_TAG = "fragment_new_note";
    public static String FRAGMENT_NOTE_DETAILS_TAG = "fragment_note_details";
    public static String FRAGMENT_NOTES_LIST_TAG = "fragment_notes_list";
    public static String FRAGMENT_AGENDA_TASK_DETAILS_TAG = "fragment_agenda_task_details";
    public static String FRAGMENT_AGENDA_MEETING_DETAILS_TAG = "fragment_agenda_meeting_details";
    public static String FRAGMENT_AGENDA_EDIT_TASK_TAG = "fragment_agenda_edit_task_tag";
    public static String FRAGMENT_AGENDA_CONTACT_FINDER = "fragment_agenda_contact_finder_tag";
    public static String FRAGMENT_ONBOARDING_SALESFORCE = "fragment_onboarding_salesforce";
    public static String FRAGMENT_ONBOARDING_SALESFORCE_WEBVIEW = "fragment_onboarding_salesforce_webview";
    public static String FRAGMENT_ONBOARDING_EMAIL = "fragment_onboarding_email";
    public static String FRAGMENT_ONBOARDING_CONNECT_EMAIL = "fragment_onboarding_connect_email";
    public static String FRAGMENT_ONBOARDING_GOOGLE = "fragment_onboarding_google";
    public static String FRAGMENT_ONBOARDING_GOOGLE_WEBVIEW = "fragment_onboarding_google_webview";
    public static String FRAGMENT_ONBOARDING_EXCHANGE = "fragment_onboarding_exchange";
    public static String FRAGMENT_ONBOARDING_EXCHANGE_MANUALLY = "fragment_onboarding_exchange_manually";
    public static String FRAGMENT_ONBOARDING_LOCAL_SOURCES = "fragment_onboarding_local_sources";
    public static String FRAGMENT_ONBOARDING_CONTACTS = "fragment_onboarding_contacts";
    public static String FRAGMENT_ONBOARDING_CALENDARS = "fragment_onboarding_calendars";
    public static String FRAGMENT_ONBOARDING_TACT_REGISTRATION = "fragment_onboarding_tact_registration";
    public static String FRAGMENT_ONBOARDING_TACT_USER_REGISTRATION = "fragment_onboarding_tact_user_registration";
    public static String FRAGMENT_ONBOARDING_TACT_PASSWORD_REGISTRATION = "fragment_onboarding_tact_password_registration";
    public static String FRAGMENT_LOGGING_NEW_EDIT_TAG = "fragment_logging_new_edit";
    public static String FRAGMENT_LOGGING_LIST_TAG = "fragment_logging_list";
    public static String FRAGMENT_LOGGING_DETAILS_TAG = "fragment_logging_details";
    public static String FRAGMENT_CONTACT_DETAILS_TAG = "fragment_contact_details";

    //*******************Synchronization types***************************************************
    public static int SYNC_TYPE_HOTSYNC = 0;
    public static int SYNC_TYPE_FISYNC = 1;

    //************************Email states***************************************************
    public static enum EmailCurrentState {
        InBoxView,
        SentView,
    }

    //*********************************Names***************************************************
    public static final String APPLICATION_NAME		= "Tact for Android";
    public static final String APPLICATION_CAPTION	= "Tact for Android - always better!";

    //************************Server Constants***************************************************
    public static final String BUILD_VERSION           = "0.5B";
    public static final String TACT_APP_VERSION        = "686";
    public static final String TACT_DB_DATA_VERSION    = "-1";
    public static final String TACT_SYNC_REVISION      = "0";
    public static final String TACT_TIME_ZONE          = "PDT";
    public static final String TACT_GMT_OFFSET         = "-10800";
    public static final String TACT_CUDL_VERSION       = "0";
    public static final String TACT_CUDL_TIMESTAMP     = "0";

    //************************End points***************************************************
    public static final String END_POINT_CONFIG = "/config";
    public static final String END_POINT_AUTH = "/auth";
    public static final String END_POINT_PUSHER_AUTH = "/pusher/auth";
    public static final String END_POINT_AUTH_GOOGLE = "/oauth/google";
    public static final String END_POINT_AUTH_GOOGLE_REDIRECT = "/oauth/google/callback";
    public static final String END_POINT_REGISTER = "/register";
    public static final String END_POINT_SOURCES = "/sources";
    public static final String END_POINT_SOURCES_ADDED = "/sources_added";
    public static final String END_POINT_AVAILABLE_USERNAMES = "/available_usernames";
    public static final String END_POINT_ME = "/me";
    public static final String END_POINT_GENERATE_INITIAL_DB = "/generate_initial_db";
    public static final String END_POINT_DOWNLOAD_DB = "/sqlite_db";
    public static final String END_POINT_DEVICE_CONTACT_UPLOAD =  "/device_contacts_upload_details";
    public static final String END_POINT_RESET_PASSWORD =  "/request_password";
    public static final String END_POINT_HOT_SYNC =  "/hotsync";
    public static final String END_POINT_SYNC =  "/sync";
    public static final String END_POINT_FEED_ITEM_SYNC =  "/synchronize/FeedItem";


    //*****************************URLs enumerator***************************************************
    public enum URLs {
        TactTestEnvironment,
        TactStagingEnvironment,
        TactBetaEnvironment,
        TactEapEnvironment,
        TactProductionEnvironment,
        TactLocalEnvironment
    }

    //*****************************Tact Errors enumerator***************************************************
    public enum TactError {
        NetworkingError,
        ParseError,
        FileError,
        DatabaseError
    }

    //*****************************URL SERVER CONSTANTS***************************************************
    //Test Enviroment
    public static final String SERVER_URL_TEST_ENVIROMENT = "https://s-api.tactapp.com:443/v1";
    public static final String SEGMENT_API_KEY_TEST_ENVIROMENT = "3us75pqosr";
    public static final String MIXPANEL_API_KEY_TEST_ENVIROMENT = "6e4b2b40a613be0314d103b37a6a5726";
    public static final String SERVER_OAUTH_URL_TEST_ENVIROMENT = "https://s-api.tactapp.com";
    public static final String SHOW_DEBUG_SETTINGS_TEST_ENVIROMENT = "true";
    public static final String PUSH_ENABLE_KEY_TEST_ENVIROMENT = "false";
    public static final String HOCKEY_APP_ID_TEST_ENVIROMENT = "9ee020512b5ee8f17df1463c38d276e3";

    //Production Enviroment
    public static final String SERVER_URL_PRODUCTION_ENVIROMENT = "https://p-api.tactapp.com:443/v1";
    public static final String SEGMENT_API_KEY_PRODUCTION_ENVIROMENT = "3jgmofs79q";
    public static final String MIXPANEL_API_KEY_PRODUCTION_ENVIROMENT = "4c89452bc38b9af8b93121ac4d1c20bd";
    public static final String SERVER_OAUTH_URL_PRODUCTION_ENVIROMENT = "https://p-api.tactapp.com";
    public static final String SHOW_DEBUG_SETTINGS_PRODUCTION_ENVIROMENT = "true";
    public static final String PUSH_ENABLE_KEY_PRODUCTION_ENVIROMENT = "false";

    //Local Enviroment
    public static final String SERVER_URL_LOCAL_ENVIROMENT = "http://" + "PUT YOUR LOCAL IP HERE" + ":" + "PUT YOUR LOCAL PORT HERE" + "/v1";
    public static final String SEGMENT_API_KEY_LOCAL_ENVIROMENT = "3us75pqosr";
    public static final String MIXPANEL_API_KEY_LOCAL_ENVIROMENT = "ebacbfe587bc2aed8286ece8068bb8ee";
    public static final String SERVER_OAUTH_URL_LOCAL_ENVIROMENT = "http://" + "PUT YOUR LOCAL IP HERE" + ":" + "PUT YOUR LOCAL PORT HERE";
    public static final String SHOW_DEBUG_SETTINGS_LOCAL_ENVIROMENT = "true";
    public static final String PUSH_ENABLE_KEY_LOCAL_ENVIROMENT = "true";

    //*****************************CALLS TAGS***************************************************
    public static final String CALL_TAG_HOT_SYNC = "CALL_HOT_SYNC";
    public static final String CALL_TAG_TEMP_REGISTER = "CALL_TEMP_REGISTER";
    public static final String CALL_TAG_ME = "CALL_ME";
    public static final String CALL_TAG_SOURCES = "CALL_SOURCES";
    public static final String CALL_TAG_LOGIN = "CALL_LOGIN";
    public static final String CALL_TAG_DEVICE_INFORMATION = "CALL_DEVICE_INFROMATION";
    public static final String CALL_TAG_DEVICE_CONTACT_UPLOAD = "CALL_DEVICE_CONTACT_UPLOAD";
    public static final String CALL_TAG_DEVICE_CONTACT_UPLOAD_AMAZON = "CALL_DEVICE_CONTACT_UPLOAD_AMAZON";
    public static final String CALL_TAG_GET_AVAILABLES_NAMES = "CALL_GET_AVAILABLES_NAMES";
    public static final String CALL_TAG_PUT_UPDATE_REGISTRATION = "CALL_PUT_UPDATE_REGISTRATION";
    public static final String CALL_TAG_SOURCES_ADDED = "CALL_SOURCES_ADDED";
    public static final String CALL_TAG_CONFIG = "CALL_CONFIG";
    public static final String CALL_TAG_RESET_PASSWORD = "CALL_RESET_PASSWORD";
    public static final String CALL_TAG_FEEDITEMS_SYNC = "CALL_FEEDITEMS_SYNC";

    //******************************Services consts***************************
    public static final String SERVICE_SYNC_IS_STARTING = "isStarting";
    public static final String SERVICE_SYNC_DOWNLOAD_DATABASE_URL = "downloadURL";
    public static final String SERVICE_SYNC_ERROR = "errorObject";

    public static final int NOTIFICATION_ID_INITIAL_SYNC_FINISHED = 3331;
    public static final String NOTIFICATION_ACTION_SYNC = "NOTIFICATION_SYNC_CLICK";

    //*****************************DATABASE CONSTANTS*******************************************
    public static final String INITIAL_DB_FILENAME = "tact_db.zip";
//    public static final String INITIAL_DB_FOLDER_PATH = TactApplication.getAppContext().getFilesDir().getAbsolutePath() + "/databases";

    public static final String SYNC_REVISION = "revision";
    public static final String SYNC_OPPORTUNITIES_REVISION = "opportunities_revision";

    //*****************************SHAREDPREFS CONSTANTS*******************************************
    public static final String SHARED_PREF_SYNC_STATUS_KEY = "initialSyncStatus";
    public static final String SHARED_PREF_SYNC_STATUS_NO_SYNC = "no_sync";
    public static final String SHARED_PREF_SYNC_STATUS_READY_SYNC = "ready_sync";
    public static final String SHARED_PREF_SYNC_STATUS_SYNCED = "synced";
    public static final String SHARED_PREF_SYNC_DOWNLOAD_URL_KEY = "initialDatabaseDownloadUrl";

    public static final String SHARED_PREF_NEED_SYNC_KEY = "needSync";
    public static final String SHARED_PREF_NEED_SYNC = "need_sync";
    public static final String SHARED_PREF_NO_NEED_SYNC = "no_need_sync";

    public static final int SERVICE_CUDL_ERROR_CONNECTION_RETRY = 0;
    public static final int SERVICE_CUDL_ERROR_SUBSCRIBE = 1;
    public static final int SERVICE_CUDL_ERROR_SYNC = 2;


    public static final int NOTEBOOK_LOAD_TYPE_LOGS = 0;
    public static final int NOTEBOOK_LOAD_TYPE_ALL = 1;
    public static final int NOTEBOOK_LOAD_TYPE_LOG_FROM_PARENT = 2;

    public static final int CONTACT_PICK_RESULT_OK = 111;
    public static final int CONTACT_PICK_RESULT_CANCEL = 112;
    public static final int CONTACT_PICK_REQUEST_WHO = 222;
    public static final int CONTACT_PICK_REQUEST_WHAT = 223;

    public static final String ONBOARDING_SYNCED_CALENDARS = "onboarding_synced_calendars";

    public static final String ONBOARDING_LOGGED_IN_SALESFORCE = "loggedInSalesforce";
    public static final String ONBOARDING_LOGGED_IN_GOOGLE = "loggedInGoogle";
    public static final String ONBOARDING_LOGGED_IN_EXCHANGE = "loggedInExchange";
    public static final String ONBOARDING_SYNC_GOOGLE = "syncGoogle";
    public static final String ONBOARDING_SYNC_EXCHANGE = "syncExchange";
    public static final String ONBOARDING_SYNC_CONTACTS = "syncContacts";
    public static final String ONBOARDING_SYNC_CALENDARS = "syncCalendars";
    public static final String ONBOARDING_PROCESSING_CONTACT_SYNC = "syncContactsProcessing";
    public static final String ONBOARDING_EXCHANGE_MANUALLTY_ENABLED = "exchangeManuallyEnabled";
    public static final String ONBOARDING_TACTAPP_SOURCE = "tactapp_source";
    public static final String ONBOARDING_SALESFORCE_SOURCE = "salesforce_source";
    public static final String ONBOARDING_EXCHANGE_SOURCE = "exchange_source";
    public static final String ONBOARDING_GOOGLE_SOURCE = "google_source";
    public static final String ONBOARDING_STAGE_EXCHANGE_MANUALLY = "stageExchangeManually";
    public static final String ONBOARDING_EMAIL_EXCHANGE_MANUALLY = "emailExchangeManually";
    public static final String ONBOARDING_SERVER_EXCHANGE_MANUALLY = "serverExchangeManually";
    public static final String ONBOARDING_DOMAIN_EXCHANGE_MANUALY = "domainExchangeManually";
    public static final String ONBOARDING_USERNAME_EXCHANGE_MANUALLY = "usernameExchangeManually";
    public static final String ONBOARDING_PASSWORD_EXCHANGE_MANUALLY = "passwordExchangeManually";
    public static final String ONBOARDING_USER_SELECTION = "userSelection";
    public static final String ONBOARDING_STEP = "step";
    public static final String ONBOARDING_USER_REGISTRATION = "userRegistration";

    public static final int SCROLL_DIRECTION_UP = 0;
    public static final int SCROLL_DIRECTION_DOWN = 1;

    public static final int CONTACT_LIST_MODE_ALL = 0;
    public static final int CONTACT_LIST_MODE_RECENT = 1;
    public static final int CONTACT_LIST_MODE_STARRED = 2;

    public static final int CONTACT_LIST_LOAD_ALL = 0;
    public static final int CONTACT_LIST_LOAD_PERSONS = 1;
    public static final int CONTACT_LIST_LOAD_COMPANIES = 2;

    public static final String CONTACT_ENTITY = "Contact";
    public static final String CONTACT_DETAIL_ENTITY = "ContactDetail";
    public static final String CONTACT_CUSTOM_FIELD_ENTITY = "ContactCustomField";
}
