package com.example.omer.myapplication.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Shadik on 1/28/2017.
 */

public class TagModel {
    private String tagName;
    @SerializedName("_id")
    private String tId;

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTId() {
        return tId;
    }

    public void setTId(String tId) {
        this.tId = tId;
    }
}
