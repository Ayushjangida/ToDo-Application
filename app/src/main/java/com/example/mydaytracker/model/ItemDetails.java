package com.example.mydaytracker.model;

import java.util.Date;

public class ItemDetails {
    String item;
    String listName;
    Date dateAdded;
    boolean itemCompleted;
    int listColor;

    public ItemDetails()    {

    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public boolean isItemCompleted() {
        return itemCompleted;
    }

    public void setItemCompleted(boolean itemCompleted) {
        this.itemCompleted = itemCompleted;
    }

    public int getListColor() {
        return listColor;
    }

    public void setListColor(int listColor) {
        this.listColor = listColor;
    }
}
