package com.tactile.tact.database.model;


import android.database.Cursor;

/**
 * Created by sebafonseca on 10/6/14.
 */
public class SyncStatus extends BaseDatabaseModel{

    /*
     * Database Table Name
     */

    @SuppressWarnings("SpellCheckingInspection")
    public static final String TABLE_NAME = "ZSYNCSTATUS";

    /*
     * Table Rows
     */

    @SuppressWarnings({"SpellCheckingInspection", "UnusedDeclaration"})
    public static final String
            ROW_PRIMARY_KEY             = "Z_PK",
            ROW_ENTITY_PRIMARY_KEY      = "Z_ENT",
            ROW_OPPORTUNITY_PRIMARY_KEY = "Z_OPT",
            ROW_PRIMARY_KEY_ALIAS       = "_id",
            ROW_OFFSET                  = "ZOFFSET",
            ROW_OPPORTUNITIES_REVISION  = "ZOPPORTUNITIESREVISION",
            ROW_REVISION                = "ZREVISION";

    /*
     * Constructor
     */

    public SyncStatus(Cursor cursor) {
        super(cursor);
    }
}