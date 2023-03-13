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

    public int getHighestQRScore() {
        int maxScore = 0;
        for (QRCode qrCode: this.getQRList()) {
            if (qrCode.getQRScore() > maxScore) {
                maxScore = qrCode.getQRScore();
            }
        }
        return maxScore;
    }

    public int getLowestQRScore() {
        int minScore = Integer.MAX_VALUE;
        for (QRCode qrCode: this.getQRList()) {
            if (qrCode.getQRScore() < minScore) {
                minScore = qrCode.getQRScore();
            }
        }
        return minScore;
    }

    public int getSumQRScores() {
        int sumScore = 0;
        for (QRCode qrCode: this.getQRList()) {
            sumScore += qrCode.getQRScore();
        }
        return sumScore;
    }



    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", QRList=" + QRList +
                '}';
    }
}
