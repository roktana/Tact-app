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

import com.tactile.tact.R;
import com.tactile.tact.consts.TactConst;
import com.tactile.tact.database.DatabaseManager;
import com.tactile.tact.database.entities.Contact;
import com.tactile.tact.database.model.FeedItem;
import com.tactile.tact.database.model.FeedItemEvent;
import com.tactile.tact.database.model.FeedItemNote;
import com.tactile.tact.database.model.FragmentMoveHandler;
import com.tactile.tact.services.events.EventGoBackHomeActivity;
import com.tactile.tact.services.events.EventHideActivityActionBar;
import com.tactile.tact.services.events.EventMoveToFragment;
import com.tactile.tact.services.network.RedirectURLGetter;
import com.tactile.tact.services.network.TactNetworking;
import com.tactile.tact.utils.ContactAPI;
import com.tactile.tact.views.ProfileIconView;

import de.greenrobot.event.EventBus;

/**
 * Created by ismael on 1/19/15.
 */
public class FragmentNoteDetails extends FragmentTactBase {

    private FeedItemNote note;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View noteDetailsView = inflater.inflate(R.layout.note_details, container, false);

        EventBus.getDefault().post(new EventHideActivityActionBar());
        LinearLayout actionBar = (LinearLayout) noteDetailsView.findViewById(R.id.note_details_action_bar);
        actionBar.findViewById(R.id.fragment_actionBar_layout).setBackgroundColor(getResources().getColor(R.color.tact_violet));
        ((ImageView)actionBar.findViewById(R.id.fragment_menu_button)).setImageDrawable(getResources().getDrawable(R.drawable.back_arrow_white));
        ((ImageView)actionBar.findViewById(R.id.fragment_option_menu_btn)).setImageDrawable(getResources().getDrawable(R.drawable.settings_white));
        actionBar.findViewById(R.id.fragment_actionBar_option_button_layout).setVisibility(View.GONE);
        actionBar.findViewById(R.id.fragment_actionBar_secondary_button_1).setVisibility(View.VISIBLE);
        actionBar.findViewById(R.id.fragment_actionBar_secondary_button_2).setVisibility(View.VISIBLE);
        ((ImageView)actionBar.findViewById(R.id.imageview_secondary_1)).setImageDrawable(getResources().getDrawable(R.drawable.trash_icon));
        ((ImageView)actionBar.findViewById(R.id.imageview_secondary_2)).setImageDrawable(getResources().getDrawable(R.drawable.edit_icon));
        LinearLayout delete = (LinearLayout) noteDetailsView.findViewById(R.id.fragment_actionBar_secondary_button_1);
        LinearLayout edit = (LinearLayout) noteDetailsView.findViewById(R.id.fragment_actionBar_secondary_button_2);

