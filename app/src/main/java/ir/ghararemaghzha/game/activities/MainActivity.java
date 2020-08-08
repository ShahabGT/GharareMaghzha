package ir.ghararemaghzha.game.activities;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmResults;
import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.classes.MySharedPreference;
import ir.ghararemaghzha.game.classes.Utils;
import ir.ghararemaghzha.game.data.RetrofitClient;
import ir.ghararemaghzha.game.dialogs.GetDataDialog;
import ir.ghararemaghzha.game.dialogs.NewVersionDialog;
import ir.ghararemaghzha.game.dialogs.TimeDialog;
import ir.ghararemaghzha.game.fragments.BuyFragment;
import ir.ghararemaghzha.game.fragments.HighscoreFragment;
import ir.ghararemaghzha.game.fragments.MessagesFragment;
import ir.ghararemaghzha.game.fragments.ProfileFragment;
import ir.ghararemaghzha.game.fragments.StartFragment;
import ir.ghararemaghzha.game.models.GeneralResponse;
import ir.ghararemaghzha.game.models.MessageModel;
import ir.ghararemaghzha.game.models.QuestionModel;
import ir.ghararemaghzha.game.models.QuestionResponse;
import ir.ghararemaghzha.game.models.TimeResponse;
import ir.ghararemaghzha.game.models.VerifyResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ir.ghararemaghzha.game.classes.Const.GHARAREHMAGHZHA_BROADCAST;
import static ir.ghararemaghzha.game.classes.Const.GHARAREHMAGHZHA_BROADCAST_REFRESH;

public class MainActivity extends AppCompatActivity {

