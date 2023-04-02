/**
 * The ScannedFragment class extends AppCompatActivity and represents a fragment of the home page UI
 * It displays a list of QR codes that the user has scanned.
 */

package com.example.theexplorer.ui.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.theexplorer.R;
import com.example.theexplorer.services.Comment;
import com.example.theexplorer.services.NewUserService;
import com.example.theexplorer.services.QRCode;
import com.example.theexplorer.services.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScannedFragment extends AppCompatActivity {
    ListView scannedListView;
    final NewUserService newUserService = new NewUserService();
    final User[] user = {new User()};
    String userEmail1;

    FirebaseFirestore firebaseFirestore;

    /**
     * Called when the activity is starting
     * @param savedInstanceState - the saved instance state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned);

        firebaseFirestore = FirebaseFirestore.getInstance();

        scannedListView = (ListView) findViewById(R.id.listview_scanned);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userEmail1 = firebaseUser.getEmail();

        newUserService.getUser(userEmail1).addOnSuccessListener(new OnSuccessListener<User>() {
            /**
             * Called when the user data has been successfully fetched from Firebase
             * @param fetchUser - the fetched user
             */
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

    /**
     * The custom ArrayAdapter for displaying the list of QR codes
     */
    private class MyListAdapter extends ArrayAdapter<QRCode> {

        List<QRCode> items;

        /**
         * Constructs a new MyListAdapter object with the given list of QR codes
         * @param items - list of QR codes
         */
        public MyListAdapter(List<QRCode> items) {
            super(ScannedFragment.this, 0, items);
            this.items = items;
        }

        /**
         * Returns the view for the list item at the specified position
         * @param position - position of the list item
         * @param convertView - the old view to reuse, if possible
         * @param parent - the parent view
         * @return - the view for the list item at the specified position
         */
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(ScannedFragment.this);
                    builder.setTitle("View QR Code Details");

                    // Set the custom layout for the AlertDialog
                    View dialogView = getLayoutInflater().inflate(R.layout.fragment_edit_list, null);
                    builder.setView(dialogView);

                    // Get the EditText views from the custom layout
                    TextView idTextView = dialogView.findViewById(R.id.text_id);
                    TextView scoreTextView = dialogView.findViewById(R.id.text_score);
                    TextView nameTextView = dialogView.findViewById(R.id.text_name);
                    TextView latitudeTextView = dialogView.findViewById(R.id.text_latitude);
                    TextView longitudeTextView = dialogView.findViewById(R.id.text_longitude);
                    ListView usersListView = dialogView.findViewById(R.id.listview_users);

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
                            Intent intent = new Intent(ScannedFragment.this, DetailPageOfOneQR.class);
                            intent.putExtra("qr_code_key", (Serializable) selectedItem);
                            startActivity(intent);

                        }
                    });

                    // Set the negative button of the AlertDialog
                    builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            List<QRCode> qrCodeList = user[0].getQRList();
                            qrCodeList.remove(selectedItem);
                            newUserService.putUser(user[0]);
                            notifyDataSetChanged();
                        }
                    });

                    // Show the AlertDialog
                    builder.show();

                    long score = (long) qrCodeList1.get("qrscore");
                    ArrayList<String> userIDlist = new ArrayList<>();
                    ArrayList<String> userIDlist1 = new ArrayList<>();

                    newUserService.getAllUsers().addOnSuccessListener(new OnSuccessListener<List<User>>() {
                        @Override
                        public void onSuccess(List<User> users) {

                            for (int i = 0; i < users.size(); i++) {
                                for (int j = 0; j < users.get(i).getQRList().size(); j++) {
                                    Map<String, Object> selectedItem = (Map<String, Object>) users.get(i).getQRList().get(j);

                                    long score1 = (long) selectedItem.get("qrscore");
                                    if (score1 == score) {
                                        userIDlist.add(users.get(i).getUserId());
                                        break;
                                    }
                                }
                            }

                            firebaseFirestore.collection("Users").whereIn("email", userIDlist)
                                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            if (!queryDocumentSnapshots.isEmpty()) {
                                                for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                                                    String userName = (String) queryDocumentSnapshots.getDocuments().get(i).get("userName");
                                                    userIDlist1.add(userName);
                                                }
                                                UsersListAdapter adapter = new UsersListAdapter(userIDlist1);
                                                usersListView.setAdapter(adapter);
                                            }
                                        }
                                    });

                        }
                    });

                }
            });

            return convertView;
        }
    }

    /**
     * UsersListAdapter is an ArrayAdapter implementation that displays a list of Strings
     * for the ScannedFragment class.
     */
    private class UsersListAdapter extends ArrayAdapter<String> {

        List<String> items;

        public UsersListAdapter(List<String> items) {
            super(ScannedFragment.this, 0, items);
            this.items = items;
        }

        /**
         * Returns a View that displays the data at the specified position in the data set.
         * @param position The position of the item within the adapter's data set of the item whose view we want.
         * @param convertView The old view to reuse, if possible.
         * @param parent The parent that this view will eventually be attached to.
         * @return A View corresponding to the data at the specified position.
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, null);
            }

            TextView text1 = (TextView) convertView.findViewById(android.R.id.text1);
            text1.setText(items.get(position));


            return convertView;
        }
    }
}
