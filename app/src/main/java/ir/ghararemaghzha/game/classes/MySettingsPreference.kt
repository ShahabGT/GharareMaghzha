package ir.ghararemaghzha.game.classes

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class MySettingsPreference private constructor(ctx: Context) {
    private val masterKey = MasterKey.Builder(ctx).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
    private val sp = EncryptedSharedPreferences.create(
            ctx,
            "settings_preference",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)


    companion object {
        @Volatile
        private var instance: MySettingsPreference? = null

        fun getInstance(ctx: Context): MySettingsPreference =
                instance ?: synchronized(this) {
                    instance ?: MySettingsPreference(ctx).also { instance = it }
                }
    }

    fun setMusic(state: Boolean) = sp.edit { putBoolean("music",state) }

    fun getMusic() = sp.getBoolean("music", true)

    fun setNotification(state: Boolean) = sp.edit {putBoolean("Notification", state) }

    fun getNotification() = sp.getBoolean("Notification", true)

    fun setAutoNext(state: Boolean) = sp.edit{putBoolean("AutoNext", state)}

    fun getAutoNext() = sp.getBoolean("AutoNext", false)


}

