package com.tactile.tact.fragments;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStripPlus;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.tactile.tact.R;
import com.tactile.tact.consts.TactConst;
import com.tactile.tact.database.DatabaseManager;
import com.tactile.tact.database.TactDataSource;
import com.tactile.tact.database.entities.Contact;
import com.tactile.tact.database.entities.FrozenFeedItem;
import com.tactile.tact.database.model.FeedItemEvent;
import com.tactile.tact.database.model.FeedItemRelatedContact;
import com.tactile.tact.database.model.FeedItemTask;
import com.tactile.tact.database.model.FragmentMoveHandler;
import com.tactile.tact.services.events.EventDrawerAction;
import com.tactile.tact.services.events.EventHideActivityActionBar;
import com.tactile.tact.services.events.EventMoveToFragment;
import com.tactile.tact.services.events.EventOptionButtonPressed;
import com.tactile.tact.utils.CalendarAPI;
import com.tactile.tact.utils.CalendarUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;

/**
 * Created by Ismael on 11/13/14.
 */
public class FragmentCalendar extends FragmentTactBase {

    // Class variables
    private ImageView calendar_previous_month_week;
    private ImageView calendar_next_month_week;
    private TextView calendar_month_name;
    private ViewPager mPager;
    private FloatingActionsMenu multiple_actions_down;
    Calendar calendar;
    private PagerAdapter mPagerAdapter;
    Integer position = null;
    private Long todayMillis;
    private Long tomorrowMillis;
    private Long yesterdayMillis;
    private FloatingActionButton newLog;
    private FloatingActionButton newTask;
    private LinearLayout actionBar;
    private PagerSlidingTabStripPlus calendar_tabs;
    private LinearLayout dayContainer;
    private LinearLayout.LayoutParams dayTabLayoutParams;
    private ArrayList<Long> currentWeek;

    private int scrollPosition = 0;
    private int back_week_range = 2;
    private int next_week_range = 2;

