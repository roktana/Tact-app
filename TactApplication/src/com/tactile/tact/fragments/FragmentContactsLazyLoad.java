package com.tactile.tact.fragments;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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
import com.tactile.tact.views.ListViewLazyLoad;
import com.tactile.tact.views.ProfileIconView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by leyan on 1/15/15.
 */
public class FragmentContactsLazyLoad extends FragmentTactBase {

    private ListViewLazyLoad contact_list_all;
    private ListViewLazyLoad contact_list_recent;
    private CustomViewPager contactPager;
    private String filterText = "";
    private LinearLayout actionBar;
    private LinearLayout searchIcon;
    private EditText searchEditText;
    private LinearLayout headerLayout;
    private ImageView cancel_search_image;
    private LinearLayout searchLinearLayout;
    private LinearLayout actionBar_menu_button_layout;
    private LinearLayout actionBar_option_button_layout;
    private LinearLayout search_layout;
    private InputMethodManager imm;
    private PagerSlidingTabStripPlus tabs;
    private TextView no_contact_textview;

    private String firsVisibleContact;
    private String lastVisibleContact;
    private ArrayList<Contact> contact_list;

    private boolean isSearching = false;

    private Contact backContact;
    private int page = 0;

    int index = -1;
    int top = -1;

    private AsyncLoadContacts asyncLoadContacts;
    private ArrayList<RedirectURLGetter> asyncLoadContactsImages;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            backContact = (Contact)bundle.getSerializable("contact");
            page = bundle.getInt("page");
            filterText = bundle.getString("filter");
        }

        View contactView = inflater.inflate(R.layout.contact_fragment_view, container, false);

        EventBus.getDefault().post(new EventHideActivityActionBar());

        //Declarations
        actionBar = (LinearLayout) contactView.findViewById(R.id.contacts_action_bar);
        actionBar.findViewById(R.id.actionBar_layout).setBackgroundColor(getResources().getColor(R.color.action_bar_grey));
        TextView actionBarTitle = (TextView) actionBar.findViewById(R.id.actionBar_title_text);
        cancel_search_image = (ImageView) actionBar.findViewById(R.id.close_search_icon);
        actionBar_menu_button_layout = (LinearLayout) actionBar.findViewById(R.id.actionBar_menu_button_layout);
        searchLinearLayout = (LinearLayout) actionBar.findViewById(R.id.search_linear_layout);
        searchIcon = (LinearLayout) actionBar.findViewById(R.id.action_bar_secondary_button_1);
        searchEditText = (EditText) actionBar.findViewById(R.id.search_edit_text);
        headerLayout = (LinearLayout) actionBar.findViewById(R.id.header_linear_layout);
        actionBar_option_button_layout = (LinearLayout) actionBar.findViewById(R.id.actionBar_option_button_layout);
        no_contact_textview = (TextView) contactView.findViewById(R.id.no_contact_textview);
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        tabs = (PagerSlidingTabStripPlus) contactView.findViewById(R.id.tabs);

        /********HEADER BAR AND SEARCH*************************/
        actionBarTitle.setText(getResources().getString(R.string.contacts));

        actionBar_menu_button_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EventDrawerAction());
            }
        });

        actionBar_option_button_layout.setOnClickListener(new View.OnClickListener() {
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

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search();
                    return true;
                }
                return false;
            }
        });

        cancel_search_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSearch();
                refreshList(TactConst.SCROLL_DIRECTION_DOWN);
            }
        });

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSearching) {
                   search();
                } else {
                    showSearch();
                }
            }
        });

        actionBar_option_button_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EventOptionButtonPressed());
            }
        });

        if (filterText != null && !filterText.isEmpty()) {
            showSearch();
            searchEditText.setText(filterText);
            refreshList(TactConst.SCROLL_DIRECTION_DOWN);
        }

        no_contact_textview.setVisibility(View.GONE);

        searchEditText.setHint(getResources().getString(R.string.contact_search_hint));




        /****************LIST******************************************/
        asyncLoadContactsImages = new ArrayList<RedirectURLGetter>();
        contact_list = new ArrayList<Contact>();

        contactPager = (CustomViewPager) contactView.findViewById(R.id.contacts_list_pager_container);

        if (page == 0 && !isSearching) {
            refreshList(TactConst.SCROLL_DIRECTION_DOWN);
        }

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

        contactPager.getAdapter().notifyDataSetChanged();

        if (page > 0) {
            contactPager.setCurrentItem(page);
        }
        EventBus.getDefault().post(new EventSetUpActionBarFromContacts());



        return contactView;
    }

    private void showSearch() {
        isSearching = true;

        actionBar_menu_button_layout.setVisibility(View.GONE);
        headerLayout.setVisibility(View.GONE);
        actionBar_option_button_layout.setVisibility(View.GONE);
        searchLinearLayout.setVisibility(View.VISIBLE);
        searchEditText.setText("");
        searchEditText.requestFocus();
        tabs.setVisibility(View.GONE);
        imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
    }

    private void hideSearch() {
        isSearching = false;

        actionBar_menu_button_layout.setVisibility(View.VISIBLE);
        headerLayout.setVisibility(View.VISIBLE);
        actionBar_option_button_layout.setVisibility(View.VISIBLE);
        searchLinearLayout.setVisibility(View.GONE);
        searchEditText.setText("");
        filterText = null;
        tabs.setVisibility(View.VISIBLE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
        }
        clearLoadContactsParams();
    }

    private void search() {
        filterText = searchEditText.getText().toString();
        clearLoadContactsParams();
        refreshList(TactConst.SCROLL_DIRECTION_DOWN);
    }

    private void clearLoadContactsParams() {
        if (isRefreshRunning()) {
            asyncLoadContacts.cancel(true);
        }
        contact_list.clear();
        firsVisibleContact = null;
        lastVisibleContact = null;
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

    private FragmentMoveHandler getFragmentMoveHandler(Contact contact, int page, String filter) {
        FragmentMoveHandler fragmentMoveHandler = new FragmentMoveHandler();
        fragmentMoveHandler.setFragmentTag(TactConst.FRAGMENT_CONTACTS_TAG);
        fragmentMoveHandler.setObject(contact);
        fragmentMoveHandler.setPage(page);
        fragmentMoveHandler.setText(filter);
        return  fragmentMoveHandler;
    }

    public void switchPage(int page) {
        clearLoadContactsParams();
        this.page = page;
        contactPager.setCurrentItem(page);
        refreshList(TactConst.SCROLL_DIRECTION_DOWN);
    }

    public class ContactsAdapter extends BaseAdapter {

        private Context context = getActivity();
        private LayoutInflater inflater;

        public ContactsAdapter() {
            inflater = LayoutInflater.from(context);
        }

        public void refresh(ArrayList<Contact> contacts){
            contact_list = contacts;
            this.notifyDataSetChanged();
        }

        public ArrayList<Contact> getContacts() {
            if (contact_list == null) {
                contact_list = new ArrayList<>();
            }
            return contact_list;
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

            try {
                final Contact contact = contact_list.get(position);

                text.setText(contact.getDisplayName());

                if (contact.getCompanyName() != null && !contact.getCompanyName().isEmpty()) {
                    company.setVisibility(View.VISIBLE);
                    company.setText(contact_list.get(position).getCompanyName());
                } else {
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
                            RedirectURLGetter asyncTask = new RedirectURLGetter(FragmentContactsLazyLoad.this, contact.getRecordID());
                            asyncTask.execute(TactNetworking.getURL() + "/photo_store?photo_url=" + contact.getPhotoUrl());
                            asyncLoadContactsImages.add(asyncTask);
                        }
                    }
                }


                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(new EventSetupActionBarOngoing());
                        setBackStack(getFragmentMoveHandler(contact_list.get(position), contactPager.getCurrentItem(), filterText));
                        EventBus.getDefault().post(new EventMoveToFragment(TactConst.FRAGMENT_CONTACT_DETAIL_TAG, 0, 0, contact, 0));
                    }
                });
            } catch (Exception e) {

            }

            return convertView;
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
        if (contact_list != null && realPhotoUrl != null && realPhotoUrl.startsWith("http")){
            for (Contact contact: contact_list){
                if (contact.getRecordID().equals(recordID)){
                    contact.setRealPhotoUrl(realPhotoUrl);
                    if (contact_list_all != null && ((ContactsAdapter)contact_list_all.getAdapter()) != null && contactPager.getCurrentItem() == 0){
                        ((ContactsAdapter)contact_list_all.getAdapter()).notifyDataSetChanged();
                    }
                    else if (contact_list_recent != null && ((ContactsAdapter)contact_list_recent.getAdapter()) != null && contactPager.getCurrentItem() == 1){
                        ((ContactsAdapter)contact_list_recent.getAdapter()).notifyDataSetChanged();
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
            return getContactListView(FragmentContactsLazyLoad.this.getActivity(), inflater, collection, position);
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
        View myView = inflater.inflate(R.layout.contacts_page, null);
        if(myView != null){
            if (currentPosition == 0) {
                contact_list_all = (ListViewLazyLoad) myView.findViewById(R.id.contacts_lazy_list_view);
                contact_list_all.setDivider(null);

                contact_list_all.setListViewLazyListListener(new ListViewLazyLoad.OnListViewLazyListListener() {
                    @Override
                    public void onScrollingDown() {
                        if (!isRefreshRunning()) {
                            refreshList(TactConst.SCROLL_DIRECTION_DOWN);
                        }
                    }

                    @Override
                    public void onScrollingUp() {
                        if (!isRefreshRunning()) {
                            refreshList(TactConst.SCROLL_DIRECTION_UP);
                        }
                    }

                    @Override
                    public void onScrollStateChanged() {
                        tryRemoveKeyboard();
                    }

                    @Override
                    public void onOverScrollUp() {
                        if (!isRefreshRunning()) {
                            refreshList(TactConst.SCROLL_DIRECTION_UP);
                        }
                    }

                    @Override
                    public void onOverScrollDown() {

                    }
                });
            } else if (currentPosition == 1) {
                contact_list_recent = (ListViewLazyLoad) myView.findViewById(R.id.contacts_lazy_list_view);
                contact_list_recent.setDivider(null);
                contact_list_recent.setListViewLazyListListener(new ListViewLazyLoad.OnListViewLazyListListener() {
                    @Override
                    public void onScrollingDown() {
                        if (!isRefreshRunning()) {
                            refreshList(TactConst.SCROLL_DIRECTION_DOWN);
                        }
                    }

                    @Override
                    public void onScrollingUp() {
                        if (!isRefreshRunning()) {
                            refreshList(TactConst.SCROLL_DIRECTION_UP);
                        }
                    }

                    @Override
                    public void onScrollStateChanged() {
                        tryRemoveKeyboard();
                    }

                    @Override
                    public void onOverScrollUp() {
                        if (!isRefreshRunning()) {
                            refreshList(TactConst.SCROLL_DIRECTION_UP);
                        }
                    }

                    @Override
                    public void onOverScrollDown() {

                    }
                });
            }

            ContactsAdapter adapter = new ContactsAdapter();
                if (currentPosition == 0) {
                    contact_list_all.setAdapter(adapter);
                } else if (currentPosition == 1) {
                    contact_list_recent.setAdapter(adapter);
                }

            linearContent.addView(myView);
            collection.addView(linearContent);
        }

        return linearContent;
    }

    private void refreshList(int direction) {
        asyncLoadContacts = new AsyncLoadContacts(this, direction);
        asyncLoadContacts.executeContent();
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

    public class AsyncLoadContacts extends CustomAsyncTask<Void, Void, Object> {
        // Maintain attached activity for states change propose
        private FragmentContactsLazyLoad context;
        // Keep the response
        private Object _response;
        // Flag that keep async task completed status
        private boolean completed;

        private int direction = TactConst.SCROLL_DIRECTION_DOWN;

        // Constructor
        private AsyncLoadContacts(FragmentContactsLazyLoad context, int direction) {
            this.context = context;
            this.direction = direction;
        }

        // Pre execution actions
        @Override
        protected void onPreExecute() {
        }

        protected Object doInBackground(Void... arg0) {
            try {
//                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
                return DatabaseManager.loadContacts(firsVisibleContact, lastVisibleContact, filterText, direction, page, backContact, TactConst.CONTACT_LIST_LOAD_ALL);
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
        public void setActivity(FragmentContactsLazyLoad context) {
            this.context = context;
            if (completed) {
                notifyActivityTaskCompleted();
            }
        }
    }

    private int reloadContactsEndList = 0;
    private void reloadContactsBottom() {
        refreshList(TactConst.SCROLL_DIRECTION_UP);
        reloadContactsEndList++;
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

                if (contactPager.getCurrentItem() == 0 && contact_list_all != null && contact_list_all.getAdapter() != null) {
                    // save index and top position
                    index = contact_list_all.getFirstVisiblePosition();
                    View v = contact_list_all.getChildAt(0);
                    top = (v == null) ? 0 : (v.getTop() - contact_list_all.getPaddingTop());
                    // Block children layout for now
                    contact_list_all.setBlockLayoutChildren(true);
                } else if (contactPager.getCurrentItem() == 1 && contact_list_recent != null && contact_list_recent.getAdapter() != null) {
                    // save index and top position
                    index = contact_list_recent.getFirstVisiblePosition();
                    View v = contact_list_recent.getChildAt(0);
                    top = (v == null) ? 0 : (v.getTop() - contact_list_recent.getPaddingTop());
                    // Block children layout for now
                    contact_list_recent.setBlockLayoutChildren(true);
                }

                contact_list.addAll(0, contacts);
            }
            loadCurrentValues();
            if (contact_list.size() < 6 && reloadContactsEndList == 0) {
                reloadContactsBottom();
            } else {
                reloadContactsEndList = 0;
                if (contactPager.getCurrentItem() == 0 && contact_list_all != null && contact_list_all.getAdapter() != null) {
                    ((BaseAdapter) contact_list_all.getAdapter()).notifyDataSetChanged();

                    if (direction == TactConst.SCROLL_DIRECTION_UP && index >= 0) {
                        // restore index and position
                        contact_list_all.post(new Runnable() {
                            @Override
                            public void run() {
                                contact_list_all.setSelectionFromTop(index + contacts.size(), top);
                                // Let ListView start laying out children again
                                contact_list_all.setBlockLayoutChildren(false);
                                top = -1;
                                index = -1;
                            }
                        });
                    }
                }
                if (contactPager.getCurrentItem() == 1 && contact_list_recent != null && contact_list_recent.getAdapter() != null) {
                    ((BaseAdapter) contact_list_recent.getAdapter()).notifyDataSetChanged();

                    if (direction == TactConst.SCROLL_DIRECTION_UP && index >= 0) {
                        // restore index and position
                        contact_list_recent.post(new Runnable() {
                            @Override
                            public void run() {
                                contact_list_recent.setSelectionFromTop(index + contacts.size(), top);
                                // Let ListView start laying out children again
                                contact_list_recent.setBlockLayoutChildren(false);
                                top = -1;
                                index = -1;
                            }
                        });
                    }
                }
            }
        }

        if (contact_list == null || contact_list.size() == 0) {
            no_contact_textview.setVisibility(View.VISIBLE);
        } else {
            no_contact_textview.setVisibility(View.GONE);
        }

        System.gc();
    }

    public abstract class CustomAsyncTask<T, V, Q> extends AsyncTask<T, V, Q>  {

        public void executeContent(T... content) {
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
                this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, content);
            }
            else {
                this.execute(content);
            }
        }
    }
}
