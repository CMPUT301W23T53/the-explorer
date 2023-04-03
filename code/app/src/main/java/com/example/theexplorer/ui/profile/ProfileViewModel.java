package com.example.theexplorer.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.bumptech.glide.Glide;
import com.example.theexplorer.R;
import com.example.theexplorer.ui.auth.LogIn;
import com.example.theexplorer.ui.auth.Register;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import de.hdodenhof.circleimageview.CircleImageView;

/**

 ProfileViewModel is an activity that displays the profile information of the currently
 logged-in user. It shows the user's email, name, and profile image. Users can also
 log out from their account using the options menu.
 */
public class ProfileViewModel extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    TextView txtemail,txtname;
    Toolbar toolbar;
    CircleImageView img;

    private final MutableLiveData<String> mText;

    /**
     Constructor for ProfileViewModel, initializes the LiveData object.
     */
    public ProfileViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is the Profile fragment");
    }

    /**
     Returns the LiveData object containing the text.
     @return LiveData<String> the LiveData object containing the text
     */
    public LiveData<String> getText() {
        return mText;
    }

    /**
     Initializes the activity, setting up the user interface and displaying the user's profile.
     @param savedInstanceState a Bundle containing the activity's previously frozen state, if there was one
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        toolbar =(Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Explorer");
        setSupportActionBar(toolbar);

        txtemail= findViewById(R.id.tvEmail);
        txtname = findViewById(R.id.etUserName);
        img=findViewById(R.id.img);


        firebaseAuth = FirebaseAuth.getInstance();

        checkUser();
    }

    /**
     Checks the user's authentication state and retrieves their profile information.
     */
    private void checkUser()
    {
        GoogleSignInAccount account= GoogleSignIn.getLastSignedInAccount(this);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {

            startActivity(new Intent(this, Register.class));
            finish();
        }
        else {

            if (account!= null) {
                txtemail.setText(account.getEmail());
                txtname.setText(account.getDisplayName());
                Glide.with(this).load(account.getPhotoUrl()).into(img);
            }
            else
            {
                String email = firebaseUser.getEmail();
                String name = firebaseUser.getDisplayName();
                txtemail.setText(email);
                txtname.setText(name);
            }

        }

    }

    /**
     Inflate the options menu for the activity.
     @param menu the options menu in which you place your items
     @return boolean true if the menu should be displayed; false otherwise
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     Handles the options item selected event.
     @param item the selected menu item
     @return boolean true if the event was handled, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                firebaseAuth.signOut();
                startActivity(new Intent(ProfileViewModel.this, LogIn.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
