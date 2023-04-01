package com.example.theexplorer.ui.leaderboard;

public class RankingTuple implements Comparable<RankingTuple>{
    private int ranking;
    private String id;
    private long value;

    public RankingTuple(String id,long value){
        //this.ranking = ranking;
        this.id = id;
        this.value = value;
    }

    public long getValue() {
        return value;
    }
    public String getId(){
        return id;
    }

    @Override
    public int compareTo(RankingTuple rankingTuple) {
        if(this.value > rankingTuple.value){return 1;}
        else if (this.value == rankingTuple.value){return 0;}
        else{return -1;}
    }
}
