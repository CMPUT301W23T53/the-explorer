package com.example.theexplorer.ui.auth;

public class UserModel {
    private String email;
    private String name;
    public UserModel()
    {

    }

    public UserModel(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
