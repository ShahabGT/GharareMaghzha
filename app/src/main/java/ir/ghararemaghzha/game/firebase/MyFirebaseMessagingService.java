package ir.ghararemaghzha.game.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import io.realm.Realm;
import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.activities.MainActivity;
import ir.ghararemaghzha.game.classes.Const;
import ir.ghararemaghzha.game.classes.MySharedPreference;
import ir.ghararemaghzha.game.classes.Utils;
import ir.ghararemaghzha.game.models.MessageModel;

import static ir.ghararemaghzha.game.classes.Const.GHARAREHMAGHZHA_BROADCAST;
import static ir.ghararemaghzha.game.classes.Const.GHARAREHMAGHZHA_BROADCAST_SUPPORT_EXTRA;
import static ir.ghararemaghzha.game.classes.Utils.getNextKey;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
       // String date = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss", Locale.ENGLISH).format(new Date(remoteMessage.getSentTime()));
        Map<String, String> data = remoteMessage.getData();

        String title = data.get("title");
        String body = data.get("body");
        String sender = data.get("sender");
        String date = data.get("time");

        MessageModel model = new MessageModel();
        model.setMessage(body);
        model.setTitle(title);
        model.setSender(sender);
        model.setDate(date);
        Realm db = Realm.getDefaultInstance();
        Intent intent = new Intent();
        intent.setAction(GHARAREHMAGHZHA_BROADCAST);
        model.setMessageId(getNextKey(db));
        model.setRead(0);
        db.beginTransaction();
        db.insert(model);
        db.commitTransaction();
        intent.putExtra(GHARAREHMAGHZHA_BROADCAST_SUPPORT_EXTRA, "new");
        sendBroadcast(intent);
//        if (sender.contains("support")) {
//            createNotification(title, body, "support");
//        } else {
//            createNotification(title, body, "notification");
//        }
            createNotification(title, body);

    }


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        if (!s.isEmpty()) {
            MySharedPreference.getInstance(this).setFbToken(s);
        }
    }

    private void createNotification(String title, String message) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("notidata", "notification");
        //do not use
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //  Uri alarmSound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification);
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Const.CHANNEL_CODE);

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(title);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentText(message);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);
        //  builder.setSound(alarmSound, AudioManager.STREAM_NOTIFICATION);
        builder.setVibrate(new long[]{1000, 1000,1000});
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
