package com.example.theexplorer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.service.notification.NotificationListenerService;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.theexplorer.services.NewUserService;
import com.example.theexplorer.services.User;
import com.example.theexplorer.ui.leaderboard.LeaderboardActivity;
import com.example.theexplorer.ui.leaderboard.RankingData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class LeaderboardTest {
    public Solo solo;
    NewUserService userService = new NewUserService();

    /**
     * A Mock User that links to the mock user on the remote database.
     * @return User
     */
    public User mockUser(){
        User user = new User();
        user.setUserId("test123@test.com");
        return user;
    }

    @Rule
    public ActivityTestRule<MainActivity> mainRule = new ActivityTestRule<>(MainActivity.class, true, true);

    /**
     * Initialize Solo instance.
     * @throws Exception - if rule has not been defined.
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), mainRule.getActivity());
    }

    /**
     * Check that application actually starts.
     */
    @Test
    public void checkAppStart(){
        Activity activity = mainRule.getActivity();
        assertSame(solo.getCurrentActivity(), activity);
    }

    /**
     * Assert that the remote test user defined in MockUser exists.
     */
    @Test
    public void checkRemoteTestUserExists() {
        userService.getUser(mockUser().getUserId()).addOnSuccessListener(user -> assertEquals(user.getUserId(), mockUser().getUserId()));
    }
    /**
     * Check that app starts at MainActivity
     * and successfully switches to LeaderboardActivity.
     */
    @Test
    public void checkActivitySwitch(){
        solo.assertCurrentActivity("Not MainActivity",MainActivity.class);

        solo.clickOnButton("Leaderboard");

        solo.assertCurrentActivity("Did not successfully switch", LeaderboardActivity.class);
    }

    /**
     * Check that ListView shows MockUser.
     */
    @Test
    public void checkUserDisplaysInListView() {
        solo.assertCurrentActivity("Not MainActivity",MainActivity.class);

        solo.clickOnButton("Leaderboard");

        solo.assertCurrentActivity("Did not successfully switch", LeaderboardActivity.class);

        solo.waitForText("leaderboardMockTestUser",1,200);
        ListView ranking = (ListView) solo.getView(R.id.leaderboard_content);
        ListAdapter adapter = ranking.getAdapter();
        boolean exists = false;
        for(int i = 0; i < adapter.getCount(); i++){
            RankingData data = (RankingData) ranking.getItemAtPosition(i);
            if(data.getUserID().equals(mockUser().getUserId())){
                exists = true;
                break;
            }
        }
        assertTrue(exists);
    }

    /**
     * Check that ListView changes to Highest QR Code mode when Spinner clicked.
     * The Spinner changes its text to "Highest QR Score". If there are any contents, all contents should have the "isQRCode" parameter as true.
     */
    @Test
    public void checkScoreDisplayModeSwitch(){
        solo.assertCurrentActivity("Not MainActivity",MainActivity.class);

        solo.clickOnButton("Leaderboard");

        solo.assertCurrentActivity("Did not successfully switch", LeaderboardActivity.class);

        Spinner dropDown = (Spinner) solo.getView(R.id.spinner_score_type);
        ListAdapter spinnerAdapter = (ListAdapter) dropDown.getAdapter();
        solo.clickOnView(dropDown);
        solo.clickOnText("Highest QR Score");
        solo.searchText("Highest QR Score", 1);

        ListView ranking = (ListView) solo.getView(R.id.leaderboard_content);
        ListAdapter adapter = ranking.getAdapter();
        if (adapter.getCount() > 0) {
            boolean allQRCodes = true;
            for(int i = 0; i < adapter.getCount(); i++){
                RankingData data = (RankingData) ranking.getItemAtPosition(i);
                if(!data.getIsQRCode()){
                    allQRCodes = false;
                    break;
                }
            }
            assertTrue(allQRCodes);
        }
    }
    /**
     * Check that users don't repeatedly show in Highest QR Code.
     */
    @Test
    public void checkNoDuplicatedQRCode(){
        solo.assertCurrentActivity("Not MainActivity",MainActivity.class);

        solo.clickOnButton("Leaderboard");

        solo.assertCurrentActivity("Did not successfully switch", LeaderboardActivity.class);

        Spinner dropDown = (Spinner) solo.getView(R.id.spinner_score_type);
        solo.clickOnView(dropDown);
        solo.clickOnText("Highest QR Score");

        solo.waitForText(mockUser().getUserId(),1,200);
        ListView list = (ListView) solo.getView(R.id.leaderboard_content);
        ListAdapter adapter = list.getAdapter();

        if(adapter.getCount() > 0){
            boolean noDuplicates = true;
            ArrayList<String> users = new ArrayList<>();
            for(int i= 0; i < adapter.getCount();i++){
                RankingData data = (RankingData) adapter.getItem(i);
                String userID = data.getUserID();
                if(users.contains(userID)){
                    noDuplicates = false;
                    break;
                }
                else {
                    users.add(userID);
                }
            }
            assertTrue(noDuplicates);
        }
    }

    /**
     * If test user has a QR code, check that it has a ranking.
     */
    @Test
    public void checkRankingOfQRCode(){
        solo.assertCurrentActivity("Not MainActivity",MainActivity.class);

        solo.clickOnButton("Leaderboard");

        solo.assertCurrentActivity("Did not successfully switch", LeaderboardActivity.class);

        Spinner dropDown = (Spinner) solo.getView(R.id.spinner_score_type);
        solo.clickOnView(dropDown);
        solo.clickOnText("Highest QR Score");
        solo.waitForText(mockUser().getUserId(),1,200);

        ListView list = (ListView) solo.getView(R.id.leaderboard_content);
        ListAdapter adapter = list.getAdapter();

        if(adapter.getCount() > 0){
            boolean hasRanking = false;
            ArrayList<String> users = new ArrayList<>();
            for(int i= 0; i < adapter.getCount();i++){
                RankingData data = (RankingData) adapter.getItem(i);
                if(data.getUserID().equals(mockUser().getUserId()) && data.getRanking() != 0){
                    hasRanking = true;
                    break;
                }
            }
            assertTrue(hasRanking);
        }
    }

    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }

}

