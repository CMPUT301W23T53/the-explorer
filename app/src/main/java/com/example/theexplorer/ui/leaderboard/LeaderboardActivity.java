package com.example.theexplorer.ui.leaderboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class LeaderboardActivity extends AppCompatActivity {

    //private Leaderboard leaderboard;
    private ArrayList<RankingTuple> usersDataList = new ArrayList<>();
    private LeaderboardAdapter usersAdapter;

    private Spinner listFilter;
    private Spinner scoreType;
    private ListView usersList;

    private final NewUserService userService = new NewUserService();
    private boolean scoresDescending = true;
    private boolean totalScoreMode = true;
    // note that this upper bound can +1 if the user is not in the list.
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

        listFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 1 && scoresDescending || i== 0 && !scoresDescending){
                    scoresDescending = !scoresDescending;
                    refreshLeaderboardView();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                ;
            }
        });

        scoreType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 1 && totalScoreMode == true || i == 0 && totalScoreMode == false){
                    totalScoreMode = !totalScoreMode;
                    refreshLeaderboardView();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                ;
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

    private void refreshLeaderboardView(){
        usersDataList.clear();
        userService.getGameWideHighScoreOfAllPlayers().addOnSuccessListener(users -> {
            Log.d("LEADERBOARD", "List successfully obtained.");
            int i = 1;
            if(totalScoreMode){
                for (User user : users) {
                    usersDataList.add(new RankingTuple(user.getUserId(),i));
                    i++;
                    if (i > linesUpperBound) {
                        break;
                    }
                }
            }
            else {
                for (User user : users) {
                    List<QRCode> arrayQRCode = user.getQRList();
                    for (int j = 0; j < arrayQRCode.size(); j++) {
                        Map<String, Object> qrCode = (Map<String, Object>) arrayQRCode.get(j);

                        long score = (long) qrCode.get("qrscore");
                        usersDataList.add(new RankingTuple(user.getUserId(), score));

                    }
                }
            }
            Collections.sort(usersDataList);
            if (!scoresDescending && totalScoreMode|| scoresDescending && !totalScoreMode){
                Collections.reverse(usersDataList);
            }

            usersAdapter.notifyDataSetChanged();


        });
        Toast.makeText(this,"Refreshed", Toast.LENGTH_SHORT).show();
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
        listFilter = findViewById(R.id.spinner_filter);

        String[] userScopeArray = {"Total Score","Highest QR Score"};
        String[] listFilterArray= {"Descending","Ascending"};
        ArrayList<String> scoreTypeChoices = new ArrayList<>(Arrays.asList(userScopeArray));
        ArrayList<String> listFilterChoices = new ArrayList<>(Arrays.asList(listFilterArray));
        ArrayAdapter<String> scoreTypeAdapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, scoreTypeChoices);
        ArrayAdapter<String> listFilterAdapter = new ArrayAdapter<>(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, listFilterChoices);

        scoreType.setAdapter(scoreTypeAdapter);
        listFilter.setAdapter(listFilterAdapter);
    }

}