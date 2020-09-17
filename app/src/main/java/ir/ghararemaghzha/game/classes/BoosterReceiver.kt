package ir.ghararemaghzha.game.classes

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ir.ghararemaghzha.game.R

class BoosterReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!!.action == "refreshBoosterStatus") {
            MySharedPreference.getInstance(context).boosterValue = 1f
            Utils.createNotification(context!!, context.getString(R.string.booster_notif_title), context.getString(R.string.booster_notif_body), "ir.ghararemaghzha.game.TARGET_NOTIFICATION")
        }
    }
}
