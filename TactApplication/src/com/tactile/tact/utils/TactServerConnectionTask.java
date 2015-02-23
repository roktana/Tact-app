package com.tactile.tact.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tactile.tact.fragments.FragmentOnboardingTactRegistration;
import com.tactile.tact.services.events.EventTactRegistrationError;
import com.tactile.tact.services.events.EventTactRegistrationSuccess;
import com.tactile.tact.services.network.APIResponse;
import com.tactile.tact.services.network.APITextResponse;
import com.tactile.tact.services.network.ErrorResponse;
import com.tactile.tact.services.network.TactNetworking;
import com.tactile.tact.services.network.response.UpdateRegistrationResponse;

import org.json.JSONArray;

import de.greenrobot.event.EventBus;

public class TactServerConnectionTask extends AsyncTask<Void, Void, Void> {

//    // Private variables
//    private String[] args;
//    private Context context;
//
//    // Static variables
//    public static FragmentOnboardingTactRegistration fragment;
//    public static TactOperationType operationType;
//    public static enum TactOperationType {
//        GetTactUserNames,
//        TactRegistration,
//    }
//
//    /**
//     * TactServerConnectionTask constructor
//     * @param originFragment
//     * @param incomingOperation
//     * @param args
//     */
//    public TactServerConnectionTask(Context context, FragmentOnboardingTactRegistration originFragment, TactOperationType incomingOperation, String... args) {
//        fragment = originFragment;
//        this.args = args;
//        operationType = incomingOperation;
//        this.context = context;
//    }
//
//    /**
//     * Start the operation and handle the success/fail
//     * @param param
//     * @return null
//     */
    @Override
    protected Void doInBackground(Void... param) {return null;}
//        switch(operationType) {
//            case GetTactUserNames:
//                TactNetworking.callGetAvailableUsernames(context);
//                break;
//            case TactRegistration:
//                if (args.length>1) {
//                    TactNetworking.callUpdateRegistration(context, args[0], args[1]);
//                }
//        }
//        return null;
//    }
//
//    /**
//     * Send to the activity the result of the request.
//     * @param param
//     */
//    private static void sendCallBack(APIResponse param) {
//        switch (operationType) {
//            case GetTactUserNames:
//                String response = null;
//                if(param != null && param instanceof ErrorResponse) {
//                    try {
//                        response = Utils.getResponseError(param);
//                    } catch (Exception e) {}
//                } else if(param instanceof APITextResponse) {
//                    try {
//                        String call = ((APITextResponse) param).responseText;
//                        JSONArray jsonResponse = new JSONArray(call);
//                        if(jsonResponse.length()>0) {
//                            response = (String) jsonResponse.get(0);
//                        }} catch (Exception e) {}
//                }
//                fragment.taskTactCheckUserResponse(response);
//                break;
//            case TactRegistration:
//                if(param instanceof UpdateRegistrationResponse) {
//                    boolean result = ((UpdateRegistrationResponse) param).isSuccess();
//                    if (result) {
//                        fragment.taskTacktRegistrationResponse("ok");
//                    }
//                } else if (param instanceof ErrorResponse) {
//                    try {
//                        fragment.taskTacktRegistrationResponse(Utils.getResponseError(param));
//                    } catch (Exception e) {}
//                }
//                break;
//        }
//    }
//
//    public void onEvent(EventTactRegistrationSuccess eventTactRegistrationSuccess){
//        EventBus.getDefault().removeStickyEvent(eventTactRegistrationSuccess);
//        GsonBuilder gsonBuilder = new GsonBuilder();
//        Gson gson = gsonBuilder.create();
//        APIResponse response = (APIResponse) gson.fromJson(eventTactRegistrationSuccess.response.toString(), APIResponse.class);
//        sendCallBack(response);
//    }
//
//    public void onEvent(EventTactRegistrationError eventTactRegistrationError){
//        EventBus.getDefault().removeStickyEvent(eventTactRegistrationError);
//        fragment.status = FragmentOnboardingTactRegistration.connectionStatus.TactConnectionError;
//        GsonBuilder gsonBuilder = new GsonBuilder();
//        Gson gson = gsonBuilder.create();
//        APIResponse response = (APIResponse) gson.fromJson(eventTactRegistrationError.getError().getMessage(), APIResponse.class);
//        sendCallBack(response);
//    }
}
