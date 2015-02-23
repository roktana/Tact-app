package com.tactile.tact.database.model;

import android.database.Cursor;

/**
 * Created by sebafonseca on 9/29/14.
 */
public class Opportunity extends BaseDatabaseModel{

    /*
     * Database Table Name
     */

    @SuppressWarnings("SpellCheckingInspection")
    public static final String TABLE_NAME = "ZOPPORTUNITY";

    /*
     * Table Rows
     */

    @SuppressWarnings({"SpellCheckingInspection", "UnusedDeclaration"})
    public static final String
            ROW_PRIMARY_KEY             = "Z_PK",
            ROW_ENTITY_PRIMARY_KEY      = "Z_ENT",
            ROW_OPPORTUNITY_PRIMARY_KEY = "Z_OPT",
            ROW_IS_CLOSED               = "ZISCLOSED",
            ROW_IS_WON                  = "ZISWON",
            ROW_COMPANY                 = "ZCOMPANY",
            ROW_SOURCE                  = "ZSOURCE",
            ROW_CLOSE_DATE              = "ZCLOSEDATE",
            ROW_AMOUNT                  = "ZAMOUNT",
            ROW_EXPECTED_REVENUE        = "ZEXPECTEDREVENUE",
            ROW_PROBABILITY             = "ZPROBABILITY",
            ROW_QUANTITY                = "ZQUANTITY",
            ROW_FORECAST_CATEGORY       = "ZFORECASTCATEGORY",
            ROW_IDENTIFIER              = "ZIDENTIFIER",
            ROW_LEAD_SOURCE             = "ZLEADSOURCE",
            ROW_NAME                    = "ZNAME",
            ROW_NEXT_STEP               = "ZNEXTSTEP",
            ROW_OPPORTUNITY_DESCRIPTION = "ZOPPORTUNITYDESCRIPTION",
            ROW_OWNER_ID                = "ZOWNERID",
            ROW_OWNER_NAME              = "ZOWNERNAME",
            ROW_STAGE_NAME              = "ZSTAGENAME",
            ROW_TYPE                    = "ZTYPE";


    /*
     * Constructor
     */

    public Opportunity(Cursor cursor) {
        super(cursor);
    }
}
