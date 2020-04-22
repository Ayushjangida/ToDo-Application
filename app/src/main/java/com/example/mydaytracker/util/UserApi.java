package com.example.mydaytracker.util;

public class UserApi {
    private String firsName;
    private String lastName;
    private String userId;
    private static UserApi instance;

    public static UserApi getInstance() {
        if (instance == null)
            instance = new UserApi();
        return instance;

    }

    public UserApi(){}

    public String getFirsName() {
        return firsName;
    }

    public void setFirsName(String firsName) {
        this.firsName = firsName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public static void setInstance(UserApi instance) {
        UserApi.instance = instance;
    }
}
