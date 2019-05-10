package com.example.omer.myapplication.model;

public class UploadObject {

    private String message;
    private String token;
    private String path;
    private String success;

    public UploadObject(String success) {
        this.success = success;
    }
    public String getSuccess() {
        return success;
    }
    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public String getPath() {
        return path;
    }

}
