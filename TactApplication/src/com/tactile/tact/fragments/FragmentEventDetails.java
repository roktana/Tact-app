package com.tactile.tact.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.nirhart.parallaxscroll.views.ParallaxScrollView;
import com.tactile.tact.R;
import com.tactile.tact.consts.TactConst;
import com.tactile.tact.database.DatabaseManager;
import com.tactile.tact.database.entities.Contact;
import com.tactile.tact.database.model.FeedItem;
import com.tactile.tact.database.model.FeedItemEvent;
import com.tactile.tact.database.model.FeedItemLog;
import com.tactile.tact.database.model.FeedItemNote;
import com.tactile.tact.database.model.FeedItemRelatedContact;
import com.tactile.tact.database.model.FragmentMoveHandler;
import com.tactile.tact.services.events.EventGoBackHomeActivity;
import com.tactile.tact.services.events.EventHideActivityActionBar;
import com.tactile.tact.services.events.EventMoveToFragment;
import com.tactile.tact.services.events.EventOptionButtonPressed;
import com.tactile.tact.services.network.RedirectURLGetter;
import com.tactile.tact.services.network.TactNetworking;
import com.tactile.tact.utils.CalendarAPI;
import com.tactile.tact.utils.ContactAPI;
import com.tactile.tact.utils.ObservableScrollView;
import com.tactile.tact.utils.TactParallaxScrollView;
import com.tactile.tact.utils.interfaces.ScrollViewListener;
import com.tactile.tact.views.ProfileIconView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by ismael on 11/24/14.
 */
public class FragmentEventDetails extends FragmentTactBase {

    private FeedItemEvent event;
    private TactParallaxScrollView meetingDetailsScrollView;
    private TextView fragment_actionBar_title_text;
    private FloatingActionsMenu floating_action_menu;
    private FloatingActionButton newLog;
    private FloatingActionButton newTask;
    private FloatingActionButton newNote;
    private LinearLayout action_bar_layout;
    private LinearLayout log_item;

