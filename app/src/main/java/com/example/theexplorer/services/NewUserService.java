package com.example.theexplorer.services;

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
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class NewUserService {

    final FirebaseFirestore database = FirebaseFirestore.getInstance();
    final CollectionReference usersRef = database.collection("user_data");

    final CollectionReference qrCodeRef = database.collection("qrcodes");

    public Task<User> getUser(int userId) {
        final TaskCompletionSource<User> taskCompletionSource = new TaskCompletionSource<>();
        String userIdString = "" + String.valueOf(userId);

        usersRef.document("test_nested").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Map<String, Object> data = documentSnapshot.getData();
                    int userId = ((Long) data.get("userId")).intValue();
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

                                    qrCode.setQRId(document.getLong("QRId").intValue());
                                    qrCode.setQRName(document.getString("QRName"));
                                    qrCode.setQRScore(document.getLong("QRScore").intValue());
                                    qrCode.setLatitude(document.getDouble("latitude"));
                                    qrCode.setLongitude(document.getDouble("longitude"));

                                    String photoBytesString = document.getString("photoBytes");
                                    byte[] photoBytes = Base64.getDecoder().decode(photoBytesString);
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
        Query query = usersRef.whereEqualTo("userId", user.getUserId());
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
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
                }
            } else {
                Log.d("Error getting documents: ", task.getException().toString());
            }
        });
    }

    private void putQRCode(List<DocumentReference> qrCodeRefs, QRCode qrCode, OnSuccessListener<Void> listener) {
        Query query = qrCodeRef.whereEqualTo("QRId", qrCode.getQRId());
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    // Update existing document
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        document.getReference().set(qrCode.toMap(), SetOptions.merge())
                                .addOnSuccessListener(listener);
                        qrCodeRefs.add(document.getReference());
                    }
                } else {
                    // Create new document
                    qrCodeRef.add(qrCode.toMap()).addOnSuccessListener(documentReference -> {
                                // Get the ID of the newly created document
                                String documentId = documentReference.getId();
                                qrCodeRefs.add(documentReference);
                                Log.d("Document created with ID: ", documentId.toString());
                                listener.onSuccess(null);
                            })
                            .addOnFailureListener(e -> {
                                // Log an error message
                                Log.w("Error creating document", e);
                            });
                }
            } else {
                Log.d("Error getting documents: ", task.getException().toString());
            }
        });
    }
}
