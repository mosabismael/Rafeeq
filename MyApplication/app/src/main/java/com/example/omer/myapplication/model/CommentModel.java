package com.example.omer.myapplication.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Shadik on 1/28/2017.
 */

public class CommentModel {
    private String updatedAt;
    private String createdAt;
    private String message;
    private String userName;
    private int commentRecommendation;
    @SerializedName("_id")
    private String cId;

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public int getCommentRecommendation() {
        return commentRecommendation;
    }

    public void setCommentRecommendation(int commentRecommendation) {
        this.commentRecommendation = commentRecommendation;
    }

    public String getCId() {
        return cId;
    }

    public void setCId(String cId) {
        this.cId = cId;
    }
}