    // Week days
    private static final String[] daysOfTheWeek = {"S", "M", "T", "W", "T", "F", "S"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        calendar = Calendar.getInstance();

        // Set tomorrow and yesterday
        todayMillis = CalendarAPI.getDateOnly(calendar.getTimeInMillis());

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        tomorrowMillis = CalendarAPI.getDateOnly(calendar.getTimeInMillis());

        calendar.add(Calendar.DAY_OF_YEAR, -2);
        yesterdayMillis = CalendarAPI.getDateOnly(calendar.getTimeInMillis());

        // Reset today time
        calendar.add(Calendar.DAY_OF_YEAR, 1);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.getLong("calendar_time") > 0) {
                position = bundle.getInt("position");
                calendar.setTimeInMillis(bundle.getLong("calendar_time"));
                back_week_range = bundle.getInt("back_week_range");
                next_week_range = bundle.getInt("next_week_range");
            }
        }


        View calendarView = inflater.inflate(R.layout.calendar_layout, container, false);
        EventBus.getDefault().post(new EventHideActivityActionBar());
        actionBar = (LinearLayout) calendarView.findViewById(R.id.calendar_action_bar);
        actionBar.findViewById(R.id.actionBar_layout).setBackgroundColor(getResources().getColor(R.color.action_bar_grey));

        LinearLayout backButton  = (LinearLayout) actionBar.findViewById(R.id.actionBar_menu_button_layout);
        LinearLayout optionButton = (LinearLayout) actionBar.findViewById(R.id.actionBar_option_button_layout);
        TextView actionBarTitle = (TextView) actionBar.findViewById(R.id.actionBar_title_text);
        actionBar.findViewById(R.id.action_bar_secondary_button_1).setVisibility(View.GONE);
        actionBarTitle.setText(getResources().getString(R.string.calendar));

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
        
        dayTabLayoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
        calendar_previous_month_week = (ImageView)calendarView.findViewById(R.id.calendar_previous_month_week);
        calendar_next_month_week = (ImageView)calendarView.findViewById(R.id.calendar_next_month_week);
        calendar_month_name = (TextView)calendarView.findViewById(R.id.calendar_month_name);
        mPager = (ViewPager) calendarView.findViewById(R.id.calendar_day_pager_container);
        multiple_actions_down = (FloatingActionsMenu) calendarView.findViewById(R.id.multiple_actions_down);
        newLog = (FloatingActionButton) multiple_actions_down.findViewById(R.id.new_log_floating_btn);
        newTask = (FloatingActionButton) multiple_actions_down.findViewById(R.id.new_task_floating_btn);


        currentWeek = CalendarUtils.getWeekArray(calendar.getTimeInMillis());

        calendar_month_name.setText((new SimpleDateFormat("MMM yyyy")).format(calendar.getTime()));

        newLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackStack(getFragmentMoveHandler(position != null ? position : 0, getCurrentDay()));
                EventBus.getDefault().post(new EventMoveToFragment(TactConst.FRAGMENT_LOG_CREATE_TAG, 0, 0, null, 0));
            }
        });

        newTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackStack(getFragmentMoveHandler(position != null ? position : 0, getCurrentDay()));
                EventBus.getDefault().post(new EventMoveToFragment(TactConst.FRAGMENT_TASK_CREATE_TAG, 0, 0, null, 0));
            }
        });

        if (DatabaseManager.getAccountId("salesforce") == null || DatabaseManager.getAccountId("salesforce").equals("")) {
            multiple_actions_down.setVisibility(View.GONE);
        }
        calendar_tabs = (PagerSlidingTabStripPlus) calendarView.findViewById(R.id.calendar_tabs);
        dayContainer = (LinearLayout) calendarView.findViewById(R.id.day_container);
        
        buildDaysViews();

        mPagerAdapter = new CalendarDayPagerAdapter(inflater, currentWeek);
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(currentWeek.indexOf(CalendarUtils.getDateOnly(calendar.getTimeInMillis())));

        ViewPager.OnPageChangeListener pageListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                switchDay(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        };
        
        mPager.setOnPageChangeListener(pageListener);

        calendar_tabs.setViewPager(mPager);
        calendar_tabs.setOnPageChangeListener(pageListener);
        dayContainer.getChildAt(mPager.getCurrentItem()).setSelected(true);
        deselectDayTab(mPager.getCurrentItem());
        paintOtherMonthTabs();
        todayTabControl(currentWeek.indexOf(CalendarUtils.getDateOnly(calendar.getTimeInMillis())));

        if (next_week_range > 0) {
            calendar_next_month_week.setVisibility(View.VISIBLE);
            calendar_next_month_week.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    next_week_range --;
                    if (back_week_range < 4){
                        back_week_range ++;
                    }
                    switchWeek(7, currentWeek.get(mPager.getCurrentItem()));
                    refreshCalendarMovements();
                }
            });
        }
        else {
            calendar_next_month_week.setVisibility(View.INVISIBLE);
        }

        if (back_week_range > 0) {
            calendar_previous_month_week.setVisibility(View.VISIBLE);
            calendar_previous_month_week.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    back_week_range --;
                    if (next_week_range < 4){
                        next_week_range ++;
                    }
                    switchWeek(-7, currentWeek.get(mPager.getCurrentItem()));
                    refreshCalendarMovements();
                }
            });
        }
        else {
            calendar_previous_month_week.setVisibility(View.INVISIBLE);
        }
        

        if (position != null){
            switchDay(position);
        }

