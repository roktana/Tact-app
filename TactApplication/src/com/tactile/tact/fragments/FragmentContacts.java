package com.tactile.tact.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.tactile.tact.database.DatabaseContentProvider;
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
import com.tactile.tact.services.network.RedirectURLGetter;
import com.tactile.tact.services.network.TactNetworking;
import com.tactile.tact.utils.ContactAPI;
import com.tactile.tact.utils.IPredicate;
import com.tactile.tact.utils.Predicate;
import com.tactile.tact.utils.TactDialogHandler;
import com.tactile.tact.views.CustomViewPager;
import com.tactile.tact.views.ProfileIconView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;


/**
 * Created by ismael on 11/28/14.
 */
public class FragmentContacts extends FragmentTactBase {

    private StickyListHeadersListView contact_list_all;
    private StickyListHeadersListView contact_list_recent;
    private ListView alphabetic_listview;
    private CustomViewPager contactPager;
    private RelativeLayout contacts_action_bar_pager;
    private ArrayList<Contact> contacts_db_list_all;
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
    private PagerSlidingTabStripPlus tabs;
    private WordListAdapter alphabetAdapter;
    private TextView no_contact_textview;

    private TactDialogHandler dialogHandler;

    private int position = 0;
    private int page = 0;

    private AsyncLoadContacts asyncLoadContacts;
    private ArrayList<RedirectURLGetter> asyncLoadContactsImages;

    private static enum ContactsState {
        ALL,
        RECENT,
        STARRED
    }

    private ContactsState state;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            position = bundle.getInt("position");
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
        no_contact_textview = (TextView) contactView.findViewById(R.id.no_contact_textview);
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
                no_contact_textview.setVisibility(View.GONE);
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

        contactPager = (CustomViewPager) contactView.findViewById(R.id.contacts_list_pager_container);

        contacts_db_list_all = new ArrayList<Contact>();

        asyncLoadContactsImages = new ArrayList<RedirectURLGetter>();

        asyncLoadContacts = new AsyncLoadContacts(this, inflater);
        asyncLoadContacts.execute();

        EventBus.getDefault().post(new EventSetUpActionBarFromContacts());

        tabs = (PagerSlidingTabStripPlus) contactView.findViewById(R.id.tabs);

