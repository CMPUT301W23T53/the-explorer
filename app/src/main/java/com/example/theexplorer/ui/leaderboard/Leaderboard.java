package com.example.theexplorer.ui.leaderboard;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.theexplorer.services.NewUserService;
import com.example.theexplorer.services.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.List;

public class Leaderboard {
    final NewUserService userService = new NewUserService();

    String currentUserID = null;
    int linesUpperBound = 10;

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
        this.currentUserID = builder.currentUser;
        if(builder.linesUpperBound != null){
            this.linesUpperBound = builder.linesUpperBound;
        }
    }

    public void setLinesUpperBound(int newUpperBound) {
        this.linesUpperBound = newUpperBound;
    }

    public ArrayList<String> getTopNDescendingUsersAsStrings(){
        assert Leaderboard.class.getMethod("getTopNDescendingUsers");
    }
    public ArrayList<User> getTopNDescendingUsers(){
        ArrayList<User> toReturn = new ArrayList<>();
        userService.getGameWideHighScoreOfAllPlayers().addOnSuccessListener(new OnSuccessListener<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                for(int i =0; i<linesUpperBound; i++){
                    Log.d("TAG","User: " + users.get(i).getUserId() + " successfully added " +
                            "to ArrayList.");
                    toReturn.add(users.get(i));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "Error getting list.");
            }
        });
    }

    //---------------------------------------------------------------------------------------------
    public static class LeaderboardBuilder {

        private Integer linesUpperBound;
        private String currentUser;

        /**
         * Create a new Leaderboard object with a current user.
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
         * @return the same LeaderboardBuilder object instance
         */
        public LeaderboardBuilder setLinesUpperBound(int upperBound){
            this.linesUpperBound = upperBound;
            return this;
        }

        /**
         * Return a Leaderboard object with the modified variables.
         * @return A Leaderboard
         */
        public Leaderboard build(){
            return new Leaderboard(this);
        }
    }
}
