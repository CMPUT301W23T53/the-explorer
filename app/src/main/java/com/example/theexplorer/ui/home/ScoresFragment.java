package com.example.theexplorer.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.theexplorer.R;
import com.example.theexplorer.services.NewUserService;
import com.example.theexplorer.services.User;
import com.example.theexplorer.services.UserService;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * This activity allows users to view their highest and lowest scores, as well as the total number
 * of codes scanned, and the sum of all scores from each code scanned.
 */
public class ScoresFragment extends AppCompatActivity {

    final NewUserService newUserService = new NewUserService();
    final User[] user = {new User()};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        user[0].setUserId("test_nested"); // JUST FOR TESTING
        newUserService.getUser(user[0].getUserId()).addOnSuccessListener(new OnSuccessListener<User>() {

            @Override
            public void onSuccess(User fetchUser) {
                user[0] = fetchUser;

                TextView highestScoreTextView = findViewById(R.id.highest_score);
                TextView highName = findViewById(R.id.high_score_name);
                TextView low = findViewById(R.id.lowest_score);
                TextView lowName = findViewById(R.id.low_score_name);
                TextView total = findViewById(R.id.total_scanned);
                TextView sumValue = findViewById(R.id.score_sum);
                TextView noContent = findViewById(R.id.no_content);

                if (user[0].getQRList().size() == 0) {
                    noContent.setVisibility(View.VISIBLE);
                }
                else {
                    highestScoreTextView.setVisibility(View.VISIBLE);
                    highName.setVisibility(View.VISIBLE);
                    low.setVisibility(View.VISIBLE);
                    lowName.setVisibility(View.VISIBLE);
                    total.setVisibility(View.VISIBLE);
                    sumValue.setVisibility(View.VISIBLE);

                    highestScoreTextView.setText(String.valueOf(user[0].getHighestQRScore()));
                    highName.setText(user[0].getHighestQRScoreName());

                    low.setText(String.valueOf(user[0].getLowestQRScore()));
                    lowName.setText(user[0].getLowestQRScoreName());

                    total.setText(String.valueOf(user[0].getQRList().size()));

                    sumValue.setText(String.valueOf(user[0].getSumQRScores()));

                }
            }

        });

    }
}