//        EventBus.getDefault().post(new EventCloseDrawer());

        
        return calendarView;
    }

    private void refreshCalendarMovements(){
        if (next_week_range > 0){
            calendar_next_month_week.setVisibility(View.VISIBLE);
        }
        else if (next_week_range == 0){
            calendar_next_month_week.setVisibility(View.GONE);
        }
        if (back_week_range > 0){
            calendar_previous_month_week.setVisibility(View.VISIBLE);
        }
        else if (back_week_range == 0){
            calendar_previous_month_week.setVisibility(View.GONE);
        }
    }
    
    public Long getCurrentDay() {
        return currentWeek.get(mPager.getCurrentItem());
    }

    public void refresh() {
        if (mPagerAdapter != null) {
            final View scroll = mPager.findViewWithTag("scroll_id" + mPager.getCurrentItem());
            if (scroll != null && scroll instanceof ScrollView) {
                scrollPosition = ((ScrollView)scroll).getScrollY();
            }
            mPagerAdapter.notifyDataSetChanged();
            if (scroll != null && scroll instanceof ScrollView) {
                scroll.post(new Runnable() {
                    @Override
                    public void run() {
                        scroll.scrollTo(scrollPosition, scrollPosition);
                    }
                });
            }
        }
    }

    private FragmentMoveHandler getFragmentMoveHandler(int position, long time) {
        FragmentMoveHandler fragmentMoveHandler = new FragmentMoveHandler();
        fragmentMoveHandler.setFragmentTag(TactConst.FRAGMENT_CALENDAR_TAG);
        fragmentMoveHandler.setPosition(position);
        fragmentMoveHandler.setTime(time);
        fragmentMoveHandler.setBack_week_range(back_week_range);
        fragmentMoveHandler.setNext_week_range(next_week_range);
        return  fragmentMoveHandler;
    }
    
    public void paintOtherMonthTabs() {
        for (int i=0; i<daysOfTheWeek.length; i++) {
                if (CalendarUtils.getMonthByTimestamp(CalendarUtils.getMonthOfWeek(currentWeek)) != CalendarUtils.getMonthByTimestamp(currentWeek.get(i))) {
                    calendar_tabs.getTextViewTitleTabObjectAtPosition(i).setTextColor(getResources().getColorStateList(R.color.tab_text_deactivated_color));
                } else if (currentWeek.get(i).equals(todayMillis)) {
                    calendar_tabs.addSubtitleAtTab(i, getResources().getString(R.string.today));
                    ((TextView)calendar_tabs.getSingleTabLayoutAtPosition(i).getChildAt(1)).setTextSize(12f);
                }
        }
    }

    private void switchWeek(int addWeek, Long currentDay) {
        calendar.setTimeInMillis(currentDay);
        calendar.add(Calendar.DAY_OF_YEAR, addWeek);

        currentWeek = CalendarUtils.getWeekArray(calendar.getTimeInMillis());
        ((CalendarDayPagerAdapter)mPager.getAdapter()).refresh();

        calendar_month_name.setText((new SimpleDateFormat("MMM yyyy")).format(CalendarUtils.getMonthOfWeek(currentWeek)));
        paintOtherMonthTabs();
    }

    private void switchDay(int newPosition) {
        deselectDayTab(newPosition);
        todayTabControl(newPosition);
    }
    
    public void todayTabControl(int pos) {
        if (currentWeek.get(pos).equals(todayMillis)) {
            calendar_tabs.getSingleTabLayoutAtPosition(pos).getChildAt(1).setVisibility(View.INVISIBLE);
        } else {
            if (currentWeek.indexOf(todayMillis) != -1) {
                calendar_tabs.getSingleTabLayoutAtPosition(currentWeek.indexOf(todayMillis)).getChildAt(1).setVisibility(View.VISIBLE);
            }
        }
    }
    
    public void buildDaysViews() {
        dayContainer.removeAllViews();
        for (int i=0; i<daysOfTheWeek.length; i++) {
            TextView day = new TextView(getActivity().getApplicationContext());
            day.setText(daysOfTheWeek[i].toUpperCase());
            day.setGravity(Gravity.CENTER);
            day.setSingleLine();
            day.setFocusable(true);
            day.setOnClickListener(dayTabListener(i));
            day.setPadding(0,0,0,0);
            day.setLayoutParams(dayTabLayoutParams);
            day.setTextColor(getResources().getColorStateList(R.color.tab_text_aesthetic));
            
            dayContainer.addView(day, i);
        }
    }
    
    public View.OnClickListener dayTabListener(final int position) {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPager.setCurrentItem(position);
            }
        };
        
        return clickListener;
    }
    
    public void deselectDayTab(int position) {
        for (int i = 0; i<daysOfTheWeek.length; i++) {
            if (i!=position) {
                dayContainer.getChildAt(i).setSelected(false);
                ((TextView)dayContainer.getChildAt(i)).setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
            } else {
                dayContainer.getChildAt(i).setSelected(true);
                ((TextView)dayContainer.getChildAt(i)).setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
            }
        }
    }

    public class CalendarDayPagerAdapter extends PagerAdapter {
        
        private LayoutInflater inflater;
        private ArrayList<Long> weekDays;

        public CalendarDayPagerAdapter(LayoutInflater inflater, ArrayList<Long> week) {
            this.inflater = inflater;
            weekDays = week;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            return getAgendaForDay(FragmentCalendar.this.getActivity(), inflater, collection, position);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) { return view == object; }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return String.valueOf(CalendarUtils.getDayByTimestamp(weekDays.get(position)));
        }

        @Override
        public int getCount() {
            return weekDays.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }
        
        public void refresh() {
            if (currentWeek != null) {
                this.weekDays = currentWeek;
            }
            calendar_tabs.notifyDataSetChanged();
            this.notifyDataSetChanged();
        }
        
    }

    /**
     * Set item for a single day
     * @param context Activity context
     * @param inflater inflater service
     * @param collection colection from PageAdapter
     * @return object
     */
    private Object getAgendaForDay(Context context, LayoutInflater inflater, ViewGroup collection, int position){

        FrameLayout.LayoutParams layoutParms = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);

        //We need to create a ScrollView just for days
        ScrollView myScrollView = new ScrollView(context);
        myScrollView.setLayoutParams(layoutParms);
        myScrollView.setTag("scroll_id" + position);

        //Switch type of day to set texts

        //Now, inflate the agenda day item layout
        View myView = inflater.inflate(R.layout.calendar_body, null);
        if(myView != null){

            final TextView day_text = (TextView) myView.findViewById(R.id.calendar_day_text);
            TextView calendar_day_text = (TextView) myView.findViewById(R.id.calendar_day_text);

            TextView day_legend = (TextView) myView.findViewById(R.id.calendar_day_legend);

            Long dayPosition = currentWeek.get(position);

            if (dayPosition.equals(todayMillis)) {
                calendar_day_text.setText(getResources().getString(R.string.today));
            } else if (dayPosition.equals(tomorrowMillis)) {
                calendar_day_text.setText(getResources().getString(R.string.tomorrow));
            } else if (dayPosition.equals(yesterdayMillis)) {
                calendar_day_text.setText(getResources().getString(R.string.yesterday));
            } else {
                calendar_day_text.setText("");
            }
            
            day_legend.setText((new SimpleDateFormat("EE, MMM dd, yyyy")).format(dayPosition));

            addTasksAndEvents(myView, inflater, position);

            myScrollView.addView(myView);
            collection.addView(myScrollView);
        }
        return myScrollView;
    }

    private void addTasksAndEvents(View view, LayoutInflater inflater, int position) {

        Long dates = currentWeek.get(position);

        //Set List of Tasks
        LinearLayout tasks = (LinearLayout) view.findViewById(R.id.calendar_tasks_container);
        tasks.removeAllViews();
//        for (int z=0; z<tasks_list.getChildCount(); z++) {
//            tasks_list.getChildAt(z).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    setBackStack(getFragmentMoveHandler(0, 0));
//                    EventBus.getDefault().post(new EventMoveToFragment(TactConst.FRAGMENT_TASK_DETAILS_TAG, 0, 0, null, 0));
//                }
//            });
//        }

        //Set List of Meetings
        LinearLayout meetings = (LinearLayout) view.findViewById(R.id.calendar_meetings_content);
        meetings.removeAllViews();

        int count_meetings = 0;
        int count_tasks = 0;
        ArrayList<FrozenFeedItem> calendarItems = TactDataSource.getFeedItemTactAgendaItem(new Long[]{dates});
        for (FrozenFeedItem ci : calendarItems) {
            //MEETING
            if (ci.getType().contains("Event")) {
                meetings.addView(setMeetingView((FeedItemEvent) ci.getFeedItem(), inflater, position));
                count_meetings++;
            }
            //TASKS
            else if (ci.getType().contains("Task")) {
                tasks.addView(setTaskView((FeedItemTask) ci.getFeedItem(), inflater, position));
                count_tasks++;
            }
        }
        String resumeStr = "";
        if (count_meetings + count_tasks > 0) {
            resumeStr = "You have ";
            if (count_meetings > 0 && count_tasks > 0) {
                resumeStr += Integer.toString(count_tasks) + " task and " + Integer.toString(count_meetings) + " meetings today";
            } else if (count_tasks > 0) {
                resumeStr += Integer.toString(count_tasks) + " task today";
            } else if (count_meetings > 0) {
                resumeStr += Integer.toString(count_meetings) + " meetings today";
            }
        }
        TextView resume = (TextView) view.findViewById(R.id.task_meeting_resume);
        resume.setText(resumeStr);

        if (count_tasks == 0) {
            tasks.setVisibility(View.GONE);
        }
    }

    private View setTaskView(final FeedItemTask task, LayoutInflater inflater, final int position) {
        View task_item = inflater.inflate(R.layout.task_item_layout, null, false);
        final CheckBox checkBox = (CheckBox) task_item.findViewById(R.id.task_checkbox);
        final TextView subject = (TextView) task_item.findViewById(R.id.task_subject);
        TextView contactRelated = (TextView) task_item.findViewById(R.id.task_contact);

        checkBox.setChecked(task.getCompleted() != null && task.getCompleted());
        if (checkBox.isChecked()){
            subject.setPaintFlags(subject.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else {
            subject.setPaintFlags(subject.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
        }
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task.setCompleted(checkBox.isChecked());
                task.updateOnDB();

                if (checkBox.isChecked()) {
                    subject.setPaintFlags(subject.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
                else {
                    subject.setPaintFlags(subject.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                }
            }
        });

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

        task_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackStack(getFragmentMoveHandler(position, getCurrentDay()));
                EventBus.getDefault().post(new EventMoveToFragment(TactConst.FRAGMENT_TASK_DETAILS_TAG, 0, 0, task, 0));
            }
        });
        return task_item;
    }

    private View setMeetingView(final FeedItemEvent event, LayoutInflater inflater, final int position){
        View meeting_item               = inflater.inflate(R.layout.event_item_layout, null, false);
        LinearLayout event_container    = (LinearLayout) meeting_item.findViewById(R.id.event_container);
        TextView meeting_start_hour     = (TextView) meeting_item.findViewById(R.id.event_start_hour);
        TextView meeting_duration       = (TextView) meeting_item.findViewById(R.id.event_duration);
        TextView meeting_contact_name   = (TextView) meeting_item.findViewById(R.id.event_contact_name);
        TextView meeting_contact_company= (TextView) meeting_item.findViewById(R.id.event_contact_company);
        TextView meeting_organizer      = (TextView) meeting_item.findViewById(R.id.event_organizer);
        Long now = Calendar.getInstance().getTimeInMillis();

        meeting_start_hour.setText(CalendarAPI.getDate(event.getStartAt()).split("- ")[1]);

        if (event.isAllDay() ||
                (event.getEndAt() != null && event.getStartAt() != null && event.getStartAt().equals(event.getEndAt())) //Salseforce All day
                ){
            meeting_duration.setText("All day");
            meeting_start_hour.setVisibility(View.GONE);
        }
        else {
            if (event.getEndAt() != null){
                Long time = TimeUnit.MILLISECONDS.toMinutes(event.getEndAt() - event.getStartAt());
                if (time <= 59){
                    meeting_duration.setText(Long.toString(time) + " mins");
                }
                else {
                    meeting_duration.setText(time%60 > 30 ?  Long.toString(time/60 + 1) + " h" : Long.toString(time/60) + " h");
                }
            }
            else {
                meeting_duration.setText("");
            }

        }

        if (event.isRecurrence()) {
            event_container.findViewById(R.id.recurring_icon_layout).setVisibility(View.VISIBLE);
        } else {
            event_container.findViewById(R.id.recurring_icon_layout).setVisibility(View.GONE);
        }


        meeting_contact_name.setText(event.getSubject());

        meeting_contact_company.setText(event.getLocation());

        FeedItemRelatedContact contact = event.getOrganizerToShow();
        if (contact != null) {
            if (contact.getContact() != null) {
                meeting_organizer.setText(contact.getContact().getDisplayName());
            } else if (contact.getName() != null) {
                meeting_organizer.setText(contact.getName());
            } else {
                meeting_organizer.setText(contact.getEmail() != null ? contact.getEmail() : "");
            }
        }
        else {
            meeting_organizer.setVisibility(View.GONE);
        }



        meeting_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBackStack(getFragmentMoveHandler(position, getCurrentDay()));
                EventBus.getDefault().post(new EventMoveToFragment(TactConst.FRAGMENT_EVENT_DETAIL_TAG, 0, 0, event, 0));
            }
        });
        return meeting_item;
    }
}
