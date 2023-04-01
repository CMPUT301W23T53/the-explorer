package com.example.theexplorer.ui.leaderboard;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.theexplorer.services.NewUserService;
import com.example.theexplorer.services.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Leaderboard {
    private boolean debugMode = false;
    private final NewUserService userService = new NewUserService();
    private ArrayList<RankingTuple> fullList = new ArrayList<>();
    private boolean scoresDescending = true;
    // note that this upper bound can +1 if the user is not in the list.
    private int linesUpperBound = 50;


    /**
     * Initialize a Leaderboard with default settings.
     * No user will be focused; will show n top players
     */
    public Leaderboard(){
        refreshUserList();
    }

    /**
     * Initialize a new Leaderboard using the associated Builder.
     * Please see Builder constructor for more details.
     */
    private Leaderboard(LeaderboardBuilder builder){

        if(builder.preloadEntireList){refreshUserList();}
        if(builder.toReverse != null){
            this.scoresDescending = !builder.toReverse;
            if(builder.toReverse){
                Collections.reverse(fullList);
            }
        }
        if(builder.linesUpperBound != null){this.linesUpperBound = builder.linesUpperBound;}
    }

    public void setLinesUpperBound(int newUpperBound) {
        this.linesUpperBound = newUpperBound;
    }


    /**
     * Get the ranking of the user.
     * @param userString - the user ID to find.
     * @return ranking - an integer
     */
    public int getRanking(String userString){
        final int[] ranking = {0};
        userService.getUser(userString).addOnSuccessListener(new OnSuccessListener<User>() {
            @Override
            public void onSuccess(User user) {
                userService.getRankOfUser(user).addOnSuccessListener(new OnSuccessListener<Integer>() {
                    @Override
                    public void onSuccess(Integer integer) {
                        ranking[0] = integer;
                    }
                });
            }
        });
        return ranking[0];

    }
    public ArrayList<RankingTuple> getFullList(){
        return this.fullList;
    }

    public void setListOrderAsDescending(boolean toDescending){
        boolean prior = this.scoresDescending;
        this.scoresDescending = toDescending;
        if(this.scoresDescending != prior){
            Collections.reverse(fullList);
        }
    }

    /**
     * Return whether the list is in descending order, using the scores.
     * @return - a boolean True if in descending order, False otherwise.
     */
    public boolean getScoresDescending(){
        return this.scoresDescending;
    }

    /**
     * Refresh the list using the latest list from Firebase.
     */
    public void refreshUserList(){
        userService.getGameWideHighScoreOfAllPlayers().addOnCompleteListener(task -> {
            task.addOnSuccessListener(users -> {
                Log.d("LEADERBOARD","List successfully obtained.");
                int i = 1;
                for(User user: users) {
                    fullList.add(new RankingTuple(user.getUserId(),i));
                    if (i > linesUpperBound) {
                        break;
                    }
                }
            });
        });

        if(!scoresDescending){
            Collections.reverse(fullList);
        }
    }


    //---------------------------------------------------------------------------------------------
    public static class LeaderboardBuilder {

        private Integer linesUpperBound;
        private String currentUser;
        private boolean preloadEntireList;
        private Boolean toReverse;

        /**
         * Create a new Leaderboard object using the associated Builder.
         */
        public LeaderboardBuilder(){
        }



        /**
         * Modify the default lines upper bound.
         * @param upperBound - max lines to display
         * @return the same LeaderboardBuilder instance
         */
        public LeaderboardBuilder setLinesUpperBound(int upperBound){
            if(upperBound < 0){
                throw new IllegalArgumentException();
            }
            this.linesUpperBound = upperBound;
            return this;
        }

        /**
         * Set whether to load the entire list of users, in descending order.
         * (Depending on the size of the users list and time/memory constraints, this may not be desirable.)
         * @param toPreload - boolean True or False
         * @return the same LeaderboardBuilder instance
         */
        public LeaderboardBuilder initializeAtBuild(boolean toPreload){
            this.preloadEntireList = toPreload;
            return this;
        }

        /**
         * Set the list order of the Leaderboard.
         * By default, the list is in descending order; it is from the highest score to the lowest.
         * Eg. The first user will be the highest.
         * @param toReverse - boolean True, will cause the *lowest* scoring to be at the top.
         * @return the same LeaderboardBuilder instance
         */
        public LeaderboardBuilder setAscendingScoreOrder(boolean toReverse){
            this.toReverse = toReverse;
            return this;
        }


        /**
         * Return a Leaderboard object with the specified changes.
         * @return A Leaderboard
         */
        public Leaderboard build(){
            return new Leaderboard(this);
        }
    }
}
