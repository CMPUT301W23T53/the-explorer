package com.example.theexplorer.services;

import java.util.List;

public class User {
    private int userId;
    private List<QRCode> QRList;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<QRCode> getQRList() {
        return QRList;
    }

    public void setQRList(List<QRCode> QRList) {
        this.QRList = QRList;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", QRList=" + QRList +
                '}';
    }
}
