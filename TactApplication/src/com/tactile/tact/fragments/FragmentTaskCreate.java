package com.tactile.tact.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.tactile.tact.database.entities.Opportunity;
import com.tactile.tact.database.model.FeedItemTask;
import com.tactile.tact.services.events.EventGoBackHomeActivity;
import com.tactile.tact.services.events.EventHideActivityActionBar;
import com.tactile.tact.services.events.EventOptionButtonPressed;
import com.tactile.tact.utils.TactCustomSpinner;
import com.tactile.tact.utils.Utils;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import de.greenrobot.event.EventBus;

/**
 * Created by ismael on 1/19/15.
 */
public class FragmentTaskCreate extends FragmentTactBase {

    final Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
    private TextView contactSelectName;
    private TextView relatedSelectName;
    private TextView contactRequiredError;
    private TextView relatedRequiredError;
    private TextView contactSoft;
    private TextView relatedSoft;

    private Object who;
    private Object what;
    private Boolean salesforceTaskSync = false;
    private FeedItemTask task = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View taskView = inflater.inflate(R.layout.task_layout, container, false);
        if (getArguments() != null){
            task = (FeedItemTask) getArguments().getSerializable("task");
        }

        EventBus.getDefault().post(new EventHideActivityActionBar());
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        LinearLayout actionBar = (LinearLayout) taskView.findViewById(R.id.task_create_action_bar);
        actionBar.findViewById(R.id.fragment_actionBar_layout).setBackgroundColor(getResources().getColor(R.color.action_bar_orange));
        ((ImageView)actionBar.findViewById(R.id.fragment_menu_button)).setImageDrawable(getResources().getDrawable(R.drawable.back_arrow_white));
        ((ImageView)actionBar.findViewById(R.id.fragment_option_menu_btn)).setImageDrawable(getResources().getDrawable(R.drawable.settings_white));

