package com.tactile.tact.database;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.ContactsContract;

import com.tactile.tact.activities.TactApplication;
import com.tactile.tact.consts.TactConst;
import com.tactile.tact.database.entities.Contact;
import com.tactile.tact.database.entities.ContactDetail;
import com.tactile.tact.database.entities.Opportunity;
import com.tactile.tact.database.model.ContactDetails;
import com.tactile.tact.database.model.CustomField;
import com.tactile.tact.database.model.CustomFieldMetaData;
import com.tactile.tact.database.model.CustomFieldObject;
import com.tactile.tact.database.model.Source;
import com.tactile.tact.database.model.SyncStatus;
import com.tactile.tact.utils.ContactAPI;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

// Because the database already exists, we do not need to create or upgrade it, just open and return it
public class DatabaseManager {
    public static final String TAG = DatabaseManager.class.getSimpleName();

    private static final String DATABASE_NAME = "db.sqlite";
    private String path = null;
    private SQLiteDatabase readableDatabase;
    private SQLiteDatabase writableDatabase;

    /**
     * Returns a table name from enum type
     * @param table
     * @return
     */
    public static String getContactQueryField(ContactQueryFilter table) {
        String queryTable = null;
        switch (table) {
            case ByContactId:
                queryTable = ContactsContract.Data.CONTACT_ID;
                break;
            case ByEmailId:
                queryTable = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
                break;
            case ByPhoneId:
                queryTable = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
                break;
            case ByTactContactId:
                queryTable = com.tactile.tact.database.model.Contact.ROW_RECORD_ID;
        }

        return queryTable;
    }


    // Enum to determinate the 'where' of the query
    public static enum ContactQueryFilter {
        ByContactId,
        ByEmailId,
        ByPhoneId,
        ByTactContactId
    }


    public DatabaseManager() {
        readableDatabase = null;
        writableDatabase = null;
    }

    public SQLiteDatabase getWritableDatabase() {
        if (writableDatabase != null)
            return writableDatabase;
        path = TactApplication.getDatabasePath() + "/" + DATABASE_NAME;
        writableDatabase = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
        return writableDatabase;
    }

    public SQLiteDatabase getReadableDatabase() {
        if (readableDatabase != null)
            return readableDatabase;
        path = TactApplication.getDatabasePath() + "/" + DATABASE_NAME;
        readableDatabase = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        return readableDatabase;
    }

    public static ContentValues setContactValues(Contact contact) {
        ContentValues valuesContact = new ContentValues();
        valuesContact.put(com.tactile.tact.database.model.Contact.ROW_PRIMARY_KEY, contact.getId());
        valuesContact.put(com.tactile.tact.database.model.Contact.ROW_ENTITY_PRIMARY_KEY, contact.getEnt());
        valuesContact.put(com.tactile.tact.database.model.Contact.ROW_OPPORTUNITY_PRIMARY_KEY, contact.getOpt());
        valuesContact.put(com.tactile.tact.database.model.Contact.ROW_IS_STARRED, contact.getIsStarred());
        valuesContact.put(com.tactile.tact.database.model.Contact.ROW_IS_EXCHANGE, contact.getIsExchange());
        valuesContact.put(com.tactile.tact.database.model.Contact.ROW_IS_GOOGLE, contact.getIsGoogle());
        valuesContact.put(com.tactile.tact.database.model.Contact.ROW_IS_SALESFORCE, contact.getIsSalesforce());
        valuesContact.put(com.tactile.tact.database.model.Contact.ROW_SEARCH_DOC_ID, contact.getSearchDocID());
        valuesContact.put(com.tactile.tact.database.model.Contact.ROW_VISIT_COUNT, contact.getVisitCount());
        valuesContact.put(com.tactile.tact.database.model.Contact.ROW_MERGED_CONTACT, contact.getMergedContact());
        valuesContact.put(com.tactile.tact.database.model.Contact.ROW_VISITED_AT, contact.getVisitedAt());
        valuesContact.put(com.tactile.tact.database.model.Contact.ROW_DISPLAY_NAME, contact.getDisplayName());
        valuesContact.put(com.tactile.tact.database.model.Contact.ROW_PHOTO_URL, contact.getPhotoUrl());
        valuesContact.put(com.tactile.tact.database.model.Contact.ROW_RECORD_ID, contact.getRecordID());
        valuesContact.put(com.tactile.tact.database.model.Contact.ROW_SECTION_NAME, contact.getSectionName());
        valuesContact.put(com.tactile.tact.database.model.Contact.ROW_SORT_KEY, contact.getSortKey());
        valuesContact.put(com.tactile.tact.database.model.Contact.ROW_TYPE, contact.getType());
        valuesContact.put(com.tactile.tact.database.model.Contact.ROW_COMPANY_NAME, contact.getCompanyName());
        valuesContact.put(com.tactile.tact.database.model.Contact.ROW_FIRST_NAME, contact.getFirstName());
        valuesContact.put(com.tactile.tact.database.model.Contact.ROW_LAST_NAME, contact.getLastName());
        valuesContact.put(com.tactile.tact.database.model.Contact.ROW_FULL_NAME, contact.getFullName());
        valuesContact.put(com.tactile.tact.database.model.Contact.ROW_JOB_TITLE, contact.getJobTitle());
        return valuesContact;
    }

    public static ContentValues setContactDetailValues(ContactDetail contactDetail) {
        ContentValues valuesContact = new ContentValues();
        valuesContact.put(ContactDetails.ROW_PRIMARY_KEY, contactDetail.getId());
        valuesContact.put(ContactDetails.ROW_ENTITY_PRIMARY_KEY, contactDetail.getEnt());
        valuesContact.put(ContactDetails.ROW_OPPORTUNITY_PRIMARY_KEY, contactDetail.getOpt());
        valuesContact.put(ContactDetails.ROW_CONTACT_KEY, contactDetail.getContact());
        valuesContact.put(ContactDetails.ROW_SOURCE, contactDetail.getSourceId());
        valuesContact.put(ContactDetails.ROW_ANNUAL_REVENUE, contactDetail.getAnnualRevenue());
        valuesContact.put(ContactDetails.ROW_BUSINESS_CITY, contactDetail.getBusinessCity());
        valuesContact.put(ContactDetails.ROW_BUSINESS_COUNTRY, contactDetail.getBusinessCountry());
        valuesContact.put(ContactDetails.ROW_BUSINESS_EMAIL, contactDetail.getBusinessMail());
        valuesContact.put(ContactDetails.ROW_BUSINESS_FAX, contactDetail.getBusinessFax());
        valuesContact.put(ContactDetails.ROW_BUSINESS_PHONE, contactDetail.getBusinessPhone());
        valuesContact.put(ContactDetails.ROW_BUSINESS_POSTAL_CODE, contactDetail.getBusinessPostalCode());
        valuesContact.put(ContactDetails.ROW_BUSINESS_STATE, contactDetail.getBusinessState());
        valuesContact.put(ContactDetails.ROW_BUSINESS_STREET, contactDetail.getBusinessStreet());
        valuesContact.put(ContactDetails.ROW_COMPANY, contactDetail.getCompany());
        valuesContact.put(ContactDetails.ROW_COMPANY_NAME, contactDetail.getCompanyName());
        valuesContact.put(ContactDetails.ROW_COMPANYID, contactDetail.getCompanyId());
        valuesContact.put(ContactDetails.ROW_DEPARTMENT, contactDetail.getDepartment());
        valuesContact.put(ContactDetails.ROW_DESCRIPTION_FIELD, contactDetail.getDescriptionField());
        valuesContact.put(ContactDetails.ROW_FACEBOOK_USERNAME, contactDetail.getFacebookUserName());
        valuesContact.put(ContactDetails.ROW_FIRST_NAME, contactDetail.getFirstName());
        valuesContact.put(ContactDetails.ROW_HOME_PHONE, contactDetail.getHomePhone());
        valuesContact.put(ContactDetails.ROW_INDUSTRY, contactDetail.getIndustry());
        valuesContact.put(ContactDetails.ROW_JOB_TITLE, contactDetail.getJobTitle());
        valuesContact.put(ContactDetails.ROW_LAST_NAME, contactDetail.getLastName());
        valuesContact.put(ContactDetails.ROW_LINKEDIN_URL, contactDetail.getLinkedInURL());
        valuesContact.put(ContactDetails.ROW_LINKEDIN_ID, contactDetail.getLinkedInId());
        valuesContact.put(ContactDetails.ROW_MOBILE_PHONE, contactDetail.getMobilePhone());
        valuesContact.put(ContactDetails.ROW_NAME, contactDetail.getName());
        valuesContact.put(ContactDetails.ROW_NOTES, contactDetail.getNotes());
        valuesContact.put(ContactDetails.ROW_NUMBER_OF_EMPLOYEES, contactDetail.getNumberOfEmployees());
        valuesContact.put(ContactDetails.ROW_OTHER_EMAIL, contactDetail.getOtherEmail());
        valuesContact.put(ContactDetails.ROW_PERSONAL_EMAIL, contactDetail.getPersonalEmail());
        valuesContact.put(ContactDetails.ROW_PHOTO_URL, contactDetail.getPhotoURL());
        valuesContact.put(ContactDetails.ROW_REMOTE_ID, contactDetail.getRemoteId());
        valuesContact.put(ContactDetails.ROW_TICKER_SYMBOL, contactDetail.getTickerSymbol());
        valuesContact.put(ContactDetails.ROW_TWITTER_USERNAME, contactDetail.getTwitterUsername());
        valuesContact.put(ContactDetails.ROW_WEBSITE, contactDetail.getWebsite());
        return valuesContact;
    }

