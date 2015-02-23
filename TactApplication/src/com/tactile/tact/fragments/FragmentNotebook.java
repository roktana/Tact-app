package com.tactile.tact.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStripPlus;
import com.tactile.tact.R;
import com.tactile.tact.services.events.EventDrawerAction;
import com.tactile.tact.services.events.EventFilterContacts;
import com.tactile.tact.services.events.EventHideActivityActionBar;
import com.tactile.tact.services.events.EventOptionButtonPressed;
import com.tactile.tact.views.CustomViewPager;


import de.greenrobot.event.EventBus;

/**
 * Created by ismael on 1/30/15.
 */
public class FragmentNotebook extends FragmentTactBase {
    
    private LinearLayout actionBar;
    private LinearLayout searchIcon;
    private LinearLayout sortIcon;
    private ImageView sortImage;
    private EditText searchEditText;
    private ImageView option_menu_btn;
    private LinearLayout headerLayout;
    private ImageView cancel_search_image;
    private ImageView actionBarMenuImage;
    private LinearLayout searchLinearLayout;
    private LinearLayout actionBar_menu_button_layout;
    private LinearLayout actionBar_option_button_layout;
    private PagerSlidingTabStripPlus tabs;
    private InputMethodManager imm;
    private CustomViewPager notebookPager;
    private TextView no_contact_textview;
    private PopupWindow sort_menu_dropdown;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View notebookView = inflater.inflate(R.layout.notebook_main_layout, container, false);
        EventBus.getDefault().post(new EventHideActivityActionBar());
        actionBar = (LinearLayout) notebookView.findViewById(R.id.notebook_action_bar);
        actionBar.findViewById(R.id.actionBar_layout).setBackgroundColor(getResources().getColor(R.color.action_bar_grey));

        LinearLayout backButton  = (LinearLayout) actionBar.findViewById(R.id.actionBar_menu_button_layout);
        LinearLayout optionButton = (LinearLayout) actionBar.findViewById(R.id.actionBar_option_button_layout);
        TextView actionBarTitle = (TextView) actionBar.findViewById(R.id.actionBar_title_text);

        cancel_search_image = (ImageView) actionBar.findViewById(R.id.close_search_icon);
        option_menu_btn = (ImageView) actionBar.findViewById(R.id.option_menu_btn);
        actionBar_menu_button_layout = (LinearLayout) actionBar.findViewById(R.id.actionBar_menu_button_layout);
        searchLinearLayout = (LinearLayout) actionBar.findViewById(R.id.search_linear_layout);
        searchIcon = (LinearLayout) actionBar.findViewById(R.id.action_bar_secondary_button_1);
        sortIcon = (LinearLayout) actionBar.findViewById(R.id.action_bar_secondary_button_2);
        sortImage = (ImageView) actionBar.findViewById(R.id.imageview_secondary_2);
        sortImage.setImageDrawable(getResources().getDrawable(R.drawable.sort));
        searchEditText = (EditText) actionBar.findViewById(R.id.search_edit_text);
        actionBarMenuImage = (ImageView) actionBar.findViewById(R.id.menu_button);
        headerLayout = (LinearLayout) actionBar.findViewById(R.id.header_linear_layout);
        actionBar_option_button_layout = (LinearLayout) actionBar.findViewById(R.id.actionBar_option_button_layout);
        no_contact_textview = (TextView) notebookView.findViewById(R.id.no_contact_textview);

        
        LinearLayout sortLayout = (LinearLayout)inflater.inflate(R.layout.notebook_sort_menu, null);
        sort_menu_dropdown = new PopupWindow(sortLayout, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        sort_menu_dropdown.setOutsideTouchable(true);
        sort_menu_dropdown.setFocusable(true);

        sortIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sort_menu_dropdown.showAsDropDown(sortIcon);
            }
        });
        
        
        sortIcon.setVisibility(View.VISIBLE);

        actionBarTitle.setText(getResources().getString(R.string.drawer_notebook));

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
                notebookPager.setPagingEnabled(false);
                EventBus.getDefault().post(new EventFilterContacts(s.toString()));
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        notebookPager = (CustomViewPager) notebookView.findViewById(R.id.notebook_list_pager_container);
        tabs = (PagerSlidingTabStripPlus) notebookView.findViewById(R.id.tabs);
        notebookPager.setAdapter(new NotebookPagerAdapter(getActivity().getApplicationContext()));
        
        tabs.setViewPager(notebookPager);

        return notebookView;
    }

    private void clearSearch() {
        searchEditText.setText("");
        tabs.setVisibility(View.VISIBLE);
        no_contact_textview.setVisibility(View.GONE);
        notebookPager.setPagingEnabled(true);
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

    public class NotebookPagerAdapter extends PagerAdapter {

        private final String[] TITLES = {"ALL", "LOGS", "NOTES", "TASKS"};

        LayoutInflater inflater;

        public NotebookPagerAdapter(Context ctx) {
            inflater = LayoutInflater.from(ctx);
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            return getNotebookListView(FragmentNotebook.this.getActivity(), inflater, collection, position);
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
    
    public class NotebookListAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private Context ctx;

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
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
            
            return null;
        }
    }

    /**
     * Set item for a single day
     * @param context Activity context
     * @param inflater inflater service
     * @param collection colection from PageAdapter
     * @return object
     */
    private Object getNotebookListView(Context context, LayoutInflater inflater, ViewGroup collection, int currentPosition){

        ListView scrollView = new ListView(getActivity().getApplicationContext());
        scrollView.setAdapter(new NotebookListAdapter());
        return scrollView;
    }
}


