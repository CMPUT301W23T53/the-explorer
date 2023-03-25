package com.example.theexplorer.ui.auth;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.theexplorer.MainActivity;
import com.example.theexplorer.R;
import com.example.theexplorer.ui.profile.ProfileFragment;
import com.example.theexplorer.ui.profile.ProfilesActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class LogIn extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    TextInputLayout pass, email1;
    Button loginBtn;
    TextView forgotpassword, register;
    ProgressDialog progressDialog;
    public String Pass, Email;
    ImageView google;
    GoogleSignInClient googleSignInClient;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        pass = findViewById(R.id.pass);
        email1 = findViewById(R.id.email);
        forgotpassword = findViewById(R.id.forgotpassword);
        register = findViewById(R.id.register);
        loginBtn = findViewById(R.id.login);
        google = findViewById(R.id.googlelog);

        progressDialog = new ProgressDialog(this);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!EmailValidate() || !PasswordValidate())
                    return;
                else {
                    String email = email1.getEditText().getText().toString().trim();
                    String password = pass.getEditText().getText().toString();
                    if (TextUtils.isEmpty(email)) {
                        email1.setError("This Field should not be empty");
                        return;
                    }

                    if (TextUtils.isEmpty(password)) {
                        pass.setError("This Field should not be empty");
                        return;
                    }

                    progressDialog.show();

                    firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    progressDialog.cancel();
//                                    startActivity(new Intent(LogIn.this, ProfileFragment.class));
                                    startActivity(new Intent(LogIn.this, MainActivity.class));


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.cancel();
                                    Toast.makeText(LogIn.this, e.getMessage(), Toast.LENGTH_LONG).show();

                                }
                            });
                }

            }

        });

        checkUser();
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


        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email1.getEditText().getText().toString().trim();

                if (email != null) {
                    if (email.equals("")) {
                        Toast.makeText(LogIn.this, "Please enter email", Toast.LENGTH_SHORT).show();
                    }else {
                        progressDialog.setTitle("Sending Mail");
                        progressDialog.show();

                        firebaseAuth.sendPasswordResetEmail(email)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        progressDialog.cancel();
                                        Toast.makeText(LogIn.this, "Email Sent", Toast.LENGTH_LONG).show();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.cancel();
                                        Toast.makeText(LogIn.this, e.getMessage(), Toast.LENGTH_LONG).show();

                                    }
                                });
                    }
                }


            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogIn.this, Register.class));

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            Log.d("GOOGLE_SIGN_IN_TAG", "onActivityResult: Google Signin intent result");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential).addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                String uid = user.getUid();
                String email = user.getEmail();
                String name = user.getDisplayName();
                String userPhoto = user.getPhotoUrl().toString();
                //check if user is new or existing

                Log.e("..00..00..00..00..", "onSuccess: "+uid );
                Log.e("..00..00..00..00..", "onSuccess: "+user.getPhotoUrl().toString() );

                if (authResult.getAdditionalUserInfo().isNewUser()) {
                    //user is new - Account Created
                    Log.d("GOOGLE_SIGN_IN_TAG", "onSuccess: Account Created...email");
                    Toast.makeText(LogIn.this, "Account Created...in" + email + name, Toast.LENGTH_SHORT).show();


//                    String[] userNameFromEmail = email.split("@");
//                    String userName = userNameFromEmail[0];

                    firebaseFirestore.collection("Users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            Log.e("*-*-*-*", "onSuccess: " + queryDocumentSnapshots);

                            ArrayList<String> docIDList = new ArrayList<>();

                            if (queryDocumentSnapshots.getDocuments() != null) {
                                if (queryDocumentSnapshots.getDocuments().size() > 0) {
                                    for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                                        String docId = queryDocumentSnapshots.getDocuments().get(i).getId();
                                        docIDList.add(docId);
                                    }

                                    String[] userNameFromEmail = email.split("@");
                                    String userName = userNameFromEmail[0];

                                    if (docIDList.contains(email)) {
                                        progressDialog.cancel();
                                        Toast.makeText(LogIn.this, "Username already exists", Toast.LENGTH_SHORT).show();
                                    } else {
                                        progressDialog.cancel();

                                        firebaseFirestore.collection("Users")
                                                .document(email)
                                                .set(new UserModel(userPhoto, userName, email))
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        startActivity(new Intent(LogIn.this, MainActivity.class));
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(LogIn.this, ".." + e, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }

                                } else {
                                    progressDialog.cancel();

                                    String[] userNameFromEmail = email.split("@");
                                    String userName = userNameFromEmail[0];

                                    firebaseFirestore.collection("Users")
                                            .document(email)
                                            .set(new UserModel(userPhoto, userName, email))
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    startActivity(new Intent(LogIn.this, MainActivity.class));
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(LogIn.this, ".." + e, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("*-*-*-*", "onFailure: " + e);
                        }
                    });


                } else {

                    Log.d("GOOGLE_SIGN_IN_TAG", "onSuccess: Existing user...\n " + email + name);

                    startActivity(new Intent(LogIn.this, MainActivity.class));
                    finish();

                    Toast.makeText(LogIn.this, "Existing user... \n" + email + name, Toast.LENGTH_SHORT).show();
                }
//                        startActivity(new Intent(Register.this, ProfileViewModel.class));
//                startActivity(new Intent(Register.this, ProfilesActivity.class));
//                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LogIn.this, "Sorry auth failed.", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void checkUser() {

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {

            Log.d("GOOGLE_SIGN_IN_TAG", "checkUser: Already logged in");

//            startActivity(new Intent(this, ProfileFragment.class));
            startActivity(new Intent(LogIn.this, MainActivity.class));

            finish();
        }
    }

    boolean PasswordValidate() {
        Pass = pass.getEditText().toString().trim();
        if (Pass.isEmpty()) {
            pass.setError("This Field should not be empty");
            return false;
        } else if (Pass.length() < 8) {
            pass.setError("Password field should be greater than 8 characters");
            return false;
        } else {
            return true;
        }
    }//end PasswordValidate

    boolean EmailValidate() {

        Email = email1.getEditText().toString().trim();
        if (Email.isEmpty()) {
            email1.setError("This Field should not be empty");
            return false;
        } else {
            return true;
        }
    }//end EmailValidate
}