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

}
