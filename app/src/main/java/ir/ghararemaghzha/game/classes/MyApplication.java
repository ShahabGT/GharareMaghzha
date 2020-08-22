package ir.ghararemaghzha.game.classes;

import android.app.Application;
import androidx.core.provider.FontRequest;
import androidx.emoji.text.EmojiCompat;
import androidx.emoji.text.FontRequestEmojiCompatConfig;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import ir.ghararemaghzha.game.R;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
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

        FontRequest fontRequest = new FontRequest(
                "com.google.android.gms.fonts",
                "com.google.android.gms",
                "Noto Color Emoji Compat",
                R.array.com_google_android_gms_fonts_certs
        );
        EmojiCompat.init(new FontRequestEmojiCompatConfig(this, fontRequest).setReplaceAll(true));
    }
}
