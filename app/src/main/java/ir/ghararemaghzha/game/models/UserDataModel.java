package ir.ghararemaghzha.game.models;

import com.google.gson.annotations.SerializedName;

public class UserDataModel {
    //     "user_rank": "1",
//             "score_count": "26800",
//    "user_id": "16",
//                "user_name": "parisasharifi",
//                "user_code": "82016"
    @SerializedName("user_rank")
    private String userRank;

    @SerializedName("score_count")
    private String scoreCount;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("user_name")
    private String userName;

    @SerializedName("user_code")
    private String userCode;

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

    public String getScoreCount() {
        return scoreCount;
    }

    public void setScoreCount(String scoreCount) {
        this.scoreCount = scoreCount;
    }
}
