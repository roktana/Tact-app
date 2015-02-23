package com.tactile.tact.database.model;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class Contact extends BaseDatabaseModel implements Comparable<Contact> {

    /*
     * Database Table Name
     */

    @SuppressWarnings("SpellCheckingInspection")
    public static final String TABLE_NAME = "ZCONTACT";

    /*
     * Table Rows
     */

    @SuppressWarnings({"SpellCheckingInspection", "UnusedDeclaration"})
    public static final String
            ROW_PRIMARY_KEY             = "Z_PK",
            ROW_ENTITY_PRIMARY_KEY      = "Z_ENT",
            ROW_OPPORTUNITY_PRIMARY_KEY = "Z_OPT",
            ROW_PRIMARY_KEY_ALIAS       = "_id",
            ROW_IS_STARRED              = "ZHASFAVORITED",
            ROW_IS_EXCHANGE             = "ZISEXCHANGE",
            ROW_IS_GOOGLE               = "ZISGOOGLE",
            ROW_IS_SALESFORCE           = "ZISSALESFORCE",
            ROW_SEARCH_DOC_ID           = "ZSEARCHDOCID",
            ROW_VISIT_COUNT             = "ZVISITCOUNT",
            ROW_MERGED_CONTACT          = "ZMERGEDCONTACT",
            ROW_MERGED_CONTACT_AUX      = "Z1_MERGEDCONTACT",
            ROW_VISITED_AT              = "ZVISITEDAT",
            ROW_DISPLAY_NAME            = "ZDISPLAYNAME",
            ROW_PHOTO_URL               = "ZPHOTOURL",
            ROW_RECORD_ID               = "ZRECORDID",
            ROW_SECTION_NAME            = "ZSECTIONNAME",
            ROW_SORT_KEY                = "ZSORTKEY",
            ROW_TYPE                    = "ZTYPE",
            ROW_COMPANY_NAME            = "ZCOMPANYNAME",
            ROW_FIRST_NAME              = "ZFIRSTNAME",
            ROW_FULL_NAME               = "ZFULLNAME",
            ROW_JOB_TITLE               = "ZJOBTITLE",
            ROW_LAST_NAME               = "ZLASTNAME";


    /*
     * Contact Types (possible values of ROW_TYPE)
     */

    @SuppressWarnings("UnusedDeclaration")
    public static final String
            TYPE_PERSON = "Person",
            TYPE_COMPANY = "Company";


    /*
     * Constructor
     */

    public Contact(Cursor cursor) {
        super(cursor);
    }


    /*
     * As a convenience, we can sort the results before generating the list items
     */

    @Override
    public int compareTo(Contact another) {
        return ((String) this.getValue(ROW_SORT_KEY)).compareTo((String) another.getValue(ROW_SORT_KEY));
    }
}
