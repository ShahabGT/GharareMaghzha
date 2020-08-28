package ir.ghararemaghzha.game.models;

import com.google.gson.annotations.SerializedName;

public class GeneralResponse {

    private String result;

    private String message;

    private String token;

    private String time;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("user_name")
    private String userName;

    @SerializedName("user_code")
    private String userCode;

    @SerializedName("user_score")
    private String userScore;

    @SerializedName("user_plan")
    private String userPlan;

    @SerializedName("influencer_id")
    private String influencerId;

    @SerializedName("influencer_amount")
    private String influencerAmount;

    @SerializedName("merchant_id")
    private String merchantId;

    @SerializedName("user_sex")
    private String userSex;

    @SerializedName("user_bday")
    private String userBday;

    @SerializedName("user_email")
    private String userEmail;

    @SerializedName("user_invite")
    private String userInvite;

    @SerializedName("user_avatar")
    private String userAvatar;

    private String count;
    private String value;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUserPlan() {
        return userPlan;
    }

    public void setUserPlan(String userPlan) {
        this.userPlan = userPlan;
    }

    public String getUserScore() {
        return userScore;
    }

    public void setUserScore(String userScore) {
        this.userScore = userScore;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getInfluencerId() {
        return influencerId;
    }

    public void setInfluencerId(String influencerId) {
        this.influencerId = influencerId;
    }

    public String getInfluencerAmount() {
        return influencerAmount;
    }

    public void setInfluencerAmount(String influencerAmount) {
        this.influencerAmount = influencerAmount;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public String getUserBday() {
        return userBday;
    }

    public void setUserBday(String userBday) {
        this.userBday = userBday;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserInvite() {
        return userInvite;
    }

    public void setUserInvite(String userInvite) {
        this.userInvite = userInvite;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }
}
