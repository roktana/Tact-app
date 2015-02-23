package com.tactile.tact.fragments;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
 * Created by sebafonseca on 11/3/14.
 */
public class FragmentOnboardingExchangeManually extends FragmentTactBase {

    public final static int EnterServer   = 0;
    public final static int EnterDomain   = 1;
    public final static int EnterUsername = 2;
    public final static int EnterPassword = 3;
    public final static int FullForm      = 4;

    private String email                = "";
    private String server               = "";
    private String domain               = "";
    private String username             = "";
    private String password             = "";
    private TextView error_txt;
    private Boolean loggedInGoogle      = false;
    private Boolean loggedInSalesforce  = false;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        int stage = EnterServer;
        if (bundle != null){
            stage               = bundle.getInt("stageExchangeManually");
            email               = bundle.getString("emailExchangeManually");
            server              = bundle.getString("serverExchangeManually");
            domain              = bundle.getString("domainExchangeManually");
            username            = bundle.getString("usernameExchangeManually");
            password            = bundle.getString("passwordExchangeManually");
            loggedInGoogle      = bundle.getBoolean("loggedInGoogle");
            loggedInSalesforce  = bundle.getBoolean("loggedInSalesforce");
        }

        rootView = inflater.inflate(R.layout.on_boarding_exchange_connect_manually_form, container, false);

        setViewItems(rootView, stage);

