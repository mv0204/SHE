package com.example.she.models;

public class UserModel {
    String  userId,name,profile,mail,pass;

    public UserModel() {
    }

    public UserModel(String userId, String name, String profile, String mail,String pass) {
        this.userId = userId;
        this.name = name;
        this.profile = profile;
        this.mail = mail;
        this.pass=pass;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
