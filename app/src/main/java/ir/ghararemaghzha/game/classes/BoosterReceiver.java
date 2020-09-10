package ir.ghararemaghzha.game.classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ir.ghararemaghzha.game.R;

public class BoosterReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        MySharedPreference.getInstance(context).setBoosterValue(1f);
        Utils.createNotification(context,context.getString(R.string.booster_notif_title),context.getString(R.string.booster_notif_body),"ir.ghararemaghzha.game.TARGET_NOTIFICATION");
    }
}
