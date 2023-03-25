package com.example.theexplorer.ui.profile;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.theexplorer.R;
import com.example.theexplorer.databinding.FragmentProfileBinding;
import com.example.theexplorer.ui.auth.LogIn;
import com.example.theexplorer.ui.auth.Register;
import com.example.theexplorer.ui.auth.UserModel;
import com.example.theexplorer.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth firebaseAuth;
    private TextView txtemail, tvName;
    TextView etUserName;
    private Toolbar toolbar;
    private CircleImageView img;
    private CircleImageView ivEditPhoto;
    FirebaseFirestore firebaseFirestore;
    private final int GALLERY_IMAGE = 33;
    Button btnSave;
    Button btnEdit;
    boolean uploadImage;
    File file;
    Uri selectedUri;
    String userEmail1;
    String email2;
    String name2;
    String photo2;
    String userName2;
    ProgressDialog progressDialog;
    Dialog dialog;
    CircleImageView img1;
    EditText etUserName1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentProfileBinding binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
//        final TextView textView = binding.textProfile;
        Button logoutBtn = root.findViewById(R.id.logout);

        // Set click listener for logout button
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(requireContext(), LogIn.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });


        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Explorer");
        txtemail = view.findViewById(R.id.tvEmail);
        txtemail.setPaintFlags(txtemail.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        etUserName = view.findViewById(R.id.etUserName);
        tvName = view.findViewById(R.id.tvName);
        img = view.findViewById(R.id.img);
        ivEditPhoto = view.findViewById(R.id.ivEditPhoto);
        btnSave = view.findViewById(R.id.btnSave);
        btnEdit = view.findViewById(R.id.btnEdit);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please wait");
        progressDialog.setCancelable(false);

        checkUser();

        ivEditPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndRequestPermission();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();

                if (uploadImage) {
                    uploadImageFirebase();
                } else {
                    String userName1 = etUserName.getText().toString().trim();
                    String name1 = tvName.getText().toString().trim();

                    updateDataFirestore(photo2, userName1, name1, userEmail1);

                }
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_edit_profile);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(false);

                CircleImageView ivClose = dialog.findViewById(R.id.ivClose);
                img1 = dialog.findViewById(R.id.img);
                CircleImageView ivEditPhoto1 = dialog.findViewById(R.id.ivEditPhoto);
                etUserName1 = dialog.findViewById(R.id.etUserName);
                Button btnSave1 = dialog.findViewById(R.id.btnSave);

                Glide.with(getContext()).load(photo2).error(R.drawable.ic_baseline_person_24).into(img1);
                etUserName1.setText(userName2);

                ivEditPhoto1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkAndRequestPermission();
                    }
                });

                btnSave1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if (uploadImage) {
                            dialog.dismiss();
                            progressDialog.show();

                            uploadImageFirebase();
                        } else {
                            String userName1 = etUserName1.getText().toString().trim();

                            if (userName1.equals("")) {
                                Toast.makeText(getContext(), "please enter username", Toast.LENGTH_SHORT).show();
                            } else {
                                dialog.dismiss();
                                progressDialog.show();

                                updateDataFirestore(photo2, userName1, "", userEmail1);
                            }

                        }
                    }
                });

                ivClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });


    }

    private void updateDataFirestore(String photo, String userName, String name, String email) {

        firebaseFirestore.collection("Users").whereNotEqualTo("email", userEmail1).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        ArrayList<String> userNameList = new ArrayList<>();

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String userName2 = document.getString("userName");
                                userNameList.add(userName2);
                            }

                            if (userNameList.contains(userName)) {
                                progressDialog.cancel();
                                Toast.makeText(getContext(), "Username already exists", Toast.LENGTH_SHORT).show();
                            } else {
                                firebaseFirestore.collection("Users")
                                        .document(email)
                                        .set(new UserModel(photo, userName, email))
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.cancel();
                                                Toast.makeText(getContext(), "Data updated", Toast.LENGTH_SHORT).show();

                                                checkUser();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.cancel();
                                                Toast.makeText(getContext(), ".." + e, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }

                        } else {
                            Log.e("-*-*-*-*-*-*-", "Error getting documents: ", task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.cancel();
                        Log.e("-*-*-*-*-*-*-", "onFailure" + e);
                    }
                });

    }

    private void uploadImageFirebase() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("images");

        storageRef.child(file.getName()).putFile(selectedUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Toast.makeText(getContext(), "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl();

                        downloadUri.addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                String generatedFilePath = task.getResult().toString();
                                Log.e("--***--***--", "onSuccess: " + generatedFilePath);

                                String userName1 = etUserName1.getText().toString().trim();
                                String name = tvName.getText().toString().trim();

                                uploadImage = false;

                                if (userName1.equals("")) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), "please enter username", Toast.LENGTH_SHORT).show();
                                } else {
                                    updateDataFirestore(generatedFilePath, userName1, name, userEmail1);
                                }



                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.cancel();
                                Toast.makeText(getContext(), "" + e, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("---***---***---", "onFailure: " + e);
                    }
                });

    }


    private void checkUser() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        userEmail1 = firebaseUser.getEmail();
        showUserData(userEmail1);


    }

    private void showUserData(String email1) {
        firebaseFirestore.collection("Users").whereEqualTo("email", email1).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e("-*-*-*-*-*-*-", document.getId() + " => " + document.getData());

                                email2 = document.getString("email");
                                name2 = document.getString("name");
                                photo2 = document.getString("photo");
                                userName2 = document.getString("userName");

                                txtemail.setText(email2);
                                tvName.setText(name2);
                                etUserName.setText(userName2);

                                Glide.with(getContext()).load(photo2).error(R.drawable.ic_baseline_person_24).into(img);

                                Log.e("-*-*-*-*-*-", "onComplete: ..." + email2);
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_IMAGE && resultCode == RESULT_OK) {

            assert data != null;
            Uri mSelected = data.getData();

            if (mSelected == null)
                return;

            try {
                selectedUri = Utils.getPathGallery(getActivity(), mSelected);
                file = new File(selectedUri.getPath());
                Glide.with(getActivity()).load(selectedUri).placeholder(R.drawable.ic_baseline_person_24).into(img1);

                Log.e("*-*-*-*-*", "onActivityResult: ..1.." + selectedUri);
                Log.e("*-*-*-*-*", "onActivityResult: ..2.." + file);

                uploadImage = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            HashMap<String, Integer> permissionResults = new HashMap<>();
            int deniedCount = 0;

            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    permissionResults.put(permissions[i], grantResults[i]);
                    deniedCount++;
                }
            }
            if (deniedCount == 0) {
                selectImage();
            }
        }
    }

    private void checkAndRequestPermission() {
        String[] appPermission = new String[0];
        if (android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.TIRAMISU) {
            appPermission = new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO};
        } else {
            appPermission = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
        }

        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String perm : appPermission) {
            if (ContextCompat.checkSelfPermission(getActivity(), perm) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(perm);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 101);
            }
        } else {
            selectImage();
        }
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GALLERY_IMAGE);
    }


}

