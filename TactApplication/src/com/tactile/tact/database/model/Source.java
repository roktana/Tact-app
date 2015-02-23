package com.tactile.tact.database.model;


import android.database.Cursor;

/**
 * Created by sebafonseca on 10/6/14.
 */
public class Source extends BaseDatabaseModel{

    /*
     * Database Table Name
     */

    @SuppressWarnings("SpellCheckingInspection")
    public static final String TABLE_NAME = "ZSOURCE";

    /*
     * Table Rows
     */

    @SuppressWarnings({"SpellCheckingInspection", "UnusedDeclaration"})
    public static final String
            ROW_PRIMARY_KEY             = "Z_PK",
            ROW_ENTITY_PRIMARY_KEY      = "Z_ENT",
            ROW_OPPORTUNITY_PRIMARY_KEY = "Z_OPT",
            ROW_PRIMARY_KEY_ALIAS       = "_id",
            ROW_AUTH_ERROR              = "ZAUTHERROR",
            ROW_IDENTIFIER              = "ZIDENTIFIER",
            ROW_INITIAL_SYNC_FINISHED   = "ZINITIALZYNCFINISHED",
            ROW_DOMAIN                  = "ZDOMAIN",
            ROW_EMAIL                   = "ZEMAIL",
            ROW_HOSTNAME                = "ZHOSTNAME",
            ROW_TYPE                    = "ZTYPE",
            ROW_USERNAME                = "ZUSERNAME";

    /*
     * Constructor
     */

    public Source(Cursor cursor) {
        super(cursor);
    }
}