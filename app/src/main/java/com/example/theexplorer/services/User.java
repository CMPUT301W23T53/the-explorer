package com.example.theexplorer.services;

import java.util.List;

public class User {
    private int userId;
    private List<Integer> QRList;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<Integer> getQRList() {
        return QRList;
    }

    public void setQRList(List<Integer> QRList) {
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
