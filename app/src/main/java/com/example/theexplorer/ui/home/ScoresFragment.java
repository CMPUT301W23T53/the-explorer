package com.example.theexplorer.ui.home;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.theexplorer.R;
import com.example.theexplorer.services.User;
import com.example.theexplorer.services.UserService;

/**
 * This activity allows users to view their highest and lowest scores, as well as the total number
 * of codes scanned, and the sum of all scores from each code scanned.
 */
public class ScoresFragment extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        UserService userService = new UserService();
        User user = userService.getUser(1);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        TextView high = (TextView)findViewById(R.id.highest_score);
        high.setText(String.valueOf(user.getHighestQRScore()));
        TextView highName = (TextView)findViewById(R.id.high_score_name);
        highName.setText(user.getHighestQRScoreName());

        TextView low = (TextView)findViewById(R.id.lowest_score);
        low.setText(String.valueOf(user.getLowestQRScore()));
        TextView lowName = (TextView)findViewById(R.id.low_score_name);
        lowName.setText(user.getLowestQRScoreName());

        TextView total = (TextView)findViewById(R.id.total_scanned);
        total.setText(String.valueOf(user.getQRList().size()));

        TextView sumValue = (TextView)findViewById(R.id.score_sum);
        sumValue.setText(String.valueOf(user.getSumQRScores()));


    }
}
