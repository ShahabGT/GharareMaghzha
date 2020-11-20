package ir.ghararemaghzha.game.classes

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.realm.Realm
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.models.MessageModel

class BoosterReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!!.action == "refreshBoosterStatus") {
         //   Utils.updateScoreBooster(context,"0")
            MySharedPreference.getInstance(context).clearCounter(context,false)
            MySharedPreference.getInstance(context).boosterValue = 1f
            val title= context?.getString(R.string.booster_notif_title)
            val body= context?.getString(R.string.booster_notif_body)
            Utils.createNotification(context!!, title, body, "ir.ghararemaghzha.game.TARGET_NOTIFICATION")
            saveToDB(title!!,body!!)
        }
    }

    private fun saveToDB(body:String,title:String){
        val model = MessageModel()
        model.stat = 1
        model.message = body
        model.title = title
        model.sender = "admin"
        model.date = Utils.currentDate()
        model.read = 0
        val db = Realm.getDefaultInstance()
        model.messageId = Utils.getNextKey(db)
        db.beginTransaction()
        db.insert(model)
        db.commitTransaction()
    }
}
