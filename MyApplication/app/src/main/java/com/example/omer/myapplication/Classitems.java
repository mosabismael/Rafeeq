package com.example.omer.myapplication;

/**
 * Created by musab on 12/18/2018.
 */

/**
 * Created by omer on 4/23/2018.
 */

public class Classitems {

    private int ID;
    private String name;
    private String description;
    private int pics;

    public Classitems() {
    }

    public Classitems(int pics, String name, int ID) {
        pics = pics;
        name = name;
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }

    public String getTitle() {
        return name;
    }

    public int getThumbnail() {
        return pics;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setTitle(String pics) {
        pics = pics;
    }

    public void setThumbnail(int pics) {
        pics = pics;
    }
}
