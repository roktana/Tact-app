package com.tactile.tact.database.entities;

import com.tactile.tact.activities.TactApplication;
import com.tactile.tact.consts.TactConst;
import com.tactile.tact.database.DatabaseManager;
import com.tactile.tact.database.dao.ContactFIDao;
import com.tactile.tact.database.model.ContactField;
import com.tactile.tact.database.model.CustomField;
import com.tactile.tact.database.model.CustomFieldObject;
import com.tactile.tact.database.model.FeedItemLog;
import com.tactile.tact.utils.ContactAPI;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sebafonseca on 11/12/14.
 */
public class Contact implements Serializable{

    private static final long serialVersionUID = 3946745826233583372L;

    private Integer id; //Primary key for relations, represent Z_PK
    private Integer ent; //Represent Z_ENT says that 2 is Company and 3 is Person
    private Integer opt;
    private Integer isStarred;
    private Integer isExchange;
    private Integer isGoogle;
    private Integer isSalesforce;
    private Integer searchDocID;
    private Integer visitCount; //Used for something?
    private Integer mergedContact; //relation with the master contact if merged, relation is by Z_PK field
    private Long visitedAt; //Used for recent contacts representation
    private String displayName;
    private String photoUrl;
    private String realPhotoUrl;
    private String recordID;
    private String sectionName;
    private String sortKey;
    private String type; //says that 2 is Company and 1 is Person
    private String companyName;
    private String firstName;
    private String lastName;
    private String fullName;
    private String jobTitle;
    private ArrayList<ContactDetail> contactDetails;
    private HashMap<String, String> customFields;
    private ArrayList<CustomFieldObject> customFieldObjects;

