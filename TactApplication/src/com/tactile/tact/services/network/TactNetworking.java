package com.tactile.tact.services.network;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.tactile.tact.activities.TactApplication;
import com.tactile.tact.consts.TactConst;
import com.tactile.tact.controllers.TactSharedPrefController;
import com.tactile.tact.database.TactDataSource;
import com.tactile.tact.services.events.EventAddSourceError;
import com.tactile.tact.services.events.EventAddSourceSuccess;
import com.tactile.tact.services.events.EventAvailableUsernamesSuccess;
import com.tactile.tact.services.events.EventCheckInitialDBError;
import com.tactile.tact.services.events.EventCheckInitialDBSuccess;
import com.tactile.tact.services.events.EventConfigError;
import com.tactile.tact.services.events.EventConfigSuccess;
import com.tactile.tact.services.events.EventContactSyncSuccess;
import com.tactile.tact.services.events.EventContactsUploadDetailsAmazonError;
import com.tactile.tact.services.events.EventContactsUploadDetailsAmazonSuccess;
import com.tactile.tact.services.events.EventContactsUploadDetailsError;
import com.tactile.tact.services.events.EventContactsUploadDetailsSuccess;
import com.tactile.tact.services.events.EventDeviceInformationError;
import com.tactile.tact.services.events.EventDeviceInformationSuccess;
import com.tactile.tact.services.events.EventFeedItemSyncError;
import com.tactile.tact.services.events.EventFeedItemSyncSuccess;
import com.tactile.tact.services.events.EventHandleProgress;
import com.tactile.tact.services.events.EventLogInError;
import com.tactile.tact.services.events.EventLogInSuccess;
import com.tactile.tact.services.events.EventNoInternet;
import com.tactile.tact.services.events.EventNoInternetSyncService;
import com.tactile.tact.services.events.EventResetPasswordError;
import com.tactile.tact.services.events.EventResetPasswordSuccess;
import com.tactile.tact.services.events.EventSourcesAddedError;
import com.tactile.tact.services.events.EventSourcesAddedSuccess;
import com.tactile.tact.services.events.EventSyncError;
import com.tactile.tact.services.events.EventSyncSuccess;
import com.tactile.tact.services.events.EventTactRegistrationError;
import com.tactile.tact.services.events.EventTactRegistrationSuccess;
import com.tactile.tact.services.events.EventTempLoginError;
import com.tactile.tact.services.events.EventTempLoginSuccess;
import com.tactile.tact.services.network.response.DeviceContactsUploadDataSourceResponse;
import com.tactile.tact.utils.Utils;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Class to handle all networking jobs using volley library
 * Created by leyan on 10/23/14.
 */
public class TactNetworking {

    public static boolean checkInternetConnection(boolean isCritical) {
        if (!Utils.isOnline(TactApplication.getInstance())) {
            EventBus.getDefault().postSticky(new EventHandleProgress(false, false));
            EventBus.getDefault().postSticky(new EventNoInternet(isCritical));
            return false;
        }
        return true;
    }

    public static boolean checkInternetConnectionSyncService() {
        if (!Utils.isOnline(TactApplication.getInstance())) {
            EventBus.getDefault().postSticky(new EventNoInternetSyncService());
            return false;
        }
        return true;
    }

    /**
     * Get the headers for the requests with the credentials
     * @return Map with headers
     */
    public static HashMap<String, String> getMapAuthorizationHeaders(Context context) {
        try {
            HashMap<String, String> authHeader = new HashMap<String, String>();

            if (TactSharedPrefController.isLoggedIn()) {
                authHeader.put("Authorization", TactSharedPrefController.getCredentialsHeader());
            } else if (TactSharedPrefController.isTempLoggedIn()) {
                authHeader.put("Authorization", TactSharedPrefController.getTempCredentialsHeader());
            }

            authHeader.put("X-TACT-APP-VERSION", TactConst.TACT_APP_VERSION);
            authHeader.put("X-TACT-DB-DATA-VERSION", Utils.getXTactDbDataVersion(context));
            authHeader.put("X-TACT-SYNC-REVISION", TactConst.TACT_SYNC_REVISION);
            authHeader.put("TACT-TIME-ZONE", TactConst.TACT_TIME_ZONE);
            authHeader.put("TACT-GMT-OFFSET", TactConst.TACT_GMT_OFFSET);
            authHeader.put("TACT-CUDL-VERSION", TactConst.TACT_CUDL_VERSION);
            authHeader.put("TACT-CUDL-TIMESTAMP", TactConst.TACT_CUDL_TIMESTAMP);

            return authHeader;
        } catch (Exception e) {
            //TODO: this is a critical exception that does not allow continue the network call
            return null;
        }
    }

