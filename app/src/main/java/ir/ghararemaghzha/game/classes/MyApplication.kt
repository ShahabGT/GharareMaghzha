package ir.ghararemaghzha.game.classes

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.widget.Toast
import androidx.core.provider.FontRequest
import androidx.emoji.text.EmojiCompat
import androidx.emoji.text.FontRequestEmojiCompatConfig
import com.google.firebase.messaging.FirebaseMessaging
import io.realm.Realm
import io.realm.RealmConfiguration
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.activities.SplashActivity
import ir.ghararemaghzha.game.classes.Const.FCM_TOPIC


class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val config = RealmConfiguration.Builder()
                .name("myRealm.realm")
                .schemaVersion(2)
                .deleteRealmIfMigrationNeeded()
                .build()
        Realm.setDefaultConfiguration(config)
        val fontRequest =  FontRequest(
                "com.google.android.gms.fonts",
                "com.google.android.gms",
                "Noto Color Emoji Compat",
                R.array.com_google_android_gms_fonts_certs
        )
        EmojiCompat.init(FontRequestEmojiCompatConfig(this, fontRequest).setReplaceAll(true))
    }
}
