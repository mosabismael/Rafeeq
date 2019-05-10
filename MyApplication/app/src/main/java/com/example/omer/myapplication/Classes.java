package com.example.omer.myapplication;

/**
 * Created by omer on 4/23/2018.
 */

public class Classes {

    private int ID;
    private String Title;
    private int Thumbnail;

    public Classes() {
    }

    public Classes(int thumbnail, String title, int ID) {
        Thumbnail = thumbnail;
        Title = title;
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }

    public String getTitle() {
        return Title;
    }

    public int getThumbnail() {
        return Thumbnail;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setThumbnail(int thumbnail) {
        Thumbnail = thumbnail;
    }
}
