package com.tactile.tact.database.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.tactile.tact.database.entities.GlobalSyncStatus;
import com.tactile.tact.database.entities.ContactFI;
import com.tactile.tact.database.entities.FrozenFeedItem;
import com.tactile.tact.database.entities.OpportunityFI;
import com.tactile.tact.database.entities.ContactFeedItem;
import com.tactile.tact.database.entities.RelatedFeedItem;

import com.tactile.tact.database.dao.GlobalSyncStatusDao;
import com.tactile.tact.database.dao.ContactFIDao;
import com.tactile.tact.database.dao.FrozenFeedItemDao;
import com.tactile.tact.database.dao.OpportunityFIDao;
import com.tactile.tact.database.dao.ContactFeedItemDao;
import com.tactile.tact.database.dao.RelatedFeedItemDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig globalSyncStatusDaoConfig;
    private final DaoConfig contactFIDaoConfig;
    private final DaoConfig frozenFeedItemDaoConfig;
    private final DaoConfig opportunityFIDaoConfig;
    private final DaoConfig contactFeedItemDaoConfig;
    private final DaoConfig relatedFeedItemDaoConfig;

    private final GlobalSyncStatusDao globalSyncStatusDao;
    private final ContactFIDao contactFIDao;
    private final FrozenFeedItemDao frozenFeedItemDao;
    private final OpportunityFIDao opportunityFIDao;
    private final ContactFeedItemDao contactFeedItemDao;
    private final RelatedFeedItemDao relatedFeedItemDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        globalSyncStatusDaoConfig = daoConfigMap.get(GlobalSyncStatusDao.class).clone();
        globalSyncStatusDaoConfig.initIdentityScope(type);

        contactFIDaoConfig = daoConfigMap.get(ContactFIDao.class).clone();
        contactFIDaoConfig.initIdentityScope(type);

        frozenFeedItemDaoConfig = daoConfigMap.get(FrozenFeedItemDao.class).clone();
        frozenFeedItemDaoConfig.initIdentityScope(type);

        opportunityFIDaoConfig = daoConfigMap.get(OpportunityFIDao.class).clone();
        opportunityFIDaoConfig.initIdentityScope(type);

        contactFeedItemDaoConfig = daoConfigMap.get(ContactFeedItemDao.class).clone();
        contactFeedItemDaoConfig.initIdentityScope(type);

        relatedFeedItemDaoConfig = daoConfigMap.get(RelatedFeedItemDao.class).clone();
        relatedFeedItemDaoConfig.initIdentityScope(type);

        globalSyncStatusDao = new GlobalSyncStatusDao(globalSyncStatusDaoConfig, this);
        contactFIDao = new ContactFIDao(contactFIDaoConfig, this);
        frozenFeedItemDao = new FrozenFeedItemDao(frozenFeedItemDaoConfig, this);
        opportunityFIDao = new OpportunityFIDao(opportunityFIDaoConfig, this);
        contactFeedItemDao = new ContactFeedItemDao(contactFeedItemDaoConfig, this);
        relatedFeedItemDao = new RelatedFeedItemDao(relatedFeedItemDaoConfig, this);

        registerDao(GlobalSyncStatus.class, globalSyncStatusDao);
        registerDao(ContactFI.class, contactFIDao);
        registerDao(FrozenFeedItem.class, frozenFeedItemDao);
        registerDao(OpportunityFI.class, opportunityFIDao);
        registerDao(ContactFeedItem.class, contactFeedItemDao);
        registerDao(RelatedFeedItem.class, relatedFeedItemDao);
    }
    
    public void clear() {
        globalSyncStatusDaoConfig.getIdentityScope().clear();
        contactFIDaoConfig.getIdentityScope().clear();
        frozenFeedItemDaoConfig.getIdentityScope().clear();
        opportunityFIDaoConfig.getIdentityScope().clear();
        contactFeedItemDaoConfig.getIdentityScope().clear();
        relatedFeedItemDaoConfig.getIdentityScope().clear();
    }

    public GlobalSyncStatusDao getGlobalSyncStatusDao() {
        return globalSyncStatusDao;
    }

    public ContactFIDao getContactFIDao() {
        return contactFIDao;
    }

    public FrozenFeedItemDao getFrozenFeedItemDao() {
        return frozenFeedItemDao;
    }

    public OpportunityFIDao getOpportunityFIDao() {
        return opportunityFIDao;
    }

    public ContactFeedItemDao getContactFeedItemDao() {
        return contactFeedItemDao;
    }

    public RelatedFeedItemDao getRelatedFeedItemDao() {
        return relatedFeedItemDao;
    }

}
