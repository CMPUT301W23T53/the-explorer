package com.example.theexplorer.ui.scan;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.theexplorer.MainActivity;
import com.example.theexplorer.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.MultiFormatWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Locale;

/**
 * This activity allows users to scan QR codes and capture images with the camera, and display the results
 * on the screen. It also allows users to get their current location and display it on the screen.
 *
 * <p>This activity uses the following components:</p>
 * <ul>
 *   <li>{@link IntentIntegrator} to scan QR codes using the device camera</li>
 *   <li>{@link MediaStore} to capture images using the device camera</li>
 *   <li>{@link Geocoder} to get the user's current location</li>
 * </ul>
 *
 * <p>Users can also view their scan history and scores on this activity.</p>
 *
 * <p>This activity requires the following permissions:</p>
 * <ul>
 *   <li>{@link Manifest.permission#CAMERA} to access the device camera</li>
 *   <li>{@link Manifest.permission#ACCESS_FINE_LOCATION} to access the user's current location</li>
 * </ul>
 */
public class ZXingScannerScan extends AppCompatActivity implements LocationListener {
    private Button scan;
    private ImageView preview;
    Button LocationButton;
    TextView AddressText;
    LocationManager locationManager;
    private TextView score;

    private static final int REQUEST_IMAGE_CAPTURE = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        scan = findViewById(R.id.scan_button);
        preview = findViewById(R.id.image_preview);
        score = findViewById(R.id.score);
        ImageView photoTaking = findViewById(R.id.imageView_photo);
        //get address
        AddressText= findViewById(R.id.address_text_view);
        LocationButton = findViewById(R.id.address_button);
        LocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create method
                AddressText.setText("Getting the location");
                getLocation();
            }
        });


        //creating a testing bit map it can be used when no result is returned
        Bitmap bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.GRAY);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Image not found", canvas.getWidth() / 2f, canvas.getHeight() / 2f, paint);

        preview.setImageBitmap(bitmap);

        //creatig a defalt image view
        Bitmap bitmap1 = Bitmap.createBitmap(400, 300, Bitmap.Config.ARGB_8888);
        Canvas canvas1 = new Canvas(bitmap1);
        canvas1.drawColor(Color.GRAY);
        Paint paint1 = new Paint();
        paint1.setColor(Color.WHITE);
        paint1.setTextSize(30);
        paint1.setTextAlign(Paint.Align.CENTER);
        canvas1.drawText("Take a picture", canvas1.getWidth() / 2f, canvas1.getHeight() / 2f, paint);
        ImageView imageView = findViewById(R.id.imageView_photo);
        imageView.setImageBitmap(bitmap1);



        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        //IntentIntegrator
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE); //QR
        integrator.setPrompt("Scanning");
        integrator.setCameraId(0); //SELECT Camera
        integrator.setBeepEnabled(false);

        integrator.initiateScan(); //start scanning

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ZXingScannerScan.this, ZXingScannerScan.class);
                startActivity(intent);
            }
        });

        photoTaking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }
                else{
                    //prumpt camera not found
                }


            }
        });
    }

    //handle the scan result
    //may work here to get the hash
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //deal with picture taking
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            ImageView imageView = findViewById(R.id.imageView_photo);
            imageView.setImageBitmap(bitmap);
        }
        //deal with qr
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) { //scan fail
                Toast.makeText(this, "no result", Toast.LENGTH_SHORT).show();
            } else { //on success
                Toast.makeText(this, "Scan result：" + result.getContents(), Toast.LENGTH_SHORT).show();
                score.setText(result.getContents());
                //calculate the score
                String content = result.getContents();
                int theScore=0;
                for (char c : content.toCharArray()){
                    //place to implement scoring strat
                    //strat now: each & is 1 each = will have 1 and each / will have 1
                    if(c == '&' || c == '=' || c == '/'){
                        theScore++;
                    }
                }
                score.setText("Score = " + Integer.toString(theScore));

                //getting the bitmap
                Bitmap bitmap = encodeAsBitmap(result.getContents()); //result -> bitmap
                if (bitmap != null) {
                    preview.setImageBitmap(bitmap); //show in the image view for result preview
                }





            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }


    }


    //a class to convert bit map
    private Bitmap encodeAsBitmap(String contents) {
        Bitmap bitmap = null;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(contents, BarcodeFormat.QR_CODE, 500, 500);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMatrix);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    //get location
    @SuppressLint("MissingPermission")
    private void getLocation() {
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5,ZXingScannerScan.this);
        }catch (Exception e){
            e.printStackTrace();
        }}


    @Override
    public void onLocationChanged(@NonNull Location location) {
        Toast.makeText(this, ""+location.getLatitude()+","+location.getLongitude(), Toast.LENGTH_SHORT).show();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            AddressText.setText("Latitude: "+ latitude + "\n" + "Longitude: "+ longitude);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static int[] getBitmapDimensions(Bitmap bitmap) {
        int[] dimensions = new int[2];

        dimensions[0] = bitmap.getWidth();
        dimensions[1] = bitmap.getHeight();

        return dimensions;
    }






}
