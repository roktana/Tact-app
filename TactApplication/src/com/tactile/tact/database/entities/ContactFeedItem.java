package com.tactile.tact.database.entities;

import com.tactile.tact.database.dao.DaoSession;
import de.greenrobot.dao.DaoException;

import com.tactile.tact.database.dao.ContactFIDao;
import com.tactile.tact.database.dao.ContactFeedItemDao;
import com.tactile.tact.database.dao.FrozenFeedItemDao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table CONTACT_FEED_ITEM.
 */
public class ContactFeedItem {

    private Long id;
    private long contactId;
    private long feedItemId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient ContactFeedItemDao myDao;

    private ContactFI contact;
    private Long contact__resolvedKey;

    private FrozenFeedItem feedItem;
    private Long feedItem__resolvedKey;


    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public ContactFeedItem() {
    }

    public ContactFeedItem(Long id) {
        this.id = id;
    }

    public ContactFeedItem(Long id, long contactId, long feedItemId) {
        this.id = id;
        this.contactId = contactId;
        this.feedItemId = feedItemId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getContactFeedItemDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }

    public long getFeedItemId() {
        return feedItemId;
    }

    public void setFeedItemId(long feedItemId) {
        this.feedItemId = feedItemId;
    }

    /** To-one relationship, resolved on first access. */
    public ContactFI getContact() {
        long __key = this.contactId;
        if (contact__resolvedKey == null || !contact__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ContactFIDao targetDao = daoSession.getContactFIDao();
            ContactFI contactNew = targetDao.load(__key);
            synchronized (this) {
                contact = contactNew;
            	contact__resolvedKey = __key;
            }
        }
        return contact;
    }

    public void setContact(ContactFI contact) {
        if (contact == null) {
            throw new DaoException("To-one property 'contactId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.contact = contact;
            contactId = contact.getId();
            contact__resolvedKey = contactId;
        }
    }

    /** To-one relationship, resolved on first access. */
    public FrozenFeedItem getFeedItem() {
        long __key = this.feedItemId;
        if (feedItem__resolvedKey == null || !feedItem__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            FrozenFeedItemDao targetDao = daoSession.getFrozenFeedItemDao();
            FrozenFeedItem feedItemNew = targetDao.load(__key);
            synchronized (this) {
                feedItem = feedItemNew;
            	feedItem__resolvedKey = __key;
            }
        }
        return feedItem;
    }

    public void setFeedItem(FrozenFeedItem feedItem) {
        if (feedItem == null) {
            throw new DaoException("To-one property 'feedItemId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.feedItem = feedItem;
            feedItemId = feedItem.getId();
            feedItem__resolvedKey = feedItemId;
        }
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}