    public static ContentValues setCustomFieldValues(CustomFieldObject customField) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CustomField.ROW_PRIMARY_KEY, customField.getId());
        contentValues.put(CustomField.ROW_ENTITY_PRIMARY_KEY, customField.getEnt());
        contentValues.put(CustomField.ROW_OPPORTUNITY_PRIMARY_KEY, customField.getOpt());
        contentValues.put(CustomField.ROW_CONTACT, customField.getContact());
        contentValues.put(CustomField.ROW_CONTACT1, customField.getContact1());
        contentValues.put(CustomField.ROW_SOURCE, customField.getSource());
        contentValues.put(CustomField.ROW_NAME, customField.getName());
        contentValues.put(CustomField.ROW_VALUE, customField.getValue());
        return contentValues;
    }

    public static Contact mapContact(Cursor cursor) {
        Contact contact = new Contact();
        contact.setId(cursor.getInt(cursor.getColumnIndex(com.tactile.tact.database.model.Contact.ROW_PRIMARY_KEY)));
        contact.setEnt(cursor.getInt(cursor.getColumnIndex(com.tactile.tact.database.model.Contact.ROW_ENTITY_PRIMARY_KEY)));
        contact.setOpt(cursor.getInt(cursor.getColumnIndex(com.tactile.tact.database.model.Contact.ROW_OPPORTUNITY_PRIMARY_KEY)));
        contact.setIsStarred(cursor.getInt(cursor.getColumnIndex(com.tactile.tact.database.model.Contact.ROW_IS_STARRED)));
        contact.setIsExchange(cursor.getInt(cursor.getColumnIndex(com.tactile.tact.database.model.Contact.ROW_IS_EXCHANGE)));
        contact.setIsGoogle(cursor.getInt(cursor.getColumnIndex(com.tactile.tact.database.model.Contact.ROW_IS_GOOGLE)));
        contact.setIsSalesforce(cursor.getInt(cursor.getColumnIndex(com.tactile.tact.database.model.Contact.ROW_IS_SALESFORCE)));
        contact.setSearchDocID(cursor.getInt(cursor.getColumnIndex(com.tactile.tact.database.model.Contact.ROW_SEARCH_DOC_ID)));
        contact.setVisitCount(cursor.getInt(cursor.getColumnIndex(com.tactile.tact.database.model.Contact.ROW_VISIT_COUNT)));
        contact.setMergedContact(cursor.getInt(cursor.getColumnIndex(com.tactile.tact.database.model.Contact.ROW_MERGED_CONTACT)));
        contact.setVisitedAt(cursor.getLong(cursor.getColumnIndex(com.tactile.tact.database.model.Contact.ROW_VISITED_AT)));
        contact.setDisplayName(cursor.getString(cursor.getColumnIndex(com.tactile.tact.database.model.Contact.ROW_DISPLAY_NAME)));
        contact.setPhotoUrl(cursor.getString(cursor.getColumnIndex(com.tactile.tact.database.model.Contact.ROW_PHOTO_URL)));
        if (contact.getPhotoUrl() != null && !contact.getPhotoUrl().isEmpty() && contact.getPhotoUrl().startsWith("http")){
            contact.setRealPhotoUrl(contact.getPhotoUrl());
        }
        contact.setRecordID(cursor.getString(cursor.getColumnIndex(com.tactile.tact.database.model.Contact.ROW_RECORD_ID)));
        contact.setSectionName(cursor.getString(cursor.getColumnIndex(com.tactile.tact.database.model.Contact.ROW_SECTION_NAME)));
        contact.setSortKey(cursor.getString(cursor.getColumnIndex(com.tactile.tact.database.model.Contact.ROW_SORT_KEY)));
        contact.setType(cursor.getString(cursor.getColumnIndex(com.tactile.tact.database.model.Contact.ROW_TYPE)));
        contact.setCompanyName(cursor.getString(cursor.getColumnIndex(com.tactile.tact.database.model.Contact.ROW_COMPANY_NAME)));
        contact.setFirstName(cursor.getString(cursor.getColumnIndex(com.tactile.tact.database.model.Contact.ROW_FIRST_NAME)));
        contact.setLastName(cursor.getString(cursor.getColumnIndex(com.tactile.tact.database.model.Contact.ROW_LAST_NAME)));
        contact.setFullName(cursor.getString(cursor.getColumnIndex(com.tactile.tact.database.model.Contact.ROW_FULL_NAME)));
        contact.setJobTitle(cursor.getString(cursor.getColumnIndex(com.tactile.tact.database.model.Contact.ROW_JOB_TITLE)));
        contact.setContactDetails(DatabaseManager.getContactDetailsByContact(contact.getId()));
        if (contact.getDisplayName() == null){
            if (contact.getFirstName() != null && contact.getLastName() != null){
                contact.setDisplayName(contact.getFirstName() + " " + contact.getLastName());
            }
        }
        //Custom fields
        contact.setCustomFields(getCustomFieldsByContact(contact.getId()));
        contact.setCustomFieldObjects(getCustomFieldObjectsByContactId(contact.getId()));
        return contact;
    }

    public static ContactDetail mapContactDetail(Cursor cursor) {
        ContactDetail contactDetail = new ContactDetail();
        contactDetail.setId(cursor.getInt(cursor.getColumnIndex(ContactDetails.ROW_PRIMARY_KEY)));
        contactDetail.setEnt(cursor.getInt(cursor.getColumnIndex(ContactDetails.ROW_ENTITY_PRIMARY_KEY)));
        contactDetail.setOpt(cursor.getInt(cursor.getColumnIndex(ContactDetails.ROW_OPPORTUNITY_PRIMARY_KEY)));
        contactDetail.setContact(cursor.getInt(cursor.getColumnIndex(ContactDetails.ROW_CONTACT_KEY)));
        contactDetail.setSourceId(cursor.getInt(cursor.getColumnIndex(ContactDetails.ROW_SOURCE)));
        contactDetail.setAnnualRevenue(cursor.getString(cursor.getColumnIndex(ContactDetails.ROW_ANNUAL_REVENUE)));
        contactDetail.setBusinessCity(cursor.getString(cursor.getColumnIndex(ContactDetails.ROW_BUSINESS_CITY)));
        contactDetail.setBusinessCountry(cursor.getString(cursor.getColumnIndex(ContactDetails.ROW_BUSINESS_COUNTRY)));
        contactDetail.setBusinessMail(cursor.getString(cursor.getColumnIndex(ContactDetails.ROW_BUSINESS_EMAIL)));
        contactDetail.setBusinessFax(cursor.getString(cursor.getColumnIndex(ContactDetails.ROW_BUSINESS_FAX)));
        contactDetail.setBusinessPhone(cursor.getString(cursor.getColumnIndex(ContactDetails.ROW_BUSINESS_PHONE)));
        contactDetail.setBusinessPostalCode(cursor.getString(cursor.getColumnIndex(ContactDetails.ROW_BUSINESS_POSTAL_CODE)));
        contactDetail.setBusinessState(cursor.getString(cursor.getColumnIndex(ContactDetails.ROW_BUSINESS_STATE)));
        contactDetail.setBusinessStreet(cursor.getString(cursor.getColumnIndex(ContactDetails.ROW_BUSINESS_STREET)));
        contactDetail.setCompany(cursor.getString(cursor.getColumnIndex(ContactDetails.ROW_COMPANY)));
        contactDetail.setCompanyId(cursor.getString(cursor.getColumnIndex(ContactDetails.ROW_COMPANYID)));
        contactDetail.setCompanyName(cursor.getString(cursor.getColumnIndex(ContactDetails.ROW_COMPANY_NAME)));
        contactDetail.setDepartment(cursor.getString(cursor.getColumnIndex(ContactDetails.ROW_DEPARTMENT)));
        contactDetail.setDescriptionField(cursor.getString(cursor.getColumnIndex(ContactDetails.ROW_DESCRIPTION_FIELD)));
        contactDetail.setFacebookUserName(cursor.getString(cursor.getColumnIndex(ContactDetails.ROW_FACEBOOK_USERNAME)));
        contactDetail.setFirstName(cursor.getString(cursor.getColumnIndex(ContactDetails.ROW_FIRST_NAME)));
        contactDetail.setHomePhone(cursor.getString(cursor.getColumnIndex(ContactDetails.ROW_HOME_PHONE)));
        contactDetail.setIndustry(cursor.getString(cursor.getColumnIndex(ContactDetails.ROW_INDUSTRY)));
        contactDetail.setJobTitle(cursor.getString(cursor.getColumnIndex(ContactDetails.ROW_JOB_TITLE)));
        contactDetail.setLastName(cursor.getString(cursor.getColumnIndex(ContactDetails.ROW_LAST_NAME)));
        contactDetail.setLinkedInURL(cursor.getString(cursor.getColumnIndex(ContactDetails.ROW_LINKEDIN_URL)));
        contactDetail.setLinkedInId(cursor.getString(cursor.getColumnIndex(ContactDetails.ROW_LINKEDIN_ID)));
        contactDetail.setMobilePhone(cursor.getString(cursor.getColumnIndex(ContactDetails.ROW_MOBILE_PHONE)));
        contactDetail.setName(cursor.getString(cursor.getColumnIndex(ContactDetails.ROW_NAME)));
        contactDetail.setNotes(cursor.getString(cursor.getColumnIndex(ContactDetails.ROW_NOTES)));
        contactDetail.setNumberOfEmployees(cursor.getString(cursor.getColumnIndex(ContactDetails.ROW_NUMBER_OF_EMPLOYEES)));
        contactDetail.setOtherEmail(cursor.getString(cursor.getColumnIndex(ContactDetails.ROW_OTHER_EMAIL)));
        contactDetail.setPersonalEmail(cursor.getString(cursor.getColumnIndex(ContactDetails.ROW_PERSONAL_EMAIL)));
        contactDetail.setPhotoURL(cursor.getString(cursor.getColumnIndex(ContactDetails.ROW_PHOTO_URL)));
        contactDetail.setRemoteId(cursor.getString(cursor.getColumnIndex(ContactDetails.ROW_REMOTE_ID)));
        contactDetail.setTickerSymbol(cursor.getString(cursor.getColumnIndex(ContactDetails.ROW_TICKER_SYMBOL)));
        contactDetail.setTwitterUsername(cursor.getString(cursor.getColumnIndex(ContactDetails.ROW_TWITTER_USERNAME)));
        contactDetail.setWebsite(cursor.getString(cursor.getColumnIndex(ContactDetails.ROW_WEBSITE)));
        return contactDetail;
    }

    public static HashMap<String,String> getCustomFieldsByContact(Integer id) {
        try {
            HashMap<String, String> customFields = new HashMap<>();
            Cursor cursor = TactApplication.getInstance().getContentResolver().query(DatabaseContentProvider.URI_CUSTOM_FIELD,
                    new String[]{"*"},
                    getSelection(CustomField.ROW_CONTACT, new String[]{id.toString()}),
                    new String[]{id.toString()},
                    null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndex(CustomField.ROW_NAME));
                    String value = cursor.getString(cursor.getColumnIndex(CustomField.ROW_VALUE));
                    if (value != null && !value.isEmpty() && name != null && !name.isEmpty()){
                        Cursor cursorCustomFieldName = TactApplication.getInstance().getContentResolver().query(DatabaseContentProvider.URI_CUSTOM_FIELD_META,
                                new String[]{CustomFieldMetaData.ROW_LABEL},
                                getSelection(CustomFieldMetaData.ROW_NAME, new String[]{name}),
                                new String[]{name},
                                null);
                        cursorCustomFieldName.moveToNext();
                        customFields.put(cursorCustomFieldName.getString(0), value);
                        cursorCustomFieldName.close();
                    }
                }
            }
            cursor.close();
            return customFields;
        } catch (Exception e) {
            return null;
        }
    }

    public static ArrayList<CustomFieldObject> getCustomFieldObjectsByContactId(Integer id) {
        try {
            ArrayList<CustomFieldObject> customFieldObjects = new ArrayList<>();
            Cursor cursor = TactApplication.getInstance().getContentResolver().query(DatabaseContentProvider.URI_CUSTOM_FIELD,
                    new String[]{"*"},
                    getSelection(CustomField.ROW_CONTACT, new String[]{id.toString()}),
                    new String[]{id.toString()},
                    null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    CustomFieldObject customFieldObject = new CustomFieldObject();
                    customFieldObject.setId(cursor.getInt(cursor.getColumnIndex(CustomField.ROW_PRIMARY_KEY)));
                    customFieldObject.setName(cursor.getString(cursor.getColumnIndex(CustomField.ROW_NAME)));
                    customFieldObject.setValue(cursor.getString(cursor.getColumnIndex(CustomField.ROW_VALUE)));
                    if (customFieldObject.getValue() != null && !customFieldObject.getValue().isEmpty() && customFieldObject.getName() != null && !customFieldObject.getName().isEmpty()){
                        Cursor cursorCustomFieldName = TactApplication.getInstance().getContentResolver().query(DatabaseContentProvider.URI_CUSTOM_FIELD_META,
                                new String[]{CustomFieldMetaData.ROW_LABEL},
                                getSelection(CustomFieldMetaData.ROW_NAME, new String[]{customFieldObject.getName()}),
                                new String[]{customFieldObject.getName()},
                                null);
                        if (cursorCustomFieldName.getCount() > 0) {
                            cursorCustomFieldName.moveToNext();
                            customFieldObject.setLabel(cursorCustomFieldName.getString(0));
                        }
                        cursorCustomFieldName.close();
                    }
                    customFieldObjects.add(customFieldObject);
                }
            }
            cursor.close();
            return customFieldObjects;
        } catch (Exception e) {
            return null;
        }
    }

    public static ArrayList<ContactDetail> getContactDetailsByContact(Integer id) {
        try {
            ArrayList<ContactDetail> contactDetails = new ArrayList<ContactDetail>();
            Cursor cursor = TactApplication.getInstance().getContentResolver().query(DatabaseContentProvider.URI_CONTACT_DETAILS,
                    new String[]{"*", ContactDetails.ROW_CONTACT_KEY},
                    getSelection(ContactDetails.ROW_CONTACT_KEY, new String[]{id.toString()}),
                    new String[]{id.toString()},
                    null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    contactDetails.add(DatabaseManager.mapContactDetail(cursor));
                }
            }
            cursor.close();
            return contactDetails;
        } catch (Exception e) {
            return null;
        }
    }

    public static ArrayList<ContactDetail> getContactDetailsByContact(Contact contact) {
        try {
            ArrayList<ContactDetail> contactDetails = new ArrayList<ContactDetail>();
            Cursor cursor = TactApplication.getInstance().getContentResolver().query(DatabaseContentProvider.URI_CONTACT_DETAILS,
                    new String[]{"*", ContactDetails.ROW_CONTACT_KEY},
                    getSelection(ContactDetails.ROW_CONTACT_KEY, new String[]{contact.getId().toString()}),
                    new String[]{contact.getId().toString()},
                    null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    contactDetails.add(DatabaseManager.mapContactDetail(cursor));
                }
            }
            cursor.close();
            return contactDetails;
        } catch (Exception e) {
            return null;
        }
    }

    public static ArrayList<Contact> getContactChilds(Contact contact) {
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        Cursor cursor = TactApplication.getInstance().getContentResolver().query(DatabaseContentProvider.URI_ALL_CONTACTS,
                new String[]{"*", com.tactile.tact.database.model.Contact.ROW_PRIMARY_KEY},
                DatabaseManager.getSelection(com.tactile.tact.database.model.Contact.ROW_MERGED_CONTACT, new String[]{contact.getId().toString()}),
                new String[]{contact.getId().toString()},
                null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                contacts.add(DatabaseManager.mapContact(cursor));
            }
        }
        cursor.close();
        return contacts;
    }

    public static Contact getContactMatchingEmail(String email) {
        try {
            String selection = ContactDetails.ROW_BUSINESS_EMAIL + "= ? OR " + ContactDetails.ROW_PERSONAL_EMAIL + "= ? OR " + ContactDetails.ROW_OTHER_EMAIL + "= ? ";
            Cursor cursorContactDetails = TactApplication.getInstance().getContentResolver().query(DatabaseContentProvider.URI_CONTACT_DETAILS,
                    new String[]{"*", ContactDetails.ROW_CONTACT_KEY},
                    selection,
                    new String[]{email, email, email},
                    null);
            if (cursorContactDetails.getCount() > 0) {
                cursorContactDetails.moveToFirst();
                Cursor cursorContact = TactApplication.getInstance().getContentResolver().query(DatabaseContentProvider.URI_ALL_CONTACTS,
                        new String[]{"*", com.tactile.tact.database.model.Contact.ROW_PRIMARY_KEY},
                        getSelection(com.tactile.tact.database.model.Contact.ROW_PRIMARY_KEY, new String[]{String.valueOf(cursorContactDetails.getInt(cursorContactDetails.getColumnIndex(ContactDetails.ROW_CONTACT_KEY)))}),
                        new String[]{String.valueOf(cursorContactDetails.getInt(cursorContactDetails.getColumnIndex(ContactDetails.ROW_CONTACT_KEY)))},
                        null);
                if (cursorContact.getCount() > 0) {
                    cursorContact.moveToFirst();
                    Contact contact = DatabaseManager.mapContact(cursorContact);
                    if (contact.getMergedContact() != null) {
                        cursorContactDetails.close();
                        cursorContact.close();
                        return contact.getParent();
                    } else {
                        cursorContactDetails.close();
                        cursorContact.close();
                        return contact;
                    }
                } else {
                    cursorContactDetails.close();
                    cursorContact.close();
                    return null;
                }
            } else {
                cursorContactDetails.close();
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static ArrayList<Contact> getAllContacts(Uri contactTable, String query, String orderBy, String[] selectionArgs) {
        try {
            ArrayList<Contact> contacts = new ArrayList<Contact>();
//            Cursor cursor = TactApplication.getInstance().getContentResolver().query(DatabaseContentProvider.URI_ALL_CONTACTS,
//                    new String[]{"*", com.tactile.tact.database.model.Contact.ROW_PRIMARY_KEY},
//                    null,
//                    null,
//                    orderBy);

            Cursor cursor = getGeneralCursor(
                    contactTable,
                    null,
                    new String[]{"*"},
                    selectionArgs,
                    query,
                    orderBy
            );

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Contact ct = DatabaseManager.mapContact(cursor);
                    if (ct.getDisplayName() != null) {
                        contacts.add(ct);
                    }
                }
            }
            cursor.close();
            return  contacts;
        } catch (Exception e) {
            return null;
        }
    }

    public static ArrayList<Contact> getCompanyEmployes (Contact company) {
        ArrayList<Contact> contactList = new ArrayList<Contact>();

        if (company.isCompany()) {
            Cursor cursor = TactApplication.getInstance().getContentResolver().query(DatabaseContentProvider.URI_CONTACT_DETAILS,
                    new String[]{ContactDetails.ROW_CONTACT_KEY},
                    ContactDetails.ROW_COMPANYID + "=?",
                    new String[]{company.getRecordID()},
                    null);
            if (cursor.getCount() > 0){
                ArrayList<String> contact_ids = new ArrayList<>();
                while (cursor.moveToNext()){
                    contact_ids.add(cursor.getString(0));
                }
                if (contact_ids.size() > 0){
                    contactList = getContactByIds(contact_ids);
                }
            }
            cursor.close();
        }
        
        return contactList;
    }

    public static ArrayList<Contact> getAllPersons() {
        return getAllPersons(DatabaseContentProvider.URI_ALL_PRIMARY_CONTACTS);
    }

    public static ArrayList<Contact> getAllStarredPersons(){
        return getAllPersons(DatabaseContentProvider.URI_STARRED_CONTACTS);
    }

    public static ArrayList<Contact> getAllVisitedPersons(){
        return getAllPersons(DatabaseContentProvider.URI_VISITED_CONTACTS);
    }

    public static ArrayList<Contact> getAllPersons(Uri uri){
        try {
            ArrayList<Contact> contacts = new ArrayList<Contact>();
            Cursor cursor = TactApplication.getInstance().getContentResolver().query(uri,
                    new String[]{"*", com.tactile.tact.database.model.Contact.ROW_PRIMARY_KEY},
                    getSelection(ContactDetails.ROW_ENTITY_PRIMARY_KEY, new String[]{"3"}),
                    new String[]{"3"},
                    "UPPER( " + com.tactile.tact.database.model.Contact.ROW_DISPLAY_NAME + ")");

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Contact contact = DatabaseManager.mapContact(cursor);
                    if (contact.getDisplayName() != null && !contact.getDisplayName().isEmpty()) {
                        contacts.add(contact);
                    }
                }
            }
            cursor.close();
            return  contacts;
        } catch (Exception e) {
            return null;
        }
    }

    public static int getContactsTotalCount(int mode) {
        try {
            String selection = "";
            ArrayList<String> selectionArgsArrayList = new ArrayList<String>();
            String[] selectionArgs = null;

            if (mode == TactConst.CONTACT_LIST_MODE_RECENT) {
                if (selection != null && !selection.isEmpty()) {
                    selection += " AND ";
                }
                selection += com.tactile.tact.database.model.Contact.ROW_VISITED_AT + ">?";
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, -30);
                selectionArgsArrayList.add(String.valueOf(calendar.getTimeInMillis()));
            }

            if (mode == TactConst.CONTACT_LIST_MODE_STARRED) {
                if (selection != null && !selection.isEmpty()) {
                    selection += " AND ";
                }
                selection += com.tactile.tact.database.model.Contact.ROW_IS_STARRED + "=?";
                selectionArgsArrayList.add("1");
            }

            if (selection.isEmpty()) {
                selection = null;
            }
            if (selectionArgsArrayList != null || selectionArgsArrayList.size() > 0) {
                selectionArgs = new String[selectionArgsArrayList.size()];
                selectionArgsArrayList.toArray(selectionArgs);
            }

            Cursor cursor = TactApplication.getInstance().getContentResolver().query(DatabaseContentProvider.URI_ALL_CONTACTS,
                    new String[]{"*", com.tactile.tact.database.model.Contact.ROW_PRIMARY_KEY},
                    selection,
                    selectionArgs,
                    "UPPER( " + com.tactile.tact.database.model.Contact.ROW_DISPLAY_NAME + ")");
            int count  = cursor.getCount();
            cursor.close();
            return count;
        } catch (Exception e) {
            return 0;
        }
    }

    public static String getFirstContactId(int mode) {
        try {
            String selection = "";
            ArrayList<String> selectionArgsArrayList = new ArrayList<String>();
            String[] selectionArgs = null;

            if (mode == TactConst.CONTACT_LIST_MODE_RECENT) {
                if (selection != null && !selection.isEmpty()) {
                    selection += " AND ";
                }
                selection += com.tactile.tact.database.model.Contact.ROW_VISITED_AT + ">?";
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, -30);
                selectionArgsArrayList.add(String.valueOf(calendar.getTimeInMillis()));
            }

            if (mode == TactConst.CONTACT_LIST_MODE_STARRED) {
                if (selection != null && !selection.isEmpty()) {
                    selection += " AND ";
                }
                selection += com.tactile.tact.database.model.Contact.ROW_IS_STARRED + "=?";
                selectionArgsArrayList.add("1");
            }

            if (selection.isEmpty()) {
                selection = null;
            }
            if (selectionArgsArrayList != null || selectionArgsArrayList.size() > 0) {
                selectionArgs = new String[selectionArgsArrayList.size()];
                selectionArgsArrayList.toArray(selectionArgs);
            }

            Cursor cursor = TactApplication.getInstance().getContentResolver().query(DatabaseContentProvider.URI_ALL_CONTACTS,
                    new String[]{"*", com.tactile.tact.database.model.Contact.ROW_PRIMARY_KEY},
                    selection,
                    selectionArgs,
                    "UPPER( " + com.tactile.tact.database.model.Contact.ROW_DISPLAY_NAME + ") LIMIT " + String.valueOf(40));

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Contact ct = DatabaseManager.mapContact(cursor);
                    cursor.close();
                    return ct.getRecordID();
                }
            }
            cursor.close();
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static ArrayList<Contact> loadContacts(String firstVisibleContact, String lastVisibleContact, String filter, int direction, int mode, Contact backContact, int contactType) {
        try {
            ArrayList<Contact> contacts = new ArrayList<Contact>();

            String selection = "";
            ArrayList<String> selectionArgsArrayList = new ArrayList<String>();
            String[] selectionArgs = null;
            String sort = "UPPER( " + com.tactile.tact.database.model.Contact.ROW_DISPLAY_NAME + ") LIMIT " + String.valueOf(40);

            if (filter != null && !filter.isEmpty()) {
                selection = com.tactile.tact.database.model.Contact.ROW_DISPLAY_NAME + " LIKE ? COLLATE NOCASE";
                selectionArgsArrayList.add("%" + filter + "%");
            }

            if (contactType == TactConst.CONTACT_LIST_LOAD_PERSONS) {
                if (selection != null && !selection.isEmpty()) {
                    selection += " AND ";
                }
                selection += com.tactile.tact.database.model.Contact.ROW_ENTITY_PRIMARY_KEY + "=?";
                selectionArgsArrayList.add("3");
            } else if (contactType == TactConst.CONTACT_LIST_LOAD_COMPANIES) {
                if (selection != null && !selection.isEmpty()) {
                    selection += " AND ";
                }
                selection += com.tactile.tact.database.model.Contact.ROW_ENTITY_PRIMARY_KEY + "=?";
                selectionArgsArrayList.add("2");
            }

            if (mode == TactConst.CONTACT_LIST_MODE_RECENT) {
                if (selection != null && !selection.isEmpty()) {
                    selection += " AND ";
                }
                selection += com.tactile.tact.database.model.Contact.ROW_VISITED_AT + ">?";
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, -30);
                selectionArgsArrayList.add(String.valueOf(calendar.getTimeInMillis()));
            }

            if (mode == TactConst.CONTACT_LIST_MODE_STARRED) {
                if (selection != null && !selection.isEmpty()) {
                    selection += " AND ";
                }
                selection += com.tactile.tact.database.model.Contact.ROW_IS_STARRED + "=?";
                selectionArgsArrayList.add("1");
            }

            if (backContact != null) {
                if (selection != null && !selection.isEmpty()) {
                    selection += " AND ";
                }
//                if (mode == TactConst.CONTACT_LIST_MODE_RECENT || mode == TactConst.CONTACT_LIST_MODE_STARRED) {
//                    selection += com.tactile.tact.database.model.Contact.ROW_DISPLAY_NAME + ">? COLLATE NOCASE";
//                } else {
                    selection += com.tactile.tact.database.model.Contact.ROW_DISPLAY_NAME + ">=? COLLATE NOCASE";
//                }
                selectionArgsArrayList.add(backContact.getDisplayName());
            } else {
                if (direction == TactConst.SCROLL_DIRECTION_UP && firstVisibleContact != null && !firstVisibleContact.isEmpty()) {
                    if (selection != null && !selection.isEmpty()) {
                        selection += " AND ";
                    }
                    selection += com.tactile.tact.database.model.Contact.ROW_DISPLAY_NAME + "<? COLLATE NOCASE";
                    selectionArgsArrayList.add(firstVisibleContact);
                    sort = "UPPER( " + com.tactile.tact.database.model.Contact.ROW_DISPLAY_NAME + ") DESC LIMIT " + String.valueOf(40);
                } else if (direction == TactConst.SCROLL_DIRECTION_DOWN && lastVisibleContact != null && !lastVisibleContact.isEmpty()) {
                    if (selection != null && !selection.isEmpty()) {
                        selection += " AND ";
                    }
                    selection += com.tactile.tact.database.model.Contact.ROW_DISPLAY_NAME + ">? COLLATE NOCASE";
                    selectionArgsArrayList.add(lastVisibleContact);
                }
            }


            if (selection.isEmpty()) {
                selection = null;
            }
            if (selectionArgsArrayList != null || selectionArgsArrayList.size() > 0) {
                selectionArgs = new String[selectionArgsArrayList.size()];
                selectionArgsArrayList.toArray(selectionArgs);
            }

            Cursor cursor = TactApplication.getInstance().getContentResolver().query(DatabaseContentProvider.URI_ALL_PRIMARY_CONTACTS,
                    new String[]{"*", com.tactile.tact.database.model.Contact.ROW_PRIMARY_KEY},
                    selection,
                    selectionArgs,
                    sort);

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Contact ct = DatabaseManager.mapContact(cursor);
                    if (ct.getDisplayName() != null) {
                        contacts.add(DatabaseManager.mapContact(cursor));
                    }
                }
            }
            cursor.close();
            return  contacts;
        } catch (Exception e) {
            return null;
        }
    }

    public static ArrayList<Contact> getAllCompanies() {
        return getAllCompanies(DatabaseContentProvider.URI_ALL_CONTACTS);
    }

    public static ArrayList<Contact> getAllStarredCompanies() {
        return getAllCompanies(DatabaseContentProvider.URI_STARRED_CONTACTS);
    }

    public static ArrayList<Contact> getAllVisitedCompanies() {
        return getAllCompanies(DatabaseContentProvider.URI_VISITED_CONTACTS);
    }

    public static ArrayList<Contact> getAllCompanies(Uri uri) {
        try {
            ArrayList<Contact> contacts = new ArrayList<Contact>();
            Cursor cursor = TactApplication.getInstance().getContentResolver().query(uri,
                    new String[]{"*", com.tactile.tact.database.model.Contact.ROW_PRIMARY_KEY},
                    getSelection(ContactDetails.ROW_ENTITY_PRIMARY_KEY, new String[]{"2"}),
                    new String[]{"2"},
                    "UPPER( " + com.tactile.tact.database.model.Contact.ROW_DISPLAY_NAME + ")");
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Contact contact = DatabaseManager.mapContact(cursor);
                    if (contact.getDisplayName() != null && !contact.getDisplayName().isEmpty()) {
                        contacts.add(contact);
                    }
                }
            }
            cursor.close();
            return  contacts;
        } catch (Exception e) {
            return null;
        }
    }

    public static void setStarredContact(Contact contact){
        ContentValues valuesContact = DatabaseManager.setContactValues(contact);

        TactApplication.getInstance().getContentResolver().update(
                Uri.withAppendedPath(DatabaseContentProvider.URI_STARRED_CONTACTS, contact.getId().toString()),
                valuesContact,
                null,
                null);
    }

    public static void setVisitedContact(Contact contact){
        long timestamp = Calendar.getInstance().getTimeInMillis();
        contact.setVisitedAt(timestamp);

        ContentValues valuesContact = DatabaseManager.setContactValues(contact);

        TactApplication.getInstance().getContentResolver().update(
                Uri.withAppendedPath(DatabaseContentProvider.URI_VISITED_CONTACTS, contact.getId().toString()),
                valuesContact,
                null,
                null);
    }

    public static Contact getContactById(Integer id) {
        try {
            Contact contact = new Contact();
            Cursor cursor = TactApplication.getInstance().getContentResolver().query(DatabaseContentProvider.URI_ALL_CONTACTS,
                    new String[]{"*", com.tactile.tact.database.model.Contact.ROW_PRIMARY_KEY},
                    getSelection(com.tactile.tact.database.model.Contact.ROW_PRIMARY_KEY, new String[]{id.toString()}),
                    new String[]{id.toString()},
                    null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                contact = DatabaseManager.mapContact(cursor);
                cursor.close();
            } else {
                cursor.close();
                return null;
            }
            return contact;
        } catch (Exception e) {
            return null;
        }
    }

    public static ArrayList<Contact> getContactByIds(ArrayList<String> idList) {
        try {
            String[] ids = new String[idList.size()];
            int i=0;
            for (String id: idList){
                ids[i] = id;
                i++;
            }
            ArrayList<Contact> contacts = new ArrayList<Contact>();
            Cursor cursor = TactApplication.getInstance().getContentResolver().query(DatabaseContentProvider.URI_ALL_CONTACTS,
                    new String[]{"*", com.tactile.tact.database.model.Contact.ROW_PRIMARY_KEY},
                    getSelection(com.tactile.tact.database.model.Contact.ROW_PRIMARY_KEY, ids),
                    ids,
                    null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    contacts.add(DatabaseManager.mapContact(cursor));
                }

            }
            cursor.close();
            return contacts;
        } catch (Exception e) {
            return new ArrayList<Contact>();
        }
    }

    public static Contact getContactByRecordId(String recordId) {
        try {
            Contact contact = new Contact();
            Cursor cursor = TactApplication.getInstance().getContentResolver().query(DatabaseContentProvider.URI_ALL_CONTACTS,
                    new String[]{"*", com.tactile.tact.database.model.Contact.ROW_PRIMARY_KEY},
                    getSelection(com.tactile.tact.database.model.Contact.ROW_RECORD_ID, new String[]{recordId}),
                    new String[]{recordId},
                    null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                contact = DatabaseManager.mapContact(cursor);
                cursor.close();
            } else {
                cursor.close();
                return null;
            }
            return contact;
        } catch (Exception e) {
            return null;
        }
    }

    public static ArrayList<Contact> getContactByRecordIds(String[] recordIds) {
        try {
            ArrayList<Contact> contacts = new ArrayList<Contact>();
            Cursor cursor = TactApplication.getInstance().getContentResolver().query(DatabaseContentProvider.URI_ALL_CONTACTS,
                    new String[]{"*", com.tactile.tact.database.model.Contact.ROW_PRIMARY_KEY},
                    getSelection(com.tactile.tact.database.model.Contact.ROW_RECORD_ID, recordIds),
                    recordIds,
                    null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    contacts.add(DatabaseManager.mapContact(cursor));
                }
            }
            cursor.close();
            return  contacts;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get Device Synced Contacts
     * @param resolver
     * @return the list of contact device ids
     */
    public static List<String> getLocalContacts(ContentResolver resolver) {
        List<String> result = new ArrayList<String>();
        Cursor cursor = resolver.query(DatabaseContentProvider.URI_CONTACTS_WITH_DETAILS,
                new String[]{ContactDetails.ROW_REMOTE_ID},
                com.tactile.tact.database.model.Contact.ROW_IS_SALESFORCE + "=0 AND " +
                        com.tactile.tact.database.model.Contact.ROW_IS_GOOGLE + "=0 AND " +
                        com.tactile.tact.database.model.Contact.ROW_IS_EXCHANGE + "=0",
                null,
                null);
        try
        {
            while (cursor.moveToNext())
            {
                result.add(cursor.getString(0));
            }
        }
        catch (Exception e){}
        cursor.close();
        return result;
    }

    public static Boolean updateContact(ContactAPI.ContactInfo contactInfo){
        Contact contact = new Contact(contactInfo);
        return updateContact(contact);
    }

    public static Boolean updateContact(Contact contact) {
        try {

            ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

            //Update fields of Contact Table
            ContentValues valuesContact = DatabaseManager.setContactValues(contact);

            // Add the query to the batch
            operations.add(ContentProviderOperation.newUpdate(Uri.withAppendedPath(DatabaseContentProvider.URI_ALL_CONTACTS, contact.getId().toString()))
                    .withValues(valuesContact)
                    .build());

            for (ContactDetail contactDetail : contact.getContactDetails()) {
                //Update fields of ContactDetails Table
                ContentValues valuesContactDetails = DatabaseManager.setContactDetailValues(contactDetail);

                // Add the query to the batch
                operations.add(ContentProviderOperation.newUpdate(Uri.withAppendedPath(DatabaseContentProvider.URI_CONTACT_DETAILS, contactDetail.getId().toString()))
                        .withValues(valuesContactDetails)
                        .build());
            }

            // Apply the batch of operations
            TactApplication.getInstance().getContentResolver().applyBatch(DatabaseContentProvider.AUTHORITY, operations);
        }
        catch (Exception e)
        {
            return false;
        }

        return true; // all ok
    }

    public static List<Opportunity> getOpportunities(ContentResolver resolver, String[] ids){
        List<Opportunity> results = new ArrayList<Opportunity>();

        Cursor cursor = resolver.query(DatabaseContentProvider.URI_OPPORTUNITIES,
                new String[]{"*"},
                getSelection(com.tactile.tact.database.model.Opportunity.ROW_IDENTIFIER, ids),
                ids,
                null);

        try {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Opportunity current = new Opportunity();
                    current.setID(getData(cursor, com.tactile.tact.database.model.Opportunity.ROW_IDENTIFIER));
                    current.setIsClosed(getData(cursor, com.tactile.tact.database.model.Opportunity.ROW_IS_CLOSED).equals("1"));
                    current.setIsWon(getData(cursor, com.tactile.tact.database.model.Opportunity.ROW_IS_WON).equals("1"));
                    current.setCompany(getData(cursor, com.tactile.tact.database.model.Opportunity.ROW_COMPANY));
                    current.setSource(getData(cursor, com.tactile.tact.database.model.Opportunity.ROW_SOURCE));
                    current.setCloseDate(getData(cursor, com.tactile.tact.database.model.Opportunity.ROW_CLOSE_DATE));
                    current.setAmount(getData(cursor, com.tactile.tact.database.model.Opportunity.ROW_AMOUNT));
                    current.setExpectedRevenue(getData(cursor, com.tactile.tact.database.model.Opportunity.ROW_EXPECTED_REVENUE));
                    current.setProbability(getData(cursor, com.tactile.tact.database.model.Opportunity.ROW_PROBABILITY));
                    current.setQuantity(getData(cursor, com.tactile.tact.database.model.Opportunity.ROW_QUANTITY));
                    current.setForecastCategory(getData(cursor, com.tactile.tact.database.model.Opportunity.ROW_FORECAST_CATEGORY));
                    current.setLeadSource(getData(cursor, com.tactile.tact.database.model.Opportunity.ROW_LEAD_SOURCE));
                    current.setName(getData(cursor, com.tactile.tact.database.model.Opportunity.ROW_NAME));
                    current.setNextStep(getData(cursor, com.tactile.tact.database.model.Opportunity.ROW_NEXT_STEP));
                    current.setDescription(getData(cursor, com.tactile.tact.database.model.Opportunity.ROW_OPPORTUNITY_DESCRIPTION));
                    current.setOwnerID(getData(cursor, com.tactile.tact.database.model.Opportunity.ROW_OWNER_ID));
                    current.setOwnerName(getData(cursor, com.tactile.tact.database.model.Opportunity.ROW_OWNER_NAME));
                    current.setStageName(getData(cursor, com.tactile.tact.database.model.Opportunity.ROW_STAGE_NAME));
                    current.setType(getData(cursor, com.tactile.tact.database.model.Opportunity.ROW_TYPE));

                    results.add(current);
                }
            }
        }
        catch (Exception e) {}

        cursor.close();

        return results;
    }

    public static Boolean updateOpportunity(ContentResolver resolver, Opportunity opportunity) {
        ContentValues values = new ContentValues();
        values.put(com.tactile.tact.database.model.Opportunity.ROW_IS_CLOSED, opportunity.getIsClosed());
        values.put(com.tactile.tact.database.model.Opportunity.ROW_IS_WON, opportunity.getIsWon());
        values.put(com.tactile.tact.database.model.Opportunity.ROW_COMPANY, setData(opportunity.getCompany()));
        values.put(com.tactile.tact.database.model.Opportunity.ROW_SOURCE, setData(opportunity.getSource()));
        values.put(com.tactile.tact.database.model.Opportunity.ROW_CLOSE_DATE, setData(opportunity.getCloseDate()));
        values.put(com.tactile.tact.database.model.Opportunity.ROW_AMOUNT, setData(opportunity.getAmount()));
        values.put(com.tactile.tact.database.model.Opportunity.ROW_EXPECTED_REVENUE, setData(opportunity.getExpectedRevenue()));
        values.put(com.tactile.tact.database.model.Opportunity.ROW_PROBABILITY, setData(opportunity.getProbability()));
        values.put(com.tactile.tact.database.model.Opportunity.ROW_QUANTITY, setData(opportunity.getQuantity()));
        values.put(com.tactile.tact.database.model.Opportunity.ROW_FORECAST_CATEGORY, setData(opportunity.getForecastCategory()));
        values.put(com.tactile.tact.database.model.Opportunity.ROW_LEAD_SOURCE, setData(opportunity.getLeadSource()));
        values.put(com.tactile.tact.database.model.Opportunity.ROW_NAME, setData(opportunity.getName()));
        values.put(com.tactile.tact.database.model.Opportunity.ROW_NEXT_STEP, setData(opportunity.getNextStep()));
        values.put(com.tactile.tact.database.model.Opportunity.ROW_OPPORTUNITY_DESCRIPTION, setData(opportunity.getDescription()));
        values.put(com.tactile.tact.database.model.Opportunity.ROW_OWNER_ID, setData(opportunity.getOwnerID()));
        values.put(com.tactile.tact.database.model.Opportunity.ROW_OWNER_NAME, setData(opportunity.getOwnerName()));
        values.put(com.tactile.tact.database.model.Opportunity.ROW_STAGE_NAME, setData(opportunity.getStageName()));
        values.put(com.tactile.tact.database.model.Opportunity.ROW_TYPE, setData(opportunity.getType()));

        try
        {
            resolver.update(DatabaseContentProvider.URI_OPPORTUNITIES, values,
                    getSelection(com.tactile.tact.database.model.Opportunity.ROW_IDENTIFIER, new String[]{opportunity.getID()}),
                    new String[]{opportunity.getID()});
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }

    public static String getAccountId(String account){
        Cursor cursor = TactApplication.getInstance().getContentResolver().query(DatabaseContentProvider.URI_SOURCES,
                new String[]{Source.ROW_IDENTIFIER}, Source.ROW_TYPE + " = ?", new String[]{account}, null);
        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            return cursor.getString(0);
        }
        cursor.close();
        return null;
    }


    private static String getData(Cursor cursor, String key){
        return cursor.getString(cursor.getColumnIndex(key)) != null ? cursor.getString(cursor.getColumnIndex(key)) : "";
    }

    private static String setData(String data){
        return data != null && !data.isEmpty() ? data : null;
    }

    public static String getSelection(String key, String[] values){
        String selection = null;
        if (values != null && values.length > 0 && key != null) {
            String listQuery = "?";
            for (int i=0; i< values.length-1; i++) {
                listQuery+=",?";
            }
            selection = key + " IN (" + listQuery + ")";
        }
        return selection;
    }

    public static HashMap<String, Integer> getSyncRevision(){
        Cursor cursor = TactApplication.getInstance().getContentResolver().query(DatabaseContentProvider.URI_SYNC_STATUS, new String[]{"*"}, null, null, null);
        HashMap<String, Integer> result  = new HashMap<String, Integer>();
        Integer opportunities_revision   = 0;
        Integer revision                 = 0;
        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            opportunities_revision  = Integer.parseInt(cursor.getString(cursor.getColumnIndex(SyncStatus.ROW_OPPORTUNITIES_REVISION)));
            revision                = Integer.parseInt(cursor.getString(cursor.getColumnIndex(SyncStatus.ROW_REVISION)));
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(SyncStatus.ROW_REVISION, revision);
            contentValues.put(SyncStatus.ROW_OPPORTUNITIES_REVISION, opportunities_revision);
            TactApplication.getInstance().getContentResolver().insert(DatabaseContentProvider.URI_SYNC_STATUS, contentValues);
        }
        result.put(TactConst.SYNC_OPPORTUNITIES_REVISION, opportunities_revision);
        result.put(TactConst.SYNC_REVISION, revision);
        cursor.close();
        return result;
    }

    public static void setSyncRevision(int revision, int opportunitiesRevision) throws Exception {
        try {
            Cursor cursor = TactApplication.getInstance().getContentResolver().query(DatabaseContentProvider.URI_SYNC_STATUS, new String[]{"*"}, null, null, null);
            if (cursor.getCount() > 0){
                cursor.moveToFirst();
                ContentValues contentValues = new ContentValues();
                contentValues.put(SyncStatus.ROW_REVISION, revision);
                contentValues.put(SyncStatus.ROW_OPPORTUNITIES_REVISION, opportunitiesRevision);
                TactApplication.getInstance().getContentResolver().update(DatabaseContentProvider.URI_SYNC_STATUS, contentValues,
                        getSelection(SyncStatus.ROW_PRIMARY_KEY, new String[]{cursor.getString(cursor.getColumnIndex(SyncStatus.ROW_PRIMARY_KEY))}),
                        new String[]{cursor.getString(cursor.getColumnIndex(SyncStatus.ROW_PRIMARY_KEY))});
            } else {
                ContentValues contentValues = new ContentValues();
                contentValues.put(SyncStatus.ROW_REVISION, revision);
                contentValues.put(SyncStatus.ROW_OPPORTUNITIES_REVISION, opportunitiesRevision);
                TactApplication.getInstance().getContentResolver().insert(DatabaseContentProvider.URI_SYNC_STATUS, contentValues);
            }
            cursor.close();
        } catch (Exception e) {
            throw e;
        }
    }

    public static void saveContact(Contact contact) throws Exception {
        try {
            Cursor cursor = TactApplication.getInstance().getContentResolver().query(DatabaseContentProvider.URI_ALL_CONTACTS,
                    new String[]{"*", com.tactile.tact.database.model.Contact.ROW_PRIMARY_KEY},
                    getSelection(com.tactile.tact.database.model.Contact.ROW_RECORD_ID, new String[]{contact.getRecordID()}),
                    new String[]{contact.getRecordID()},
                    null);
            if (cursor.getCount() > 0){
                cursor.moveToFirst();
                TactApplication.getInstance().getContentResolver().update(DatabaseContentProvider.URI_ALL_CONTACTS, DatabaseManager.setContactValues(contact),
                        getSelection(com.tactile.tact.database.model.Contact.ROW_PRIMARY_KEY, new String[]{cursor.getString(cursor.getColumnIndex(com.tactile.tact.database.model.Contact.ROW_PRIMARY_KEY))}),
                        new String[]{cursor.getString(cursor.getColumnIndex(com.tactile.tact.database.model.Contact.ROW_PRIMARY_KEY))});
            } else {
                TactApplication.getInstance().getContentResolver().insert(DatabaseContentProvider.URI_ALL_CONTACTS, DatabaseManager.setContactValues(contact));
            }
            cursor.close();
            if (contact.getContactDetails() != null && contact.getContactDetails().size() > 0) {
                for (ContactDetail contactDetail : contact.getContactDetails()) {
                    DatabaseManager.saveContactDetail(contactDetail, contact.getRecordID());
                }
            }
            if (contact.getCustomFieldObjects() != null && contact.getCustomFieldObjects().size() > 0) {
                for (CustomFieldObject customFieldObject : contact.getCustomFieldObjects()) {
                    DatabaseManager.saveCustomField(customFieldObject, contact.getRecordID());
                }
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public static void saveContactDetail(ContactDetail contactDetail, String contactRecordId) throws Exception {
        try {
            Cursor cursor = TactApplication.getInstance().getContentResolver().query(DatabaseContentProvider.URI_CONTACT_DETAILS,
                    new String[]{"*", ContactDetails.ROW_PRIMARY_KEY},
                    getSelection(ContactDetails.ROW_PRIMARY_KEY, new String[]{String.valueOf(contactDetail.getId())}),
                    new String[]{String.valueOf(contactDetail.getId())},
                    null);
            if (cursor.getCount() > 0){
                cursor.moveToFirst();
                TactApplication.getInstance().getContentResolver().update(DatabaseContentProvider.URI_CONTACT_DETAILS, DatabaseManager.setContactDetailValues(contactDetail),
                        getSelection(ContactDetails.ROW_PRIMARY_KEY, new String[]{cursor.getString(cursor.getColumnIndex(ContactDetails.ROW_PRIMARY_KEY))}),
                        new String[]{cursor.getString(cursor.getColumnIndex(ContactDetails.ROW_PRIMARY_KEY))});
            } else {
                Cursor cursorContact = TactApplication.getInstance().getContentResolver().query(DatabaseContentProvider.URI_ALL_CONTACTS,
                        new String[]{"*", com.tactile.tact.database.model.Contact.ROW_PRIMARY_KEY},
                        getSelection(com.tactile.tact.database.model.Contact.ROW_RECORD_ID, new String[]{contactRecordId}),
                        new String[]{contactRecordId},
                        null);
                if (cursorContact.getCount() > 0) {
                    cursorContact.moveToFirst();
                    contactDetail.setContact(cursorContact.getInt(cursor.getColumnIndex(ContactDetails.ROW_PRIMARY_KEY)));
                    TactApplication.getInstance().getContentResolver().insert(DatabaseContentProvider.URI_CONTACT_DETAILS, DatabaseManager.setContactDetailValues(contactDetail));
                }
                cursorContact.close();
            }
            cursor.close();
        } catch (Exception e) {
            throw e;
        }
    }

    public static void saveCustomField(CustomFieldObject customFieldObject, String contactRecordId) throws Exception {
        try {
            Cursor cursor = TactApplication.getInstance().getContentResolver().query(DatabaseContentProvider.URI_CUSTOM_FIELD,
                    new String[]{"*", CustomField.ROW_PRIMARY_KEY},
                    getSelection(CustomField.ROW_PRIMARY_KEY, new String[]{String.valueOf(customFieldObject.getId())}),
                    new String[]{String.valueOf(customFieldObject.getId())},
                    null);
            if (cursor.getCount() > 0){
                cursor.moveToFirst();
                TactApplication.getInstance().getContentResolver().update(DatabaseContentProvider.URI_CUSTOM_FIELD, DatabaseManager.setCustomFieldValues(customFieldObject),
                        getSelection(CustomField.ROW_PRIMARY_KEY, new String[]{cursor.getString(cursor.getColumnIndex(CustomField.ROW_PRIMARY_KEY))}),
                        new String[]{cursor.getString(cursor.getColumnIndex(CustomField.ROW_PRIMARY_KEY))});
            } else {
                Cursor cursorContact = TactApplication.getInstance().getContentResolver().query(DatabaseContentProvider.URI_ALL_CONTACTS,
                        new String[]{"*", com.tactile.tact.database.model.Contact.ROW_PRIMARY_KEY},
                        getSelection(com.tactile.tact.database.model.Contact.ROW_RECORD_ID, new String[]{contactRecordId}),
                        new String[]{contactRecordId},
                        null);
                if (cursorContact.getCount() > 0) {
                    cursorContact.moveToFirst();
                    customFieldObject.setContact(cursorContact.getInt(cursor.getColumnIndex(ContactDetails.ROW_PRIMARY_KEY)));
                    TactApplication.getInstance().getContentResolver().insert(DatabaseContentProvider.URI_CUSTOM_FIELD, DatabaseManager.setCustomFieldValues(customFieldObject));
                }
                cursorContact.close();
            }
            cursor.close();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Return a event cursor to iterate over db entities
     * @param table --> table uri (table name for sql)
     * @param filter --> object or list of object that contains the table field to query
     * @param projection --> string list of field to get
     * @param selectionArgs --> data to do the filter (example: 'john' to filter by field name)
     * @return
     */
    public static Cursor getGeneralCursor(Uri table, ContactQueryFilter filter, String[] projection, String[] selectionArgs, String customQuery, String orderBy) {

        ContentResolver cr = TactApplication.getInstance().getContentResolver();

        String selection = null;
        if (customQuery == null && selectionArgs != null && selectionArgs.length>0 && filter != null) {
            String listQuery = "?";
            for (int i=0; i< selectionArgs.length-1; i++) {
                listQuery+=",?";
            }

            if (selection == null) {
                selection = DatabaseManager.getContactQueryField(filter) + " IN (" + listQuery + ")";
            }

        } else {
            selection = customQuery;
            selectionArgs = null;
        }

        Cursor calendarCursor = cr.query(
                table,
                projection,
                selection,
                (selectionArgs != null && selectionArgs.length>0)?selectionArgs:null,
                orderBy
        );

        return calendarCursor;
    }
}
