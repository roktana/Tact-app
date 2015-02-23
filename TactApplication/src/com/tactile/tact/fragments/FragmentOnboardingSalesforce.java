package com.tactile.tact.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tactile.tact.R;
import com.tactile.tact.activities.TactApplication;
import com.tactile.tact.consts.TactConst;
import com.tactile.tact.services.events.EventHandleProgress;
import com.tactile.tact.services.events.EventMoveNextFragmentOnboarding;
import com.tactile.tact.services.network.TactNetworking;

import de.greenrobot.event.EventBus;

/**
 * Created by sebafonseca on 10/27/14.
 */
public class FragmentOnboardingSalesforce extends FragmentTactBase {

    // Private variables
    private Button connectButton;
    private Button laterButton;
    private Button skipButton;
    private Button continueButton;
    private Boolean loggedInSalesforce = false;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            loggedInSalesforce = bundle.getBoolean("loggedInSalesforce");
        }

        EventBus.getDefault().postSticky(new EventHandleProgress(true, false));

        rootView = inflater.inflate(R.layout.on_boarding_salesforce, container, false);

        setViewItems(rootView);

        return rootView;
    }

    private void setViewItems(View view){
        if (loggedInSalesforce){
            setAlreadyConnected(view);
        }
        else {
            setButtonsEvents(view);
        }
        EventBus.getDefault().postSticky(new EventHandleProgress(false, false));
    }

    /**
     * Set the events for all the buttons
     * @param view the current view
     */
    private void setButtonsEvents(View view){
        connectButton  = (Button) view.findViewById(R.id.general_connect_btn);
//        laterButton    = (Button) view.findViewById(R.id.remind_me_later);
        skipButton     = (Button) view.findViewById(R.id.general_connect_skip_btn);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TactNetworking.checkInternetConnection(true)){
                    EventBus.getDefault().postSticky(new EventMoveNextFragmentOnboarding(TactConst.FRAGMENT_ONBOARDING_SALESFORCE_WEBVIEW));
                }
            }
        });

//        laterButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                EventBus.getDefault().post(new EventMoveNextFragmentOnboarding(TactConst.FRAGMENT_ONBOARDING_EMAIL, false));
//            }
//        });

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new EventMoveNextFragmentOnboarding(TactConst.FRAGMENT_ONBOARDING_EMAIL, false));
            }
        });
    }

    /**
     * Set visible the success part of the SalesForce layout.
     * It is called when the user is already logged.
     */
    private void setAlreadyConnected(View view) {
        view.findViewById(R.id.connected_layout).setVisibility(View.VISIBLE);
        view.findViewById(R.id.already_connected_buttons).setVisibility(View.VISIBLE);
        view.findViewById(R.id.buttons).setVisibility(View.GONE);
        view.findViewById(R.id.salesforce_description).setVisibility(View.GONE);

        continueButton = (Button) view.findViewById(R.id.general_continue_btn);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new EventMoveNextFragmentOnboarding(TactConst.FRAGMENT_ONBOARDING_EMAIL, true));
            }
        });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        TactApplication.unbindDrawables(rootView);
        rootView = null;
        connectButton = null;
//        laterButton = null;
        skipButton = null;
        continueButton = null;
        System.gc();
    }
}
