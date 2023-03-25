/**

 UserService class provides methods for interacting with the user REST API and QRCode REST API.
 It contains methods for getting a user, updating a user, and getting nearby QR codes.
 Uses Retrofit and CompletableFuture for async operations.
 */

package com.example.theexplorer.services;

import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserService {

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://d6392oa4v7.execute-api.us-east-2.amazonaws.com/default/")
                .addConverterFactory(GsonConverterFactory.create())
            .build();
    private RestService restService = retrofit.create(RestService.class);
    private final int RADIUS_NEARBY_QR = 300;

    /**
     * Creates a new UserService object with the given RestService object.
     * @param restService the RestService object used to make REST API calls
     */
    public UserService(RestService restService) {
        this.restService = restService;
    }

    public UserService() {
    }

    /**
     * Gets a user with the given userId.
     * @param userId the id of the user to get
     * @return the User object with the given userId
     * @throws RuntimeException if there is an error executing the REST API call
     */
    public User getUser(int userId) {
        CompletableFuture<User> userFuture = this.getUserAsync(userId);
        User user = null;
        try {
            user = userFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    private CompletableFuture<User> getUserAsync(int userId) {
        return CompletableFuture.supplyAsync(() -> {
            Call<User> call = restService.getUser(userId);
            try {
                Response<User> response = call.execute();
                if (response.isSuccessful()) {
                    return response.body();
                } else {
                    throw new RuntimeException("Failed to get user");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Updates a user with the given updatedUser object.
     * @param updatedUser the updated User object
     * @return the updated User object
     * @throws RuntimeException if there is an error executing the REST API call
     */
    public User putUser(User updatedUser) {
        CompletableFuture<User> userFuture = this.putUserAsync(updatedUser);
        User user = null;
        try {
            user = userFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    private CompletableFuture<User> putUserAsync(User updatedUser) {
        return CompletableFuture.supplyAsync(() -> {
            Call<User> call = restService.putUser(updatedUser);
            try {
                Response<User> response = call.execute();
                if (response.isSuccessful()) {
                    return response.body();
                } else {
                    throw new RuntimeException("Failed to get user");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Gets nearby QR codes for the given latitude and longitude.
     * @param latitude the latitude of the user's current location
     * @param longitude the longitude of the user's current location
     * @return a List of QRCode objects that are within the RADIUS_NEARBY_QR
     * @throws RuntimeException if there is an error executing the REST API call
    */
    public List<QRCode> getNearbyQRCodes(double latitude, double longitude) {
        CompletableFuture<List<QRCode>> nearbyQRCodesFuture = this.getNearbyQRCodeAsync(latitude, longitude);
        List<QRCode> nearbyQRCodes = null;
        try {
            nearbyQRCodes = nearbyQRCodesFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return nearbyQRCodes;
    }

    private CompletableFuture<List<QRCode>> getNearbyQRCodeAsync(double latitude, double longitude) {
        return CompletableFuture.supplyAsync(() -> {
            Call<List<QRCode>> call = restService.getNearbyQRCodes(latitude, longitude, RADIUS_NEARBY_QR);
            try {
                Response<List<QRCode>> response = call.execute();
                if (response.isSuccessful()) {
                    return response.body();
                } else {
                    throw new RuntimeException("Failed to get user");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
