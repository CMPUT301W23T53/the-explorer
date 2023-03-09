package com.example.theexplorer.ui.scan;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.theexplorer.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.MultiFormatWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;


public class ZXingScannerScan extends AppCompatActivity {
    private Button scan;
    private ImageView preview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        scan = findViewById(R.id.scan_button);
        preview = findViewById(R.id.image_preview);

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
    }

    //handle the scan result
    //may work here to get the hash
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) { //scan fail
                Toast.makeText(this, "no result", Toast.LENGTH_SHORT).show();
            } else { //on success
                Toast.makeText(this, "Scan result：" + result.getContents(), Toast.LENGTH_SHORT).show();
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



}
