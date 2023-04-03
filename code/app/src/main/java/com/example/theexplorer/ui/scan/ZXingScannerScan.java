package com.example.theexplorer.ui.scan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.theexplorer.MainActivity;
import com.example.theexplorer.R;
import com.example.theexplorer.services.NewUserService;
import com.example.theexplorer.services.QRCode;
import com.example.theexplorer.services.QRCodeNameGenerator;
import com.example.theexplorer.services.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;


public class ZXingScannerScan extends AppCompatActivity implements LocationListener {
    private Button scan;
    private ImageView preview;
    Button LocationButton;
    TextView AddressText;
    EditText editQrName;
    LocationManager locationManager;
    private TextView score;
    final NewUserService newUserService = new NewUserService();
    final User[] user = {new User()};

    public QRCode qrCode = new QRCode();

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private boolean hideQR = false;

    private Bitmap QRBitmap;

    String userEmail1;

    /**
     * create the UI
     * set buttons for use
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        scan = findViewById(R.id.scan_button);
        preview = findViewById(R.id.image_preview);
        score = findViewById(R.id.score);
        ImageView photoTaking = findViewById(R.id.imageView_photo);
        ImageView previewView = findViewById(R.id.image_preview);
        //get address
        AddressText = findViewById(R.id.address_text_view);
        LocationButton = findViewById(R.id.address_button);
        LocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create method
                AddressText.setText("Getting the location");
                getLocation();
            }
        });


        preview.setImageBitmap(notFound());
        QRBitmap = notFound();
        ImageView imageView = findViewById(R.id.imageView_photo);
        imageView.setImageBitmap(takePhoto());
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

        previewView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!hideQR) {
                    previewView.setImageBitmap(makeUpQR());
                    hideQR = true;
                } else {
                    previewView.setImageBitmap(QRBitmap);
                    hideQR = false;
                }
                return true;
            }
        });
        photoTaking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                } else {
                    //prumpt camera not found
                }
            }
        });

        // add qr to user
        Button add;
        add = findViewById(R.id.add_button);


        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userEmail1 = firebaseUser.getEmail();
        newUserService.getUser(userEmail1).addOnSuccessListener(new OnSuccessListener<User>() {
            @Override
            public void onSuccess(User fetchUser) {
                user[0] = fetchUser;
                Log.e("USER", user[0].toString());
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<QRCode> qrCodeList = user[0].getQRList();

                if (qrCode.getQRId() != "temp") {
                    qrCodeList.add(qrCode);
                }

                User user1 = new User();
                user1.setUserId(userEmail1);
                user1.setQRList(qrCodeList);

                newUserService.putUser(user1);

                Intent intent = new Intent(ZXingScannerScan.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }


    /**
     * handle results
     * handle result of scanning
     * handle result of taking picture
     * interact with database
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //deal with picture taking
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            double[] doubleArray = toDoubleArray(byteArray);

            String imageB64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            qrCode.setQRImage(imageB64);

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
                //calculate and display the score
                int theScore = calculateScore(result);
                score.setText("Score = " + Integer.toString(theScore));
                qrCode.setQRScore(theScore);

                String qrName = QRCodeNameGenerator.generateName(theScore);
                qrCode.setQRName(qrName);

                String generateUUIDNo = String.format("%010d", new BigInteger(UUID.randomUUID().toString().replace("-", ""), 16));
                String unique_no = generateUUIDNo.substring(generateUUIDNo.length() - 10);
                qrCode.setQRId(unique_no);

                //getting the bitmap (of qr)
                Bitmap bitmap = encodeAsBitmap(result.getContents()); //result -> bitmap
                if (bitmap != null) {
                    preview.setImageBitmap(bitmap); //show in the image view for result preview
                    QRBitmap = bitmap;
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * a class to convert bit map
     *
     * @param contents
     * @return
     */
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

