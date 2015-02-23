package com.tactile.tact.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStripPlus;
import com.tactile.tact.R;
import com.tactile.tact.consts.TactConst;
import com.tactile.tact.database.DatabaseManager;
import com.tactile.tact.database.entities.Contact;
import com.tactile.tact.services.network.RedirectURLGetter;
import com.tactile.tact.services.network.TactNetworking;
import com.tactile.tact.utils.IPredicate;
import com.tactile.tact.utils.Predicate;
import com.tactile.tact.utils.TactDialogHandler;
import com.tactile.tact.views.CustomViewPager;
import com.tactile.tact.views.ProfileIconView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by sebafonseca on 12/10/14.
 */
public class LogContactListActivity extends TactBaseActivity {

    private StickyListHeadersListView contact_list_all;
    private StickyListHeadersListView contact_list_recent;
    private LinearLayout actionBar;
    private StickyListHeadersListView contact_list;
    private ListView alphabetic_listview;
    private EditText searchEditText;
    private LinearLayout searchIcon;
    private LinearLayout headerLayout;
    private LinearLayout actionBar_menu_button_layout;
    private ImageView actionBarMenuImage;
    private LinearLayout searchLinearLayout;
    private InputMethodManager imm;
    private CustomViewPager contactPager;
    private ImageView option_menu_btn;
    private ImageView cancel_search_image;
    private LinearLayout actionBar_option_button_layout;
    private RelativeLayout contacts_action_bar_pager;
    private LinearLayout backButtonLayout;
    private ArrayList<Contact> contacts_db_list_all;
    private AsyncLoadContacts asyncLoadContacts;
    private PagerSlidingTabStripPlus tabs;
    private WordListAdapter alphabetAdapter;

    private TactDialogHandler dialogHandler;

    private Boolean person = true;
    private Object selected_object;

    private int page = 0;
    private String filterText = "";

    private static enum ContactsState {
        ALL,
        RECENT,
        STARRED
    }

    private ContactsState state;

    private ArrayList<RedirectURLGetter> asyncLoadContactsImages;

    @Override
    protected void onCreate(Bundle bundle) {

        super.onCreate(bundle);
        Intent intent = getIntent();
        if (intent != null){
            person = intent.getBooleanExtra("person", true);
            selected_object = intent.getSerializableExtra("object");
            page = intent.getIntExtra("page", 0);
        }

        dialogHandler = new TactDialogHandler(this);

        setContentView(R.layout.log_contact_finder);
        actionBar = (LinearLayout) findViewById(R.id.log_contact_finder_action_bar);
        actionBar.setBackgroundColor(getResources().getColor(R.color.action_bar_grey));
        backButtonLayout = (LinearLayout) actionBar.findViewById(R.id.actionBar_menu_button_layout);
        ((ImageView) actionBar.findViewById(R.id.menu_button)).setImageDrawable(getResources().getDrawable(R.drawable.back_arrow_orange));
        ((ImageView) actionBar.findViewById(R.id.actionBar_pin_logo)).setVisibility(View.GONE);
        if (person) {
            ((TextView) actionBar.findViewById(R.id.actionBar_title_text)).setText(getResources().getString(R.string.log_select_contacts));
        }
        else {
            ((TextView) actionBar.findViewById(R.id.actionBar_title_text)).setText(getResources().getString(R.string.log_select_companies));
        }

        state = ContactsState.ALL;



        actionBar_option_button_layout = (LinearLayout) actionBar.findViewById(R.id.actionBar_option_button_layout);
        contacts_action_bar_pager = (RelativeLayout)findViewById(R.id.tabs_general_pager);
//        starredBtn = (ToggleButton) contacts_action_bar_pager.findViewById(R.id.pager_starred_btn);
        option_menu_btn = (ImageView) actionBar.findViewById(R.id.option_menu_btn);
        option_menu_btn.setVisibility(View.GONE);
        cancel_search_image = (ImageView) actionBar.findViewById(R.id.close_search_icon);
        actionBarMenuImage = (ImageView) actionBar.findViewById(R.id.menu_button);
        actionBar_menu_button_layout = (LinearLayout) actionBar.findViewById(R.id.actionBar_menu_button_layout);
        headerLayout = (LinearLayout) actionBar.findViewById(R.id.header_linear_layout);

        state = ContactsState.ALL;

        searchIcon = (LinearLayout)actionBar.findViewById(R.id.action_bar_secondary_button_1);
        searchEditText = (EditText) actionBar.findViewById(R.id.search_edit_text);
        searchLinearLayout = (LinearLayout) actionBar.findViewById(R.id.search_linear_layout);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        contactPager = (CustomViewPager) findViewById(R.id.contacts_list_pager_container);


        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditText.setVisibility(View.VISIBLE);
                searchEditText.setHint(getResources().getString(R.string.contact_search_hint));
                tabs.setVisibility(View.VISIBLE);
                contactPager.setPagingEnabled(true);
                searchEditText.setText("");
                filterContacts("");
                headerLayout.setVisibility(View.GONE);
                searchIcon.setVisibility(View.GONE);
                cancel_search_image.setVisibility(View.VISIBLE);
                searchLinearLayout.setVisibility(View.VISIBLE);
                actionBarMenuImage.setImageDrawable(getResources().getDrawable(R.drawable.search_orange_icon));
                searchEditText.requestFocus();
                actionBar_menu_button_layout.setClickable(false);
                imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);

