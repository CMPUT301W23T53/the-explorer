package com.example.theexplorer.ui.leaderboard;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.theexplorer.services.NewUserService;
import com.example.theexplorer.services.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

public class Leaderboard {
    final NewUserService userService = new NewUserService();

    private String currentUserID = null;
    private int linesUpperBound = 10;
    private List<User> fullList = null;

    /**
     * Initialize a Leaderboard with default settings.
     * No user will be focused; will show n top players
     */
    public Leaderboard(){}

    /**
     * Initialize a new Leaderboard using the associated Builder.
     * Please see Builder constructor for more details.
     */
    private Leaderboard(LeaderboardBuilder builder){
        if(this.currentUserID != null){ this.currentUserID = builder.currentUser;}
        if(builder.linesUpperBound != null){this.linesUpperBound = builder.linesUpperBound;}
        if(builder.preloadEntireList){refreshUserList();}
    }

    public void setLinesUpperBound(int newUpperBound) {
        this.linesUpperBound = newUpperBound;
    }

    /**
     * Get the ranking of the user.
     * @param userString - the user ID to find.
     * @return ranking - an Integer
     */
    public int getRanking(String userString){
        int ranking = -1;
        int index = 1;
        for(User userToFind : fullList){
            if (userToFind.getUserId().equals(userString)){
                ranking = index;
                break;
            }
            index++;
        }
        return ranking;
    }

    /**
     * Refresh the list using the latest list from Firebase.
     */
    public void refreshUserList(){
        userService.getGameWideHighScoreOfAllPlayers().addOnSuccessListener(new OnSuccessListener<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                    Log.d("TAG","List successfully obtained.");
                    fullList = users;
                }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "Error getting list.");
            }
        });
    }
    public ArrayList<String> getTopNDescendingUsersAsStrings(){
        ArrayList<String> toReturn = new ArrayList<>();
        for(User user : getTopNDescendingUsers()){
            toReturn.add(user.getUserId());
        }
        return toReturn;
    }
    public ArrayList<User> getTopNDescendingUsers(){
        return (ArrayList<User>) fullList.subList(0,linesUpperBound);
    }

    //---------------------------------------------------------------------------------------------
    public static class LeaderboardBuilder {

        private Integer linesUpperBound;
        private String currentUser;
        private boolean preloadEntireList;

        /**
         * Create a new Leaderboard object using the associated Builder.
         * Focuses on the user; if user is beyond line n,
         * the list will be n+1 long with the last being the user's ranking
         * @param currentUser - the currentUser we want to focus
         */
        public LeaderboardBuilder(String currentUser){
            this.currentUser = currentUser;
        }

        /**
         * Modify the default lines upper bound.
         * @param upperBound - max lines to display
         * @return the same LeaderboardBuilder instance
         */
        public LeaderboardBuilder setLinesUpperBound(int upperBound){
            this.linesUpperBound = upperBound;
            return this;
        }

        /**
         * Set whether to load the entire list of users, in descending order.
         * (Depending on the size of the users list and time/memory constraints, this may not be desirable.)
         * @param toPreload - boolean True or False
         * @return the same LeaderboardBuilder instance
         */
        public LeaderboardBuilder initializeEntireUserList(boolean toPreload){
            this.preloadEntireList = toPreload;
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
