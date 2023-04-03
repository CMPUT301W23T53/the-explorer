package com.example.theexplorer.ui.auth;

/**
 The UserModel class represents a user object with basic information such as photo, username, and email.
 */
public class UserModel {
    private String photo;
    private String userName;
    private String email;

    /**
     * Constructs a new UserModel object with the specified photo, username,
     * and email.
     *
     * @param photo    The user's profile photo URL
     * @param userName The user's username
     * @param email    The user's email address
     */
    public UserModel(String photo, String userName, String email) {
        this.photo = photo;
        this.userName = userName;
        this.email = email;
    }

    /**
     * Returns the user's profile photo URL.
     *
     * @return The user's profile photo URL
     */
    public String getPhoto() {
        return photo;
    }

    /**
     * Sets the user's profile photo URL.
     *
     * @param photo The new profile photo URL
     */
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    /**
     * Returns the user's username.
     *
     * @return The user's username
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the user's username.
     *
     * @param userName The new username
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Returns the user's email address.
     *
     * @return The user's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email address.
     *
     * @param email The new email address
     */
    public void setEmail(String email) {
        this.email = email;
    }
}
