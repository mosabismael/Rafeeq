package com.example.omer.myapplication;

/**
 * Created by omer on 6/2/2018.
 */

public class Bussinses {
    private int ID;
    private String Title;
    private int Thumbnail;

    public Bussinses() {
    }

    public Bussinses(int thumbnail, String title, int ID) {
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
