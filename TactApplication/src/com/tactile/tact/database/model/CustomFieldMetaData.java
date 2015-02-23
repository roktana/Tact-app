package com.tactile.tact.database.model;

import android.database.Cursor;

public class CustomFieldMetaData extends BaseDatabaseModel {

    /*
     * Database Table Name
     */

    @SuppressWarnings("SpellCheckingInspection")
    public static final String TABLE_NAME = "ZCUSTOMFIELDMETADATA";

    /*
     * Table Rows
     */

    @SuppressWarnings({"SpellCheckingInspection", "UnusedDeclaration"})
    public static final String
            ROW_PRIMARY_KEY = "Z_PK",
            ROW_LABEL = "ZLABEL",
            ROW_NAME = "ZNAME";

    public CustomFieldMetaData(Cursor cursor) {
        super(cursor);
    }

}
