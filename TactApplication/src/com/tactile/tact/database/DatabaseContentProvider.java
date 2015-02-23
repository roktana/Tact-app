package com.tactile.tact.database;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.tactile.tact.consts.TactConst;
import com.tactile.tact.database.model.Contact;
import com.tactile.tact.database.model.ContactDetails;
import com.tactile.tact.database.model.CustomField;
import com.tactile.tact.database.model.CustomFieldMetaData;
import com.tactile.tact.database.model.Opportunity;
import com.tactile.tact.database.model.Source;
import com.tactile.tact.database.model.SyncStatus;

import java.util.HashMap;
import java.util.List;

public class DatabaseContentProvider extends ContentProvider {
    public static final String TAG = DatabaseContentProvider.class.getSimpleName();

    private DatabaseManager databaseManager;

    private static final int
            ALL_CONTACTS                    = 0,
            ALL_PRIMARY_CONTACTS            = 1,
            ALL_STARRED_CONTACTS            = 2,
            ALL_RECENT_CONTACTS             = 3,
            MERGED_CONTACTS                 = 4,
            SINGLE_CONTACT                  = 5,
            ALL_CONTACT_DETAILS             = 6,
            SINGLE_CONTACT_DETAILS          = 7,
            DISTINCT_SECTION_NAMES          = 8,
            DISTINCT_STARRED_SECTION_NAMES  = 9,
            CUSTOM_FIELD                    = 10,
            CUSTOM_FIELD_META_DATA          = 11,
            SOURCES                         = 12,
            SYNC_STATUS                     = 13,
            OPPORTUNITIES                   = 14,
            CONTACTS_WITH_DETAILS           = 15,
            SINGLE_STARRED_CONTACT          = 16,
            SINLGE_RECENT_CONTACT           = 17;

    public static final String AUTHORITY = "com.tactile.tact.DatabaseContentProvider";

