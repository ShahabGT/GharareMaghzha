package ir.ghararemaghzha.game.classes;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class MySettingsPreference {

    private static MySettingsPreference instance;
    private static SharedPreferences sharedPreferences;




    public static MySettingsPreference getInstance(Context context){
        if (instance==null) {
            instance = new MySettingsPreference(context);
        }

        return instance;
    }

    private MySettingsPreference(Context context){
        try {
            MasterKey masterKey = new MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build();
            sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    "settings_preference",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }
    public void setMusic(boolean state){
        sharedPreferences.edit().putBoolean("music",state).apply();
    }
    public boolean getMusic(){
        return  sharedPreferences.getBoolean("music",true);
    }

    public void setNotification(boolean state){
        sharedPreferences.edit().putBoolean("Notification",state).apply();
    }
    public boolean getNotification(){
        return  sharedPreferences.getBoolean("Notification",true);
    }

    public void setAutoNext(boolean state){
        sharedPreferences.edit().putBoolean("AutoNext",state).apply();
    }
    public boolean getAutoNext(){
        return  sharedPreferences.getBoolean("AutoNext",false);
    }

}

