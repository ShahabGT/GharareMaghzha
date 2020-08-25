package ir.ghararemaghzha.game.models;

import java.util.List;

public class SliderResponse {

    private String result;
    private String message;
    private List<SliderModel> data;

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

    public List<SliderModel> getData() {
        return data;
    }

    public void setData(List<SliderModel> data) {
        this.data = data;
    }
}
