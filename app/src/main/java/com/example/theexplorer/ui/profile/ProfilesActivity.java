package com.example.theexplorer.ui.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.theexplorer.R;
import com.example.theexplorer.ui.auth.Register;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilesActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private TextView txtemail, tvName;
    TextView etUserName;
    private Toolbar toolbar;
    private CircleImageView img;
    private CircleImageView ivEditPhoto;
    FirebaseFirestore firebaseFirestore;
    private final int GALLERY_IMAGE = 33;

    boolean uploadImage;
    File file;
    Uri selectedUri;
    String email1;
    String email;
    String name;
    String photo;
    String userName;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiles);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Profile");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        txtemail = findViewById(R.id.tvEmail);
        etUserName = findViewById(R.id.etUserName);
        tvName = findViewById(R.id.tvName);
        img = findViewById(R.id.img);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait");
        progressDialog.setCancelable(false);

        String userName1 = getIntent().getStringExtra("userName1");
        checkUser(userName1);
    }

    private void checkUser(String userName1) {
        showUserData(userName1);
    }

    private void showUserData(String userName1) {
        firebaseFirestore.collection("Users").whereEqualTo("userName", userName1).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e("-*-*-*-*-*-*-", document.getId() + " => " + document.getData());

                                email = document.getString("email");
                                name = document.getString("name");
                                photo = document.getString("photo");
                                userName = document.getString("userName");

                                txtemail.setText(email);
                                tvName.setText(name);
                                etUserName.setText(userName);

                                Glide.with(ProfilesActivity.this).load(photo).error(R.drawable.ic_baseline_person_24).into(img);

                                Log.e("-*-*-*-*-*-", "onComplete: ..." + email);
                            }
                        } else {
                            Log.e("-*-*-*-*-*-*-", "Error getting documents: ", task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("-*-*-*-*-*-*-", "Error getting documents: " + e);
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
    }
}