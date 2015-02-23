package com.tactile.tact.database.model;

import com.google.gson.annotations.SerializedName;
import com.tactile.tact.consts.TactConst;

import java.io.Serializable;


/**
 * Created by leyan on 1/28/15.
 */
public class ContactField implements Serializable {

    private static final long serialVersionUID = 3946745823434281272L;

    @SerializedName("name")
    private String serverName;
    private String entityName;
    private String dbName;
    private String contactEntity;
    @SerializedName("value")
    private Object value;
    @SerializedName("source_id")
    private Integer sourceId;
    @SerializedName("remote_id")
    private String remoteId;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("record_id")
    private String recordId;
    @SerializedName("parent_id")
    private String parentId;
    @SerializedName("user_id")
    private String userId;
    @SerializedName("archived")
    private Integer archived;
    @SerializedName("id")
    private Integer id;
    @SerializedName("revision")
    private Integer revision;
    @SerializedName("type")
    private Integer type;

    public ContactField() {

    }

    public ContactField(String serverName, Object value, Integer sourceId, String remoteId) {
        this.serverName = serverName;
        this.setValue(value);
        this.setSourceId(sourceId);
        this.setRemoteId(remoteId);
        matchFields(serverName);
    }

    public void matchFields(String serverName) {
        if (serverName != null && !serverName.isEmpty()) {
            switch (serverName) {
                case  "business_phone": {
                    this.setContactEntity(TactConst.CONTACT_DETAIL_ENTITY);
                    this.setDbName(ContactDetails.ROW_BUSINESS_PHONE);
                    this.setEntityName("businessPhone");
                }
                case  "mobile_phone": {
                    this.setContactEntity(TactConst.CONTACT_DETAIL_ENTITY);
                    this.setDbName(ContactDetails.ROW_MOBILE_PHONE);
                    this.setEntityName("mobilePhone");
                }
                case  "home_phone": {
                    this.setContactEntity(TactConst.CONTACT_DETAIL_ENTITY);
                    this.setDbName(ContactDetails.ROW_HOME_PHONE);
                    this.setEntityName("homePhone");
                }
                case  "business_fax": {
                    this.setContactEntity(TactConst.CONTACT_DETAIL_ENTITY);
                    this.setDbName(ContactDetails.ROW_BUSINESS_FAX);
                    this.setEntityName("businessFax");
                }
                case  "business_email": {
                    this.setContactEntity(TactConst.CONTACT_DETAIL_ENTITY);
                    this.setDbName(ContactDetails.ROW_BUSINESS_EMAIL);
                    this.setEntityName("businessMail");
                }
                case  "personal_email": {
                    this.setContactEntity(TactConst.CONTACT_DETAIL_ENTITY);
                    this.setDbName(ContactDetails.ROW_PERSONAL_EMAIL);
                    this.setEntityName("personalEmail");
                }
                case  "other_email": {
                    this.setContactEntity(TactConst.CONTACT_DETAIL_ENTITY);
                    this.setDbName(ContactDetails.ROW_OTHER_EMAIL);
                    this.setEntityName("otherEmail");
                }
                case  "first_name": {
                    this.setContactEntity(TactConst.CONTACT_DETAIL_ENTITY);
                    this.setDbName(ContactDetails.ROW_FIRST_NAME);
                    this.setEntityName("firstName");
                }
                case  "last_name": {
                    this.setContactEntity(TactConst.CONTACT_DETAIL_ENTITY);
                    this.setDbName(ContactDetails.ROW_LAST_NAME);
                    this.setEntityName("lastName");
                }
                case  "name": {
                    this.setContactEntity(TactConst.CONTACT_DETAIL_ENTITY);
                    this.setDbName(ContactDetails.ROW_NAME);
                    this.setEntityName("name");
                }
                case  "company": {
                    this.setContactEntity(TactConst.CONTACT_DETAIL_ENTITY);
                    this.setDbName(ContactDetails.ROW_COMPANY);
                    this.setEntityName("company");
                }
                case  "job_title": {
                    this.setContactEntity(TactConst.CONTACT_DETAIL_ENTITY);
                    this.setDbName(ContactDetails.ROW_JOB_TITLE);
                    this.setEntityName("jobTitle");
                }
                case  "department": {
                    this.setContactEntity(TactConst.CONTACT_DETAIL_ENTITY);
                    this.setDbName(ContactDetails.ROW_DEPARTMENT);
                    this.setEntityName("department");
                }
                case  "business_address_street": {
                    this.setContactEntity(TactConst.CONTACT_DETAIL_ENTITY);
                    this.setDbName(ContactDetails.ROW_BUSINESS_STREET);
                    this.setEntityName("businessStreet");
                }
                case  "business_address_city": {
                    this.setContactEntity(TactConst.CONTACT_DETAIL_ENTITY);
                    this.setDbName(ContactDetails.ROW_BUSINESS_CITY);
                    this.setEntityName("businessCity");
                }
                case  "business_address_state": {
                    this.setContactEntity(TactConst.CONTACT_DETAIL_ENTITY);
                    this.setDbName(ContactDetails.ROW_BUSINESS_STATE);
                    this.setEntityName("businessState");
                }
                case  "business_address_postal_code": {
                    this.setContactEntity(TactConst.CONTACT_DETAIL_ENTITY);
                    this.setDbName(ContactDetails.ROW_BUSINESS_POSTAL_CODE);
                    this.setEntityName("businessPostalCode");
                }
                case  "business_address_country": {
                    this.setContactEntity(TactConst.CONTACT_DETAIL_ENTITY);
                    this.setDbName(ContactDetails.ROW_BUSINESS_COUNTRY);
                    this.setEntityName("businessCountry");
                }
                case  "annual_revenue": {
                    this.setContactEntity(TactConst.CONTACT_DETAIL_ENTITY);
                    this.setDbName(ContactDetails.ROW_ANNUAL_REVENUE);
                    this.setEntityName("annualRevenue");
                }
                case  "description": {
                    this.setContactEntity(TactConst.CONTACT_DETAIL_ENTITY);
                    this.setDbName(ContactDetails.ROW_DESCRIPTION_FIELD);
                    this.setEntityName("descriptionField");
                }
                case  "industry": {
                    this.setContactEntity(TactConst.CONTACT_DETAIL_ENTITY);
                    this.setDbName(ContactDetails.ROW_INDUSTRY);
                    this.setEntityName("industry");
                }
                case  "number_of_employees": {
                    this.setContactEntity(TactConst.CONTACT_DETAIL_ENTITY);
                    this.setDbName(ContactDetails.ROW_NUMBER_OF_EMPLOYEES);
                    this.setEntityName("numberOfEmployees");
                }
                case  "ticker_symbol": {
                    this.setContactEntity(TactConst.CONTACT_DETAIL_ENTITY);
                    this.setDbName(ContactDetails.ROW_TICKER_SYMBOL);
                    this.setEntityName("tickerSymbol");
                }
                case  "website": {
                    this.setContactEntity(TactConst.CONTACT_DETAIL_ENTITY);
                    this.setDbName(ContactDetails.ROW_WEBSITE);
                    this.setEntityName("website");
                }
                case  "photo_url": {
                    this.setContactEntity(TactConst.CONTACT_DETAIL_ENTITY);
                    this.setDbName(ContactDetails.ROW_PHOTO_URL);
                    this.setEntityName("photoURL");
                }
                case  "company_id": {
                    this.setContactEntity(TactConst.CONTACT_DETAIL_ENTITY);
                    this.setDbName(ContactDetails.ROW_COMPANYID);
                    this.setEntityName("companyId");
                }
                case  "company_name": {
                    this.setContactEntity(TactConst.CONTACT_DETAIL_ENTITY);
                    this.setDbName(ContactDetails.ROW_COMPANY_NAME);
                    this.setEntityName("companyName");
                }
                case  "twitter_username": {
                    this.setContactEntity(TactConst.CONTACT_DETAIL_ENTITY);
                    this.setDbName(ContactDetails.ROW_TWITTER_USERNAME);
                    this.setEntityName("twitterUsername");
                }
                case  "facebook_username": {
                    this.setContactEntity(TactConst.CONTACT_DETAIL_ENTITY);
                    this.setDbName(ContactDetails.ROW_FACEBOOK_USERNAME);
                    this.setEntityName("facebookUserName");
                }
                case  "linkedin_url": {
                    this.setContactEntity(TactConst.CONTACT_DETAIL_ENTITY);
                    this.setDbName(ContactDetails.ROW_LINKEDIN_URL);
                    this.setEntityName("linkedInURL");
                }
                case  "linkedin_id": {
                    this.setContactEntity(TactConst.CONTACT_DETAIL_ENTITY);
                    this.setDbName(ContactDetails.ROW_LINKEDIN_ID);
                    this.setEntityName("linkedInId");
                }
                case  "notes": {
                    this.setContactEntity(TactConst.CONTACT_DETAIL_ENTITY);
                    this.setDbName(ContactDetails.ROW_NOTES);
                    this.setEntityName("notes");
                }
                case  "tct_display_name": {
                    this.setContactEntity(TactConst.CONTACT_ENTITY);
                    this.setDbName(com.tactile.tact.database.model.Contact.ROW_DISPLAY_NAME);
                    this.setEntityName("displayName");
                }
                case  "tct_sort": {
                    this.setContactEntity(TactConst.CONTACT_ENTITY);
                    this.setDbName(com.tactile.tact.database.model.Contact.ROW_SORT_KEY);
                    this.setEntityName("sortKey");
                }
                case  "tct_section": {
                    this.setContactEntity(TactConst.CONTACT_ENTITY);
                    this.setDbName(com.tactile.tact.database.model.Contact.ROW_SECTION_NAME);
                    this.setEntityName("sectionName");
                }
                case  "tct_full_name": {
                    this.setContactEntity(TactConst.CONTACT_ENTITY);
                    this.setDbName(com.tactile.tact.database.model.Contact.ROW_FULL_NAME);
                    this.setEntityName("fullName");
                }
                default:{
                    //Assuming is a custom field
                    this.setContactEntity(TactConst.CONTACT_CUSTOM_FIELD_ENTITY);
                }
            }
        }
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getContactEntity() {
        return contactEntity;
    }

    public void setContactEntity(String contactEntity) {
        this.contactEntity = contactEntity;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getArchived() {
        return archived;
    }

    public void setArchived(Integer archived) {
        this.archived = archived;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
