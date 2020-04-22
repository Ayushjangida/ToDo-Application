package com.example.mydaytracker.util;

public class ListDetailApi {
    String listName;
    private static ListDetailApi instance;

    public ListDetailApi(String listName) {
        this.listName = listName;
    }
    public static ListDetailApi getInstance() {
        if (instance == null)
            instance = new ListDetailApi();
        return instance;

    }

    public  ListDetailApi() {

    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }
}

