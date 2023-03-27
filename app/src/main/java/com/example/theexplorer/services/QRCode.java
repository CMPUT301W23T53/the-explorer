/**
 Represents a QRCode that can be scanned by the user.
 */

package com.example.theexplorer.services;

import android.util.Base64;

import com.google.firebase.firestore.GeoPoint;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class QRCode {

    private String QRId;
    private byte[] photoBytes;
    private int QRScore;
    private String QRName;
    private double latitude;
    private double longitude;
    public QRCode() {
        QRId = "temp";
        photoBytes = new byte[]{};
        QRScore = 0;
        QRName = "";
        latitude = 0;
        longitude = 0;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getQRName() {
        return QRName;
    }

    public void setQRName(String QRName) {
        this.QRName = QRName;
    }

    public String getQRId() {
        return QRId;
    }

    public void setQRId(String QRId) {
        this.QRId = QRId;
    }

    public byte[] getPhotoBytes() {
        return photoBytes;
    }

    public void setPhotoBytes(byte[] photoBytes) {
        this.photoBytes = photoBytes;
    }

    public int getQRScore() {
        return QRScore;
    }

    public void setQRScore(int QRScore) {
        this.QRScore = QRScore;
    }

    @Override
    public String toString() {
        return "QRCode{" +
                "QRId=" + QRId +
                ", photoBytes=" + Arrays.toString(photoBytes) +
                ", QRScore=" + QRScore +
                ", QRName='" + QRName + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("QRId", QRId);
        result.put("QRName", QRCodeNameGenerator.generateName(QRScore));
        result.put("QRScore", QRScore);
        result.put("location", new GeoPoint(latitude, longitude));
        result.put("photoBytes", Base64.encodeToString(photoBytes, Base64.DEFAULT));
        return result;
    }
}
