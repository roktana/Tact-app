package com.tactile.tact.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.tactile.tact.R;
import com.tactile.tact.activities.TactApplication;
import com.tactile.tact.consts.TactConst;
import com.tactile.tact.database.TactDataSource;
import com.tactile.tact.services.events.EventContactsUploadDetailsAmazonError;
import com.tactile.tact.services.events.EventContactsUploadDetailsError;
import com.tactile.tact.services.events.EventDeviceInformationError;
import com.tactile.tact.services.events.EventFinalizeLocalSources;
import com.tactile.tact.services.events.EventMoveNextFragmentOnboarding;
import com.tactile.tact.services.events.EventOnboardingChanges;
import com.tactile.tact.utils.Utils;

import org.json.JSONArray;

import de.greenrobot.event.EventBus;
import pl.droidsonroids.gif.GifTextView;

/**
 * Created by sebafonseca on 10/31/14.
 */
public class FragmentOnboardingContacts extends FragmentTactBase {

    private Boolean syncCalendars;
    private Button retry_contact_btn;
    private LinearLayout failed_block;
    private GifTextView progressBar;
    private Boolean syncContactsProcessing = false;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            syncCalendars           = bundle.getBoolean("syncCalendars");
            syncContactsProcessing  = bundle.getBoolean("syncContactsProcessing");
        }

        rootView = inflater.inflate(R.layout.on_boarding_contact_uploading_db, container, false);

        progressBar         = (GifTextView)     rootView.findViewById(R.id.progress_bar);
        failed_block        = (LinearLayout)    rootView.findViewById(R.id.failed_block);
        retry_contact_btn   = (Button)          rootView.findViewById(R.id.retry_btn);

        if (!syncContactsProcessing)
            new DownloadContacts(getActivity()).execute();

        return rootView;
    }

    /**
     * Displays the fail block message and hides the progress bar
     */
    public void displayFailMessage(){
        progressBar.setVisibility(View.INVISIBLE);
        failed_block.setVisibility(View.VISIBLE);
    }

    public void onEventMainThread(EventDeviceInformationError eventDeviceInformationError){
        EventBus.getDefault().removeStickyEvent(eventDeviceInformationError);
        displayFailMessage();
    }

    public void onEventMainThread(EventContactsUploadDetailsError eventContactsUploadDetailsError){
        EventBus.getDefault().removeStickyEvent(eventContactsUploadDetailsError);
        displayFailMessage();
    }

    public void onEventMainThread(EventContactsUploadDetailsAmazonError eventContactsUploadDetailsAmazonError){
        EventBus.getDefault().removeStickyEvent(eventContactsUploadDetailsAmazonError);
        displayFailMessage();
    }

    /**
     * Download contacts asynchronously
     */
    private class DownloadContacts extends AsyncTask<Void, Void, Void> {
        private Boolean send_contacts = false;
        private Context context;

        public DownloadContacts(Context context) {
            this.context = context;
        }

        /**
         * Get contact info and send it to amazon
         * @return null for async task syntax
         */
        protected Void doInBackground(Void... param) {
            if (!syncCalendars) {
                try {
                    Utils.createFile(getActivity(), "initialActivities.json", new JSONArray().toString());
                } catch (Exception e) {
                    Utils.Log("Exception in createFile: " + e.getMessage());
                    displayFailMessage();
                }
            }

            try {
                JSONArray contacts = TactDataSource.getContactsJson(context, context.getContentResolver());
                send_contacts = contacts != null;
                Utils.createFile(getActivity(), "initialContacts.json", contacts.toString());
            } catch (Exception e) {
                Utils.Log("Exception in createFile: " + e.getMessage());
                displayFailMessage();
            }

            return null;
        }

        /**
         * Set the connect contacts view
         */
        protected void onPreExecute() {
            syncContactsProcessing = true;
            EventBus.getDefault().postSticky(new EventOnboardingChanges(EventOnboardingChanges.ChangeType.SYNC_CONTACTS_PROCESSING, true));
            //Set fail text block invisible
            failed_block.setVisibility(View.INVISIBLE);

            //Set listener for retry contact button
            retry_contact_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new DownloadContacts(context).execute();
                }
            });
            progressBar.setVisibility(View.VISIBLE);
        }

        /**
         * Evaluates if an error occur or if we have to go to next step
         * @param result null for async task syntax
         */
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            syncContactsProcessing = false;
            EventBus.getDefault().postSticky(new EventOnboardingChanges(EventOnboardingChanges.ChangeType.SYNC_CONTACTS_PROCESSING, false));
            if (!send_contacts) {
                displayFailMessage();
            }
            else {
                if (syncCalendars){
                    EventBus.getDefault().postSticky(new EventMoveNextFragmentOnboarding(TactConst.FRAGMENT_ONBOARDING_CALENDARS)); // move to sync calendars
                }
                else {
                    EventBus.getDefault().postSticky(new EventFinalizeLocalSources());
                }
            }
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        TactApplication.unbindDrawables(rootView);
        rootView = null;
        syncCalendars = null;
        retry_contact_btn = null;
        failed_block = null;
        progressBar = null;
        syncContactsProcessing = null;
        System.gc();
    }
}