        LinearLayout backButton  = (LinearLayout) actionBar.findViewById(R.id.fragment_actionBar_menu_button_layout);
        LinearLayout optionButton = (LinearLayout) actionBar.findViewById(R.id.fragment_actionBar_option_button_layout);
        ImageButton saveButton = (ImageButton) taskView.findViewById(R.id.save_task_floating_btn);

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
        if (task != null){
            titleActionBar.setText(getResources().getString(R.string.edit_task));
            displayTask(taskView, task);
        }
        else {
            titleActionBar.setText(getResources().getString(R.string.create_task));
            taskView.findViewById(R.id.task_to_field).setVisibility(View.GONE);
            taskView.findViewById(R.id.task_subject_textview).setVisibility(View.GONE);
        }

        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));

        final TextView dateTextView = (TextView) taskView.findViewById(R.id.task_date_textview);
        final TextView dueDateSoft = (TextView) taskView.findViewById(R.id.due_date_title_soft);
        final TactCustomSpinner due_date_picker = (TactCustomSpinner) taskView.findViewById(R.id.task_due_date_spinner);
        final TextView subject_text_view = (TextView) taskView.findViewById(R.id.subject_text_view);
        final TextView subject_title_soft = (TextView) taskView.findViewById(R.id.subject_title_soft);
        final EditText subject_edit_text = (EditText) taskView.findViewById(R.id.subject_edit_text);
        LinearLayout contactSelectField = (LinearLayout) taskView.findViewById(R.id.task_contact_layout);
        LinearLayout relatedSelectField = (LinearLayout) taskView.findViewById(R.id.task_related_to_layout);
        contactSelectName = (TextView) taskView.findViewById(R.id.task_contact_name_text_view);
        relatedSelectName = (TextView) taskView.findViewById(R.id.task_relate_to_name_text_view);
        contactRequiredError = (TextView)taskView.findViewById(R.id.task_contact_required_txt);
        relatedRequiredError = (TextView)taskView.findViewById(R.id.task_related_to_required_txt);
        contactSoft = (TextView) taskView.findViewById(R.id.contact_title_soft);
        relatedSoft = (TextView) taskView.findViewById(R.id.related_to_title_soft);
        ToggleButton salesforceTask = (ToggleButton) taskView.findViewById(R.id.tact_toggle_button_salesforce);
        final EditText description_edit_text = (EditText) taskView.findViewById(R.id.description_edit_text);
        final TextView descriptionSoft = (TextView) taskView.findViewById(R.id.description_title_soft);
        final TextView description_text_view = (TextView) taskView.findViewById(R.id.description_text_view);
        LinearLayout description_layout = (LinearLayout) taskView.findViewById(R.id.task_description_layout);

        due_date_picker.getPickerOkBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.set(Calendar.YEAR, due_date_picker.getDatePicker().getYear());
                calendar.set(Calendar.MONTH, due_date_picker.getDatePicker().getMonth());
                calendar.set(Calendar.DAY_OF_MONTH, due_date_picker.getDatePicker().getDayOfMonth());
                dateTextView.setText(Utils.getDateStringFormated(calendar, "MMM dd, yyyy"));
                dateTextView.setTextColor(getResources().getColor(R.color.dark_grey));
                dueDateSoft.setVisibility(View.VISIBLE);
                due_date_picker.dialogDismiss();
            }
        });
        due_date_picker.getDatePicker().init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), null);
        dateTextView.setText(Utils.getDateStringFormated(calendar, "MMM dd, yyyy"));
        dateTextView.setTextColor(getResources().getColor(R.color.dark_grey));
        dueDateSoft.setVisibility(View.VISIBLE);

        due_date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                due_date_picker.clickEvent();
            }
        });

        subject_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subject_text_view.setVisibility(View.GONE);
                subject_title_soft.setVisibility(View.VISIBLE);
                subject_edit_text.setVisibility(View.VISIBLE);
                subject_edit_text.requestFocus();
                imm.showSoftInput(subject_edit_text, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        subject_edit_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && subject_edit_text.getText().toString().isEmpty()) {
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    subject_title_soft.setVisibility(View.INVISIBLE);
                    subject_edit_text.setVisibility(View.GONE);
                    subject_text_view.setVisibility(View.VISIBLE);
                }
            }
        });

        description_edit_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && description_edit_text.getText().toString().isEmpty()) {
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    descriptionSoft.setVisibility(View.INVISIBLE);
                    description_edit_text.setVisibility(View.GONE);
                    description_text_view.setVisibility(View.VISIBLE);
                }
            }
        });

        contactSelectField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), LogContactLazyLoadListActivity.class);
                i.putExtra("person", true);
                if (who != null) {
                    if (who instanceof Contact) {
                        i.putExtra("object", (Contact)who);
                    }
                }
                startActivityForResult(i, TactConst.CONTACT_PICK_REQUEST_WHO);
            }
        });

        relatedSelectField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), LogContactLazyLoadListActivity.class);
                i.putExtra("person", false);
                if (what != null) {
                    if (what instanceof Contact) {
                        i.putExtra("object", (Contact)what);
                    }
                }
                startActivityForResult(i, TactConst.CONTACT_PICK_REQUEST_WHAT);
            }
        });

        salesforceTask.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                salesforceTaskSync = isChecked;
            }
        });
        salesforceTask.setChecked(salesforceTaskSync);

        description_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                description_text_view.setVisibility(View.GONE);
                descriptionSoft.setVisibility(View.VISIBLE);
                description_edit_text.setVisibility(View.VISIBLE);
                description_edit_text.requestFocus();
                imm.showSoftInput(description_edit_text, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        if (who != null && who instanceof Contact) {
            contactSelectName.setText(((Contact) who).getDisplayName());
            contactSelectName.setTextColor(getResources().getColor(R.color.dark_grey));
            contactSoft.setVisibility(View.VISIBLE);
        } else {
            contactSelectName.setText(getActivity().getString(R.string.contact));
            contactSelectName.setTextColor(getResources().getColor(R.color.mid_grey));
            contactSoft.setVisibility(View.INVISIBLE);
        }
        if (what != null && what instanceof Contact) {
            relatedSelectName.setText(((Contact) what).getDisplayName());
            relatedSelectName.setTextColor(getResources().getColor(R.color.dark_grey));
            relatedSoft.setVisibility(View.VISIBLE);
        } else if (what != null && what instanceof Opportunity) {

        } else {
            relatedSelectName.setText(getActivity().getString(R.string.related_to));
            relatedSelectName.setTextColor(getResources().getColor(R.color.mid_grey));
            relatedSoft.setVisibility(View.INVISIBLE);
        }

        saveButton.setOnClickListener(saveTask(taskView));

        return taskView;
    }

    private View.OnClickListener saveTask(final View parentView){
        return new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (who == null && what == null) {
                    //At least one of the relations must exist
                    contactRequiredError.setVisibility(View.VISIBLE);
                    relatedRequiredError.setVisibility(View.VISIBLE);
                }
                else {
                    //TODO: create or update the task - persist
                    Boolean salesforceTask = salesforceTaskSync;
                    String subject = ((EditText) parentView.findViewById(R.id.subject_edit_text)).getText().toString();
                    Long dueDate = calendar.getTimeInMillis();
                    String description = ((EditText) parentView.findViewById(R.id.description_edit_text)).getText().toString();

                    if (task != null){
                        //We are making an update of the task
                    }
                    else {
                        //We are creating a new task
                    }
                }
            }
        };
    }

    private void displayTask(View view, FeedItemTask task){
        view.findViewById(R.id.task_to_field).setVisibility(View.GONE);
        TextView title = (TextView) view.findViewById(R.id.task_title_textview);
        EditText description = (EditText) view.findViewById(R.id.description_edit_text);
        EditText subject = (EditText) view.findViewById(R.id.subject_edit_text);

        title.setText(getResources().getString(R.string.edit_task));

        //Set the contact
        if (task.getContactRelated() != null && task.getContactRelated().getIdentifier() != null) {
            Contact contact = DatabaseManager.getContactByRecordId(task.getContactRelated().getIdentifier());
            if (contact != null) {
                who = contact;
                //Set Note Contact
                view.findViewById(R.id.task_to_field).setVisibility(View.VISIBLE);
                ((TextView)view.findViewById(R.id.task_contact_textview)).setText(contact.getDisplayName() != null ? contact.getDisplayName() : "");
            }
        }

        //Set Note Subject
        view.findViewById(R.id.task_subject_textview).setVisibility(View.VISIBLE);
        ((TextView)view.findViewById(R.id.task_subject_textview)).setText(task.getSubject() != null ? task.getSubject() : "");

        //Set the company
        if (task.getCompanyRelated() != null && task.getCompanyRelated().getIdentifier() != null) {
            Contact company = DatabaseManager.getContactByRecordId(task.getCompanyRelated().getIdentifier());
            if (company != null) {
                what = company;
            }
        }

        //Set the description
        if (task.getDescription() != null && !task.getDescription().isEmpty()) {
            description.setText(task.getDescription());
            view.findViewById(R.id.description_text_view).setVisibility(View.GONE);
            view.findViewById(R.id.description_title_soft).setVisibility(View.VISIBLE);
            description.setVisibility(View.VISIBLE);
        }
        //Set the subject
        if (task.getSubject() != null && !task.getSubject().isEmpty()) {
            subject.setText(task.getSubject());
            view.findViewById(R.id.subject_text_view).setVisibility(View.GONE);
            view.findViewById(R.id.subject_title_soft).setVisibility(View.VISIBLE);
            subject.setVisibility(View.VISIBLE);
        }

        //Set the due date
        if (task.getDueAt() != null && task.getDueAt() > 0){
            calendar.setTimeInMillis(task.getDueAt());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == TactConst.CONTACT_PICK_RESULT_OK) {
            if (requestCode == TactConst.CONTACT_PICK_REQUEST_WHO) {
                who = data.getSerializableExtra("object");
                if (who != null) {
                    contactSelectName.setText(((Contact) who).getDisplayName());
                    contactSelectName.setTextColor(getResources().getColor(R.color.dark_grey));
                    contactSoft.setVisibility(View.VISIBLE);
                } else {
                    contactSelectName.setText(getActivity().getString(R.string.contact));
                    contactSelectName.setTextColor(getResources().getColor(R.color.mid_grey));
                    contactSoft.setVisibility(View.INVISIBLE);
                }
            } else if (requestCode == TactConst.CONTACT_PICK_REQUEST_WHAT) {
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
        if (who != null || what != null) {
            //At least one of the relations must exist
            contactRequiredError.setVisibility(View.INVISIBLE);
            relatedRequiredError.setVisibility(View.INVISIBLE);
        }
    }
}
