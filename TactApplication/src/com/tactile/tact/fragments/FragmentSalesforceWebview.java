package com.tactile.tact.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
import com.tactile.tact.utils.TactDialogHandler;
import com.tactile.tact.utils.Utils;
import com.tactile.tact.utils.oauth2.TokenEndpointResponse;
import com.tactile.tact.utils.oauth2.UriFragmentParser;

import java.util.Map;

import de.greenrobot.event.EventBus;

@SuppressLint("SetJavaScriptEnabled")
public class FragmentSalesforceWebview extends FragmentTactBase {

    private WebView webView;

    private String client_id            = null;
    private String redirect_uri         = null;
    private String URL                  = null;
    private Context context;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CookieManager.getInstance().removeAllCookie();

        context = inflater.getContext();

        EventBus.getDefault().postSticky(new EventHandleProgress(true, false));

        rootView = inflater.inflate(R.layout.generic_webview, container, false);


        client_id = getResources().getString(R.string.remoteAccessConsumerKey);
        redirect_uri = getResources().getString(R.string.oauthRedirectURI);
//        URL = "https://s-api.tactapp.com/oauth/salesforce";

        URL = "https://login.salesforce.com/services/oauth2/authorize?response_type=token&client_id=" + client_id +
                "&scope=refresh_token%20api%20id%20full&redirect_uri=" + redirect_uri;
        webView = (WebView) rootView.findViewById(R.id.webview);
        CookieManager.getInstance().setAcceptCookie(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setVisibility(View.VISIBLE);

        webView.setWebViewClient(new AuthWebViewClient());
        webView.loadUrl(URL);

        return rootView;
    }

    protected class AuthWebViewClient extends WebViewClient {
        PageFinishHandler pageFinishHandler = new PageFinishHandler();
        boolean finish = false;

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon){
            if (url.contains("forgotpassword")) {
                view.stopLoading();
            }
            else if (url.contains("logout.jsp")){ //In that case, if we try to stopLoading, we add more errors in following requests
                CookieManager.getInstance().removeAllCookie();
                view.loadUrl(URL);
            }
            else {
                super.onPageStarted(view, url, favicon);
                EventBus.getDefault().postSticky(new EventHandleProgress(true, false));
                pageFinishHandler.removeMessages(PageFinishHandler.MESSAGE_PAGE_FINISHED);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (url.startsWith(redirect_uri)){
                processLastResponse(view, url);
            }
            else if (!finish){
                Message msg = Message.obtain(pageFinishHandler, PageFinishHandler.MESSAGE_PAGE_FINISHED, "");
                pageFinishHandler.sendMessageDelayed(msg, PageFinishHandler.DELAY_PAGE_FINISHED);
            }
            else {
                EventBus.getDefault().postSticky(new EventHandleProgress(true, false));
            }
        }

        @Override
        public void onReceivedError(final WebView webView, int errorCode, String description, final String failingUrl)
        {
            if (!description.contains("net::ERR_UNKNOWN_URL_SCHEME")) {
                EventBus.getDefault().postSticky(new EventHandleProgress(true, false));

                final TactDialogHandler dialog = new TactDialogHandler(webView.getContext());

                dialog.showConfirmation(getResources().getString(R.string.timeout_message), getResources().getString(R.string.message_title_info),
                        getResources().getString(R.string.retry), getResources().getString(R.string.cancel),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //Retry pressed
                                webView.loadUrl(failingUrl);
                                EventBus.getDefault().postSticky(new EventHandleProgress(true, false));
                                dialog.dismiss();
                            }
                        },
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //Cancel pressed
                                EventBus.getDefault().postSticky(new EventHandleProgress(true, false));
                                EventBus.getDefault().post(new EventMoveNextFragmentOnboarding(TactConst.FRAGMENT_ONBOARDING_SALESFORCE));
                                dialog.dismiss();
                            }
                        });


                super.onReceivedError(webView, errorCode, description, failingUrl);
            }
        }

        private void processLastResponse(View view, String url){
            Uri callbackUri = Uri.parse(url);
            Map<String, String> params = UriFragmentParser.parse(callbackUri);
            String error = params.get("error");
            // Did we fail?
            if (error != null) {
                String errorDesc = params.get("error_description");
                Log.v(error, errorDesc);
                CookieManager.getInstance().removeAllCookie();
                webView.loadUrl(URL);
                EventBus.getDefault().post(new EventOnboardingChanges(EventOnboardingChanges.ChangeType.LOGGED_IN_SALESFORCE, false));
            }
            // Or succeed?
            else {
                TokenEndpointResponse tr = new TokenEndpointResponse(params);
                view.setVisibility(View.INVISIBLE);
                CookieManager.getInstance().removeAllCookie();
                TactNetworking.callAddSource(getActivity(), "salesforce", tr.idUrl, tr.authToken, tr.refreshToken, tr.instanceUrl);
                finish = true;
            }
        }

        public class PageFinishHandler extends Handler {
            public static final int MESSAGE_PAGE_FINISHED = 0;
            public static final int DELAY_PAGE_FINISHED = 1000;

            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_PAGE_FINISHED) {
                    EventBus.getDefault().postSticky(new EventHandleProgress(false, false));
                }
            }
        }
    }

    public void onEventMainThread(EventAddSourceSuccess eventAddSourceSuccess){
        //TODO: Only if we are in onboarding flow
        EventBus.getDefault().removeStickyEvent(eventAddSourceSuccess);
        Utils.addOnboardingSourceId(eventAddSourceSuccess);
        EventBus.getDefault().post(new EventOnboardingChanges(EventOnboardingChanges.ChangeType.LOGGED_IN_SALESFORCE, true));
        EventBus.getDefault().post(new EventMoveNextFragmentOnboarding(TactConst.FRAGMENT_ONBOARDING_SALESFORCE, true));
    }

    public void onEventMainThread(EventAddSourceError eventAddSourceError){
        EventBus.getDefault().removeStickyEvent(eventAddSourceError);
        if (eventAddSourceError.getError().getCodeInt() == 400)
        {
                Toast.makeText(context, getResources().getString(R.string.salesforce_account_error), Toast.LENGTH_SHORT).show();
        }
        else if (eventAddSourceError.getError().getCodeInt() / 100 == 5){
            Toast.makeText(context, getResources().getString(R.string.salesforce_server_error), Toast.LENGTH_SHORT).show();
        }
        EventBus.getDefault().post(new EventOnboardingChanges(EventOnboardingChanges.ChangeType.LOGGED_IN_SALESFORCE, false));
        EventBus.getDefault().post(new EventMoveNextFragmentOnboarding(TactConst.FRAGMENT_ONBOARDING_SALESFORCE, false));
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        TactApplication.unbindDrawables(rootView);
        rootView = null;
        webView = null;
        client_id = null;
        redirect_uri = null;
        URL = null;
        context = null;
        System.gc();
    }
}
