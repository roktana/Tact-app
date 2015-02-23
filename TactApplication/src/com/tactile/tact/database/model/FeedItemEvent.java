package com.tactile.tact.database.model;

import com.google.gson.annotations.SerializedName;
import com.tactile.tact.database.DatabaseManager;
import com.tactile.tact.database.TactDataSource;
import com.tactile.tact.database.entities.ContactFeedItem;
import com.tactile.tact.database.entities.FrozenFeedItem;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by leyan on 11/27/14.
 */
public class FeedItemEvent extends FeedItem implements Serializable {

    private static final long serialVersionUID = 7755745821215581172L;

    @SerializedName("is_recurrence")
    private boolean isRecurrence;
    @SerializedName("assigned_to")
    private String assignedTo;
    @SerializedName("due_at")
    private Long dueAt;
    @SerializedName("remind_at")
    private Long remindAt;
    @SerializedName("end_at")
    private Long endAt;
    private String subject;
    @SerializedName("start_at")
    private Long startAt;
    @SerializedName("related_to")
    private String relatedTo;
    private String location;
    private String timezone;
    private String url;
    @SerializedName("invited_emails")
    private ArrayList<String> invitedEmails;
    @SerializedName("organizer_email")
    private String organizerEmail;
    @SerializedName("android:event_id")
    private Long androidEventId;
    @SerializedName("android:calendar_id")
    private Integer androidCalendarId;
    @SerializedName("email_names")
    private Map<String, EmailName> emailNames;
    private String description;
    @SerializedName("all_day")
    private boolean allDay;
    @SerializedName("android:recurrence")
    private String androidRecurrence;

    public FeedItemEvent clone() {
        FeedItemEvent feedItem = new FeedItemEvent();
        feedItem.setDescription(this.description);
        feedItem.setServerId(this.getServerId());
        feedItem.setUrl(this.getUrl());
        feedItem.setMetadata(this.getMetadata().clone());
        feedItem.setSubject(this.getSubject());
        feedItem.setStartAt(this.getStartAt());
        feedItem.setTimestamp(this.getTimestamp());
        feedItem.setRecurrence(this.isRecurrence());
        feedItem.setTimezone(this.getTimezone());
        feedItem.setAllDay(this.isAllDay());
        feedItem.setAndroidCalendarId(this.getAndroidCalendarId());
        feedItem.setAndroidEventId(this.getAndroidEventId());
        feedItem.setAssignedTo(this.getAssignedTo());
        feedItem.setDueAt(this.getDueAt());
        feedItem.setEmailNames(this.getEmailNames());
        feedItem.setEndAt(this.getEndAt());
        feedItem.setInvitedEmails(this.getInvitedEmails());
        feedItem.setLocation(this.getLocation());
        feedItem.setOrganizerEmail(this.getOrganizerEmail());
        feedItem.setRelatedTo(this.getRelatedTo());
        feedItem.setRemindAt(this.getRemindAt());
        feedItem.setRemoteId(this.getRemoteId());
        feedItem.setSourceId(this.getSourceId());
        feedItem.setSourceType(this.getSourceType());
        feedItem.setVersion(this.getVersion());
        feedItem.setAndroidRecurrence(this.getAndroidRecurrence());
        return feedItem;
    }

    public String toJSON() {
        try {
            JSONObject jObject = feedItemToJSON(FeedItemEvent.class);
            if (jObject != null) {
                JSONObject jData = feedItemToJSON(FeedItem.class);
                jObject.put("data", jData);
                return jObject.toString();
            } else return null;
        } catch (Exception e) {
            return null;
        }
    }

    public ArrayList<FeedItemLog> getLogs() {
        ArrayList<FeedItemLog> feedItemLogs = new ArrayList<FeedItemLog>();
        FrozenFeedItem master = this.getParent().getMaster();
        if (master != null) {
            ArrayList<FrozenFeedItem> logs = TactDataSource.getLogsOfMaster(master);
            for (FrozenFeedItem frozenFeedItem : logs) {
                feedItemLogs.add((FeedItemLog)frozenFeedItem.getFeedItem());
            }
        }
        return feedItemLogs;
    }

    public ArrayList<FeedItemNote> getNotes(){
        ArrayList<FeedItemNote> feedItemNotes = new ArrayList<>();
        FrozenFeedItem master = this.getParent().getMaster();
        if (master != null) {
            for (FrozenFeedItem frozenFeedItem : TactDataSource.getNotesOfMaster(master)) {
                feedItemNotes.add((FeedItemNote)frozenFeedItem.getFeedItem());
            }
        }
        return feedItemNotes;
    }

