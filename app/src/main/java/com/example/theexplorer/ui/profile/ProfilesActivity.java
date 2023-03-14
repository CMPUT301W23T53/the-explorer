package com.example.theexplorer.ui.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.theexplorer.R;
import com.example.theexplorer.ui.auth.LogIn;
import com.example.theexplorer.ui.auth.Register;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilesActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private TextView txtemail,txtname;
    private Toolbar toolbar;
    private CircleImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiles);
        Button logoutBtn = findViewById(R.id.logout);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Explorer");
        txtemail = findViewById(R.id.txtemail);
        txtname = findViewById(R.id.txtname);
        img = findViewById(R.id.img);

        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();

        checkUser();
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                Intent intent = new Intent(ProfilesActivity.this, LogIn.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void checkUser() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(ProfilesActivity.this);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            startActivity(new Intent(ProfilesActivity.this, Register.class));
            finish();
        } else {
            if (account != null) {
                txtemail.setText(account.getEmail());
                txtname.setText(account.getDisplayName());
                Glide.with(this).load(account.getPhotoUrl()).into(img);
            } else {
                String email = firebaseUser.getEmail();
                String name = firebaseUser.getDisplayName();
                txtemail.setText(email);
                txtname.setText(name);
            }
        }
    }



//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.logout:
//                firebaseAuth.signOut();
//                startActivity(new Intent(ProfilesActivity.this, LogIn.class));
//                finish();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

}