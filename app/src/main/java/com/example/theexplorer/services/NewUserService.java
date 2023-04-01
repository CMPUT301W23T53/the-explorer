/**

 The NewUserService class provides methods to interact with Firestore database to get and put user data.
 The class also interacts with 'qrcodes2' and 'comments2' collections in Firestore.
 */
package com.example.theexplorer.services;

import android.util.Base64;
import android.util.Log;
import androidx.annotation.NonNull;
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
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NewUserService {

    final FirebaseFirestore database = FirebaseFirestore.getInstance();
    final CollectionReference usersRef = database.collection("user_data2");

    final CollectionReference qrCodeRef = database.collection("qrcodes2");

    final CollectionReference commentRef = database.collection("comments2");

    /**
     * Returns a Task that retrieves all user data from the Firestore database and returns a list of Users
     *
     * @return a Task that retrieves all user data from the Firestore database and returns a list of Users
     */
    public Task<List<User>> getAllUsers() {
        final TaskCompletionSource<List<User>> taskCompletionSource = new TaskCompletionSource<>();

        usersRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                List<Task<User>> userTasks = new ArrayList<>();

                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    String userId = document.getId();
                    Task<User> userTask = getUser(userId).continueWith(task1 -> {
                        User[] users = new User[1];
                        if (task1.isSuccessful()) {
                            users[0] = task1.getResult();
                        } else {
                            taskCompletionSource.setException(task1.getException());
                        }
                        return users[0];
                    });
                    userTasks.add(userTask);
                }

                Task<List<User>> allUserTasks = Tasks.whenAllSuccess(userTasks);
                allUserTasks.addOnSuccessListener(new OnSuccessListener<List<User>>() {
                    @Override
                    public void onSuccess(List<User> users) {
                        taskCompletionSource.setResult(users);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Error getting all users", e.toString());
                        taskCompletionSource.setException(e);
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Error getting all users", e.toString());
                taskCompletionSource.setException(e);
            }
        });

        return taskCompletionSource.getTask();
    }

    /**
     * Returns a Task that retrieves user data from the Firestore database for a given userId and returns a User
     *
     * @param userId a String representing the userId to retrieve data for
     * @return a Task that retrieves user data from the Firestore database for a given userId and returns a User
     */
    public Task<User> getUser(String userId) {
        User user = new User();

        final TaskCompletionSource<User> taskCompletionSource = new TaskCompletionSource<>();
        usersRef.document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Map<String, Object> data = documentSnapshot.getData();

                    ArrayList<QRCode> qrCodeList = (ArrayList<QRCode>) data.get("qrlist");
                    String userId = (String) data.get("userId");

                    user.setUserId(userId);
                    user.setQRList(qrCodeList);

                    taskCompletionSource.setResult(user);
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

    /**
     * Adds or updates a user in Firestore database.
     *
     * @param user the user to be added or updated.
     * @throws NullPointerException if user is null.
     */
    public void putUser(User user) {
        usersRef.document(user.getUserId()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();

                document.getReference().set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        List<DocumentReference> qrCodeRefs = new ArrayList<>();
                        for (int i = 0; i < user.getQRList().size(); i++) {
                            if (user.getQRList().get(i) instanceof QRCode) {

                                String qrID = user.getQRList().get(i).getQRId();
                                QRCode qrCode = user.getQRList().get(i);

                                qrCodeRef.document(qrID).get().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot documentSnapshot = task.getResult();
                                        if (documentSnapshot.exists()) {
                                            // Update existing document
                                            documentSnapshot.getReference().set(qrCode.toMap(), SetOptions.merge());
                                            qrCodeRefs.add(documentSnapshot.getReference());
                                        } else {
                                            // Create new document
                                            qrCodeRef.add(qrCode.toMap()).addOnSuccessListener(documentReference -> {
                                                // Get the ID of the newly created document
                                                qrCodeRefs.add(documentReference);
                                            }).addOnFailureListener(e -> {
                                            });
                                        }
                                    } else {
                                        Log.d("Error getting documents: ", task.getException().toString());
                                    }
                                });
                            } else {
                                Map<String, Object> qrCodeList1 = (Map<String, Object>) user.getQRList().get(i);

                                String qrID = (String) qrCodeList1.get("qrid");

                                qrCodeRef.document(qrID).get().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot documentSnapshot = task.getResult();
                                        if (documentSnapshot.exists()) {
                                            // Update existing document
                                            documentSnapshot.getReference().set(qrCodeList1, SetOptions.merge());
                                            qrCodeRefs.add(documentSnapshot.getReference());
                                        } else {
                                            // Create new document
                                            qrCodeRef.add(qrCodeList1).addOnSuccessListener(documentReference -> {
                                                // Get the ID of the newly created document
                                                qrCodeRefs.add(documentReference);

                                            }).addOnFailureListener(e -> {
                                            });
                                        }
                                    } else {
                                        Log.d("Error getting documents: ", task.getException().toString());
                                    }
                                });
                            }
                        }

                        Log.e("-***-***-***-", "onSuccess: ");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("-***-***-***-", "onFailure: " + e);
                    }
                });
            } else {
                Log.d("Error getting documents: ", task.getException().toString());
            }
        });
    }

    /**
     * Updates or creates a QR code in the Firestore database.
     *
     * @param qrCodeRefs List of DocumentReference objects representing the updated or newly created QR codes.
     * @param qrCode QRCode object to update or create.
     * @param listener OnSuccessListener to be called when the operation succeeds.
     */
    private void putQRCode(List<DocumentReference> qrCodeRefs, QRCode qrCode, OnSuccessListener<Void> listener) {
        String id = qrCode.getQRId() != null ? qrCode.getQRId() : "NULL";
        qrCodeRef.document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    // Update existing document
                    documentSnapshot.getReference().set(qrCode.toMap(), SetOptions.merge()).addOnSuccessListener(listener);
                    qrCodeRefs.add(documentSnapshot.getReference());
                } else {
                    // Create new document
                    qrCodeRef.add(qrCode.toMap()).addOnSuccessListener(documentReference -> {
                        // Get the ID of the newly created document
                        qrCodeRefs.add(documentReference);
                        listener.onSuccess(null);
                    }).addOnFailureListener(e -> {
                    });
                }
            } else {
                Log.d("Error getting documents: ", task.getException().toString());
            }
        });
    }

    /**
     * Gets a list of all users that have at least one QRCode in their QRList with the specified QRScore.
     *
     * @param qrScore The QRScore to match against.
     * @return A task that resolves with a list of matching User objects.
     */
    public Task<List<User>> getUsersWithMatchingQRCodeScore(int qrScore) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        TaskCompletionSource<List<User>> taskCompletionSource = new TaskCompletionSource<>();

        // Query all QRCode documents with the specified attribute
        qrCodeRef.whereEqualTo("QRScore", qrScore).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<DocumentReference> matchingQRCodeRefs = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    matchingQRCodeRefs.add(document.getReference());
                }
                // Query all User documents that have a QRList containing references to the matching QRCode documents
                usersRef.whereArrayContainsAny("QRList", matchingQRCodeRefs).get().addOnCompleteListener(task1 -> {
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

    /**
     * Retrieves a list of QR codes located within a specified radius of a given latitude and longitude.
     *
     * @param currentLat the latitude of the current location
     * @param currentLong the longitude of the current location
     * @param radius the search radius in kilometers
     * @return a {@code Task} that resolves to a list of {@code QRCode} objects that are within the specified radius of the given location
     */
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
                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                    QRCode qrCode = mapQRCodeFromFirebase(documentSnapshot);
                    qrCodes.add(qrCode);
                }
            } else {
                Log.e("Error getting nearby QR codes", task.getException().toString());
            }
            return qrCodes;
        });
    }

    /**
     * Retrieves a list of comments associated with the QR code identified by the specified QR ID.
     *
     * @param QRId the ID of the QR code to retrieve comments for
     * @return a Task that will complete with a List of Comment objects associated with the specified QR code ID
     */
    public Task<List<Comment>> getCommentsOfQRCode(String QRId) {
        Query query = commentRef.whereEqualTo("QRId", QRId);
        return query.get().continueWith(task -> {
            List<Comment> comments = new ArrayList<>();
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    Comment comment = mapCommentFromFirebase(document);
                    comments.add(comment);
                }
            } else {
                Log.e("Error getting all comments", task.getException().toString());
            }
            return comments;
        });
    }

    /**
     * Adds or updates a comment in the Firestore database.
     *
     * @param comment The Comment object to be added or updated.
     */
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

    /**
     * Gets the rank of the specified user based on their QR scores compared to other users.
     *
     * @param user the user to get the rank for
     * @return a Task that asynchronously returns the rank as an integer
     */
    public Task<Integer> getRankOfUser(User user) {
        Query query = qrCodeRef.orderBy("QRScore", Query.Direction.DESCENDING);
        return query.get().continueWith(task -> {
            List<Integer> allQRScoresSorted = new ArrayList<>();
            int minUntilNow = Integer.MAX_VALUE;
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    int currentScore = document.getLong("QRScore").intValue();
                    if (currentScore < minUntilNow) {
                        allQRScoresSorted.add(currentScore);
                        minUntilNow = currentScore;
                    }
                }
            } else {
                Log.e("Error getting rank of user", task.getException().toString());
            }
            return allQRScoresSorted.indexOf(getHighestQRScore(user.getQRList())) + 1;
        });
    }

    /**
     * Calculates the highest QR score from a given list of QR codes.
     *
     * @param arrayQRCode a list of QR codes.
     * @return the highest QR score in the list.
     */

    public long getHighestQRScore(List<QRCode> arrayQRCode) {
        long maxScore = 0;

        for (int i = 0; i < arrayQRCode.size(); i++) {
            Map<String, Object> qrCode = (Map<String, Object>) arrayQRCode.get(i);

            long score = (long) qrCode.get("qrscore");
            if (score >= maxScore) {
                maxScore = score;
            }
        }
        return maxScore;
    }

    /**
     * Returns a task that retrieves the game-wide high score of all players. This task retrieves all
     * users from the usersRef and then retrieves the highest score of all users. The result of this
     * task is a list of all users sorted by their highest score in descending order.
     *
     * @return a task that retrieves the game-wide high score of all players
     */
    public Task<List<User>> getGameWideHighScoreOfAllPlayers() {
        final TaskCompletionSource<List<User>> taskCompletionSource = new TaskCompletionSource<>();

        usersRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                List<Task<User>> userTasks = new ArrayList<>();
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    String userId = document.getId();
                    Task<User> userTask = getUser(userId).continueWith(task1 -> {
                        User[] users = new User[1];
                        if (task1.isSuccessful()) {
                            users[0] = task1.getResult();
                        } else {
                            taskCompletionSource.setException(task1.getException());
                        }
                        return users[0];
                    });
                    userTasks.add(userTask);
                }

                Task<List<User>> allUserTasks = Tasks.whenAllSuccess(userTasks);
                allUserTasks.addOnSuccessListener(new OnSuccessListener<List<User>>() {
                    @Override
                    public void onSuccess(List<User> users) {
                        taskCompletionSource.setResult(users);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Error getting all users", e.toString());
                        taskCompletionSource.setException(e);
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Error getting all users", e.toString());
                taskCompletionSource.setException(e);
            }
        });

        return taskCompletionSource.getTask();
    }

    /**
     * Maps a Firestore DocumentSnapshot to a QRCode object.
     *
     * @param document the DocumentSnapshot to map
     * @return the mapped QRCode object
     */
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

        double[] doubleArray = toDoubleArray(photoBytes);

        qrCode.setPhotoBytes(convertDoubleToList(doubleArray));

        return qrCode;
    }

    /**
     * Maps a {@link DocumentSnapshot} object from Firebase to a {@link Comment} object.
     *
     * @param document the {@link DocumentSnapshot} object to map to a {@link Comment} object.
     * @return a {@link Comment} object mapped from the {@link DocumentSnapshot} object.
     */
    private Comment mapCommentFromFirebase(DocumentSnapshot document) {
        Comment comment = new Comment();

        comment.setCommentId(document.getId());
        comment.setUserId(document.getString("userId"));
        comment.setContent(document.getString("content"));
        comment.setQRId(document.getString("QRId"));
        comment.setCreatedAt(document.getTimestamp("createdAt").toDate());

        return comment;
    }


    public static double[] toDoubleArray(byte[] byteArray) {
        int times = Double.SIZE / Byte.SIZE;
        double[] doubles = new double[byteArray.length / times];
        for (int i = 0; i < doubles.length; i++) {
            doubles[i] = ByteBuffer.wrap(byteArray, i * times, times).getDouble();
        }
        return doubles;
    }

    private static ArrayList<Double> convertDoubleToList(double[] bytes) {
        final ArrayList<Double> list = new ArrayList<>();
        for (double b : bytes) {
            list.add(b);
        }
        return list;
    }
}
