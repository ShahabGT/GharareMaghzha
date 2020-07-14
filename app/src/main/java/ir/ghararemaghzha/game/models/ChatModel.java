package ir.ghararemaghzha.game.models;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ChatModel extends RealmObject {
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

    @PrimaryKey
    @SerializedName("message_id")
    private String messageId;

    @SerializedName("sender_user_id")
    private String senderUserId;

    @SerializedName("receiver_user_id")
    private String receiverUserId;

    private String message;

    private String date;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(String senderUserId) {
        this.senderUserId = senderUserId;
    }

    public String getReceiverUserId() {
        return receiverUserId;
    }

    public void setReceiverUserId(String receiverUserId) {
        this.receiverUserId = receiverUserId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
