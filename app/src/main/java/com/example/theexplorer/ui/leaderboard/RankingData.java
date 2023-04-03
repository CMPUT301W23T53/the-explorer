package com.example.theexplorer.ui.leaderboard;

public class RankingData implements Comparable<RankingData>{
    private String userID;
    private String subtitle;
    private long value;
    private boolean isQRCode;

    public RankingData(String userID, String subtitle, long value, boolean isQRCode){
        this.userID = userID;
        this.subtitle = subtitle;
        this.value = value;
        this.isQRCode = isQRCode;
    }

    public String getSubtitle() {return subtitle;}
    public long getValue() {
        return value;
    }
    public String getUserID(){return userID;}
    public boolean getIsQRCode(){return isQRCode;}
    @Override
    public int compareTo(RankingData rankingData) {
        if(this.value > rankingData.value){return 1;}
        else if (this.value == rankingData.value){return 0;}
        else{return -1;}
    }
}
