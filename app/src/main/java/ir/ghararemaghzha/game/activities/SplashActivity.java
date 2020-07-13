package ir.ghararemaghzha.game.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.classes.MySharedPreference;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        String userId=MySharedPreference.getInstance(this).getUserId();
        new Handler().postDelayed(()->{
            if(userId.isEmpty())
                startActivity(new Intent(SplashActivity.this,RegisterActivity.class));
            else
                startActivity(new Intent(SplashActivity.this,MainActivity.class));

            SplashActivity.this.finish();

        },1500);
    }

    @Override
    public void onBackPressed() {
    }
}