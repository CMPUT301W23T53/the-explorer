/**
 The User class represents a user in the system. It contains the user's ID and a list of QR codes associated with the user.
 */

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

    /**
     * Returns the highest QR score among all QR codes associated with the user.
     *
     * @return the highest QR score
     */
    public int getHighestQRScore() {
        int maxScore = 0;
        for (QRCode qrCode: this.getQRList()) {
            if (qrCode.getQRScore() > maxScore) {
                maxScore = qrCode.getQRScore();
            }
        }
        return maxScore;
    }

    /**
     * Returns the lowest QR score among all QR codes associated with the user.
     *
     * @return the lowest QR score
     */
    public int getLowestQRScore() {
        int minScore = Integer.MAX_VALUE;
        for (QRCode qrCode: this.getQRList()) {
            if (qrCode.getQRScore() < minScore) {
                minScore = qrCode.getQRScore();
            }
        }
        return minScore;
    }

    /**
     * Returns the sum of all QR scores associated with the user.
     *
     * @return the sum of all QR scores
     */
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
