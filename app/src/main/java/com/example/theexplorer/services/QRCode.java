/**
 Represents a QRCode that can be scanned by the user.
 */

package com.example.theexplorer.services;

import android.util.Base64;

import com.google.firebase.firestore.GeoPoint;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;import android.os.Parcel;
import android.os.Parcelable;

public class QRCode implements Parcelable{

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
        result.put("QRName", QRName);
        result.put("QRScore", QRScore);
        result.put("location", new GeoPoint(latitude, longitude));
        result.put("photoBytes", Base64.encodeToString(photoBytes, Base64.DEFAULT));
        return result;
    }


    //code for parcelabel
    protected QRCode(Parcel in) {
        QRId = in.readString();
        photoBytes = in.createByteArray();
        QRScore = in.readInt();
        QRName = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(QRId);
        dest.writeByteArray(photoBytes);
        dest.writeInt(QRScore);
        dest.writeString(QRName);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    public static final Parcelable.Creator<QRCode> CREATOR = new Parcelable.Creator<QRCode>() {
        @Override
        public QRCode createFromParcel(Parcel in) {
            return new QRCode(in);
        }

        @Override
        public QRCode[] newArray(int size) {
            return new QRCode[size];
        }
    };

}