    private LinearLayout actionBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            event = (FeedItemEvent) bundle.getSerializable("event");
        }

        View taskDetailsView = inflater.inflate(R.layout.meeting_detail, container, false);
        EventBus.getDefault().post(new EventHideActivityActionBar());
        actionBar = (LinearLayout) taskDetailsView.findViewById(R.id.meeting_action_bar);
        action_bar_layout = (LinearLayout)actionBar.findViewById(R.id.fragment_actionBar_layout);
        action_bar_layout.setBackgroundColor(getResources().getColor(R.color.clear_blue_gray));
        ((ImageView)actionBar.findViewById(R.id.fragment_menu_button)).setImageDrawable(getResources().getDrawable(R.drawable.back_arrow_white));
        ((ImageView)actionBar.findViewById(R.id.fragment_option_menu_btn)).setImageDrawable(getResources().getDrawable(R.drawable.settings_white));
        fragment_actionBar_title_text = (TextView) action_bar_layout.findViewById(R.id.fragment_actionBar_title_text);
        action_bar_layout.getBackground().setAlpha(0);
        fragment_actionBar_title_text.setAlpha(0);
        meetingDetailsScrollView = (TactParallaxScrollView) taskDetailsView.findViewById(R.id.meeting_scroll_view);

        LinearLayout backButton  = (LinearLayout) actionBar.findViewById(R.id.fragment_actionBar_menu_button_layout);
        LinearLayout optionButton = (LinearLayout) actionBar.findViewById(R.id.fragment_actionBar_option_button_layout);

        log_item = (LinearLayout) taskDetailsView.findViewById(R.id.log_item);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EventGoBackHomeActivity());
            }
        });

        optionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EventOptionButtonPressed());
            }
        });

        floating_action_menu = (FloatingActionsMenu) taskDetailsView.findViewById(R.id.multiple_actions_down);
        newLog = (FloatingActionButton) floating_action_menu.findViewById(R.id.new_log_floating_btn);
        newTask = (FloatingActionButton) floating_action_menu.findViewById(R.id.new_task_floating_btn);
        newNote = (FloatingActionButton) floating_action_menu.findViewById(R.id.new_note_floating_btn);

        if (DatabaseManager.getAccountId("salesforce") == null || DatabaseManager.getAccountId("salesforce").equals("")) {
            floating_action_menu.setVisibility(View.GONE);
        }

        //**************LOGGING********************

        LinearLayout log1_layout = (LinearLayout) taskDetailsView.findViewById(R.id.log1_layout);
        LinearLayout log2_layout = (LinearLayout) taskDetailsView.findViewById(R.id.log2_layout);
        LinearLayout log_list = (LinearLayout)taskDetailsView.findViewById(R.id.meeting_details_log_part);
        LinearLayout allLogsLayout = (LinearLayout) log_list.findViewById(R.id.all_logs_layout);
        LinearLayout divider1 = (LinearLayout)log1_layout.findViewById(R.id.log_item_divider);
        LinearLayout divider2 = (LinearLayout)log2_layout.findViewById(R.id.log_item_divider);

        final ArrayList<FeedItem> relatedObjects = event.getRelatedObjects();
        if (relatedObjects != null && relatedObjects.size() > 0) {
            log1_layout.setVisibility(View.GONE);
            log2_layout.setVisibility(View.GONE);
            allLogsLayout.setVisibility(View.GONE);
            divider1.setVisibility(View.GONE);
            divider2.setVisibility(View.GONE);
            if (relatedObjects.size() >= 1) {
                setRelatedObjectView(relatedObjects.get(0), log1_layout);
            }
            if (relatedObjects.size() >= 2) {
                divider1.setVisibility(View.VISIBLE);
                divider2.setVisibility(View.GONE);
                setRelatedObjectView(relatedObjects.get(1), log2_layout);
            }
            if (relatedObjects.size() > 2) {
                allLogsLayout.findViewById(R.id.log_list_item_contact_text_view).setVisibility(View.GONE);
                TextView log_list_item_title_text_view = (TextView) allLogsLayout.findViewById(R.id.log_list_item_title_text_view);
                log_list_item_title_text_view.setText(getResources().getString(R.string.meeting_log_all));
                log_list_item_title_text_view.setAllCaps(true);
                allLogsLayout.findViewById(R.id.log_list_item_image).setVisibility(View.GONE);
                allLogsLayout.findViewById(R.id.log_item_divider).setVisibility(View.GONE);
                divider1.setVisibility(View.VISIBLE);
                divider2.setVisibility(View.VISIBLE);
                allLogsLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setBackStack(getFragmentMoveHandler());
                        EventBus.getDefault().post(new EventMoveToFragment(TactConst.FRAGMENT_LOG_LIST_TAG, 0, 0, event, 0));
                    }
                });
                allLogsLayout.setVisibility(View.VISIBLE);
            }
        }
        else {
            log_list.setVisibility(View.GONE);
        }
        //*************************************************************


        newLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackStack(getFragmentMoveHandler());
                EventBus.getDefault().post(new EventMoveToFragment(TactConst.FRAGMENT_LOG_CREATE_TAG, 0, 0, event, 0));
            }
        });
        newTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackStack(getFragmentMoveHandler());
                EventBus.getDefault().post(new EventMoveToFragment(TactConst.FRAGMENT_TASK_CREATE_TAG, 0, 0, null, 0));
            }
        });
        newNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackStack(getFragmentMoveHandler());
                EventBus.getDefault().post(new EventMoveToFragment(TactConst.FRAGMENT_NOTE_CREATE_TAG, 0, 0, null, 0));
            }
        });

        setViewElements(taskDetailsView, inflater);

        return taskDetailsView;
    }

    private FragmentMoveHandler getFragmentMoveHandler() {
        FragmentMoveHandler fragmentMoveHandler = new FragmentMoveHandler();
        fragmentMoveHandler.setFragmentTag(TactConst.FRAGMENT_EVENT_DETAIL_TAG);
        fragmentMoveHandler.setObject(event);
        return  fragmentMoveHandler;
    }

    private void setRelatedObjectView(final FeedItem item, LinearLayout layout){
        TextView title = (TextView) layout.findViewById(R.id.log_list_item_title_text_view);
        TextView contact_view = (TextView) layout.findViewById(R.id.log_list_item_contact_text_view);

        //Set the Title
        title.setText(item instanceof FeedItemLog ? ((FeedItemLog) item).getSubject() : ((FeedItemNote) item).getSubject());

        //Set the Contact
        if (item instanceof FeedItemLog) {
            FeedItemLog itemLog = (FeedItemLog) item;
            if (itemLog.getContact() != null && itemLog.getContact().getIdentifier() != null && !itemLog.getContact().getIdentifier().isEmpty()) {
                Contact contact = DatabaseManager.getContactByRecordId(itemLog.getContact().getIdentifier());
                if (contact != null) {
                    contact_view.setText(contact.getDisplayName());
                }
            } else if (itemLog.getRelated() != null && itemLog.getRelated().getIdentifier() != null && itemLog.getRelated().getType().equals("Company")) {
                Contact contact = DatabaseManager.getContactByRecordId(itemLog.getRelated().getIdentifier());
                if (contact != null) {
                    contact_view.setText(contact.getDisplayName());
                }
            }
        }
        else if (item instanceof FeedItemNote) {
            FeedItemNote itemNote = (FeedItemNote) item;
            if (itemNote.getParentIdTact() != null && itemNote.getParentIdTact().getIdentifier() != null && !itemNote.getParentIdTact().getIdentifier().isEmpty()) {
                Contact contact = DatabaseManager.getContactByRecordId(itemNote.getParentIdTact().getIdentifier());
                if (contact != null) {
                    contact_view.setText(contact.getDisplayName());
                }
            }
            //Set the Icon image
            ImageView note_icon = (ImageView) layout.findViewById(R.id.log_list_item_image);
            note_icon.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.notebook_icon_2));
        }

        //Set Event Handler
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackStack(getFragmentMoveHandler());
                if (item instanceof FeedItemLog) {
                    EventBus.getDefault().post(new EventMoveToFragment(TactConst.FRAGMENT_LOG_DETAIL_TAG, 0, 0, item, 0));
                }
                else if (item instanceof FeedItemNote){
                    EventBus.getDefault().post(new EventMoveToFragment(TactConst.FRAGMENT_NOTE_DETAIL_TAG, 0, 0, item, 0));
                }
            }
        });
        layout.setVisibility(View.VISIBLE);
    }

    private void setViewElements(View view, LayoutInflater inflater){
        //Set List of Log

//        log_list.removeAllViews();

        //TODO: if logs.size() == 0
//        log_list.setVisibility(View.GONE);



        //Log ListItem View
        //View log_listitem_view = inflater.inflate(R.layout.meeting_log_item, null, false);

        //Set Log item
        final LinearLayout log_item = (LinearLayout) view.findViewById(R.id.log_item);
        log_item.removeAllViews();
        String title_text = null;
        View log_item_view = inflater.inflate(R.layout.meeting_log_item_detail_header, null, false);
        LinearLayout organizer_container = (LinearLayout) log_item_view.findViewById(R.id.event_organizer_container);
        //LinearLayout location_container = (LinearLayout) log_item_view.findViewById(R.id.event_location_container);

        TextView organizer      = (TextView)log_item_view.findViewById(R.id.event_organizer);
        TextView subject        = (TextView)log_item_view.findViewById(R.id.event_subject);
        TextView location       = (TextView)log_item_view.findViewById(R.id.event_location);
        TextView time_date      = (TextView)log_item_view.findViewById(R.id.event_time_date);
        TextView time_duration  = (TextView)log_item_view.findViewById(R.id.event_time_duration);
        ImageView timeIcon      = (ImageView)log_item_view.findViewById(R.id.meeting_time_icon);

        final FeedItemRelatedContact contactOrganizer = event.getOrganizerToShow();
        String organizerStr = "";
        ProfileIconView iconView = (ProfileIconView) log_item_view.findViewById(R.id.contact_details_icon);

        if (contactOrganizer != null) {
            iconView.setVisibility(View.VISIBLE);
            if (contactOrganizer.getContact() != null) {
                organizerStr = contactOrganizer.getContact().getDisplayName();
                if (contactOrganizer.getContact().isLocal() && contactOrganizer.getContact().getContactDetails().size() > 0){
                    try {
                        Bitmap bitmap = ContactAPI.getContactImage(Integer.parseInt(contactOrganizer.getContact().getContactDetails().get(0).getRemoteId()));
                        iconView.loadImage(bitmap, contactOrganizer.getContact().getInitials());
                    }
                    catch (Exception e){
                        Log.w("LOAD IMAGE", e.getMessage());
                    }
                }
                else {
                    if (contactOrganizer.getContact().getRealPhotoUrl() != null) {
                        iconView.loadImage(contactOrganizer.getContact().getRealPhotoUrl(), contactOrganizer.getContact().getInitials(),
                                contactOrganizer.getContact().isCompany());
                    } else {
                        iconView.loadImage(null, contactOrganizer.getContact().getInitials(), contactOrganizer.getContact().isCompany());
                        if (contactOrganizer.getContact().getPhotoUrl() != null && !contactOrganizer.getContact().getPhotoUrl().isEmpty() &&
                                !contactOrganizer.getContact().getPhotoUrl().startsWith("http")) {
                            RedirectURLGetter asyncTask = new RedirectURLGetter(FragmentEventDetails.this, iconView,
                                    contactOrganizer.getContact().getInitials(), contactOrganizer.getContact().isCompany());
                            asyncTask.execute(TactNetworking.getURL() + "/photo_store?photo_url=" + contactOrganizer.getContact().getPhotoUrl());
                        }
                    }

                    iconView.loadImage(contactOrganizer.getContact().getPhotoUrl(), contactOrganizer.getContact().getInitials(), contactOrganizer.getContact().isCompany());
                }
                organizer_container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setBackStack(getFragmentMoveHandler());
                        EventBus.getDefault().post(new EventMoveToFragment(TactConst.FRAGMENT_CONTACT_DETAIL_TAG, 0, 0, contactOrganizer.getContact(), 0));
                    }
                });
            } else if (contactOrganizer.getName() != null) {
                organizerStr = contactOrganizer.getName();
            } else {
                organizerStr = contactOrganizer.getEmail() != null ? contactOrganizer.getEmail() : "";
            }
        }

        if (!organizerStr.isEmpty()) {
            organizer.setText(organizerStr);
            title_text = organizerStr;
        }
        else {
            organizer_container.setVisibility(View.GONE);
        }


        subject.setText(event.getSubject());
        if (title_text == null ||title_text.isEmpty()) {
            title_text = subject.getText().toString();
        }

        String locationStr = event.getLocation();
        if (locationStr == null || locationStr.isEmpty()) {
            location.setText("Unknown");
        }
        else {
            location.setText(locationStr);
        }

        time_date.setText((new SimpleDateFormat("EEEE, MMMM dd, yyyy")).format((event.getStartAt())));
        if (event.isAllDay()){
            time_duration.setText("All day");
        }
        else {
            String duration = CalendarAPI.getDate(event.getStartAt()).split("- ")[1];
            if (event.getEndAt() != null){
                duration += " to " + CalendarAPI.getDate(event.getEndAt()).split("- ")[1];
            }
            time_duration.setText(duration);
        }
        if (event.getAndroidRecurrence() != null && !event.getAndroidRecurrence().isEmpty()){
            TextView recurrenceView = (TextView)log_item_view.findViewById(R.id.event_recurrence);
            recurrenceView.setVisibility(View.VISIBLE);
            recurrenceView.setText("Repeats every " + event.getAndroidRecurrence());
        }

        if (event.isRecurrence()) {
            timeIcon.setImageDrawable(getResources().getDrawable(R.drawable.recurring_big));
        }

        log_item.addView(log_item_view);

        //Set Event Description
        final TextView event_description = (TextView) view.findViewById(R.id.event_description);
        final TextView see_more = (TextView) view.findViewById(R.id.event_description_see_more);
        final String event_descriptionStr = event.getDescription();
        final int headerHeight = getResources().getDimensionPixelSize(R.dimen.standard_action_bar_4x);
        if (event_descriptionStr == null || event_descriptionStr.isEmpty()){
            LinearLayout event_descrip_layout = (LinearLayout) view.findViewById(R.id.event_description_layout);
            event_descrip_layout.setVisibility(View.GONE);
            see_more.setVisibility(View.GONE);
        }
        else {
            if (event_descriptionStr.length() > 40) {
                event_description.setText(event_descriptionStr.substring(0, 40) + "...");
                see_more.setText(getActivity().getString(R.string.meeting_log_see_more));
                see_more.setVisibility(View.VISIBLE);
                see_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (see_more.getText().equals(getActivity().getString(R.string.meeting_log_see_more))) {
                            event_description.setText(event_descriptionStr);
                            see_more.setText(getActivity().getString(R.string.meeting_log_see_less));
                        } else {
                            event_description.setText(event_descriptionStr.substring(0, 40) + "...");
                            see_more.setText(getActivity().getString(R.string.meeting_log_see_more));
                        }
                    }
                });
            } else {
                event_description.setText(event_descriptionStr);
                see_more.setVisibility(View.GONE);
            }
        }

        if (event.getInvitedEmails() == null || event.getInvitedEmails().size() == 0){
            LinearLayout divider = (LinearLayout) view.findViewById(R.id.divider_line_layout);
            divider.setVisibility(View.GONE);
            LinearLayout invitees = (LinearLayout) view.findViewById(R.id.event_invitees);
            invitees.setVisibility(View.GONE);
        }
        else {
            ArrayList<FeedItemRelatedContact> contacts = event.getInviteesToShow();
            if (contacts.size() == 0){
                LinearLayout divider = (LinearLayout) view.findViewById(R.id.divider_line_layout);
                divider.setVisibility(View.GONE);
                LinearLayout invitees = (LinearLayout) view.findViewById(R.id.event_invitees);
                invitees.setVisibility(View.GONE);
            }
            else {
                LinearLayout invitees = (LinearLayout) view.findViewById(R.id.event_invitees);
                for (FeedItemRelatedContact contact: contacts){
                    if (contactOrganizer.getContact() == null || (!contact.getContact().getId().equals(contactOrganizer.getContact().getId()))){
                        invitees.addView(setInvitedView(contact, inflater));
                    }
                }
            }
        }

        fragment_actionBar_title_text.setText(title_text);

        meetingDetailsScrollView.setScrollViewListener(new ScrollViewListener() {
            @Override
            public void onScrollChanged(ObservableScrollView observableScrollView, int x, int y, int oldx, int oldy) {

            }

            @Override
            public void onParallaxScrollChanged(ParallaxScrollView observableScrollView, int x, int y, int oldx, int oldy) {
                if (y>0 && y<headerHeight) {
                    float ratio = 1 - Math.max((float) (headerHeight - y) / headerHeight, 0f);
                    int backgAlpha = Math.round(ratio*100);
                    action_bar_layout.getBackground().setAlpha(backgAlpha);
                    fragment_actionBar_title_text.setAlpha(ratio);
                    log_item.setAlpha(1-ratio);
                } else if (y==0) {
                    action_bar_layout.getBackground().setAlpha(0);
                    fragment_actionBar_title_text.setAlpha(0);
                    log_item.setAlpha(1);
                } else if (y>=headerHeight) {
                    action_bar_layout.getBackground().setAlpha(255);
                    fragment_actionBar_title_text.setAlpha(1);
                    log_item.setAlpha(0);
                }
            }
        });

    }

    private View setInvitedView(final FeedItemRelatedContact contact, LayoutInflater inflater){
        View invited_item     = inflater.inflate(R.layout.meeting_invitees_layout, null, false);
        TextView contact_name = (TextView) invited_item.findViewById(R.id.contact_name_text_view);
        ProfileIconView profileIconView = (ProfileIconView) invited_item.findViewById(R.id.contact_details_icon_small);
        TextView company_name_text_view = (TextView) invited_item.findViewById(R.id.company_name_text_view);
        company_name_text_view.setVisibility(View.GONE);

        if (contact.getContact() != null){
            contact_name.setText(contact.getContact().getDisplayName());
            if (contact.getContact().isLocal() && contact.getContact().getContactDetails().size() > 0){
                try {
                    Bitmap bitmap = ContactAPI.getContactImage(Integer.parseInt(contact.getContact().getContactDetails().get(0).getRemoteId()));
                    profileIconView.loadImage(bitmap, contact.getContact().getInitials());
                }
                catch (Exception e){
                    Log.w("LOAD IMAGE", e.getMessage());
                }
            }
            else {
                if (contact.getContact().getRealPhotoUrl() != null) {
                    profileIconView.loadImage(contact.getContact().getRealPhotoUrl(), contact.getContact().getInitials(), contact.getContact().isCompany());
                } else {
                    profileIconView.loadImage(null, contact.getContact().getInitials(), contact.getContact().isCompany());
                    if (contact.getContact().getPhotoUrl() != null && !contact.getContact().getPhotoUrl().isEmpty() &&
                            !contact.getContact().getPhotoUrl().startsWith("http")) {
                        RedirectURLGetter asyncTask = new RedirectURLGetter(FragmentEventDetails.this, profileIconView,
                                contact.getContact().getInitials(), contact.getContact().isCompany());
                        asyncTask.execute(TactNetworking.getURL() + "/photo_store?photo_url=" + contact.getContact().getPhotoUrl());
                    }
                }
            }
            invited_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setBackStack(getFragmentMoveHandler());
                    EventBus.getDefault().post(new EventMoveToFragment(TactConst.FRAGMENT_CONTACT_DETAIL_TAG, 0, 0, contact.getContact(), 0));
                }
            });
        }
        else if (contact.getName() != null) {
            contact_name.setText(contact.getName());
        }
        else {
            contact_name.setText(contact.getEmail() != null ? contact.getEmail() : "");
        }

        return invited_item;
    }

    public void notifyURLGetterTaskCompleted(String realPhotoUrl, ProfileIconView view, String initials, Boolean isCompany){
        if (realPhotoUrl != null && realPhotoUrl.startsWith("http")){
            view.loadImage(realPhotoUrl, initials, isCompany);
        }
        view = null;
        System.gc();
    }
}
