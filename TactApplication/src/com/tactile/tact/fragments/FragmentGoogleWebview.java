package com.tactile.tact.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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

import de.greenrobot.event.EventBus;

/**
 * Created by sebafonseca on 10/29/14.
 */
@SuppressLint("SetJavaScriptEnabled")
public class FragmentGoogleWebview extends FragmentTactBase {

    private WebView webView;
    private String authorizationUrl;
    private String redirectUrl;
    private Boolean handled;
    private Boolean lastLoading = false;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        authorizationUrl    = TactNetworking.getOauthURL() + TactConst.END_POINT_AUTH_GOOGLE;
        redirectUrl         = TactNetworking.getOauthURL() + TactConst.END_POINT_AUTH_GOOGLE_REDIRECT;

        CookieManager.getInstance().removeAllCookie();
        CookieManager.getInstance().setAcceptCookie(true);

        rootView = inflater.inflate(R.layout.generic_webview, container, false);

        webView = (WebView) rootView.findViewById(R.id.webview);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setVisibility(View.VISIBLE);
        handled = false;
        webView.setWebViewClient(new GoogleWebViewClient());
        webView.setWebChromeClient(new GoogleWebChromeClient());
        EventBus.getDefault().postSticky(new EventHandleProgress(true, false));
        webView.loadUrl(authorizationUrl, TactNetworking.getMapAuthorizationHeaders(getActivity()));
        return rootView;
    }

    public void onEvent(EventAddSourceSuccess eventAddSourceSuccess){
        EventBus.getDefault().removeStickyEvent(eventAddSourceSuccess);
        Utils.addOnboardingSourceId(eventAddSourceSuccess);
        EventBus.getDefault().postSticky(new EventHandleProgress(false, false));
        EventBus.getDefault().post(new EventOnboardingChanges(EventOnboardingChanges.ChangeType.LOGGED_IN_GOOGLE, true));
        EventBus.getDefault().post(new EventMoveNextFragmentOnboarding(TactConst.FRAGMENT_ONBOARDING_GOOGLE));
    }

    public void onEvent(EventAddSourceError eventAddSourceError){
        EventBus.getDefault().removeStickyEvent(eventAddSourceError);
        EventBus.getDefault().postSticky(new EventHandleProgress(false, false));
        EventBus.getDefault().post(new EventOnboardingChanges(EventOnboardingChanges.ChangeType.LOGGED_IN_GOOGLE, false));
        EventBus.getDefault().post(new EventMoveNextFragmentOnboarding(TactConst.FRAGMENT_ONBOARDING_GOOGLE));
    }

    protected class GoogleWebViewClient extends WebViewClient{
        @Override
        public void onPageStarted(WebView view, String url,Bitmap bitmap){
            if (url.startsWith(redirectUrl)){
                lastLoading = true;
            }
            else if (url.contains("RecoverAccount") ||
                    url.contains("SignUp") ||
                    url.contains("/about") ||
                    url.contains("TOS?") ||
                    url.contains("support/accounts")){
                view.stopLoading();
            }
        }
        @Override
        public void onPageFinished(final WebView view, final String url)  {
            if (url.startsWith(redirectUrl)){
                //Disable touch on WebView
                webView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
                if(!handled) {
                    new ProcessToken(url).execute();
                }
            }
        }

        @Override
        public void onReceivedError(final WebView webView, int errorCode, String description, final String failingUrl)
        {
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
                            EventBus.getDefault().postSticky(new EventHandleProgress(false, false));
                            EventBus.getDefault().post(new EventMoveNextFragmentOnboarding(TactConst.FRAGMENT_ONBOARDING_EMAIL));
                            dialog.dismiss();
                        }
                    });


            super.onReceivedError(webView, errorCode, description, failingUrl);
        }
    }

    protected class GoogleWebChromeClient extends WebChromeClient{

        PageFinishHandler pageFinishHandler = new PageFinishHandler();

        @Override
        public void onProgressChanged(WebView view, int progress) {
            try{
                EventBus.getDefault().postSticky(new EventHandleProgress(true, false));
                pageFinishHandler.removeMessages(PageFinishHandler.MESSAGE_PAGE_FINISHED);
            }catch(Exception e){
                e.printStackTrace();
            }

            EventBus.getDefault().post(new EventOnboardingChanges(EventOnboardingChanges.ChangeType.GOOGLE_PROGRESS, (progress * 1000)));

            if (progress == 100 && !lastLoading) {
                Message msg = Message.obtain(pageFinishHandler, PageFinishHandler.MESSAGE_PAGE_FINISHED, "");
                pageFinishHandler.sendMessageDelayed(msg, PageFinishHandler.DELAY_PAGE_FINISHED);
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

    private class ProcessToken extends AsyncTask<Uri, Void, Void> {

        String url;
        boolean startActivity = false;

        public ProcessToken(String url) {
            this.url = url;
        }

        @Override
        protected Void doInBackground(Uri...params) {

            if(url.startsWith(redirectUrl)) {
                Utils.Log("Redirect URL found" + url);
                handled = true;
                try {
                    if(url.indexOf("code=")!=-1) {
                        startActivity   = true;
                    }
                    else if(url.indexOf("error=")!=-1) {
                        startActivity = false;
                        EventBus.getDefault().post(new EventMoveNextFragmentOnboarding(TactConst.FRAGMENT_ONBOARDING_GOOGLE_WEBVIEW));
                    }
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                Utils.Log("Not doing anything for url " + url);
            }
            return null;
        }


        @Override
        protected void onPreExecute(){}

        /**
         * When we're done and we've retrieved either a valid token or an error from the server,
         * we'll return to our original activity
         */
        @Override
        protected void onPostExecute(Void result) {
            if (startActivity) {
                TactNetworking.callAddSource(getActivity(), "google", url);
            }

            else {
                //TODO: handle this errror with events!
            }

        }

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        TactApplication.unbindDrawables(rootView);
        rootView = null;
        webView = null;
        authorizationUrl = null;
        redirectUrl = null;
        handled = null;
        lastLoading = null;
        System.gc();
    }
}
