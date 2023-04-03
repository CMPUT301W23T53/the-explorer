package com.example.theexplorer.ui.leaderboard;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnFailureListener;

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

//        listFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                if(i == 1 && scoresDescending || i== 0 && !scoresDescending){
//                    scoresDescending = !scoresDescending;
//                    Collections.reverse(usersDataList);
//                    usersAdapter.notifyDataSetChanged();
//                    //refreshLeaderboardView();
//                }
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//                ;
//            }
//        });

        scoreType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if((i == 1 && totalScoreMode) || (i == 0 && !totalScoreMode)){
                    totalScoreMode = !totalScoreMode;
                    //listFilter.setSelection(0);
//                    if(!scoresDescending){
//                        scoresDescending = !scoresDescending;
//                    }
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
                    for (int j = 0; j < arrayQRCode.size(); j++) {
                        Map<String, Object> qrCode = (Map<String, Object>) arrayQRCode.get(j);


                        String name = (String) qrCode.get("qrname");
                        Log.d("L", qrCode.keySet().toString());

                        Long score = (Long) qrCode.get("qrscore");
                        String id = (String) qrCode.get("qrid");
                        Log.d("L",(String) qrCode.get("qrid"));
                        if(!uniqueQRCodes.contains(name)){
                            usersDataList.add(new RankingData(user.getUserId(),name,score,true,null));
                            uniqueQRCodes.add(name);
                        }
                    }
                }
            }
            Collections.sort(usersDataList);

//            if(scoresDescending){i = 1;}
//            else{i = usersDataList.size();}
            i = 1;

            for (RankingData data: usersDataList){
                data.setRanking(i);
//                if(scoresDescending){i++;}
//                else{i--;}
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
        //listFilter = findViewById(R.id.spinner_filter);

        String[] userScopeArray = {"Total Score","Highest QR Score"};
        //String[] listFilterArray= {"Descending","Ascending"};
        ArrayList<String> scoreTypeChoices = new ArrayList<>(Arrays.asList(userScopeArray));
        //ArrayList<String> listFilterChoices = new ArrayList<>(Arrays.asList(listFilterArray));
        ArrayAdapter<String> scoreTypeAdapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, scoreTypeChoices);
        //ArrayAdapter<String> listFilterAdapter = new ArrayAdapter<>(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, listFilterChoices);

        scoreType.setAdapter(scoreTypeAdapter);
        //listFilter.setAdapter(listFilterAdapter);
    }

}