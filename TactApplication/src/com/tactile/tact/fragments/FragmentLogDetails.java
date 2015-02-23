package com.tactile.tact.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tactile.tact.R;
import com.tactile.tact.consts.TactConst;
import com.tactile.tact.database.DatabaseManager;
import com.tactile.tact.database.entities.Contact;
import com.tactile.tact.database.model.FeedItem;
import com.tactile.tact.database.model.FeedItemEvent;
import com.tactile.tact.database.model.FeedItemLog;
import com.tactile.tact.database.model.FragmentMoveHandler;
import com.tactile.tact.services.events.EventGoBackHomeActivity;
import com.tactile.tact.services.events.EventHideActivityActionBar;
import com.tactile.tact.services.events.EventMoveToFragment;
import com.tactile.tact.services.events.EventOptionButtonPressed;
import com.tactile.tact.utils.Utils;

import java.util.Calendar;

import de.greenrobot.event.EventBus;

/**
 * Created by ismael on 12/4/14.
 */
public class FragmentLogDetails extends FragmentTactBase {
    //****************************  CLASS VARIABLES *********************************\\

    // Private class variables
    private FeedItemLog log;
    private LinearLayout actionBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View logDetailsView = inflater.inflate(R.layout.log_details, container, false);

        actionBar = (LinearLayout) logDetailsView.findViewById(R.id.log_details_action_bar);
        actionBar.findViewById(R.id.fragment_actionBar_layout).setBackgroundColor(getResources().getColor(R.color.action_bar_grey));
        ((ImageView)actionBar.findViewById(R.id.fragment_menu_button)).setImageDrawable(getResources().getDrawable(R.drawable.back_arrow_orange));
        ((ImageView)actionBar.findViewById(R.id.fragment_option_menu_btn)).setImageDrawable(getResources().getDrawable(R.drawable.header_settings));
        ((TextView)actionBar.findViewById(R.id.fragment_actionBar_title_text)).setText("");

        LinearLayout backButton  = (LinearLayout) actionBar.findViewById(R.id.fragment_actionBar_menu_button_layout);
        LinearLayout optionButton = (LinearLayout) actionBar.findViewById(R.id.fragment_actionBar_option_button_layout);

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

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            log = (FeedItemLog) bundle.getSerializable("log");
        }

        TextView objectTitle = (TextView)logDetailsView.findViewById(R.id.meeting_log_title);
        TextView objectSubtitle = (TextView)logDetailsView.findViewById(R.id.meeting_log_subtitle);
        ImageView objectImage = (ImageView)logDetailsView.findViewById(R.id.meeting_log_image);
        LinearLayout objectLayout = (LinearLayout)logDetailsView.findViewById(R.id.parent_layout);

        TextView contactName = (TextView)logDetailsView.findViewById(R.id.contact_name_text_view);
        TextView relatedName = (TextView)logDetailsView.findViewById(R.id.related_to_name_text_view);
        TextView description = (TextView)logDetailsView.findViewById(R.id.log_description_text_view);
        TextView subject = (TextView)logDetailsView.findViewById(R.id.log_subject_text_view);
        TextView date = (TextView)logDetailsView.findViewById(R.id.due_text_view);

        final FeedItem parent = log.getMasterFeedItem();
        if (parent != null) {
            if (parent instanceof FeedItemEvent) {
                if (((FeedItemEvent)parent).getSubject() != null && !((FeedItemEvent)parent).getSubject().isEmpty()) {
                    objectTitle.setText(((FeedItemEvent)parent).getSubject());
                }
                if (((FeedItemEvent)parent).getOrganizerToShow() != null) {
                    objectSubtitle.setText(((FeedItemEvent)parent).getOrganizerToShow().getNameToShow());
                } else if (((FeedItemEvent)parent).getLocation() != null && !((FeedItemEvent)parent).getLocation().isEmpty())  {
                    objectSubtitle.setText(((FeedItemEvent)parent).getLocation());
                }
                objectImage.setImageResource(R.drawable.calendar_icon_2);
                objectLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setBackStack(getFragmentMoveHandler());
                        EventBus.getDefault().post(new EventMoveToFragment(TactConst.FRAGMENT_EVENT_DETAIL_TAG, 0, 0, ((FeedItemEvent)parent), 0));
                    }
                });
            }
        } else {
            objectLayout.setVisibility(View.GONE);
        }

        if (log.getContact() != null) {
            Contact contact = DatabaseManager.getContactByRecordId(log.getContact().getIdentifier());
            if (contact != null) {
                contactName.setText(contact.getDisplayName());
            }
            contact = null;
        }
        if (log.getRelated() != null) {
            if (log.getRelated().getType().equals("Account") || log.getRelated().getType().equals("Contact")) {
                Contact contact = DatabaseManager.getContactByRecordId(log.getRelated().getIdentifier());
                if (contact != null) {
                    relatedName.setText(contact.getDisplayName());
                }
                contact = null;
            }
        }

        description.setText(log.getDescription());
        subject.setText(log.getSubject());
        if (log.getDate() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(log.getDate() * 1000);
            date.setText(Utils.getDateStringFormated(calendar, "MMM dd, yyyy"));
            calendar = null;
        }



        System.gc();
        return logDetailsView;
    }

    private FragmentMoveHandler getFragmentMoveHandler() {
        FragmentMoveHandler fragmentMoveHandler = new FragmentMoveHandler();
        fragmentMoveHandler.setFragmentTag(TactConst.FRAGMENT_LOG_DETAIL_TAG);
        fragmentMoveHandler.setObject(log);
        return  fragmentMoveHandler;
    }
}
