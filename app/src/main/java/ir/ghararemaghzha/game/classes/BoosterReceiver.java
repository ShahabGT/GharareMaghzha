package ir.ghararemaghzha.game.classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BoosterReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        MySharedPreference.getInstance(context).setBoosterValue(1f);
        Utils.createNotification(context,"booster expired","YOUR BOOSTER HAS EXPIRED PLEASE RECHARGE IF YOU NEED MORE TIME WITH BOOSTER!","ir.ghararemaghzha.game.TARGET_NOTIFICATION");
    }
}