        return rootView;
    }

    private void setViewItems(final View view, final int stage){
        error_txt                               = (TextView) view.findViewById(R.id.error_text);
        ImageView exchange_manual_image         = (ImageView)view.findViewById(R.id.exchange_manual_image);
        final EditText exchange_manual_input    = (EditText) view.findViewById(R.id.exchange_manual_input);
        Button next                             = (Button)   view.findViewById(R.id.next_btn);
        final EditText exchange_email           = (EditText) view.findViewById(R.id.exchange_email);
        final EditText exchange_password        = (EditText) view.findViewById(R.id.exchange_password);
        final EditText exchange_server          = (EditText) view.findViewById(R.id.exchange_server);
        final EditText exchange_domain          = (EditText) view.findViewById(R.id.exchange_domain);
        final EditText exchange_username        = (EditText) view.findViewById(R.id.exchange_username);
        final Button connect                          = (Button)   view.findViewById(R.id.next);
        Button skip                             = (Button)   view.findViewById(R.id.skip);

        EventBus.getDefault().post(new EventOnboardingChanges(EventOnboardingChanges.ChangeType.SYNC_EXCHANGE_STAGE, stage));

        if (stage != FullForm) {
            exchange_manual_input.requestFocus();
            final Context context = getActivity();
            exchange_manual_input.postDelayed(
                new Runnable() {
                    public void run() {
                        if (context != null){
                            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);;
                            inputMethodManager.showSoftInput(exchange_manual_input, 0);
                        }
                    }
                }, 200);
        }

        switch (stage){
            case EnterServer:
                exchange_manual_image.setImageResource(R.drawable.on_boarding_exchange_manually_server);
                exchange_manual_input.setHint("Server");
                exchange_manual_input.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
                exchange_manual_input.setText(server);
                break;
            case EnterDomain:
                exchange_manual_image.setImageResource(R.drawable.on_boarding_exchange_manually_domain);
                exchange_manual_input.setHint("Domain (optional)");
                exchange_manual_input.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
                exchange_manual_input.setText(domain);
                break;
            case EnterUsername:
                exchange_manual_image.setImageResource(R.drawable.on_boarding_exchange_manually_username);
                exchange_manual_input.setTransformationMethod(null);
                exchange_manual_input.setHint("Username");
                exchange_manual_input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                exchange_manual_input.setText(username);
                break;
            case EnterPassword:
                exchange_manual_image.setImageResource(R.drawable.on_boarding_exchange_manually_password);
                exchange_manual_input.setHint("Password");
                exchange_manual_input.setTransformationMethod(PasswordTransformationMethod.getInstance());
                exchange_manual_input.setText(password);
                break;
            case FullForm:
                exchange_email.setText(email);
                exchange_server.setText(server);
                exchange_domain.setText(domain);
                exchange_username.setText(username);
                exchange_password.setText(password);
                break;
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewOnNextPressed(stage, exchange_manual_input, view);
            }
        });

        exchange_manual_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    if (TactNetworking.checkInternetConnection(true)){
                        setViewOnNextPressed(stage, exchange_manual_input, view);
                    }
                }
                return false;
            }
        });

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectManually(exchange_email, exchange_server, exchange_domain, exchange_username, exchange_password, view.getContext());
            }
        });

        exchange_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    connectManually(exchange_email, exchange_server, exchange_domain, exchange_username, exchange_password, v.getContext());
                }
                return false;
            }
        });

        if (stage != FullForm && (loggedInSalesforce || loggedInGoogle)){
            skip.setVisibility(View.VISIBLE);
        }
        else if (loggedInSalesforce || loggedInGoogle){
            skip = (Button) view.findViewById(R.id.skip_full);
            skip.setVisibility(View.VISIBLE);
        }
        //convert to Link view
        if (skip != null) {
            skip.setPaintFlags(skip.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            skip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().post(new EventOnboardingChanges(EventOnboardingChanges.ChangeType.SYNC_EXCHANGE_STAGE, EnterServer));
                    EventBus.getDefault().post(new EventMoveNextFragmentOnboarding(TactConst.FRAGMENT_ONBOARDING_LOCAL_SOURCES));
                }
            });
        }
    }

    private void connectManually(EditText exchange_email, EditText exchange_server, EditText exchange_domain, EditText exchange_username, EditText exchange_password, Context context){
        EventBus.getDefault().post(new EventOnboardingChanges(EventOnboardingChanges.ChangeType.SYNC_EXCHANGE_EMAIL, email));
        EventBus.getDefault().postSticky(new EventHandleProgress(true, false));
        email    = exchange_email.getText().toString();
        server   = exchange_server.getText().toString();
        domain   = exchange_domain.getText().toString();
        username = exchange_username.getText().toString();
        password = exchange_password.getText().toString();
        if (TactNetworking.checkInternetConnection(true)){
            //TODO: see why we could not do the request with volley, the response code is 201 and for volley that is an error!
            //TactNetworking.callAddSource(getActivity(), "exchange", server, domain, email, username, password, false); // type, server, domain, email, username, password, skipssl
            new ExchangeServiceConnection(getActivity(), server, domain, email, username, password, false).execute();
        }
    }

    private void setViewOnNextPressed(int stage, EditText exchange_manual_input, View view){
        switch (stage){
            case EnterServer:
                server = exchange_manual_input.getText().toString();
                EventBus.getDefault().post(new EventOnboardingChanges(EventOnboardingChanges.ChangeType.SYNC_EXCHANGE_SERVER, server));
                EventBus.getDefault().post(new EventOnboardingChanges(EventOnboardingChanges.ChangeType.SYNC_EXCHANGE_EMAIL, email));
                setViewItems(view, EnterDomain);
                break;
            case EnterDomain:
                domain = exchange_manual_input.getText().toString();
                EventBus.getDefault().post(new EventOnboardingChanges(EventOnboardingChanges.ChangeType.SYNC_EXCHANGE_DOMAIN, domain));
                setViewItems(view, EnterUsername);
                break;
            case EnterUsername:
                username = exchange_manual_input.getText().toString();
                EventBus.getDefault().post(new EventOnboardingChanges(EventOnboardingChanges.ChangeType.SYNC_EXCHANGE_USERNAME, username));
                setViewItems(view, EnterPassword);
                break;
            case EnterPassword:
                password = exchange_manual_input.getText().toString();
                EventBus.getDefault().post(new EventOnboardingChanges(EventOnboardingChanges.ChangeType.SYNC_EXCHANGE_PWD, password));
                LinearLayout wizard = (LinearLayout) view.findViewById(R.id.main_body_wizard);
                LinearLayout full_form = (LinearLayout) view.findViewById(R.id.main_body_full);
                wizard.setVisibility(View.GONE);
                full_form.setVisibility(View.VISIBLE);
                setViewItems(view, FullForm);
                break;
        }
    }

    public void onEvent(EventAddSourceSuccess eventAddSourceSuccess){
        EventBus.getDefault().removeStickyEvent(eventAddSourceSuccess);
        Utils.addOnboardingSourceId(eventAddSourceSuccess);
        EventBus.getDefault().postSticky(new EventHandleProgress(false, false));
        EventBus.getDefault().post(new EventOnboardingChanges(EventOnboardingChanges.ChangeType.LOGGED_IN_EXCHANGE, true));
        EventBus.getDefault().post(new EventMoveNextFragmentOnboarding(TactConst.FRAGMENT_ONBOARDING_EXCHANGE));
    }

    public void onEventMainThread(EventAddSourceError eventAddSourceError){
        EventBus.getDefault().removeStickyEvent(eventAddSourceError);
        EventBus.getDefault().postSticky(new EventHandleProgress(false, false));
        EventBus.getDefault().post(new EventOnboardingChanges(EventOnboardingChanges.ChangeType.LOGGED_IN_EXCHANGE, false));
        error_txt.setVisibility(View.VISIBLE);
        error_txt.setText(getResources().getString(R.string.exchange_unable_login));
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        TactApplication.unbindDrawables(rootView);
        rootView = null;
        email = null;
        server = null;
        domain = null;
        username = null;
        password = null;
        error_txt = null;
        loggedInGoogle = null;
        loggedInSalesforce = null;
        System.gc();
    }
}
