package com.tactile.tact.database.entities;

/**
 * Created by sebafonseca on 11/14/14.
 */
public class Opportunity {

    private String id;
    private Boolean isClosed;
    private Boolean isWon;
    private String company;
    private String source;
    private String closeDate;
    private String amount;
    private String expectedRevenue;
    private String probability;
    private String quantity;
    private String forecastCategory;
    private String leadSource;
    private String name;
    private String nextStep;
    private String description;
    private String ownerID;
    private String ownerName;
    private String stageName;
    private String type;

    public Opportunity(){}

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public Boolean getIsClosed() {
        return isClosed;
    }

    public void setIsClosed(Boolean isClosed) {
        this.isClosed = isClosed;
    }

    public Boolean getIsWon() {
        return isWon;
    }

    public void setIsWon(Boolean isWon) {
        this.isWon = isWon;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(String closeDate) {
        this.closeDate = closeDate;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getExpectedRevenue() {
        return expectedRevenue;
    }

    public void setExpectedRevenue(String expectedRevenue) {
        this.expectedRevenue = expectedRevenue;
    }

    public String getProbability() {
        return probability;
    }

    public void setProbability(String probability) {
        this.probability = probability;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getForecastCategory() {
        return forecastCategory;
    }

    public void setForecastCategory(String forecastCategory) {
        this.forecastCategory = forecastCategory;
    }

    public String getLeadSource() {
        return leadSource;
    }

    public void setLeadSource(String leadSource) {
        this.leadSource = leadSource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNextStep() {
        return nextStep;
    }

    public void setNextStep(String nextStep) {
        this.nextStep = nextStep;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
