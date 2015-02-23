package com.tactile.tact.fragments;

import android.content.Context;
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
import com.tactile.tact.database.model.FragmentMoveHandler;
import com.tactile.tact.services.events.EventDrawerAction;
import com.tactile.tact.services.events.EventFilterContacts;
import com.tactile.tact.services.events.EventHideActivityActionBar;
import com.tactile.tact.services.events.EventMoveToFragment;
import com.tactile.tact.services.events.EventOptionButtonPressed;
import com.tactile.tact.services.events.EventSetUpActionBarFromContacts;
import com.tactile.tact.services.events.EventSetupActionBarOngoing;
import com.tactile.tact.utils.IPredicate;
import com.tactile.tact.utils.Predicate;
import com.tactile.tact.utils.TactDialogHandler;
import com.tactile.tact.views.ProfileIconView;

import java.util.ArrayList;
import java.util.Collections;

import de.greenrobot.event.EventBus;

/**
 * Created by leyan on 1/5/15.
 */
public class FragmentContactsBatchLoad  extends FragmentTactBase {

    private LinearLayout slide_tabs_line;
    private ListView lsv_contacts;
    private ListView alphabetic_listview;
    private ViewPager contactPager;
    private RelativeLayout contacts_action_bar_pager;
//    private ArrayList<Conta   ct> contacts_db_list_all;
    private String filterText = "";
    private LinearLayout actionBar;
    private LinearLayout searchIcon;
    private EditText searchEditText;
    private ImageView option_menu_btn;
    private LinearLayout headerLayout;
    private ImageView cancel_search_image;
    private ImageView actionBarMenuImage;
    private LinearLayout searchLinearLayout;
    private LinearLayout actionBar_menu_button_layout;
    private LinearLayout actionBar_option_button_layout;
    private InputMethodManager imm;
    private LinearLayout tabsChild;
    private PagerSlidingTabStripPlus tabs;

    private TactDialogHandler dialogHandler;

    private ArrayList<Contact> contact_list;
    private String firsVisibleContact;
    private String lastVisibleContact;
    private String filter;

    private Contact backContact;
    private int page = 0;

    private AsyncLoadContacts asyncLoadContacts;

    private ContactsAdapter allAdapter = null;
    private ContactsAdapter recentAdapter = null;

    private int listScrollX = -1;

    private static enum ContactsState {
        ALL,
        RECENT,
        STARRED
    }

    private ContactsState state;

