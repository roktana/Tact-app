package com.tactile.tact.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.tactile.tact.R;
import com.tactile.tact.activities.LogContactLazyLoadListActivity;
import com.tactile.tact.consts.TactConst;
import com.tactile.tact.database.DatabaseManager;
import com.tactile.tact.database.entities.Contact;
import com.tactile.tact.database.model.FeedItemNote;
import com.tactile.tact.services.events.EventGoBackHomeActivity;
import com.tactile.tact.services.events.EventHideActivityActionBar;
import com.tactile.tact.services.events.EventOptionButtonPressed;

import de.greenrobot.event.EventBus;

/**
 * Created by ismael on 1/19/15.
 */
public class FragmentNoteCreate extends FragmentTactBase {

    private Object what;
    private Boolean salesforceNoteSync = false;
    private FeedItemNote note = null;

    private TextView relatedSelectName;
    private TextView relatedRequiredError;
    private TextView relatedSoft;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View noteView = inflater.inflate(R.layout.note_layout, container, false);
        if (getArguments() != null){
            note = (FeedItemNote) getArguments().getSerializable("note");
        }

        EventBus.getDefault().post(new EventHideActivityActionBar());
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        LinearLayout actionBar = (LinearLayout) noteView.findViewById(R.id.note_create_action_bar);
        actionBar.findViewById(R.id.fragment_actionBar_layout).setBackgroundColor(getResources().getColor(R.color.tact_violet));
        ((ImageView)actionBar.findViewById(R.id.fragment_menu_button)).setImageDrawable(getResources().getDrawable(R.drawable.back_arrow_white));
        ((ImageView)actionBar.findViewById(R.id.fragment_option_menu_btn)).setImageDrawable(getResources().getDrawable(R.drawable.settings_white));

        LinearLayout backButton  = (LinearLayout) actionBar.findViewById(R.id.fragment_actionBar_menu_button_layout);
        LinearLayout optionButton = (LinearLayout) actionBar.findViewById(R.id.fragment_actionBar_option_button_layout);
        LinearLayout relatedTo = (LinearLayout) noteView.findViewById(R.id.note_related_to_layout);
        ImageButton saveButton = (ImageButton) noteView.findViewById(R.id.save_note_floating_btn);
        ToggleButton salesforceNote = (ToggleButton) noteView.findViewById(R.id.note_toggle_button_salesforce);
        final TextView title_text_view = (TextView) noteView.findViewById(R.id.note_title_text_view);
        final TextView title_soft = (TextView) noteView.findViewById(R.id.note_title_title_soft);
        final EditText title_edit_text = (EditText) noteView.findViewById(R.id.note_title_edit_text);
        final TextView body_text_view = (TextView) noteView.findViewById(R.id.body_text_view);
        final TextView body_soft = (TextView) noteView.findViewById(R.id.body_title_soft);
        final EditText body_edit_text = (EditText) noteView.findViewById(R.id.body_edit_text);

        relatedSelectName = (TextView) noteView.findViewById(R.id.note_relate_to_name_text_view);
        relatedRequiredError = (TextView)noteView.findViewById(R.id.note_related_to_required_txt);
        relatedSoft = (TextView) noteView.findViewById(R.id.related_to_title_soft);

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

        actionBar.findViewById(R.id.fragment_actionBar_layout).getBackground().setAlpha(0);
        TextView titleActionBar = (TextView) actionBar.findViewById(R.id.fragment_actionBar_title_text);
        titleActionBar.setAlpha(0);
        titleActionBar.setTextColor(getResources().getColor(R.color.white));
        if (note != null){
            ((TextView) noteView.findViewById(R.id.note_title_textview)).setText(getResources().getString(R.string.edit_note));
            displayNote(noteView, note);
        }
        else {
            ((TextView) noteView.findViewById(R.id.note_title_textview)).setText(getResources().getString(R.string.create_note));
            noteView.findViewById(R.id.note_contact_textview).setVisibility(View.GONE);
            noteView.findViewById(R.id.task_subject_textview).setVisibility(View.GONE);
        }

        relatedTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), LogContactLazyLoadListActivity.class);
                i.putExtra("person", true);
                if (what != null) {
                    if (what instanceof Contact) {
                        i.putExtra("object", (Contact)what);
                    }
                }
                startActivityForResult(i, TactConst.CONTACT_PICK_REQUEST_WHAT);
            }
        });
        salesforceNote.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                salesforceNoteSync = isChecked;
            }
        });
        salesforceNote.setChecked(salesforceNoteSync);

        title_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title_text_view.setVisibility(View.GONE);
                title_soft.setVisibility(View.VISIBLE);
                title_edit_text.setVisibility(View.VISIBLE);
                title_edit_text.requestFocus();
                imm.showSoftInput(title_edit_text, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        title_edit_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && title_edit_text.getText().toString().isEmpty()) {
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    title_soft.setVisibility(View.INVISIBLE);
                    title_edit_text.setVisibility(View.GONE);
                    title_text_view.setVisibility(View.VISIBLE);
                }
            }
        });

        body_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                body_text_view.setVisibility(View.GONE);
                body_soft.setVisibility(View.VISIBLE);
                body_edit_text.setVisibility(View.VISIBLE);
                body_edit_text.requestFocus();
                imm.showSoftInput(body_edit_text, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        body_edit_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && body_edit_text.getText().toString().isEmpty()) {
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    body_soft.setVisibility(View.INVISIBLE);
                    body_edit_text.setVisibility(View.GONE);
                    body_text_view.setVisibility(View.VISIBLE);
                }
            }
        });

        if (what != null && what instanceof Contact) {
            relatedSelectName.setText(((Contact) what).getDisplayName());
            relatedSelectName.setTextColor(getResources().getColor(R.color.dark_grey));
            relatedSoft.setVisibility(View.VISIBLE);
        } else {
            relatedSelectName.setText(getActivity().getString(R.string.related_to));
            relatedSelectName.setTextColor(getResources().getColor(R.color.mid_grey));
            relatedSoft.setVisibility(View.INVISIBLE);
        }

        saveButton.setOnClickListener(saveNote(noteView));

        return noteView;
    }

    private void displayNote(View noteView, FeedItemNote note){
        //Set Contact Name
        if (note.getParentIdTact().getType().equals("Account") || note.getParentIdTact().getType().equals("Contact")) {
            Contact contact = DatabaseManager.getContactByRecordId(note.getParentIdTact().getIdentifier());
            if (contact != null) {
                what = contact;
                ((TextView)noteView.findViewById(R.id.note_contact_textview)).setText(contact.getDisplayName() != null ? contact.getDisplayName() : "");
            }
            else {
                noteView.findViewById(R.id.note_contact_textview).setVisibility(View.GONE);
            }
        }

        //Set Note Subject
        ((TextView)noteView.findViewById(R.id.task_subject_textview)).setText(note.getSubject() != null ? note.getSubject() : "");

        //Set the Title
        if (note.getSubject() != null && !note.getSubject().isEmpty()){
            EditText title_edit_text = (EditText) noteView.findViewById(R.id.note_title_edit_text);
            title_edit_text.setVisibility(View.VISIBLE);
            noteView.findViewById(R.id.note_title_text_view).setVisibility(View.GONE);
            noteView.findViewById(R.id.note_title_title_soft).setVisibility(View.VISIBLE);
            title_edit_text.setText(note.getSubject());
        }

        //Set the Body
        if (note.getDescription() != null && !note.getDescription().isEmpty()){
            EditText body_edit_text = (EditText) noteView.findViewById(R.id.body_edit_text);
            body_edit_text.setVisibility(View.VISIBLE);
            noteView.findViewById(R.id.body_text_view).setVisibility(View.GONE);
            noteView.findViewById(R.id.body_title_soft).setVisibility(View.VISIBLE);
            body_edit_text.setText(note.getDescription());
        }
    }

    private View.OnClickListener saveNote(final View parentView){
        return new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (what == null) {
                    relatedRequiredError.setVisibility(View.VISIBLE);
                }
                else {
                    //TODO: create or update the note - persist
                    Boolean salesforceNote = salesforceNoteSync;
                    String title = ((EditText) parentView.findViewById(R.id.note_title_edit_text)).getText().toString();
                    String body = ((EditText) parentView.findViewById(R.id.body_edit_text)).getText().toString();
                    Log.w("Save NOTE", title + " - " + body + " - " + salesforceNote.toString());
//                    if (note != null){
//                        //We are making an update of the note
//                    }
//                    else {
//                        //We are creating a new note
//                    }
                }
            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == TactConst.CONTACT_PICK_RESULT_OK) {
            if (requestCode == TactConst.CONTACT_PICK_REQUEST_WHAT) {
                what = data.getSerializableExtra("object");
                if (what != null) {
                    relatedSelectName.setText(((Contact) what).getDisplayName());
                    relatedSelectName.setTextColor(getResources().getColor(R.color.dark_grey));
                    relatedSoft.setVisibility(View.VISIBLE);
                } else {
                    relatedSelectName.setText(getActivity().getString(R.string.related_to));
                    relatedSelectName.setTextColor(getResources().getColor(R.color.mid_grey));
                    relatedSoft.setVisibility(View.INVISIBLE);
                }
            }
        }
        if (what != null) {
            relatedRequiredError.setVisibility(View.INVISIBLE);
        }
    }

}