                actionBar_option_button_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clearSearch();

                    }
                });

            }
        });

        searchEditText.setHint(getResources().getString(R.string.contact_search_hint));
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tabs.setVisibility(View.GONE);
                contactPager.setPagingEnabled(false);
                filterContacts(s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        tabs = (PagerSlidingTabStripPlus) findViewById(R.id.tabs);

        backButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        asyncLoadContacts = new AsyncLoadContacts(this, getLayoutInflater());
        asyncLoadContacts.execute();
    }

    private void clearSearch() {
        if (contacts_db_list_all.size() > 0) {
            filterContacts("");
            tabs.setVisibility(View.VISIBLE);
            contactPager.setPagingEnabled(true);
        }
        headerLayout.setVisibility(View.VISIBLE);
        searchIcon.setVisibility(View.VISIBLE);
        actionBarMenuImage.setImageDrawable(getResources().getDrawable(R.drawable.back_arrow_orange));
        cancel_search_image.setVisibility(View.GONE);
        searchLinearLayout.setVisibility(View.GONE);
        searchEditText.setVisibility(View.GONE);
        actionBar_menu_button_layout.setClickable(true);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
        }
    }

    public void switchPage(int page) {
        ContactsState prevState = state;
        switch (page) {
            case 0:
                state = ContactsState.ALL;
                break;
            case 1:
                state = ContactsState.RECENT;
                break;
            case 2:
                state = ContactsState.STARRED;
                break;
        }
        filterContacts(filterText);
        contactPager.setCurrentItem(page);
    }

    public class WordListAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private Context ctx;
        private String [] wordList = getResources().getStringArray(R.array.word_list);

        private View lastSelected;

        public WordListAdapter(Context context) {
            ctx = context;
            inflater = LayoutInflater.from(ctx);
        }

        @Override
        public int getCount() {
            return wordList.length;
        }

        @Override
        public String getItem(int position) {
            return wordList[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.word_layout, parent, false);
            }

            TextView wordTextView = (TextView) convertView.findViewById(R.id.word_text_view_layout);
            wordTextView.setText(wordList[position]);

            if (contactPager != null && contactPager.getCurrentItem() == 0) {
                if (((StickyAdapter)contact_list_all.getAdapter()).getPositionOfFirstElement(wordList[position]) >= 0) {
                    wordTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (lastSelected != null) {
                                lastSelected.setSelected(false);
                            }
                            v.setSelected(true);
                            lastSelected = v;
                            clearSearch();
                            contact_list_all.setSelection(((StickyAdapter)contact_list_all.getAdapter()).getPositionOfFirstElement(wordList[position]));
                        }
                    });
                } else {
                    wordTextView.setClickable(false);
                    wordTextView.setTextColor(getResources().getColor(R.color.btn_white_gray));
                }
            } else if (contactPager != null && contactPager.getCurrentItem() == 1) {
                if (((StickyAdapter)contact_list_recent.getAdapter()).getPositionOfFirstElement(wordList[position]) >= 0) {
                    wordTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (lastSelected != null) {
                                lastSelected.setSelected(false);
                            }
                            v.setSelected(true);
                            lastSelected = v;
                            clearSearch();
                            contact_list_recent.setSelection(((StickyAdapter)contact_list_recent.getAdapter()).getPositionOfFirstElement(wordList[position]));
                        }
                    });
                } else {
                    wordTextView.setClickable(false);
                    wordTextView.setTextColor(getResources().getColor(R.color.btn_white_gray));
                }
            }

            return convertView;
        }

    }

    public class StickyAdapter extends BaseAdapter implements StickyListHeadersAdapter {

        private Context context = getApplicationContext();
        private LayoutInflater inflater;
        private ArrayList<Contact> contacts_array;
        private Map<String, Integer> headersMap;

        public StickyAdapter(ArrayList<Contact> contacts) {
            inflater = LayoutInflater.from(context);
            contacts_array = contacts;
            headersMap = new HashMap<>();
            fillHeadersMap();
        }

        public void refresh(ArrayList<Contact> contacts){
            if (contacts_array.size() != contacts.size()) {
                contacts_array = contacts;
                this.notifyDataSetChanged();
                if (alphabetAdapter != null) {
                    alphabetAdapter.notifyDataSetChanged();
                }
            }
        }

        private void fillHeadersMap() {
            String lastLetter = null;
            for (Contact contact : contacts_array) {
                char letter = contact.getDisplayName().charAt(0);
                if (Character.isLetter(letter)) {
                    if (lastLetter == null || !lastLetter.equals(String.valueOf(letter).toUpperCase())) {
                        headersMap.put(String.valueOf(letter).toUpperCase(), contacts_array.indexOf(contact));
                        lastLetter = String.valueOf(letter).toUpperCase();
                    }
                }
            }
        }

        public int getPositionOfFirstElement(String startingLetter) {
            int elementPosition = -1;
            try {
                elementPosition = headersMap.get(startingLetter.toUpperCase());
            } catch (Exception e) {
                //do nothing thgere is no letter in that alphabet
            }
            return elementPosition;
        }

        public int getPositionByObjectId(String objectId) {
            int elementPosition = -1;
            if (objectId != null) {
                for (Contact contact : contacts_array) {
                    if (objectId.equals(contact.getRecordID())) {
                        elementPosition = contacts_array.indexOf(contact);
                        break;
                    }
                }
            }
            return elementPosition;
        }

        @Override
        public int getCount() {
            return contacts_array.size();
        }

        @Override
        public Contact getItem(int position) {
            return contacts_array.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.log_contact_selection, parent, false);
            }

            TextView text = (TextView) convertView.findViewById(R.id.log_contact_name_text_view);
            TextView company = (TextView) convertView.findViewById(R.id.log_company_name_text_view);
            ProfileIconView image = (ProfileIconView) convertView.findViewById(R.id.log_contact_details_icon_small);

            ImageView tick = (ImageView) convertView.findViewById(R.id.log_selected_contact_tick);
            LinearLayout wraper = (LinearLayout) convertView.findViewById(R.id.log_contact_field);
            if (selected_object != null && contacts_array.get(position).getRecordID().equals(((Contact)selected_object).getRecordID())){
                tick.setVisibility(View.VISIBLE);
                wraper.setSelected(true);
            }
            else {
                tick.setVisibility(View.GONE);
                wraper.setSelected(false);
            }

            Contact contact = contacts_array.get(position);
            text.setText(contact.getDisplayName());
            if (contact.getCompanyName() != null && !contact.getCompanyName().isEmpty()) {
                company.setVisibility(View.VISIBLE);
                company.setText(contact.getCompanyName());
            }
            else {
                company.setVisibility(View.GONE);
            }
            //image.loadImage(contacts_array.get(position).getPhotoUrl(), contacts_array.get(position).getInitials(), contacts_array.get(position).isCompany());

            if (contact.getRealPhotoUrl() != null){
                image.loadImage(contact.getRealPhotoUrl(), contact.getInitials(), contact.isCompany());
            }
            else {
                image.loadImage(null, contact.getInitials(), contact.isCompany());
                if (contact.getPhotoUrl() != null && !contact.getPhotoUrl().isEmpty() && !contact.getPhotoUrl().startsWith("http") &&
                        !isURLGetterAsyncTaskRunning(contact.getRecordID())){
                    RedirectURLGetter asyncTask = new RedirectURLGetter(LogContactListActivity.this, contact.getRecordID());
                    asyncTask.execute(TactNetworking.getURL() + "/photo_store?photo_url=" + contact.getPhotoUrl());
                    asyncLoadContactsImages.add(asyncTask);
                }
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent returnIntent = new Intent();
                    if (selected_object != null && contacts_array.get(position).getRecordID().equals(((Contact)selected_object).getRecordID())){
                        returnIntent.putExtra("object", (Serializable)null);
                    }
                    else {
                        returnIntent.putExtra("object", contacts_array.get(position));
                    }
                    setResult(TactConst.CONTACT_PICK_RESULT_OK,returnIntent);
                    finish();
                }
            });

            return convertView;
        }

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup parent) {
            HeaderViewHolder holder;
            if (convertView == null) {
                holder = new HeaderViewHolder();
                convertView = inflater.inflate(R.layout.contacts_group_view, parent, false);
                holder.text = (TextView) convertView.findViewById(R.id.contact_group_heading);
                convertView.setTag(holder);
            } else {
                holder = (HeaderViewHolder) convertView.getTag();
            }
            //set header text as first char in name
            holder.text.setText("" + contacts_array.get(position).getDisplayName().toUpperCase().subSequence(0, 1).charAt(0));

            return convertView;
        }

        @Override
        public long getHeaderId(int position) {
            return contacts_array.get(position).getDisplayName().toUpperCase().subSequence(0, 1).charAt(0);
        }

        class HeaderViewHolder {
            TextView text;
        }

    }

    /**
     * Set PageAdapter to a current view
     * @param inflater
     * @return PagerAdapter
     */
    private PagerAdapter getMainPageAdapter(final LayoutInflater inflater){
        return new PagerAdapter(){

            @Override
            public Object instantiateItem(ViewGroup collection, int position) {
                return getContactListView(getApplicationContext(), inflater, collection, position);
            }

            @Override
            public int getCount(){return 2;}

            @Override
            public boolean isViewFromObject(View view, Object object) { return view == object; }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public int getItemPosition(Object object) {
                return POSITION_NONE;
            }
        };
    }

    @Override
    public void onBackPressed(){
        setResult(TactConst.CONTACT_PICK_RESULT_CANCEL, null);
        finish();
    }

    /**
     * Set item for a single day
     * @param context Activity context
     * @param inflater inflater service
     * @param collection colection from PageAdapter
     * @return object
     */
    private Object getContactListView(Context context, LayoutInflater inflater, ViewGroup collection, int currentPosition){

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);

        //We need to create a ScrollView just for days
        LinearLayout linearContent = new LinearLayout(context);
        linearContent.setLayoutParams(layoutParams);

        //Now, inflate the agenda day item layout
        View myView = inflater.inflate(R.layout.combo_contacts_layout, null);
        if(myView != null){
            if (currentPosition == 0) {
                contact_list_all = (StickyListHeadersListView) myView.findViewById(R.id.contact_sticky_list_view);
                contact_list_all.setDivider(null);
                contact_list_all.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                        View v = getCurrentFocus();
                        if (v != null) {
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm.isActive()) {
                                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            }
                        }
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                    }
                });
            } else if (currentPosition == 1) {
                contact_list_recent = (StickyListHeadersListView) myView.findViewById(R.id.contact_sticky_list_view);
                contact_list_recent.setDivider(null);
                contact_list_recent.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                        View v = getCurrentFocus();
                        if (v != null) {
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm.isActive()) {
                                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            }
                        }
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                    }
                });
            }

            alphabetic_listview = (ListView) myView.findViewById(R.id.alphabetic_listview);
