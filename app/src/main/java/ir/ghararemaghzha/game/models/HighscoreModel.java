package ir.ghararemaghzha.game.models;

import com.google.gson.annotations.SerializedName;

public class HighscoreModel {
//        "data": [
//    {
//        "score_count": "10",
//            "user_id": "6",
//            "user_name": "شهاب جون",
//            "user_number": "09183857769",
//            "user_code": "7226"
//    },

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

    @SerializedName("user_avatar")
    private String userAvatar;

    private int color;

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

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
