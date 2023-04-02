package com.example.theexplorer;

import android.content.Intent;
import android.os.Bundle;

import com.example.theexplorer.ui.home.DetailPageOfOneQR;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

public class DetailPageOfOneQRTest {
    DetailPageOfOneQR activity = Mockito.mock(DetailPageOfOneQR.class);
    Bundle bundle = Mockito.mock(Bundle.class);
    Intent intent = Mockito.mock(Intent.class);
    Map<String, Object> qrCode = new HashMap<>();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public class convertByteToBitmapTest() {

    }
}
