package com.tactile.tact.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import com.tactile.tact.activities.TactApplication;
import com.tactile.tact.database.TactDataSource;
import com.tactile.tact.database.model.Contact;
import com.tactile.tact.database.model.EmailName;
import com.tactile.tact.database.model.FeedItemEvent;
import com.tactile.tact.database.model.FeedItemMetadata;

import net.hockeyapp.android.utils.Util;

import org.joda.time.DateTimeZone;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalendarAPI {

    //****************************  STATIC CRUD ITEMS *****************************************\\

    // Calendar columns for the calendar queries
    public static String[] CALENDAR_PROJECTION = new String[] {
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.OWNER_ACCOUNT,
            CalendarContract.Calendars.CALENDAR_COLOR
    };

    // Event columns for event queries
    public static String [] EVENT_PROJECTION = new String[]{
            CalendarContract.Events.CALENDAR_ID,
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DESCRIPTION,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.EVENT_LOCATION,
            CalendarContract.Events.ORGANIZER,
            CalendarContract.Events.HAS_ALARM,
            CalendarContract.Events.DURATION,
            CalendarContract.Events.STATUS,
            CalendarContract.Events.ALL_DAY,
            CalendarContract.Events.CALENDAR_DISPLAY_NAME,
            CalendarContract.Events.LAST_SYNCED,
            CalendarContract.Events.EVENT_TIMEZONE,
            CalendarContract.Events.RRULE,
            //CalendarContract.Attendees.ATTENDEE_EMAIL, //??

    };

    // Calendar columns for the calendar queries
    public static String[] ATENDEE_PROJECTION = new String[] {
            CalendarContract.Attendees._ID,
            CalendarContract.Attendees.EVENT_ID,
            CalendarContract.Attendees.ATTENDEE_NAME,
            CalendarContract.Attendees.ATTENDEE_EMAIL
    };

    public static String[] INSTANCES_PROJECTION = new String[] {
            CalendarContract.Instances.EVENT_ID,
            CalendarContract.Instances.DTEND,
            CalendarContract.Instances.DTSTART,
            CalendarContract.Instances.BEGIN,
            CalendarContract.Instances.END,
    };

    // Enum to determinate the 'where' of the   query
    public static enum QueryFilter{
        ByCalendarId,
        ByEventId,
        ByOwnerName,
        ByContactId,
        ByContactNames,
        ByUpdatedEvents,
    }

    /**
     * Calendar object inner class
     */
    public static class CalendarInfo {
        private String calendarName;
        private long id;
        private String accountName;
        private String ownerAccount;
        private boolean isPhoneCalendar;
        private int color;

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public boolean isPhoneCalendar() {
            return isPhoneCalendar;
        }

        private CalendarInfo() {
        }

        public String getCalendarName() {
            return calendarName;
        }

        public void setCalendarName(String calendarName) {
            this.calendarName = calendarName;
        }

        public long getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public CalendarInfo(String calendarName, long id, String accountName, String ownerAccount, int color) {
            this.calendarName = calendarName;
            this.id = id;
            this.accountName = accountName;
            this.ownerAccount = ownerAccount;
            this.color = color;
            if (this.ownerAccount.equals("phone")) {
                this.isPhoneCalendar = true;
            } else {
                this.isPhoneCalendar = false;
            }
        }
    }

    /**
     * Atendee object inner class
     */
    public static class AttendeeInfo {
        private long eventId;
        private String name;
        private String email;

        public long getEventId() {
            return eventId;
        }

        public void setEventId(long eventId) {
            this.eventId = eventId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

    }

    //****************************  CALENDAR CRUD *****************************************\\

    /**
     * Returns a CalendarInfo ArrayList; each object contains all of calendar info
     * @param ctx
     * @param filter
     * @param args
     * @return
     */
    public static ArrayList<CalendarInfo> getCalendarList(Context ctx, QueryFilter filter,  String [] args ) {

        Cursor calendarCursor = getCursor(ctx, CalendarContract.Calendars.CONTENT_URI, filter, CALENDAR_PROJECTION, args);
        ArrayList<CalendarInfo> calendarList = new ArrayList<CalendarInfo>();

        ArrayList<Long> ids = new ArrayList<Long>();

        while (calendarCursor.moveToNext()) {

            // Get the field values
            long calID = calendarCursor.getLong(0);
            if (!ids.contains((calID))) {
                ids.add(calID);
                String displayName = calendarCursor.getString(1);
                String accountName = calendarCursor.getString(2);
                String ownerName = calendarCursor.getString(3);
                int color = Integer.parseInt(calendarCursor.getString(4));

                calendarList.add(new CalendarInfo(
                        displayName,
                        calID,
                        accountName,
                        ownerName,
                        color
                ));
            }
        }

        return calendarList;
    }


    public static ArrayList<String> getCalendarIds() {

        Cursor calendarCursor = getCursor(TactApplication.getInstance().getApplicationContext(), CalendarContract.Calendars.CONTENT_URI, null, CALENDAR_PROJECTION, null);
        ArrayList<String> calendarList = new ArrayList<String>();

        while (calendarCursor.moveToNext()) {
            calendarList.add(calendarCursor.getString(0));
        }

        return calendarList;
    }

    //****************************  CALENDAR EVENTS CRUD  *********************************\\

    /**
     * Gets all events added in the android smartphone
     *
     * @param ctx
     * @return
     */
    public static ArrayList<FeedItemEvent> getCalendarEventList(String sourceName, Integer sourceId, Context ctx, QueryFilter filter, String [] selectionArgs) {
        ArrayList<FeedItemEvent> eventList = new ArrayList<FeedItemEvent>();

        Cursor cursor = getCursor(ctx, CalendarContract.Events.CONTENT_URI, filter, EVENT_PROJECTION, selectionArgs);

        long current_time = Calendar.getInstance().getTimeInMillis();
        long two_weeks_milliseconds = TactDataSource.TactDataSourceConstants.MILLISECONDS_IN_A_DAY*14;

        while (cursor.moveToNext()) {
            FeedItemEvent event = getEvent(sourceName, sourceId, ctx, cursor);

            if (event.isRecurrence()){
                Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI
                        .buildUpon();
                ContentUris.appendId(eventsUriBuilder, current_time - two_weeks_milliseconds);
                ContentUris.appendId(eventsUriBuilder, current_time + two_weeks_milliseconds);
                Uri eventsUri = eventsUriBuilder.build();

                Cursor cursorRecurrence = TactApplication.getInstance().getContentResolver().query(
                        eventsUri,
                        INSTANCES_PROJECTION,
                        "Events._id = ?",
                        new String[]{event.getAndroidEventId().toString()},
                        null
                );

                while (cursorRecurrence.moveToNext()) {
                    eventList.add(createRecurrentEvent(event, cursorRecurrence.getString(0), sourceId, sourceName,
                            cursorRecurrence.getString(3), cursorRecurrence.getString(4)));
                }
                cursorRecurrence.close();
            }
            else {
                eventList.add(event);
            }
        }
        cursor.close();
        return eventList;
    }

    private static FeedItemEvent createRecurrentEvent(FeedItemEvent oldEvent, String newId, Integer sourceId, String sourceName, String startAt, String endAt){
        FeedItemEvent event = new FeedItemEvent();
        String remoteId = TactDataSource.getNewRemoteId();
        event.setServerId(TactDataSource.generateFeedItemId(sourceId.toString(), remoteId));
        event.setRemoteId(remoteId);
        event.setSourceType(sourceName);
        event.setSourceId(sourceId);
        event.setVersion(oldEvent.getVersion());
        event.setTimestamp(Long.parseLong(startAt));
        FeedItemMetadata metadata = new FeedItemMetadata();
        metadata.setCreatedAt(oldEvent.getMetadata().getCreatedAt());
        metadata.setUpdatedAt(oldEvent.getMetadata().getUpdatedAt());
        metadata.setIsActivity(true);
        metadata.setPushedSources(new ArrayList<Integer>());
        metadata.setPushToSources(new ArrayList<Integer>());
        metadata.setUnpushFromSources(new ArrayList<Integer>());
        metadata.setRelatedItems(new ArrayList<String>());
        metadata.setType("Event");
        metadata.setRelatedContacts(new ArrayList<String>());
        metadata.setOpportunityId(null);
        event.setMetadata(metadata);
        event.setAllDay(oldEvent.isAllDay());
        event.setAndroidCalendarId(oldEvent.getAndroidCalendarId());
        event.setAndroidEventId(Long.parseLong(newId));
        event.setDescription(oldEvent.getDescription());
        event.setDueAt(oldEvent.getDueAt());
        Map<String, EmailName> emailNames = new HashMap<String, EmailName>();
        for(String key: oldEvent.getEmailNames().keySet()){
            emailNames.put(key, oldEvent.getEmailNames().get(key));
        }
        event.setEmailNames(emailNames);
        ArrayList<String> invitedEmails = new ArrayList<String>();
        for (String email: oldEvent.getInvitedEmails()){
            invitedEmails.add(email);
        }
        event.setInvitedEmails(invitedEmails);
        event.setEndAt(endAt != null ? Long.parseLong(endAt) : null);
        event.setLocation(oldEvent.getLocation());
        event.setOrganizerEmail(oldEvent.getOrganizerEmail());
        event.setRecurrence(true);
        event.setAndroidRecurrence(oldEvent.getAndroidRecurrence());
        event.setStartAt(Long.parseLong(startAt));
        event.setSubject(oldEvent.getSubject());
        event.setTimezone(oldEvent.getTimezone());
        event.setUrl(oldEvent.getUrl());
        return event;
    }

    /**
     * Gets the events that matches with ids on the android smartphone
     *
     * @param ids the ids of the events
     * @return
     */
    public static ArrayList<FeedItemEvent> getCalendarEvents(String[] ids, String sourceName, Integer sourceId) {
        ArrayList<FeedItemEvent> eventList = new ArrayList<FeedItemEvent>();

        String selection = null;
        if (ids != null && ids.length>0) {
            String listQuery = "?";
            for (int i=0; i< ids.length-1; i++) {
                listQuery+=",?";
            }

            selection = CalendarContract.Events._ID + " IN (" + listQuery + ")";
        }

        Cursor cursor = TactApplication.getInstance().getContentResolver().query(
                CalendarContract.Events.CONTENT_URI,
                EVENT_PROJECTION,
                selection,
                (ids != null && ids.length>0)?ids:null,
                null
        );

        long current_time = Calendar.getInstance().getTimeInMillis();
        long two_weeks_milliseconds = TactDataSource.TactDataSourceConstants.MILLISECONDS_IN_A_DAY*14;

        while (cursor.moveToNext()) {
            FeedItemEvent event = getEvent(sourceName, sourceId, TactApplication.getInstance().getApplicationContext(), cursor);

            if (event.isRecurrence()){
                Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI
                        .buildUpon();
                ContentUris.appendId(eventsUriBuilder, current_time - two_weeks_milliseconds);
                ContentUris.appendId(eventsUriBuilder, current_time + two_weeks_milliseconds);
                Uri eventsUri = eventsUriBuilder.build();

                Cursor cursorRecurrence = TactApplication.getInstance().getContentResolver().query(
                        eventsUri,
                        INSTANCES_PROJECTION,
                        "Events._id = ?",
                        new String[]{event.getAndroidEventId().toString()},
                        null
                );

                while (cursorRecurrence.moveToNext()) {
                    eventList.add(createRecurrentEvent(event, cursorRecurrence.getString(0), sourceId, sourceName,
                            cursorRecurrence.getString(3), cursorRecurrence.getString(4)));
                }
                cursorRecurrence.close();
            }
            else {
                eventList.add(event);
            }
        }
        cursor.close();

        return eventList;
    }


//    public static ArrayList<FeedItemEvent> getCalendarEventList(String sourceName, Integer sourceId, Context ctx, String[] calendars) {
//        ArrayList<FeedItemEvent> eventList = new ArrayList<FeedItemEvent>();
//
//        Cursor cursor = getCursor(ctx, CalendarContract.Events.CONTENT_URI, CalendarAPI.QueryFilter.ByCalendarId, EVENT_PROJECTION, calendars);
//
//        while (cursor.moveToNext()) {
//
//            eventList.add(getEvent(sourceName, sourceId, ctx, cursor));
//
//        }
//        cursor.close();
//        return eventList;
//    }

    public static ArrayList<Long> getCalendarEventsIds(String[] calendars, String minTimestamp, String maxTimestamp, Boolean only_updated, Boolean only_deleted) {
        ArrayList<Long> eventList = new ArrayList<Long>();

        String selection = "";
        String[] args;
        if (calendars != null && calendars.length > 0){
           String listQuery = "?";
            for (int i=0; i< calendars.length-1; i++) {
                listQuery+=",?";
            }
            selection = CalendarContract.Events.CALENDAR_ID + " IN (" + listQuery + ") AND ";
            args = new String[calendars.length + 2];
            int i = 0;
            for (String cal: calendars){
                args[i] = cal;
                i++;
            }
            args[i] = minTimestamp;
            args[i+1] = maxTimestamp;
        }
        else {
            args = new String[2];
            args[0] = minTimestamp;
            args[1] = maxTimestamp;
        }

        if (only_updated && !only_deleted){
            selection += CalendarContract.Events.DIRTY + " = 1 AND ";
        }
        selection += CalendarContract.Events.DELETED + " = " + (only_deleted ? "1" : "0") + " AND "
                + CalendarContract.Events.DTSTART + " >= ? AND "
                + CalendarContract.Events.DTSTART + " <= ?";


        Cursor cursor = TactApplication.getInstance().getContentResolver().query(
                CalendarContract.Events.CONTENT_URI,
                EVENT_PROJECTION,
                selection,
                args,
                null
        );

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                eventList.add(Long.valueOf(cursor.getString(1)));
            }
        }
        cursor.close();
        return eventList;
    }

    private static FeedItemEvent getEvent(String sourceName, Integer sourceId, Context context, Cursor cursor){
        FeedItemEvent event = new FeedItemEvent();

        List<AttendeeInfo> attendees = CalendarAPI.getAtendees(context, CalendarAPI.QueryFilter.ByEventId, new String[] {String.valueOf(cursor.getString(1))});

        String remoteId = TactDataSource.getNewRemoteId();
        event.setServerId(TactDataSource.generateFeedItemId(sourceId.toString(), remoteId));
        event.setRemoteId(remoteId);
        event.setSourceType(sourceName);
        event.setSourceId(sourceId);
        event.setVersion(0);
        event.setEndAt(cursor.getString(5) != null ? Long.parseLong(cursor.getString(5)) : null);
        FeedItemMetadata metadata = new FeedItemMetadata();
        metadata.setCreatedAt(cursor.getString(4) != null ? Long.parseLong(cursor.getString(4)) : null);
        metadata.setUpdatedAt(cursor.getString(13) != null ? Long.parseLong(cursor.getString(13)) : null);
        metadata.setIsActivity(true);
        metadata.setPushedSources(new ArrayList<Integer>());
        metadata.setPushToSources(new ArrayList<Integer>());
        metadata.setUnpushFromSources(new ArrayList<Integer>());
        metadata.setRelatedItems(new ArrayList<String>());
        metadata.setType("Event");
        metadata.setRelatedContacts(new ArrayList<String>());
        metadata.setOpportunityId(null);
        event.setMetadata(metadata);
        event.setAllDay(!cursor.getString(11).equals("null") && cursor.getString(11).equals("1"));
        if (event.isAllDay()){
            Long timestampAux = cursor.getString(4) != null ? Long.parseLong(cursor.getString(4)) : null;
            if (timestampAux != null){
                event.setTimestamp(Utils.getDateInGMT(timestampAux));
            }
            event.setStartAt(event.getTimestamp());
        }
        else {
            event.setTimestamp(cursor.getString(4) != null ? Long.parseLong(cursor.getString(4)) : null);
            event.setStartAt(cursor.getString(4) != null ? Long.parseLong(cursor.getString(4)) : null);
        }


        event.setAndroidCalendarId(Integer.parseInt(cursor.getString(0)));
        event.setAndroidEventId(Long.parseLong(cursor.getString(1)));
        event.setDescription(cursor.getString(3));
        event.setDueAt(cursor.getString(5) != null ? Long.parseLong(cursor.getString(5)) : null);
        Map<String, EmailName> emailNames = new HashMap<String, EmailName>();
        ArrayList<String> invitedEmails = new ArrayList<String>();
        for(AttendeeInfo attendee: attendees){
            emailNames.put(attendee.getEmail(), new EmailName(attendee.getEmail(), attendee.getName()));
            invitedEmails.add(attendee.getEmail());
        }
        event.setEmailNames(emailNames);
        event.setEndAt(cursor.getString(5) != null ? Long.parseLong(cursor.getString(5)) : null); // TODO: if endAt is null we can use duration that its in RFC2445 format
        event.setInvitedEmails(invitedEmails);
        event.setLocation(cursor.getString(6));
        event.setOrganizerEmail(cursor.getString(7));
        event.setRecurrence(cursor.getString(15) != null && !cursor.getString(15).isEmpty());
        if (event.isRecurrence()){
            String recurrence = cursor.getString(15);
            if (recurrence.contains("BYDAY")){
                Pattern replace = Pattern.compile(".*BYDAY=");
                Matcher matcher2 = replace.matcher(recurrence);
                recurrence = matcher2.replaceAll("");
                String recurrenceStr = "";
                Map<String, String> daysMap = new HashMap<String, String>();
                daysMap.put("SU", "Sunday");
                daysMap.put("MO", "Monday");
                daysMap.put("TU", "Tuesday");
                daysMap.put("WE", "Wednesday");
                daysMap.put("TH", "Thursday");
                daysMap.put("FR", "Friday");
                daysMap.put("SA", "Saturday");
                String[] days = new String[]{"SU", "MO", "TU", "WE", "TH", "FR", "SA"};
                for (String day: days){
                    if (recurrence.contains(day)){
                        recurrenceStr += daysMap.get(day) + ", ";
                    }
                }

                if (!recurrenceStr.isEmpty()) {
                    int ind = recurrenceStr.lastIndexOf(", ");
                    recurrenceStr = new StringBuilder(recurrenceStr).replace(ind, ind + 2, "").toString();
                    event.setAndroidRecurrence(recurrenceStr);
                }
            }
        }
        if (event.getEndAt() == null && event.getStartAt() != null){
            String duration = cursor.getString(9);
            if (duration.startsWith("P") && duration.endsWith("S")){
                String durationStr = duration.replace("P", "").replace("S", "");
                event.setEndAt(Long.parseLong(durationStr)*1000 + event.getStartAt());
            }
        }
        event.setSubject(cursor.getString(2));
        event.setTimezone(cursor.getString(14));
        event.setUrl(null);

        return event;
    }

    //****************************  CALENDAR CRUD *****************************************\\

    /**
     * Returns a CalendarInfo ArrayList; each object contains all of calendar info
     * @param ctx
     * @param filter
     * @param args
     * @return
     */
    public static ArrayList<AttendeeInfo> getAtendees(Context ctx, QueryFilter filter,  String [] args ) {

        Cursor atendeesCursor = getCursor(ctx, CalendarContract.Attendees.CONTENT_URI, filter, ATENDEE_PROJECTION, args);
        ArrayList<AttendeeInfo> atendeeList = new ArrayList<AttendeeInfo>();

        while (atendeesCursor.moveToNext()) {

            // Get the field values
            long eventID = atendeesCursor.getLong(1);
            String name = atendeesCursor.getString(2);
            String email = atendeesCursor.getString(3);

            AttendeeInfo attendeeInfo = new AttendeeInfo();
            attendeeInfo.setEventId(eventID);
            attendeeInfo.setName(name);
            attendeeInfo.setEmail(email);

            atendeeList.add(attendeeInfo);
        }

        return atendeeList;
    }

    /**
     * Deletes an event from event id
     * @param ctx
     * @param eventId
     */
    public static void deleteCalendarEvent(Context ctx, long eventId) {
        long id = eventId;
        Uri deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, id);
        ctx.getContentResolver().delete(deleteUri, null, null);
    }

    /**
     * Gets an event from event id
     * @param ctx
     * @param eventId
     * @return
     */
