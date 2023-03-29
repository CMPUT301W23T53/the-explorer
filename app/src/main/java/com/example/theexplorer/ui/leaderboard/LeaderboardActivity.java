package com.example.theexplorer.ui.leaderboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.theexplorer.R;
import com.example.theexplorer.services.QRCode;
import com.example.theexplorer.services.User;

import java.util.ArrayList;
import java.util.Arrays;

public class LeaderboardActivity extends AppCompatActivity {

    private Leaderboard leaderboard;
    private ArrayList<User> usersDataList;
    private LeaderboardAdapter usersAdapter;

    private Spinner userScope;
    private Spinner listFilter;
    private ArrayAdapter<String> userScopeAdapter;
    private ArrayAdapter<String> listFilterAdapter;
    private ListView usersList;
    private String userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        Toolbar toolbar = findViewById(R.id.leaderboard_toolbar);
        if(getIntent().hasExtra("username")){
             userName = String.valueOf(getIntent().getStringExtra("username"));
        }

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Leaderboard");

            //display back button on Toolbar
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        initializeLeaderboard(userName);
        initializeSpinners();

        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.list_view_pull_to_refresh);
        pullToRefresh.setOnRefreshListener(() -> {
            refreshLeaderboardView();
            pullToRefresh.setRefreshing(false);
        });


    }

    @Override
    public void onStart(){
        super.onStart();

        if(leaderboard.getScoresDescending()){
            listFilter.setSelection(0);
        }
        else{
            listFilter.setSelection(1);
        }

        listFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                leaderboard.setListOrderAsDescending(i == 0);
                refreshLeaderboardView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                ;
            }
        });



    }

    @Override
    public void onResume(){
        super.onResume();
        refreshLeaderboardView();
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
        leaderboard.refreshUserList();
        usersDataList = leaderboard.getTopNUsers();
        usersAdapter.notifyDataSetChanged();
    }

    /**
     * Initialize the Leaderboard ListView and associated components.
     * @param userName - to include in the dataset, if necessary.
     */
    private void initializeLeaderboard(String userName){
        Leaderboard.LeaderboardBuilder builder = new Leaderboard.LeaderboardBuilder()
                .setLinesUpperBound(10)
                .initializeEntireUserList(true);

        if(userName != null){builder.setUsername(userName);}

        leaderboard = builder.build();

        //for debugging purposes

        usersDataList = leaderboard.getTopNUsers();

        usersList = findViewById(R.id.leaderboard_content);
        usersAdapter = new LeaderboardAdapter(this,usersDataList);
        usersList.setAdapter(usersAdapter);
    }

    /**
     * Initialize the Spinners at the top of the activity.
     */
    private void initializeSpinners(){
        userScope = findViewById(R.id.spinner_user_scope);
        listFilter = findViewById(R.id.spinner_filter);

        String[] userScopeArray = {"Global","Nearby"};
        String[] listFilterArray= {"Descending","Ascending"};
        ArrayList<String> userScopeChoices = new ArrayList<>(Arrays.asList(userScopeArray));
        ArrayList<String> listFilterChoices = new ArrayList<>(Arrays.asList(listFilterArray));
        userScopeAdapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,userScopeChoices);
        listFilterAdapter = new ArrayAdapter<>(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item,listFilterChoices);

        userScope.setAdapter(userScopeAdapter);
        listFilter.setAdapter(listFilterAdapter);
    }

}