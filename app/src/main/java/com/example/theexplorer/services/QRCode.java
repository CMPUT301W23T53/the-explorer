package com.example.theexplorer.services;

import java.nio.ByteBuffer;

public class QRCode {

    private int QRId;
    private byte[] photoBytes;
    private int QRScore;

    public int getQRId() {
        return QRId;
    }

    public void setQRId(int QRId) {
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
                ", photoBytes=" + photoBytes +
                ", QRScore=" + QRScore +
                '}';
    }
}
