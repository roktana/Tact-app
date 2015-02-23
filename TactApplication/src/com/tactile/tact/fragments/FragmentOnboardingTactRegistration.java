package com.tactile.tact.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tactile.tact.R;
import com.tactile.tact.activities.TactApplication;
import com.tactile.tact.consts.TactConst;
import com.tactile.tact.controllers.TactSharedPrefController;
import com.tactile.tact.services.events.EventHandleProgress;
import com.tactile.tact.services.events.EventMoveNextFragmentOnboarding;
import com.tactile.tact.services.events.EventOnboardingChanges;
import com.tactile.tact.services.events.EventTactRegistrationError;
import com.tactile.tact.services.events.EventTactRegistrationSuccess;
import com.tactile.tact.services.network.TactNetworking;
import com.tactile.tact.utils.Utils;

import org.json.JSONArray;

import de.greenrobot.event.EventBus;

/**
 * Created by sebafonseca on 10/31/14.
 */
public class FragmentOnboardingTactRegistration extends FragmentTactBase {

//    // Static variables
//    public connectionStatus status; // connectionStatus.TactUserSelection;
//
//    public static enum connectionStatus {
//        TactUserSelection,
//        TactPasswordSelection,
//        TactConnectionError,
//    }
//
//    public static enum TactOperationType {
//        GetTactUserNames,
//        TactRegistration,
//    }
//
//    private TextView sourceUser;
//    private LinearLayout userPart;
//    private LinearLayout passwordPart;
//    private EditText textUser;
//    private TextView loginError;
//    private EditText textPassword;
//    private EditText textConfirmPassword;
//    private Button setPasswordButton;
//    private TactOperationType operationType;
//    private Button continueButton;
//    private Boolean validating = false;
//    private View rootView;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        rootView = inflater.inflate(R.layout.on_boarding_tact_registration, container, false);
//
//        setViewItems(rootView);
//
//        if (TactSharedPrefController.getOnboardingUserSelection()){
//            status = connectionStatus.TactUserSelection;
//            checkSourceAccounts();
//        }
//        else {
//            status = connectionStatus.TactPasswordSelection;
//            EventBus.getDefault().postSticky(new EventOnboardingChanges(EventOnboardingChanges.ChangeType.REGISTER_CHANGE_TITLE, getResources().getString(R.string.tact_password_title)));
//            taskTactCheckUserResponse(TactSharedPrefController.getTempCredentialUsername());
//        }
//
//        return rootView;
//    }
//
//    private void setViewItems(View view){
//        sourceUser     = (TextView) view.findViewById(R.id.sourceUser);
//        userPart       = (LinearLayout) view.findViewById(R.id.userPart);
//        passwordPart   = (LinearLayout) view.findViewById(R.id.passwordPart);
//        textUser       = (EditText) view.findViewById(R.id.textUser);
//        loginError     = (TextView) view.findViewById(R.id.loginError);
//        continueButton = (Button) view.findViewById(R.id.buttonContinue);
//
//        textPassword           = (EditText) view.findViewById(R.id.textPassword);
//        textConfirmPassword    = (EditText) view.findViewById(R.id.textConfirmPassword);
//        setPasswordButton      = (Button) view.findViewById(R.id.buttonSetPassword);
//        setPasswordButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                validatePassword(view);
//            }
//        });
//
//        continueButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                validateUserName(view);
//            }
//        });
//
//        textConfirmPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
//                    if (!validating && TactNetworking.checkInternetConnection(true)){
//                        validatePassword(v);
//                    }
//                }
//                return false;
//            }
//        });
//
//        textUser.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
//                    if (!validating && TactNetworking.checkInternetConnection(true)){
//                        validateUserName(v);
//                    }
//                }
//                return false;
//            }
//        });
//    }
//
//    private void validatePassword(View view){
//        validating = true;
//        if (TactNetworking.checkInternetConnection(true)){
//            if (passwordControl()) {
//                validTactUserTask(textUser.getText().toString(), textPassword.getText().toString());
//            } else {
//                if (textPassword.getText().toString().length() < 6)
//                {
//                    loginError.setText(getResources().getString(R.string.password_length_error));
//                }
//                else if (textPassword.getText().toString().contains(" "))
//                {
//                    loginError.setText(getResources().getString(R.string.password_blank_error));
//                } else
//                {
//                    loginError.setText(getResources().getString(R.string.not_match_passwords));
//                }
//                loginError.setVisibility(View.VISIBLE);
//                validating = false;
//            }
//        }
//        else {
//            validating = false;
//        }
//    }
//
//    private void validateUserName(View view){
//        validating = true;
//        if (TactNetworking.checkInternetConnection(true)){
//            String username = textUser.getText().toString();
//
//            if (Utils.isEmailValid(username)) {
//                //If the value entered has email format
//                validTactUserTask(username, null);
//            }
//            else {
//                if(username.length() < 1)
//                    //If the user enter an empty value
//                    loginError.setText(getResources().getString(R.string.email_error));
//                else
//                    //If the value entered has not email format
//                    loginError.setText(getResources().getString(R.string.email_format_error));
//                loginError.setVisibility(View.VISIBLE);
//                validating = false;
//            }
//        }
//        else {
//            validating = false;
//        }
//    }
//
//    /**
//     * Check if the user has some email source synced in the previous steps.
//     * If it happens, the method returns the username.
//     * @return
//     */
//    public void checkSourceAccounts() {
//        if (TactNetworking.checkInternetConnection(true)){
//            operationType = TactOperationType.GetTactUserNames;
//            tactServerConnectionTask();
//        }
//    }
//
//    /**
//     * TaskServerConnectionTask response for get sources user
//     * @param user
//     */
//    public void taskTactCheckUserResponse(String user) {
//        if (user!=null) {
//            sourceUser.setVisibility(View.VISIBLE);
//            sourceUser.setHint(user);
//
//            userPart.setVisibility(View.GONE);
//            passwordPart.setVisibility(View.VISIBLE);
//
//            textUser.setText(user);
//            status = connectionStatus.TactPasswordSelection;
//            TactSharedPrefController.setOnboardingUserSelection(false);
//        } else {
//            userViewStep();
//        }
//        EventBus.getDefault().postSticky(new EventHandleProgress(false, false));
//    }
//
//    /**
//     * TaskServerConnection server response for the registration.
//     * @param isValid
//     */
//    public void taskTacktRegistrationResponse(String isValid) {
//        if (isValid.equals("ok")) {
//
//            String password;
//            if(textPassword!=null && !textPassword.getText().toString().isEmpty()) {
//                password = textPassword.getText().toString();
//            } else {
//                password = null;
//            }
//
//            if (password == null) {
//                TactSharedPrefController.storeTempCredentials(textUser.getText().toString(), TactSharedPrefController.getTempCredentialPassword());
//            }
//            else{
//                TactSharedPrefController.storeTempCredentials(textUser.getText().toString(), password);
//            }
//
//            if (status != connectionStatus.TactPasswordSelection || password == null) {
//                passwordViewStep();
//            } else {
//                TactSharedPrefController.storeCredentials(TactSharedPrefController.getTempCredentialUsername(), TactSharedPrefController.getTempCredentialPassword());
//                EventBus.getDefault().postSticky(new EventMoveNextFragmentOnboarding(TactConst.FRAGMENT_ONBOARDING_TACT_REGISTRATION));
//            }
//
//        } else {
//            status = connectionStatus.TactConnectionError;
//            loginError.setText(isValid);
//            loginError.setVisibility(View.VISIBLE);
//        }
//    }
//
//    /**
//     * Check password matching.
//     * @return true/false
//     */
//    private boolean passwordControl() {
//        String password = textPassword.getText().toString();
//        String repeatPassword = textConfirmPassword.getText().toString();
//
//        return password.length()>5 && password.equals(repeatPassword) && !password.contains(" ");
//    }
//
//    /**
//     * Creates new async task to try the registration.
//     * @param user
//     * @param password
//     *
//     * If the password is null, only checks if the username is available.
//     */
//    private void validTactUserTask(String user, String password) {
//        if (TactNetworking.checkInternetConnection(true)){
//            operationType = TactOperationType.TactRegistration;
//            tactServerConnectionTask(user, password);
//        }
//        else {
//            validating = false;
//        }
//    }
//
//    /**
//     * Shows the user part (username only) and the tile
//     */
//    private void userViewStep() {
//        textUser.requestFocus();
//        textUser.postDelayed(
//            new Runnable() {
//                public void run() {
//                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                    inputMethodManager.showSoftInput(textUser, 0);
//                }
//            }, 200);
//        userPart.setVisibility(View.VISIBLE);
//        passwordPart.setVisibility(View.GONE);
//        sourceUser.setVisibility(View.GONE);
//        EventBus.getDefault().postSticky(new EventOnboardingChanges(EventOnboardingChanges.ChangeType.REGISTER_CHANGE_TITLE, getResources().getString(R.string.tact_username_title)));
//    }
//
//    /**
//     * Set the layout to insert the passwords.
//     */
//    private void passwordViewStep() {
//        textPassword.requestFocus();
//        textPassword.postDelayed(
//            new Runnable() {
//                public void run() {
//                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                    inputMethodManager.showSoftInput(textPassword, 0);
//                }
//            }, 200);
//        status = connectionStatus.TactPasswordSelection;
//        TactSharedPrefController.setOnboardingUserSelection(false);
//        passwordPart.setVisibility(View.VISIBLE);
//        userPart.setVisibility(View.GONE);
//        loginError.setVisibility(View.GONE);
//        EventBus.getDefault().postSticky(new EventOnboardingChanges(EventOnboardingChanges.ChangeType.REGISTER_CHANGE_TITLE, getResources().getString(R.string.tact_password_title)));
//    }
//
//    public void tactServerConnectionTask(String... args) {
//        switch(operationType) {
//            case GetTactUserNames:
//                TactNetworking.callGetAvailableUsernames(getActivity());
//                break;
//            case TactRegistration:
//                if (args.length>1) {
//                    TactNetworking.callUpdateRegistration(getActivity(), args[0], args[1]);
//                }
//        }
//    }
//
//    private void sendCallBack(Object param, Boolean success) {
//        switch (operationType) {
//            case GetTactUserNames:
//                String response = null;
//                try {
//                    if(((JSONArray)param).length()>0) {
//                        response = ((JSONArray)param).getString(0);
//                    }} catch (Exception e) {}
//                taskTactCheckUserResponse(response);
//                break;
//            case TactRegistration:
//                if (success) {
//                    taskTacktRegistrationResponse("ok");
//                }
//                else{
//                    taskTacktRegistrationResponse(getResources().getString(R.string.registration_repeated_email));
//                }
//                break;
//        }
//        EventBus.getDefault().postSticky(new EventHandleProgress(false, false));
//    }
//
//    public void onEvent(EventTactRegistrationSuccess eventTactRegistrationSuccess){
//        validating = false;
//        EventBus.getDefault().removeStickyEvent(eventTactRegistrationSuccess);
//        sendCallBack(eventTactRegistrationSuccess.response, true);
//    }
//
//    public void onEvent(EventTactRegistrationError eventTactRegistrationError){
//        validating = false;
//        EventBus.getDefault().removeStickyEvent(eventTactRegistrationError);
//        status = FragmentOnboardingTactRegistration.connectionStatus.TactConnectionError;
//        sendCallBack(null, false);
//    }
//
//    @Override
//    public void onDestroy(){
//        super.onDestroy();
//        TactApplication.unbindDrawables(rootView);
//        rootView = null;
//        status = null;
//        sourceUser = null;
//        userPart = null;
//        passwordPart = null;
//        textUser = null;
//        loginError = null;
//        textPassword = null;
//        textConfirmPassword = null;
//        setPasswordButton = null;
//        operationType = null;
//        continueButton = null;
//        validating = null;
//        System.gc();
//    }
}
