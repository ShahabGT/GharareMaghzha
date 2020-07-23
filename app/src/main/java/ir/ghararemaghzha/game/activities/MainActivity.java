package ir.ghararemaghzha.game.activities;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.classes.MySharedPreference;
import ir.ghararemaghzha.game.classes.Utils;
import ir.ghararemaghzha.game.data.RetrofitClient;
import ir.ghararemaghzha.game.dialogs.TimeDialog;
import ir.ghararemaghzha.game.fragments.BuyFragment;
import ir.ghararemaghzha.game.fragments.HighscoreFragment;
import ir.ghararemaghzha.game.fragments.MessagesFragment;
import ir.ghararemaghzha.game.fragments.ProfileFragment;
import ir.ghararemaghzha.game.models.QuestionModel;
import ir.ghararemaghzha.game.models.QuestionResponse;
import ir.ghararemaghzha.game.models.TimeResponse;
import ir.ghararemaghzha.game.models.VerifyResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TimeDialog timeDialog;
    @SuppressLint("StaticFieldLeak")
    public static ImageView profile, messages, highscore, buy;
    public static int whichFragment = 1;
    private boolean doubleBackToExitPressedOnce;
    private Realm db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.main_menu).setOnClickListener(v->Utils.logout(this));


        init();

    }

    private void init() {
        db = Realm.getDefaultInstance();
        doubleBackToExitPressedOnce = false;
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
                            int newPlan = Integer.parseInt(response.body().getUserPlan());
                            int oldPlan = Integer.parseInt(MySharedPreference.getInstance(MainActivity.this).getPlan());
                            if (newPlan > oldPlan) {
                                getQuestions();
                            }
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
                            if (!Utils.isTimeAcceptable(response.body().getTime())) {
                                timeDialog = Utils.showTimeError(MainActivity.this);
                            } else {
                                MySharedPreference.getInstance(MainActivity.this).setDaysPassed(response.body().getPassed());
                                if (!response.body().getPassed().equals("10")) {
                                    updateDatabase();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<TimeResponse> call, @NonNull Throwable t) {

                    }
                });

    }

    private void updateDatabase() {
        Date d = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);

        int lastUpdate = MySharedPreference.getInstance(this).getLastUpdate();
        int nowDate = Integer.parseInt(dateFormat.format(d));
        int passed = Integer.parseInt(MySharedPreference.getInstance(this).getDaysPassed());

        if (passed == 0 && nowDate > lastUpdate) {
            // TODO: 7/21/2020 make more questions visible
            //   int remaining =  get from database
            // int range = remaining/(10-passed);
            // alter database to show range
            MySharedPreference.getInstance(this).setLastUpdate(nowDate);
        }


    }

    private void getQuestions() {
        String number = MySharedPreference.getInstance(this).getNumber();
        String token = MySharedPreference.getInstance(this).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(MainActivity.this);
            return;
        }
        int questions = db.where(QuestionModel.class).findAll().size();
        int plan = Integer.parseInt(MySharedPreference.getInstance(this).getPlan());
        int start = 0;
        int size = 0;
        if (questions == 0) {
            RetrofitClient.getInstance().getApi()
                    .getQuestions("Bearer " + token, number, String.valueOf(start), String.valueOf(size))
                    .enqueue(new Callback<QuestionResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<QuestionResponse> call, @NonNull Response<QuestionResponse> response) {
                            if (response.isSuccessful() && response.body() != null && !response.body().getMessage().equals("empty")) {
                                MySharedPreference.getInstance(MainActivity.this).setGotQuestions();
                                for (QuestionModel model : response.body().getData()) {
                                    if (model.getUserAnswer().equals("-1"))
                                        model.setUploaded(false);
                                    else
                                        model.setUploaded(true);
                                    db.executeTransaction(realm1 -> realm1.insertOrUpdate(model));
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<QuestionResponse> call, @NonNull Throwable t) {

                        }
                    });

        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity();
            return;
        }
        doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.general_exit), Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }
}