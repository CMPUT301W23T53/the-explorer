package com.example.theexplorer.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import com.example.theexplorer.R;
import com.example.theexplorer.services.QRCode;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.DialogInterface;
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
import java.util.Date;
import java.util.List;

import com.example.theexplorer.services.NewUserService;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;


public class DetailPageOfOneQR extends AppCompatActivity {
    private ArrayList<String> comments;
    private ArrayAdapter<String> commentAdapter;
    String qrId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Get the selected QRCode object
        Map<String, Object> qrCode = (Map) getIntent().getSerializableExtra("qr_code_key");

//        Intent intent = getIntent();
//        QRCode qrCode = intent.getParcelableExtra("qr_code_key");


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_page_of_one_qr);
        //display info
        TextView name = findViewById(R.id.qr_name);
        name.setText("Name: " + (String) qrCode.get("qrname"));

        TextView id = findViewById(R.id.qr_id);
        id.setText("ID: " + (String) qrCode.get("qrid"));

        TextView score = findViewById(R.id.qr_score_for_detail);
        score.setText("Score: " + (long) qrCode.get("qrscore"));

        TextView location = findViewById(R.id.qr_location);

        if (qrCode.get("latitude") instanceof Double) {
            location.setText("Longitude: " + (double) qrCode.get("latitude") + " Latitude: " + (double) qrCode.get("longitude"));
        } else {
            location.setText("Longitude: " + (long) qrCode.get("latitude") + " Latitude: " + (long) qrCode.get("longitude"));
        }

        // visual representation starting here
        if (((String) qrCode.get("qrname")).contains("cool")) {
            TextView visRepEyes = findViewById(R.id.vis_rep_eyes);
            visRepEyes.setText("|  .     .  |");
        }
        if (((String) qrCode.get("qrname")).contains("Wolf")) {
            TextView visRepEyes = findViewById(R.id.vis_rep_eyes);
            visRepEyes.setText("|  +     +  |");
        }
        if (((String) qrCode.get("qrname")).contains("Sunday")) {
            TextView visRepMouth = findViewById(R.id.vis_rep_mouth);
            visRepMouth.setText("|     n     |");
        }
        if (((String) qrCode.get("qrname")).contains("Large")) {
            TextView visRepTop = findViewById(R.id.vis_rep_top);
            visRepTop.setText("========");
        }
        if (((String) qrCode.get("qrname")).contains("Tiny")) {
            TextView visRepBot = findViewById(R.id.vis_rep_bot);
            visRepBot.setText("========");
        }


        // Initialize the comments list
        comments = new ArrayList<>();

        // Add some sample comments for demonstration purposes
        qrId = (String) qrCode.get("qrid");

        NewUserService newUserService = new NewUserService();
        newUserService.getCommentsOfQRCode(qrId).addOnSuccessListener(new OnSuccessListener<List<Comment>>() {
            @Override
            public void onSuccess(List<Comment> comments) {
                Collections.sort(comments, new Comparator<Comment>() {
                    @Override
                    public int compare(Comment c1, Comment c2) {
                        return c2.getCreatedAt().compareTo(c1.getCreatedAt());
                    }
                });

                // Convert the List<Comment> to ArrayList<String> and store it in the comments variable
                ArrayList<String> commentStrings = new ArrayList<>();
                for (Comment comment : comments) {
                    commentStrings.add(comment.getContent() +" - " + "waiting function to get user id");
                }

                // Update the ArrayAdapter with the new comments data
                commentAdapter.clear();
                commentAdapter.addAll(commentStrings);
                commentAdapter.notifyDataSetChanged();
            }
        });


        // Initialize the ArrayAdapter and set it to the ListView
        commentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, comments);
        ListView commentListView = findViewById(R.id.comment_list_view);
        commentListView.setAdapter(commentAdapter);

        //set preview
        ImageView photoShow = findViewById(R.id.image_view_photo);

        ArrayList<Object> photoBytes = (ArrayList<Object>) qrCode.get("photoBytes");
        String qrimage = (String) qrCode.get("qrimage");

        if (qrimage != null) {
            byte[] decodedByteArray = Base64.decode(qrimage, Base64.DEFAULT);
            Bitmap bitmap1 = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);

            if (bitmap1 != null && bitmap1.getWidth() != 0 && bitmap1.getHeight() != 0) {
                photoShow.setVisibility(View.VISIBLE);
                photoShow.setImageBitmap(bitmap1);
            }
        }

        Button addComment = findViewById(R.id.add_comment_button);
        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = findViewById(R.id.comment_edit_text);
                String commentText = editText.getText().toString();
                if (!commentText.isEmpty()) {
                    Comment comment = new Comment();
                    comment.setCreatedAt(new Date());
                    comment.setQRId(qrId); // Set it to qrCode.getQRId()
                    comment.setContent(commentText);
                    comment.setUserId("waiting function to get user id");
                    NewUserService newUserService = new NewUserService();
                    newUserService.putComment(comment);

                    editText.setText(""); // clear EditText
                    /** uncomment this to update comment when upload is successful. ie, we have newUserService.putComment(comment).addOnSuccessListener
                     newUserService.putComment(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override public void onSuccess(Void aVoid) {
                    // Add the new comment to the adapter's data set
                    commentAdapter.add(commentText);

                    // Clear the EditText
                    editText.setText("");

                    // Notify the adapter that the data set has changed
                    commentAdapter.notifyDataSetChanged();
                    }
                    });
                     */

                    commentAdapter.insert(commentText+" - "+"waiting function to get user id", 0);
                    commentAdapter.notifyDataSetChanged();
                }
            }
        });
    }


    public Bitmap convertByteToBitmap(byte[] bytes) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }

    public static byte[] toByteArray(ArrayList<Byte> in) {
        final int n = in.size();
        byte ret[] = new byte[n];
        for (int i = 0; i < n; i++) {
            ret[i] = in.get(i);
        }
        return ret;
    }

    public static double[] toDoubleArray(ArrayList<Object> doubles) {
        double[] target = new double[doubles.size()];
        for (int i = 0; i < target.length; i++) {
            if (doubles.get(i) instanceof Double) {
                target[i] = ((Double) doubles.get(i)).doubleValue();
            } else {
                target[i] = ((Long) doubles.get(i)).doubleValue();
            }
        }
        return target;
    }

    public static byte[] toByteArray(double[] doubleArray) {
        int times = Double.SIZE / Byte.SIZE;
        byte[] bytes = new byte[doubleArray.length * times];
        for (int i = 0; i < doubleArray.length; i++) {
            ByteBuffer.wrap(bytes, i * times, times).putDouble(doubleArray[i]);
        }
        return bytes;
    }


}
