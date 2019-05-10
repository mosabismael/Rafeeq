package com.example.omer.myapplication;

/**
 * Created by omer on 4/26/2018.
 */

public class Items {

    private  int ID;
    private  String name;
    private  String description;
    private  int thumbnail;

    public Items() {
    }

    public Items(int ID, String name, String description, int thumbnail) {
        this.ID = ID;
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
    }

    public int getID() {
        return ID;
    }

    public String getTitle() {
        return name;
    }

    public String getDesc() {
        return description;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setTitle(String name) {
        this.name = name;
    }

    public void setDesc(String description) {
        this.description = description;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }
}