    // Trying to get the system services and request to update Location
    @SuppressLint("MissingPermission")
    private void getLocation() {
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            Location lastLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location lastLocationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (lastLocationGPS != null) {
                onLocationChanged(lastLocationGPS);
            } else if (lastLocationNetwork != null) {
                onLocationChanged(lastLocationNetwork);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, ZXingScannerScan.this);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, ZXingScannerScan.this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the current location
     * <p>
     * This method won't returns anything. When call this function it will get the current location and set the AddressText.
     * AddressText will show the  latitude and longitude for the current location
     *
     * @param location the location for current location
     * @return null
     */
    @Override
    public void onLocationChanged(@NonNull Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        AddressText.setText("Latitude: " + latitude + "\n" + "Longitude: " + longitude);
        qrCode.setLatitude(latitude);
        qrCode.setLongitude(longitude);

    }

    /**
     * Generates a bitmap that displays "Image not found" message for testing purposes.
     * @return a bitmap that displays "Image not found" message.
     */
    public Bitmap notFound() {
        //creating a testing bit map it can be used when no result is returned
        Bitmap bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.GRAY);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Image not found", canvas.getWidth() / 2f, canvas.getHeight() / 2f, paint);
        return bitmap;
    }

    /**
     * Generates a default image view that displays "Take a picture" message.
     * @return a bitmap that displays "Take a picture" message.
     */
    public Bitmap takePhoto() {
        //creatig a defalt image view
        Bitmap bitmap1 = Bitmap.createBitmap(400, 300, Bitmap.Config.ARGB_8888);
        Canvas canvas1 = new Canvas(bitmap1);
        canvas1.drawColor(Color.GRAY);
        Paint paint1 = new Paint();
        paint1.setColor(Color.WHITE);
        paint1.setTextSize(30);
        paint1.setTextAlign(Paint.Align.CENTER);
        canvas1.drawText("Take a picture", canvas1.getWidth() / 2f, canvas1.getHeight() / 2f, paint1);
        return bitmap1;
    }

    /**
     * Calculates a score for the given QR code content based on a predefined strategy.
     * Currently, each '&' is counted as 1, each '=' is counted as 1, and each '/' is counted as 1.
     * In addition, each digit is counted based on its value, and each letter is counted based on its ASCII value.
     * @param result the IntentResult object that contains the QR code content.
     * @return an integer value representing the calculated score.
     */
    public int calculateScore(IntentResult result) {
        String content = result.getContents();
        int theScore = 0;
        for (char c : content.toCharArray()) {
            //place to implement scoring strat
            //strat now: each & is 1 each = will have 1 and each / will have 1
            if (c == '&' || c == '=' || c == '/') {
                theScore++;
            }
            for(int i=48; i<57;i++){
                if(c == (char)i){
                    theScore+=(i-48);
                }
            }
            if (c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z') {
                theScore += (int) c;
            }

        }
        theScore += content.length();
        return theScore;
    }

    /**
     * Generates a bitmap of a black and white checkerboard pattern with the text "HIDDEN" in the center.
     * @return a bitmap of a black and white checkerboard pattern with the text "HIDDEN" in the center.
     */
    public Bitmap makeUpQR() {
        Bitmap bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.GRAY);
        Paint squarePaint = new Paint();
        squarePaint.setColor(Color.BLACK);
        int squareSize = 50;
        for (int i = 0; i < canvas.getWidth(); i += squareSize) {
            for (int j = 0; j < canvas.getHeight(); j += squareSize) {
                if ((i / squareSize + j / squareSize) % 2 == 0) {
                    canvas.drawRect(i, j, i + squareSize, j + squareSize, squarePaint);
                }
            }
        }
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(50);
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("HIDDEN", canvas.getWidth() / 2f, canvas.getHeight() / 2f, textPaint);
        return bitmap;
    }

    public static double[] toDoubleArray(byte[] byteArray) {
        int times = Double.SIZE / Byte.SIZE;
        double[] doubles = new double[byteArray.length / times];
        for (int i = 0; i < doubles.length; i++) {
            doubles[i] = ByteBuffer.wrap(byteArray, i * times, times).getDouble();
        }
        return doubles;
    }

}