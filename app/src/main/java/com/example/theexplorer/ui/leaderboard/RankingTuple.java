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

    /**
     * Gets the value associated with this tuple.
     * @return The value associated with this tuple.
     */
    public long getValue() {
        return value;
    }

    /**
     * Gets the ID associated with this tuple.
     * @return The ID associated with this tuple.
     */
    public String getId(){
        return id;
    }

    /**
     * Compares this RankingTuple object with another RankingTuple object based on their values.
     * @param rankingTuple The other RankingTuple object to compare this object to.
     * @return An integer indicating the order of the two RankingTuple objects.
     *         Returns 1 if this object is greater than the other object,
     *         0 if they are equal, and -1 if this object is less than the other object.
     */
    @Override
    public int compareTo(RankingTuple rankingTuple) {
        if(this.value > rankingTuple.value){return 1;}
        else if (this.value == rankingTuple.value){return 0;}
        else{return -1;}
    }
}
