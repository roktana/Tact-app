package com.tactile.tact.database.model;

import java.io.Serializable;

/**
 * Created by leyan on 9/3/14.
 */
public class EmailName implements Serializable {
    private String email;
    private String name;

    private static final long serialVersionUID = 3645745856285583173L;

    public EmailName(String email, String name) {
        this.email = email;
        this.name = name;
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
