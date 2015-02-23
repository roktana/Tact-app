package com.tactile.tact.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;

import com.tactile.tact.activities.TactApplication;
import com.tactile.tact.consts.ContactType;
import com.tactile.tact.consts.IconType;
import com.tactile.tact.database.DatabaseContentProvider;
import com.tactile.tact.database.model.Contact;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sebafonseca on 9/22/14.
 */
public class ContactAPI {
    //****************************  STATIC CRUD ITEMS *****************************************\\

    private static Uri contact_table            = ContactsContract.Contacts.CONTENT_URI;
    private static Uri contactData_table        = ContactsContract.Data.CONTENT_URI;
    private static Uri tact_contacts_table      = DatabaseContentProvider.URI_ALL_CONTACTS;
    private static Uri contactEmail_table       = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
    private static Uri contactPhone_table       = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;


    private static String structuredName_cit    = ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE;
    private static String firstName             = ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME;
    private static String lastName              = ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME;

    private static String structuredPostal_cit  = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE;
    private static String city                  = ContactsContract.CommonDataKinds.StructuredPostal.CITY;
    private static String region                = ContactsContract.CommonDataKinds.StructuredPostal.REGION;
    private static String country               = ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY;
    private static String postcode              = ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE;
    private static String formated_address      = ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS;
    private static String street                = ContactsContract.CommonDataKinds.StructuredPostal.STREET;

    private static String organization_cit      = ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE;
    private static String company               = ContactsContract.CommonDataKinds.Organization.COMPANY;
    private static String org_title             = ContactsContract.CommonDataKinds.Organization.TITLE;
    private static String org_department        = ContactsContract.CommonDataKinds.Organization.DEPARTMENT;

    private static String website_cit           = ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE;
    private static String websiteUrl            = ContactsContract.CommonDataKinds.Website.URL;

    private static String notes_cit             = ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE;
    private static String note                  = ContactsContract.CommonDataKinds.Note.NOTE;

    private static String email_cit             = ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE;
    private static String emailLabel            = ContactsContract.CommonDataKinds.Email.LABEL;
    private static String emailType             = ContactsContract.CommonDataKinds.Email.TYPE;
    private static String email                 = ContactsContract.CommonDataKinds.Email.DATA;

    private static String phone_cit             = ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE;
    private static String phoneLabel            = ContactsContract.CommonDataKinds.Phone.LABEL;
    private static String phoneType             = ContactsContract.CommonDataKinds.Phone.TYPE;
    private static String phoneNumber           = ContactsContract.CommonDataKinds.Phone.NUMBER;




    public static String[] CONTACT_PROJECTION = new String[] {
            ContactsContract.Contacts._ID,
    };



    // Enum to determinate the 'where' of the query
    public static enum QueryFilter{
        ByContactId,
        ByEmailId,
        ByPhoneId,
        ByTactContactId
    }

    /**
     * Contact object inner class
     */
    public static class ContactInfo implements Serializable{
        private int id;
        private String record_id;
        private String first_name;
        private String last_name;
        private String business_city;
        private String business_state;
        private String business_country;
        private String business_postal_code;
        private String business_address;
        private String business_street;
        private String company_name;
        private String job_title;
        private String department;
        private String website;
        private String notes;
        private String personal_email;
        private String business_email;
        private String other_email;
        private String home_phone;
        private String business_phone;
        private String mobile_phone;
        private boolean isStarred;
        private IconType iconType;
        private ContactType contactType;
        private String initials;
        private String subtitle;
        private String displayName;

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public void setSubtitle(String subtitle) {
            this.subtitle = subtitle;
        }

        public String getInitials() {
            return initials;
        }

        public void setInitials(String initials) {
            this.initials = initials;
        }

        public ContactType getContactType() {
            return contactType;
        }

        public void setContactType(ContactType contactType) {
            this.contactType = contactType;
        }

        public IconType getIconType() {
            return iconType;
        }

        public void setIconType(IconType iconType) {
            this.iconType = iconType;
        }

        public boolean isStarred() {
            return isStarred;
        }

        public void setStarred(boolean isStarred) {
            this.isStarred = isStarred;
        }

        public ContactInfo() {}

        public ContactInfo(int id) {
            this.id = id;
        }

