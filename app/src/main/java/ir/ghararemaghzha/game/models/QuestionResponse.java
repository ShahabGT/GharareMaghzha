package ir.ghararemaghzha.game.models;

import java.util.List;

public class QuestionResponse {
//        "result": "success",
//                "message": "questions from 0,100",
//                "data": [
//                          ]
    private String result;
    private String message;
    private List<QuestionModel> data;

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

    public List<QuestionModel> getData() {
        return data;
    }

    public void setData(List<QuestionModel> data) {
        this.data = data;
    }
}
