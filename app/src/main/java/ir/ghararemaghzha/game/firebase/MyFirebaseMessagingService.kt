package ir.ghararemaghzha.game.firebase;

import android.content.Intent
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.realm.Realm
import ir.ghararemaghzha.game.classes.Const.GHARAREHMAGHZHA_BROADCAST
import ir.ghararemaghzha.game.classes.Const.GHARAREHMAGHZHA_BROADCAST_SUPPORT_EXTRA
import ir.ghararemaghzha.game.classes.MySettingsPreference
import ir.ghararemaghzha.game.classes.MySharedPreference
import ir.ghararemaghzha.game.classes.Utils
import ir.ghararemaghzha.game.classes.Utils.getNextKey
import ir.ghararemaghzha.game.models.MessageModel


class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        if (p0.isNotEmpty())
            MySharedPreference.getInstance(this).fbToken = p0
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val data = remoteMessage.data
        val title = data["title"]!!
        val notificationBody = data["body"]!!
        val clickAction = data["click_action"]!!
        val intent = Intent()
        intent.action = GHARAREHMAGHZHA_BROADCAST
        intent.putExtra(GHARAREHMAGHZHA_BROADCAST_SUPPORT_EXTRA, "new")

        if (title != "support") {
            val body = data["body"]
            val sender = data["sender"]
            val model = MessageModel()
            val date = data["time"]
            model.stat = 1
            model.message = body
            model.title = title
            model.sender = sender
            model.date = date
            model.read = 0
            val db = Realm.getDefaultInstance()
            model.messageId = getNextKey(db)
            db.beginTransaction()
            db.insert(model)
            db.commitTransaction()


        } else {
            MySharedPreference.getInstance(this).unreadChats = MySharedPreference.getInstance(this).unreadChats + 1
        }
        sendBroadcast(intent)
        if (MySettingsPreference.getInstance(this).notification)
            Utils.createNotification(this, title, notificationBody, clickAction)

    }


}