    /**
     * Get the headers for the requests with the credentials
     * @return Map with headers
     */
    public static HashMap<String, String> getMapAuthorizationHeadersNoCredentials(Context context) {
        try {
            HashMap<String, String> authHeader = new HashMap<String, String>();

            authHeader.put("Accept", "*/*");

            authHeader.put("X-TACT-APP-VERSION", TactConst.TACT_APP_VERSION);
            authHeader.put("X-TACT-DB-DATA-VERSION", Utils.getXTactDbDataVersion(context));
            authHeader.put("X-TACT-SYNC-REVISION", TactConst.TACT_SYNC_REVISION);
            authHeader.put("TACT-TIME-ZONE", TactConst.TACT_TIME_ZONE);
            authHeader.put("TACT-GMT-OFFSET", TactConst.TACT_GMT_OFFSET);
            authHeader.put("TACT-CUDL-VERSION", TactConst.TACT_CUDL_VERSION);
            authHeader.put("TACT-CUDL-TIMESTAMP", TactConst.TACT_CUDL_TIMESTAMP);

            return authHeader;
        } catch (Exception e) {
            //TODO: this is a critical exception that does not allow continue the network call
            return null;
        }
    }

    public static HashMap<String, String> getMapAuthorizationHeaders(Context context, String username, String password) {
        try {
            HashMap<String, String> authHeader = new HashMap<String, String>();

            authHeader.put("Authorization", TactSharedPrefController.getCredentialsHeader(username, password));

            authHeader.put("X-TACT-APP-VERSION", TactConst.TACT_APP_VERSION);
            authHeader.put("X-TACT-DB-DATA-VERSION", Utils.getXTactDbDataVersion(context));
            authHeader.put("X-TACT-SYNC-REVISION", TactConst.TACT_SYNC_REVISION);
            authHeader.put("TACT-TIME-ZONE", TactConst.TACT_TIME_ZONE);
            authHeader.put("TACT-GMT-OFFSET", TactConst.TACT_GMT_OFFSET);
            authHeader.put("TACT-CUDL-VERSION", TactConst.TACT_CUDL_VERSION);
            authHeader.put("TACT-CUDL-TIMESTAMP", TactConst.TACT_CUDL_TIMESTAMP);

            return authHeader;
        } catch (Exception e) {
            //TODO: this is a critical exception that does not allow continue the network call
            return null;
        }
    }

    /**
     * Get the headers for the requests with the temporal credentials
     * @return Map with headers
     */
    public static HashMap<String, String> getMapAuthorizationHeadersTempCredentials(Context context) {
        try {
            HashMap<String, String> authHeader = new HashMap<String, String>();

            authHeader.put("Authorization", TactSharedPrefController.getTempCredentialsHeader());

            authHeader.put("X-TACT-APP-VERSION", TactConst.TACT_APP_VERSION);
            authHeader.put("X-TACT-DB-DATA-VERSION", Utils.getXTactDbDataVersion(context));
            authHeader.put("X-TACT-SYNC-REVISION", TactConst.TACT_SYNC_REVISION);
            authHeader.put("TACT-TIME-ZONE", TactConst.TACT_TIME_ZONE);
            authHeader.put("TACT-GMT-OFFSET", TactConst.TACT_GMT_OFFSET);
            authHeader.put("TACT-CUDL-VERSION", TactConst.TACT_CUDL_VERSION);
            authHeader.put("TACT-CUDL-TIMESTAMP", TactConst.TACT_CUDL_TIMESTAMP);

            return authHeader;
        } catch (Exception e) {
            //TODO: this is a critical exception that does not allow continue the network call
            return null;
        }
    }

