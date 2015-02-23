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
public class FragmentOnboardingTactPasswordRegistration extends FragmentTactBase {

    private LinearLayout passwordPart;
    private TextView loginError;
    private EditText textPassword;
    private EditText textConfirmPassword;
    private Button setPasswordButton;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.on_boarding_tact_password_registration, container, false);

        EventBus.getDefault().postSticky(new EventOnboardingChanges(EventOnboardingChanges.ChangeType.REGISTER_CHANGE_TITLE, getResources().getString(R.string.tact_password_title)));

        setViewItems(rootView);

        return rootView;
    }

    private void setViewItems(View view){
        loginError     = (TextView) view.findViewById(R.id.loginError);
        passwordPart   = (LinearLayout) rootView.findViewById(R.id.passwordPart);
        passwordPart.setVisibility(View.VISIBLE);
        textPassword           = (EditText) view.findViewById(R.id.textPassword);
        textConfirmPassword    = (EditText) view.findViewById(R.id.textConfirmPassword);
        setPasswordButton      = (Button) view.findViewById(R.id.buttonSetPassword);
        setPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validatePassword();
            }
        });

        textConfirmPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    if (TactNetworking.checkInternetConnection(true)){
                        validatePassword();
                    }
                }
                return false;
            }
        });
    }

    private void validatePassword(){
        if (TactNetworking.checkInternetConnection(true)){
            if (passwordControl()) {
                TactNetworking.callUpdateRegistration(getActivity(), TactSharedPrefController.getTempCredentialUsername(), textPassword.getText().toString());
            } else {
                if (textPassword.getText().toString().length() < 6)
                {
                    loginError.setText(getResources().getString(R.string.password_length_error));
                }
                else if (textPassword.getText().toString().contains(" "))
                {
                    loginError.setText(getResources().getString(R.string.password_blank_error));
                } else
                {
                    loginError.setText(getResources().getString(R.string.not_match_passwords));
                }
                loginError.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Check password matching.
     * @return true/false
     */
    private boolean passwordControl() {
        String password = textPassword.getText().toString();
        String repeatPassword = textConfirmPassword.getText().toString();

        return password.length()>5 && password.equals(repeatPassword) && !password.contains(" ");
    }


    public void onEvent(EventTactRegistrationSuccess eventTactRegistrationSuccess){
        EventBus.getDefault().removeStickyEvent(eventTactRegistrationSuccess);

        TactSharedPrefController.storeTempCredentials(TactSharedPrefController.getTempCredentialUsername(), textPassword.getText().toString());

        TactSharedPrefController.storeCredentials(TactSharedPrefController.getTempCredentialUsername(), TactSharedPrefController.getTempCredentialPassword());

        EventBus.getDefault().postSticky(new EventMoveNextFragmentOnboarding(TactConst.FRAGMENT_ONBOARDING_TACT_REGISTRATION));

        EventBus.getDefault().postSticky(new EventHandleProgress(false, false));
    }

    public void onEvent(EventTactRegistrationError eventTactRegistrationError){
        EventBus.getDefault().removeStickyEvent(eventTactRegistrationError);
        loginError.setText("Something bad happened... Please try again");
        EventBus.getDefault().postSticky(new EventHandleProgress(false, false));
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        TactApplication.unbindDrawables(rootView);
        rootView = null;
        passwordPart = null;
        loginError = null;
        textPassword = null;
        textConfirmPassword = null;
        setPasswordButton = null;
        System.gc();
    }
}
