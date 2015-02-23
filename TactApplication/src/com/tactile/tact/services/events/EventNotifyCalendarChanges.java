package com.tactile.tact.services.events;

import java.util.ArrayList;

/**
 * Created by sebafonseca on 12/19/14.
 */
public class EventNotifyCalendarChanges extends EventTactBase {

    public ArrayList<Long> dates;
    public Boolean syncOpportunities;
    public Boolean syncEventsOrTasks;
    public Boolean syncContacts;
    public Boolean syncEmails;
    public Boolean syncNotebook;
    public Boolean sendEvent;


    public EventNotifyCalendarChanges(ArrayList<Long> dates){
        this.dates = dates;
        syncOpportunities = false;
        syncEventsOrTasks = false;
        syncContacts = false;
        syncEmails = false;
        syncNotebook = false;
        sendEvent = false;
    }

    public EventNotifyCalendarChanges(){
        syncOpportunities = false;
        syncEventsOrTasks = false;
        syncContacts = false;
        syncEmails = false;
        syncNotebook = false;
    }

    public void setDates(ArrayList<Long> dates){
        this.dates = dates;
    }

    public void setSyncOpportunities(Boolean syncOpportunities){
        this.syncOpportunities = syncOpportunities;
        sendEvent = true;
    }

    public void setSyncEventsOrTasks(Boolean syncEventsOrTasks){
        this.syncEventsOrTasks = syncEventsOrTasks;
        sendEvent = true;
    }

    public void setSyncContacts(Boolean syncContacts){
        this.syncContacts = syncContacts;
        sendEvent = true;
    }

    public void setSyncEmails(Boolean syncEmails){
        this.syncEmails = syncEmails;
        sendEvent = true;
    }

    public void setSyncNotebook(Boolean syncNotebook){
        this.syncNotebook = syncNotebook;
        sendEvent = true;
    }

    public Boolean hasToSyncOpportunities(){
        return syncOpportunities;
    }

    public Boolean hasToSyncEventsOrTasks(){
        return syncEventsOrTasks;
    }

    public Boolean hasToSyncContacts(){
        return syncContacts;
    }

    public Boolean hasToSyncEmails(){
        return syncEmails;
    }

    public Boolean hasToSyncNotebook(){
        return syncNotebook;
    }

    public Boolean hasDataToSync(){
        return sendEvent;
    }

    public ArrayList<Long> getDates(){
        return dates;
    }

}
