package com.tactile.tact.database.model;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * Created by leyan on 1/29/15.
 */
public class CustomFieldObject implements Serializable {

    private static final long serialVersionUID = 3745767556221563172L;

    private String name;
    private String value;
    private String label;
    private Integer id;
    private Integer ent; //Represent Z_ENT says that 2 is Company and 3 is Person
    private Integer opt;
    private Integer contact;
    private Integer contact1;
    private Integer source;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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

    public Integer getContact1() {
        return contact1;
    }

    public void setContact1(Integer contact1) {
        this.contact1 = contact1;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public void setValuesByFields(ContactField field) {
        try {
            this.setName(field.getServerName());
            this.setValue(String.valueOf(field.getValue()));
        } catch (Exception e) {

        }
    }
}