    private int totalContactsCount = 0;
    private String firstContactId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            backContact = (Contact)bundle.getSerializable("contact");
            page = bundle.getInt("page");
        }

        dialogHandler = new TactDialogHandler(getActivity());

        View contactView = inflater.inflate(R.layout.contact_fragment_view, container, false);

        EventBus.getDefault().post(new EventHideActivityActionBar());
        actionBar = (LinearLayout) contactView.findViewById(R.id.contacts_action_bar);
        actionBar.findViewById(R.id.actionBar_layout).setBackgroundColor(getResources().getColor(R.color.action_bar_grey));

        LinearLayout backButton  = (LinearLayout) actionBar.findViewById(R.id.actionBar_menu_button_layout);
        LinearLayout optionButton = (LinearLayout) actionBar.findViewById(R.id.actionBar_option_button_layout);
        TextView actionBarTitle = (TextView) actionBar.findViewById(R.id.actionBar_title_text);

        cancel_search_image = (ImageView) actionBar.findViewById(R.id.close_search_icon);
        option_menu_btn = (ImageView) actionBar.findViewById(R.id.option_menu_btn);
        actionBar_menu_button_layout = (LinearLayout) actionBar.findViewById(R.id.actionBar_menu_button_layout);
        searchLinearLayout = (LinearLayout) actionBar.findViewById(R.id.search_linear_layout);
        searchIcon = (LinearLayout) actionBar.findViewById(R.id.action_bar_secondary_button_1);
        searchEditText = (EditText) actionBar.findViewById(R.id.search_edit_text);
        actionBarMenuImage = (ImageView) actionBar.findViewById(R.id.menu_button);
        headerLayout = (LinearLayout) actionBar.findViewById(R.id.header_linear_layout);
        actionBar_option_button_layout = (LinearLayout) actionBar.findViewById(R.id.actionBar_option_button_layout);
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        actionBarTitle.setText(getResources().getString(R.string.contacts));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EventDrawerAction());
            }
        });

        optionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EventOptionButtonPressed());
            }
        });


        searchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
                    }
                }
            }
        });

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditText.setVisibility(View.VISIBLE);
                searchEditText.setHint(getResources().getString(R.string.contact_search_hint));
                searchEditText.setText("");
                option_menu_btn.setVisibility(View.GONE);
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
                        searchEditText.setText("");
                        EventBus.getDefault().post(new EventFilterContacts(""));
                        option_menu_btn.setVisibility(View.VISIBLE);
                        headerLayout.setVisibility(View.VISIBLE);
                        searchIcon.setVisibility(View.VISIBLE);
                        actionBarMenuImage.setImageDrawable(getResources().getDrawable(R.drawable.header_menu));
                        cancel_search_image.setVisibility(View.GONE);
                        searchLinearLayout.setVisibility(View.GONE);
                        searchEditText.setVisibility(View.GONE);
                        actionBar_menu_button_layout.setClickable(true);
                        if (imm.isActive()) {
                            imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
                        }
                        actionBar_option_button_layout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EventBus.getDefault().post(new EventOptionButtonPressed());
                            }
                        });

                    }
                });
            }
        });

        searchEditText.setHint(getResources().getString(R.string.contact_search_hint));
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                EventBus.getDefault().post(new EventFilterContacts(s.toString()));
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        contacts_action_bar_pager = (RelativeLayout)contactView.findViewById(R.id.tabs_general_pager);

        state = ContactsState.ALL;

        tabs = (PagerSlidingTabStripPlus) contactView.findViewById(R.id.tabs);
        tabs.setDividerColor(getResources().getColor(R.color.transparent));
        tabs.setTextSize(getResources().getDimensionPixelSize(R.dimen.xl_text_size));
        tabs.setTextColor(getResources().getColor(R.color.tab_text_aesthetic));
        tabsChild = ((LinearLayout)tabs.getChildAt(0));

        refreshList(TactConst.SCROLL_DIRECTION_DOWN);

        contactPager = (ViewPager) contactView.findViewById(R.id.contacts_list_pager_container);

