package com.e.cloud_login.Data;

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    public String username;
    public String password;
    public String email;
    public String photo;
    public double space;


    public User(String account, String md5, String email, String photo, double space) {
        this.username =account;
        this.password=md5;
        this.email=email;
        this.photo=photo;
        this.space=space;
    }
}

