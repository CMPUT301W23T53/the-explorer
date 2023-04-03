/**
 The User class represents a user in the system. It contains the user's ID and a list of QR codes associated with the user.
 */

package com.example.theexplorer.services;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String userId;
    private List<QRCode> QRList;

    public User() {
        userId = "";
        QRList = new ArrayList<>();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<QRCode> getQRList() {
        return QRList;
    }

    public void setQRList(List<QRCode> QRList) {
        this.QRList = QRList;
    }

}
