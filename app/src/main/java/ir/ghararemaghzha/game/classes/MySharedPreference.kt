package ir.ghararemaghzha.game.classes

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import io.realm.Realm
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.models.MessageModel

class MySharedPreference private constructor(ctx: Context) {
    private val context = ctx
    private val masterKey = MasterKey.Builder(ctx).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
    private val sp = EncryptedSharedPreferences.create(
            ctx,
            "questions_preference",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)


    companion object {
        @Volatile
        private var instance: MySharedPreference? = null
        fun getInstance(ctx: Context): MySharedPreference =
                instance ?: synchronized(this) {
                    instance ?: MySharedPreference(ctx).also { instance = it }

                }
    }

    fun clear() {
        val fbToken: String = getFbToken()
        val counter: Int = getCounter()
        sp.edit { clear() }
        setFbToken(fbToken)
        setFirstTime()
        setFirstTimeQuestion()
        setCounter(counter)
    }

    fun setCounter(counter: Int) = sp.edit { putInt("counter", counter) }


    private fun getCounter(): Int = sp.getInt("counter", 0)

    fun counterIncrease() {
        var counter = sp.getInt("counter", 0)
        if (counter < 299) {
            counter++
            sp.edit(commit = true) {putInt("counter", counter) }
            Utils.updateScoreBooster(context, 300 - counter)
        } else clearCounter(true)
    }

    fun clearCounter(showNotification: Boolean) {
        sp.edit { putInt("counter", 0) }
        Utils.updateScoreBooster(context, 0)
        setBoosterValue(1f)
        if (showNotification) {
            Utils.createNotification(context, context.getString(R.string.booster_notif_title), context.getString(R.string.booster_notif_body), "ir.ghararemaghzha.game.TARGET_NOTIFICATION")
            saveToDB()
        }
    }

    private fun saveToDB() {
        val model = MessageModel()
        model.stat = 1
        model.message = context.getString(R.string.booster_notif_body)
        model.title = context.getString(R.string.booster_notif_title)
        model.sender = "admin"
        model.date = Utils.currentDate()
        model.read = 0
        val db = Realm.getDefaultInstance()
        model.messageId = Utils.getNextKey(db)
        db.beginTransaction()
        db.insert(model)
        db.commitTransaction()
    }

    fun isFirstTime() = sp.getBoolean("isFirstTime", true)
    fun setFirstTime() = sp.edit { putBoolean("isFirstTime", false) }

    fun isFirstTimeQuestion() = sp.getBoolean("isFirstTimeQuestion", true)
    fun setFirstTimeQuestion() = sp.edit { putBoolean("isFirstTimeQuestion", false) }

    fun setAccessToken(accessToken: String) = sp.edit { putString("accessToken", accessToken) }
    fun getAccessToken() = sp.getString("accessToken", "") ?: ""

    fun setUsername(username: String) = sp.edit { putString("username", username) }
    fun getUsername() = sp.getString("username", "") ?: ""

    fun setNumber(number: String) = sp.edit { putString("number", number) }
    fun getNumber() = sp.getString("number", "") ?: ""

    fun setUserId(userId: String) = sp.edit { putString("userId", userId) }
    fun getUserId() = sp.getString("userId", "") ?: ""

    fun setUserCode(userCode: String) = sp.edit { putString("userCode", userCode) }
    fun getUserCode() = sp.getString("userCode", "") ?: ""

    fun setFbToken(fbToken: String) = sp.edit { putString("fbToken", fbToken) }
    fun getFbToken() = sp.getString("fbToken", "") ?: ""

    fun setScore(score: String) = sp.edit { putString("score", score) }
    fun getScore() = sp.getString("score", "0") ?: "0"

    fun setPlan(plan: Int) = sp.edit { putInt("plan", plan) }
    fun getPlan() = sp.getInt("plan", 0)

    fun setDaysPassed(daysPassed: Int) = sp.edit { putInt("daysPassed", daysPassed) }
    fun getDaysPassed() = sp.getInt("daysPassed", -1)

    fun setLastUpdate(lastUpdate: Int) = sp.edit { putInt("lastUpdate", lastUpdate) }
    fun getLastUpdate() = sp.getInt("lastUpdate", -1)

    fun setLastUpdateChat(lastUpdate: String) = sp.edit { putString("lastUpdateChat", lastUpdate) }
    fun getLastUpdateChat() = sp.getString("lastUpdateChat", "0") ?: "0"

    fun setUserBirthday(userBirthday: String) = sp.edit { putString("UserBday", userBirthday) }
    fun getUserBirthday() = sp.getString("UserBday", "") ?: ""

    fun setUserSex(UserSex: String) = sp.edit { putString("UserSex", UserSex) }
    fun getUserSex() = sp.getString("UserSex", "") ?: ""

    fun setUserEmail(Email: String) = sp.edit { putString("Email", Email) }
    fun getUserEmail() = sp.getString("Email", "") ?: ""

    fun setUserInvite(invite: String) = sp.edit { putString("invite", invite) }
    fun getUserInvite() = sp.getString("invite", "") ?: ""

    fun setUserAvatar(Avatar: String) = sp.edit { putString("Avatar", Avatar) }
    fun getUserAvatar() = sp.getString("Avatar", "") ?: ""

    fun setUnreadChats(num: Int) = sp.edit { putInt("chats", num) }
    fun getUnreadChats() = sp.getInt("chats", 0)

    fun setBooster(booster: Int) = sp.edit { putInt("userbooster", booster) }
    fun getBooster() = sp.getInt("userbooster", 0)

    fun setBoosterValue(boosterValue: Float) = sp.edit { putFloat("boosterValue", boosterValue) }
    fun getBoosterValue() = sp.getFloat("boosterValue", 1f)


}