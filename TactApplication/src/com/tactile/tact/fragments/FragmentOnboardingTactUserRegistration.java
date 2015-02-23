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
import com.tactile.tact.services.events.EventAvailableUsernamesSuccess;
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
public class FragmentOnboardingTactUserRegistration extends FragmentTactBase {

    private EditText textUser;
    private Button continueButton;
    private View rootView;
    private TextView loginError;
    private Boolean continueFlow;
    private TextView sourceUser;
    private LinearLayout divider;
    private LinearLayout progressLayout;
    private Boolean waitngForResponse = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        continueFlow = false;
        EventBus.getDefault().postSticky(new EventOnboardingChanges(EventOnboardingChanges.ChangeType.REGISTER_CHANGE_TITLE, getResources().getString(R.string.tact_username_title)));

        rootView = inflater.inflate(R.layout.on_boarding_tact_user_registration, container, false);

        setViewItems(rootView);

        waitngForResponse = true;
        TactNetworking.callGetAvailableUsernames(getActivity());

        EventBus.getDefault().postSticky(new EventHandleProgress(false, false));

        return rootView;
    }

    private void setViewItems(View view){
        textUser       = (EditText) view.findViewById(R.id.textUser);
        continueButton = (Button) view.findViewById(R.id.buttonContinueReg);
        loginError     = (TextView) view.findViewById(R.id.loginError);
        sourceUser     = (TextView) view.findViewById(R.id.sourceUser);
        divider        = (LinearLayout) view.findViewById(R.id.divider_line_layout);
        progressLayout = (LinearLayout) view.findViewById(R.id.progress_bar_user_reg);
        showProgress();

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateUserName();
            }
        });

        textUser.setOnEditorActionListener(getEditorListener());
    }

    private void showProgress() {
        continueButton.setVisibility(View.GONE);
        continueButton.setClickable(false);
        progressLayout.setVisibility(View.VISIBLE);
        textUser.setOnEditorActionListener(getEditorListener());
    }

    public void hideProgress() {
        continueButton.setVisibility(View.VISIBLE);
        progressLayout.setVisibility(View.GONE);
        continueButton.setClickable(true);
        textUser.setOnEditorActionListener(null);
    }

    private void validateUserName(){
        showProgress();
        if (!waitngForResponse) {
            if (continueFlow) {
                waitngForResponse = true;
                TactNetworking.callUpdateRegistration(getActivity(), textUser.getText().toString(), null);
            } else {
                if (TactNetworking.checkInternetConnection(true)) {
                    String username = textUser.getText().toString();

                    if (Utils.isEmailValid(username)) {
                        //If the value entered has email format
                        waitngForResponse = true;
                        TactNetworking.callUpdateRegistration(getActivity(), username, null);
                    } else {
                        if (username.length() < 1)
                            //If the user enter an empty value
                            loginError.setText(getResources().getString(R.string.email_error));
                        else
                            //If the value entered has not email format
                            loginError.setText(getResources().getString(R.string.email_format_error));
                        loginError.setVisibility(View.VISIBLE);
                        hideProgress();
                    }
                }
            }
        }
    }

    /**
     * Shows the user part (username only) and the tile
     */
    private void userViewStep() {
        textUser.requestFocus();
        textUser.postDelayed(
                new Runnable() {
                    public void run() {
                        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(textUser, 0);
                    }
                }, 200);
        EventBus.getDefault().postSticky(new EventOnboardingChanges(EventOnboardingChanges.ChangeType.REGISTER_CHANGE_TITLE, getResources().getString(R.string.tact_username_title)));
    }

    public void onEventMainThread(EventAvailableUsernamesSuccess eventAvailableUsernamesSuccess){
        EventBus.getDefault().removeStickyEvent(eventAvailableUsernamesSuccess);

        String user = null;
        try {
            if(((JSONArray)eventAvailableUsernamesSuccess.response).length()>0) {
                user = ((JSONArray)eventAvailableUsernamesSuccess.response).getString(0);
            }
        } catch (Exception e) {}
        if (user!= null){
            continueFlow = true;
            sourceUser.setVisibility(View.VISIBLE);
            sourceUser.setHint(user);
            textUser.setVisibility(View.GONE);
            textUser.setText(user);
            divider.setVisibility(View.GONE);
        }
        else {
            userViewStep();
        }

        hideProgress();
        waitngForResponse = false;
    }

    public TextView.OnEditorActionListener getEditorListener() {
        return new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    if (!waitngForResponse && TactNetworking.checkInternetConnection(true)) {
                        validateUserName();
                    }
                }
                return false;
            }
        };
    }

    public void onEventMainThread(EventTactRegistrationSuccess eventTactRegistrationSuccess){
        EventBus.getDefault().removeStickyEvent(eventTactRegistrationSuccess);
        TactSharedPrefController.storeTempCredentials(textUser.getText().toString(), TactSharedPrefController.getTempCredentialPassword());
        TactSharedPrefController.setOnboardingUserSelection(false);
        EventBus.getDefault().postSticky(new EventMoveNextFragmentOnboarding(TactConst.FRAGMENT_ONBOARDING_TACT_PASSWORD_REGISTRATION));
    }

    public void onEventMainThread(EventTactRegistrationError eventTactRegistrationError){
        EventBus.getDefault().removeStickyEvent(eventTactRegistrationError);
        if (eventTactRegistrationError.getError().getCodeInt() == 400) {
            loginError.setText(getResources().getString(R.string.registration_repeated_email));
        }
        else {
            loginError.setText("Something bad happened...");
        }
        loginError.setVisibility(View.VISIBLE);

        textUser.setOnEditorActionListener(getEditorListener());
        hideProgress();
        waitngForResponse = false;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        TactApplication.unbindDrawables(rootView);
        rootView = null;
        textUser = null;
        loginError = null;
        continueButton = null;
        System.gc();
    }
}
