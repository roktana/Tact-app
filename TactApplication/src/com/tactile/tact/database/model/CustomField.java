package com.tactile.tact.database.model;

import android.database.Cursor;

public class CustomField extends BaseDatabaseModel {

    /*
     * Database Table Name
     */

    @SuppressWarnings("SpellCheckingInspection")
    public static final String TABLE_NAME = "ZCUSTOMFIELD";

    /*
     * Table Rows
     */

    @SuppressWarnings({"SpellCheckingInspection", "UnusedDeclaration"})
    public static final String
            ROW_PRIMARY_KEY = "Z_PK",
            ROW_ENTITY_PRIMARY_KEY      = "Z_ENT",
            ROW_OPPORTUNITY_PRIMARY_KEY = "Z_OPT",
            ROW_CONTACT = "ZCONTACT",
            ROW_CONTACT1 = "Z1_CONTACT",
            ROW_SOURCE= "ZSOURCE",
            ROW_NAME = "ZNAME",
            ROW_VALUE = "ZVALUE";





    public CustomField(Cursor cursor) {
        super(cursor);
    }
}
