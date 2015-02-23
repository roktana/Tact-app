package com.tactile.tact.fragments;

import android.content.Context;
import android.graphics.Paint;
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
import android.widget.Toast;

import com.tactile.tact.R;
import com.tactile.tact.activities.TactApplication;
import com.tactile.tact.consts.TactConst;
import com.tactile.tact.services.events.EventAddSourceError;
import com.tactile.tact.services.events.EventAddSourceSuccess;
import com.tactile.tact.services.events.EventHandleProgress;
import com.tactile.tact.services.events.EventMoveNextFragmentOnboarding;
import com.tactile.tact.services.events.EventOnboardingChanges;
import com.tactile.tact.services.network.TactNetworking;
import com.tactile.tact.utils.ExchangeServiceConnection;
import com.tactile.tact.utils.Utils;

import de.greenrobot.event.EventBus;

/**
 * Created by sebafonseca on 10/29/14.
 */
public class FragmentOnboardingExchange extends FragmentTactBase {

    private Boolean loggedInGoogle;
    private Boolean loggedInSalesforce;
    private Boolean loggedInExchange;
//    private Boolean exchangeManuallyEnabled = false;
    private TextView error_text;
    private View rootView;

    //Exchange Vars
    private static String email;
    private static String username;
    private static String password;

    private EditText exchange_email;
    private EditText exchange_password;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            loggedInGoogle          = bundle.getBoolean("loggedInGoogle");
            loggedInSalesforce      = bundle.getBoolean("loggedInSalesforce");
            loggedInExchange        = bundle.getBoolean("loggedInExchange");
//            exchangeManuallyEnabled = bundle.getBoolean("exchangeManuallyEnabled");
            username                = bundle.getString("emailExchangeManually");
        }

        rootView = inflater.inflate(R.layout.on_boarding_exchange_connect, container, false);

        setViewItems(rootView);

        error_text = (TextView) rootView.findViewById(R.id.error_text);
        error_text.setVisibility(View.GONE);

        return rootView;
    }

    private void setViewItems(final View view){
        LinearLayout already_connected_buttons  = (LinearLayout) view.findViewById(R.id.already_connected_buttons);
        LinearLayout buttons                    = (LinearLayout) view.findViewById(R.id.buttons);
        exchange_email                          = (EditText)     view.findViewById(R.id.exchange_email);
        exchange_password                       = (EditText)     view.findViewById(R.id.exchange_password);
//        TextView connect_manually                 = (TextView)       view.findViewById(R.id.connect_manually);

        if(loggedInExchange){
            LinearLayout enter_data_block           = (LinearLayout) view.findViewById(R.id.enter_data_block);
            LinearLayout success_connection_block   = (LinearLayout) view.findViewById(R.id.success_connection_block);
//            TextView success_username               = (TextView)     view.findViewById(R.id.success_username);
            Button continue_btn                   = (Button)     view.findViewById(R.id.general_continue_btn);
            already_connected_buttons.setVisibility(View.VISIBLE);
            buttons.setVisibility(View.GONE);

            enter_data_block.setVisibility(View.GONE);
            success_connection_block.setVisibility(View.VISIBLE);
//            success_username.setText(username);

            continue_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().post(new EventMoveNextFragmentOnboarding(TactConst.FRAGMENT_ONBOARDING_LOCAL_SOURCES));
                }
            });
        }

        else{
            exchange_email.requestFocus();
            final Context context = getActivity();
            exchange_email.postDelayed(
                new Runnable() {
                    public void run() {
                        if (context != null) {
                            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.showSoftInput(exchange_email, 0);
                        }
                    }
                }, 200);
            if(already_connected_buttons != null && buttons != null) {
                already_connected_buttons.setVisibility(View.GONE);
                buttons.setVisibility(View.VISIBLE);
//                Button remind_me_later        = (Button)   view.findViewById(R.id.remind_me_later);
                Button connect                = (Button)   view.findViewById(R.id.general_connect_btn);
                Button skip                 = (Button) view.findViewById(R.id.general_connect_skip_btn);
                if(loggedInGoogle || loggedInSalesforce) {
//                    remind_me_later.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            EventBus.getDefault().post(new EventMoveNextFragmentOnboarding(TactConst.FRAGMENT_ONBOARDING_LOCAL_SOURCES));
//                        }
//                    });

                    //convert to Link view
                    if (skip != null) {
                        skip.setPaintFlags(skip.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                        skip.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                EventBus.getDefault().post(new EventMoveNextFragmentOnboarding(TactConst.FRAGMENT_ONBOARDING_LOCAL_SOURCES));
                            }
                        });
                    }

                } else {
//                    remind_me_later.setVisibility(View.GONE);
//                    skip.setVisibility(View.GONE);
                    skip.setAlpha(0.5F);
                    skip.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(view.getContext(), getResources().getString(R.string.select_one_email_resource), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                connect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (TactNetworking.checkInternetConnection(true)){
                            executeExchangeSimpleConnection();
                        }
                    }
                });
            }
        }
