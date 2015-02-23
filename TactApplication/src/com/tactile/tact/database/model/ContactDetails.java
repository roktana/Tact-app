package com.tactile.tact.database.model;

import android.database.Cursor;

public class ContactDetails extends BaseDatabaseModel {

    /*
     * Database Table Name
     */

    @SuppressWarnings("SpellCheckingInspection")
    public static final String TABLE_NAME = "ZCONTACTDETAIL";


    /*
     * Table Rows
     */

    @SuppressWarnings({"SpellCheckingInspection", "UnusedDeclaration"})
    public static final String
            ROW_PRIMARY_KEY             = "Z_PK",
            ROW_ENTITY_PRIMARY_KEY      = "Z_ENT",
            ROW_OPPORTUNITY_PRIMARY_KEY = "Z_OPT",
            ROW_CORE_DATA_IS_BROKEN     = "ZCOREDATAISBROKEN",
            ROW_CONTACT_KEY             = "ZCONTACT",
            ROW_CONTACT_KEY_AUX         = "Z1_CONTACT",
            ROW_SOURCE                  = "ZSOURCE",
            ROW_ANNUAL_REVENUE          = "ZANNUALREVENUE",
            ROW_BUSINESS_CITY           = "ZBUSINESSCITY",
            ROW_BUSINESS_COUNTRY        = "ZBUSINESSCOUNTRY",
            ROW_BUSINESS_EMAIL          = "ZBUSINESSEMAIL",
            ROW_BUSINESS_FAX            = "ZBUSINESSFAX",
            ROW_BUSINESS_PHONE          = "ZBUSINESSPHONE",
            ROW_BUSINESS_POSTAL_CODE    = "ZBUSINESSPOSTALCODE",
            ROW_BUSINESS_STATE          = "ZBUSINESSSTATE",
            ROW_BUSINESS_STREET         = "ZBUSINESSSTREET",
            ROW_COMPANY                 = "ZCOMPANY",
            ROW_COMPANYID               = "ZCOMPANYID",
            ROW_COMPANY_NAME            = "ZCOMPANYNAME",
            ROW_DEPARTMENT              = "ZDEPARTMENT",
            ROW_DESCRIPTION_FIELD       = "ZDESCRIPTIONFIELD",
            ROW_FACEBOOK_USERNAME       = "ZFACEBOOKUSERNAME",
            ROW_FIRST_NAME              = "ZFIRSTNAME",
            ROW_HOME_PHONE              = "ZHOMEPHONE",
            ROW_INDUSTRY                = "ZINDUSTRY",
            ROW_JOB_TITLE               = "ZJOBTITLE",
            ROW_LAST_NAME               = "ZLASTNAME",
            ROW_LINKEDIN_ID             = "ZLINKEDINID",
            ROW_LINKEDIN_URL            = "ZLINKEDINURL",
            ROW_MOBILE_PHONE            = "ZMOBILEPHONE",
            ROW_NAME                    = "ZNAME",
            ROW_NOTES                   = "ZNOTES",
            ROW_NUMBER_OF_EMPLOYEES     = "ZNUMBEROFEMPLOYEES",
            ROW_OTHER_EMAIL             = "ZOTHEREMAIL",
            ROW_PERSONAL_EMAIL          = "ZPERSONALEMAIL",
            ROW_PHOTO_URL               = "ZPHOTOURL",
            ROW_REMOTE_ID               = "ZREMOTEID",
            ROW_TICKER_SYMBOL           = "ZTICKERSYMBOL",
            ROW_TWITTER_USERNAME        = "ZTWITTERUSERNAME",
            ROW_WEBSITE                 = "ZWEBSITE";


    /*
     * Constructor
     */

    public ContactDetails(Cursor cursor) {
        super(cursor);
    }
}