        public String getRecord_id() {
            return record_id;
        }

        public void setRecord_id(String record_id) {
            this.record_id = record_id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setFirstName(String first_name) {
            this.first_name = validateValue(first_name, this.first_name);
        }

        public void setLastName(String last_name) {
            this.last_name = validateValue(last_name, this.last_name);
        }

        public void setBusinessCity(String business_city) {
            this.business_city = validateValue(business_city, this.business_city);
        }

        public void setBusinessState(String business_state) {
            this.business_state = validateValue(business_state, this.business_state);
        }

        public void setBusinessCountry(String business_country) {
            this.business_country = validateValue(business_country, this.business_country);
        }

        public void setBusinessPostalCode(String business_postal_code) {
            this.business_postal_code = validateValue(business_postal_code, this.business_postal_code);
        }

        public void setBusinessAddress(String business_address) {
            this.business_address = validateValue(business_address, this.business_address);
        }

        public void setBusinessStreet(String business_street) {
            this.business_street = validateValue(business_street, this.business_street);
        }

        public void setCompanyName(String company_name) {
            this.company_name = validateValue(company_name, this.company_name);
        }

        public void setJobTitle(String job_title) {
            this.job_title = validateValue(job_title, this.job_title);
        }

        public void setDepartment(String department) {
            this.department = validateValue(department, this.department);
        }

        public void setWebsite(String website) {
            this.website = validateValue(website, this.website);
        }

        public void setNotes(String notes) {
            this.notes = validateValue(notes, this.notes);
        }

        public void setPersonalEmail(String personal_email) {
            this.personal_email = validateValue(personal_email, this.personal_email);
        }

        public void setBusinessEmail(String business_email) {
            this.business_email = validateValue(business_email, this.business_email);
        }

        public void setOtherEmail(String other_email) {
            this.other_email = validateValue(other_email, this.other_email);
        }

        public void setHomePhone(String home_phone) {
            this.home_phone = validateValue(home_phone, this.home_phone);
        }

        public void setBusinessPhone(String business_phone) {
            this.business_phone = validateValue(business_phone, this.business_phone);
        }

        public void setMobilePhone(String mobile_phone) {
            this.mobile_phone = validateValue(mobile_phone, this.mobile_phone);
        }

        public int getId() {
            return id;
        }

        public String getFirstName(){
            return first_name;
        }

        public String getLastName() {
            return last_name;
        }

        public String getBusinessCity() {
            return business_city;
        }

        public String getBusinessState() {
            return business_state;
        }

        public String getBusinessCountry() {
            return business_country;
        }

        public String getBusinessPpostalCode() {
            return business_postal_code;
        }

        public String getBusinessAddress() {
            return business_address;
        }

        public String getBusinessStreet() {
            return business_street;
        }

        public String getCompanyName() {
            return company_name;
        }

        public String getJobTitle() {
            return job_title;
        }

        public String getDepartment() {
            return department;
        }

        public String getWebsite() {
            return website;
        }

        public String getNotes() {
            return notes;
        }

        public String getPersonalEmail() {
            return personal_email;
        }

        public String getBusinessEmail() {
            return business_email;
        }

        public String getOtherEmail() {
            return other_email;
        }

        public String getHomePhone() {
            return home_phone;
        }

        public String getBusinessPhone() {
            return business_phone;
        }

        public String getMobilePhone() {
            return mobile_phone;
        }

        private String validateValue(String value, String current_value){
            if (current_value != null && !current_value.isEmpty()){
                return current_value;
            }
            if (value != null && !value.isEmpty()){
                return value;
            }
            return null;
        }
    }

    /**
     * Get all the device contacts ids
     * @param resolver
     * @return a list of string
     */
    public static ArrayList<String> getContactsIds(ContentResolver resolver) {

        Cursor contactCursor = getCursor(resolver, contact_table, null, CONTACT_PROJECTION, null);
        ArrayList<String> contacts = new ArrayList<String>();

        while (contactCursor.moveToNext()) {
            contacts.add(contactCursor.getString(0));
        }
        return contacts;
    }

