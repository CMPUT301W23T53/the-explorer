package com.example.theexplorer.services;

/**
 * OnQRCodeDeletedListener is an interface that defines a single method to be called
 * when a QR code is deleted. Classes implementing this interface should provide
 * their own implementation for handling the deletion of a QR code.
 */
public interface OnQRCodeDeletedListener {

    /**
     * This method is called when a QR code is deleted, providing the ID of the deleted QR code.
     *
     * @param qrID the ID of the deleted QR code.
     */
    void onQRCodeDeleted(String qrID);
}