//        contacts_db_list_all = new ArrayList<Contact>();

        contact_list = new ArrayList<Contact>();



        contactPager.setAdapter(new ContactPagerAdapter(getActivity().getApplicationContext()));
        contactPager.setCurrentItem(0);

        ViewPager.OnPageChangeListener pageListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
                tryRemoveKeyboard();
            }

            @Override
            public void onPageSelected(int i) {
                switchPage(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        };


        contactPager.setOnPageChangeListener(pageListener);
        tabs.setViewPager(contactPager);
        tabs.setOnPageChangeListener(pageListener);
        // TODO: delete this code and create new features for the library
        ((TextView) tabsChild.getChildAt(0)).setTextColor(getResources().getColor(R.color.dark_grey));
        ((TextView) tabsChild.getChildAt(1)).setTextColor(getResources().getColor(R.color.light_gray));

        contactPager.getAdapter().notifyDataSetChanged();

        if (page > 0) {
            contactPager.setCurrentItem(page);
        }



        EventBus.getDefault().post(new EventSetUpActionBarFromContacts());



        return contactView;
    }

    private void refreshList(int direction) {
        asyncLoadContacts = new AsyncLoadContacts(this, direction);
        asyncLoadContacts.execute();
    }

    private void loadCurrentValues() {
        if (contact_list != null && contact_list.size() > 0) {
            firsVisibleContact = contact_list.get(0).getDisplayName();
            lastVisibleContact = contact_list.get(contact_list.size() - 1).getDisplayName();
        } else {
            firsVisibleContact = null;
            lastVisibleContact = null;
        }

    }

    private boolean isRefreshRunning() {
        return asyncLoadContacts != null && (asyncLoadContacts.getStatus() == AsyncTask.Status.PENDING || asyncLoadContacts.getStatus() == AsyncTask.Status.RUNNING);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isRefreshRunning()) {
            asyncLoadContacts.setActivity(this);
        }
    }

    public void onEventMainThread(EventFilterContacts eventFilterContacts) {
        filterContacts(eventFilterContacts.filter);
    }

    public void filterContacts(CharSequence filter){
        filterText = filter.toString();
//        View view = contactPager.getChildAt(contactPager.getCurrentItem()).getRootView();
//        StickyListHeadersListView list = (StickyListHeadersListView) view.findViewById(R.id.contact_sticky_list_view);
//        StickyAdapter adapter = (StickyAdapter)list.getAdapter();
        ContactsAdapter adapter = null;
        if (state == ContactsState.ALL){
            adapter = allAdapter;
        }
        else if (state == ContactsState.RECENT){
            adapter = recentAdapter;
        }

//        if (adapter != null) {
//            if (state == ContactsState.ALL) {
//                if (filter.length() > 0) {
//                    adapter.refresh(filterContactList(filter));
//                } else {
//                    adapter.refresh(contacts_db_list_all);
//                }
//            } else if (state == ContactsState.RECENT) {
//                adapter.refresh(getRecentContacts(filter));
//
//            } else if (state == ContactsState.STARRED) {
//                adapter.refresh(getStarredContacts(filter));
//            }
//        }
    }

    public ArrayList<Contact> getStarredContacts(final CharSequence filter){
        ArrayList<Contact> filteredList = (ArrayList<Contact>) Predicate.filter(contact_list, new IPredicate<Contact>() {
            public boolean apply(Contact sContact) {
                if (filter.length() > 0) {
                    return sContact.getIsStarred().equals(1) && sContact.getDisplayName().toLowerCase().contains(filter.toString().toLowerCase());
                } else {
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
        ArrayList<Contact> filteredList = (ArrayList<Contact>) Predicate.filter(contact_list, new IPredicate<Contact>() {
            public boolean apply(Contact sContact) {
                if (filter.length() > 0){
                    return sContact.getVisitedAt() != null && sContact.getVisitedAt() != 0 && sContact.getDisplayName().toLowerCase().contains(filter.toString().toLowerCase());
                }
                else {
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
        ArrayList<Contact> filteredList = (ArrayList<Contact>) Predicate.filter(contact_list, new IPredicate<Contact>() {
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

    private FragmentMoveHandler getFragmentMoveHandler(Contact contact, int page) {
        FragmentMoveHandler fragmentMoveHandler = new FragmentMoveHandler();
        fragmentMoveHandler.setFragmentTag(TactConst.FRAGMENT_CONTACTS_TAG);
        fragmentMoveHandler.setObject(contact);
        fragmentMoveHandler.setPage(page);
        return  fragmentMoveHandler;
    }

    public void switchPage(int page) {
        switch (page) {
            case 0:
                state = ContactsState.ALL;

                // TODO: delete this code and create new features for the library
                ((TextView) tabsChild.getChildAt(0)).setTextColor(getResources().getColor(R.color.dark_grey));
                ((TextView) tabsChild.getChildAt(1)).setTextColor(getResources().getColor(R.color.light_gray));
                break;
            case 1:
                state = ContactsState.RECENT;

                // TODO: delete this code and create new features for the library
                ((TextView) tabsChild.getChildAt(1)).setTextColor(getResources().getColor(R.color.dark_grey));
                ((TextView) tabsChild.getChildAt(0)).setTextColor(getResources().getColor(R.color.light_gray));
                break;
            case 2:
                state = ContactsState.STARRED;
                break;
        }
        EventBus.getDefault().post(new EventFilterContacts(filterText));
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
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.word_layout, parent, false);
            }

            TextView wordTextView = (TextView) convertView.findViewById(R.id.word_text_view_layout);
            wordTextView.setText(wordList[position]);
            wordTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (lastSelected != null) {
                        lastSelected.setSelected(false);
                    }
                    v.setSelected(true);
                    lastSelected = v;
                }
            });

            return convertView;
        }

    }

    public class ContactsAdapter extends BaseAdapter {

        private Context context = getActivity();
        private LayoutInflater inflater;

        public ContactsAdapter() {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return contact_list.size();
        }

        @Override
        public Contact getItem(int position) {
            return contact_list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.meeting_invitees_layout, parent, false);
            }
            TextView text = (TextView) convertView.findViewById(R.id.contact_name_text_view);
            TextView company = (TextView) convertView.findViewById(R.id.company_name_text_view);
            ProfileIconView image = (ProfileIconView) convertView.findViewById(R.id.contact_details_icon_small);


            text.setText(contact_list.get(position).getDisplayName());
            if (contact_list.get(position).getCompanyName() != null && !contact_list.get(position).getCompanyName().isEmpty()) {
                company.setText(contact_list.get(position).getCompanyName());
            }
            else {
                company.setVisibility(View.GONE);
            }
            image.loadImage(contact_list.get(position).getPhotoUrl(), contact_list.get(position).getInitials(), contact_list.get(position).isCompany());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new EventSetupActionBarOngoing());
                    setBackStack(getFragmentMoveHandler(contact_list.get(position), contactPager.getCurrentItem()));
                    EventBus.getDefault().post(new EventMoveToFragment(TactConst.FRAGMENT_CONTACT_DETAIL_TAG, 0, 0, contact_list.get(position), 0));
                }
            });

            return convertView;
        }
