package com.example.mydaytracker.model;

public class ListType {
    String listName;
    int listSize;
    int listFinishedSize;
    int listColor;

    public ListType()   {

    }

    public ListType(String listName, int listSize) {
        this.listName = listName;
        this.listSize = listSize;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public int getListSize() {
        return listSize;
    }

    public void setListSize(int listSize) {
        this.listSize = listSize;
    }

    public int getListColor() {
        return listColor;
    }

    public void setListColor(int listColor) {
        this.listColor = listColor;
    }

    public int getListFinishedSize() {
        return listFinishedSize;
    }

    public void setListFinishedSize(int listFinishedSize) {
        this.listFinishedSize = listFinishedSize;
    }
}
