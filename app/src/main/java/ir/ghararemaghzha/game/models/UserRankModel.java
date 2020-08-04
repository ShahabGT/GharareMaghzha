package ir.ghararemaghzha.game.models;

import com.google.gson.annotations.SerializedName;

public class UserRankModel {
    //        "user": {
//        "user_rank": "2",
//                "score_count": "770",
//                "user_id": "5",
//                "user_name": "شهاب عظیمی",
//                "user_number": "09355452358",
//                "user_code": "7216"
//    }
    @SerializedName("user_rank")
    private String userRank;

    @SerializedName("score_count")
    private String scoreCount;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("user_name")
    private String userName;

    @SerializedName("user_number")
    private String userNumber;

    @SerializedName("user_code")
    private String userCode;

    public String getScoreCount() {
        return scoreCount;
    }

    public void setScoreCount(String scoreCount) {
        this.scoreCount = scoreCount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserRank() {
        return userRank;
    }

    public void setUserRank(String userRank) {
        this.userRank = userRank;
    }
}
