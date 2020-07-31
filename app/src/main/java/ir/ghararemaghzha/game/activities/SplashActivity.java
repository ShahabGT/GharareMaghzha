package ir.ghararemaghzha.game.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.classes.MySharedPreference;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        String userId = MySharedPreference.getInstance(this).getUserId();

        if (userId.isEmpty())
            startActivity(new Intent(SplashActivity.this, RegisterActivity.class));
        else
            startActivity(new Intent(SplashActivity.this, MainActivity.class));

        SplashActivity.this.finish();

    }

    @Override
    public void onBackPressed() {
    }
}