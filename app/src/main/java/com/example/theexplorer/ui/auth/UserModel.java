package com.example.theexplorer.ui.auth;

public class UserModel {
    private String photo;
    private String userName;
    private String email;

    public UserModel(String photo, String userName, String email) {
        this.photo = photo;
        this.userName = userName;
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
