package ir.ghararemaghzha.game.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.textview.MaterialTextView;

import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.classes.MySharedPreference;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        ImageView logo = findViewById(R.id.splash_logo);
        MaterialTextView textView = findViewById(R.id.splash_text);


        new Handler().postDelayed(() -> {
            String userId = MySharedPreference.getInstance(this).getUserId();

            if (userId.isEmpty())
                startActivity(new Intent(SplashActivity.this, RegisterActivity.class));
            else
                startActivity(new Intent(SplashActivity.this, MainActivity.class));

            SplashActivity.this.finish();
        }, 500);


    }

    @Override
    public void onBackPressed() {
    }
}