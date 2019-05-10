package com.example.omer.myapplication.model;

import com.example.omer.myapplication.Posts;

import java.util.List;

/**
 * Created by Shadik on 1/28/2017.
 */

public class PostModel {


    private boolean success;
    private String message;
    private List<Posts> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Posts> getData() {
        return data;
    }

    public void setData(List<Posts> data) {
        this.data = data;
    }


}
