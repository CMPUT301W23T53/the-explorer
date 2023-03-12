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
    private final RestService restService = retrofit.create(RestService.class);
    private final int RADIUS_NEARBY_QR = 300;

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
        Log.d("PUT", "");
        return CompletableFuture.supplyAsync(() -> {
            Call<User> call = restService.putUser(updatedUser);
            try {
                Response<User> response = call.execute();
                if (response.isSuccessful()) {
                    Log.d("UPDATED ", response.body().toString());
                    return response.body();
                } else {
                    throw new RuntimeException("Failed to get user");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

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
