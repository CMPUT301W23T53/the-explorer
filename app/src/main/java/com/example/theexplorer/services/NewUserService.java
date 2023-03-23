package com.example.theexplorer.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class NewUserService {

    final FirebaseFirestore database = FirebaseFirestore.getInstance();
    final CollectionReference usersRef = database.collection("user_data");

    final CollectionReference qrCodeRef = database.collection("qrcodes");

    public Task<User> getUser(int userId) {
        final TaskCompletionSource<User> taskCompletionSource = new TaskCompletionSource<>();
        String userIdString = "" + String.valueOf(userId);

        usersRef.document("test").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Map<String, Object> data = documentSnapshot.getData();
                    int userId = ((Long) data.get("userId")).intValue();
                    List<Long> fetchedQRList = (List<Long>) documentSnapshot.get("QRList");
                    List<Task<QRCode>> qrCodeTasks = new ArrayList<>(); // create a list to hold the QR code tasks
                    for (Long value : fetchedQRList) {
                        qrCodeTasks.add(getQRCode(value.intValue())); // add the QR code tasks to the list
                    }

                    Tasks.whenAllSuccess(qrCodeTasks).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                        @Override
                        public void onSuccess(List<Object> qrCodes) {
                            List<QRCode> QRList = new ArrayList<>();
                            for (Object qrCodeObj : qrCodes) {
                                QRCode qrCode = (QRCode) qrCodeObj;
                                Log.d("FETCHED QRCODE: ", qrCode.toString());
                                QRList.add(qrCode);
                            }

                            User user = new User();
                            user.setUserId(userId);
                            user.setQRList(QRList);

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
    private Task<QRCode> getQRCode(int QRId) {
        final TaskCompletionSource<QRCode> taskCompletionSource = new TaskCompletionSource<>();
        qrCodeRef.whereEqualTo("QRId", QRId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                if (!querySnapshot.isEmpty()) {
                    DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                    Map<String, Object> data = documentSnapshot.getData();
                    int qrCodeId = ((Long) data.get("QRId")).intValue();
                    String QRName = (String) data.get("QRName");
                    int QRScore = ((Long) data.get("QRScore")).intValue();
                    double latitude = (Double) data.get("latitude");
                    double longitude =(Double) data.get("longitude");

                    QRCode qrCode = new QRCode();
                    qrCode.setQRId(qrCodeId);
                    qrCode.setQRName(QRName);
                    qrCode.setQRScore(QRScore);
                    qrCode.setLatitude(latitude);
                    qrCode.setLongitude(longitude);

                    taskCompletionSource.setResult(qrCode);
                } else {
                    taskCompletionSource.setException(new Exception("QRCode with id " + QRId + " not found"));
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
}