//        if(exchangeManuallyEnabled){
//            if(connect_manually != null){
//                connect_manually.setVisibility(View.VISIBLE);
//                connect_manually.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        EventBus.getDefault().post(new EventOnboardingChanges(EventOnboardingChanges.ChangeType.SYNC_EXCHANGE_EMAIL, email));
//                        EventBus.getDefault().post(new EventMoveNextFragmentOnboarding(TactConst.FRAGMENT_ONBOARDING_EXCHANGE_MANUALLY, email, password));
//                    }
//                });
//            }
//        }

        exchange_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    if (TactNetworking.checkInternetConnection(true)){
                        executeExchangeSimpleConnection();
                    }
                }
                return false;
            }
        });
    }

    /**
     * Prepare data to be sent to Exchange by simple way
     * Username and Password
     */
    private void executeExchangeSimpleConnection(){
        email          = (exchange_email != null) ? exchange_email.getText().toString() : null;
        username       = email;
        password       = (exchange_password != null) ? exchange_password.getText().toString() : null;

        //server, domain, email, username, password, false
        if (email == null || password == null){
            Toast.makeText(getActivity(), "Email and Password are required.", Toast.LENGTH_LONG).show();
        }
        else {
            EventBus.getDefault().postSticky(new EventHandleProgress(true, false));
            //TODO: see why we could not do the request with volley, the response code is 201 and for volley that is an error!
            //TactNetworking.callAddSource(getActivity(), "exchange", "", "", email, username, password, false); // type, server, domain, email, username, password, skipssl
            new ExchangeServiceConnection(getActivity(), "", "", email, username, password, false).execute();
        }
    }

    public void onEvent(EventAddSourceSuccess eventAddSourceSuccess){
        EventBus.getDefault().removeStickyEvent(eventAddSourceSuccess);
        Utils.addOnboardingSourceId(eventAddSourceSuccess);
        EventBus.getDefault().postSticky(new EventHandleProgress(false, false));
        EventBus.getDefault().post(new EventOnboardingChanges(EventOnboardingChanges.ChangeType.SYNC_EXCHANGE_EMAIL, email));
        EventBus.getDefault().post(new EventOnboardingChanges(EventOnboardingChanges.ChangeType.LOGGED_IN_EXCHANGE, true));
        EventBus.getDefault().post(new EventMoveNextFragmentOnboarding(TactConst.FRAGMENT_ONBOARDING_EXCHANGE));
    }

    public void onEventMainThread(EventAddSourceError eventAddSourceError){
        EventBus.getDefault().removeStickyEvent(eventAddSourceError);
        EventBus.getDefault().postSticky(new EventHandleProgress(false, false));
        if (TactNetworking.checkInternetConnection(true)) {
            EventBus.getDefault().post(new EventOnboardingChanges(EventOnboardingChanges.ChangeType.LOGGED_IN_EXCHANGE, false));
//            exchangeManuallyEnabled = true;
//            EventBus.getDefault().post(new EventOnboardingChanges(EventOnboardingChanges.ChangeType.SYNC_EXCHANGE_MANUALLY, true));

            error_text.setVisibility(View.VISIBLE);
            if (eventAddSourceError.getError().getCodeInt() / 100 == 4){
                error_text.setText(getResources().getString(R.string.exchange_wrong_login));
            }
            else {
                error_text.setText(getResources().getString(R.string.exchange_unable_login));
            }

            setViewItems(getView());
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        TactApplication.unbindDrawables(rootView);
        rootView = null;
        loggedInGoogle = null;
        loggedInSalesforce = null;
        loggedInExchange = null;
//        exchangeManuallyEnabled = null;
        error_text = null;
        email = null;
        username = null;
        password = null;
        exchange_email = null;
        exchange_password = null;
        System.gc();
    }
}
