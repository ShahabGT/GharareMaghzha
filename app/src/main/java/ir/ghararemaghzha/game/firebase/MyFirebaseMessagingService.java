package ir.ghararemaghzha.game.firebase;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import ir.ghararemaghzha.game.classes.MySharedPreference;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
    }


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        if(!s.isEmpty()){
            MySharedPreference.getInstance(this).setFbToken(s);
        }
    }
}
