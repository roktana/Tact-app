package com.tactile.tact.fragments;

import android.content.Context;
import android.os.AsyncTask;
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
import com.tactile.tact.controllers.TactSharedPrefController;
import com.tactile.tact.database.TactDataSource;
import com.tactile.tact.services.events.EventContactsUploadDetailsAmazonError;
import com.tactile.tact.services.events.EventContactsUploadDetailsError;
import com.tactile.tact.services.events.EventDeviceInformationError;
import com.tactile.tact.services.events.EventFinalizeLocalSources;
import com.tactile.tact.services.events.EventHandleProgress;
import com.tactile.tact.services.network.TactNetworking;
import com.tactile.tact.utils.CalendarAPI;
import com.tactile.tact.utils.CircleView;
import com.tactile.tact.utils.Utils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by sebafonseca on 10/31/14.
 */
public class FragmentOnboardingCalendars extends FragmentTactBase {

    private ArrayList<Long> ids_calendar = new ArrayList<Long>();
    private Boolean show_calendars = true;
    private Button continue_btn;
    private String sourceName;
    private Integer sourceId;
    private View rootView;
    private Button skip_btn_calendar;
    private Boolean syncContacts = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.on_boarding_calendar_upload, container, false);

        setViewItems(rootView, inflater.getContext());

        Bundle bundle = getArguments();
        if (bundle != null) {
            sourceId = bundle.getInt("sourceId");
            sourceName = bundle.getString("sourceName");
            syncContacts = bundle.getBoolean("syncContacts");
        }

        return rootView;
    }

    private void setViewItems(View view, Context context){
        final ArrayList<Long> all_ids_calendar = new ArrayList<Long>();
        skip_btn_calendar = (Button) view.findViewById(R.id.general_continue_skip_btn);

        continue_btn = (Button) view.findViewById(R.id.general_continue_btn);
        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] calendars = new String[ids_calendar.size() + 1];
                for (int i=0; i < ids_calendar.size(); i++){
                    calendars[i] = ids_calendar.get(i).toString();
                    Utils.Log("Selected calendar id: " + calendars[i]);
                }
                if (TactNetworking.checkInternetConnection(true)){
                    EventBus.getDefault().postSticky(new EventHandleProgress(true, false));
                    new DownloadCalendars().execute(calendars);
                }
            }
        });
        skip_btn_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!syncContacts) {
                        Utils.createFile(getActivity(), "initialActivities.json", new JSONArray().toString());
                    }
                    Utils.createFile(getActivity(), "initialContacts.json", new JSONArray().toString());
                }
                catch (Exception e){}
                EventBus.getDefault().postSticky(new EventHandleProgress(true, false));
                EventBus.getDefault().postSticky(new EventFinalizeLocalSources());
            }
        });

        List<CalendarAPI.CalendarInfo> calendars = CalendarAPI.getCalendarList(context, null, null);
        if (calendars.size() > 0) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final LinearLayout calendar_list = (LinearLayout) view.findViewById(R.id.calendar_list);

            Boolean isFirstItem = true;
            final HashMap<CheckBox, TextView> checkboxes = new HashMap<CheckBox, TextView>();
            for (CalendarAPI.CalendarInfo calendar : calendars) {

                final View calendar_item                = inflater.inflate(R.layout.on_boarding_list_calendar, null, false);
                final CheckBox calendar_checkbox        = (CheckBox) calendar_item.findViewById(R.id.check_box_calendar_selected);
                final TextView calendar_name            = (TextView) calendar_item.findViewById(R.id.text_view_calendar_name);
                final TextView calendar_description     = (TextView) calendar_item.findViewById(R.id.text_view_calendar_description);
                final CircleView calendar_color         = (CircleView) calendar_item.findViewById(R.id.view_calendar_color);
                checkboxes.put(calendar_checkbox, calendar_name);

//                //CREATE view show/hide all calendars
//                View show_calendars_view = inflater.inflate(R.layout.display_calendars, null, false);
//                final TextView show_hide_calendars = (TextView) show_calendars_view.findViewById(R.id.show_all_calendars_text);
//                show_calendars_view.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if (show_calendars){
//                            show_calendars = false;
//                            show_hide_calendars.setText(R.string.show_all_calendars);
//                            for(CheckBox checkbox: checkboxes.keySet()) {
//                                if (checkbox.isChecked()) {
//                                    checkbox.setChecked(false);
//                                }
//                                checkboxes.get(checkbox).setTextColor(getResources().getColor(R.color.text_grey_disable));
//                            }
//                            ids_calendar = new ArrayList<Long>();
//                        }
//                        else{
//                            show_calendars = true;
//                            show_hide_calendars.setText(R.string.hide_all_calendars);
//                            for(CheckBox checkbox: checkboxes.keySet()) {
//                                if (!checkbox.isChecked()) {
//                                    checkbox.setChecked(true);
//                                }
//                                checkboxes.get(checkbox).setTextColor(getResources().getColor(R.color.text_grey));
//                            }
//                            Collections.copy(ids_calendar, all_ids_calendar);
//                        }
//                    }
//                });
//
//                calendar_list.addView(show_calendars_view);
//                isFirstItem = false;

                calendar_checkbox.setChecked(true);
                calendar_checkbox.setTag(calendar.getId());
                calendar_name.setText(calendar.getCalendarName());
                calendar_name.setTextColor(getResources().getColor(R.color.text_grey));

                calendar_description.setVisibility(View.GONE);

                calendar_color.setFillColor(calendar.getColor());

                ids_calendar.add(calendar.getId());
                all_ids_calendar.add(calendar.getId());

                calendar_item.setClickable(true);
                calendar_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        calendar_checkbox.setChecked(!calendar_checkbox.isChecked());
                    }
                });
                calendar_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (isChecked){
                            if (!ids_calendar.contains(compoundButton.getTag())){
                                ids_calendar.add((Long)compoundButton.getTag());
                            }
                            calendar_name.setTextColor(getResources().getColor(R.color.text_grey));
                        }
                        else {
                            if (ids_calendar.contains(compoundButton.getTag())){
                                ids_calendar.remove((Long)compoundButton.getTag());
                            }
                            calendar_name.setTextColor(getResources().getColor(R.color.text_grey_disable));
                        }
                    }
                });

                calendar_list.addView(calendar_item);
            }
        }