    public static void callConfig(final Context context) {
        try {
            if (TactNetworking.checkInternetConnection(true)) {
                Log.w("NETWORKING", "A web service call started to the url: " + TactNetworking.getURL() + TactConst.END_POINT_CONFIG);
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                        TactNetworking.getURL() + TactConst.END_POINT_CONFIG,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                Log.w("NETWORKING", "A call to config succeed with response: " + jsonObject.toString());
                                EventBus.getDefault().post(new EventConfigSuccess(jsonObject));
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.w("NETWORKING", "A call to config fail with message " + volleyError.getMessage());
                        int errorCode = -1;
                        if (volleyError != null && volleyError.networkResponse != null) {
                            errorCode = volleyError.networkResponse.statusCode;
                        }
                        EventBus.getDefault().post(new EventConfigError(false, false, errorCode, volleyError.getMessage()));
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return TactNetworking.getMapAuthorizationHeaders(context);
                    }
                };
                TactApplication.getInstance().addToRequestQueue(request, TactConst.CALL_TAG_CONFIG);
            }
        } catch (Exception e) {
            Log.w("NETWORKING", "A call to config fail with message " + e.getMessage());
            EventBus.getDefault().post(new EventConfigError(false, false, 0, e.getMessage()));
        }
    }

    /**
     * Call to sources added web service
     */
    public static void callSync(final Context context) {
        try {
            if (TactNetworking.checkInternetConnection(true)) {
                Log.w("NETWORKING", "A web service call started to the url: " + TactNetworking.getURL() + TactConst.END_POINT_SYNC + " with parameters: " + TactDataSource.getSyncRequest());
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                        TactNetworking.getURL() + TactConst.END_POINT_SYNC,
                        TactDataSource.getSyncRequest(),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                Log.w("NETWORKING", "A call to contacts sync succeed with response: " + jsonObject.toString());
                                EventBus.getDefault().post(new EventSyncSuccess(jsonObject));
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.w("NETWORKING", "A call to contacts sync fail with message " + volleyError.getMessage());
                        int errorCode = -1;
                        if (volleyError != null && volleyError.networkResponse != null) {
                            errorCode = volleyError.networkResponse.statusCode;
                        }
                        EventBus.getDefault().post(new EventSyncError(false, errorCode, volleyError.getMessage()));
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return TactNetworking.getMapAuthorizationHeaders(context);
                    }
                };
                TactApplication.getInstance().addToRequestQueue(request, TactConst.CALL_TAG_FEEDITEMS_SYNC);
            }
        } catch (Exception e) {
            Log.w("NETWORKING", "A call to feed items sync fail with message " + e.getMessage());
            EventBus.getDefault().post(new EventFeedItemSyncError(false, 0, e.getMessage()));
        }
    }

    /**
     * Call to sources added web service
     */
    public static void callSyncFeedItems(final Context context) {
        try {
                Log.w("NETWORKING", "A web service call started to the url: " + TactNetworking.getURL() + TactConst.END_POINT_FEED_ITEM_SYNC + " with parameters: " + TactDataSource.getFeedItemsSyncRequest(TactDataSource.getItemsNeedSync()));
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                        TactNetworking.getURL() + TactConst.END_POINT_FEED_ITEM_SYNC,
                        TactDataSource.getFeedItemsSyncRequest(TactDataSource.getItemsNeedSync()),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                Log.w("NETWORKING", "A call to feed items sync succeed with response: " + jsonObject.toString());
                                EventBus.getDefault().post(new EventFeedItemSyncSuccess(jsonObject));
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.w("NETWORKING", "A call to feed items sync fail with message " + volleyError.getMessage());
                        int errorCode = -1;
                        if (volleyError != null && volleyError.networkResponse != null) {
                            errorCode = volleyError.networkResponse.statusCode;
                        }
                        EventBus.getDefault().post(new EventFeedItemSyncError(false, errorCode, volleyError.getMessage()));
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return TactNetworking.getMapAuthorizationHeaders(context);
                    }
                };
                TactApplication.getInstance().addToRequestQueue(request, TactConst.CALL_TAG_FEEDITEMS_SYNC);
        } catch (Exception e) {
            Log.w("NETWORKING", "A call to feed items sync fail with message " + e.getMessage());
            EventBus.getDefault().post(new EventFeedItemSyncError(false, 0, e.getMessage()));
        }
    }

    /**
     * Call to sources added web service
     */
    public static void callSourcesAdded(final Context context) {
        try {
            if (TactNetworking.checkInternetConnectionSyncService()) {
                Log.w("NETWORKING", "A web service call started to the url: " + TactNetworking.getURL() + TactConst.END_POINT_SOURCES_ADDED);
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                        TactNetworking.getURL() + TactConst.END_POINT_SOURCES_ADDED,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                Log.w("NETWORKING", "A call to sources added succeed with response: " + jsonObject.toString());
                                EventBus.getDefault().post(new EventSourcesAddedSuccess());
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.w("NETWORKING", "A call to sources added fail with message " + volleyError.getMessage());
                        int errorCode = -1;
                        if (volleyError != null && volleyError.networkResponse != null) {
                            errorCode = volleyError.networkResponse.statusCode;
                        }
                        EventBus.getDefault().post(new EventSourcesAddedError(false, errorCode, volleyError.getMessage()));
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return TactNetworking.getMapAuthorizationHeaders(context);
                    }
                };
                TactApplication.getInstance().addToRequestQueue(request, TactConst.CALL_TAG_SOURCES_ADDED);
            }
        } catch (Exception e) {
            Log.w("NETWORKING", "A call to sources added fail with message " + e.getMessage());
            EventBus.getDefault().post(new EventSourcesAddedError(false, 0, e.getMessage()));
        }
    }

    /**
     * Call the register temporal user web service
     */
    public static void callTempLogin(final Context context) {
        try {
            if (TactNetworking.checkInternetConnection(true)) {
                Log.w("NETWORKING", "A web service call started to the url: " + TactNetworking.getURL() + TactConst.END_POINT_REGISTER);
                Log.w("NETWORKING", "The parameters are: " + TactDataSource.getTempLoginRequest());
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                        TactNetworking.getURL() + TactConst.END_POINT_REGISTER,
                        TactDataSource.getTempLoginRequest(),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                Log.w("NETWORKING", "A call to temp login succeed with response: " + jsonObject.toString());
                                //if success trigger the success event
                                EventBus.getDefault().postSticky(new EventTempLoginSuccess(jsonObject));
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.w("NETWORKING", "A call to temp login fail with message: " + volleyError.getMessage());
                        //if error trigger the error event
                        EventBus.getDefault().postSticky(new EventTempLoginError(volleyError.getMessage()));
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return TactNetworking.getMapAuthorizationHeadersTempCredentials(context);
                    }
                };
                TactApplication.getInstance().addToRequestQueue(request, TactConst.CALL_TAG_TEMP_REGISTER);
            }
        } catch (Exception e) {
            Log.w("NETWORKING", "A call to temp login fail with message " + e.getMessage());
            //if error trigger the error event
            EventBus.getDefault().postSticky(new EventTempLoginError(e.getMessage()));
        }
    }

    /**
     * Call to check database web service
     */
    public static void callCheckDatabaseStatus(final boolean justCheck, final Context context) {
        try {
            if (TactNetworking.checkInternetConnectionSyncService()) {
                Log.w("NETWORKING", "A web service call started to the url: " + TactNetworking.getURL() + TactConst.END_POINT_ME);
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                        TactNetworking.getURL() + TactConst.END_POINT_ME,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                Log.w("NETWORKING", "A call to check database succeed with response: " + jsonObject.toString());
                                //if success trigger the success event
                                EventBus.getDefault().postSticky(new EventCheckInitialDBSuccess(jsonObject, justCheck));
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.w("NETWORKING", "A call to check database fail with message: " + volleyError.getMessage());
                        //if error trigger the error event
                        int errorCode = -1;
                        if (volleyError != null && volleyError.networkResponse != null) {
                            errorCode = volleyError.networkResponse.statusCode;
                        }
                        EventBus.getDefault().postSticky(new EventCheckInitialDBError(justCheck, false, errorCode, volleyError.getMessage()));
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        //in onboarding with temp credentials and if login credentials ready with that then
                        return TactNetworking.getMapAuthorizationHeaders(context);
                    }
                };
                TactApplication.getInstance().addToRequestQueue(request, TactConst.CALL_TAG_ME);
            }
        } catch (Exception e) {
            Log.w("NETWORKING", "A call to check database fail with message " + e.getMessage());
            //if error trigger the error event
            EventBus.getDefault().postSticky(new EventCheckInitialDBError(justCheck, false, 0, e.getMessage()));
        }
    }

    /**
     * Call to check database web service
     */
    public static void callAddSource(final Context context, final String source, final Object ...args) {
        try {
            if (TactNetworking.checkInternetConnection(true)) {
                String url;
                if (source.equals("google")){
                    url = (String)args[0];
                }
                else {
                    url = TactNetworking.getURL() + TactConst.END_POINT_SOURCES;
                }
                Log.w("NETWORKING", "A web service call started to the url: " + url);
                JsonArrayRequest request = new JsonArrayRequest(url,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray jsonObject) {
                                Log.w("NETWORKING", "A call to add source succeed with response: " + jsonObject.toString());
                                //if success trigger the success event
                                EventBus.getDefault().postSticky(new EventAddSourceSuccess(jsonObject));
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.w("NETWORKING", "A call to add source fail with message: " + volleyError.getMessage());
                        //if error trigger the error event
                        try {
                            int errorCode = -1;
                            if (volleyError != null && volleyError.networkResponse != null) {
                                errorCode = volleyError.networkResponse.statusCode;
                            }
                            EventBus.getDefault().postSticky(new EventAddSourceError(false, errorCode, volleyError.getMessage()));
                        } catch (Exception e) {
                            EventBus.getDefault().postSticky(new EventAddSourceError(false, 0, e.getMessage()));
                        }
                    }
                }
                ) {
                    @Override
                    public int getMethod() {
                        if (source.equals("google"))
                            return Method.GET;
                        else
                            return Method.POST;
                    }

                    @Override
                    public byte[] getBody() {
                        if (source.equals("google"))
                            return null;
                        else
                            return TactDataSource.getAddSourceRequest(source, args).toString().getBytes();
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        //in onboarding with temp credentials and if login credentials ready with that then
                        return TactNetworking.getMapAuthorizationHeaders(context);
                    }

                };
                TactApplication.getInstance().addToRequestQueue(request, TactConst.CALL_TAG_SOURCES);
            }
        } catch (Exception e) {
            Log.w("NETWORKING", "A call to add source fail with message " + e.getMessage());
            //if error trigger the error event
            EventBus.getDefault().postSticky(new EventAddSourceError(false, 0, e.getMessage()));
        }
    }

    /**
     * Call to login web service
     * @param username - Username
     * @param password - Password
     */
    public static void callLogin(final Context context, final String username, final String password) {
        try {
            if (TactNetworking.checkInternetConnection(true)) {
                Log.w("NETWORKING", "A web service call started to the url: " + TactNetworking.getURL() + TactConst.END_POINT_AUTH);
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                        TactNetworking.getURL() + TactConst.END_POINT_AUTH,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                Log.w("NETWORKING", "A call to login succeed");
                                //if success trigger the success event
                                EventBus.getDefault().post(new EventLogInSuccess(username, password, jsonObject));
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.w("NETWORKING", "A call to login fail with message: " + volleyError.getMessage());
                        //if error trigger the error event
                        int errorCode = -1;
                        if (volleyError != null && volleyError.networkResponse != null) {
                            errorCode = volleyError.networkResponse.statusCode;
                        }
                        EventBus.getDefault().post(new EventLogInError(false, errorCode, volleyError.getMessage()));
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        //in login with the entered credentials
                        return TactNetworking.getMapAuthorizationHeaders(context, username, password);
                    }
                };
                TactApplication.getInstance().addToRequestQueue(request, TactConst.CALL_TAG_LOGIN);
            }
        } catch (Exception e) {
            Log.w("NETWORKING", "A call to login fail with message " + e.getMessage());
            //if error trigger the error event
            EventBus.getDefault().post(new EventLogInError(false, 0, e.getMessage()));
        }
    }

    /**
     * Call to reset password web service
     * @param email - Email related to aacount
     */
    public static void callResetPassword(final Context context, String email) {
        try {
            if (TactNetworking.checkInternetConnection(true)) {
                Log.w("NETWORKING", "A web service call started to the url: " + TactNetworking.getURL() + TactConst.END_POINT_RESET_PASSWORD);
                Log.w("NETWORKING", "The parameters are: " + TactDataSource.getResetPasswordRequest(email));
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                        TactNetworking.getURL() + TactConst.END_POINT_RESET_PASSWORD,
                        TactDataSource.getResetPasswordRequest(email),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                Log.w("NETWORKING", "A call to reset password succeed with response: " + jsonObject.toString());
                                EventBus.getDefault().post(new EventResetPasswordSuccess());
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.w("NETWORKING", "A call to reset password fail with message: " + volleyError.getMessage());
                        int errorCode = -1;
                        if (volleyError != null && volleyError.networkResponse != null) {
                            errorCode = volleyError.networkResponse.statusCode;
                        }
                        EventBus.getDefault().post(new EventResetPasswordError(true, errorCode, volleyError.getMessage()));
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        //Add headers without credentials
                        return TactNetworking.getMapAuthorizationHeadersNoCredentials(context);
                    }
                };
                TactApplication.getInstance().addToRequestQueue(request, TactConst.CALL_TAG_RESET_PASSWORD);
            }
        } catch (Exception e) {
            Log.w("NETWORKING", "A call to reset password fail with message: " + e.getMessage());
            EventBus.getDefault().post(new EventResetPasswordError(false, 0, e.getMessage()));
        }
    }

    /**
     * Send to the server the device information
     */
    public static void callDeviceInformation(final Context context){
        try {
            if (TactNetworking.checkInternetConnection(true)) {
                Log.w("NETWORKING", "A web service call started to the url: " + TactNetworking.getURL() + TactConst.END_POINT_SOURCES);
                Log.w("NETWORKING", "The parameters are: " + TactDataSource.getDeviceInformationRequest());
                JsonArrayRequest request = new JsonArrayRequest(
                        TactNetworking.getURL() + TactConst.END_POINT_SOURCES,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray jsonObject) {
                                Log.w("NETWORKING", "A call to device information succeed with response: " + jsonObject.toString());
                                EventBus.getDefault().postSticky(new EventDeviceInformationSuccess());
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.w("NETWORKING", "A call to device information fail with message: " + volleyError.getMessage());
                        int errorCode = -1;
                        if (volleyError != null && volleyError.networkResponse != null) {
                            errorCode = volleyError.networkResponse.statusCode;
                        }
                        EventBus.getDefault().postSticky(new EventDeviceInformationError(false, errorCode, volleyError.getMessage()));
                    }
                }
                ) {
                    @Override
                    public int getMethod() {
                        return Method.POST;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return TactNetworking.getMapAuthorizationHeaders(context);
                    }

                    @Override
                    public byte[] getBody() {
                        return TactDataSource.getDeviceInformationRequest().toString().getBytes();
                    }

                };
                TactApplication.getInstance().addToRequestQueue(request, TactConst.CALL_TAG_DEVICE_INFORMATION);
            }
        } catch (Exception e) {
            Log.w("NETWORKING", "A call to device information fail with message " + e.getMessage());
            EventBus.getDefault().postSticky(new EventDeviceInformationError(false, 0, e.getMessage()));
        }
    }

    /**
     * Get the headers needed to send data to amazon
     */
    public static void callContactsUploadDetails(final Context context) {
        try {
            if (TactNetworking.checkInternetConnection(true)) {
                Log.w("NETWORKING", "A web service call started to the url: " + TactNetworking.getURL() + TactConst.END_POINT_DEVICE_CONTACT_UPLOAD + "/" + Build.ID);
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                        TactNetworking.getURL() + TactConst.END_POINT_DEVICE_CONTACT_UPLOAD + "/" + Build.ID,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                Log.w("NETWORKING", "A call to contacts upload details succeed with response: " + jsonObject.toString());
                                EventBus.getDefault().postSticky(new EventContactsUploadDetailsSuccess(jsonObject));
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.w("NETWORKING", "A call to contacts upload details fail with message: " + volleyError.getMessage());
                        int errorCode = -1;
                        if (volleyError != null && volleyError.networkResponse != null) {
                            errorCode = volleyError.networkResponse.statusCode;
                        }
                        EventBus.getDefault().postSticky(new EventContactsUploadDetailsError(false, errorCode, volleyError.getMessage()));
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return TactNetworking.getMapAuthorizationHeaders(context);
                    }
                };
                TactApplication.getInstance().addToRequestQueue(request, TactConst.CALL_TAG_DEVICE_CONTACT_UPLOAD);
            }
        } catch (Exception e) {
            Log.w("NETWORKING", "A call to contacts upload details fail with message " + e.getMessage());
            //if error trigger the error event
            EventBus.getDefault().postSticky(new EventContactsUploadDetailsError(false, 0, e.getMessage()));
        }

    }

    /**
     * Send the zip with contacts and calendars to amazon
     * @param files_with_path
     * @param data
     */
    public static void callContactsUploadDetailsAmazon(final Context context, String files_with_path, DeviceContactsUploadDataSourceResponse data){
        final MultipartEntity entity = TactDataSource.getContactsUploadDetailsAmazonRequest(files_with_path, data);
        try {
            if (TactNetworking.checkInternetConnection(true)) {
                Log.w("NETWORKING", "A web service call started to the url: " + data.getUrl());
                StringRequest request = new StringRequest(
                        Request.Method.POST,
                        data.getUrl(),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String jsonObject) {
                                Log.w("NETWORKING", "A call to contacts upload to amazon succeed with response: " + jsonObject.toString());
                                Utils.deleteFile(context, "initialData.zip");
                                EventBus.getDefault().postSticky(new EventContactsUploadDetailsAmazonSuccess(jsonObject));
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.w("NETWORKING", "A call to contacts upload to amazon fail with message: " + volleyError.getMessage());
                        try {
                            Utils.deleteFile(context, "initialData.zip");
                            int errorCode = -1;
                            if (volleyError != null && volleyError.networkResponse != null) {
                                errorCode = volleyError.networkResponse.statusCode;
                            }
                            EventBus.getDefault().postSticky(new EventContactsUploadDetailsAmazonError(false, errorCode, volleyError.getMessage()));
                        } catch (Exception e) {
                            EventBus.getDefault().postSticky(new EventContactsUploadDetailsAmazonError(false, 0, volleyError.getMessage()));
                        }
                    }
                }
                ) {
                    @Override
                    public String getBodyContentType() {
                        return entity.getContentType().getValue();
                    }

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        try {
                            entity.writeTo(bos);
                        } catch (IOException e) {
                            VolleyLog.e("IOException writing to ByteArrayOutputStream");
                        }
                        return bos.toByteArray();
                    }
                };
                TactApplication.getInstance().addToRequestQueue(request, TactConst.CALL_TAG_DEVICE_CONTACT_UPLOAD_AMAZON);
            }
        } catch (Exception e) {
            Log.w("NETWORKING", "A call to contacts upload to amazon fail with message " + e.getMessage());
            EventBus.getDefault().postSticky(new EventContactsUploadDetailsAmazonError(false, 0, e.getMessage()));
        }

    }

    public static void callGetAvailableUsernames(final Context context){
        try {
            if (TactNetworking.checkInternetConnection(true)) {
                Log.w("NETWORKING", "A web service call started to the url: " + TactNetworking.getURL() + TactConst.END_POINT_AVAILABLE_USERNAMES);
                JsonArrayRequest request = new JsonArrayRequest(
                        TactNetworking.getURL() + TactConst.END_POINT_AVAILABLE_USERNAMES,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray jsonObject) {
                                Log.w("NETWORKING", "A call to get available usernames succeed with response: " + jsonObject.toString());
                                EventBus.getDefault().postSticky(new EventAvailableUsernamesSuccess(jsonObject));
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.w("NETWORKING", "A call to get available usernames fail with message: " + volleyError.getMessage());
                        int errorCode = -1;
                        if (volleyError != null && volleyError.networkResponse != null) {
                            errorCode = volleyError.networkResponse.statusCode;
                        }
                        EventBus.getDefault().postSticky(new EventTactRegistrationError(false, errorCode, volleyError.getMessage()));
                    }
                }) {
                    @Override
                    public int getMethod() {
                        return Request.Method.GET;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return TactNetworking.getMapAuthorizationHeaders(context);
                    }
                };
                TactApplication.getInstance().addToRequestQueue(request, TactConst.CALL_TAG_GET_AVAILABLES_NAMES);
            }
        } catch (Exception e) {
            Log.w("NETWORKING", "A call to get available usernames fail with message " + e.getMessage());
            //if error trigger the error event
            EventBus.getDefault().postSticky(new EventTactRegistrationError(false, 0, e.getMessage()));
        }
    }

    public static void callUpdateRegistration(final Context context, final String user, final String pass){
        try {
            if (TactNetworking.checkInternetConnection(true)) {
                Log.w("NETWORKING", "A web service call started to the url: " + TactNetworking.getURL() + TactConst.END_POINT_ME);
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT,
                        TactNetworking.getURL() + TactConst.END_POINT_ME,
                        TactDataSource.getUpdateRegistration(user, pass),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                Log.w("NETWORKING", "A call to update registration succeed with response: " + jsonObject.toString());
                                EventBus.getDefault().postSticky(new EventTactRegistrationSuccess(jsonObject));
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.w("NETWORKING", "A call to update registration fail with message: " + volleyError.getMessage());
                        int errorCode = -1;
                        if (volleyError != null && volleyError.networkResponse != null) {
                            errorCode = volleyError.networkResponse.statusCode;
                        }
                        EventBus.getDefault().postSticky(new EventTactRegistrationError(false, errorCode, volleyError.getMessage()));
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return TactNetworking.getMapAuthorizationHeaders(context);
                    }
                };
                TactApplication.getInstance().addToRequestQueue(request, TactConst.CALL_TAG_PUT_UPDATE_REGISTRATION);
            }
        } catch (Exception e) {
            Log.w("NETWORKING", "A call to update registration fail with message " + e.getMessage());
            //if error trigger the error event
            EventBus.getDefault().postSticky(new EventTactRegistrationError(false, 0, e.getMessage()));
        }
    }

    /**
     * TODO: Take a look at this method and how to improve the call!!!!
     * @return
     */
    public static String getDownloadDatabaseURL() {
        try {
            if (TactNetworking.checkInternetConnection(true)) {
                HttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(TactNetworking.getURL() + TactConst.END_POINT_DOWNLOAD_DB);

                // Here's the trickery I talked about! We want to get the 302 redirect response and extract
                // the Location header value so we can use it in out image view without the pesky auth header.
                HttpParams params = httpGet.getParams();
                params.setParameter(ClientPNames.HANDLE_REDIRECTS, Boolean.FALSE);
                httpGet.setParams(params);

                // Here's the culprit... We need it now, but it breaks if we let volley or
                // android apache client try to get the image from the original url.
                // I.e., A 400 error (Bad Request) happens.
                httpGet.addHeader("Authorization", TactSharedPrefController.getCredentialsHeader());
//        //if onboarding
                httpGet.addHeader("X-TACT-APP-VERSION", TactConst.TACT_APP_VERSION);
                httpGet.addHeader("X-TACT-DB-DATA-VERSION", TactConst.TACT_DB_DATA_VERSION);
                httpGet.addHeader("X-TACT-SYNC-REVISION", TactConst.TACT_SYNC_REVISION);
                httpGet.addHeader("TACT-TIME-ZONE", TactConst.TACT_TIME_ZONE);
                httpGet.addHeader("TACT-GMT-OFFSET", TactConst.TACT_GMT_OFFSET);
                httpGet.addHeader("TACT-CUDL-VERSION", TactConst.TACT_CUDL_VERSION);
                httpGet.addHeader("TACT-CUDL-TIMESTAMP", TactConst.TACT_CUDL_TIMESTAMP);

                try {
                    HttpResponse response = client.execute(httpGet);
                    if (response.getStatusLine().getStatusCode() == 302) {
                        Header locationHeader = response.getFirstHeader("Location");
                        if (locationHeader != null) {
                            return locationHeader.getValue();
                        }
                    }
                } catch (ClientProtocolException e) {
                    throw e;
                } catch (IOException e) {
                    throw e;
                }
                return null;
            } else throw new Exception("No internet");


        } catch (Exception e) {
            Log.w("NETWORKING", "Exception trying to get the download url with message: " + e.getMessage());
            return null;
        }
    }

    public static String getTactImageUrl(String url){
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);

        HttpParams params = httpGet.getParams();
        params.setParameter(ClientPNames.HANDLE_REDIRECTS, Boolean.FALSE);
        httpGet.setParams(params);

        try{
            httpGet.addHeader("Authorization", TactSharedPrefController.getCredentialsHeader());
        }
        catch (Exception e){}
        httpGet.addHeader("X-TACT-APP-VERSION", TactConst.TACT_APP_VERSION);
        httpGet.addHeader("X-TACT-DB-DATA-VERSION", TactConst.TACT_DB_DATA_VERSION);
        httpGet.addHeader("X-TACT-SYNC-REVISION", TactConst.TACT_SYNC_REVISION);
        httpGet.addHeader("TACT-TIME-ZONE", TactConst.TACT_TIME_ZONE);
        httpGet.addHeader("TACT-GMT-OFFSET", TactConst.TACT_GMT_OFFSET);
        httpGet.addHeader("TACT-CUDL-VERSION", TactConst.TACT_CUDL_VERSION);
        httpGet.addHeader("TACT-CUDL-TIMESTAMP", TactConst.TACT_CUDL_TIMESTAMP);

        try {
            HttpResponse response = client.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 302) {
                Header locationHeader = response.getFirstHeader("Location");
                if (locationHeader != null) {
                    return locationHeader.getValue();
                }
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Get the service call url
     * @return String with the bas ewen service url
     */
    public static String getURL() {
        switch (TactApplication.enviroment) {
            case TactTestEnvironment:
                return TactConst.SERVER_URL_TEST_ENVIROMENT;
            case TactStagingEnvironment:
                return TactConst.SERVER_URL_TEST_ENVIROMENT;
            case TactBetaEnvironment:
                return TactConst.SERVER_URL_PRODUCTION_ENVIROMENT;
            case TactEapEnvironment:
                return TactConst.SERVER_URL_PRODUCTION_ENVIROMENT;
            case TactProductionEnvironment:
                return TactConst.SERVER_URL_PRODUCTION_ENVIROMENT;
            case TactLocalEnvironment:
                return TactConst.SERVER_URL_LOCAL_ENVIROMENT;
            default:
                return TactConst.SERVER_URL_TEST_ENVIROMENT;
        }
    }

    /**
     * Get the oauth service call url
     * @return String with the bas ewen service url
     */
    public static String getOauthURL(){
        switch (TactApplication.enviroment) {
            case TactTestEnvironment:
                return TactConst.SERVER_OAUTH_URL_TEST_ENVIROMENT;
            case TactStagingEnvironment:
                return TactConst.SERVER_OAUTH_URL_TEST_ENVIROMENT;
            case TactBetaEnvironment:
                return TactConst.SERVER_OAUTH_URL_PRODUCTION_ENVIROMENT;
            case TactEapEnvironment:
                return TactConst.SERVER_OAUTH_URL_PRODUCTION_ENVIROMENT;
            case TactProductionEnvironment:
                return TactConst.SERVER_OAUTH_URL_PRODUCTION_ENVIROMENT;
            case TactLocalEnvironment:
                return TactConst.SERVER_OAUTH_URL_LOCAL_ENVIROMENT;
            default:
                return TactConst.SERVER_OAUTH_URL_TEST_ENVIROMENT;
        }
    }

}
