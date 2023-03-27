package com.example.theexplorer.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import com.example.theexplorer.R;
import com.example.theexplorer.services.QRCode;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;import android.graphics.Bitmap;import android.graphics.BitmapFactory;import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.theexplorer.R;
import com.example.theexplorer.services.Comment;
import com.example.theexplorer.services.NewUserService;
import com.example.theexplorer.services.QRCode;
import com.example.theexplorer.services.User;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;


public class DetailPageOfOneQR extends AppCompatActivity {
    private ArrayList<String> comments;
    private ArrayAdapter<String> commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Get the selected QRCode object
        Intent intent = getIntent();
        QRCode qrCode = intent.getParcelableExtra("qr_code_key");


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

        //set preview
        ImageView photoShow = findViewById(R.id.image_view_photo);
        Bitmap photoBitmap = convertByteToBitmap(qrCode.getPhotoBytes());
        if(photoBitmap!=null && photoBitmap.getWidth()!=0 && photoBitmap.getHeight()!=0){
            photoShow.setVisibility(View.VISIBLE);
            photoShow.setImageBitmap(photoBitmap);
        }

        Button addComment = findViewById(R.id.add_comment_button);
        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = findViewById(R.id.comment_edit_text);
                String commentText = editText.getText().toString();

                Comment comment = new Comment();
                comment.setQRId(qrCode.get); // Set it to qrCode.getQRId()
                comment.setContent("test contentttt");
                newUserService.putComment(comment);

                editText.setText(""); // 清空 EditText 视图
            }
        });
    }



    public Bitmap convertByteToBitmap(byte[] bytes) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }

    public void onAddCommentClick(View view) {


    }



}
