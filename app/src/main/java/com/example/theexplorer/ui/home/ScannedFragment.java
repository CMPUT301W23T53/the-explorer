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

import java.util.ArrayList;
import java.util.List;

public class ScannedFragment extends AppCompatActivity {
    ListView scannedListView;
    final NewUserService newUserService = new NewUserService();
    final User[] user = {new User()};

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned);

        user[0].setUserId("test_nested"); // JUST FOR TESTING
        newUserService.getUser(user[0].getUserId()).addOnSuccessListener(new OnSuccessListener<User>() {

            @Override
            public void onSuccess(User fetchUser) {
                user[0] = fetchUser;
                Log.d("USER", user[0].toString());

                scannedListView = (ListView) findViewById(R.id.listview_scanned);
                List<QRCode> arrayQRCode = user[0].getQRList();
                Log.d("GETQRLIST", user[0].getQRList().toString());

                MyListAdapter adapter = new MyListAdapter(arrayQRCode);
                scannedListView.setAdapter(adapter);
            }
        });

    }
    private class MyListAdapter extends ArrayAdapter<QRCode> {

        public MyListAdapter(List<QRCode> items) {
            super(ScannedFragment.this, 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, null);
            }

            QRCode item = getItem(position);
            TextView text1 = (TextView) convertView.findViewById(android.R.id.text1);
            text1.setText(item.getQRName());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Get the selected QRCode object
                    QRCode selectedItem = getItem(position);
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

                    // Set the EditText values to the selected QRCode object's id and name
                    idTextView.setText("ID: " + selectedItem.getQRId());
                    scoreTextView.setText("Score: " + String.valueOf(selectedItem.getQRScore()));
                    nameTextView.setText("Name: " + selectedItem.getQRName());
                    latitudeTextView.setText("Latitude: " + String.valueOf(selectedItem.getLatitude()));
                    longitudeTextView.setText("Longitude: " + String.valueOf(selectedItem.getLongitude()));

                    // Set the positive button of the AlertDialog
                    builder.setPositiveButton("More", new DialogInterface.OnClickListener() {


                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(ScannedFragment.this, DetailPageOfOneQR.class);
                            intent.putExtra("qr_code_key", selectedItem);
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
                }
            });

            return convertView;
        }
    }
}