//            alphabetic_listview.setVisibility(View.GONE);


            ArrayList<Contact> contacts_db_list = new ArrayList<Contact>();
            switch (currentPosition) {
                case 0:
                    contacts_db_list = filterContactList(filterText);
                    break;
                case 1:
                    contacts_db_list = getRecentContacts(filterText);
                    break;
                case 2:
                    contacts_db_list = getStarredContacts(filterText);
                    break;
            }
            if (contacts_db_list != null) {
                if (currentPosition == 0) {
                    contact_list_all.setAdapter(new StickyAdapter(contacts_db_list));
                    final int selectedElementPosition = ((StickyAdapter)contact_list_all.getAdapter()).getPositionByObjectId(((Contact)selected_object).getRecordID());
                    if (selectedElementPosition > 0 && currentPosition == page) {
                        contact_list_all.post(new Runnable() {
                            @Override
                            public void run() {
                                contact_list_all.setSelection(selectedElementPosition);
                            }
                        });
                    }
                } else if (currentPosition == 1) {
                    contact_list_recent.setAdapter(new StickyAdapter(contacts_db_list));
                    final int selectedElementPosition = ((StickyAdapter)contact_list_recent.getAdapter()).getPositionByObjectId(((Contact)selected_object).getRecordID());
                    if (selectedElementPosition > 0 && currentPosition == page) {
                        contact_list_recent.post(new Runnable() {
                            @Override
                            public void run() {
                                contact_list_recent.setSelection(selectedElementPosition);
                            }
                        });
                    }
                }
                alphabetAdapter = new WordListAdapter(this);
                alphabetic_listview.setAdapter(alphabetAdapter);

            } else {
                alphabetic_listview.setVisibility(View.GONE);
            }

            linearContent.addView(myView);
            collection.addView(linearContent);
        }

        return linearContent;
    }


    public void filterContacts(CharSequence filter){
        filterText = filter.toString();

        if (state == ContactsState.ALL && contact_list_all != null) {
            if (filter.length() > 0) {
                ((StickyAdapter)contact_list_all.getAdapter()).refresh(filterContactList(filter));
            } else {
                ((StickyAdapter)contact_list_all.getAdapter()).refresh(contacts_db_list_all);
            }
        } else if (state == ContactsState.RECENT && contact_list_recent != null) {
            ((StickyAdapter)contact_list_recent.getAdapter()).refresh(getRecentContacts(filter));

        }
//        else if (state == ContactsState.STARRED) {
//            adapter.refresh(getStarredContacts(filter));
//        }
    }

    public ArrayList<Contact> getStarredContacts(final CharSequence filter){
        ArrayList<Contact> filteredList = (ArrayList<Contact>) Predicate.filter(contacts_db_list_all, new IPredicate<Contact>() {
            public boolean apply(Contact sContact) {
                if (filter.length() > 0){
                    return sContact.getIsStarred().equals(1) && sContact.getDisplayName().toLowerCase().contains(filter.toString().toLowerCase());
                }
                else {
                    return sContact.getIsStarred().equals(1);
                }
            }
        });
        if (filteredList == null) {
            filteredList = new ArrayList<Contact>();
        }
        return filteredList;
    }

    public ArrayList<Contact> getRecentContacts(final CharSequence filter){
        ArrayList<Contact> filteredList = (ArrayList<Contact>) Predicate.filter(contacts_db_list_all, new IPredicate<Contact>() {
            public boolean apply(Contact sContact) {
                if (filter.length() > 0) {
                    return sContact.getVisitedAt() != null && sContact.getVisitedAt() != 0 && sContact.getDisplayName().toLowerCase().contains(filter.toString().toLowerCase());
                } else {
                    return sContact.getVisitedAt() != null && sContact.getVisitedAt() != 0;
                }
            }
        });
        if (filteredList == null) {
            filteredList = new ArrayList<Contact>();
        }
        return filteredList;
    }

    private ArrayList<Contact> filterContactList(final CharSequence filter) {
        ArrayList<Contact> filteredList = (ArrayList<Contact>) Predicate.filter(contacts_db_list_all, new IPredicate<Contact>() {
            public boolean apply(Contact sContact) {
                Predicate.predicateParams = sContact.getDisplayName();
                return sContact.getDisplayName().toLowerCase().contains(filter.toString().toLowerCase());
            }
        });
        if (filteredList == null) {
            filteredList = new ArrayList<Contact>();
        }
        return filteredList;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (asyncLoadContacts != null && (asyncLoadContacts.getStatus() == AsyncTask.Status.PENDING || asyncLoadContacts.getStatus() == AsyncTask.Status.RUNNING)) {
            asyncLoadContacts.setActivity(this);
        }
        if (asyncLoadContactsImages != null){
            for (RedirectURLGetter asyncTask: asyncLoadContactsImages){
                asyncTask.setActivity(this);
            }
        }
    }

    public class AsyncLoadContacts extends AsyncTask<Void, Void, Object> {
        // Maintain attached activity for states change propose
        private LogContactListActivity context;
        // Keep the response
        private Object _response;
        // Flag that keep async task completed status
        private boolean completed;

        private LayoutInflater inflater;

        // Constructor
        private AsyncLoadContacts(LogContactListActivity context, LayoutInflater inflater) {
            this.context = context;
            this.inflater = inflater;
        }

        // Pre execution actions
        @Override
        protected void onPreExecute() {
            contactPager.setVisibility(View.GONE);
            dialogHandler.showProgress(true);
        }

        protected Object doInBackground(Void... arg0) {
            try {
                if (person){
                    return DatabaseManager.getAllPersons();
                }
                else {
                    return DatabaseManager.getAllCompanies();
                }
            } catch (Exception e) {
                return null;
            }
        }

        // Post execution actions
        @Override
        protected void onPostExecute(Object response) {
            // Set task completed and notify the activity
            completed = true;
            _response = response;
            notifyActivityTaskCompleted();
        }

        // Notify activity of async task complete
        private void notifyActivityTaskCompleted() {
            if (null != context) {
                context.notifyFragmentTaskCompleted((ArrayList<Contact>) _response, inflater);
            }
        }

        // Sets the current activity to the async task
        public void setActivity(LogContactListActivity context) {
            this.context = context;
            if (completed) {
                notifyActivityTaskCompleted();
            }
        }
    }

    public class ContactPagerAdapter extends PagerAdapter {

        private final String[] TITLES = {"ALL", "RECENT"};

        LayoutInflater inflater;

        public ContactPagerAdapter(Context ctx) {
            inflater = LayoutInflater.from(ctx);
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            return getContactListView(getApplicationContext(), inflater, collection, position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    private void notifyFragmentTaskCompleted(ArrayList<Contact> contacts, LayoutInflater inflater) {
        asyncLoadContacts = null;
        contacts_db_list_all = contacts;

        ContactPagerAdapter contactPagerAdapter = new ContactPagerAdapter(this);
        contactPager.setAdapter(contactPagerAdapter);
        contactPager.setCurrentItem(0);

        ViewPager.OnPageChangeListener pageListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);
                    }
                }
            }

            @Override
            public void onPageSelected(int i) {
                switchPage(i);
                if (alphabetAdapter != null) {
                    alphabetAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        };
        contactPager.setOnPageChangeListener(pageListener);

        tabs.setViewPager(contactPager);
        tabs.setOnPageChangeListener(pageListener);

        contactPager.getAdapter().notifyDataSetChanged();

        if (page > 0) {
            contactPager.setCurrentItem(page);
        }

        contactPager.setVisibility(View.VISIBLE);
        dialogHandler.dismiss();

        System.gc();
    }

    public void notifyURLGetterTaskCompleted(String realPhotoUrl, String recordID){
        if (contacts_db_list_all != null && realPhotoUrl != null && realPhotoUrl.startsWith("http")){
            for (Contact contact: contacts_db_list_all){
                if (contact.getRecordID().equals(recordID)){
                    contact.setRealPhotoUrl(realPhotoUrl);
                    if (contact_list_all != null && ((StickyAdapter)contact_list_all.getAdapter()) != null && contactPager.getCurrentItem() == 0){
                        ((StickyAdapter)contact_list_all.getAdapter()).notifyDataSetChanged();
                    }
                    else if (contact_list_recent != null && ((StickyAdapter)contact_list_recent.getAdapter()) != null && contactPager.getCurrentItem() == 1){
                        ((StickyAdapter)contact_list_recent.getAdapter()).notifyDataSetChanged();
                    }
                    break;
                }
            }
        }
        if (asyncLoadContactsImages != null) {
            int asyncTaskToRemove = -1;
            for (RedirectURLGetter asyncTask : asyncLoadContactsImages) {
                asyncTaskToRemove++;
                if (asyncTask.getRecordID().equals(recordID)) {
                    break;
                }
            }
            if (asyncTaskToRemove >= 0 && asyncTaskToRemove <= asyncLoadContactsImages.size()) {
                asyncLoadContactsImages.remove(asyncTaskToRemove);
            }
            System.gc();
        }
    }

    public boolean isURLGetterAsyncTaskRunning(String recordID){
        if (asyncLoadContactsImages == null){
            asyncLoadContactsImages = new ArrayList<RedirectURLGetter>();
        }
        for(RedirectURLGetter asyncTask: asyncLoadContactsImages){
            if (asyncTask.getRecordID().equals(recordID)){
                return asyncTask.getStatus() == AsyncTask.Status.PENDING || asyncTask.getStatus() == AsyncTask.Status.RUNNING;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        asyncLoadContactsImages = null;
        System.gc();
    }
}