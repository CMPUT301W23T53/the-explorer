package com.example.theexplorer.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import com.example.theexplorer.R;
import com.example.theexplorer.services.QRCode;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class DetailPageOfOneQR extends AppCompatActivity {
    private ArrayList<String> comments;
    private ArrayAdapter<String> commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Get the selected QRCode object
        //QRCode selectedItem = getItem(position);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_page_of_one_qr);


        // Initialize the comments list
        comments = new ArrayList<>();

        // Add some sample comments for demonstration purposes
        comments.add("Great QR code!(test)");


        // Initialize the ArrayAdapter and set it to the ListView
        commentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, comments);
        ListView commentListView = findViewById(R.id.comment_list_view);
        commentListView.setAdapter(commentAdapter);
    }



}
