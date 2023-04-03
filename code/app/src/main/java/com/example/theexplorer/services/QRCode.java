/**
 * Represents a QRCode that can be scanned by the user.
 */

package com.example.theexplorer.services;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import com.google.firebase.firestore.GeoPoint;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class QRCode implements Parcelable {

    private String QRId;
    private ArrayList<Double> photoBytes;
    private int QRScore;
    private String QRName;
    private double latitude;
    private double longitude;
    private String QRImage;
    /**
     * Default constructor that initializes QRCode object with default values.
     */
    public QRCode() {
        QRId = "temp";
        photoBytes = new ArrayList<>();
        QRScore = 0;
        QRName = "";
        latitude = 0;
        longitude = 0;
        QRImage = "";
    }

    /**
     * Constructor that initializes QRCode object with values passed in a Parcel.
     *
     * @param in the Parcel containing the values to initialize the QRCode object
     */
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
    /**
     * Parcelable.Creator instance that generates new QRCode objects from a Parcel.
     */
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
    /**
     * Getter method for QRImage field.
     *
     * @return the QRImage field of the QRCode object
     */
    public String getQRImage() {
        return QRImage;
    }
    /**
     * Setter method for QRImage field.
     *
     * @param QRImage the QRImage field of the QRCode object to set
     */
    public void setQRImage(String QRImage) {
        this.QRImage = QRImage;
    }
    /**
     * Getter method for latitude field.
     *
     * @return the latitude field of the QRCode object
     */
    public double getLatitude() {
        return latitude;
    }
    /**
     * Setter method for latitude field.
     *
     * @param latitude the latitude field of the QRCode object to set
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    /**
     * Getter method for longitude field.
     *
     * @return the longitude field of the QRCode object
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Setter method for longitude field.
     *
     * @param longitude the longitude field of the QRCode object to set
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    /**
     * Getter method for QRName field.
     *
     * @return the QRName field of the QRCode object
     */
    public String getQRName() {
        return QRName;
    }
    /**
     * Sets the name of the QR code.
     * @param QRName the name of the QR code
     */
    public void setQRName(String QRName) {
        this.QRName = QRName;
    }
    /**
     * Gets the ID of the QR code.
     * @return the ID of the QR code
     */
    public String getQRId() {
        return QRId;
    }
    /**
     * Sets the ID of the QR code.
     * @param QRId the ID of the QR code
     */
    public void setQRId(String QRId) {
        this.QRId = QRId;
    }
    /**
     * Gets the photo bytes of the QR code.
     * @return the photo bytes of the QR code
     */
    public ArrayList<Double> getPhotoBytes() {
        return photoBytes;
    }
    /**
     * Sets the photo bytes of the QR code.
     * @param photoBytes the photo bytes of the QR code
     */
    public void setPhotoBytes(ArrayList<Double> photoBytes) {
        this.photoBytes = photoBytes;
    }
    /**
     * Gets the score of the QR code.
     * @return the score of the QR code
     */
    public int getQRScore() {
        return QRScore;
    }
    /**
     * Sets the score of the QR code.
     * @param QRScore the score of the QR code
     */
    public void setQRScore(int QRScore) {
        this.QRScore = QRScore;
    }
    /**
     * Returns a string representation of the QR code.
     * @return a string representation of the QR code
     */
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
    /**
     * Converts the QR code object to a map.
     * @return a map representation of the QR code object
     */
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
    /**
     * Converts a double array to a byte array.
     * @param doubleArray the double array to be converted
     * @return the converted byte array
     */
    public static byte[] toByteArray1(double[] doubleArray) {
        int times = Double.SIZE / Byte.SIZE;
        byte[] bytes = new byte[doubleArray.length * times];
        for (int i = 0; i < doubleArray.length; i++) {
            ByteBuffer.wrap(bytes, i * times, times).putDouble(doubleArray[i]);
        }
        return bytes;
    }

    /**
     * Converts an array list of doubles to an array of doubles.
     * @param doubles the array list of doubles to be converted
     * @return the converted array of doubles
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Returns a bitmask indicating the set of special object types marshaled by this Parcelable object instance.
     * @return a bitmask indicating the set of special object types marshaled by this Parcelable object
     */
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
