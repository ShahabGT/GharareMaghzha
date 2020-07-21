package ir.ghararemaghzha.game.activities;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;

import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.classes.MySharedPreference;
import ir.ghararemaghzha.game.classes.Utils;
import ir.ghararemaghzha.game.data.RetrofitClient;
import ir.ghararemaghzha.game.dialogs.TimeDialog;
import ir.ghararemaghzha.game.fragments.BuyFragment;
import ir.ghararemaghzha.game.fragments.HighscoreFragment;
import ir.ghararemaghzha.game.fragments.MessagesFragment;
import ir.ghararemaghzha.game.fragments.ProfileFragment;
import ir.ghararemaghzha.game.models.TimeResponse;
import ir.ghararemaghzha.game.models.VerifyResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TimeDialog timeDialog;
    private ImageView profile, messages, highscore, buy;
    private int whichFragment = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        init();

    }

    private void init() {
        profile = findViewById(R.id.main_profile);
        messages = findViewById(R.id.main_messages);
        highscore = findViewById(R.id.main_highscore);
        buy = findViewById(R.id.main_buy);
        ImageViewCompat.setImageTintList(profile, ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark)));
        changeFragment(new ProfileFragment());

        onClicks();
    }

    private void onClicks() {
        profile.setOnClickListener(v -> {
            if (whichFragment != 1) {
                ImageViewCompat.setImageTintList(profile, ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark)));
                ImageViewCompat.setImageTintList(messages, ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.black)));
                ImageViewCompat.setImageTintList(highscore, ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.black)));
                ImageViewCompat.setImageTintList(buy, ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.black)));
                changeFragment(new ProfileFragment());
                whichFragment = 1;
            }


        });

        messages.setOnClickListener(v -> {
            if (whichFragment != 2) {
                ImageViewCompat.setImageTintList(messages, ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark)));
                ImageViewCompat.setImageTintList(profile, ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.black)));
                ImageViewCompat.setImageTintList(highscore, ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.black)));
                ImageViewCompat.setImageTintList(buy, ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.black)));
                changeFragment(new MessagesFragment());
                whichFragment = 2;
            }
        });
        highscore.setOnClickListener(v -> {
            if (whichFragment != 3) {
                ImageViewCompat.setImageTintList(highscore, ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark)));
                ImageViewCompat.setImageTintList(profile, ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.black)));
                ImageViewCompat.setImageTintList(messages, ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.black)));
                ImageViewCompat.setImageTintList(buy, ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.black)));
                changeFragment(new HighscoreFragment());
                whichFragment = 3;
            }
        });
        buy.setOnClickListener(v -> {
            if (whichFragment != 4) {
                ImageViewCompat.setImageTintList(buy, ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark)));
                ImageViewCompat.setImageTintList(profile, ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.black)));
                ImageViewCompat.setImageTintList(messages, ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.black)));
                ImageViewCompat.setImageTintList(highscore, ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.black)));
                changeFragment(new BuyFragment());
                whichFragment = 4;
            }
        });


    }

    private void changeFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                .replace(R.id.main_container, fragment)
                .commit();
    }


    @Override
    protected void onResume() {
        super.onResume();
        checkTime();
        verify();
    }

    private void verify() {
        String number = MySharedPreference.getInstance(this).getNumber();
        String token = MySharedPreference.getInstance(this).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(MainActivity.this);
            return;
        }

        RetrofitClient.getInstance().getApi()
                .verify("Bearer " + token, number)
                .enqueue(new Callback<VerifyResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<VerifyResponse> call, @NonNull Response<VerifyResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getResult().equals("success")) {
                            int myVersion = Utils.getVersionCode(MainActivity.this);
                            int newVersion = Integer.parseInt(response.body().getVersion());
                            if (newVersion > myVersion) {
                                if (response.body().getVersionEssential().equals("1")) {
                                    Toast.makeText(MainActivity.this, "version " + newVersion + " available ESSENTIAL", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "version " + newVersion + " available", Toast.LENGTH_SHORT).show();

                                }


                            }
                        } else if (response.code() == 401) {
                            Utils.logout(MainActivity.this);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<VerifyResponse> call, @NonNull Throwable t) {

                    }
                });
    }

    private void checkTime() {
        if (timeDialog != null)
            timeDialog.dismiss();
        RetrofitClient.getInstance().getApi()
                .getServerTime()
                .enqueue(new Callback<TimeResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<TimeResponse> call, @NonNull Response<TimeResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getResult().equals("success")) {
                            if (!Utils.isTimeAcceptable(response.body().getTime()))
                                timeDialog = Utils.showTimeError(MainActivity.this);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<TimeResponse> call, @NonNull Throwable t) {

                    }
                });

    }
}