    public ArrayList<FeedItem> getRelatedObjects(){
        ArrayList<FeedItem> feedItems = new ArrayList<>();
        feedItems.addAll(getLogs());
        feedItems.addAll(getNotes());
        return feedItems;
    }

    public ArrayList<com.tactile.tact.database.entities.Contact> getContactsRelated() {
        if (this.getParent().getContactFeedItems() == null || this.getParent().getContactFeedItems().size() == 0){
            return null;
        } else {
            String[] ids = new String[this.getParent().getContactFeedItems().size()];
            for (ContactFeedItem contactFeedItem : this.getParent().getContactFeedItems()) {
                ids[ids.length - 1] = contactFeedItem.getContact().getContactId();
            }
            return DatabaseManager.getContactByRecordIds(ids);
        }
    }

    private FeedItemRelatedContact getFeedItemRelatedContact(String email) {
        FeedItemRelatedContact relatedContact = new FeedItemRelatedContact();
        relatedContact.setEmail(email);

        if (getEmailNames() != null && getEmailNames().size() > 0 && emailNames.containsKey(email)) {
            String name = this.getEmailNames().get(email).getName();
            relatedContact.setName(name != null && !name.isEmpty() ? name : null);
        }

//        ArrayList<Contact> contacts = getContactsRelated();
//        if (contacts != null && contacts.size() > 0) {
//            for (Contact contact : contacts) {
//                if (contact.isEmailRelatedContact(email)) {
                    relatedContact.setContact(DatabaseManager.getContactMatchingEmail(email));
//                    break;
//                }
//            }
//        }

        return relatedContact;
    }

    public FeedItemRelatedContact getOrganizerToShow() {
        if (this.getOrganizerEmail() != null && !this.getOrganizerEmail().isEmpty()) {
            return this.getFeedItemRelatedContact(this.getOrganizerEmail());
        } else return null;
    }

    public ArrayList<FeedItemRelatedContact> getInviteesToShow() {
        if (this.getInvitedEmails() != null && this.getInvitedEmails().size() > 0) {
            ArrayList<FeedItemRelatedContact> feedItemRelatedContacts = new ArrayList<FeedItemRelatedContact>();
            for (String inviteeEmail : this.getInvitedEmails()) {
                feedItemRelatedContacts.add(this.getFeedItemRelatedContact(inviteeEmail));
            }
            return feedItemRelatedContacts;
        } else return null;
    }

    public boolean isRecurrence() {
        return isRecurrence;
    }

    public void setRecurrence(boolean isRecurrence) {
        this.isRecurrence = isRecurrence;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public Long getDueAt() {
        return dueAt;
    }

    public void setDueAt(Long dueAt) {
        this.dueAt = dueAt;
    }

    public Long getRemindAt() {
        return remindAt;
    }

    public void setRemindAt(Long remindAt) {
        this.remindAt = remindAt;
    }

    public Long getEndAt() {
        return endAt;
    }

    public void setEndAt(Long endAt) {
        this.endAt = endAt;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Long getStartAt() {
        return startAt;
    }

    public void setStartAt(Long startAt) {
        this.startAt = startAt;
    }

    public String getRelatedTo() {
        return relatedTo;
    }

    public void setRelatedTo(String relatedTo) {
        this.relatedTo = relatedTo;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ArrayList<String> getInvitedEmails() {
        return invitedEmails;
    }

    public void setInvitedEmails(ArrayList<String> invitedEmails) {
        this.invitedEmails = invitedEmails;
    }

    public String getOrganizerEmail() {
        return organizerEmail;
    }

    public void setOrganizerEmail(String organizerEmail) {
        this.organizerEmail = organizerEmail;
    }

    public Long getAndroidEventId() {
        return androidEventId;
    }

    public void setAndroidEventId(Long androidEventId) {
        this.androidEventId = androidEventId;
    }

    public Integer getAndroidCalendarId() {
        return androidCalendarId;
    }

    public void setAndroidCalendarId(Integer androidCalendarId) {
        this.androidCalendarId = androidCalendarId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    public Map<String, EmailName> getEmailNames() {
        return emailNames;
    }

    public void setEmailNames(Map<String, EmailName> emailNames) {
        this.emailNames = emailNames;
    }

    public String getAndroidRecurrence() {
        return androidRecurrence;
    }

    public void setAndroidRecurrence(String androidRecurrence) {
        this.androidRecurrence = androidRecurrence;
    }
}
