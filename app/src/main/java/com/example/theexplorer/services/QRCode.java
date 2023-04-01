/**
 * Represents a QRCode that can be scanned by the user.
 */

package com.example.theexplorer.services;

import android.util.Base64;

import com.google.firebase.firestore.GeoPoint;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

public class QRCode implements Parcelable {

    private String QRId;
    private ArrayList<Double> photoBytes;
    private int QRScore;
    private String QRName;
    private double latitude;
    private double longitude;
    private String QRImage;

    public QRCode() {
        QRId = "temp";
        photoBytes = new ArrayList<>();
        QRScore = 0;
        QRName = "";
        latitude = 0;
        longitude = 0;
        QRImage = "";
    }

    protected QRCode(Parcel in) {
        QRId = in.readString();
        QRScore = in.readInt();
        QRName = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        QRImage = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(QRId);
        dest.writeInt(QRScore);
        dest.writeString(QRName);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(QRImage);
    }

    public static final Creator<QRCode> CREATOR = new Creator<QRCode>() {
        @Override
        public QRCode createFromParcel(Parcel in) {
            return new QRCode(in);
        }

        @Override
        public QRCode[] newArray(int size) {
            return new QRCode[size];
        }
    };

    public String getQRImage() {
        return QRImage;
    }

    public void setQRImage(String QRImage) {
        this.QRImage = QRImage;
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

    public ArrayList<Double> getPhotoBytes() {
        return photoBytes;
    }

    public void setPhotoBytes(ArrayList<Double> photoBytes) {
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
                ", photoBytes=" + Arrays.toString(toDoubleArray(photoBytes)) +
                ", QRScore=" + QRScore +
                ", QRName='" + QRName + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", QRImage=" + QRImage +
                '}';
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("QRId", QRId);
        result.put("QRName", QRCodeNameGenerator.generateName(QRScore));
        result.put("QRScore", QRScore);
        result.put("location", new GeoPoint(latitude, longitude));
        result.put("photoBytes", Base64.encodeToString(toByteArray1(toDoubleArray(photoBytes)), Base64.DEFAULT));
        result.put("QRImage", QRImage);
        return result;
    }

    public static byte[] toByteArray1(double[] doubleArray) {
        int times = Double.SIZE / Byte.SIZE;
        byte[] bytes = new byte[doubleArray.length * times];
        for (int i = 0; i < doubleArray.length; i++) {
            ByteBuffer.wrap(bytes, i * times, times).putDouble(doubleArray[i]);
        }
        return bytes;
    }


    @Override
    public int describeContents() {
        return 0;
    }


    public static double[] toDoubleArray(ArrayList<Double> doubles) {
        double[] target = new double[doubles.size()];
        for (int i = 0; i < target.length; i++) {
            target[i] = doubles.get(i).doubleValue();  // java 1.4 style
            // or:
//            target[i] = doubles.get(i);                // java 1.5+ style (outboxing)
        }
        return target;
    }

}
