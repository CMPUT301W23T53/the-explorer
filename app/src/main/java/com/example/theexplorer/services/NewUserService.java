package com.example.theexplorer.services;

import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NewUserService {

    final FirebaseFirestore database = FirebaseFirestore.getInstance();
    final CollectionReference usersRef = database.collection("user_data");

    final CollectionReference qrCodeRef = database.collection("qrcodes");

    public Task<User> getUser(String userId) {
        final TaskCompletionSource<User> taskCompletionSource = new TaskCompletionSource<>();
        usersRef.document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Map<String, Object> data = documentSnapshot.getData();

                    ArrayList<DocumentReference> qrCodeRefs = (ArrayList<DocumentReference>) data.get("QRList");
                    ArrayList<Task<QRCode>> qrCodeTasks = new ArrayList<>();

                    // Fetch each QR code document
                    for (DocumentReference qrCodeRef : qrCodeRefs) {
                        Task<QRCode> qrCodeTask = qrCodeRef.get().continueWith(new Continuation<DocumentSnapshot, QRCode>() {
                            @Override
                            public QRCode then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    DocumentSnapshot document = task.getResult();
                                    Map<String, Object> qrData = document.getData();
                                    QRCode qrCode = new QRCode();

                                    qrCode.setQRId(document.getId());
                                    qrCode.setQRName(document.getString("QRName"));
                                    qrCode.setQRScore(document.getLong("QRScore").intValue());
                                    qrCode.setLatitude(document.getDouble("latitude"));
                                    qrCode.setLongitude(document.getDouble("longitude"));

                                    String photoBytesString = document.getString("photoBytes");
                                    byte[] photoBytes = Base64.decode(photoBytesString, Base64.DEFAULT);
                                    qrCode.setPhotoBytes(photoBytes);

                                    return qrCode;
                                } else {
                                    throw new Exception("Error fetching QR code: " + task.getException());
                                }
                            }
                        });
                        qrCodeTasks.add(qrCodeTask);
                    }

                    // Wait for all QR code tasks to complete
                    Task<List<QRCode>> allQRCodesTask = Tasks.whenAllSuccess(qrCodeTasks);
                    allQRCodesTask.addOnSuccessListener(new OnSuccessListener<List<QRCode>>() {
                        @Override
                        public void onSuccess(List<QRCode> qrCodes) {
                            User user = new User();
                            user.setUserId(userId);
                            user.setQRList(qrCodes);
                            taskCompletionSource.setResult(user);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("ERROR: ", e.toString());
                            taskCompletionSource.setException(e);
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("ERROR: ", e.toString());
                taskCompletionSource.setException(e);
            }
        });

        return taskCompletionSource.getTask();
    }

    public void putUser(User user) {
        usersRef.document(user.getUserId()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document =  task.getResult();
                List<DocumentReference> qrCodeRefs = new ArrayList<>();
                int qrCodeCount = user.getQRList().size();
                final int[] updatedCount = {0};
                for (QRCode qrCode : user.getQRList()) {
                    putQRCode(qrCodeRefs, qrCode, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            updatedCount[0]++;
                            if (updatedCount[0] == qrCodeCount) {
                                document.getReference().update("QRList", qrCodeRefs);
                            }
                        }
                    });
                }
            } else {
                Log.d("Error getting documents: ", task.getException().toString());
            }
        });
    }

    private void putQRCode(List<DocumentReference> qrCodeRefs, QRCode qrCode, OnSuccessListener<Void> listener) {
        String id = qrCode.getQRId() != null ? qrCode.getQRId() : "NULL";
        qrCodeRef.document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    // Update existing document
                    documentSnapshot.getReference()
                            .set(qrCode.toMap(), SetOptions.merge())
                            .addOnSuccessListener(listener);
                    qrCodeRefs.add(documentSnapshot.getReference());
                } else {
                    // Create new document
                    qrCodeRef.add(qrCode.toMap()).addOnSuccessListener(documentReference -> {
                                // Get the ID of the newly created document
                                qrCodeRefs.add(documentReference);
                                listener.onSuccess(null);
                            })
                            .addOnFailureListener(e -> {
                            });
                }
            } else {
                Log.d("Error getting documents: ", task.getException().toString());
            }
        });
    }
}
