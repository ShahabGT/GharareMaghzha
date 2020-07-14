package ir.ghararemaghzha.game.models;

public class TimeResponse {
//    "result": "success",
//            "message": "ok",
//            "time": "2020-07-14 22:59:00"

    private String result;
    private String message;
    private String time;

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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
