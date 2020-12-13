package ir.ghararemaghzha.game.classes;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

import io.realm.Realm;
import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.models.MessageModel;


public class MySharedPreference {

    private static MySharedPreference instance;
    private static SharedPreferences sharedPreferences;
    private Context context;


    public static MySharedPreference getInstance(Context context) {
        if (instance == null) {
            instance = new MySharedPreference(context);
        }

        return instance;
    }

    private MySharedPreference(Context context) {
        try {
            this.context=context;
            MasterKey masterKey = new MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build();
            sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    "questions_preference",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

    }

    public void clear() {
        String fbToken = getFbToken();
        int counter = getCounter();
        sharedPreferences.edit().clear().apply();
        setFbToken(fbToken);
        setFirstTime();
        setFirstTimeQuestion();
        setCounter(counter);
    }

    public void setCounter(int counter) {
        sharedPreferences.edit().putInt("counter", counter).apply();
    }

    private int getCounter() {
        return sharedPreferences.getInt("counter", 0);
    }

    public void counterIncrease() {
        int counter = sharedPreferences.getInt("counter", 0);
        if (counter < 299) {
            counter++;
            sharedPreferences.edit().putInt("counter", counter).apply();
            Utils.updateScoreBooster(context, 300 - counter);
        } else
            clearCounter( true);

    }

    public void clearCounter(boolean showNotification) {
        sharedPreferences.edit().putInt("counter", 0).apply();
        Utils.updateScoreBooster(context, 0);
        setBoosterValue(1f);

        if (showNotification) {
            Utils.createNotification(context, context.getString(R.string.booster_notif_title), context.getString(R.string.booster_notif_body), "ir.ghararemaghzha.game.TARGET_NOTIFICATION");
            saveToDB();
        }
    }

    private void saveToDB() {
        MessageModel model = new MessageModel();
        model.setStat(1);
        model.setMessage(context.getString(R.string.booster_notif_body));
        model.setTitle(context.getString(R.string.booster_notif_title));
        model.setSender("admin");
        model.setDate(Utils.currentDate());
        model.setRead(0);
        Realm db = Realm.getDefaultInstance();
        model.setMessageId(Utils.getNextKey(db));
        db.beginTransaction();
        db.insert(model);
        db.commitTransaction();
    }

    public boolean isFirstTime() {
        return sharedPreferences.getBoolean("isFirstTime", true);
    }

    public void setFirstTime() {
        sharedPreferences.edit().putBoolean("isFirstTime", false).apply();
    }

    public boolean isFirstTimeQuestion() {
        return sharedPreferences.getBoolean("isFirstTimeQuestion", true);
    }

    public void setFirstTimeQuestion() {
        sharedPreferences.edit().putBoolean("isFirstTimeQuestion", false).apply();
    }

    public void setAccessToken(String accessToken) {
        sharedPreferences.edit().putString("accessToken", accessToken).apply();
    }

    public String getAccessToken() {
        return sharedPreferences.getString("accessToken", "");
    }

    public void setUsername(String username) {
        sharedPreferences.edit().putString("username", username).apply();
    }

    public String getUsername() {
        return sharedPreferences.getString("username", "");
    }

    public void setNumber(String number) {
        sharedPreferences.edit().putString("number", number).apply();
    }

    public String getNumber() {
        return sharedPreferences.getString("number", "");
    }

    public void setUserId(String userId) {
        sharedPreferences.edit().putString("userId", userId).apply();
    }

    public String getUserId() {
        return sharedPreferences.getString("userId", "");
    }

    public void setUserCode(String userCode) {
        sharedPreferences.edit().putString("userCode", userCode).apply();
    }

    public String getUserCode() {
        return sharedPreferences.getString("userCode", "");
    }

    public void setFbToken(String fbToken) {
        sharedPreferences.edit().putString("fbToken", fbToken).apply();
    }

    public String getFbToken() {
        return sharedPreferences.getString("fbToken", "");
    }

    public void setScore(String score) {
        sharedPreferences.edit().putString("score", score).apply();
    }

    public String getScore() {
        return sharedPreferences.getString("score", "0");
    }

    public void setPlan(String plan) {
        sharedPreferences.edit().putString("plan", plan).apply();
    }

    public String getPlan() {
        return sharedPreferences.getString("plan", "");
    }

    public void setDaysPassed(String daysPassed) {
        sharedPreferences.edit().putString("daysPassed", daysPassed).apply();
    }

    public String getDaysPassed() {
        return sharedPreferences.getString("daysPassed", "-1");
    }

    public void setLastUpdate(int lastUpdate) {
        sharedPreferences.edit().putInt("lastUpdate", lastUpdate).apply();
    }

    public int getLastUpdate() {
        return sharedPreferences.getInt("lastUpdate", -1);
    }

    public void setLastUpdateChat(String lastUpdate) {
        sharedPreferences.edit().putString("lastUpdateChat", lastUpdate).apply();
    }

    public String getLastUpdateChat() {
        return sharedPreferences.getString("lastUpdateChat", "0");
    }

    public void setUserBday(String UserBday) {
        sharedPreferences.edit().putString("UserBday", UserBday).apply();
    }

    public String getUserBday() {
        return sharedPreferences.getString("UserBday", "");
    }

    public void setUserSex(String UserSex) {
        sharedPreferences.edit().putString("UserSex", UserSex).apply();
    }

    public String getUserSex() {
        return sharedPreferences.getString("UserSex", "");
    }

    public void setUserEmail(String Email) {
        sharedPreferences.edit().putString("Email", Email).apply();
    }

    public String getUserEmail() {
        return sharedPreferences.getString("Email", "");
    }

    public void setUserInvite(String invite) {
        sharedPreferences.edit().putString("invite", invite).apply();
    }

    public String getUserInvite() {
        return sharedPreferences.getString("invite", "");
    }

    public void setUserAvatar(String Avatar) {
        sharedPreferences.edit().putString("Avatar", Avatar).apply();
    }

    public String getUserAvatar() {
        return sharedPreferences.getString("Avatar", "");
    }

    public void setUnreadChats(int num) {
        sharedPreferences.edit().putInt("chats", num).apply();
    }

    public int getUnreadChats() {
        return sharedPreferences.getInt("chats", 0);
    }

    public void setBooster(int booster) {
        sharedPreferences.edit().putInt("userbooster", booster).apply();
    }

    public int getBooster() {
        return sharedPreferences.getInt("userbooster", 0);
    }

    public void setBoosterDate(String boosterDate) {
        sharedPreferences.edit().putString("boosterDate", boosterDate).apply();
    }

    public String getBoosterDate() {
        return sharedPreferences.getString("boosterDate", "");
    }

    public void setBoosterValue(float boosterValue) {
        sharedPreferences.edit().putFloat("boosterValue", boosterValue).apply();
    }

    public float getBoosterValue() {
        return sharedPreferences.getFloat("boosterValue", 1f);
    }
}
