package com.example.theexplorer.services;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface RestService {

    @GET("the-explorerr")
    Call<User> getUser(@Query("userId") int userId);

    @PUT("the-explorerr")
    Call<User> putUser(@Body User updatedUser);

    @GET("the-explorer-qr")
    Call<List<QRCode>> getNearbyQRCodes(@Query("latitude") double latitude, @Query("longitude") double longitude, @Query("radius") int radius);

}
