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
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmResults;
import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.classes.Const;
import ir.ghararemaghzha.game.classes.MySharedPreference;
import ir.ghararemaghzha.game.classes.Utils;
import ir.ghararemaghzha.game.data.RetrofitClient;
import ir.ghararemaghzha.game.dialogs.GetDataDialog;
import ir.ghararemaghzha.game.dialogs.NewVersionDialog;
import ir.ghararemaghzha.game.dialogs.RulesDialog;
import ir.ghararemaghzha.game.dialogs.TimeDialog;
import ir.ghararemaghzha.game.models.AppOpenResponse;
import ir.ghararemaghzha.game.models.GeneralResponse;
import ir.ghararemaghzha.game.models.MessageModel;
import ir.ghararemaghzha.game.models.QuestionModel;
import ir.ghararemaghzha.game.models.QuestionResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ir.ghararemaghzha.game.classes.Const.GHARAREHMAGHZHA_BROADCAST;
import static ir.ghararemaghzha.game.classes.Const.GHARAREHMAGHZHA_BROADCAST_MESSAGE;
import static ir.ghararemaghzha.game.classes.Const.GHARAREHMAGHZHA_BROADCAST_REFRESH;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;
    private TimeDialog timeDialog;
    private ImageView newChat, newToolbar;
    private boolean doubleBackToExitPressedOnce;
    private Realm db;
    private BottomNavigationView bnv;
    private ImageView avatar;
    private Intent refreshIntent;
    private MotionLayout motionLayout;
    private NavController navController;
    private final BroadcastReceiver notificationBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateMessages();
            Utils.removeNotification(MainActivity.this);
        }
    };

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
        if (MySharedPreference.Companion.getInstance(this).isFirstTime())
            helpInfo();
        else
            firebaseDebug("ok");


    }

    private void getData() {
        GetDataDialog dialog = Utils.showGetDataLoading(MainActivity.this);
        String number = MySharedPreference.Companion.getInstance(this).getNumber();
        String token = MySharedPreference.Companion.getInstance(this).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(MainActivity.this, true);
            return;
        }
        RetrofitClient.Companion.getInstance().getApi()
                .getQuestions("Bearer " + token, number)
                .enqueue(new Callback<QuestionResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<QuestionResponse> call, @NonNull Response<QuestionResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<QuestionModel> data = new ArrayList<>();
                            for (QuestionModel m : response.body().getData()) {
                                m.setUploaded(!m.getUserAnswer().equals("-1"));
                                data.add(m);
                            }
                            db.executeTransaction(it -> it.insertOrUpdate(data));
                            dialog.dismiss();
                            appOpen();
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
                MySharedPreference.Companion.getInstance(MainActivity.this).setFirstTime();
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
                .load(activity.getString(R.string.avatar_url, MySharedPreference.Companion.getInstance(activity).getUserAvatar()))
                .circleCrop()
                .placeholder(R.drawable.placeholder)
                .into((ImageView) activity.findViewById(R.id.navigation_avatar));

        Glide.with(activity)
                .load(activity.getString(R.string.avatar_url, MySharedPreference.Companion.getInstance(activity).getUserAvatar()))
                .circleCrop()
                .placeholder(R.drawable.placeholder)
                .into((ImageView) activity.findViewById(R.id.toolbar_avatar));
    }

    private void navigationDrawer() {
        ((MaterialTextView) findViewById(R.id.navigation_name)).setText(MySharedPreference.Companion.getInstance(this).getUsername());
        ((MaterialTextView) findViewById(R.id.navigation_code)).setText(getString(R.string.profile_code, MySharedPreference.Companion.getInstance(this).getUserCode()));
        ((MaterialTextView) findViewById(R.id.navigation_score)).setText(getString(R.string.highscore_score, MySharedPreference.Companion.getInstance(this).getScore()));

        findViewById(R.id.navigation_exit).setOnClickListener(v -> Utils.logout(this, false));
        findViewById(R.id.navigation_buyhistory).setOnClickListener(v -> {
            navController.navigate(R.id.action_global_buyHistoryFragment);
            motionLayout.transitionToStart();
        });
        findViewById(R.id.navigation_support).setOnClickListener(v -> {
            startActivity(new Intent(this, SupportActivity.class));
            motionLayout.transitionToStart();
        });
        findViewById(R.id.navigation_invite).setOnClickListener(v -> {
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

        navigationDrawer();
        registerReceiver(notificationBroadCast, new IntentFilter(GHARAREHMAGHZHA_BROADCAST));

    }

    private void onClicks() {
        findViewById(R.id.toolbar_insta).setOnClickListener(v -> {
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
        getExtraIntent();
        if (db.isEmpty()) {

            MySharedPreference.Companion.getInstance(MainActivity.this).clearCounter(false);
            MySharedPreference.Companion.getInstance(this).setScore(0);
            MySharedPreference.Companion.getInstance(this).setBoosterValue(1f);

            getData();
        } else {
            updateMessages();
            appOpen();
        }

        setAvatars(this);
        Utils.removeNotification(this);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    private void getExtraIntent() {
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            String in = intent.getExtras().getString(GHARAREHMAGHZHA_BROADCAST_MESSAGE, "default");
            if (in != null && in.equals("new"))
                bnv.setSelectedItemId(R.id.menu_message);
            else if (in != null && in.equals("chat")) {
                startActivity(new Intent(MainActivity.this, SupportActivity.class));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(notificationBroadCast);
    }

    private void updateMessages() {
        int size = db.where(MessageModel.class).equalTo("sender", "admin").equalTo("read", 0).findAll().size();
        BadgeDrawable badgeDrawable = bnv.getOrCreateBadge(R.id.menu_message);
        badgeDrawable.setVisible(size > 0);

        if (MySharedPreference.Companion.getInstance(MainActivity.this).getUnreadChats() > 0) {
            newChat.setVisibility(View.VISIBLE);
            newToolbar.setVisibility(View.VISIBLE);
        } else {
            newChat.setVisibility(View.GONE);
            newToolbar.setVisibility(View.GONE);
        }
    }

    private void appOpen() {
        String number = MySharedPreference.Companion.getInstance(this).getNumber();
        String token = MySharedPreference.Companion.getInstance(this).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(MainActivity.this, true);
            return;
        }
        if (timeDialog != null)
            timeDialog.dismiss();

        RetrofitClient.Companion.getInstance().getApi()
                .appOpen("Bearer " + token, number)
                .enqueue(new Callback<AppOpenResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<AppOpenResponse> call, @NonNull Response<AppOpenResponse> response) {
                        if (response.body() != null)
                            if (response.isSuccessful() && response.body().getResult().equals("success")) {
                                if (!Utils.isTimeAcceptable(response.body().getTime())) {
                                    timeDialog = Utils.showTimeError(MainActivity.this);
                                    return;
                                } else {
                                    MySharedPreference.Companion.getInstance(MainActivity.this).setDaysPassed(Integer.parseInt(response.body().getPassed()));
                                }

                                int newSeason = Integer.parseInt(response.body().getSeason());
                                int oldSeason = MySharedPreference.Companion.getInstance(MainActivity.this).getSeason();

                                if (oldSeason == 0) {
                                    MySharedPreference.Companion.getInstance(MainActivity.this).setSeason(newSeason);
                                } else if (newSeason > oldSeason) {
                                    resetForNewSeason(newSeason);
                                    return;
                                }

                                int myVersion = Utils.getVersionCode(MainActivity.this);
                                int newVersion = Integer.parseInt(response.body().getVersion());

                                if (newVersion > myVersion) {
                                    if (response.body().getVersionEssential().equals("1")) {
                                        showNewVersionDialog("1");
                                        return;
                                    } else
                                        showNewVersionDialog("0");
                                }

                                int newScore = Integer.parseInt(response.body().getScoreCount());
                                int oldScore = MySharedPreference.Companion.getInstance(MainActivity.this).getScore();

                                if (newScore == -1) {
                                    MySharedPreference.Companion.getInstance(MainActivity.this).setScore(0);
                                    uploadScore(String.valueOf(0));
                                } else if (newScore > oldScore)
                                    MySharedPreference.Companion.getInstance(MainActivity.this).setScore(newScore);
                                else if (oldScore > newScore)
                                    uploadScore(String.valueOf(oldScore));


                                sendBroadcast(refreshIntent);

                                int serverBooster = Integer.parseInt(response.body().getUserBooster());
                                int serverBoosterCount = Integer.parseInt(response.body().getScoreBoosterCount());
                                if (serverBooster > 0 && serverBoosterCount > 0) {
                                    MySharedPreference.Companion.getInstance(MainActivity.this).setBoosterValue(Float.parseFloat(response.body().getBoosterValue()));
                                    MySharedPreference.Companion.getInstance(MainActivity.this).setCounter(200 - serverBoosterCount);
                                } else {
                                    MySharedPreference.Companion.getInstance(MainActivity.this).setBoosterValue(1f);
                                }

                                uploadAnswers();

                            } else if (response.code() == 401) {
                                Utils.logout(MainActivity.this, true);
                            } else
                                Utils.showInternetError(MainActivity.this, () -> appOpen());
                    }

                    @Override
                    public void onFailure(@NonNull Call<AppOpenResponse> call, @NonNull Throwable t) {
                        firebaseDebug("failed");
                        Utils.showInternetError(MainActivity.this, () -> appOpen());

                    }
                });
    }

    private void resetForNewSeason(int newSeason) {
        MySharedPreference.Companion.getInstance(this).setScore(0);
        MySharedPreference.Companion.getInstance(this).clearCounter(false);
        MySharedPreference.Companion.getInstance(this).setBoosterValue(1f);
        MySharedPreference.Companion.getInstance(this).setSeason(newSeason);
        final RealmResults<QuestionModel> results = db.where(QuestionModel.class).findAll();
        db.executeTransaction(realm -> results.deleteAllFromRealm());
        getData();

    }

    private void uploadScore(String score) {
        int passed = MySharedPreference.Companion.getInstance(this).getDaysPassed();
        if (passed >= 0 && passed < 7) {
            String number = MySharedPreference.Companion.getInstance(this).getNumber();
            String token = MySharedPreference.Companion.getInstance(this).getAccessToken();
            if (number.isEmpty() || token.isEmpty()) {
                Utils.logout(MainActivity.this, true);
                return;
            }
            RetrofitClient.Companion.getInstance().getApi()
                    .sendScore("Bearer " + token, number, score, Const.SEASON)
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
        int passed = MySharedPreference.Companion.getInstance(this).getDaysPassed();
        if (passed >= 0 && passed < 7) {
            RealmResults<QuestionModel> models = db.where(QuestionModel.class).notEqualTo("userAnswer", "-1").equalTo("uploaded", false).findAll();
            for (QuestionModel model : models)
                uploadAnswer(model.getQuestionId(), model.getUserAnswer(), model.getUserBooster());
        }
    }

    private void uploadAnswer(String questionId, String userAnswer, String booster) {
        String number = MySharedPreference.Companion.getInstance(this).getNumber();
        String token = MySharedPreference.Companion.getInstance(this).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(MainActivity.this, true);
            return;
        }
        RetrofitClient.Companion.getInstance().getApi()
                .answerQuestion("Bearer " + token, number, questionId, userAnswer, booster, Const.SEASON)
                .enqueue(new Callback<GeneralResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getResult().equals("success")) {
                            db.executeTransaction(it -> {
                                QuestionModel result = db.where(QuestionModel.class).equalTo("questionId", questionId).findFirst();
                                if (result != null)
                                    result.setUploaded(true);
                            });
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