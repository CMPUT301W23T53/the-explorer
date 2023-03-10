package com.example.theexplorer.services;

import android.util.Log;

import java.io.IOException;
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

    public User getUser(int userId) {
        UserService userService = new UserService();
        CompletableFuture<User> userFuture = userService.getUserAsync(userId);
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

    public User putUser(int userId, User updatedUser) {
        UserService userService = new UserService();
        CompletableFuture<User> userFuture = userService.putUserAsync(userId, updatedUser);
        User user = null;
        try {
            user = userFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    private CompletableFuture<User> putUserAsync(int userId, User updatedUser) {
        Log.d("PUT", "");
        return CompletableFuture.supplyAsync(() -> {
            Call<User> call = restService.putUser(userId, updatedUser);
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
}
