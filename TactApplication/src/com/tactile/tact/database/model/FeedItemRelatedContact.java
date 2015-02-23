package com.tactile.tact.database.model;

import com.tactile.tact.database.entities.Contact;

/**
 * Created by leyan on 12/5/14.
 */
public class FeedItemRelatedContact {

    private String email;
    private String name;
    private com.tactile.tact.database.entities.Contact contact;

    public FeedItemRelatedContact() {

    }
    public FeedItemRelatedContact(String email, String name, Contact contact) {
        this.setEmail(email);
        this.setName(name);
        this.contact = contact;

    }

    public String getNameToShow() {
        if (this.contact != null && this.contact.getDisplayName() != null && !this.contact.getDisplayName().isEmpty()) {
            return this.contact.getDisplayName();
        } else if (this.name != null && !this.name.isEmpty()) {
                return this.name;
        }
        return this.email;
    }


    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
