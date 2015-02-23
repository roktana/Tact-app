package com.tactile.tact.database.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.dao.identityscope.IdentityScopeType;

import com.tactile.tact.database.dao.GlobalSyncStatusDao;
import com.tactile.tact.database.dao.ContactFIDao;
import com.tactile.tact.database.dao.FrozenFeedItemDao;
import com.tactile.tact.database.dao.OpportunityFIDao;
import com.tactile.tact.database.dao.ContactFeedItemDao;
import com.tactile.tact.database.dao.RelatedFeedItemDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * Master of DAO (schema version 3): knows all DAOs.
*/
public class DaoMaster extends AbstractDaoMaster {
    public static final int SCHEMA_VERSION = 3;

    /** Creates underlying database table using DAOs. */
    public static void createAllTables(SQLiteDatabase db, boolean ifNotExists) {
        GlobalSyncStatusDao.createTable(db, ifNotExists);
        ContactFIDao.createTable(db, ifNotExists);
        FrozenFeedItemDao.createTable(db, ifNotExists);
        OpportunityFIDao.createTable(db, ifNotExists);
        ContactFeedItemDao.createTable(db, ifNotExists);
        RelatedFeedItemDao.createTable(db, ifNotExists);
    }
    
    /** Drops underlying database table using DAOs. */
    public static void dropAllTables(SQLiteDatabase db, boolean ifExists) {
        GlobalSyncStatusDao.dropTable(db, ifExists);
        ContactFIDao.dropTable(db, ifExists);
        FrozenFeedItemDao.dropTable(db, ifExists);
        OpportunityFIDao.dropTable(db, ifExists);
        ContactFeedItemDao.dropTable(db, ifExists);
        RelatedFeedItemDao.dropTable(db, ifExists);
    }
    
    public static abstract class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory, SCHEMA_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i("greenDAO", "Creating tables for schema version " + SCHEMA_VERSION);
            createAllTables(db, false);
        }
    }
    
    /** WARNING: Drops all table on Upgrade! Use only during development. */
    public static class DevOpenHelper extends OpenHelper {
        public DevOpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
            dropAllTables(db, true);
            onCreate(db);
        }
    }

    public DaoMaster(SQLiteDatabase db) {
        super(db, SCHEMA_VERSION);
        registerDaoClass(GlobalSyncStatusDao.class);
        registerDaoClass(ContactFIDao.class);
        registerDaoClass(FrozenFeedItemDao.class);
        registerDaoClass(OpportunityFIDao.class);
        registerDaoClass(ContactFeedItemDao.class);
        registerDaoClass(RelatedFeedItemDao.class);
    }
    
    public DaoSession newSession() {
        return new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
    }
    
    public DaoSession newSession(IdentityScopeType type) {
        return new DaoSession(db, type, daoConfigMap);
    }
    
}
