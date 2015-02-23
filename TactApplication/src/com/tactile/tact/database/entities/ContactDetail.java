package com.tactile.tact.database.entities;

import com.tactile.tact.database.model.ContactField;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by leyan on 12/1/14.
 * Describe a Contact Detail object from database
 */
public class ContactDetail implements Serializable{

    private static final long serialVersionUID = 3916770827233383373L;

    private Integer id; // represent Z_PK field
    private Integer ent;
    private Integer opt;
    private Integer contact; // this is the relation field with Contact table by it's Z_PK field
    private Integer sourceId;
    private String annualRevenue;
    private String businessCity;
    private String businessCountry;
    private String businessMail;
    private String businessFax;
    private String businessPhone;
    private String businessPostalCode;
    private String businessState;
    private String businessStreet;
    private String company;
    private String companyId;
    private String companyName;
    private String department;
    private String descriptionField;
    private String facebookUserName;
    private String firstName;
    private String homePhone;
    private String industry;
    private String jobTitle;
    private String lastName;
    private String linkedInURL;
    private String linkedInId;
    private String mobilePhone;
    private String name;
    private String notes;
    private String numberOfEmployees;
    private String otherEmail;
    private String personalEmail;
    private String photoURL;
    private String remoteId;
    private String tickerSymbol;
    private String twitterUsername;
    private String website;

    public boolean isEmailRelated(String email) {
        if ((this.getBusinessMail() != null && this.getBusinessMail().equals(email)) ||
                (this.getOtherEmail() != null && this.getOtherEmail().equals(email)) ||
                (this.getPersonalEmail() != null && this.getPersonalEmail().equals(email))) {
            return true;
        }
        return false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEnt() {
        return ent;
    }

    public void setEnt(Integer ent) {
        this.ent = ent;
    }

    public Integer getOpt() {
        return opt;
    }

    public void setOpt(Integer opt) {
        this.opt = opt;
    }

    public Integer getContact() {
        return contact;
    }

    public void setContact(Integer contact) {
        this.contact = contact;
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public String getAnnualRevenue() {
        return annualRevenue;
    }

    public void setAnnualRevenue(String annualRevenue) {
        this.annualRevenue = annualRevenue;
    }

    public String getBusinessCity() {
        return businessCity;
    }

    public void setBusinessCity(String businessCity) {
        this.businessCity = businessCity;
    }

    public String getBusinessCountry() {
        return businessCountry;
    }

    public void setBusinessCountry(String businessCountry) {
        this.businessCountry = businessCountry;
    }

    public String getBusinessMail() {
        return businessMail;
    }

    public void setBusinessMail(String businessMail) {
        this.businessMail = businessMail;
    }

    public String getBusinessFax() {
        return businessFax;
    }

    public void setBusinessFax(String businessFax) {
        this.businessFax = businessFax;
    }

    public String getBusinessPhone() {
        return businessPhone;
    }

    public void setBusinessPhone(String businessPhone) {
        this.businessPhone = businessPhone;
    }

    public String getBusinessPostalCode() {
        return businessPostalCode;
    }

    public void setBusinessPostalCode(String businessPostalCode) {
        this.businessPostalCode = businessPostalCode;
    }

    public String getBusinessState() {
        return businessState;
    }

    public void setBusinessState(String businessState) {
        this.businessState = businessState;
    }

    public String getBusinessStreet() {
        return businessStreet;
    }

    public void setBusinessStreet(String businessStreet) {
        this.businessStreet = businessStreet;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDescriptionField() {
        return descriptionField;
    }

    public void setDescriptionField(String descriptionField) {
        this.descriptionField = descriptionField;
    }

    public String getFacebookUserName() {
        return facebookUserName;
    }

    public void setFacebookUserName(String facebookUserName) {
        this.facebookUserName = facebookUserName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLinkedInURL() {
        return linkedInURL;
    }

    public void setLinkedInURL(String linkedInURL) {
        this.linkedInURL = linkedInURL;
    }

    public String getLinkedInId() {
        return linkedInId;
    }

    public void setLinkedInId(String linkedInId) {
        this.linkedInId = linkedInId;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getNumberOfEmployees() {
        return numberOfEmployees;
    }

    public void setNumberOfEmployees(String numberOfEmployees) {
        this.numberOfEmployees = numberOfEmployees;
    }

    public String getOtherEmail() {
        return otherEmail;
    }

    public void setOtherEmail(String otherEmail) {
        this.otherEmail = otherEmail;
    }

    public String getPersonalEmail() {
        return personalEmail;
    }

    public void setPersonalEmail(String personalEmail) {
        this.personalEmail = personalEmail;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public String getTickerSymbol() {
        return tickerSymbol;
    }

    public void setTickerSymbol(String tickerSymbol) {
        this.tickerSymbol = tickerSymbol;
    }

    public String getTwitterUsername() {
        return twitterUsername;
    }

    public void setTwitterUsername(String twitterUsername) {
        this.twitterUsername = twitterUsername;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setValuesByFields(ContactField field) {
        try {
            Field fieldDefinition = this.getClass().getDeclaredField(field.getEntityName());
            fieldDefinition.set(this, field.getValue());
        } catch (Exception e) {

        }
    }
}