    /**
     * Get all the device contacts updated after timestamp
     * @param resolver
     * @param minTimestamp the time in milliseconds
     * @param minTimestamp the time in milliseconds
     * @return the list of ids of contacts
     */
    public static List<String> getContactsIds(ContentResolver resolver, String minTimestamp, String maxTimestamp)
    {
        Cursor contactCursor = resolver.query(contact_table, CONTACT_PROJECTION,
                ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP + ">=? AND "
                        + ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP + "<=?", new String[]{minTimestamp, maxTimestamp}, null);
        List<String> contacts = new ArrayList<String>();

        while (contactCursor.moveToNext()) {
            if (!contacts.contains(contactCursor.getString(0))) {
                contacts.add(contactCursor.getString(0));
            }
        }
        contactCursor.close();
        return contacts;
    }

    /**
     * Get the info of the contact
     * @param resolver
     * @param contact_id the id of the contact
     * @return The info of the contact
     */
    public static ContactInfo getContactInfo(Context context, ContentResolver resolver, String contact_id){

        Cursor contactCursor = getCursor(resolver, contactData_table, QueryFilter.ByContactId, null, new String[]{contact_id});
        ContactInfo contact = new ContactInfo(Integer.parseInt(contact_id));


        if (contactCursor.getCount() > 0) {
            while (contactCursor.moveToNext()) {
                String mime = getMimeType(contactCursor);

                if (mime.equals(structuredName_cit)) {

                    contact.setFirstName(getData(contactCursor, firstName));
                    if (getData(contactCursor, lastName) != null) {
                        contact.setLastName(((getData(contactCursor, "data5") != null ? getData(contactCursor, "data5") : "") + " " + getData(contactCursor, lastName)).trim());
                    }
                    if (contact.first_name != null && contact.last_name != null) {
                        contact.setDisplayName(((contact.getFirstName() != null ? contact.getFirstName() : "") + " " + (contact.getLastName() != null ? contact.getLastName() : "").trim()));
                    }

                } else if (mime.equals(structuredPostal_cit)) {

                    contact.setBusinessCity(getData(contactCursor, city));
                    contact.setBusinessState(getData(contactCursor, region));
                    contact.setBusinessCountry(getData(contactCursor, country));
                    contact.setBusinessPostalCode(getData(contactCursor, postcode));
                    contact.setBusinessAddress(getData(contactCursor, formated_address));
                    contact.setBusinessStreet(getData(contactCursor, street));

                } else if (mime.equals(organization_cit)) {

                    contact.setCompanyName(getData(contactCursor, company));
                    contact.setJobTitle(getData(contactCursor, org_title));
                    contact.setDepartment(getData(contactCursor, org_department));

                } else if (mime.equals(website_cit)) {

                    contact.setWebsite(getData(contactCursor, websiteUrl));

                } else if (mime.equals(notes_cit)) {

                    contact.setNotes(getData(contactCursor, note));

                } else if (mime.equals(email_cit)){
                    Cursor emailsCursor = getCursor(resolver, contactEmail_table, QueryFilter.ByEmailId, null, new String[]{contact_id});

                    Integer max_size_emails     = 3;
                    Integer current_email_count = 0;

                    while (emailsCursor.moveToNext() && current_email_count < max_size_emails) {
                        String email_label  = getData(emailsCursor, emailLabel);
                        int type            = Integer.parseInt(getData(emailsCursor, emailType));
                        String email_type   = (String) ContactsContract.CommonDataKinds.Email.getTypeLabel(context.getResources(),
                                type, email_label);
                        String key          = email_type.toLowerCase();
                        current_email_count++;
                        if (key.equals("home")){
                            contact.setPersonalEmail(getData(emailsCursor, email));
                        }
                        else if (key.equals("work")){
                            contact.setBusinessEmail(getData(emailsCursor, email));
                        }
                        else if (key.equals("other")){
                            contact.setOtherEmail(getData(emailsCursor, email));
                        }
                    }

                    emailsCursor.close();
                } else if (mime.equals(phone_cit)){
                    Cursor phonesCursor = getCursor(resolver, contactPhone_table, QueryFilter.ByPhoneId, null, new String[]{contact_id});

                    Integer max_size_phones     = 3;
                    Integer current_phone_count = 0;

                    while (phonesCursor.moveToNext() && current_phone_count < max_size_phones) {
                        String phone_label  = getData(phonesCursor, phoneLabel);
                        int type            = Integer.parseInt(getData(phonesCursor, phoneType));
                        String phoneType    = (String) ContactsContract.CommonDataKinds.Phone.getTypeLabel(context.getResources(),
                                type, phone_label);
                        String key          = phoneType.toLowerCase();
                        if (key.equals("home")){
                            contact.setHomePhone(getData(phonesCursor, phoneNumber));
                        }
                        else if (key.equals("work")){
                            contact.setBusinessPhone(getData(phonesCursor, phoneNumber));
                        }
                        else if (key.equals("mobile")){
                            contact.setMobilePhone(getData(phonesCursor, phoneNumber));
                        }
                    }
                    phonesCursor.close();
                }

            }

        }
        contactCursor.close();
        return contact;
    }

