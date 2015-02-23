package com.tactile.tact.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tactile.tact.R;
import com.tactile.tact.consts.TactConst;
import com.tactile.tact.database.DatabaseManager;
import com.tactile.tact.database.entities.Contact;
import com.tactile.tact.database.model.FeedItemTask;
import com.tactile.tact.database.model.FragmentMoveHandler;
import com.tactile.tact.services.events.EventGoBackHomeActivity;
import com.tactile.tact.services.events.EventHideActivityActionBar;
import com.tactile.tact.services.events.EventMoveToFragment;
import com.tactile.tact.utils.Log;

import java.text.SimpleDateFormat;

import de.greenrobot.event.EventBus;

/**
 * Created by ismael on 1/19/15.
 */
public class FragmentTaskDetails extends FragmentTactBase {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        View taskDetailsView = inflater.inflate(R.layout.task_details, container, false);

        EventBus.getDefault().post(new EventHideActivityActionBar());
        LinearLayout actionBar = (LinearLayout) taskDetailsView.findViewById(R.id.task_create_action_bar);
        actionBar.findViewById(R.id.fragment_actionBar_layout).setBackgroundColor(getResources().getColor(R.color.action_bar_orange));
        ((ImageView)actionBar.findViewById(R.id.fragment_menu_button)).setImageDrawable(getResources().getDrawable(R.drawable.back_arrow_white));
        ((ImageView)actionBar.findViewById(R.id.fragment_option_menu_btn)).setImageDrawable(getResources().getDrawable(R.drawable.settings_white));
        actionBar.findViewById(R.id.fragment_actionBar_option_button_layout).setVisibility(View.GONE);
        actionBar.findViewById(R.id.fragment_actionBar_secondary_button_1).setVisibility(View.VISIBLE);
        actionBar.findViewById(R.id.fragment_actionBar_secondary_button_2).setVisibility(View.VISIBLE);
        ((ImageView)actionBar.findViewById(R.id.imageview_secondary_1)).setImageDrawable(getResources().getDrawable(R.drawable.trash_icon));
        ((ImageView)actionBar.findViewById(R.id.imageview_secondary_2)).setImageDrawable(getResources().getDrawable(R.drawable.edit_icon));

        LinearLayout backButton  = (LinearLayout) actionBar.findViewById(R.id.fragment_actionBar_menu_button_layout);
        LinearLayout optionButton = (LinearLayout) actionBar.findViewById(R.id.fragment_actionBar_option_button_layout);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EventGoBackHomeActivity());
            }
        });

        TextView titleActionBar = (TextView) actionBar.findViewById(R.id.fragment_actionBar_title_text);
        titleActionBar.setTextColor(getResources().getColor(R.color.white));
        titleActionBar.setText(getResources().getString(R.string.task_details));

        setTaskView(taskDetailsView, (FeedItemTask) getArguments().getSerializable("task"));

        return taskDetailsView;
    }

    private void setTaskView(View view, final FeedItemTask task){
        TextView subject = (TextView) view.findViewById(R.id.task_subject);
        TextView contactRelated = (TextView) view.findViewById(R.id.contact_name_text_view);
        TextView companyRelated = (TextView) view.findViewById(R.id.related_to_name_text_view);
        TextView description = (TextView) view.findViewById(R.id.task_description_text_view);
        TextView dueDate = (TextView) view.findViewById(R.id.due_text_view);
        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.task_checkbox);
        LinearLayout delete = (LinearLayout) view.findViewById(R.id.fragment_actionBar_secondary_button_1);
        LinearLayout edit = (LinearLayout) view.findViewById(R.id.fragment_actionBar_secondary_button_2);

        subject.setText(task.getSubject() != null ? task.getSubject() : "");

        if (task.getContactRelated() != null && task.getContactRelated().getIdentifier() != null) {
            Contact contact = DatabaseManager.getContactByRecordId(task.getContactRelated().getIdentifier());
            if (contact != null && contact.getDisplayName() != null){
                contactRelated.setText(contact.getDisplayName());
            }
            else {
                contactRelated.setText("");
            }
        }
        else {
            contactRelated.setText("");
        }

        if (task.getCompanyRelated() != null && task.getCompanyRelated().getIdentifier() != null) {
            Contact company = DatabaseManager.getContactByRecordId(task.getCompanyRelated().getIdentifier());
            if (company != null && company.getDisplayName() != null){
                companyRelated.setText(company.getDisplayName());
            }
            else {
                companyRelated.setText("");
            }
        }
        else {
            companyRelated.setText("");
        }

        description.setText(task.getDescription() != null ? task.getDescription() : "");

        dueDate.setText(task.getDueAt() != null ?
                (new SimpleDateFormat("EE, MMM dd, yyyy")).format(task.getDueAt()) : "");

        checkBox.setChecked(task.getCompleted() != null && task.getCompleted());
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task.setCompleted(checkBox.isChecked());
                task.updateOnDB();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackStack(getFragmentMoveHandler(task));
                EventBus.getDefault().post(new EventMoveToFragment(TactConst.FRAGMENT_TASK_CREATE_TAG, 0, 0, task, 0));
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("TASK", "delete: " + task.getSubject());
            }
        });
    }

    private FragmentMoveHandler getFragmentMoveHandler(FeedItemTask task) {
        FragmentMoveHandler fragmentMoveHandler = new FragmentMoveHandler();
        fragmentMoveHandler.setFragmentTag(TactConst.FRAGMENT_TASK_DETAILS_TAG);
        fragmentMoveHandler.setObject(task);
        return  fragmentMoveHandler;
    }
}
