package com.tactile.tact.services.events;

/**
 * Created by sebafonseca on 11/11/14.
 */
public class EventOnboardingChanges extends EventTactBase {

    private ChangeType type;
    private Object new_value;

    public enum ChangeType
    {
        LOGGED_IN_SALESFORCE,
        LOGGED_IN_GOOGLE,
        LOGGED_IN_EXCHANGE,
        GOOGLE_PROGRESS,
        SYNC_GOOGLE,
        SYNC_EXCHANGE,
        SYNC_CONTACTS,
        SYNC_CALENDARS,
        SYNC_CONTACTS_PROCESSING,
        SYNC_EXCHANGE_MANUALLY,
        SYNC_EXCHANGE_STAGE,
        SYNC_EXCHANGE_EMAIL,
        SYNC_EXCHANGE_SERVER,
        SYNC_EXCHANGE_DOMAIN,
        SYNC_EXCHANGE_USERNAME,
        SYNC_EXCHANGE_PWD,
        REGISTER_CHANGE_TITLE
    }

    public EventOnboardingChanges(ChangeType type, Object new_value){
        this.type = type;
        this.new_value = new_value;
    }

    public ChangeType getType(){
        return this.type;
    }

    public Object getNewValue(){
        return this.new_value;
    }
}
