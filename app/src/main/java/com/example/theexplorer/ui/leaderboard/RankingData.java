package com.example.theexplorer.ui.leaderboard;

import com.example.theexplorer.services.User;

public class RankingData implements Comparable<RankingData>{
    private String title;
    private String subtitle;
    private long value;
    private User userReference;
    private int listPosition;
    private String id;

    public RankingData(String id, long value, User userReference){
        this.id = id;
        //this.ranking = ranking;
        //this.title = title;
        //this.subtitle = subtitle;
        this.value = value;
        this.userReference = userReference;
    }

    //public String getTitle(){return title;}
    //public String getSubtitle() {return subtitle;}
    public long getValue() {
        return value;
    }
    public String getId(){return id;}

    public User getUserReference(){ return userReference;}
    //public int getListPosition(){return listPosition;}

    @Override
    public int compareTo(RankingData rankingData) {
        if(this.value > rankingData.value){return 1;}
        else if (this.value == rankingData.value){return 0;}
        else{return -1;}
    }
}
