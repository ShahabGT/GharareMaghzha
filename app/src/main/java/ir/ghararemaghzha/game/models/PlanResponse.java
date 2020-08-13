package ir.ghararemaghzha.game.models;

import java.util.List;

public class PlanResponse {

    private String result;
    private String message;
    private List<PlanModel> data;


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

    public List<PlanModel> getData() {
        return data;
    }

    public void setData(List<PlanModel> data) {
        this.data = data;
    }
}
