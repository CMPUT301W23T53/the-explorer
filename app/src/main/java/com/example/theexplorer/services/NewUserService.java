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
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;


import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NewUserService {

    final FirebaseFirestore database = FirebaseFirestore.getInstance();
    final CollectionReference usersRef = database.collection("user_data");

    final CollectionReference qrCodeRef = database.collection("qrcodes");

    final CollectionReference commentRef = database.collection("comments");

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
                                    return mapQRCodeFromFirebase(document);
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

    public Task<List<User>> getUsersWithMatchingQRCodeScore(QRCode qrCode) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        TaskCompletionSource<List<User>> taskCompletionSource = new TaskCompletionSource<>();

        // Query all QRCode documents with the specified attribute
        qrCodeRef.whereEqualTo("QRScore", qrCode.getQRScore())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentReference> matchingQRCodeRefs = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            matchingQRCodeRefs.add(document.getReference());
                        }
                        // Query all User documents that have a QRList containing references to the matching QRCode documents
                        usersRef.whereArrayContainsAny("QRList", matchingQRCodeRefs)
                                .get()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        List<User> matchingUsers = new ArrayList<>();
                                        for (QueryDocumentSnapshot document : task1.getResult()) {
                                            User user = new User();
                                            user.setUserId(document.getId());
                                            matchingUsers.add(user);
                                        }
                                        taskCompletionSource.setResult(matchingUsers);
                                    } else {
                                        taskCompletionSource.setException(task1.getException());
                                    }
                                });
                    } else {
                        taskCompletionSource.setException(task.getException());
                    }
                });

        return taskCompletionSource.getTask();
    }

    public Task<List<QRCode>> getNearbyQRCodes(double currentLat, double currentLong, double radius) {
        double latMin = currentLat - (radius / 111.12);
        double longMin = currentLong - (radius / 111.12 * Math.cos(currentLat));
        GeoPoint southWestPoint = new GeoPoint(latMin, longMin);

        double latMax = currentLat + (radius / 111.12);
        double longMax = currentLong + (radius / 111.12 * Math.cos(currentLat));
        GeoPoint northEastPoint = new GeoPoint(latMax, longMax);

        Query query = qrCodeRef.whereGreaterThan("location", southWestPoint).whereLessThan("location", northEastPoint);
        return query.get().continueWith(task -> {
            List<QRCode> qrCodes = new ArrayList<>();
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                for (DocumentSnapshot documentSnapshot: querySnapshot.getDocuments()) {
                    QRCode qrCode = mapQRCodeFromFirebase(documentSnapshot);
                    qrCodes.add(qrCode);
                }
            } else {
                Log.e("Error getting nearby QR codes", task.getException().toString());
            }
            return qrCodes;
        });
    }

    public Task<List<Comment>> getCommentsOfQRCode(QRCode qrCode) {
        String QRId = qrCode.getQRId();

        Query query = commentRef.whereEqualTo("QRId", QRId);
        return query.get().continueWith(task -> {
            List<Comment> comments = new ArrayList<>();
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                for (DocumentSnapshot document: querySnapshot.getDocuments()) {
                    Comment comment = mapCommentFromFirebase(document);
                    comments.add(comment);
                }
            } else {
                Log.e("Error getting all comments", task.getException().toString());
            }
            return comments;
        });
    }

    public void putComment(Comment comment) {
        String commentId = comment.getCommentId();
        commentRef.document(commentId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    // Update existing document
                    documentSnapshot.getReference().set(comment.toMap(), SetOptions.merge());
                } else {
                    // Create new document
                    commentRef.add(comment.toMap());
                }
            } else {
                Log.d("Error getting documents: ", task.getException().toString());
            }
        });
    }

    public Task<Integer> getRankOfUser(User user) {
        Query query = qrCodeRef.orderBy("QRScore", Query.Direction.DESCENDING);
        return query.get().continueWith(task -> {
            List<Integer> allQRScoresSorted = new ArrayList<>();
            int minUntilNow = Integer.MAX_VALUE;
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                for (DocumentSnapshot document: querySnapshot.getDocuments()) {
                    int currentScore = document.getLong("QRScore").intValue();
                    if (currentScore < minUntilNow)  {
                        allQRScoresSorted.add(currentScore);
                        minUntilNow = currentScore;
                    }
                }
            } else {
                Log.e("Error getting rank of user", task.getException().toString());
            }
            return allQRScoresSorted.indexOf(user.getHighestQRScore()) + 1;
        });
    }


    private QRCode mapQRCodeFromFirebase(DocumentSnapshot document) {
        QRCode qrCode = new QRCode();

        qrCode.setQRId(document.getId());
        qrCode.setQRName(document.getString("QRName"));
        qrCode.setQRScore(document.getLong("QRScore").intValue());

        GeoPoint location = document.getGeoPoint("location");
        qrCode.setLatitude(location.getLatitude());
        qrCode.setLongitude(location.getLongitude());

        String photoBytesString = document.getString("photoBytes");
        byte[] photoBytes = Base64.decode(photoBytesString, Base64.DEFAULT);
        qrCode.setPhotoBytes(photoBytes);

        return qrCode;
    }

    private Comment mapCommentFromFirebase(DocumentSnapshot document) {
        Comment comment = new Comment();

        comment.setCommentId(document.getId());
        comment.setUserId(document.getString("userId"));
        comment.setContent(document.getString("content"));
        comment.setQRId(document.getString("QRId"));
        comment.setCreatedAt(document.getTimestamp("createdAt").toDate());

        return comment;
    }
}
