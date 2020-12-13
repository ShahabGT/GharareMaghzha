package ir.ghararemaghzha.game.models

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ChatModel(
        @PrimaryKey
        @SerializedName("message_id") var messageId: String ="",
        @SerializedName("sender_user_id") var senderUserId: String="",
        @SerializedName("receiver_user_id") var receiverUserId: String="",
        var message: String="",
        var date: String="",
        var adminseen: String=""
) : RealmObject()