    public boolean isEmailRelatedContact(String email) {
        ArrayList<Contact> contacts = getChilds();
        if (contacts != null && contacts.size() > 0) {
            for (Contact contact : contacts) {
                for (ContactDetail contactDetail : contact.getContactDetails()) {
                    if (contactDetail.isEmailRelated(email)) {
                        return true;
                    }
                }
            }
        } else {
            for (ContactDetail contactDetail : this.getContactDetails()) {
                if (contactDetail.isEmailRelated(email)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Contact(){}

    public Contact(ContactAPI.ContactInfo contactInfo){
        this.setId(contactInfo.getId());
        //this.setEnt();
        this.setOpt(0);
        this.setIsStarred(0);
        this.setIsExchange(0);
        this.setIsGoogle(0);
        this.setIsSalesforce(0);
        //this.setSearchDocID();
        //this.setVisitCount()
        this.setMergedContact(0);
        //this.setVisitedAt()
        this.setDisplayName(contactInfo.getDisplayName());
        //this.setPhotoUrl();
        this.setRecordID(Integer.toString(contactInfo.getId()));
        this.setSectionName(contactInfo.getLastName().substring(0, 1));
        this.setSortKey((contactInfo.getLastName().substring(0, 1) + contactInfo.getLastName() + contactInfo.getFirstName()).toUpperCase());
        this.setType("Person");
        this.setCompanyName(contactInfo.getCompanyName());
        this.setFirstName(contactInfo.getFirstName());
        this.setLastName(contactInfo.getLastName());
        this.setFullName(contactInfo.getDisplayName());
        this.setJobTitle(contactInfo.getJobTitle());
        this.setContactDetails(new ArrayList<ContactDetail>());
    }

    public ArrayList<FeedItemLog> getLogsFromContact() {
        try {
            ArrayList<FeedItemLog> logs = new ArrayList<FeedItemLog>();
//            Query<FrozenFeedItem> query = TactApplication.daoSession.getFrozenFeedItemDao().queryRawCreate(", CONTACT_FEED_ITEM AS CFI, CONTACT_FI AS C WHERE T.TYPE='Log' AND C.CONTACT_ID=? AND T._id=CFI.FEED_ITEM_ID AND C._id=CFI.CONTACT_ID GROUP BY T._id", this.getRecordID());
//            ArrayList<FrozenFeedItem> ffis = (ArrayList<FrozenFeedItem>)query.list();
            ArrayList<ContactFI> contacts = (ArrayList<ContactFI>)TactApplication.daoSession.getContactFIDao().queryBuilder().where(ContactFIDao.Properties.ContactId.eq(this.getRecordID())).list();
            if (contacts != null && contacts.size() > 0) {
                for (ContactFI contactFI : contacts) {
                    ArrayList<ContactFeedItem> contactFeedItems = (ArrayList<ContactFeedItem>)contactFI.getContactFeedItems();
                    if (contactFeedItems != null && contactFeedItems.size() > 0) {
                        for (ContactFeedItem contactFeedItem : contactFeedItems) {
                            if (contactFeedItem.getFeedItem().getType().equals("Log")) {
                                logs.add((FeedItemLog)contactFeedItem.getFeedItem().getFeedItem());
                            }
                        }
                    }
                }
            }
            return logs;
        } catch (Exception e) {
            return null;
        }
    }
    
    public ArrayList<Contact> getCompanyEmployes () {
        return DatabaseManager.getCompanyEmployes(this);
    }

    public ArrayList<Contact> getChilds() {
        return DatabaseManager.getContactChilds(this);
    }

    public Contact getParent() {
        return DatabaseManager.getContactById(this.getId());
    }

    public boolean isPerson() {
        return getEnt() == 3;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEnt() {
        return ent;
    }

    public void setEnt(Integer ent) {
        this.ent = ent;
    }

    public Integer getOpt() {
        return opt;
    }

    public void setOpt(Integer opt) {
        this.opt = opt;
    }

    public Integer getIsStarred() {
        return isStarred;
    }

    public void setIsStarred(Integer isStarred) {
        this.isStarred = isStarred;
    }

    public Integer getIsExchange() {
        return isExchange;
    }

    public void setIsExchange(Integer isExchange) {
        this.isExchange = isExchange;
    }

    public Integer getIsGoogle() {
        return isGoogle;
    }

    public void setIsGoogle(Integer isGoogle) {
        this.isGoogle = isGoogle;
    }

    public Integer getIsSalesforce() {
        return isSalesforce;
    }

    public void setIsSalesforce(Integer isSalesforce) {
        this.isSalesforce = isSalesforce;
    }

    public Integer getSearchDocID() {
        return searchDocID;
    }

    public void setSearchDocID(Integer searchDocID) {
        this.searchDocID = searchDocID;
    }

    public Integer getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(Integer visitCount) {
        this.visitCount = visitCount;
    }

    public Integer getMergedContact() {
        return mergedContact;
    }

    public void setMergedContact(Integer mergedContact) {
        this.mergedContact = mergedContact;
    }

    public Long getVisitedAt() {
        return visitedAt;
    }

    public void setVisitedAt(Long visitedAt) {
        this.visitedAt = visitedAt;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getRealPhotoUrl() {
        return realPhotoUrl;
    }

    public void setRealPhotoUrl(String realPhotoUrl) {
        this.realPhotoUrl = realPhotoUrl;
    }

    public String getRecordID() {
        return recordID;
    }

    public void setRecordID(String recordID) {
        this.recordID = recordID;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getSortKey() {
        return sortKey;
    }

    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public ArrayList<ContactDetail> getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(ArrayList<ContactDetail> contactDetails) {
        this.contactDetails = contactDetails;
    }

    public HashMap<String, String> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(HashMap<String, String> customFields) {
        this.customFields = customFields;
    }

    public String getInitials(){
        if (displayName != null && !displayName.isEmpty() && displayName.split(" ").length > 1){
            String[] letters = displayName.replaceAll("\\s+", " ").split(" ");
            if (letters.length > 1){
                return letters[0].substring(0, 1).toUpperCase() + letters[1].substring(0, 1).toUpperCase();
            }
        }
        else if (displayName != null && !displayName.isEmpty() && displayName.length() > 1){
            return displayName.substring(0, 2).toUpperCase();
        }

        String f = firstName != null ? firstName.substring(0, 1).toUpperCase() : null;
        String l = lastName != null ? lastName.substring(0, 1).toUpperCase() : null;
        if (f != null && l!= null){
            return f + l;
        }
        else {
            return null;
        }
    }

    public Boolean isCompany(){
        return type.equals("2") || type.equals("Company");
    }

    public Boolean isLocal(){
        return getIsExchange() == 0 && getIsGoogle() == 0 && getIsSalesforce() == 0;
    }

    private ContactDetail getContactDetail(String remoteId, Integer sourceId) {
        if (this.getContactDetails() != null && this.getContactDetails().size() > 0) {
            for (ContactDetail contactDetail : this.getContactDetails()) {
                if (contactDetail.getRemoteId().equals(remoteId) && contactDetail.getSourceId() == sourceId) {
                    return contactDetail;
                }
            }
        }
        return null;
    }

    private CustomFieldObject getCustomFieldObject(String name, Integer sourceId) {
        if (this.getCustomFieldObjects() != null && this.getCustomFieldObjects().size() > 0) {
            for (CustomFieldObject customFieldObject : this.getCustomFieldObjects()) {
                if (customFieldObject.getName().equals(name) && customFieldObject.getSource() == sourceId) {
                    return customFieldObject;
                }
            }
        }
        return null;
    }

    public void setValuesByFields(ArrayList<ContactField> contactFields) {
        if (contactFields != null && contactFields.size() > 0) {
            for (ContactField field : contactFields) {
                try {
                    if (field.getContactEntity().equals(TactConst.CONTACT_ENTITY)) {
                        Field fieldDefinition = this.getClass().getDeclaredField(field.getEntityName());
                        fieldDefinition.set(this, field.getValue());
                    } else if (field.getContactEntity().equals(TactConst.CONTACT_DETAIL_ENTITY)) {
                        if (this.getContactDetails() == null) {
                            this.setContactDetails(new ArrayList<ContactDetail>());
                        }
                        if (this.getContactDetail(field.getRemoteId(), field.getSourceId()) != null) {
                            this.getContactDetail(field.getRemoteId(), field.getSourceId()).setValuesByFields(field);
                        } else {
                            ContactDetail newContactDetail = new ContactDetail();
                            newContactDetail.setValuesByFields(field);
                            this.getContactDetails().add(newContactDetail);
                        }
                    } else {
                        if (this.getCustomFieldObjects() == null) {
                            this.setCustomFieldObjects(new ArrayList<CustomFieldObject>());
                        }
                        if (this.getCustomFieldObject(field.getServerName(), field.getSourceId()) != null) {
                            this.getCustomFieldObject(field.getServerName(), field.getSourceId()).setValuesByFields(field);
                        } else {
                            CustomFieldObject newCustomFieldObject = new CustomFieldObject();
                            newCustomFieldObject.setValuesByFields(field);
                            this.getCustomFieldObjects().add(newCustomFieldObject);
                        }
                    }
                } catch (Exception e) {

                }
            }
        }

    }

    public ArrayList<CustomFieldObject> getCustomFieldObjects() {
        return customFieldObjects;
    }

    public void setCustomFieldObjects(ArrayList<CustomFieldObject> customFieldObjects) {
        this.customFieldObjects = customFieldObjects;
    }
}