    public static final Uri URI_ALL_CONTACTS            = Uri.parse("content://" + AUTHORITY + "/contacts");
    public static final Uri URI_ALL_PRIMARY_CONTACTS    = Uri.parse("content://" + AUTHORITY + "/contacts/primary");
    public static final Uri URI_MERGED_CONTACTS         = Uri.parse("content://" + AUTHORITY + "/contacts/merged");
    public static final Uri URI_STARRED_CONTACTS        = Uri.parse("content://" + AUTHORITY + "/contacts/starred");
    public static final Uri URI_STARRED_SECTIONS        = Uri.parse("content://" + AUTHORITY + "/contacts/starred/sections");
    public static final Uri URI_VISITED_CONTACTS        = Uri.parse("content://" + AUTHORITY + "/contacts/recent");
    public static final Uri URI_CONTACT_SECTIONS        = Uri.parse("content://" + AUTHORITY + "/contacts/sections");
    public static final Uri URI_CONTACT_DETAILS         = Uri.parse("content://" + AUTHORITY + "/contactDetails");
    public static final Uri URI_CUSTOM_FIELD            = Uri.parse("content://" + AUTHORITY + "/customField");
    public static final Uri URI_CUSTOM_FIELD_META       = Uri.parse("content://" + AUTHORITY + "/customFieldMetaData");
    public static final Uri URI_SOURCES                 = Uri.parse("content://" + AUTHORITY + "/sources");
    public static final Uri URI_SYNC_STATUS             = Uri.parse("content://" + AUTHORITY + "/syncStatus");
    public static final Uri URI_OPPORTUNITIES           = Uri.parse("content://" + AUTHORITY + "/opportunities");
    public static final Uri URI_CONTACTS_WITH_DETAILS   = Uri.parse("content://" + AUTHORITY + "/contactsWithDetails");

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "contacts", ALL_CONTACTS);
        uriMatcher.addURI(AUTHORITY, "contacts/#", SINGLE_CONTACT);
        uriMatcher.addURI(AUTHORITY, "contacts/merged/#", MERGED_CONTACTS);
        uriMatcher.addURI(AUTHORITY, "contacts/primary", ALL_PRIMARY_CONTACTS);
        uriMatcher.addURI(AUTHORITY, "contacts/starred", ALL_STARRED_CONTACTS);
        uriMatcher.addURI(AUTHORITY, "contacts/starred/#", SINGLE_STARRED_CONTACT);
        uriMatcher.addURI(AUTHORITY, "contacts/sections", DISTINCT_SECTION_NAMES);
        uriMatcher.addURI(AUTHORITY, "contacts/starred/sections", DISTINCT_STARRED_SECTION_NAMES);
        uriMatcher.addURI(AUTHORITY, "contacts/recent", ALL_RECENT_CONTACTS);
        uriMatcher.addURI(AUTHORITY, "contacts/recent/#", SINLGE_RECENT_CONTACT);
        uriMatcher.addURI(AUTHORITY, "contactDetails", ALL_CONTACT_DETAILS);
        uriMatcher.addURI(AUTHORITY, "contactDetails/#", SINGLE_CONTACT_DETAILS);
        uriMatcher.addURI(AUTHORITY, "customField", CUSTOM_FIELD);
        uriMatcher.addURI(AUTHORITY, "customFieldMetaData", CUSTOM_FIELD_META_DATA);
        uriMatcher.addURI(AUTHORITY, "sources", SOURCES);
        uriMatcher.addURI(AUTHORITY, "syncStatus", SYNC_STATUS);
        uriMatcher.addURI(AUTHORITY, "opportunities", OPPORTUNITIES);
        uriMatcher.addURI(AUTHORITY, "contactsWithDetails", CONTACTS_WITH_DETAILS);
    }

    @Override
    public boolean onCreate() {
        databaseManager = new DatabaseManager();
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = databaseManager.getReadableDatabase();

        List<String> pathSegments = uri.getPathSegments();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        if (pathSegments != null) {
            String groupBy = null;
            // Set the table name and where clause
            switch (uriMatcher.match(uri)) {
                case ALL_CONTACTS:
                    queryBuilder.setTables(Contact.TABLE_NAME);
                    // No where clause
                    break;
                case ALL_PRIMARY_CONTACTS:
                    queryBuilder.setTables(Contact.TABLE_NAME);
                    queryBuilder.appendWhere(Contact.ROW_MERGED_CONTACT + " is null or " + Contact.ROW_MERGED_CONTACT + "='' or " +
                    Contact.ROW_MERGED_CONTACT + "=0");
                    break;
                case ALL_STARRED_CONTACTS:
                    queryBuilder.setTables(Contact.TABLE_NAME);
                    queryBuilder.appendWhere(Contact.ROW_IS_STARRED + "=1");
                    queryBuilder.appendWhere(Contact.ROW_MERGED_CONTACT + " is null or " + Contact.ROW_MERGED_CONTACT + "='' or " +
                            Contact.ROW_MERGED_CONTACT + "=0");
                    break;
                case ALL_RECENT_CONTACTS:
                    queryBuilder.setTables(Contact.TABLE_NAME);
                    queryBuilder.appendWhere(Contact.ROW_VISITED_AT + " is not null");
                    queryBuilder.appendWhere(Contact.ROW_MERGED_CONTACT + " is null or " + Contact.ROW_MERGED_CONTACT + "='' or " +
                            Contact.ROW_MERGED_CONTACT + "=0");
                    break;
                case MERGED_CONTACTS:
                    queryBuilder.setTables(Contact.TABLE_NAME);
                    queryBuilder.appendWhere(Contact.ROW_MERGED_CONTACT + "=" + pathSegments.get(2));
                    break;
                case SINGLE_CONTACT:
                    queryBuilder.setTables(Contact.TABLE_NAME);
                    queryBuilder.appendWhere(Contact.ROW_PRIMARY_KEY + "=" + pathSegments.get(1));
                    break;
                case ALL_CONTACT_DETAILS:
                    queryBuilder.setTables(ContactDetails.TABLE_NAME);
                    break;
                case SINGLE_CONTACT_DETAILS:
                    queryBuilder.setTables(ContactDetails.TABLE_NAME);
                    queryBuilder.appendWhere(ContactDetails.ROW_CONTACT_KEY + "=" + pathSegments.get(1));
                    break;
                case DISTINCT_SECTION_NAMES:
                    queryBuilder.setTables(Contact.TABLE_NAME);
                    queryBuilder.appendWhere(Contact.ROW_MERGED_CONTACT + " is null or " + Contact.ROW_MERGED_CONTACT + "=''");
                    queryBuilder.setDistinct(true);
                    groupBy = Contact.ROW_SECTION_NAME;
                    break;
                case DISTINCT_STARRED_SECTION_NAMES:
                    queryBuilder.setTables(Contact.TABLE_NAME);
                    queryBuilder.setDistinct(true);
                    queryBuilder.appendWhere(Contact.ROW_IS_STARRED + "=1");
                    groupBy = Contact.ROW_SECTION_NAME;
                    break;
                case CUSTOM_FIELD:
                    queryBuilder.setTables(CustomField.TABLE_NAME);
                    break;
                case CUSTOM_FIELD_META_DATA:
                    queryBuilder.setTables(CustomFieldMetaData.TABLE_NAME);
                    break;
                case SOURCES:
                    queryBuilder.setTables(Source.TABLE_NAME);
                    break;
                case SYNC_STATUS:
                    queryBuilder.setTables(SyncStatus.TABLE_NAME);
                    break;
                case CONTACTS_WITH_DETAILS:
                    queryBuilder.setTables(Contact.TABLE_NAME + ", " + ContactDetails.TABLE_NAME);
                    queryBuilder.appendWhere(Contact.TABLE_NAME + "." + Contact.ROW_PRIMARY_KEY + " = " + ContactDetails.TABLE_NAME + "." + ContactDetails.ROW_CONTACT_KEY);
                    break;
                case OPPORTUNITIES:
                    queryBuilder.setTables(Opportunity.TABLE_NAME);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported URI: " + uri);

            }
            return queryBuilder.query(database, projection, selection, selectionArgs, groupBy, null, sortOrder);
        }
        return null;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case ALL_CONTACTS:
            case ALL_PRIMARY_CONTACTS:
            case ALL_STARRED_CONTACTS:
            case ALL_RECENT_CONTACTS:
            case MERGED_CONTACTS:
                return "vnd.android.cursor.dir/vnd." + AUTHORITY + ".contacts";
            case SINGLE_CONTACT:
                return "vnd.android.cursor.item/vnd." + AUTHORITY + ".contacts";
            case ALL_CONTACT_DETAILS:
                return "vnd.android.cursor.dir/vnd." + AUTHORITY + ".contactDetails";
            case SINGLE_CONTACT_DETAILS:
                return "vnd.android.cursor.item/vnd." + AUTHORITY + ".contactDetails";
            case DISTINCT_SECTION_NAMES:
            case DISTINCT_STARRED_SECTION_NAMES:
                return "vnd.android.cursor.item/vnd." + AUTHORITY + ".contacts.sectionName";
            case CUSTOM_FIELD:
                return "vnd.android.cursor.item/vnd." + AUTHORITY + ".customField";
            case CUSTOM_FIELD_META_DATA:
                return "vnd.android.cursor.item/vnd." + AUTHORITY + ".customFieldMetaData";
            case SYNC_STATUS:
                return "vnd.android.cursor.item/vnd." + AUTHORITY + ".syncStatus";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase database = databaseManager.getWritableDatabase();
        long id = 0;
        switch (uriMatcher.match(uri)) {
            case SOURCES:
                id = database.insert(Source.TABLE_NAME, null, values);
                break;
            case SYNC_STATUS:
                id = database.insert(SyncStatus.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return getUriForId(id, uri);
    }

    private Uri getUriForId(long id, Uri uri) {
        if (id > 0) {
            return ContentUris.withAppendedId(uri, id);
        }
        // s.th. went wrong:
        throw new SQLException("Problem while inserting into uri: " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = databaseManager.getWritableDatabase();
        int count = 0;
        switch (uriMatcher.match(uri)){
            case SOURCES:
                count = database.delete(Source.TABLE_NAME, selection, selectionArgs);
                break;
            case ALL_CONTACTS:
                count = database.delete(Contact.TABLE_NAME, selection, selectionArgs);
                break;
            case OPPORTUNITIES:
                count = database.delete(Opportunity.TABLE_NAME, selection, selectionArgs);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase database = databaseManager.getWritableDatabase();
        List<String> pathSegments = uri.getPathSegments();
        if (pathSegments != null) {
            String tableName;
            switch (uriMatcher.match(uri)) {
                case SINGLE_CONTACT:
                    tableName = Contact.TABLE_NAME;
                    selection = Contact.ROW_PRIMARY_KEY + "=" + uri.getPathSegments().get(1)
                            + (!TextUtils.isEmpty(selection) ?
                            " AND (" + selection + ')' : "");
                    break;
                case SINGLE_CONTACT_DETAILS:
                    tableName = ContactDetails.TABLE_NAME;
                    selection = ContactDetails.ROW_CONTACT_KEY + "=" + uri.getPathSegments().get(1)
                            + (!TextUtils.isEmpty(selection) ?
                            " AND (" + selection + ')' : "");
                    break;
                case OPPORTUNITIES:
                    tableName = Opportunity.TABLE_NAME;
                    selection = Opportunity.ROW_IDENTIFIER + "=?";
                    break;
                case SINLGE_RECENT_CONTACT:
                    tableName = Contact.TABLE_NAME;
                    selection = Contact.ROW_PRIMARY_KEY + "=" + uri.getPathSegments().get(2)
                            + (!TextUtils.isEmpty(selection) ?
                            " AND (" + selection + ')' : "");
                    break;
                case SINGLE_STARRED_CONTACT:
                    tableName = Contact.TABLE_NAME;
                    selection = Contact.ROW_PRIMARY_KEY + "=" + uri.getPathSegments().get(2)
                            + (!TextUtils.isEmpty(selection) ?
                            " AND (" + selection + ')' : "");
                    break;
                case SYNC_STATUS:
                    tableName = SyncStatus.TABLE_NAME;
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported URI: " + uri);
            }
            int updateCount = database.update(tableName, values, selection, selectionArgs);
            //noinspection ConstantConditions
            getContext().getContentResolver().notifyChange(uri, null);
            if (values.containsKey(Contact.ROW_VISITED_AT)) {
                getContext().getContentResolver().notifyChange(URI_VISITED_CONTACTS, null);
            }
            if (values.containsKey(Contact.ROW_IS_STARRED)) {
                getContext().getContentResolver().notifyChange(URI_STARRED_CONTACTS, null);
            }
            return updateCount;
        }
        return 0;
    }

    public static boolean getIsSynced(ContentResolver resolver, String account){
        Cursor cursor = resolver.query(URI_SOURCES,
                new String[]{"*"}, Source.ROW_TYPE + " = ?", new String[]{account}, null);
        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }

    public static String getUsername(ContentResolver resolver, String account){
        Cursor cursor = resolver.query(URI_SOURCES,
                new String[]{Source.ROW_USERNAME}, Source.ROW_TYPE + " = ?", new String[]{account}, null);
        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            return cursor.getString(0);
        }
        cursor.close();
        return "";
    }

    public static String getAccountId(ContentResolver resolver, String account){
        Cursor cursor = resolver.query(URI_SOURCES,
                new String[]{Source.ROW_IDENTIFIER}, Source.ROW_TYPE + " = ?", new String[]{account}, null);
        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            return cursor.getString(0);
        }
        cursor.close();
        return "";
    }

    public static void removeAccount(ContentResolver resolver, String account){
        resolver.delete(URI_SOURCES,
                Source.ROW_TYPE + " = ?",
                new String[]{account});
    }

    public static void removeContactsFromAccount(ContentResolver resolver, String account){

        String account_selection = null;
        if (account.equals("salesforce")){
            account_selection = Contact.ROW_IS_SALESFORCE + " = 1";
        }
        else if (account.equals("exchange")){
            account_selection = Contact.ROW_IS_EXCHANGE + " = 1";
        }
        else if (account.equals("google")){
            account_selection = Contact.ROW_IS_GOOGLE + " = 1";
        }

        if (account_selection != null) {
            resolver.delete(URI_ALL_CONTACTS,
                    account_selection,
                    null);
        }
    }

    public static void removeOpportunities(ContentResolver resolver){
        String source_id = "";
        Cursor cursor = resolver.query(URI_SOURCES,
                new String[]{Source.ROW_PRIMARY_KEY}, Source.ROW_TYPE + " = ?", new String[]{"salesforce"}, null);
        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            source_id = cursor.getString(0);
        }
        cursor.close();
        if (!source_id.equals("")){
            resolver.delete(URI_OPPORTUNITIES,
                    Opportunity.ROW_SOURCE + " = ?",
                    new String[]{source_id});
        }
    }
}
