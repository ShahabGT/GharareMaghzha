package ir.ghararemaghzha.game.models;

import java.util.List;

public class ChatResponse {
//        "result": "success",
//                "message": "ok",
//                "data": [
//    {
//        "message_id": "2",
//            "sender_user_id": "2",
//            "receiver_user_id": "1",
//            "message": "سلام",
//            "date": "2020-07-14 23:58:36",
//            "adminseen": "0"
//    }
//    ]

    private String result;
    private String message;
    private List<MessageModel> data;


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

    public List<MessageModel> getData() {
        return data;
    }

    public void setData(List<MessageModel> data) {
        this.data = data;
    }
}
