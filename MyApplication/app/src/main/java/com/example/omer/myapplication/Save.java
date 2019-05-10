package com.example.omer.myapplication;

/**
 * Created by musab on 12/23/2018.
 */

public class Save {
    private int ID;
    private String author;
    private String post;

    public Save() {
    }
    public Save(int ID , String author , String post) {
        this.ID = ID;
        this.author = author;
        this.post = post;
    }
    public String getUser() {
        return author;
    }
    public String getPost() {
        return post;
    }

    public void setUser(String author) {
        this.author = author;
    }
    public void setPost(String post) {
        this.post = post;
    }


}
