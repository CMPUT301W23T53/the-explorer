package com.example.theexplorer.services;

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
}