    public static Object getTactContactInfoVisited(Context ctx, String contact_id, String query) {
        Cursor cr = getGeneralCursor(ctx, DatabaseContentProvider.URI_VISITED_CONTACTS, null, new String[]{"*"}, new String[]{contact_id}, query);
        return doContactObject(cr);
    }

    public static Object getTactContactInfoAll(Context ctx, String contact_id, String query) {
        Cursor cr = getGeneralCursor(ctx, DatabaseContentProvider.URI_ALL_CONTACTS, null, new String[]{"*"}, new String[]{contact_id}, query);
        return doContactObject(cr);
    }

    public static Object getTactContactInfoStarred(Context ctx, String contact_id, String query) {
        Cursor cr = getGeneralCursor(ctx, DatabaseContentProvider.URI_STARRED_CONTACTS, null, new String[]{"*"}, new String[]{contact_id}, query);
        return doContactObject(cr);
    }

    public static Object getTactContactInfo(Context ctx, String contact_id, String query) {

        Cursor cr = getGeneralCursor(ctx, tact_contacts_table, QueryFilter.ByTactContactId, new String[]{"*"}, new String[]{contact_id}, query);
        return doContactObject(cr);
    }

    public static Object doContactObject(Cursor cr) {
        ArrayList<ContactInfo> contactList = new ArrayList<ContactInfo>();
        ContactInfo finalContact = null;
        Object queryRequest = null;

        if (cr.getCount() > 0) {
            while (cr.moveToNext()) {
                ContactInfo contactInfo = new ContactInfo();
                int contactId = -1;
                if (cr.getColumnIndex(Contact.ROW_PRIMARY_KEY_ALIAS) != -1) {
                    contactId = cr.getInt(cr.getColumnIndex(Contact.ROW_PRIMARY_KEY_ALIAS));
                } else if (cr.getColumnIndex(Contact.ROW_PRIMARY_KEY) != -1){
                    contactId = cr.getInt(cr.getColumnIndex(Contact.ROW_PRIMARY_KEY));
                }
                contactInfo.setId(contactId);

                contactInfo.setDisplayName(cr.getString(cr.getColumnIndex(Contact.ROW_DISPLAY_NAME)));
                contactInfo.setRecord_id(cr.getString(cr.getColumnIndex(Contact.ROW_RECORD_ID)));
                contactInfo.setFirstName(cr.getString(cr.getColumnIndex(Contact.ROW_FULL_NAME)));
                contactInfo.setCompanyName(cr.getString(cr.getColumnIndex(Contact.ROW_COMPANY_NAME)));
                Integer starred = cr.getInt(cr.getColumnIndex(Contact.ROW_IS_STARRED));
                contactInfo.setStarred(starred != null && starred != 0);
                contactInfo.setJobTitle(cr.getString(cr.getColumnIndex(Contact.ROW_JOB_TITLE)));
                contactInfo.setSubtitle(contactInfo.getCompanyName() != null ? contactInfo.getCompanyName()
                        : (contactInfo.getJobTitle() != null ? contactInfo.getJobTitle() : null));

                // Determine if the contact is a person or a company
                String type = cr.getString(cr.getColumnIndex(Contact.ROW_TYPE));
                if (type.equalsIgnoreCase(Contact.TYPE_PERSON)) {
                    contactInfo.setContactType(ContactType.PERSON);
                } else { // if (type.equalsIgnoreCase(Contact.TYPE_COMPANY)) {
                    contactInfo.setContactType(ContactType.COMPANY);
                }

                String photo = cr.getString(cr.getColumnIndex(Contact.ROW_PHOTO_URL));
                if (photo != null) {
                    contactInfo.setIconType(IconType.PHOTO_ICON);
                } else {
                    if (contactInfo.getContactType() == ContactType.PERSON) {
                        String firstName = cr.getString(cr.getColumnIndex(Contact.ROW_FIRST_NAME));
                        String lastName = cr.getString(cr.getColumnIndex(Contact.ROW_LAST_NAME));
                        String f = firstName != null ? firstName.substring(0, 1).toUpperCase() : null;
                        String l = lastName != null ? lastName.substring(0, 1).toUpperCase() : null;
                        contactInfo.setInitials(f+l);
                        if (Utils.hasValidInitials(f, l)) {
                            contactInfo.setIconType(IconType.INITIALS);
                        } else {
                            contactInfo.setIconType(IconType.PERSON_ICON);
                        }
                    } else {
                        contactInfo.setIconType(IconType.COMPANY_ICON);
                    }
                }

                if (cr.getCount()>1) {
                    contactList.add(contactInfo);
                } else {
                    finalContact = contactInfo;
                }
            }
            if (contactList.size()>0) {
                queryRequest = contactList;
            } else {
                queryRequest = finalContact;
            }
        }
        cr.close();

        return queryRequest;
    }


