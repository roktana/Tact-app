package com.tactile.tact.fragments;

import android.content.Context;
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
import com.tactile.tact.services.events.EventContactsUploadDetailsAmazonError;
import com.tactile.tact.services.events.EventContactsUploadDetailsError;
import com.tactile.tact.services.events.EventDeviceInformationError;
import com.tactile.tact.services.events.EventFinalizeLocalSources;
import com.tactile.tact.services.events.EventHandleProgress;
import com.tactile.tact.services.events.EventMoveNextFragmentOnboarding;
import com.tactile.tact.services.events.EventOnboardingChanges;
import com.tactile.tact.services.network.TactNetworking;
import com.tactile.tact.utils.TactDialogHandler;
import com.tactile.tact.utils.Utils;

import org.json.JSONArray;

import de.greenrobot.event.EventBus;

/**
 * Created by sebafonseca on 10/29/14.
 */
public class FragmentOnboardingLocalSources extends FragmentTactBase {

    private TextView contacts_text_view;
    private TextView meetings_text_view;
    private Boolean syncContacts;
    private Boolean syncCalendars;
    private Boolean contact_switch_confirmed = false;
    private Boolean calendar_switch_confirmed = false;
    private Button continue_btn;
    private CheckBox contact_switch;
    private CheckBox calendar_switch;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            syncContacts    = bundle.getBoolean("syncContacts");
            syncCalendars   = bundle.getBoolean("syncCalendars");
        }

        rootView = inflater.inflate(R.layout.on_boarding_local_sources, container, false);

        setViewItems(rootView, inflater);

        return rootView;
    }

    private void setViewItems(View view, final LayoutInflater inflater){
        //Get all buttons and labels
        contact_switch   = (CheckBox) view.findViewById(R.id.contact_switch);
        calendar_switch  = (CheckBox) view.findViewById(R.id.calendar_switch);
        continue_btn     = (Button) view.findViewById(R.id.general_continue_btn);
        contacts_text_view = (TextView) view.findViewById(R.id.contacts_text_view);
        meetings_text_view = (TextView) view.findViewById(R.id.meetings_text_view);
        Button skip        = (Button) view.findViewById(R.id.general_continue_skip_btn);
        LinearLayout contacts = (LinearLayout) view.findViewById(R.id.contacts_container);
        LinearLayout meetings = (LinearLayout) view.findViewById(R.id.meetings_container);

        contact_switch.setChecked(syncContacts);
        if (syncContacts) {
            contacts_text_view.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        }
        calendar_switch.setChecked(syncCalendars);
        if (syncCalendars) {
            meetings_text_view.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        }
        //Set contact switch listener
        contact_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                syncContacts = isChecked;
                if (isChecked) {
                    contacts_text_view.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
                } else {
                    contacts_text_view.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
                }
                EventBus.getDefault().post(new EventOnboardingChanges(EventOnboardingChanges.ChangeType.SYNC_CONTACTS, syncContacts));
                if (!contact_switch_confirmed && !isChecked) {
                    contact_switch_confirmed = true;
                    getConfirmDialog(0, inflater.getContext());
                }
            }
        });

        contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contact_switch.setChecked(!contact_switch.isChecked());
            }
        });

        //Set calendar switch listener
        calendar_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                syncCalendars = isChecked;
                if (isChecked) {
                    meetings_text_view.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
                } else {
                    meetings_text_view.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
                }
                EventBus.getDefault().post(new EventOnboardingChanges(EventOnboardingChanges.ChangeType.SYNC_CALENDARS, syncCalendars));
                if (!calendar_switch_confirmed && !isChecked) {
                    calendar_switch_confirmed = true;
                    getConfirmDialog(1, inflater.getContext());
                }
            }
        });

        meetings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar_switch.setChecked(!calendar_switch.isChecked());
            }
        });

        //Set continue button listener
        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TactNetworking.checkInternetConnection(true)){

                    if (syncContacts || syncCalendars) {
                        if (!syncContacts){
                            try {
                                Utils.createFile(getActivity(), "initialContacts.json", new JSONArray().toString());
                            } catch (Exception e) {
                            }
                            EventBus.getDefault().post(new EventMoveNextFragmentOnboarding(TactConst.FRAGMENT_ONBOARDING_CALENDARS)); // move to sync calendars
                        }
                        else {
                            EventBus.getDefault().post(new EventMoveNextFragmentOnboarding(TactConst.FRAGMENT_ONBOARDING_CONTACTS)); // move to sync contacts and then move to sync calendars
                        }
                    } else {
                        try {
                            Utils.createFile(getActivity(), "initialActivities.json", new JSONArray().toString());
                            Utils.createFile(getActivity(), "initialContacts.json", new JSONArray().toString());
                        }
                        catch (Exception e){}
                        EventBus.getDefault().postSticky(new EventHandleProgress(true, false));
                        EventBus.getDefault().postSticky(new EventFinalizeLocalSources());
                    }
                }
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Utils.createFile(getActivity(), "initialActivities.json", new JSONArray().toString());
                    Utils.createFile(getActivity(), "initialContacts.json", new JSONArray().toString());
                }
                catch (Exception e){}
                EventBus.getDefault().postSticky(new EventHandleProgress(true, false));
                EventBus.getDefault().postSticky(new EventFinalizeLocalSources());
            }
        });
    }

    public void onEventMainThread(EventDeviceInformationError eventDeviceInformationError){
        EventBus.getDefault().removeStickyEvent(eventDeviceInformationError);
        displayError();
    }

    public void onEventMainThread(EventContactsUploadDetailsError eventContactsUploadDetailsError){
        EventBus.getDefault().removeStickyEvent(eventContactsUploadDetailsError);
        displayError();
    }

    public void onEventMainThread(EventContactsUploadDetailsAmazonError eventContactsUploadDetailsAmazonError){
        EventBus.getDefault().removeStickyEvent(eventContactsUploadDetailsAmazonError);
        displayError();
    }

    private void displayError(){
        Toast.makeText(getActivity(), getResources().getString(R.string.local_sources_error), Toast.LENGTH_LONG).show();
        EventBus.getDefault().postSticky(new EventHandleProgress(false, false));
    }


    public View.OnClickListener doneListener(final int dialog_text_index, final TactDialogHandler dialog){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog_text_index == 0) { //corresponds to contacts
                    syncContacts = false;
                    EventBus.getDefault().post(new EventOnboardingChanges(EventOnboardingChanges.ChangeType.SYNC_CONTACTS, false));
                }
                else { //corresponds to calendars
                    syncCalendars = false;
                    EventBus.getDefault().post(new EventOnboardingChanges(EventOnboardingChanges.ChangeType.SYNC_CALENDARS, false));
                }
                dialog.dismiss();

            }
        };
    }

    public View.OnClickListener cancelListener(final int dialog_text_index, final TactDialogHandler dialog){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog_text_index == 0) { //corresponds to contacts
                    syncContacts = true;
                    contact_switch.setChecked(true);
                    EventBus.getDefault().post(new EventOnboardingChanges(EventOnboardingChanges.ChangeType.SYNC_CONTACTS, true));
                }
                else { //corresponds to calendars
                    syncCalendars = true;
                    calendar_switch.setChecked(true);
                    EventBus.getDefault().post(new EventOnboardingChanges(EventOnboardingChanges.ChangeType.SYNC_CALENDARS, true));
                }
                dialog.dismiss();

            }
        };
    }

    /**
     * Display the confirmation dialog
     * @param dialog_text_index 0 to display info of the contacts / 1 to display info of the calendars
     */
    private void getConfirmDialog(final int dialog_text_index, Context context){
        TactDialogHandler dialog = new TactDialogHandler(context);

        String legend_text      = (getResources().getStringArray(R.array.local_sources_dialogs))[dialog_text_index];

        dialog.showConfirmation(legend_text, context.getResources().getString(R.string.are_you_sure),
                context.getResources().getString(R.string.confirm), context.getResources().getString(R.string.cancel),
                doneListener(dialog_text_index, dialog), cancelListener(dialog_text_index, dialog));
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        TactApplication.unbindDrawables(rootView);
        rootView = null;
        syncContacts = null;
        syncCalendars = null;
        contact_switch_confirmed = null;
        calendar_switch_confirmed = null;
        continue_btn = null;
        contact_switch = null;
        calendar_switch = null;
        System.gc();
    }
}
