package ir.ghararemaghzha.game.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import io.realm.Realm;
import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.activities.MainActivity;
import ir.ghararemaghzha.game.activities.SupportActivity;
import ir.ghararemaghzha.game.classes.Const;
import ir.ghararemaghzha.game.classes.MySettingsPreference;
import ir.ghararemaghzha.game.classes.MySharedPreference;
import ir.ghararemaghzha.game.models.MessageModel;

import static ir.ghararemaghzha.game.classes.Const.GHARAREHMAGHZHA_BROADCAST;
import static ir.ghararemaghzha.game.classes.Const.GHARAREHMAGHZHA_BROADCAST_SUPPORT_EXTRA;
import static ir.ghararemaghzha.game.classes.Utils.getNextKey;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title") + "";
        String body = data.get("body") + "";
        String notificationBody = data.get("body") + "";
        String clickAction = data.get("click_action") + "";
        String sender = data.get("sender") + "";
        MessageModel model = new MessageModel();

        if (title.equals("support")) {
            byte[] byteData = body.getBytes(StandardCharsets.UTF_8);
            body = Base64.encodeToString(byteData, Base64.DEFAULT);

            title = "پیام جدید";
        }

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

        Intent intent = new Intent();
        intent.setAction(GHARAREHMAGHZHA_BROADCAST);
        intent.putExtra(GHARAREHMAGHZHA_BROADCAST_SUPPORT_EXTRA, "new");
        sendBroadcast(intent);

        if (MySettingsPreference.getInstance(this).getNotification())
            createNotification(title, notificationBody, clickAction);

    }


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        if (!s.isEmpty()) {
            MySharedPreference.getInstance(this).setFbToken(s);
        }
    }

    private void createNotification(String title, String message, String clickAction) {
        Intent intent;
        if (clickAction.equals("ir.ghararemaghzha.game.TARGET_NOTIFICATION"))
            intent = new Intent(this, MainActivity.class);
        else
            intent = new Intent(this, SupportActivity.class);


        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Const.CHANNEL_CODE);

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(title);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentText(message);
        builder.setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);
        //  Uri alarmSound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification);
        //  builder.setSound(alarmSound, AudioManager.STREAM_NOTIFICATION);
        builder.setVibrate(new long[]{1000, 1000, 1000});
        builder.setLights(Color.YELLOW, 1000, 1000);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(Const.NOTIFICATION_ID, builder.build());


    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Ghararemaghzha Message Notifications";
            String description = "Using this channel to display notification for Ghararemaghzha game";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(Const.CHANNEL_CODE, name, importance);
            notificationChannel.setDescription(description);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