//
//        @Override
//        public View getHeaderView(int position, View convertView, ViewGroup parent) {
//            if (convertView == null) {
//                convertView = inflater.inflate(R.layout.contacts_group_view, parent, false);
//            }
//
//            TextView text = (TextView) convertView.findViewById(R.id.contact_group_heading);
//
//            //set header text as first char in name
//            text.setText("" + contacts_array.get(position).getDisplayName().toUpperCase().subSequence(0, 1).charAt(0));
//
//            return convertView;
//        }
//
//        @Override
//        public long getHeaderId(int position) {
//            return contacts_array.get(position).getDisplayName().toUpperCase().subSequence(0, 1).charAt(0);
//        }

    }

    public class ContactPagerAdapter extends PagerAdapter {

        private final String[] TITLES = {"ALL", "RECENT"};

        LayoutInflater inflater;

        public ContactPagerAdapter(Context ctx) {
            inflater = LayoutInflater.from(ctx);
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            return getContactListView(FragmentContactsBatchLoad.this.getActivity(), inflater, collection, position);
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

    /**
     * Set item for a single day
     * @param context Activity context
     * @param inflater inflater service
     * @param collection colection from PageAdapter
     * @return object
     */
    private Object getContactListView(Context context, LayoutInflater inflater, ViewGroup collection, int position){

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);

        //We need to create a ScrollView just for days
        LinearLayout linearContent = new LinearLayout(context);
        linearContent.setLayoutParams(layoutParams);

        //Now, inflate the agenda day item layout
        View myView = inflater.inflate(R.layout.combo_contacts_layout, null);
        if(myView != null){
//            lsv_contacts = (ListView) myView.findViewById(R.id.contact_list_view);
            alphabetic_listview = (ListView) myView.findViewById(R.id.alphabetic_listview);
            alphabetic_listview.setVisibility(View.GONE);

//            alphabetic_listview.setAdapter(new WordListAdapter(getActivity()));
//            alphabetic_listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

            lsv_contacts.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);

            lsv_contacts.setOnScrollListener(new AbsListView.OnScrollListener() {

                private int mLastFirstVisibleItem;

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    tryRemoveKeyboard();
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    int itemsDownNotVisible = totalItemCount - (firstVisibleItem + visibleItemCount);
                    int itemsUpNotVisible = totalItemCount - (itemsDownNotVisible + visibleItemCount);
                    if(mLastFirstVisibleItem<firstVisibleItem)
                    {
                        //SCROLLING DOWN
                        if (itemsDownNotVisible < 10 && !isRefreshRunning()) {
                            refreshList(TactConst.SCROLL_DIRECTION_DOWN);
                        }
                    }
                    if(mLastFirstVisibleItem>firstVisibleItem)
                    {
                        //SCROLLING UP
                        if (itemsUpNotVisible < 10 && !isRefreshRunning()) {
                            refreshList(TactConst.SCROLL_DIRECTION_UP);
                            listScrollX = firstVisibleItem;
                        }
                    }
                    mLastFirstVisibleItem=firstVisibleItem;
                }
            });

            ArrayList<Contact> contacts_db_list = new ArrayList<Contact>();
            switch (position) {
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
//            if (contacts_db_list != null && contacts_db_list.size()>0) {
                lsv_contacts.setAdapter(new ContactsAdapter());
                if (position == 0 && allAdapter == null){
                    allAdapter = (ContactsAdapter)lsv_contacts.getAdapter();
                }
                else if (position == 1 && recentAdapter == null){
                    recentAdapter = (ContactsAdapter)lsv_contacts.getAdapter();
                }
//            } else {
//                alphabetic_listview.setVisibility(View.GONE);
//            }



            linearContent.addView(myView);
            collection.addView(linearContent);
        }

        return linearContent;
    }

    public class AsyncLoadContacts extends AsyncTask<Void, Void, Object> {
        // Maintain attached activity for states change propose
        private FragmentContactsBatchLoad context;
        // Keep the response
        private Object _response;
        // Flag that keep async task completed status
        private boolean completed;

        private int direction = TactConst.SCROLL_DIRECTION_DOWN;

        // Constructor
        private AsyncLoadContacts(FragmentContactsBatchLoad context, int direction) {
            this.context = context;
            this.direction = direction;
        }

        // Pre execution actions
        @Override
        protected void onPreExecute() {
        }

        protected Object doInBackground(Void... arg0) {
            try {
                totalContactsCount = DatabaseManager.getContactsTotalCount(contactPager.getCurrentItem());
                firstContactId = DatabaseManager.getFirstContactId(contactPager.getCurrentItem());
                return DatabaseManager.loadContacts(firsVisibleContact, lastVisibleContact, filter, direction, contactPager.getCurrentItem(), backContact, 0);
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
            backContact = null;
            notifyActivityTaskCompleted();
        }

        // Notify activity of async task complete
        private void notifyActivityTaskCompleted() {
            if (null != context) {
                context.notifyFragmentTaskCompleted((ArrayList<Contact>) _response, direction);
            }
        }

        // Sets the current activity to the async task
        public void setActivity(FragmentContactsBatchLoad context) {
            this.context = context;
            if (completed) {
                notifyActivityTaskCompleted();
            }
        }
    }

    private void notifyFragmentTaskCompleted(final ArrayList<Contact> contacts, int direction) {
        asyncLoadContacts = null;
        if (contact_list == null) {
            contact_list = new ArrayList<Contact>();
        }
        if (contacts != null) {
            if (direction == TactConst.SCROLL_DIRECTION_DOWN) {
                contact_list.addAll(contacts);
            } else {
                Collections.reverse(contacts);
                Contact firstContactBefore = contacts.get(0);
                contact_list.addAll(0, contacts);
                //find the new position of the first visible contact before
                listScrollX = contacts.indexOf(firstContactBefore);
                firstContactBefore = null;
            }
            loadCurrentValues();
            if (contactPager.getCurrentItem() == 0 && allAdapter != null) {
                allAdapter.notifyDataSetChanged();
            }
            if (contactPager.getCurrentItem() == 1 && recentAdapter != null) {
                recentAdapter.notifyDataSetChanged();
            }
        }

        System.gc();
    }
}
