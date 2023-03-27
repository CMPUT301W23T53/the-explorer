package com.example.theexplorer.ui.leaderboard;

import com.example.theexplorer.services.NewUserService;
import com.example.theexplorer.services.User;

import java.util.ArrayList;

public class Leaderboard {
    final NewUserService userService = new NewUserService();

    String currentUserID = null;
    int linesUpperBound = 10;

    /**
     * Initialize a Leaderboard with default settings.
     */
    public Leaderboard(){}

    /**
     * Initialize a new Leaderboard using the associated Builder.
     * Please see Builder constructor for more details.
     */
    private Leaderboard(LeaderboardBuilder builder){
        this.currentUserID = builder.currentUser;
        this.linesUpperBound = builder.linesUpperBound;
    }

    public void setLinesUpperBound(int newUpperBound) {
        this.linesUpperBound = newUpperBound;
    }

    public ArrayList<String> getTopNDescendingUsersAsStrings(){
        ;
    }
    public ArrayList<User> getTopNDescendingUsers(){
        ;
    }
    public static class LeaderboardBuilder {

        private int linesUpperBound;
        private String currentUser;

        /**
         * Create a new Leaderboard object with a current user.
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