        LinearLayout backButton  = (LinearLayout) actionBar.findViewById(R.id.fragment_actionBar_menu_button_layout);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EventGoBackHomeActivity());
            }
        });

        TextView titleActionBar = (TextView) actionBar.findViewById(R.id.fragment_actionBar_title_text);
        titleActionBar.setTextColor(getResources().getColor(R.color.white));
        titleActionBar.setText(getResources().getString(R.string.note_details));

        note = (FeedItemNote) getArguments().getSerializable("note");

        TextView title = (TextView) noteDetailsView.findViewById(R.id.note_details_title_text_view);
        TextView body = (TextView) noteDetailsView.findViewById(R.id.note_details_body_text_view);

        title.setText(note.getSubject() != null ? note.getSubject() : "");

        body.setText(note.getDescription() != null ? note.getDescription() : "");

        LinearLayout objectLayout = (LinearLayout) noteDetailsView.findViewById(R.id.parent_layout);
        final FeedItem parent = note.getMasterFeedItem();
        if (parent != null) {
            if (parent instanceof FeedItemEvent) {
                TextView objectTitle = (TextView) noteDetailsView.findViewById(R.id.note_meeting_title);
                TextView objectSubtitle = (TextView) noteDetailsView.findViewById(R.id.note_meeting_subtitle);
                FeedItemEvent parentEvent = (FeedItemEvent)parent;
                if (parentEvent.getSubject() != null && !parentEvent.getSubject().isEmpty()) {
                    objectTitle.setText(parentEvent.getSubject());
                }
                if (parentEvent.getOrganizerToShow() != null) {
                    objectSubtitle.setText(parentEvent.getOrganizerToShow().getNameToShow());
                } else if (parentEvent.getLocation() != null && !parentEvent.getLocation().isEmpty())  {
                    objectSubtitle.setText(parentEvent.getLocation());
                }
                objectLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setBackStack(getFragmentMoveHandler());
                        EventBus.getDefault().post(new EventMoveToFragment(TactConst.FRAGMENT_EVENT_DETAIL_TAG, 0, 0, parent, 0));
                    }
                });
            }
        } else {
            objectLayout.setVisibility(View.GONE);
        }

        if (note.getParentIdTact() != null){
            if (note.getParentIdTact().getType().equals("Account") || note.getParentIdTact().getType().equals("Contact")) {
                Contact contact = DatabaseManager.getContactByRecordId(note.getParentIdTact().getIdentifier());
                if (contact != null) {
                    setContactView(contact, noteDetailsView);
                }
                else {
                    hideRelatedTo(noteDetailsView);
                }
            }
            else {
                hideRelatedTo(noteDetailsView);
            }
        }
        else {
            hideRelatedTo(noteDetailsView);
        }

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackStack(getFragmentMoveHandler());
                EventBus.getDefault().post(new EventMoveToFragment(TactConst.FRAGMENT_NOTE_CREATE_TAG, 0, 0, note, 0));
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("NOTE", "Handle DELETE!");
            }
        });

        return noteDetailsView;
    }

    private void setContactView(final Contact contact, View view){
        LinearLayout container = (LinearLayout) view.findViewById(R.id.meeting_invitee_layout);
        TextView contact_name = (TextView) view.findViewById(R.id.contact_name_text_view);
        ProfileIconView profileIconView = (ProfileIconView) view.findViewById(R.id.contact_details_icon_small);
        TextView company_name_text_view = (TextView) view.findViewById(R.id.company_name_text_view);
        company_name_text_view.setVisibility(View.GONE);

        if (contact != null){
            contact_name.setText(contact.getDisplayName());
            if (contact.isLocal() && contact.getContactDetails().size() > 0){
                try {
                    Bitmap bitmap = ContactAPI.getContactImage(Integer.parseInt(contact.getContactDetails().get(0).getRemoteId()));
                    profileIconView.loadImage(bitmap, contact.getInitials());
                }
                catch (Exception e){
                    Log.w("LOAD IMAGE", e.getMessage());
                }
            }
            else {
                if (contact.getRealPhotoUrl() != null) {
                    profileIconView.loadImage(contact.getRealPhotoUrl(), contact.getInitials(), contact.isCompany());
                } else {
                    profileIconView.loadImage(null, contact.getInitials(), contact.isCompany());
                    if (contact.getPhotoUrl() != null && !contact.getPhotoUrl().isEmpty() &&
                            !contact.getPhotoUrl().startsWith("http")) {
                        RedirectURLGetter asyncTask = new RedirectURLGetter(FragmentNoteDetails.this, profileIconView,
                                contact.getInitials(), contact.isCompany());
                        asyncTask.execute(TactNetworking.getURL() + "/photo_store?photo_url=" + contact.getPhotoUrl());
                    }
                }
            }
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setBackStack(getFragmentMoveHandler());
                    EventBus.getDefault().post(new EventMoveToFragment(TactConst.FRAGMENT_CONTACT_DETAIL_TAG, 0, 0, contact, 0));
                }
            });
        }
    }

    private void hideRelatedTo(View view){
        view.findViewById(R.id.related_to).setVisibility(View.GONE);
    }

    private FragmentMoveHandler getFragmentMoveHandler() {
        FragmentMoveHandler fragmentMoveHandler = new FragmentMoveHandler();
        fragmentMoveHandler.setFragmentTag(TactConst.FRAGMENT_NOTE_DETAIL_TAG);
        fragmentMoveHandler.setObject(note);
        return  fragmentMoveHandler;
    }

    public void notifyURLGetterTaskCompleted(String realPhotoUrl, ProfileIconView view, String initials, Boolean isCompany){
        if (realPhotoUrl != null && realPhotoUrl.startsWith("http")){
            view.loadImage(realPhotoUrl, initials, isCompany);
        }
        view = null;
        System.gc();
    }
}
