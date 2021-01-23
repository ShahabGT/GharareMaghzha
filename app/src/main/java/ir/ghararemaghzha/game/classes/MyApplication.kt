package ir.ghararemaghzha.game.classes

import android.app.Application
import androidx.core.provider.FontRequest
import androidx.emoji.text.EmojiCompat
import androidx.emoji.text.FontRequestEmojiCompatConfig
import io.realm.Realm
import io.realm.RealmConfiguration
import ir.ghararemaghzha.game.R

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val config = RealmConfiguration.Builder()
                .name("myRealm.realm")
                .schemaVersion(11)
                .allowWritesOnUiThread(true)
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
