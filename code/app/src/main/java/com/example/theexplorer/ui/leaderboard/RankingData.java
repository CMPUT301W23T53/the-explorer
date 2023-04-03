package com.example.theexplorer.ui.leaderboard;

/**

 RankingData is a class representing a data structure to store ranking information.
 It implements the Comparable interface to allow sorting based on its value.
 */
public class RankingData implements Comparable<RankingData>{
    private String userID;
    private Integer ranking;
    private String subtitle;
    private long value;
    private boolean isQRCode;

    /**
     Constructor for RankingData objects.
     @param userID The user ID
     @param subtitle A subtitle for the ranking data
     @param value The value associated with the ranking data
     @param isQRCode A boolean flag indicating whether this is QR code data or not
     @param ranking The ranking value for the data
     */
    public RankingData(String userID, String subtitle, long value, boolean isQRCode, Integer ranking){
        this.userID = userID;
        this.subtitle = subtitle;
        this.value = value;
        this.isQRCode = isQRCode;
        this.ranking = ranking;
    }

    public String getSubtitle() {return subtitle;}
    public long getValue() {
        return value;
    }
    public String getUserID(){return userID;}
    public boolean getIsQRCode(){return isQRCode;}
    public int getRanking(){return ranking;}
    public void setRanking(int ranking){this.ranking = ranking;}

    /**
    * Compares this RankingData object with another RankingData object.
    * @param rankingData The RankingData object to compare with
    * @return An integer indicating the comparison result:
    *         1 if this object's value is less than the given object's value,
    *         0 if both values are equal,
     *        -1 if this object's value is greater than the given object's value.
    */
    @Override
    public int compareTo(RankingData rankingData) {
        if(this.value < rankingData.value){return 1;}
        else if (this.value == rankingData.value){return 0;}
        else{return -1;}
    }
}
