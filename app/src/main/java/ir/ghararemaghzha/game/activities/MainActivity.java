package ir.ghararemaghzha.game.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textview.MaterialTextView;

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
    public static ImageView newChat, newToolbar;
    private boolean doubleBackToExitPressedOnce;
    private Realm db;
    private GetDataDialog dataDialog;
    private BottomNavigationView bnv;
    private BroadcastReceiver notificationBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateMessages();
        }
    };

    private ImageView avatar;
    private Intent refreshIntent;
    private MotionLayout motionLayout;
    private NavController navController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        init();

    }

    private void setAvatars() {
        Glide.with(this)
                .load(getString(R.string.avatar_url, MySharedPreference.getInstance(this).getUserAvatar()))
                .circleCrop()
                .placeholder(R.drawable.placeholder)
                .into((ImageView) findViewById(R.id.navigation_avatar));

        Glide.with(this)
                .load(getString(R.string.avatar_url, MySharedPreference.getInstance(this).getUserAvatar()))
                .circleCrop()
                .placeholder(R.drawable.placeholder)
                .into(avatar);
    }

    private void navigationDrawer() {
        ((MaterialTextView) findViewById(R.id.navigation_name)).setText(MySharedPreference.getInstance(this).getUsername());
        ((MaterialTextView) findViewById(R.id.navigation_code)).setText(getString(R.string.profile_code, MySharedPreference.getInstance(this).getUserCode()));
        ((MaterialTextView) findViewById(R.id.navigation_score)).setText(getString(R.string.highscore_score, MySharedPreference.getInstance(this).getScore()));

        findViewById(R.id.navigation_exit).setOnClickListener(v -> Utils.logout(this, false));
        findViewById(R.id.navigation_buyhistory).setOnClickListener(v -> {
            startActivity(new Intent(this, BuyHistoryActivity.class));
            motionLayout.transitionToStart();
        });
        findViewById(R.id.navigation_support).setOnClickListener(v -> {
            startActivity(new Intent(this, SupportActivity.class));
            motionLayout.transitionToStart();
        });
        findViewById(R.id.navigation_invite).setOnClickListener(v -> {
            startActivity(new Intent(this, InviteActivity.class));
            motionLayout.transitionToStart();
        });
        findViewById(R.id.navigation_setting).setOnClickListener(v -> {
            navController.navigate(R.id.action_global_settingsFragment);
            motionLayout.transitionToStart();

        });
    }


    private void init() {
        bnv = findViewById(R.id.main_bnv);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.main_container);
        if(navHostFragment!=null)
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(bnv, navController);

        motionLayout = findViewById(R.id.main_motion);
        refreshIntent = new Intent(GHARAREHMAGHZHA_BROADCAST_REFRESH);
        db = Realm.getDefaultInstance();
        doubleBackToExitPressedOnce = false;
        newChat = findViewById(R.id.main_chat_new);
        newToolbar = findViewById(R.id.toolbar_new);
        avatar = findViewById(R.id.toolbar_avatar);
        onClicks();
        uploadAnswers();
        navigationDrawer();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) db.close();
    }

    private void onClicks() {
        avatar.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ProfileActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAvatars();
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
        int size = db.where(MessageModel.class).equalTo("sender", "admin").equalTo("read", 0).findAll().size();
        BadgeDrawable badgeDrawable = bnv.getOrCreateBadge(R.id.menu_message);

        if (size > 0) {
            badgeDrawable.setVisible(true);
        } else {
            badgeDrawable.setVisible(false);
        }
        int chatSize = db.where(MessageModel.class).notEqualTo("sender", "admin").equalTo("read", 0).findAll().size();
        if (chatSize > 0) {
            newChat.setVisibility(View.VISIBLE);
            newToolbar.setVisibility(View.VISIBLE);

        } else {
            newChat.setVisibility(View.GONE);
            newToolbar.setVisibility(View.GONE);
        }
    }

    private void checkTime() {
        String number = MySharedPreference.getInstance(this).getNumber();
        String token = MySharedPreference.getInstance(this).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(MainActivity.this, true);
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

                                int lastUpdate = 0;
                                if (response.body().getLastUpdate() != null && !response.body().getLastUpdate().isEmpty())
                                    lastUpdate = Integer.parseInt(response.body().getLastUpdate());
                                updateDatabase(serverCount, lastUpdate);

                            }
                        } else if (response.code() == 401) {
                            Utils.logout(MainActivity.this, true);
                        } else
                            Utils.showInternetError(MainActivity.this, () -> checkTime());

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
            Utils.logout(MainActivity.this, true);
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
                                dataDialog = Utils.showGetDataLoading(MainActivity.this);
                                getQuestions(String.valueOf(newPlan));
                            }
                            int myVersion = Utils.getVersionCode(MainActivity.this);
                            int newVersion = Integer.parseInt(response.body().getVersion());
                            int newScore = Integer.parseInt(response.body().getScoreCount());
                            int oldScore = Integer.parseInt(MySharedPreference.getInstance(MainActivity.this).getScore());

                            if (newScore > oldScore)
                                MySharedPreference.getInstance(MainActivity.this).setScore(String.valueOf(newScore));
                            else if (oldScore > newScore)
                                uploadScore(String.valueOf(oldScore));

                            if (newVersion > myVersion) {
                                if (response.body().getVersionEssential().equals("1")) {
                                    showNewVersionDialog("1");
                                } else {
                                    showNewVersionDialog("0");
                                }


                            }

                            sendBroadcast(refreshIntent);
                        } else if (response.code() == 401) {
                            Utils.logout(MainActivity.this, true);
                        } else
                            Utils.showInternetError(MainActivity.this, () -> verify());
                    }

                    @Override
                    public void onFailure(@NonNull Call<VerifyResponse> call, @NonNull Throwable t) {
                        Utils.showInternetError(MainActivity.this, () -> verify());

                    }
                });
    }

    private void updateDatabase() {
        Date d = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
        int nowDate = Integer.parseInt(dateFormat.format(d));
        int passed = Integer.parseInt(MySharedPreference.getInstance(this).getDaysPassed());
        int remaining = db.where(QuestionModel.class).equalTo("userAnswer", "-1").findAll().size();
        int range = remaining / (10 - passed);
        if (range > 0) {
            db.executeTransaction(realm -> {
                RealmResults<QuestionModel> questions = realm.where(QuestionModel.class).equalTo("userAnswer", "-1").limit(range).findAll();
                questions.setBoolean("visible", true);
            });
            updateTime(nowDate);
            Utils.updateServerQuestions(this, String.valueOf(db.where(QuestionModel.class).equalTo("visible", true).findAll().size()));
            sendBroadcast(refreshIntent);

        }
    }

    private void updateDatabase(int serverCount, int lastUpdate) {
        int userCount = db.where(QuestionModel.class).equalTo("visible", true).findAll().size();
        Date d = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);

        //   int lastUpdate = MySharedPreference.getInstance(this).getLastUpdate();
        int nowDate = Integer.parseInt(dateFormat.format(d));
        int passed = Integer.parseInt(MySharedPreference.getInstance(this).getDaysPassed());
        if (passed == 9) {
            db.executeTransaction(realm -> {
                RealmResults<QuestionModel> questions = realm.where(QuestionModel.class).equalTo("userAnswer", "-1").findAll();
                questions.setBoolean("visible", true);
            });
            updateTime(nowDate);
            Utils.updateServerQuestions(this, String.valueOf(db.where(QuestionModel.class).equalTo("visible", true).findAll().size()));
            sendBroadcast(refreshIntent);
            //   MySharedPreference.getInstance(this).setLastUpdate(lastUpdate);
        } else if (passed >= 0 && nowDate > lastUpdate && passed < 10) {
            int remaining = db.where(QuestionModel.class).equalTo("userAnswer", "-1").findAll().size();
            int range = remaining / (10 - passed);
            if (range > 0) {
                db.executeTransaction(realm -> {
                    RealmResults<QuestionModel> questions = realm.where(QuestionModel.class).equalTo("userAnswer", "-1").limit(range).findAll();
                    questions.setBoolean("visible", true);
                });
                updateTime(nowDate);
                Utils.updateServerQuestions(this, String.valueOf(db.where(QuestionModel.class).equalTo("visible", true).findAll().size()));
                sendBroadcast(refreshIntent);
                //      MySharedPreference.getInstance(this).setLastUpdate(lastUpdate);

            }
        } else if (nowDate == lastUpdate && serverCount > userCount) {
            db.executeTransaction(realm -> {
                RealmResults<QuestionModel> questions = realm.where(QuestionModel.class).equalTo("userAnswer", "-1").limit(serverCount).findAll();
                questions.setBoolean("visible", true);
            });
            sendBroadcast(refreshIntent);
            //   MySharedPreference.getInstance(this).setLastUpdate(lastUpdate);
        }
    }

    private void updateTime(int lastUpdate) {
        String number = MySharedPreference.getInstance(this).getNumber();
        String token = MySharedPreference.getInstance(this).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(MainActivity.this, true);
            return;
        }
        RetrofitClient.getInstance().getApi()
                .updateLastUpdate("Bearer " + token, number, String.valueOf(lastUpdate))
                .enqueue(new Callback<GeneralResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                        if (response.code() == 401)
                            Utils.logout(MainActivity.this, true);
                        else if (!response.isSuccessful())
                            Utils.showInternetError(MainActivity.this, () -> updateTime(lastUpdate));

                    }

                    @Override
                    public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                        Utils.showInternetError(MainActivity.this, () -> updateTime(lastUpdate));

                    }
                });
    }

    private void getQuestions(String newPlan) {
        String number = MySharedPreference.getInstance(this).getNumber();
        String token = MySharedPreference.getInstance(this).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(MainActivity.this, true);
            return;
        }
        int questions = db.where(QuestionModel.class).findAll().size();  //3
        int plan = Integer.parseInt(newPlan);  //5
        int size = (plan * 1000) + 100;
        RetrofitClient.getInstance().getApi()
                .getQuestions("Bearer " + token, number, String.valueOf(questions), String.valueOf(size))
                .enqueue(new Callback<QuestionResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<QuestionResponse> call, @NonNull Response<QuestionResponse> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().getMessage().equals("empty")) {
                            for (QuestionModel model : response.body().getData()) {
                                if (model.getUserAnswer().equals("-1"))
                                    model.setUploaded(false);
                                else
                                    model.setUploaded(true);
                                model.setVisible(false);
                                db.executeTransaction(realm1 -> realm1.insertOrUpdate(model));
                            }
                            updateDatabase();
                            MySharedPreference.getInstance(MainActivity.this).setPlan(newPlan);
                            sendBroadcast(refreshIntent);
                            if (dataDialog != null) dataDialog.dismiss();
                        } else if (response.code() == 401) {
                            if (dataDialog != null) dataDialog.dismiss();
                            Utils.logout(MainActivity.this, true);
                        } else {
                            if (dataDialog != null) dataDialog.dismiss();
                            Utils.showInternetError(MainActivity.this, () -> getQuestions(newPlan));
                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<QuestionResponse> call, @NonNull Throwable t) {
                        if (dataDialog != null) dataDialog.dismiss();
                        Utils.showInternetError(MainActivity.this, () -> getQuestions(newPlan));
                    }
                });
    }

    private void uploadScore(String score) {
        String number = MySharedPreference.getInstance(this).getNumber();
        String token = MySharedPreference.getInstance(this).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(MainActivity.this, true);
            return;
        }
        RetrofitClient.getInstance().getApi()
                .sendScore("Bearer " + token, number, score)
                .enqueue(new Callback<GeneralResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                        if (response.code() == 401) {
                            Utils.logout(MainActivity.this, true);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {

                    }
                });
    }

    private void uploadAnswers() {
        RealmResults<QuestionModel> models = db.where(QuestionModel.class).equalTo("visible", false).notEqualTo("userAnswer", "-1").equalTo("uploaded", false).findAll();
        for (QuestionModel model : models)
            uploadAnswer(model.getQuestionId(), model.getUserAnswer());
    }

    private void uploadAnswer(String questionId, String userAnswer) {

        String number = MySharedPreference.getInstance(this).getNumber();
        String token = MySharedPreference.getInstance(this).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(MainActivity.this, true);
            return;
        }
        RetrofitClient.getInstance().getApi()
                .answerQuestion("Bearer " + token, number, questionId, userAnswer)
                .enqueue(new Callback<GeneralResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getResult().equals("success")) {
                            db.beginTransaction();
                            RealmResults<QuestionModel> result = db.where(QuestionModel.class).equalTo("questionId", questionId).findAll();
                            Objects.requireNonNull(result.first()).setUploaded(true);
                            db.commitTransaction();

                        } else if (response.code() == 401) {
                            Utils.logout(MainActivity.this, true);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                    }
                });
    }

    @Override
    public void onBackPressed() {

        if (motionLayout.getCurrentState() == motionLayout.getEndState()) {
            motionLayout.transitionToStart();
            return;
        }
        if (navController.popBackStack())
            return;
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.general_exit), Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    private void showNewVersionDialog(String urgent) {
        NewVersionDialog dialog = new NewVersionDialog(this);
        if (urgent.equals("0")) {
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
        } else {
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