package com.example.omer.myapplication;

public class Comments {
    private  int ID;
    private  String author;
    private  String content;
    private String account;
    private  String post_id;
    private  int thumbnail;

    public Comments() {
    }

    public Comments(int ID,String account, String content, int thumbnail) {
        this.ID = ID;
        this.author = author;
        this.content = content;
        this.account = account;
        this.thumbnail = thumbnail;
    }

    public int getID() {
        return ID;
    }

    public String getAccount() {
        return account;
    }

    public String getPost() {
        return post_id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setAccount(String account) {
        this.account = account;
    }

}
