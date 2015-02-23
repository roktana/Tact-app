package com.tactile.tact.database.entities;

import java.util.List;
import com.tactile.tact.database.dao.DaoSession;
import de.greenrobot.dao.DaoException;

import com.tactile.tact.database.dao.ContactFIDao;
import com.tactile.tact.database.dao.ContactFeedItemDao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table CONTACT_FI.
 */
public class ContactFI {

    private Long id;
    private String contactId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient ContactFIDao myDao;

    private List<ContactFeedItem> contactFeedItems;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public ContactFI() {
    }

    public ContactFI(Long id) {
        this.id = id;
    }

    public ContactFI(Long id, String contactId) {
        this.id = id;
        this.contactId = contactId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getContactFIDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<ContactFeedItem> getContactFeedItems() {
        if (contactFeedItems == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ContactFeedItemDao targetDao = daoSession.getContactFeedItemDao();
            List<ContactFeedItem> contactFeedItemsNew = targetDao._queryContactFI_ContactFeedItems(id);
            synchronized (this) {
                if(contactFeedItems == null) {
                    contactFeedItems = contactFeedItemsNew;
                }
            }
        }
        return contactFeedItems;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetContactFeedItems() {
        contactFeedItems = null;
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