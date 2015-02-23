package com.tactile.tact.database.model;

import android.database.Cursor;

import com.google.gson.JsonObject;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseDatabaseModel {
    protected Map<String, Object> fieldMap = null;

    public BaseDatabaseModel(Cursor cursor) {
        fieldMap = new HashMap<String, Object>();
        if (cursor != null) {
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                int type = cursor.getType(i);
                switch (type) {
                    case Cursor.FIELD_TYPE_BLOB:
                        fieldMap.put(cursor.getColumnName(i), cursor.getBlob(i));
                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        fieldMap.put(cursor.getColumnName(i), cursor.getFloat(i));
                        break;
                    case Cursor.FIELD_TYPE_INTEGER:
                        fieldMap.put(cursor.getColumnName(i), cursor.getInt(i));
                        break;
                    case Cursor.FIELD_TYPE_STRING:
                        fieldMap.put(cursor.getColumnName(i), cursor.getString(i));
                        break;
                }
            }
        }
    }

    public Object getValue(String fieldName) {
        return fieldMap.get(fieldName);
    }

    @Override
    public String toString() {
        JsonObject jsonObject = new JsonObject();
        for (Map.Entry<String, Object> entry : fieldMap.entrySet()) {
            jsonObject.addProperty(entry.getKey(), entry.getValue().toString());
        }
        return jsonObject.toString();
    }
}
