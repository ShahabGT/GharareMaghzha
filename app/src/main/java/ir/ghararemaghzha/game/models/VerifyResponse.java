package ir.ghararemaghzha.game.models;

import com.google.gson.annotations.SerializedName;

public class VerifyResponse {
//    "result": "success",
//            "version": "1",
//            "version_essential": "0",
//            "message": "ok"

    private String result;
    private String version;

    @SerializedName("version_essential")
    private String versionEssential;

    private String message;

    @SerializedName("user_plan")
    private String userPlan;

    @SerializedName("score_count")
    private String scoreCount;

    @SerializedName("user_booster_expire")
    private String userBoosterExpire;

    @SerializedName("user_booster")
    private String userBooster;

    @SerializedName("booster_value")
    private String boosterValue;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersionEssential() {
        return versionEssential;
    }

    public void setVersionEssential(String versionEssential) {
        this.versionEssential = versionEssential;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserPlan() {
        return userPlan;
    }

    public void setUserPlan(String userPlan) {
        this.userPlan = userPlan;
    }

    public String getScoreCount() {
        return scoreCount;
    }

    public void setScoreCount(String scoreCount) {
        this.scoreCount = scoreCount;
    }

    public String getUserBoosterExpire() {
        return userBoosterExpire;
    }

    public void setUserBoosterExpire(String userBoosterExpire) {
        this.userBoosterExpire = userBoosterExpire;
    }

    public String getUserBooster() {
        return userBooster;
    }

    public void setUserBooster(String userBooster) {
        this.userBooster = userBooster;
    }

    public String getBoosterValue() {
        return boosterValue;
    }

    public void setBoosterValue(String boosterValue) {
        this.boosterValue = boosterValue;
    }
}
