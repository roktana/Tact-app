package com.tactile.tact.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tactile.tact.R;
import com.tactile.tact.activities.TactApplication;
import com.tactile.tact.consts.TactConst;
import com.tactile.tact.services.events.EventMoveNextFragmentOnboarding;
import com.tactile.tact.services.network.TactNetworking;

import de.greenrobot.event.EventBus;

/**
 * Created by sebafonseca on 10/29/14.
 */
public class FragmentOnboardingGoogle extends FragmentTactBase {

    private Boolean loggedInSalesforce  = false;
    private Boolean loggedInExchange    = false;
    private Boolean loggedInGoogle      = false;
    private Boolean syncExchange        = false;
    private View rootView;
    private TextView gmail_legend;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            loggedInSalesforce  = bundle.getBoolean("loggedInSalesforce");
            loggedInExchange    = bundle.getBoolean("loggedInExchange");
            loggedInGoogle      = bundle.getBoolean("loggedInGoogle");
            syncExchange        = bundle.getBoolean("syncExchange");
        }

        rootView = inflater.inflate(R.layout.on_boarding_gmail_connect, container, false);

        setViewItems(rootView);

        return rootView;
    }

    private void setViewItems(final View view){
        LinearLayout already_connected_buttons  = (LinearLayout) view.findViewById(R.id.already_connected_buttons);
        LinearLayout buttons                    = (LinearLayout) view.findViewById(R.id.buttons);
        Button skip                           = (Button)    view.findViewById(R.id.general_connect_skip_btn);

        skip.setVisibility(View.VISIBLE);
        skip.setAlpha(0.5F);

        gmail_legend                            = (TextView)     view.findViewById(R.id.gmail_legend);
        if(loggedInGoogle){
            Button continue_btn               = (Button)     already_connected_buttons.findViewById(R.id.general_continue_btn);
            already_connected_buttons.setVisibility(View.VISIBLE);
            gmail_legend.setVisibility(View.GONE);
            buttons.setVisibility(View.GONE);
            continue_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (syncExchange)
                        EventBus.getDefault().post(new EventMoveNextFragmentOnboarding(TactConst.FRAGMENT_ONBOARDING_EXCHANGE)); //move to Exchange view
                    else
                        EventBus.getDefault().post(new EventMoveNextFragmentOnboarding(TactConst.FRAGMENT_ONBOARDING_LOCAL_SOURCES)); //move to Local Sources
                }
            });
        }
        else {
            Button connect          = (Button)      view.findViewById(R.id.general_connect_btn);
            already_connected_buttons.setVisibility(View.GONE);
            buttons.setVisibility(View.VISIBLE);

            if(syncExchange || loggedInSalesforce || loggedInExchange) {
//                remind_me_later.setVisibility(View.VISIBLE);
                skip.setVisibility(View.VISIBLE);
                skip.setEnabled(true);
                skip.setAlpha(1);

//                remind_me_later.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        EventBus.getDefault().post(new EventMoveNextFragmentOnboarding(TactConst.FRAGMENT_ONBOARDING_EXCHANGE)); //move to Exchange view
//                    }
//                });

                skip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (syncExchange) {
                            EventBus.getDefault().post(new EventMoveNextFragmentOnboarding(TactConst.FRAGMENT_ONBOARDING_EXCHANGE)); //move to Exchange view
                        }
                        else {
                            EventBus.getDefault().post(new EventMoveNextFragmentOnboarding(TactConst.FRAGMENT_ONBOARDING_LOCAL_SOURCES)); //move to Local Sources View
                        }
                    }
                });

            } else {
//                remind_me_later.setVisibility(View.GONE);
                skip.setVisibility(View.VISIBLE);
                skip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(view.getContext(), getResources().getString(R.string.select_one_email_resource), Toast.LENGTH_SHORT).show();
                    }
                });
                skip.setAlpha(0.5F);
            }

            connect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (TactNetworking.checkInternetConnection(true)){
                        EventBus.getDefault().postSticky(new EventMoveNextFragmentOnboarding(TactConst.FRAGMENT_ONBOARDING_GOOGLE_WEBVIEW));
                    }
                }
            });
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        TactApplication.unbindDrawables(rootView);
        rootView = null;
        loggedInSalesforce = null;
        loggedInExchange = null;
        loggedInGoogle = null;
        syncExchange = null;
        System.gc();
    }
}