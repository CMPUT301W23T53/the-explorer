package com.example.theexplorer.ui.leaderboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.example.theexplorer.R;

public class LeaderboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        Toolbar toolbar = findViewById(R.id.leaderboard_toolbar);

        String userName = String.valueOf(getIntent().getBundleExtra("username"));

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Leaderboard");

            //display back button on Toolbar
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        Leaderboard leaderboard = new Leaderboard.LeaderboardBuilder(userName)
                .setLinesUpperBound(10)
                .build();
    }
}