package com.example.theexplorer.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.theexplorer.R;
import com.example.theexplorer.services.User;
import com.example.theexplorer.services.UserService;

import org.w3c.dom.Text;

/**
 * This activity allows users to view their highest and lowest scores, as well as the total number
 * of codes scanned, and the sum of all scores from each code scanned.
 */
public class ScoresFragment extends AppCompatActivity {

    private Button highest;
    private Button lowest;
    private Button scanned;
    private Button sum;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        UserService userService = new UserService();
        User user = userService.getUser(1);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);
        highest = findViewById(R.id.button_show_highest);
        lowest = findViewById(R.id.button_show_lowest);
        scanned = findViewById(R.id.button_show_total);
        sum = findViewById(R.id.button_sum);
        TextView high = (TextView)findViewById(R.id.highest_score);
        TextView low = (TextView)findViewById(R.id.lowest_score);
        TextView total = (TextView)findViewById(R.id.total_scanned);
        TextView sumValue = (TextView)findViewById(R.id.score_sum);

        highest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                high.setText(String.valueOf(user.getHighestQRScore()));
            }
        });

        lowest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                low.setText(String.valueOf(user.getLowestQRScore()));;

            }
        });

        scanned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                total.setText(String.valueOf(user.getQRList().size()));
            }
        });

        sum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sumValue.setText(String.valueOf(user.getSumQRScores()));
            }
        });

    }
}
