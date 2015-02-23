package com.tactile.tact.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tactile.tact.R;
import com.tactile.tact.consts.TactConst;
import com.tactile.tact.database.DatabaseManager;
import com.tactile.tact.database.entities.Contact;
import com.tactile.tact.database.model.FeedItemEvent;
import com.tactile.tact.database.model.FeedItemLog;
import com.tactile.tact.database.model.FragmentMoveHandler;
import com.tactile.tact.services.events.EventGoBackHomeActivity;
import com.tactile.tact.services.events.EventMoveToFragment;
import com.tactile.tact.services.events.EventOptionButtonPressed;
import com.tactile.tact.utils.IPredicate;
import com.tactile.tact.utils.Predicate;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by ismael on 12/4/14.
 */
public class FragmentLogList extends FragmentTactBase {

    private ArrayList<Object> log_list;
    private ListView logListView;
    private LinearLayout actionBar;

    private FeedItemEvent event;
    private int position = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();

        if (bundle != null) {
            event = ((FeedItemEvent) bundle.getSerializable("event"));
            position = (bundle.getInt("position"));
        }

        View fragmentListView = inflater.inflate(R.layout.log_list, container, false);
        actionBar = (LinearLayout) fragmentListView.findViewById(R.id.log_list_action_bar);
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

        logListView = (ListView)fragmentListView.findViewById(R.id.log_list_view);

        logListView.setAdapter(new LogListAdapter(event.getLogs()));

        if (position > 0) {
            logListView.setSelection(position);
        }

        return fragmentListView;
    }

    private FragmentMoveHandler getFragmentMoveHandler(int position) {
        FragmentMoveHandler fragmentMoveHandler = new FragmentMoveHandler();
        fragmentMoveHandler.setFragmentTag(TactConst.FRAGMENT_LOG_LIST_TAG);
        fragmentMoveHandler.setObject(event);
        fragmentMoveHandler.setPosition(position);
        return  fragmentMoveHandler;
    }

    public class LogListAdapter extends BaseAdapter {

        private ArrayList<FeedItemLog> logArray;
        private Context context;
        private LayoutInflater inflater;

        public LogListAdapter (ArrayList<FeedItemLog> logsArray) {
            this.logArray = logsArray;
            context = getActivity();
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return logArray.size();
        }

        @Override
        public Object getItem(int position) {
            return logArray.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.log_list_item, parent, false);
            }

            TextView log_list_item_title_text_view = (TextView) convertView.findViewById(R.id.log_list_item_title_text_view);
            TextView log_list_item_contact_text_view = (TextView) convertView.findViewById(R.id.log_list_item_contact_text_view);
            ImageView log_list_item_image = (ImageView) convertView.findViewById(R.id.log_list_item_image);

            //this is for Logs
            if (logArray.get(position) instanceof FeedItemLog) {
                log_list_item_title_text_view.setText(((FeedItemLog)logArray.get(position)).getSubject());
                if (((FeedItemLog)logArray.get(position)).getContact() != null && ((FeedItemLog)logArray.get(position)).getContact().getIdentifier() != null) {
                    Contact contact = DatabaseManager.getContactByRecordId(((FeedItemLog)logArray.get(position)).getContact().getIdentifier());
                    if (contact != null) {
                        log_list_item_contact_text_view.setText(contact.getDisplayName());
                    }
                } else if (((FeedItemLog)logArray.get(position)).getRelated() != null && ((FeedItemLog)logArray.get(position)).getRelated().getIdentifier() != null && ((FeedItemLog)logArray.get(position)).getRelated().getType().equals("Company")) {
                    Contact contact = DatabaseManager.getContactByRecordId(((FeedItemLog)logArray.get(position)).getRelated().getIdentifier());
                    if (contact != null) {
                        log_list_item_contact_text_view.setText(contact.getDisplayName());
                    }
                }

                log_list_item_image.setImageDrawable(getResources().getDrawable(R.drawable.logdetail_log));

                // TODO: delete this if. The padding change because the others icons are mor big. The padding will change
//                if (logArray.get(position).getImageId() != R.drawable.logdetail_log)
//                    log_list_item_image.setPadding(8, 8, 8, 8);

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setBackStack(getFragmentMoveHandler(position));
                        EventBus.getDefault().post(new EventMoveToFragment(TactConst.FRAGMENT_LOG_DETAIL_TAG, 0, 0, logArray.get(position), 0));
                    }
                });
            }

            return convertView;
        }
    }
}
