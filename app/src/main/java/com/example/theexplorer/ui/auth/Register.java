package com.example.theexplorer.ui.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.SoundPool;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.theexplorer.R;
import com.example.theexplorer.ui.auth.LogIn;
import com.example.theexplorer.ui.auth.UserModel;
import com.example.theexplorer.ui.profile.ProfileFragment;
import com.example.theexplorer.ui.profile.ProfileViewModel;
import com.example.theexplorer.ui.profile.ProfilesActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class Register extends AppCompatActivity {

    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    private Button registerBtn , cancelBtn;
    private TextInputLayout passwordd, emailAddress,namee;
    ImageView google;
    private GoogleSignInClient googleSignInClient;
    public String Name,Pass,Email;
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth=FirebaseAuth.getInstance();

        firebaseFirestore=FirebaseFirestore.getInstance();

        checkUser();
        registerBtn= findViewById(R.id.registerBtn);
        passwordd= findViewById(R.id.password);
        emailAddress= findViewById(R.id.email);
        namee = findViewById(R.id.name1);
        cancelBtn=findViewById(R.id.cancelBtn);
        google =findViewById(R.id.googler);
        progressDialog=new ProgressDialog(this);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailAddress.getEditText().getText().toString().trim();
                String namee1 = namee.getEditText().getText().toString().trim();
                String password = passwordd.getEditText().getText().toString();

                if (TextUtils.isEmpty(namee1))
                {
                    namee.setError("This Field should not be empty");
                    return ;
                }

                if (TextUtils.isEmpty(email))
                {
                    emailAddress.setError("This Field should not be empty");
                    return ;
                }


                if (TextUtils.isEmpty(password))
                {
                    passwordd.setError("This Field should not be empty");
                    return ;
                }
                if(password.length()<8 )
                {
                    passwordd.setError("Password field should not be greater than 8 charcter");
                    return ;
                }
                progressDialog.show();
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                startActivity(new Intent(Register.this, LogIn.class));
                                progressDialog.cancel();

                                firebaseFirestore.collection("User")
                                        .document(FirebaseAuth.getInstance().getUid())
                                        .set(new UserModel(email,namee1));
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                progressDialog.cancel();
                            }
                        });
            }

        });

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("GOOGLE_SIGN_IN_TAG", "onClick: begin Google SignIn");
                Intent i = googleSignInClient.getSignInIntent();
                startActivityForResult(i, 100);
            }
        });


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this,LogIn.class));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            Log.d("GOOGLE_SIGN_IN_TAG",  "onActivityResult: Google Signin intent result");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try
            {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            }
            catch (ApiException e)
            {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(this,new OnSuccessListener<AuthResult>(){
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();

                        String uid=user.getUid();
                        String email = user.getEmail();
                        String name =user.getDisplayName();
                        //check if user is new or existing

                        if (authResult.getAdditionalUserInfo().isNewUser()){
                            //user is new - Account Created
                            Log.d("GOOGLE_SIGN_IN_TAG", "onSuccess: Account Created...email");
                            Toast.makeText(  Register.this, "Account Created...in"+email+name, Toast.LENGTH_SHORT).show();

                        }

                        else
                        {

                            Log.d("GOOGLE_SIGN_IN_TAG", "onSuccess: Existing user...\n "+email+name);

                            Toast.makeText(  Register.this, "Existing user... \n"+ email + name, Toast.LENGTH_SHORT).show();
                        }
//                        startActivity(new Intent(Register.this, ProfileViewModel.class));
                        startActivity(new Intent(Register.this, ProfilesActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Register.this, "Sorry auth failed.", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void checkUser() {

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {

            Log.d("GOOGLE_SIGN_IN_TAG", "checkUser: Already logged in");

//            startActivity(new Intent(this, ProfileViewModel.class));
            startActivity(new Intent(Register.this, ProfilesActivity.class));

            finish();
        }
    }
}