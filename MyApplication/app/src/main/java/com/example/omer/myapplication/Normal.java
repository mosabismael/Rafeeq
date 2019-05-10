package com.example.omer.myapplication;


public class Normal {

    private String _id;
    private String fullName;
    private String email;
    private String password;
    private String bio;
    private String date;
    private String newPassword;
    private String token;
    private String phone;
    private String gender;

    public void setID(String ID) {
        this._id = _id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getID() {
        return _id;
    }

    public String getFullname() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getCreated_at() {
        return date;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }


    public void setToken(String token) {
        this.token = token;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getBio() {
        return bio;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }



}