    private TimeDialog timeDialog;
    @SuppressLint("StaticFieldLeak")
    public static ImageView profile, messages, highscore, buy, start, newMessage;
    public static int whichFragment = 1;
    private boolean doubleBackToExitPressedOnce;
    private Realm db;
    private GetDataDialog dataDialog;
    private BroadcastReceiver notificationBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateMessages();
        }
    };

    private ImageView avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);
        init();
        findViewById(R.id.main_menu).setOnClickListener(v -> Utils.logout(this));


    }

    private void animate() {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(start, "scaleX", 1f, 1.1f, 1f);
        scaleX.setDuration(1500);
        scaleX.setRepeatCount(ValueAnimator.INFINITE);
        scaleX.setRepeatMode(ValueAnimator.RESTART);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(start, "scaleY", 1f, 1.1f, 1f);
        scaleY.setDuration(1500);
        scaleY.setRepeatCount(ValueAnimator.INFINITE);
        scaleY.setRepeatMode(ValueAnimator.RESTART);
        scaleX.start();
        scaleY.start();
    }

    private void init() {
        db = Realm.getDefaultInstance();
        doubleBackToExitPressedOnce = false;
        profile = findViewById(R.id.main_profile);
        messages = findViewById(R.id.main_messages);
        newMessage = findViewById(R.id.main_messages_new);
        highscore = findViewById(R.id.main_highscore);
        avatar = findViewById(R.id.main_avatar);
        Glide.with(this)
                .load(getString(R.string.avatar_url,MySharedPreference.getInstance(this).getUserId()))
                .circleCrop()
                .placeholder(R.drawable.placeholder)
                .into(avatar);
       // avatar.setImageURI(Uri.parse(getString(R.string.avatar_url,MySharedPreference.getInstance(this).getUserId())));
        buy = findViewById(R.id.main_buy);
        start = findViewById(R.id.main_start);
        ImageViewCompat.setImageTintList(profile, ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark)));
        changeFragment(new ProfileFragment());

        updateDatabase(0);
        animate();
        onClicks();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        whichFragment=1;
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
        start.setOnClickListener(v -> {
            if (whichFragment != 5) {
                ImageViewCompat.setImageTintList(buy, ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.black)));
                ImageViewCompat.setImageTintList(profile, ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.black)));
                ImageViewCompat.setImageTintList(messages, ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.black)));
                ImageViewCompat.setImageTintList(highscore, ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.black)));
                changeFragment(new StartFragment());
                whichFragment = 5;
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
        Utils.removeNotification(this);
        registerReceiver(notificationBroadCast, new IntentFilter(GHARAREHMAGHZHA_BROADCAST));
        updateMessages();
        checkTime();
    }


    @Override
    protected void onStop() {
        unregisterReceiver(notificationBroadCast);
        super.onStop();
    }

    private void updateMessages() {
        if (db.where(MessageModel.class).equalTo("read", 0).findAll().size() > 0) {
            newMessage.setVisibility(View.VISIBLE);
        } else {
            newMessage.setVisibility(View.GONE);
        }
    }

    private void checkTime() {
        String number = MySharedPreference.getInstance(this).getNumber();
        String token = MySharedPreference.getInstance(this).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(MainActivity.this);
            return;
        }
        if (timeDialog != null)
            timeDialog.dismiss();
        RetrofitClient.getInstance().getApi()
                .getServerTime("Bearer " + token, number)
                .enqueue(new Callback<TimeResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<TimeResponse> call, @NonNull Response<TimeResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getResult().equals("success")) {
                            verify();


                            int serverCount = Integer.parseInt(response.body().getUserQuestions());

                            if (!Utils.isTimeAcceptable(response.body().getTime())) {
                                timeDialog = Utils.showTimeError(MainActivity.this);
                            } else {
                                MySharedPreference.getInstance(MainActivity.this).setDaysPassed(response.body().getPassed());
                                if (response.body().getLastUpdate() != null && !response.body().getLastUpdate().isEmpty()) {
                                    int lastUpdate = Integer.parseInt(response.body().getLastUpdate());
                                    MySharedPreference.getInstance(MainActivity.this).setLastUpdate(lastUpdate);
                                }
                                updateDatabase(serverCount);

                            }
                        } else if (response.code() == 401) {
                            Utils.logout(MainActivity.this);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<TimeResponse> call, @NonNull Throwable t) {
                        Utils.showInternetError(MainActivity.this, () -> checkTime());
                    }
                });

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
                                MySharedPreference.getInstance(MainActivity.this).setPlan(String.valueOf(newPlan));
                                dataDialog = Utils.showGetDataLoading(MainActivity.this);
                                getQuestions();
                            }
                            int myVersion = Utils.getVersionCode(MainActivity.this);
                            int newVersion = Integer.parseInt(response.body().getVersion());
                            int newScore = Integer.parseInt(response.body().getScoreCount());
                            int oldScore = Integer.parseInt(MySharedPreference.getInstance(MainActivity.this).getScore());

                            if(newScore>oldScore)
                                MySharedPreference.getInstance(MainActivity.this).setScore(String.valueOf(newScore));
                            else if(oldScore>newScore)
                                uploadScore(String.valueOf(oldScore));

                            if (newVersion > myVersion) {
                                if (response.body().getVersionEssential().equals("1")) {
                                    showNewVersionDialog("1");
                                } else {
                                    showNewVersionDialog("0");
                                }


                            }
                            Intent i = new Intent(GHARAREHMAGHZHA_BROADCAST_REFRESH);
                            sendBroadcast(i);
                        } else if (response.code() == 401) {
                            Utils.logout(MainActivity.this);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<VerifyResponse> call, @NonNull Throwable t) {
                        Utils.showInternetError(MainActivity.this, () -> verify());

                    }
                });
    }


    private void updateDatabase(int serverCount) {
        int userCount = db.where(QuestionModel.class).equalTo("visible", true).findAll().size();
        Date d = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);

        int lastUpdate = MySharedPreference.getInstance(this).getLastUpdate();
        int nowDate = Integer.parseInt(dateFormat.format(d));
        int passed = Integer.parseInt(MySharedPreference.getInstance(this).getDaysPassed());
        if (passed == 9) {
            db.executeTransaction(realm -> {
                RealmResults<QuestionModel> questions = realm.where(QuestionModel.class).equalTo("userAnswer", "-1").findAll();
                questions.setBoolean("visible", true);
            });
            updateTime(nowDate);
            Utils.updateServerQuestions(this,String.valueOf(db.where(QuestionModel.class).equalTo("visible", true).findAll().size()));

        } else if (passed >= 0 && nowDate > lastUpdate && passed < 10) {
            int remaining = db.where(QuestionModel.class).equalTo("userAnswer", "-1").findAll().size();
            int range = remaining / (10 - passed);
            if (range > 0) {
                db.executeTransaction(realm -> {
                    RealmResults<QuestionModel> questions = realm.where(QuestionModel.class).equalTo("userAnswer", "-1").limit(range).findAll();
                    questions.setBoolean("visible", true);
                });
                updateTime(nowDate);
                Utils.updateServerQuestions(this,String.valueOf(db.where(QuestionModel.class).equalTo("visible", true).findAll().size()));
            }
        }else if (nowDate == lastUpdate && serverCount>userCount){
            db.executeTransaction(realm -> {
                RealmResults<QuestionModel> questions = realm.where(QuestionModel.class).equalTo("userAnswer", "-1").limit(serverCount).findAll();
                questions.setBoolean("visible", true);
            });
        }
    }

    private void updateTime(int lastUpdate) {
        String number = MySharedPreference.getInstance(this).getNumber();
        String token = MySharedPreference.getInstance(this).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(MainActivity.this);
            return;
        }
        RetrofitClient.getInstance().getApi()
                .updateLastUpdate("Bearer " + token, number, String.valueOf(lastUpdate))
                .enqueue(new Callback<GeneralResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getResult().equals("success")) {
                            MySharedPreference.getInstance(MainActivity.this).setLastUpdate(lastUpdate);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {

                    }
                });
    }

    private void getQuestions() {
        String number = MySharedPreference.getInstance(this).getNumber();
        String token = MySharedPreference.getInstance(this).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(MainActivity.this);
            return;
        }
        int questions = db.where(QuestionModel.class).findAll().size();  //3
        int plan = Integer.parseInt(MySharedPreference.getInstance(this).getPlan());  //5
        int start = questions + 100;
        int size = (plan * 1000) + 100;
        if (questions == 0) {
            RetrofitClient.getInstance().getApi()
                    .getQuestions("Bearer " + token, number, String.valueOf(start), String.valueOf(size))
                    .enqueue(new Callback<QuestionResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<QuestionResponse> call, @NonNull Response<QuestionResponse> response) {
                            if(dataDialog!=null) dataDialog.dismiss();
                            if (response.isSuccessful() && response.body() != null && !response.body().getMessage().equals("empty")) {
                                MySharedPreference.getInstance(MainActivity.this).setGotQuestions();
                                for (QuestionModel model : response.body().getData()) {
                                    if (model.getUserAnswer().equals("-1"))
                                        model.setUploaded(false);
                                    else
                                        model.setUploaded(true);
                                    model.setVisible(false);
                                    db.executeTransaction(realm1 -> realm1.insertOrUpdate(model));
                                    updateDatabase(0);
                                }
                            } else if (response.code() == 401) {
                                Utils.logout(MainActivity.this);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<QuestionResponse> call, @NonNull Throwable t) {
                            if(dataDialog!=null) dataDialog.dismiss();
                            Utils.showInternetError(MainActivity.this, () -> getQuestions());
                        }
                    });

        }
    }

    private void uploadScore(String score){
        String number = MySharedPreference.getInstance(this).getNumber();
        String token = MySharedPreference.getInstance(this).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(MainActivity.this);
            return;
        }
        RetrofitClient.getInstance().getApi()
                .sendScore("Bearer "+token,number,score)
                .enqueue(new Callback<GeneralResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GeneralResponse> call,@NonNull  Response<GeneralResponse> response) {
                        if (response.code() == 401) {
                            Utils.logout(MainActivity.this);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<GeneralResponse> call,@NonNull  Throwable t) {

                    }
                });
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

    private void showNewVersionDialog(String urgent) {

        NewVersionDialog dialog = new NewVersionDialog(this, urgent);
//        if (urgent.equals("0"))
//            newVersionDialog.setCancelable(true);
//        else
//            newVersionDialog.setCancelable(false);
//        Objects.requireNonNull(newVersionDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        newVersionDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//
//        newVersionDialog.show();
//        Window window = newVersionDialog.getWindow();
//        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        if(urgent.equals("0")){
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
        }else{
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }
}