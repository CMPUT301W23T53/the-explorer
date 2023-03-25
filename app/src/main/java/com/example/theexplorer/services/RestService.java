/**

 The RestService interface provides the endpoints to make RESTful API calls to retrieve or update user information and
 retrieve nearby QR codes.
 */

package com.example.theexplorer.services;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface RestService {

    /**
     * Retrieves a user based on their user ID.
     * @param userId The ID of the user to retrieve.
     * @return A Retrofit Call object representing the HTTP request/response.
     */
    @GET("the-explorerr")
    Call<User> getUser(@Query("userId") int userId);


    /**
     * Updates a user's information.
     * @param updatedUser A User object representing the updated user information.
     * @return A Retrofit Call object representing the HTTP request/response.
     */
    @PUT("the-explorerr")
    Call<User> putUser(@Body User updatedUser);

    /**
     * Retrieves a list of nearby QR codes based on the user's current location.
     * @param latitude The latitude of the user's current location.
     * @param longitude The longitude of the user's current location.
     * @param radius The radius in meters to search for nearby QR codes.
     * @return A Retrofit Call object representing the HTTP request/response.
     */
    @GET("the-explorer-qr")
    Call<List<QRCode>> getNearbyQRCodes(@Query("latitude") double latitude, @Query("longitude") double longitude, @Query("radius") int radius);

}
