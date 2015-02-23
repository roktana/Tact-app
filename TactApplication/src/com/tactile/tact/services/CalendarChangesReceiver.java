package com.tactile.tact.services;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.google.common.collect.Sets;
import com.tactile.tact.activities.TactApplication;
import com.tactile.tact.controllers.TactSharedPrefController;
import com.tactile.tact.database.DatabaseManager;
import com.tactile.tact.database.TactDataSource;
import com.tactile.tact.database.entities.FrozenFeedItem;
import com.tactile.tact.database.model.FeedItem;
import com.tactile.tact.database.model.FeedItemEvent;
import com.tactile.tact.services.events.EventNotifyCalendarChanges;
import com.tactile.tact.utils.CalendarAPI;
import com.tactile.tact.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.greenrobot.event.EventBus;

/**
 * Created by sebafonseca on 12/5/14.
 */
public class CalendarChangesReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Utils.Log("Local Event Changes...");
        new AsyncUpdateDBCalendarEvents().execute();
    }

    public class AsyncUpdateDBCalendarEvents extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... param) {
            try {

                long current_time = Calendar.getInstance().getTimeInMillis();
                long two_weeks_milliseconds = TactDataSource.TactDataSourceConstants.MILLISECONDS_IN_A_DAY*14;
                String source_name = "tactapp";
                if (Utils.existsDatabase("db.sqlite")) {
                    String source_id = DatabaseManager.getAccountId(source_name);

                    //Get all Local Calendar Events
                    List<Long> local_events;
                    ArrayList<String> calendarOnboardingIds = TactSharedPrefController.getOnboardingCalendarIds();
                    if (calendarOnboardingIds.size() == 0){
                        calendarOnboardingIds = CalendarAPI.getCalendarIds();
                    }

                    if (calendarOnboardingIds.size() > 0) { //if we have the calendars ids in shared preferences, use it
                        ArrayList<Long> dates = new ArrayList<Long>();
                        local_events = CalendarAPI.getCalendarEventsIds(getIds(calendarOnboardingIds), Long.toString(current_time - two_weeks_milliseconds),
                                Long.toString(current_time + two_weeks_milliseconds), false, false);

                        //Get all Local Synced Calendar Events
                        ArrayList<FeedItemEvent> db_feed_events = TactDataSource.getLocalFeedItemTactAgendaItem(current_time - two_weeks_milliseconds,
                                current_time + two_weeks_milliseconds, Integer.valueOf(source_id));
                        List<Long> db_events = new ArrayList<Long>();
                        for (FeedItemEvent item : db_feed_events) {
                            db_events.add(item.getAndroidEventId());
                        }

                        Set<Long> set_local_events = new HashSet<Long>(local_events);
                        Set<Long> set_db_events = new HashSet<Long>(db_events);

                        //Added     ---> (set local events) - (set db events)
                        Set<Long> added = Sets.difference(set_local_events, set_db_events);
                        if (added.size() > 0) {
                            ArrayList<FeedItemEvent> feedItemEvents = CalendarAPI.getCalendarEvents(getIds(added), source_name, Integer.valueOf(source_id));
                            for (FeedItemEvent item : feedItemEvents) {
                                FrozenFeedItem feedItem = new FrozenFeedItem();
                                feedItem.setFeedItem(item, 1);
                                if (!feedItem.itemExist()) {
                                    feedItem.setId(TactApplication.daoSession.insert(feedItem));
                                    feedItem.updateRelations();
                                    if (!dates.contains(CalendarAPI.getDateOnly(feedItem.getTimestamp()))){
                                        dates.add(CalendarAPI.getDateOnly(feedItem.getTimestamp()));
                                    }
                                }
                            }
                        }

                        ArrayList<FeedItemEvent> events_updated = CalendarAPI.getCalendarEventList(source_name, Integer.valueOf(source_id),
                                TactApplication.getInstance().getApplicationContext(), CalendarAPI.QueryFilter.ByUpdatedEvents, null);
                        for (FeedItemEvent event: events_updated){
                            if (event.isRecurrence()){
                                ArrayList<FeedItemEvent> db_updated_events = TactDataSource.getRecurrentFeedItemEvent(event.getTimestamp());
                                for (FeedItemEvent db_event: db_updated_events){
                                    if (db_event.getAndroidCalendarId().equals(event.getAndroidCalendarId()) &&
                                            db_event.getAndroidEventId().equals(event.getAndroidEventId())){
                                        FrozenFeedItem feedItem = db_event.getParent();
                                        feedItem.setFeedItem(event, 1);
                                        TactApplication.daoSession.update(feedItem);
                                        feedItem.updateRelations();
                                        if (!dates.contains(CalendarAPI.getDateOnly(feedItem.getTimestamp()))){
                                            dates.add(CalendarAPI.getDateOnly(feedItem.getTimestamp()));
                                        }
                                    }
                                }
                            }
                            else {
                                FrozenFeedItem feedItem = TactDataSource.getLocalEventFromDatabase(event);
                                if (feedItem != null) {
                                    feedItem.setFeedItem(event, 1);
                                    TactApplication.daoSession.update(feedItem);
                                    feedItem.updateRelations();
                                    if (!dates.contains(CalendarAPI.getDateOnly(feedItem.getTimestamp()))){
                                        dates.add(CalendarAPI.getDateOnly(feedItem.getTimestamp()));
                                    }
                                }
                            }

                            if (dates.size() > 0){
                                EventNotifyCalendarChanges notify = new EventNotifyCalendarChanges(dates);
                                notify.setSyncEventsOrTasks(true);
                                EventBus.getDefault().postSticky(notify);
                            }
                        }

                        //Deleted   ---> (set db contacts) - (set local contacts)
                        Set<Long> deleted = Sets.difference(set_db_events, set_local_events);
                        ArrayList<Long> deleted_local_db = CalendarAPI.getCalendarEventsIds(getIds(calendarOnboardingIds),
                                Long.toString(current_time - two_weeks_milliseconds),
                                Long.toString(current_time + two_weeks_milliseconds), false, true);
                        deleted = Sets.union(deleted, new HashSet<Long>(deleted_local_db));
                        if (deleted.size() > 0) {
                            Utils.Log(Integer.toString(deleted.size()) + " events need to be deleted....");
                        }
                        /*if (deleted.size() > 0){
                            for (FeedItemEvent item: db_feed_events){
                                if (deleted.contains(item.getAndroidEventId().longValue())){
                                    FrozenFeedItem feedItem = item.getParent();
                                    feedItem.setIsDeleted(1);
                                    feedItem.setFeedItem(item, 1);
                                    TactApplication.daoSession.update(feedItem);
                                    feedItem.updateRelations();
                                }
                            }
                        }*/
                    }
                }


            } catch (Exception e) {
                //Utils.Log(e.getMessage());
            }
            return null;
        }

        private String[] getIds(Set<Long> ids){
            String[] elems = new String[ids.size()];
            int i = 0;
            for (Long id : ids){
                elems[i] = id.toString();
                i++;
            }
            return elems;
        }

        private String[] getIds(ArrayList<String> ids){
            String[] elems = new String[ids.size()];
            int i = 0;
            for (String id: ids){
                elems[i] = id;
                i++;
            }
            return elems;
        }

    }

}
