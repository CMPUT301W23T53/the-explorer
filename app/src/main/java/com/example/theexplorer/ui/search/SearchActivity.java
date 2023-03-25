package com.example.theexplorer.ui.search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.theexplorer.R;
import com.example.theexplorer.ui.auth.LogIn;
import com.example.theexplorer.ui.auth.Register;
import com.example.theexplorer.ui.auth.UserModel;
import com.example.theexplorer.ui.profile.ProfilesActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class SearchActivity extends AppCompatActivity {

    EditText etSearch;
    ImageView ivSearch;
    ImageView ivBack;
    FirebaseFirestore firebaseFirestore;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        etSearch = findViewById(R.id.etSearch);
        etSearch.setSingleLine(true);
        ivSearch = findViewById(R.id.ivSearch);
        ivBack = findViewById(R.id.ivBack);

        firebaseFirestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(SearchActivity.this);
        progressDialog.setMessage("Please wait");
        progressDialog.setCancelable(false);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });

        etSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });
    }

    private void performSearch() {
        String searchData = etSearch.getText().toString();

        if (searchData.equals("")) {
            Toast.makeText(SearchActivity.this, "Please enter username", Toast.LENGTH_SHORT).show();
        } else {
            firebaseFirestore.collection("Users").whereEqualTo("userName", searchData).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().size() != 0) {
                                    if (task.getResult().getDocuments().size() > 0) {
                                        for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                                            Toast.makeText(SearchActivity.this, "User exists", Toast.LENGTH_SHORT).show();
                                            Log.e("--**--**--**--", "onComplete: " + task.getResult().getDocuments().get(i).getId());

                                            Intent ii = new Intent(SearchActivity.this, ProfilesActivity.class);
                                            ii.putExtra("userName1", task.getResult().getDocuments().get(0).getString("userName"));
                                            startActivity(ii);

                                        }
                                    }
                                } else {
                                    progressDialog.cancel();
                                    Toast.makeText(SearchActivity.this, "User not exists", Toast.LENGTH_SHORT).show();
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
    }
}
