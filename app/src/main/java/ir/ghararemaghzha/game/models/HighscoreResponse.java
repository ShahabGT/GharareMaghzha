package ir.ghararemaghzha.game.models;

import java.util.List;

public class HighscoreResponse {

    private String result;
    private String message;
    private List<HighscoreModel> data;
    private UserRankModel user;


    public UserRankModel getUser() {
        return user;
    }

    public void setUser(UserRankModel user) {
        this.user = user;
    }

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

    public List<HighscoreModel> getData() {
        return data;
    }

    public void setData(List<HighscoreModel> data) {
        this.data = data;
    }
}
