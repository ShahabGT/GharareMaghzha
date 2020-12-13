package ir.ghararemaghzha.game.models

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class MessageModel(
        @PrimaryKey var messageId: Int=0,
        var id: String="",
        var read: Int=0,
        var message: String="",
        var title: String="",
        @SerializedName("sender_user_id") var sender: String="",
        @SerializedName("receiver_user_id") var receiver: String="",
        var date: String="",
        var stat: Int=0
) : RealmObject()