//    public static FeedItemEvent getEventFromId(String sourceName, Integer sourceId, Context ctx, long eventId) {
//        String [] filter = {""+eventId};
//        ArrayList<FeedItemEvent> eventsList = CalendarAPI.getCalendarEventList(sourceName, sourceId, ctx, QueryFilter.ByEventId, filter);
//        if (eventsList.size()>0) {
//            return eventsList.get(0);
//        } else {
//            return null;
//        }
//    }

    //****************************  UTILITIES  *********************************\\

    /**
     * Returns a table name from enum type
     * @param table
     * @return
     */
    public static String getQueryField(QueryFilter table) {
        String queryTable = null;
        switch (table) {
            case ByCalendarId:
                queryTable = CalendarContract.Events.CALENDAR_ID;
                break;
            case ByEventId: case ByUpdatedEvents:
                queryTable = "Events._id";
                break;
            case ByOwnerName:
                queryTable = CalendarContract.Calendars.OWNER_ACCOUNT;
                break;
            case ByContactId:
                queryTable = Contact.ROW_RECORD_ID;
        }

        return queryTable;
    }

    /**
     * Return a event cursor to iterate over the calendar or events
     * @param ctx
     * @return
     */
    public static Cursor getCursor(Context ctx, Uri table, QueryFilter filter, String[] projection, String[] selectionArgs) {


        String selection = null;
        if (selectionArgs != null && selectionArgs.length>0 && filter != null) {
            String listQuery = "?";
            for (int i=0; i< selectionArgs.length-1; i++) {
                listQuery+=",?";
            }

            selection = CalendarAPI.getQueryField(filter) + " IN (" + listQuery + ")";
        }

        if (filter == QueryFilter.ByUpdatedEvents){
            selection = CalendarContract.Events.DIRTY + " = 1 AND ";
            selection += CalendarContract.Events.DELETED + " = 0 AND "
                    + CalendarContract.Events.DTSTART + " >= ? AND "
                    + CalendarContract.Events.DTSTART + " <= ?";
            long current_time = Calendar.getInstance().getTimeInMillis();
            long two_weeks_milliseconds = TactDataSource.TactDataSourceConstants.MILLISECONDS_IN_A_DAY*14;
            if (selectionArgs == null){
                selectionArgs = new String[2];
                selectionArgs[0] = Long.toString(current_time - two_weeks_milliseconds);
                selectionArgs[1] = Long.toString(current_time + two_weeks_milliseconds);
            }
        }

        Cursor calendarCursor = ctx.getContentResolver().query(
                table,
                projection,
                selection,
                (selectionArgs != null && selectionArgs.length>0)?selectionArgs:null,
                null
        );

        return calendarCursor;
    }

    /**
     * Return a event cursor to iterate over db entities
     * @param ctx --> context application
     * @param table --> table uri (table name for sql)
     * @param filter --> object or list of object that contains the table field to query
     * @param projection --> string list of field to get
     * @param selectionArgs --> data to do the filter (example: 'john' to filter by field name)
     * @return
     */
    public static Cursor getGeneralCursor(Context ctx, Uri table, QueryFilter filter, String[] projection, String[] selectionArgs, String customQuery) {


        String selection = null;
        if (customQuery == null && selectionArgs != null && selectionArgs.length>0 && filter != null) {
            String listQuery = "?";
            for (int i=0; i< selectionArgs.length-1; i++) {
                listQuery+=",?";
            }

            if (selection == null) {
                selection = CalendarAPI.getQueryField(filter) + " IN (" + listQuery + ")";
            }

        } else {
            selection = customQuery;
            selectionArgs = null;
        }

        Cursor calendarCursor = ctx.getContentResolver().query(
                table,
                projection,
                selection,
                (selectionArgs != null && selectionArgs.length>0)?selectionArgs:null,
                null
        );

        return calendarCursor;
    }

    /**
     * Return a string datetime in Day, Month MonthDay, year  - hh:mm
     * @param milliSeconds
     * @return
     */
    public static String getDate(long milliSeconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return dateFormatter().format(calendar.getTime());
    }

    /**
     * Returns a date in the tact format
     * @return
     */
    public static SimpleDateFormat dateFormatter() {
        return new SimpleDateFormat(
                "EEEE, MMMM dd, yyyy - HH:mm");
    }

    /**
     * Converts string date in long millisecond
     * @param date
     * @return
     */
    public static Long eventDateToMills(String date) {
        try {
            if (date != null) {
                Date convertDate = dateFormatter().parse(date);
                return convertDate.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Long getDateOnly(Long date) {
        return CalendarAPI.eventDateToMills(CalendarAPI.getDate(date).split(" -")[0] + " - 00:00" );
    }
}