    //****************************  UTILITIES  *********************************\\

    /**
     * Returns a table name from enum type
     * @param table
     * @return
     */
    public static String getQueryField(QueryFilter table) {
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
                queryTable = Contact.ROW_RECORD_ID;
        }

        return queryTable;
    }

    /**
     * Return a cursor to iterate over the contacts
     * @param resolver
     * @return
     */
    public static Cursor getCursor(ContentResolver resolver, Uri table, QueryFilter filter, String[] projection, String[] selectionArgs) {


        String selection = null;
        if (selectionArgs != null && selectionArgs.length>0 && filter != null) {
            String listQuery = "?";
            for (int i=0; i< selectionArgs.length-1; i++) {
                listQuery+=",?";
            }

            selection = ContactAPI.getQueryField(filter) + " IN (" + listQuery + ")";
        }

        Cursor contactCursor = resolver.query(
                table,
                projection,
                selection,
                (selectionArgs != null && selectionArgs.length>0)?selectionArgs:null,
                null
        );

        return contactCursor;
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
                selection = ContactAPI.getQueryField(filter) + " IN (" + listQuery + ")";
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
                "ZDISPLAYNAME"
        );

        return calendarCursor;
    }

    /**
     * Get the data in the cursor obtained by a query
     * @param cursor the cursor obtained by a query
     * @param filter the column we want to obtain in the cursor
     * @return the data string
     */
    private static String getData(Cursor cursor, String filter) {
        return cursor.getString(cursor.getColumnIndex(filter));
    }

    /**
     * Get the mime type
     * @param contactCursor
     * @return
     */
    private static String getMimeType(Cursor contactCursor){
        return contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Data.MIMETYPE));
    }

    public static Bitmap getContactImage(int rawContactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, rawContactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = TactApplication.getInstance().getContentResolver().query(photoUri,
                new String[]{ContactsContract.Contacts.Photo.PHOTO}, null, null, null);

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    byte[] imageBytes = cursor.getBlob(0);
                    if (imageBytes != null) {
                        return BitmapFactory.decodeStream(new ByteArrayInputStream(imageBytes));
                    }
                }
            }
            finally {
                cursor.close();
            }
        }
        return null;
    }

    /*public static Uri getContactImage(int rawContactId){
        try {
            Cursor cur = TactApplication.getInstance().getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI,
                    null,
                    ContactsContract.Data.CONTACT_ID + "=" + rawContactId + " AND "
                            + ContactsContract.Data.MIMETYPE + "='"
                            + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'", null,
                    null);
            if (cur != null) {
                if (!cur.moveToFirst()) {
                    return null; // no photo
                }
            } else {
                return null; // error in cursor process
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, rawContactId);
        return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
    }*/
}