        return contactView;
    }

    private void clearSearch() {
        searchEditText.setText("");
        tabs.setVisibility(View.VISIBLE);
        no_contact_textview.setVisibility(View.GONE);
        contactPager.setPagingEnabled(true);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        asyncLoadContactsImages = null;
        System.gc();
    }

    public void onEventMainThread(EventFilterContacts eventFilterContacts) {
        filterContacts(eventFilterContacts.filter);
    }

    public void filterContacts(CharSequence filter){
        filterText = filter.toString();
        ArrayList<Contact> contactListSearch = new ArrayList<Contact>();

        if (state == ContactsState.ALL && contact_list_all != null) {
            contactListSearch = filterContactList(filter);
            if (filter.length() > 0) {
                ((StickyAdapter)contact_list_all.getAdapter()).refresh(contactListSearch);
            } else {
                ((StickyAdapter)contact_list_all.getAdapter()).refresh(contacts_db_list_all);
            }
        } else if (state == ContactsState.RECENT && contact_list_recent != null) {
            contactListSearch = getRecentContacts(filter);
            ((StickyAdapter)contact_list_recent.getAdapter()).refresh(contactListSearch);
        }
//        else if (state == ContactsState.STARRED) {
//            adapter.refresh(getStarredContacts(filter));
//        }
        
        if (contactListSearch.size()==0) {
            no_contact_textview.setVisibility(View.VISIBLE);
        } else {
            no_contact_textview.setVisibility(View.GONE);
        }
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

    private FragmentMoveHandler getFragmentMoveHandler(int position, int page) {
        FragmentMoveHandler fragmentMoveHandler = new FragmentMoveHandler();
        fragmentMoveHandler.setFragmentTag(TactConst.FRAGMENT_CONTACTS_TAG);
        fragmentMoveHandler.setPosition(position);
        fragmentMoveHandler.setPage(page);
        return  fragmentMoveHandler;
    }

    public void switchPage(int page) {
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

        private Context context = getActivity();
        private LayoutInflater inflater;
        public ArrayList<Contact> contacts_array;
        private Map<String, Integer> headersMap;

        public StickyAdapter(ArrayList<Contact> contacts) {
            inflater = LayoutInflater.from(context);
            contacts_array = contacts;
            headersMap = new HashMap<>();
            fillHeadersMap();
        }

        public void refresh(ArrayList<Contact> contacts){
            contacts_array = contacts;
            fillHeadersMap();
            this.notifyDataSetChanged();
            if (alphabetAdapter != null) {
                alphabetAdapter.notifyDataSetChanged();
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
                convertView = inflater.inflate(R.layout.meeting_invitees_layout, parent, false);
            }
            TextView text = (TextView) convertView.findViewById(R.id.contact_name_text_view);
            TextView company = (TextView) convertView.findViewById(R.id.company_name_text_view);
            ProfileIconView image = (ProfileIconView) convertView.findViewById(R.id.contact_details_icon_small);

            final Contact contact = contacts_array.get(position);

            text.setText(contact.getDisplayName());

            if (contact.getCompanyName() != null && !contact.getCompanyName().isEmpty()) {
                company.setVisibility(View.VISIBLE);
                company.setText(contacts_array.get(position).getCompanyName());
            }
            else {
                company.setVisibility(View.GONE);
            }

            if (contact.isLocal() && contact.getContactDetails().size() > 0){
                try {
                    Bitmap bitmap = ContactAPI.getContactImage(Integer.parseInt(contact.getContactDetails().get(0).getRemoteId()));
                    image.loadImage(bitmap, contact.getInitials());
                }
                catch (Exception e){
                    Log.w("LOAD IMAGE", e.getMessage());
                }
            }
            else {
                if (contact.getRealPhotoUrl() != null) {
                    image.loadImage(contact.getRealPhotoUrl(), contact.getInitials(), contact.isCompany());
                } else {
                    image.loadImage(null, contact.getInitials(), contact.isCompany());
                    if (contact.getPhotoUrl() != null && !contact.getPhotoUrl().isEmpty() && !contact.getPhotoUrl().startsWith("http") &&
                            !isURLGetterAsyncTaskRunning(contact.getRecordID())) {
                        RedirectURLGetter asyncTask = new RedirectURLGetter(FragmentContacts.this, contact.getRecordID());
                        asyncTask.execute(TactNetworking.getURL() + "/photo_store?photo_url=" + contact.getPhotoUrl());
                        asyncLoadContactsImages.add(asyncTask);
                    }
                }
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new EventSetupActionBarOngoing());
                    setBackStack(getFragmentMoveHandler(position, contactPager.getCurrentItem()));
                    EventBus.getDefault().post(new EventMoveToFragment(TactConst.FRAGMENT_CONTACT_DETAIL_TAG, 0, 0, contact, 0));
                }
            });

            return convertView;
        }

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.contacts_group_view, parent, false);
            }

            TextView text = (TextView) convertView.findViewById(R.id.contact_group_heading);

            //set header text as first char in name
            text.setText("" + contacts_array.get(position).getDisplayName().toUpperCase().subSequence(0, 1).charAt(0));

            return convertView;
        }

        @Override
        public long getHeaderId(int position) {
            return contacts_array.get(position).getDisplayName().toUpperCase().subSequence(0, 1).charAt(0);
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

    public class ContactPagerAdapter extends PagerAdapter {

        private final String[] TITLES = {"ALL", "RECENT"};

        LayoutInflater inflater;

        public ContactPagerAdapter(Context ctx) {
            inflater = LayoutInflater.from(ctx);
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            return getContactListView(FragmentContacts.this.getActivity(), inflater, collection, position);
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
                        tryRemoveKeyboard();
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
                        tryRemoveKeyboard();
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
                StickyAdapter adapter = new StickyAdapter(contacts_db_list);
                if (currentPosition == 0) {
                    contact_list_all.setAdapter(adapter);
                    if (position > 0 && currentPosition == page) {
                        contact_list_all.post(new Runnable() {
                            @Override
                            public void run() {
                                contact_list_all.setSelection(position);
                            }
                        });
                    }
                } else if (currentPosition == 1) {
                    contact_list_recent.setAdapter(adapter);
                    if (position > 0 && currentPosition == page) {
                        contact_list_recent.post(new Runnable() {
                            @Override
                            public void run() {
                                contact_list_recent.setSelection(position);
                            }
                        });
                    }
                }
                alphabetAdapter = new WordListAdapter(getActivity());
                alphabetic_listview.setAdapter(alphabetAdapter);

            } else {
                alphabetic_listview.setVisibility(View.GONE);
            }

            linearContent.addView(myView);
            collection.addView(linearContent);
        }

        return linearContent;
    }

    public class AsyncLoadContacts extends AsyncTask<Void, Void, Object> {
        // Maintain attached activity for states change propose
        private FragmentContacts context;
        // Keep the response
        private Object _response;
        // Flag that keep async task completed status
        private boolean completed;

        private LayoutInflater inflater;

        // Constructor
        private AsyncLoadContacts(FragmentContacts context, LayoutInflater inflater) {
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
                return DatabaseManager.getAllContacts(
                        DatabaseContentProvider.URI_ALL_PRIMARY_CONTACTS,
                        null,
                        "UPPER( " + com.tactile.tact.database.model.Contact.ROW_DISPLAY_NAME + ")", null
                );
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
        public void setActivity(FragmentContacts context) {
            this.context = context;
            if (completed) {
                notifyActivityTaskCompleted();
            }
        }
    }

    private void notifyFragmentTaskCompleted(ArrayList<Contact> contacts, LayoutInflater inflater) {
        asyncLoadContacts = null;
        contacts_db_list_all = contacts;

        ContactPagerAdapter contactPagerAdapter = new ContactPagerAdapter(getActivity().getApplicationContext());
        contactPager.setAdapter(contactPagerAdapter);
        contactPager.setCurrentItem(0);

        ViewPager.OnPageChangeListener pageListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
                tryRemoveKeyboard();
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
}
