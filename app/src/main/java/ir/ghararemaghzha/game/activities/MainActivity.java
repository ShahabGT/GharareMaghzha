package ir.ghararemaghzha.game.activities;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.analytics.FirebaseAnalytics;
import java.util.ArrayList;
import java.util.Objects;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.classes.MySharedPreference;
import ir.ghararemaghzha.game.classes.Utils;
import ir.ghararemaghzha.game.data.RetrofitClient;
import ir.ghararemaghzha.game.dialogs.GetDataDialog;
import ir.ghararemaghzha.game.dialogs.NewVersionDialog;
import ir.ghararemaghzha.game.dialogs.RulesDialog;
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

    private FirebaseAnalytics mFirebaseAnalytics;


    private TimeDialog timeDialog;
    private ImageView newChat, newToolbar;
    private boolean doubleBackToExitPressedOnce;
    private Realm db;
    private GetDataDialog dataDialog;
    private BottomNavigationView bnv;
    private final BroadcastReceiver notificationBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateMessages();
            Utils.removeNotification(MainActivity.this);
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
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        db = Realm.getDefaultInstance();

        init();
        if (MySharedPreference.getInstance(this).isFirstTime())
            helpInfo();
        else
            firebaseDebug("ok");


    }

    private void getData() {
        GetDataDialog dialog = Utils.showGetDataLoading(MainActivity.this);
        String number = MySharedPreference.getInstance(this).getNumber();
        String token = MySharedPreference.getInstance(this).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(MainActivity.this, true);
            return;
        }
        RetrofitClient.getInstance().getApi()
                .getQuestions("Bearer " + token, number, "6000", "3000")
                .enqueue(new Callback<QuestionResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<QuestionResponse> call, @NonNull Response<QuestionResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String userPlan = MySharedPreference.getInstance(MainActivity.this).getPlan();
                            int size = 500;
                            switch (userPlan) {
                                case "1":
                                    size += 500;
                                    break;
                                case "2":
                                    size += 1000;
                                    break;
                                case "3":
                                    size += 1500;
                                    break;
                                case "4":
                                    size += 2000;
                                    break;
                                case "5":
                                    size += 2500;
                                    break;
                            }
                            ArrayList<QuestionModel> data = new ArrayList<>();
                            for (int i = 0; i < response.body().getData().size(); i++) {
                                QuestionModel m = response.body().getData().get(i);
                                m.setVisible(false);
                                m.setUploaded(!m.getUserAnswer().equals("-1"));

                                m.setBought(i < size);
                                m.setVisible(false);
                                data.add(m);
                            }
                            db.executeTransaction(realm -> realm.insertOrUpdate(data));
                            dialog.dismiss();
                            updateMessages();
                            checkTime();
                        } else {
                            dialog.dismiss();
                            Utils.showInternetError(MainActivity.this, () -> getData());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<QuestionResponse> call, @NonNull Throwable t) {
                        dialog.dismiss();
                        Utils.showInternetError(MainActivity.this, () -> getData());
                    }
                });
    }

    private void firebaseDebug(String result) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "verify");
        bundle.putString(FirebaseAnalytics.Param.ITEM_VARIANT, result);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);
    }

    private void helpInfo() {
        new TapTargetSequence(this)
                .targets(
                        TapTarget.forView(bnv.getRootView().findViewById(R.id.menu_profile), getString(R.string.tap_target_profile_title), getString(R.string.tap_target_profile_des))
                                .cancelable(false)
                                .dimColor(R.color.black)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(R.color.white)
                                .textColor(android.R.color.black),
                        TapTarget.forView(bnv.getRootView().findViewById(R.id.menu_highscore), getString(R.string.tap_target_highscore_title), getString(R.string.tap_target_highscore_des))
                                .cancelable(false)
                                .dimColor(R.color.black)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(R.color.white)
                                .textColor(android.R.color.black),
                        TapTarget.forView(bnv.getRootView().findViewById(R.id.menu_start), getString(R.string.tap_target_start_title), getString(R.string.tap_target_start_des))
                                .cancelable(false)
                                .dimColor(R.color.black)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(R.color.white)
                                .textColor(android.R.color.black),
                        TapTarget.forView(bnv.getRootView().findViewById(R.id.menu_buy), getString(R.string.tap_target_buy_title), getString(R.string.tap_target_buy_des))
                                .cancelable(false)
                                .dimColor(R.color.black)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(R.color.white)
                                .textColor(android.R.color.black),
                        TapTarget.forView(bnv.getRootView().findViewById(R.id.menu_message), getString(R.string.tap_target_message_title), getString(R.string.tap_target_message_des))
                                .cancelable(false)
                                .dimColor(R.color.black)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(R.color.white)
                                .textColor(android.R.color.black),
                        TapTarget.forView(findViewById(R.id.toolbar_menu), getString(R.string.tap_target_menu_title), getString(R.string.tap_target_menu_des))
                                .cancelable(false)
                                .dimColor(R.color.black)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(R.color.white)
                                .textColor(android.R.color.black)

                ).listener(new TapTargetSequence.Listener() {

            @Override
            public void onSequenceFinish() {
                MySharedPreference.getInstance(MainActivity.this).setFirstTime();
                firebaseDebug("ok");

            }

            @Override
            public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
            }

            @Override
            public void onSequenceCanceled(TapTarget lastTarget) {
            }
        }).start();
    }

    public static void setAvatars(Activity activity) {
        Glide.with(activity)
                .load(activity.getString(R.string.avatar_url, MySharedPreference.getInstance(activity).getUserAvatar()))
                .circleCrop()
                .placeholder(R.drawable.placeholder)
                .into((ImageView) activity.findViewById(R.id.navigation_avatar));

        Glide.with(activity)
                .load(activity.getString(R.string.avatar_url, MySharedPreference.getInstance(activity).getUserAvatar()))
                .circleCrop()
                .placeholder(R.drawable.placeholder)
                .into((ImageView) activity.findViewById(R.id.toolbar_avatar));
    }

    private void navigationDrawer() {
        ((MaterialTextView) findViewById(R.id.navigation_name)).setText(MySharedPreference.getInstance(this).getUsername());
        ((MaterialTextView) findViewById(R.id.navigation_code)).setText(getString(R.string.profile_code, MySharedPreference.getInstance(this).getUserCode()));
        ((MaterialTextView) findViewById(R.id.navigation_score)).setText(getString(R.string.highscore_score, MySharedPreference.getInstance(this).getScore()));

        findViewById(R.id.navigation_exit).setOnClickListener(v -> Utils.logout(this, false));
        findViewById(R.id.navigation_buyhistory).setOnClickListener(v -> {
            //   startActivity(new Intent(this, BuyHistoryActivity.class));
            navController.navigate(R.id.action_global_buyHistoryFragment);
            motionLayout.transitionToStart();
        });
        findViewById(R.id.navigation_support).setOnClickListener(v -> {
            startActivity(new Intent(this, SupportActivity.class));
            motionLayout.transitionToStart();
        });
        findViewById(R.id.navigation_invite).setOnClickListener(v -> {
            // startActivity(new Intent(this, InviteActivity.class));
            navController.navigate(R.id.action_global_inviteFragment);
            motionLayout.transitionToStart();
        });
        findViewById(R.id.navigation_setting).setOnClickListener(v -> {
            navController.navigate(R.id.action_global_settingsFragment);
            motionLayout.transitionToStart();

        });
        findViewById(R.id.navigation_instagram).setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(getString(R.string.instagram_url)));
            startActivity(intent);
            motionLayout.transitionToStart();

        });

        findViewById(R.id.navigation_rule).setOnClickListener(v -> {
            showRulesDialog();
            motionLayout.transitionToStart();

        });
        findViewById(R.id.navigation_about).setOnClickListener(v -> {
            navController.navigate(R.id.action_global_aboutFragment);
            motionLayout.transitionToStart();

        });
        findViewById(R.id.navigation_help).setOnClickListener(v -> {
            motionLayout.transitionToStart();
            helpInfo();

        });
    }


    private void init() {

        bnv = findViewById(R.id.main_bnv);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.main_container);
        if (navHostFragment != null)
            navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(bnv, navController);

        bnv.setOnNavigationItemReselectedListener(item -> {
        });

        motionLayout = findViewById(R.id.main_motion);
        refreshIntent = new Intent(GHARAREHMAGHZHA_BROADCAST_REFRESH);
        doubleBackToExitPressedOnce = false;
        newChat = findViewById(R.id.main_chat_new);
        newToolbar = findViewById(R.id.toolbar_new);
        avatar = findViewById(R.id.toolbar_avatar);
        onClicks();
        uploadAnswers();
        navigationDrawer();
        registerReceiver(notificationBroadCast, new IntentFilter(GHARAREHMAGHZHA_BROADCAST));


    }

    private void onClicks() {
        findViewById(R.id.toolbar_insta).setOnClickListener(v->{
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(getString(R.string.instagram_url)));
            startActivity(intent);
        });
        avatar.setOnClickListener(v -> navController.navigate(R.id.action_global_profileEditFragment));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Utils.isBoosterValid(MySharedPreference.getInstance(this).getBoosterDate())) {
            MySharedPreference.getInstance(this).setBoosterValue(Float.parseFloat("1"));
        }
        if (db.isEmpty()) {
            getData();
        } else {
            updateMessages();
            checkTime();
        }

        setAvatars(this);
        Utils.removeNotification(this);

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(notificationBroadCast);
        super.onDestroy();
    }

    private void updateMessages() {
        int size = db.where(MessageModel.class).equalTo("sender", "admin").equalTo("read", 0).findAll().size();
        BadgeDrawable badgeDrawable = bnv.getOrCreateBadge(R.id.menu_message);
        badgeDrawable.setVisible(size > 0);

        if (MySharedPreference.getInstance(MainActivity.this).getUnreadChats() > 0) {
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
                            if (!Utils.isTimeAcceptable(response.body().getTime())) {
                                timeDialog = Utils.showTimeError(MainActivity.this);
                            } else {
                                MySharedPreference.getInstance(MainActivity.this).setDaysPassed(response.body().getPassed());
                                verify();
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
                .validate("Bearer " + token, number)
                .enqueue(new Callback<VerifyResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<VerifyResponse> call, @NonNull Response<VerifyResponse> response) {
                        if (response.body() != null)
                            if (response.isSuccessful() && response.body().getResult().equals("success")) {
                                int newPlan = Integer.parseInt(response.body().getUserPlan());
                                int oldPlan = Integer.parseInt(MySharedPreference.getInstance(MainActivity.this).getPlan());
                                if (newPlan > oldPlan) {
                                    dataDialog = Utils.showGetDataLoading(MainActivity.this);
                                    getQuestions(newPlan);
                                } else {
                                    updateDatabase(false);
                                }
                                int myVersion = Utils.getVersionCode(MainActivity.this);
                                int newVersion = Integer.parseInt(response.body().getVersion());
                                int newScore = Integer.parseInt(response.body().getScoreCount());
                                int oldScore = Integer.parseInt(MySharedPreference.getInstance(MainActivity.this).getScore());

                                if (newScore == 0 || newScore > oldScore)
                                    MySharedPreference.getInstance(MainActivity.this).setScore(String.valueOf(newScore));
                                else if (oldScore > newScore)
                                    uploadScore(String.valueOf(oldScore));

                                if (newVersion > myVersion)
                                    if (response.body().getVersionEssential().equals("1"))
                                        showNewVersionDialog("1");
                                    else
                                        showNewVersionDialog("0");
                                sendBroadcast(refreshIntent);

                                int serverBooster = Integer.parseInt(response.body().getUserBooster());
                                int localBooster = MySharedPreference.getInstance(MainActivity.this).getBooster();
                                if (serverBooster != 0 && serverBooster > localBooster) {
                                    if (Utils.isBoosterValid(response.body().getUserBoosterExpire())) {
                                        MySharedPreference.getInstance(MainActivity.this).clearCounter(MainActivity.this, false);
                                        MySharedPreference.getInstance(MainActivity.this).setBoosterValue(Float.parseFloat(response.body().getBoosterValue()));
                                        MySharedPreference.getInstance(MainActivity.this).setBooster(serverBooster);
                                        MySharedPreference.getInstance(MainActivity.this).setBoosterDate(response.body().getUserBoosterExpire());
                                        String[] expireDate = response.body().getUserBoosterExpire().replace(" ", ":").replace("-", ":").split(":");
                                        Utils.setAlarm(MainActivity.this,
                                                Integer.parseInt(expireDate[0]),
                                                Integer.parseInt(expireDate[1]),
                                                Integer.parseInt(expireDate[2]),
                                                Integer.parseInt(expireDate[3]),
                                                Integer.parseInt(expireDate[4]));
                                    }
                                }

                            } else if (response.code() == 401) {
                                Utils.logout(MainActivity.this, true);
                            } else
                                Utils.showInternetError(MainActivity.this, () -> verify());
                    }

                    @Override
                    public void onFailure(@NonNull Call<VerifyResponse> call, @NonNull Throwable t) {
                        firebaseDebug("failed");

                        Utils.showInternetError(MainActivity.this, () -> verify());

                    }
                });
    }

    private void updateDatabase(boolean shouldUpdate) {
        int day = Integer.parseInt(MySharedPreference.getInstance(this).getDaysPassed());
        if (day >= 0 && day < 10) {
            if (shouldUpdate | day != MySharedPreference.getInstance(this).getLastUpdate()) {
                final int range;
                switch (day) {
                    case 1:
                        range = 600;
                        break;
                    case 2:
                        range = 900;
                        break;
                    case 3:
                        range = 1200;
                        break;
                    case 4:
                        range = 1500;
                        break;
                    case 5:
                        range = 1800;
                        break;
                    case 6:
                        range = 2100;
                        break;
                    case 7:
                        range = 2400;
                        break;
                    case 8:
                        range = 2700;
                        break;
                    case 9:
                        range = 3000;
                        break;
                    default:
                        range = 300;
                }

                db.executeTransaction(realm -> {
                    RealmResults<QuestionModel> questions = realm.where(QuestionModel.class)
                            .equalTo("bought", true)
                            .sort("sortId", Sort.ASCENDING)
                            .limit(range).findAll();

                    questions.setBoolean("visible", true);
                });
                sendBroadcast(refreshIntent);
                MySharedPreference.getInstance(this).setLastUpdate(day);
            } else if (!Utils.isBoosterValid(MySharedPreference.getInstance(this).getBoosterDate())) {
                MySharedPreference.getInstance(this).clearCounter(this, false);
            }
        }

    }

    private void getQuestions(int plan) {
        final int size;
        switch (plan) {
            case 1:
                size = 1000;
                break;
            case 2:
                size = 1500;
                break;
            case 3:
                size = 2000;
                break;
            case 4:
                size = 2500;
                break;
            case 5:
                size = 3000;
                break;
            default:
                size = 0;
        }

        db.executeTransaction(realm -> {
            RealmResults<QuestionModel> questions = realm.where(QuestionModel.class)
                    .sort("sortId", Sort.ASCENDING)
                    .limit(size).findAll();

            questions.setBoolean("bought", true);
        });
        MySharedPreference.getInstance(MainActivity.this).setPlan(String.valueOf(plan));
        if (dataDialog != null) dataDialog.dismiss();
        updateDatabase(true);
    }

    private void uploadScore(String score) {
        int passed = Integer.parseInt(MySharedPreference.getInstance(this).getDaysPassed());
        if(passed>0 && passed<10) {
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
    }

    private void uploadAnswers() {
        int passed = Integer.parseInt(MySharedPreference.getInstance(this).getDaysPassed());
        if(passed>0 && passed<10) {
            RealmResults<QuestionModel> models = db.where(QuestionModel.class).equalTo("visible", false).notEqualTo("userAnswer", "-1").equalTo("uploaded", false).findAll();
            for (QuestionModel model : models)
                uploadAnswer(model.getQuestionId(), model.getUserAnswer(), model.getUserBooster());

        }
    }

    private void uploadAnswer(String questionId, String userAnswer, String booster) {

        String number = MySharedPreference.getInstance(this).getNumber();
        String token = MySharedPreference.getInstance(this).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(MainActivity.this, true);
            return;
        }
        RetrofitClient.getInstance().getApi()
                .answerQuestion("Bearer " + token, number, questionId, userAnswer, booster)
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

        if (motionLayout.getCurrentState() == R.id.end) {
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

    private void showRulesDialog() {
        RulesDialog dialog = new RulesDialog(this);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }
}