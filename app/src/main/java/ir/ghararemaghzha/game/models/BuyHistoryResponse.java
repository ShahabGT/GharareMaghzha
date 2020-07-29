package ir.ghararemaghzha.game.models;

import java.util.List;

public class BuyHistoryResponse {

    private String result;
    private String message;
    private List<BuyHistoryModel> data;

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

    public List<BuyHistoryModel> getData() {
        return data;
    }

    public void setData(List<BuyHistoryModel> data) {
        this.data = data;
    }
}