//        else {
//            TextView no_calendars = (TextView) view.findViewById(R.id.no_calendars);
//            no_calendars.setVisibility(View.VISIBLE);
//        }
    }

    /**
     * Download calendars asynchronously
     */
    private class DownloadCalendars extends AsyncTask<Object, Void, Void> {
        protected Void doInBackground(Object... params) {
            String[] calendars = new String[params.length-1];
            for (int i=0; i<params.length-1; i++){
                calendars[i] = (String)params[i];
                TactSharedPrefController.addOnboardingCalendarId(calendars[i]);
            }
            String file_name = "initialActivities.json";
            if (calendars.length > 0){
                JSONArray calendarsJson = TactDataSource.getCalendars(sourceName, sourceId, getActivity(), calendars);
                try {
                    Utils.createFile(getActivity(), file_name, calendarsJson.toString());
                }
                catch (Exception e){
                    try {
                        Utils.createFile(getActivity(), file_name, new JSONArray().toString());
                    }
                    catch (Exception ex){}
                }
            }
            else {
                try {
                    Utils.createFile(getActivity(), file_name, new JSONArray().toString());
                }
                catch (Exception e){}
            }
            EventBus.getDefault().postSticky(new EventFinalizeLocalSources());
            return null;
        }
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
        EventBus.getDefault().postSticky(new EventHandleProgress(false, false));
        continue_btn.setClickable(true);
        TactSharedPrefController.removeOnboardingCalendarIds();
        Toast.makeText(getActivity(), getResources().getString(R.string.local_sources_error), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        TactApplication.unbindDrawables(rootView);
        rootView = null;
        ids_calendar = null;
        show_calendars = null;
        continue_btn = null;
        sourceName = null;
        sourceId = null;
        System.gc();
    }
}
