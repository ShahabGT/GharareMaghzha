package ir.ghararemaghzha.game.models;

import com.google.gson.annotations.SerializedName;

public class UserDetailsResponse {

//    "result": "success",
//            "message": "ok",
//            "user_data": {
//                "user_id": "16",
//                "user_name": "parisasharifi",
//                "user_code": "82016"
//    },
//            "correct": 212,
//            "incorrect": 449,
//            "plan": "5"

    private String result;
    private String message;
    private int correct;
    private int incorrect;
    private String plan;
    @SerializedName("user_data")
    private UserDataModel userData;


    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCorrect() {
        return correct;
    }

    public void setCorrect(int correct) {
        this.correct = correct;
    }

    public int getIncorrect() {
        return incorrect;
    }

    public void setIncorrect(int incorrect) {
        this.incorrect = incorrect;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public UserDataModel getUserData() {
        return userData;
    }

    public void setUserData(UserDataModel userData) {
        this.userData = userData;
    }
}
