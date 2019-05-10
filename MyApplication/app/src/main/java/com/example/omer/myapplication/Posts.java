package com.example.omer.myapplication;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by omer on 4/29/2018.
 */

public class Posts {

    private int ID;
    private Object _id;
    private Bitmap[] image;
    private Bitmap profile;
    private String url_avtar;
    private String author;
    private String account;
    private String date;
    private String classTitle;
    private String location;
    private int comments;
    private String likes;
    private String shares;
    private String title;
    private String Image;

    private boolean success;
    private String message;
    @SerializedName("data")
    private List<Posts> data;

    public Posts() {
    }

    public Posts(int ID,Object _id ,Bitmap[] image,String Image,Bitmap profile,String account ,String date, String location,String classTitle, int comments, String likes, String shares, String title) {
        this.ID = ID;
        this._id  = _id;
        this.image = image;
        this.author = author;
        this.Image = Image;
        this.account = account;
        this.date = date;
        this.location = location;
        this.classTitle = classTitle;
        this.comments = comments;
        this.likes = likes;
        this.shares = shares;
        this.title = title;
        this.profile = profile;
    }

    public int getID() {
        return ID;
    }
    public Object getIDPost() {
        return _id;
    }

    public Bitmap[] getImage() {
        return image;
    }

//    public String getImage() {
//        return image;
//    }


    public String getImageId() {
        return Image;
    }

    public String getAuthor() {
        return author;
    }

    public String getAccount() {
        return account;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }
    public Object getClassName() {
        return classTitle;
    }

    public List<Posts> getData() {
        return data;
    }
    public int getComments() {
        return comments;
    }

    public String getUrl_avtar() {
        return url_avtar;
    }


    public String getLikes() {
        return likes;
    }

    public String getShares() {
        return shares;
    }

    public String getTitle() {
        return title;
    }

    public Bitmap getProfile() {
        return profile;
    }

    public void setProfile(Bitmap profile) {
        this.profile = profile;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setIDPost(Object _id) {
        this._id = _id;
    }


    public void setImage(Bitmap[] image) {
        this.image = image;
    }

    public void setAccount(String account) {
        this.account = account;
    }


    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setClassName(String classTitle) {
        this.classTitle = classTitle;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public void setShares(String shares) {
        this.shares = shares;
    }

    public void setImageid(String Image) {
        this.Image = Image;
    }


    public void setTitle(String title) {
        this.title = title;
    }
}
