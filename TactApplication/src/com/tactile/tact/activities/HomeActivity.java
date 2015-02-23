/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tactile.tact.activities;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tactile.tact.R;
import com.tactile.tact.consts.TactConst;
import com.tactile.tact.controllers.TactSharedPrefController;
import com.tactile.tact.database.model.FragmentMoveHandler;
import com.tactile.tact.fragments.FragmentCalendar;
import com.tactile.tact.fragments.FragmentContactDetails;
import com.tactile.tact.fragments.FragmentContactsLazyLoad;
import com.tactile.tact.fragments.FragmentEventDetails;
import com.tactile.tact.fragments.FragmentLogCreate;
import com.tactile.tact.fragments.FragmentLogDetails;
import com.tactile.tact.fragments.FragmentLogList;
import com.tactile.tact.fragments.FragmentNoteCreate;
import com.tactile.tact.fragments.FragmentNoteDetails;
import com.tactile.tact.fragments.FragmentNotebook;
import com.tactile.tact.fragments.FragmentTaskCreate;
import com.tactile.tact.fragments.FragmentTaskDetails;
import com.tactile.tact.services.TactCUDLSyncService;
import com.tactile.tact.services.events.EventCloseDrawer;
import com.tactile.tact.services.events.EventDrawerAction;
import com.tactile.tact.services.events.EventGoBackHomeActivity;
import com.tactile.tact.services.events.EventLogout;
import com.tactile.tact.services.events.EventMoveToFragment;
import com.tactile.tact.services.events.EventNotifyCalendarChanges;
import com.tactile.tact.services.events.EventOptionButtonPressed;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class HomeActivity extends TactBaseActivity {

    private LinearLayout actionBarLayout        = null;
    private static String query = null;

    private DrawerLayout mDrawerLayout;
    private View menuDrawerLinealLayout;

    PopupWindow option_menu_dropdown;

    Point p;

    private LinearLayout mailLayout;
    private LinearLayout calendarLayout;
    private LinearLayout contactsLayout;
    private LinearLayout notebookLayout;
    private LinearLayout opportunitiesLayout;
    private View totalWindowLayout;
    private View option_menu_layout;
    private android.os.Handler mDrawerHandler;

    int offSetX, offSetY, statusBarHeight;

    enum FragmentHandler {
        EMAIL,
        EMAIL_DETAIL,
        CALENDAR,
        CONTACTS,
        NOTES,
        SETTINGS,
        OPPORTUNITIES,
        NOTE_NEW_EDIT,
        NEW_NOTE,
        NOTE_DETAIL,
        NOTE_EDIT,
        NOTEBOOK,
        TASK_DETAILS,
        AGENDA_MEETING_DETAILS,
        TASK_CREATE,
        AGENDA_CONTACT_FINDER,
        LOGGING_NEW_EDIT,
        LOGGING_LIST,
        LOGGING_DETAILS,
        INNER_CONTACTS,
        CONTACT_DETAILS
    }

    HashMap <FragmentHandler, Integer> drawerMenuLayoutDictionary = new HashMap<FragmentHandler, Integer>();

    private Fragment currentFragment;


    //****************************  OVERRIDES  *********************************\\
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupDrawerMenu();
        generateMenu();
//        setupActionBarMenu();
        totalWindowLayout = findViewById(R.id.all_app_frame_layout);
        if (!TactSharedPrefController.isNeedSync()) {
            //Start the sync service when the activity is created,
            //if the service is running already the onStartCommand method will be called again
            //and it's already prepared to handle multiple calls
            Intent intentService = new Intent(this, TactCUDLSyncService.class);
            this.startService(intentService);
        }

        EventBus.getDefault().post(new EventMoveToFragment(TactConst.FRAGMENT_CALENDAR_TAG, 0, 0, null, 0, true));
        drawerSelectedItem(FragmentHandler.CALENDAR);

        mDrawerHandler = new android.os.Handler();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        readLocation();
    }

    private void readLocation() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        offSetX = totalWindowLayout.getMeasuredWidth();
        offSetY = statusBarHeight;

        p = new Point();
        p.x = offSetX;
        p.y = offSetY;
    }

    public void generateMenu() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        option_menu_layout = inflater.inflate(R.layout.option_menu, null);
        option_menu_dropdown = new PopupWindow(option_menu_layout, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        // Closes the popup window when touch outside of it - when looses focus
        option_menu_dropdown.setOutsideTouchable(true);
        option_menu_dropdown.setFocusable(true);

        // Removes default black background
        option_menu_dropdown.setBackgroundDrawable(new BitmapDrawable());

        //Set listeners
        setMenuListeners(option_menu_layout);
    }

    private void setMenuListeners(View view){
        //Settings

        //Notifications

        //Data Sources

        //Send Feedback

        //Account

        //Sign Out
        final LinearLayout sign_out = (LinearLayout) view.findViewById(R.id.option_menu_sign_out_btn);
        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                option_menu_dropdown.setTouchable(false);
                sign_out.setEnabled(false);
                finish();
                EventBus.getDefault().post(new EventLogout());
            }
        });
    }

    public void showDismissOptionPopUpMenu() {
        if (!option_menu_dropdown.isShowing()) {
            option_menu_dropdown.showAtLocation(option_menu_layout, Gravity.NO_GRAVITY, p.x, p.y);
        } else {
            option_menu_dropdown.dismiss();
        }
    }

    public void drawerSelectedItem(FragmentHandler selectedItem) {
        for(Map.Entry<FragmentHandler, Integer> selected: drawerMenuLayoutDictionary.entrySet()) {
            if (selected.getKey().equals(selectedItem)) {
                menuDrawerLinealLayout.findViewById(selected.getValue()).setSelected(true);
            } else {
                menuDrawerLinealLayout.findViewById(selected.getValue()).setSelected(false);
            }
        }
    }

    public void setupDrawerMenu() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        menuDrawerLinealLayout = (LinearLayout) findViewById(R.id.menu_linear_layout);
        mailLayout = (LinearLayout) menuDrawerLinealLayout.findViewById(R.id.drawer_email_field);
        calendarLayout = (LinearLayout) menuDrawerLinealLayout.findViewById(R.id.drawer_calendar_field);
        contactsLayout = (LinearLayout) menuDrawerLinealLayout.findViewById(R.id.drawer_contacts_field);
        notebookLayout = (LinearLayout) menuDrawerLinealLayout.findViewById(R.id.drawer_notebook_field);
        opportunitiesLayout = (LinearLayout) menuDrawerLinealLayout.findViewById(R.id.drawer_opportunities_field);

        drawerMenuLayoutDictionary.put(FragmentHandler.CONTACTS, R.id.drawer_contacts_field);
        drawerMenuLayoutDictionary.put(FragmentHandler.CALENDAR, R.id.drawer_calendar_field);
        drawerMenuLayoutDictionary.put(FragmentHandler.EMAIL, R.id.drawer_email_field);
        drawerMenuLayoutDictionary.put(FragmentHandler.NOTEBOOK, R.id.drawer_notebook_field);
        drawerMenuLayoutDictionary.put(FragmentHandler.OPPORTUNITIES, R.id.drawer_opportunities_field);

        //Set drawer menu username
        TextView username = (TextView) findViewById(R.id.app_username);
        username.setText(TactSharedPrefController.getCredentialUsername());

        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        statusBarHeight = getResources().getDimensionPixelSize(resourceId);

        mailLayout.setEnabled(false);
        opportunitiesLayout.setEnabled(false);

        calendarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerSelectedItem(FragmentHandler.CALENDAR);
                if (mDrawerLayout.isDrawerOpen(menuDrawerLinealLayout)) {
                    mDrawerLayout.closeDrawer(menuDrawerLinealLayout);
                }
                // Clears any previously posted runnables, for double clicks
                mDrawerHandler.removeCallbacksAndMessages(null);
                mDrawerHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(new EventMoveToFragment(TactConst.FRAGMENT_CALENDAR_TAG, 0, 0, null, 0, true));
                    }
                }, 250);
            }
        });

        contactsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                drawerSelectedItem(FragmentHandler.CONTACTS);
                if (mDrawerLayout.isDrawerOpen(menuDrawerLinealLayout)) {
                    mDrawerLayout.closeDrawer(menuDrawerLinealLayout);
                }
                // Clears any previously posted runnables, for double clicks
                mDrawerHandler.removeCallbacksAndMessages(null);
                mDrawerHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(new EventMoveToFragment(TactConst.FRAGMENT_CONTACTS_TAG, 0, 0, null, 0, true));
                    }
                }, 250);


            }
        });


        notebookLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                drawerSelectedItem(FragmentHandler.NOTEBOOK);
                if (mDrawerLayout.isDrawerOpen(menuDrawerLinealLayout)) {
                    mDrawerLayout.closeDrawer(menuDrawerLinealLayout);
                }
                // Clears any previously posted runnables, for double clicks
                mDrawerHandler.removeCallbacksAndMessages(null);
                mDrawerHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(new EventMoveToFragment(TactConst.FRAGMENT_NOTEBOOK_TAG, 0, 0, null, 0, true));
                    }
                }, 250);


            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        checkForCrashes();
        checkForUpdates();

        TactApplication.getInstance().onHomeActivityResumed(this);
    }

    private void checkForCrashes() {
        CrashManager.register(this, "02193abc58c22eaa602d63f68931aa1c");
    }

    private void checkForUpdates() {
        // Remove this for store builds!
        UpdateManager.register(this, "02193abc58c22eaa602d63f68931aa1c");
    }

    @Override
    public void onPause() {
        super.onPause();
        TactApplication.getInstance().onHomeActivityPaused();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    @Override
    public void onBackPressed(){
        processBackFragment();
    }

    /**
     * Go back from current fragment
     */
    private void processBackFragment(){
        FragmentMoveHandler fragmentMoveHandler = TactApplication.getInstance().getFromFragmentBackStack();
        if (fragmentMoveHandler != null) {
            EventBus.getDefault().post(new EventMoveToFragment(fragmentMoveHandler, true));
        }
    }

    //****************************  UTILITIES  *********************************\\
    public void hideActionBar(){
        this.actionBarLayout.setVisibility(View.GONE);
    }

    public void showActionBar(){
        this.actionBarLayout.setVisibility(View.VISIBLE);
    }


    //****************************  UTILITIES  *********************************\\

    /**
     ** @return query the text to filter in search contacts
     */
    public static String getQuery(){
        return query;
    }

    private boolean isFragmentVisible(int fragmentTag){
        return getFragmentManager().findFragmentByTag(String.valueOf(fragmentTag)) != null &&
                getFragmentManager().findFragmentByTag(String.valueOf(fragmentTag)).isVisible();
    }

    public void onEventMainThread(EventGoBackHomeActivity eventGoBackHomeActivity) {
        processBackFragment();
    }

    public void onEventMainThread(EventOptionButtonPressed eventOptionButtonPressed) {
        showDismissOptionPopUpMenu();
    }

    public void onEventMainThread(EventDrawerAction eventDrawerAction) {
        mDrawerLayout.openDrawer(Gravity.LEFT);
    }

    public void onEventMainThread(EventMoveToFragment eventMoveToFragment) {
        
        int enterAnim, exitAnim;
        
        if (eventMoveToFragment != null && eventMoveToFragment.getFragmentMoveHandler() != null) {
            currentFragment =  null;
            switch (eventMoveToFragment.getFragmentMoveHandler().getFragmentTag()) {
                case TactConst.FRAGMENT_CALENDAR_TAG: {
                    TactApplication.getInstance().clearFragmentBackStack();
                    currentFragment = new FragmentCalendar();
                    Bundle bundle = new Bundle();
                    bundle.putInt("position", eventMoveToFragment.getFragmentMoveHandler().getPosition());
                    bundle.putLong("calendar_time", eventMoveToFragment.getFragmentMoveHandler().getTime());
                    bundle.putInt("next_week_range", eventMoveToFragment.getFragmentMoveHandler().getNext_week_range());
                    bundle.putInt("back_week_range", eventMoveToFragment.getFragmentMoveHandler().getBack_week_range());
                    currentFragment.setArguments(bundle);
                    break;
                }
                case TactConst.FRAGMENT_CONTACT_DETAIL_TAG: {
                    currentFragment = new FragmentContactDetails();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("contact", (Serializable) eventMoveToFragment.getFragmentMoveHandler().getObject());
                    currentFragment.setArguments(bundle);
                    break;
                }
                case TactConst.FRAGMENT_CONTACTS_TAG: {
                    TactApplication.getInstance().clearFragmentBackStack();
                    currentFragment = new FragmentContactsLazyLoad();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("contact", (Serializable) eventMoveToFragment.getFragmentMoveHandler().getObject());
                    bundle.putInt("page", eventMoveToFragment.getFragmentMoveHandler().getPage());
                    bundle.putString("filter", eventMoveToFragment.getFragmentMoveHandler().getText());
                    currentFragment.setArguments(bundle);
                    break;
                }
                case TactConst.FRAGMENT_EVENT_DETAIL_TAG: {
                    currentFragment = new FragmentEventDetails();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("event", (Serializable) eventMoveToFragment.getFragmentMoveHandler().getObject());
                    currentFragment.setArguments(bundle);
                    break;
                }
                case TactConst.FRAGMENT_LOG_CREATE_TAG: {
                    currentFragment = new FragmentLogCreate();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("parent", (Serializable) eventMoveToFragment.getFragmentMoveHandler().getObject());
                    currentFragment.setArguments(bundle);
                    break;
                }
                case TactConst.FRAGMENT_LOG_DETAIL_TAG: {
                    currentFragment = new FragmentLogDetails();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("log", (Serializable) eventMoveToFragment.getFragmentMoveHandler().getObject());
                    currentFragment.setArguments(bundle);
                    break;
                }
                case TactConst.FRAGMENT_LOG_LIST_TAG: {
                    currentFragment = new FragmentLogList();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("event", (Serializable) eventMoveToFragment.getFragmentMoveHandler().getObject());
                    bundle.putInt("position", eventMoveToFragment.getFragmentMoveHandler().getPosition());
                    currentFragment.setArguments(bundle);
                    break;
                }
                case TactConst.FRAGMENT_TASK_CREATE_TAG: {
                    currentFragment = new FragmentTaskCreate();
                    if (eventMoveToFragment.getFragmentMoveHandler().getObject() != null){
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("task", (Serializable) eventMoveToFragment.getFragmentMoveHandler().getObject());
                        currentFragment.setArguments(bundle);
                    }
                    break;
                }
                case TactConst.FRAGMENT_TASK_DETAILS_TAG: {
                    currentFragment = new FragmentTaskDetails();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("task", (Serializable) eventMoveToFragment.getFragmentMoveHandler().getObject());
                    currentFragment.setArguments(bundle);
                    break;
                }
                case TactConst.FRAGMENT_NOTE_CREATE_TAG: {
                    currentFragment = new FragmentNoteCreate();
                    if (eventMoveToFragment.getFragmentMoveHandler().getObject() != null){
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("note", (Serializable) eventMoveToFragment.getFragmentMoveHandler().getObject());
                        currentFragment.setArguments(bundle);
                    }
                    break;
                }
                case TactConst.FRAGMENT_NOTE_DETAIL_TAG: {
                    currentFragment = new FragmentNoteDetails();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("note", (Serializable) eventMoveToFragment.getFragmentMoveHandler().getObject());
                    currentFragment.setArguments(bundle);
                    break;
                }
                case TactConst.FRAGMENT_NOTEBOOK_TAG: {
                    currentFragment = new FragmentNotebook();
                    break;
                }
            }
            
            if (!eventMoveToFragment.getFragmentMoveHandler().isPrimary()) {
                if (eventMoveToFragment.isBack()) {
                    enterAnim = R.anim.fragment_slide_right_enter;
                    exitAnim = R.anim.fragment_slide_right_exit;
                } else {
                    enterAnim = R.anim.fragment_slide_left_enter;
                    exitAnim = R.anim.fragment_slide_left_exit;
                }
            } else {
                enterAnim = R.anim.zoom_alpha_in;
                exitAnim = R.anim.zoom_alpha_out;
            }

            //  && !isFragmentVisible(eventMoveToFragment.getFragmentMoveHandler().getFragmentTag())
            if(currentFragment != null){
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(enterAnim, exitAnim)
                        .replace(R.id.fragment_content_linear_layout, currentFragment, String.valueOf(eventMoveToFragment.getFragmentMoveHandler().getFragmentTag())).commit();
            }
        }
    }

    public void onEventMainThread(EventCloseDrawer eventCloseDrawer) {
        if (mDrawerLayout.isDrawerOpen(menuDrawerLinealLayout)) {
            mDrawerLayout.closeDrawer(menuDrawerLinealLayout);
        }
    }

//    public void refreshVisibleFragment() {
//        if (currentFragment != null) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if (currentFragment instanceof  FragmentCalendar)
//                        ((FragmentCalendar) currentFragment).refresh();
//                }
//            });
//        }
//    }

    public void onEventMainThread(EventNotifyCalendarChanges eventNotifyCalendarChanges) {
        EventBus.getDefault().removeStickyEvent(eventNotifyCalendarChanges);
        if (eventNotifyCalendarChanges.hasDataToSync() && currentFragment != null){
            if (currentFragment instanceof FragmentCalendar){
                if (eventNotifyCalendarChanges.hasToSyncEventsOrTasks() && eventNotifyCalendarChanges.getDates() != null){
                    for (Long date: eventNotifyCalendarChanges.getDates()){
                        if (((FragmentCalendar)currentFragment).getCurrentDay() != -1 && ((FragmentCalendar)currentFragment).getCurrentDay() == date){
                            ((FragmentCalendar)currentFragment).refresh();
                        }
                    }
                }
            }
        }
    }


    //****************************  CLASSES  *********************************\\

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TactApplication.getInstance().clearFragmentBackStack();
        TactApplication.unbindDrawables(findViewById(R.id.drawer_layout));

        option_menu_dropdown.dismiss();

        actionBarLayout = null;
        query = null;
        mDrawerLayout = null;
        menuDrawerLinealLayout = null;
        option_menu_dropdown = null;
        p = null;
        mailLayout = null;
        calendarLayout = null;
        contactsLayout = null;
        notebookLayout = null;
        opportunitiesLayout = null;
        totalWindowLayout = null;
        option_menu_layout = null;
        mDrawerHandler = null;

        System.gc();
    }
}
