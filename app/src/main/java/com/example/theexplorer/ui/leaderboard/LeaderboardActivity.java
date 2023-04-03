package com.example.theexplorer.ui.leaderboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.theexplorer.R;
import com.example.theexplorer.services.NewUserService;
import com.example.theexplorer.services.QRCode;
import com.example.theexplorer.services.User;
import com.example.theexplorer.ui.profile.ProfilesActivity;
import com.example.theexplorer.ui.search.SearchActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class LeaderboardActivity extends AppCompatActivity {

    //private Leaderboard leaderboard;
    private ArrayList<RankingData> usersDataList = new ArrayList<>();
    private LeaderboardAdapter usersAdapter;

    private Spinner listFilter;
    private Spinner scoreType;
    private ListView usersList;

    private final NewUserService userService = new NewUserService();
    //private boolean scoresDescending = true;
    private boolean totalScoreMode = true;
    private int linesUpperBound = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        Toolbar toolbar = findViewById(R.id.leaderboard_toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Leaderboard");

            //display back button on Toolbar
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        initializeLeaderboard();
        initializeSpinners();
    }

    @Override
    public void onStart(){
        super.onStart();

        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.list_view_pull_to_refresh);
        pullToRefresh.setOnRefreshListener(() -> {
            refreshLeaderboardView();
            pullToRefresh.setRefreshing(false);
        });

        scoreType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if((i == 1 && totalScoreMode) || (i == 0 && !totalScoreMode)){
                    totalScoreMode = !totalScoreMode;
                    refreshLeaderboardView();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                ;
            }
        });

        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent goToUser = new Intent(LeaderboardActivity.this, ProfilesActivity.class);
                userService.getNameFromEmail(usersDataList.get(i).getUserID()).addOnSuccessListener(s -> {
                    goToUser.putExtra("userName1", s);
                    startActivity(goToUser);
                }).addOnFailureListener(e -> Toast.makeText(LeaderboardActivity.this,"Network error. Please try again.", Toast.LENGTH_SHORT).show());
            }
        });
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onBackPressed() {
        finish();
    }

    /**
     * Refresh the Leaderboard dataset and push changes to ListView.
     */
    private void refreshLeaderboardView(){
        usersDataList.clear();
        userService.getGameWideHighScoreOfAllPlayers().addOnSuccessListener(users -> {
            Log.d("LEADERBOARD", "List successfully obtained.");
            int i = 1;
            if(totalScoreMode){
                for (User user : users) {
                    long sum = 0;
                    List<QRCode> arrayQRCode = user.getQRList();
                    for (int j = 0; j < arrayQRCode.size(); j++) {
                        Map<String, Object> qrCode = (Map<String, Object>) arrayQRCode.get(j);
                        long score = (long) qrCode.get("qrscore");
                        sum += score;
                    }
                    usersDataList.add(new RankingData(user.getUserId(),String.valueOf(sum),sum,false, null));
                    i++;
                    if (i > linesUpperBound) {
                        break;
                    }
                }
            }
            else {
                ArrayList<String> uniqueQRCodes = new ArrayList<>();
                for (User user : users) {
                    List<QRCode> arrayQRCode = user.getQRList();
                    Long score = Long.valueOf(0);
                    String name = "";
                    for (int j = 0; j < arrayQRCode.size(); j++) {
                        Map<String, Object> qrCode = (Map<String, Object>) arrayQRCode.get(j);
                        Long scoreToCompare = (Long) qrCode.get("qrscore");

                        if(scoreToCompare >= score){
                            score = scoreToCompare;
                            name = (String) qrCode.get("qrname");
                        }
                    }
                    if(score > 0){
                        usersDataList.add(new RankingData(user.getUserId(),name,score,true,null));
                    }
                    i++;
                    if(i > linesUpperBound){
                        break;
                    }
                }
            }
            Collections.sort(usersDataList);
            i = 1;

            for (RankingData data: usersDataList){
                data.setRanking(i);
                i++;
            }

            usersAdapter.notifyDataSetChanged();


        }).addOnFailureListener(e -> Toast.makeText(getBaseContext(), "Unable to get the data. Check back later.",Toast.LENGTH_SHORT).show());

        Toast.makeText(getBaseContext(),"Refreshed", Toast.LENGTH_SHORT).show();
    }

    /**
     * Initialize the Leaderboard ListView and associated components.
     */
    private void initializeLeaderboard(){
        usersList = findViewById(R.id.leaderboard_content);
        usersAdapter = new LeaderboardAdapter(this,usersDataList);
        usersList.setAdapter(usersAdapter);
        refreshLeaderboardView();
    }

    /**
     * Initialize the Spinners at the top of the activity.
     */
    private void initializeSpinners(){
        scoreType = findViewById(R.id.spinner_score_type);

        String[] userScopeArray = {"Total Score","Highest QR Score"};
        ArrayList<String> scoreTypeChoices = new ArrayList<>(Arrays.asList(userScopeArray));
        ArrayAdapter<String> scoreTypeAdapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, scoreTypeChoices);

        scoreType.setAdapter(scoreTypeAdapter);
    }

}