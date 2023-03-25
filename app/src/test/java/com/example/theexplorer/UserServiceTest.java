package com.example.theexplorer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.theexplorer.services.QRCode;
import com.example.theexplorer.services.RestService;
import com.example.theexplorer.services.User;
import com.example.theexplorer.services.UserService;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    private UserService userService;

    @Mock
    private RestService restService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        userService = new UserService(restService);
    }

    @Test
    public void testGetUser() throws InterruptedException, ExecutionException, IOException {
        User user = new User();
        Call<User> call = mock(Call.class);
        when(call.execute()).thenReturn(Response.success(user));
        when(restService.getUser(any(Integer.class))).thenReturn(call);

        User result = userService.getUser(1);

        assertNotNull(result);
        assertEquals(user, result);
    }

    @Test(expected = RuntimeException.class)
    public void testGetUserWithException() throws InterruptedException, ExecutionException, IOException {
        Call<User> call = mock(Call.class);
        when(call.execute()).thenThrow(new IOException());
        when(restService.getUser(any(Integer.class))).thenReturn(call);

        userService.getUser(1);
    }

    @Test
    public void testPutUser() throws InterruptedException, ExecutionException, IOException {
        User updatedUser = new User();
        Call<User> call = mock(Call.class);
        when(call.execute()).thenReturn(Response.success(updatedUser));
        when(restService.putUser(any(User.class))).thenReturn(call);

        User result = userService.putUser(updatedUser);

        assertNotNull(result);
        assertEquals(updatedUser, result);
    }

    @Test(expected = RuntimeException.class)
    public void testPutUserWithException() throws InterruptedException, ExecutionException, IOException {
        User updatedUser = new User();
        Call<User> call = mock(Call.class);
        when(call.execute()).thenThrow(new IOException());
        when(restService.putUser(any(User.class))).thenReturn(call);

        userService.putUser(updatedUser);
    }

    @Test
    public void testGetNearbyQRCodes() throws InterruptedException, ExecutionException, IOException {
        double latitude = 37.7749;
        double longitude = -122.4194;
        List<QRCode> qrCodes = Collections.singletonList(new QRCode());
        Call<List<QRCode>> call = mock(Call.class);
        when(call.execute()).thenReturn(Response.success(qrCodes));
        when(restService.getNearbyQRCodes(any(Double.class), any(Double.class), any(Integer.class))).thenReturn(call);

        List<QRCode> result = userService.getNearbyQRCodes(latitude, longitude);

        assertNotNull(result);
        assertEquals(qrCodes, result);
    }

    @Test(expected = RuntimeException.class)
    public void testGetNearbyQRCodesWithException() throws InterruptedException, ExecutionException, IOException {
        double latitude = 37.7749;
        double longitude = -122.4194;

        Call<List<QRCode>> call = mock(Call.class);
        when(call.execute()).thenThrow(new IOException());

        when(restService.getNearbyQRCodes(any(Double.class), any(Double.class), any(Integer.class))).thenReturn(call);

        List<QRCode> nearbyQRCodesFuture = userService.getNearbyQRCodes(latitude, longitude);
    }
}