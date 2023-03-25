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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.theexplorer.MainActivity;
import com.example.theexplorer.R;
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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Register extends AppCompatActivity {

    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    private Button registerBtn, cancelBtn;
    private TextInputLayout passwordd, emailAddress, etUserName;
    ImageView google;
    private GoogleSignInClient googleSignInClient;
    public String Name, Pass, Email;
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseFirestore = FirebaseFirestore.getInstance();


//        firebaseFirestore.collection("User").document().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                if (documentSnapshot.exists()) {
//                    Log.e("-*-*-*-*-*-*-", "onSuccess: "+documentSnapshot );
//                }
//            }
//        }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                Log.e("-*-*-*-*-*", "onFailure: "+documentSnapshot );
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.e("-*-*-*-*-*", "onFailure: "+e );
//            }
//        });


        checkUser();
        registerBtn = findViewById(R.id.registerBtn);
        passwordd = findViewById(R.id.password);
        emailAddress = findViewById(R.id.email);
        etUserName = findViewById(R.id.name1);
        cancelBtn = findViewById(R.id.cancelBtn);
        google = findViewById(R.id.googler);
        progressDialog = new ProgressDialog(this);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailAddress.getEditText().getText().toString().trim();
                String userName = etUserName.getEditText().getText().toString().trim();
                String password = passwordd.getEditText().getText().toString();

                if (TextUtils.isEmpty(userName)) {
                    etUserName.setError("This Field should not be empty");
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    emailAddress.setError("This Field should not be empty");
                    return;
                }


                if (TextUtils.isEmpty(password)) {
                    passwordd.setError("This Field should not be empty");
                    return;
                }
                if (password.length() < 8) {
                    passwordd.setError("Password field should be greater than 8 characters");
                    return;
                }
                progressDialog.show();

                firebaseFirestore.collection("Users").whereEqualTo("userName", userName).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().size() == 0) {
                                        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                            @Override
                                            public void onSuccess(AuthResult authResult) {

                                                progressDialog.cancel();
                                                firebaseFirestore.collection("Users")
                                                        .document(email)
                                                        .set(new UserModel("", userName, email))
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                startActivity(new Intent(Register.this, LogIn.class));
                                                                finish();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(Register.this, ".." + e, Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                                progressDialog.cancel();
                                            }
                                        });
                                    } else {
                                        progressDialog.cancel();
                                        Toast.makeText(Register.this, "Username already exists", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    progressDialog.cancel();
                                    Log.e("-*-*-*-*-*-*-", "Error getting documents: ", task.getException());
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("-*-*-*-*-*-*-", "Error getting documents: " + e);
                                progressDialog.cancel();
                            }
                        });

            }

        });

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();

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
                startActivity(new Intent(Register.this, LogIn.class));
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
                    Toast.makeText(Register.this, "Account Created...in" + email + name, Toast.LENGTH_SHORT).show();


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
                                        Toast.makeText(Register.this, "Username already exists", Toast.LENGTH_SHORT).show();
                                    } else {
                                        progressDialog.cancel();

                                        firebaseFirestore.collection("Users")
                                                .document(email)
                                                .set(new UserModel(userPhoto, userName, email))
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        startActivity(new Intent(Register.this, LogIn.class));
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(Register.this, ".." + e, Toast.LENGTH_SHORT).show();
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
                                                    startActivity(new Intent(Register.this, LogIn.class));
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(Register.this, ".." + e, Toast.LENGTH_SHORT).show();
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

                    startActivity(new Intent(Register.this, LogIn.class));
                    finish();

                    Toast.makeText(Register.this, "Existing user... \n" + email + name, Toast.LENGTH_SHORT).show();
                }
//                        startActivity(new Intent(Register.this, ProfileViewModel.class));
//                startActivity(new Intent(Register.this, ProfilesActivity.class));
//                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
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
//            startActivity(new Intent(Register.this, ProfilesActivity.class));
            startActivity(new Intent(Register.this, MainActivity.class));

            finish();
        }
    }
}