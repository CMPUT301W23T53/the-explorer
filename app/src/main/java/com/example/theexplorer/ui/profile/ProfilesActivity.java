package com.example.theexplorer.ui.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.theexplorer.R;
import com.example.theexplorer.services.NewUserService;
import com.example.theexplorer.services.QRCode;
import com.example.theexplorer.services.User;
import com.example.theexplorer.ui.auth.Register;
import com.example.theexplorer.ui.home.DetailPageOfOneQR;
import com.example.theexplorer.ui.home.ScannedFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

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
    ListView scannedListView;
    final NewUserService newUserService = new NewUserService();
    final User[] user = {new User()};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiles);

        scannedListView = (ListView) findViewById(R.id.listview_scanned);
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


                                getQrCodes(email);

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

    private void getQrCodes(String email) {
        newUserService.getUser(email).addOnSuccessListener(new OnSuccessListener<User>() {
            @Override
            public void onSuccess(User fetchUser) {
                user[0] = fetchUser;
                if (user[0] != null) {
                    List<QRCode> arrayQRCode = user[0].getQRList();

                    MyListAdapter adapter = new MyListAdapter(arrayQRCode);
                    scannedListView.setAdapter(adapter);
                }
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

    private class MyListAdapter extends ArrayAdapter<QRCode> {

        List<QRCode> items;

        public MyListAdapter(List<QRCode> items) {
            super(ProfilesActivity.this, 0, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, null);
            }

            Map<String, Object> qrCodeList1 = (Map<String, Object>) items.get(position);
            TextView text1 = (TextView) convertView.findViewById(android.R.id.text1);
            text1.setText((String) qrCodeList1.get("qrname"));

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Get the selected QRCode object
                    Map<String, Object> selectedItem = (Map<String, Object>) items.get(position);
                    // Create a new AlertDialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfilesActivity.this);
                    builder.setTitle("View QR Code Details");

                    // Set the custom layout for the AlertDialog
                    View dialogView = getLayoutInflater().inflate(R.layout.fragment_edit_list_1, null);
                    builder.setView(dialogView);

                    // Get the EditText views from the custom layout
                    TextView idTextView = dialogView.findViewById(R.id.text_id);
                    TextView scoreTextView = dialogView.findViewById(R.id.text_score);
                    TextView nameTextView = dialogView.findViewById(R.id.text_name);
                    TextView latitudeTextView = dialogView.findViewById(R.id.text_latitude);
                    TextView longitudeTextView = dialogView.findViewById(R.id.text_longitude);

                    // Set the EditText values to the selected QRCode object's id and name
                    idTextView.setText("ID: " + (String) qrCodeList1.get("qrid"));
                    scoreTextView.setText("Score: " + (long) qrCodeList1.get("qrscore"));
                    nameTextView.setText("Name: " + (String) qrCodeList1.get("qrname"));


                    if (qrCodeList1.get("latitude") instanceof Double) {
                        latitudeTextView.setText("Latitude: " + (double) qrCodeList1.get("latitude"));
                    } else {
                        latitudeTextView.setText("Latitude: " + (long) qrCodeList1.get("latitude"));
                    }
                    if (qrCodeList1.get("longitude") instanceof Double) {
                        longitudeTextView.setText("Longitude: " + (double) qrCodeList1.get("longitude"));
                    } else {
                        longitudeTextView.setText("Longitude: " + (long) qrCodeList1.get("longitude"));
                    }

                    // Set the positive button of the AlertDialog
                    builder.setPositiveButton("More", new DialogInterface.OnClickListener() {


                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(ProfilesActivity.this, DetailPageOfOneQR.class);
                            intent.putExtra("qr_code_key", (Serializable) selectedItem);
                            startActivity(intent);

                        }
                    });

                    // Show the AlertDialog
                    builder.show();
                }
            });

            return convertView;
        }
    }
}