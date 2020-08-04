package ir.ghararemaghzha.game.classes;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().name("myRealm.realm").build();
        byte[] dbKey = new byte[64];
        Random random = new SecureRandom();
        random.nextBytes(dbKey);
        RealmConfiguration config2 = new RealmConfiguration.Builder()
                .name("myrealm.realm")
                .encryptionKey(dbKey)
                .schemaVersion(1)
                .build();
        Realm.setDefaultConfiguration(config);
        Arrays.fill(dbKey, (byte) 0);
    }
}
