package com.tactile.tact.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tactile.tact.R;
import com.tactile.tact.activities.TactApplication;
import com.tactile.tact.consts.TactConst;
import com.tactile.tact.services.events.EventMoveNextFragmentOnboarding;
import com.tactile.tact.services.events.EventOnboardingChanges;

import de.greenrobot.event.EventBus;

/**
 * Created by sebafonseca on 10/28/14.
 */
public class FragmentOnboardingEmailSources extends FragmentTactBase {

    private Button skip;
    private Button connect_btn;
    private TextView gmail_text_view;
    private TextView exchange_text_view;
    private CheckBox gmail_switch;
    private CheckBox exchange_switch;
    private View rootView;

    public Boolean syncGoogle = true;
    public Boolean syncExchange = true;

    private Boolean loggedInSalesforce  = false;
    private Boolean loggedInExchange    = false;
    private Boolean loggedInGoogle      = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            loggedInSalesforce  = bundle.getBoolean("loggedInSalesforce");
            loggedInExchange    = bundle.getBoolean("loggedInExchange");
            loggedInGoogle      = bundle.getBoolean("loggedInGoogle");
            syncGoogle          = bundle.getBoolean("syncGoogle");
            syncExchange        = bundle.getBoolean("syncExchange");
        }

        rootView = inflater.inflate(R.layout.on_boarding_email_sources, container, false);

        setViewItems(rootView);

        return rootView;
    }

    private void setViewItems(final View view){
        skip                   = (Button)    view.findViewById(R.id.general_continue_skip_btn);
        connect_btn            = (Button)    view.findViewById(R.id.general_continue_btn);
        gmail_switch           = (CheckBox)    view.findViewById(R.id.gmail_switch);
        exchange_switch        = (CheckBox)    view.findViewById(R.id.exchange_switch);
        exchange_text_view     = (TextView)    view.findViewById(R.id.exchange_text_view);
        gmail_text_view        = (TextView)    view.findViewById(R.id.gmail_text_view);
        LinearLayout gmail     = (LinearLayout) view.findViewById(R.id.gmail_container);
        LinearLayout exchange  = (LinearLayout) view.findViewById(R.id.exchange_container);

        if (syncExchange && syncExchange) {
            gmail_text_view.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
            exchange_text_view.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        }

        //Set skip and remind me later visible only if the user is connected with Salesforce, Google or Exchange
        if (loggedInSalesforce || loggedInGoogle || loggedInExchange){
            skip.setVisibility(View.VISIBLE);
//            remind_me_later.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    EventBus.getDefault().post(new EventMoveNextFragmentOnboarding(TactConst.FRAGMENT_ONBOARDING_LOCAL_SOURCES));
//                }
//            });
            skip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().post(new EventMoveNextFragmentOnboarding(TactConst.FRAGMENT_ONBOARDING_LOCAL_SOURCES));
                }
            });
        }
        else {
            skip.setVisibility(View.GONE);
        }

        //Set Listener to checkboxes when it changes
        gmail_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                syncGoogle = isChecked;
                if (isChecked) {
                    gmail_text_view.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
                } else {
                    gmail_text_view.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
                }
                EventBus.getDefault().post(new EventOnboardingChanges(EventOnboardingChanges.ChangeType.SYNC_GOOGLE, syncGoogle));
                connect_btn.setAlpha(syncExchange || syncGoogle?1:0.5f);
            }
        });
        gmail_switch.setChecked(syncGoogle);

        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gmail_switch.setChecked(!gmail_switch.isChecked());
            }
        });

        exchange_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                syncExchange = isChecked;
                if (isChecked) {
                    exchange_text_view.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
                } else {
                    exchange_text_view.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
                }

                EventBus.getDefault().post(new EventOnboardingChanges(EventOnboardingChanges.ChangeType.SYNC_EXCHANGE, syncExchange));
                connect_btn.setAlpha(syncExchange || syncGoogle?1:0.5f);
            }
        });
        exchange_switch.setChecked(syncExchange);

        exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exchange_switch.setChecked(!exchange_switch.isChecked());
            }
        });

        connect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (syncExchange || syncGoogle) {
                    EventBus.getDefault().post(new EventMoveNextFragmentOnboarding(TactConst.FRAGMENT_ONBOARDING_CONNECT_EMAIL));
                } else {
                    Toast.makeText(view.getContext(), getResources().getString(R.string.select_one_email_resource), Toast.LENGTH_SHORT).show();
                }
            }
        });

        connect_btn.setAlpha(syncExchange || syncGoogle?1:0.5f);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        TactApplication.unbindDrawables(rootView);
        rootView = null;
//        remind_me_later = null;
        skip = null;
        connect_btn = null;
        gmail_switch = null;
        exchange_switch = null;
        syncGoogle = null;
        syncExchange = null;
        loggedInSalesforce = null;
        loggedInExchange = null;
        loggedInGoogle = null;
        System.gc();
    }
}
