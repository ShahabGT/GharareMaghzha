package ir.ghararemaghzha.game.firebase;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import io.realm.Realm;
import ir.ghararemaghzha.game.classes.MySettingsPreference;
import ir.ghararemaghzha.game.classes.MySharedPreference;
import ir.ghararemaghzha.game.classes.Utils;
import ir.ghararemaghzha.game.models.MessageModel;

import static ir.ghararemaghzha.game.classes.Const.GHARAREHMAGHZHA_BROADCAST;
import static ir.ghararemaghzha.game.classes.Const.GHARAREHMAGHZHA_BROADCAST_SUPPORT_EXTRA;
import static ir.ghararemaghzha.game.classes.Utils.getNextKey;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title") + "";
        String notificationBody = data.get("body") + "";
        String clickAction = data.get("click_action") + "";
        Intent intent = new Intent();
        intent.setAction(GHARAREHMAGHZHA_BROADCAST);
        intent.putExtra(GHARAREHMAGHZHA_BROADCAST_SUPPORT_EXTRA, "new");

        if (!title.equals("support")) {
            String body = data.get("body") + "";
            String sender = data.get("sender") + "";
            MessageModel model = new MessageModel();
            String date = data.get("time");
            model.setStat(1);
            model.setMessage(body);
            model.setTitle(title);
            model.setSender(sender);
            model.setDate(date);
            model.setRead(0);
            Realm db = Realm.getDefaultInstance();
            model.setMessageId(getNextKey(db));
            db.beginTransaction();
            db.insert(model);
            db.commitTransaction();
        } else {
            MySharedPreference.getInstance(this).setUnreadChats(MySharedPreference.getInstance(this).getUnreadChats() + 1);
        }
        sendBroadcast(intent);
        if (MySettingsPreference.getInstance(this).getNotification())
            Utils.createNotification(this, title, notificationBody, clickAction);

    }


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        if (!s.isEmpty())
            MySharedPreference.getInstance(this).setFbToken(s);
    }

}
