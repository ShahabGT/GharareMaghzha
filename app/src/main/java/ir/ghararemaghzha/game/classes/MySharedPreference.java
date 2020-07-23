package ir.ghararemaghzha.game.classes;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import java.io.IOException;
import java.security.GeneralSecurityException;


public class MySharedPreference {

    private static MySharedPreference instance;
    private static SharedPreferences sharedPreferences;


    public static MySharedPreference getInstance(Context context){
        if (instance==null) {
            instance = new MySharedPreference(context);
        }

        return instance;
    }

    private MySharedPreference(Context context){
        try {
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

    public void clear(){
        sharedPreferences.edit().clear().apply();
    }

    public void setAccessToken(String accessToken){
        sharedPreferences.edit().putString("accessToken",accessToken).apply();
    }
    public String getAccessToken(){
        return  sharedPreferences.getString("accessToken","");
    }

    public void setUsername(String username){
        sharedPreferences.edit().putString("username",username).apply();
    }
    public String getUsername(){
        return  sharedPreferences.getString("username","");
    }

    public void setNumber(String number){
        sharedPreferences.edit().putString("number",number).apply();
    }
    public String getNumber(){
        return  sharedPreferences.getString("number","");
    }

    public void setUserId(String userId){
        sharedPreferences.edit().putString("userId",userId).apply();
    }
    public String getUserId(){
        return  sharedPreferences.getString("userId","");
    }

    public void setUserCode(String userCode){
        sharedPreferences.edit().putString("userCode",userCode).apply();
    }
    public String getUserCode(){
        return  sharedPreferences.getString("userCode","");
    }

    public void setFbToken(String fbToken){
        sharedPreferences.edit().putString("fbToken",fbToken).apply();
    }
    public String getFbToken(){
        return  sharedPreferences.getString("fbToken","");
    }

    public void setScore(String score){
        sharedPreferences.edit().putString("score",score).apply();
    }
    public String getScore(){
        return  sharedPreferences.getString("score","0");
    }

    public void setPlan(String plan){
        sharedPreferences.edit().putString("plan",plan).apply();
    }
    public String getPlan(){
        return  sharedPreferences.getString("plan","");
    }

    public void setDaysPassed(String daysPassed){
        sharedPreferences.edit().putString("daysPassed",daysPassed).apply();
    }
    public String getDaysPassed(){
        return  sharedPreferences.getString("daysPassed","-1");
    }

    public void setLastUpdate(int lastUpdate){
        sharedPreferences.edit().putInt("lastUpdate",lastUpdate).apply();
    }
    public int getLastUpdate(){
        return  sharedPreferences.getInt("lastUpdate",0);
    }

    public void setGotQuestions(){
        sharedPreferences.edit().putBoolean("GotQuestions",true).apply();
    }
    public boolean getGotQuestions(){
        return  sharedPreferences.getBoolean("GotQuestions",false);
    }
}
