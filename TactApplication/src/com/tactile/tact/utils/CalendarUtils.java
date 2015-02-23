package com.tactile.tact.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by ismael on 11/19/14.
 */
public class CalendarUtils {

    public static ArrayList<Long> getWeekArray (Long currentTime) {
        ArrayList<Long> weekArray = new ArrayList<Long>();
        Calendar week = Calendar.getInstance();
        week.setTimeInMillis(currentTime);
        Calendar calendar = new GregorianCalendar();
        calendar.clear();
        calendar.set(Calendar.YEAR, week.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, week.get(Calendar.MONTH));
        calendar.set(Calendar.WEEK_OF_MONTH, week.get(Calendar.WEEK_OF_MONTH));
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);

        for (int i = 0; i< 7; i++) {
            weekArray.add(CalendarUtils.getDateOnly(calendar.getTimeInMillis()));
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        return weekArray;
    }

    public static int getDayByTimestamp(Long day) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(day);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    public static int getMonthByTimestamp(Long day) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(day);
        return cal.get(Calendar.MONTH);
    }

    public static Long getMonthOfWeek(ArrayList<Long> week) {
        int month1 = CalendarUtils.getMonthByTimestamp(week.get(2));
        int month2 = CalendarUtils.getMonthByTimestamp(week.get(3));

        if (month1 != month2) {
            return week.get(3);
        } else {
            return week.get(2);
        }
    }

    public static Long getDateOnly(Long date) {
        return CalendarAPI.eventDateToMills(CalendarAPI.getDate(date).split(" -")[0] + " - 00:00" );
    }

}
