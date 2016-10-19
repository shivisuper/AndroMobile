package com.shivisuper.alachat_mobile.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by shivisuper on 10/12/16.
 */

@IgnoreExtraProperties
public class User {

    private String email;
    private String name;
    private String mobile;

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, String mobile, String name) {
        this.email = email;
        this.mobile = mobile;
        this.name = name;
    }

}
