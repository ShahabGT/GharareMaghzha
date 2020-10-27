package ir.ghararemaghzha.game.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmResults;
import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.classes.MySettingsPreference;
import ir.ghararemaghzha.game.classes.MySharedPreference;
import ir.ghararemaghzha.game.classes.Utils;
import ir.ghararemaghzha.game.data.RetrofitClient;
import ir.ghararemaghzha.game.models.GeneralResponse;
import ir.ghararemaghzha.game.models.QuestionModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ir.ghararemaghzha.game.classes.Const.GHARAREHMAGHZHA_BROADCAST_END;

public class QuestionActivity extends AppCompatActivity {
    private int progress = 100;
    private int time = 15;
    private CountDownTimer downTimer, nextTimer;
    private ProgressBar progressBar;
    private MaterialButton next;
    private MaterialTextView timeText, question, answer1, answer2, answer3, answer4, score, questionPoints, questionRemain;
    private MaterialCardView answer1c, answer2c, answer3c, answer4c, questionc;
    private RealmResults<QuestionModel> data;
    private List<Integer> randomAnswers;
    private String correctAnswer = "";
    private QuestionModel model;
    private Realm db;
    private SoundPool soundPool;
    private MediaPlayer mediaPlayer;
    private int correctSound, wrongSound;
    private int gameScore = 0;
    private boolean shouldRandomize;
    private ImageView music;
    private ImageView autoNext;
    private boolean musicSetting;
    private boolean autoNextSetting;
    private boolean hasBooster = false;
    private boolean foreground = false;

    private final BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(QuestionActivity.this, R.string.general_end, Toast.LENGTH_LONG).show();
            onBackPressed();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_question);

        if (MySharedPreference.getInstance(this).isFirstTimeQuestion())
            helpInfo();
        else
            init();


    }

    private void helpInfo() {
        new TapTargetSequence(this)
                .targets(
                        TapTarget.forView(findViewById(R.id.question_score_card), getString(R.string.tap_target_question_score_title), getString(R.string.tap_target_question_score_des))
                                .cancelable(false)
                                .tintTarget(false)
                                .dimColor(R.color.black)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(R.color.white)
                                .textColor(android.R.color.black),
                        TapTarget.forView(findViewById(R.id.question_music), getString(R.string.tap_target_question_music_title), getString(R.string.tap_target_question_music_des))
                                .cancelable(false)
                                .dimColor(R.color.black)
                                .tintTarget(false)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(R.color.white)
                                .textColor(android.R.color.black),
                        TapTarget.forView(findViewById(R.id.question_autonext), getString(R.string.tap_target_question_next_title), getString(R.string.tap_target_question_next_des))
                                .cancelable(false)
                                .tintTarget(false)
                                .dimColor(R.color.black)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(R.color.white)
                                .textColor(android.R.color.black),
                        TapTarget.forView(findViewById(R.id.question_booster), getString(R.string.tap_target_question_booster_title), getString(R.string.tap_target_question_booster_des))
                                .cancelable(false)
                                .tintTarget(false)
                                .dimColor(R.color.black)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(R.color.white)
                                .textColor(android.R.color.black),
                        TapTarget.forView(findViewById(R.id.question_progress_text), getString(R.string.tap_target_question_time_title), getString(R.string.tap_target_question_time_des))
                                .cancelable(false)
                                .tintTarget(false)
                                .dimColor(R.color.black)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(R.color.white)
                                .textColor(android.R.color.black),
                        TapTarget.forView(findViewById(R.id.question_points), getString(R.string.tap_target_question_point_title), getString(R.string.tap_target_question_point_des))
                                .cancelable(false)
                                .tintTarget(false)
                                .dimColor(R.color.black)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(R.color.white)
                                .textColor(android.R.color.black),
                        TapTarget.forView(findViewById(R.id.question_remaining), getString(R.string.tap_target_question_question_title), getString(R.string.tap_target_question_question_des))
                                .cancelable(false)
                                .tintTarget(false)
                                .dimColor(R.color.black)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(R.color.white)
                                .textColor(android.R.color.black),
                        TapTarget.forView(findViewById(R.id.question_report), getString(R.string.tap_target_question_next_report_title), getString(R.string.tap_target_question_next_report_des))
                                .cancelable(false)
                                .tintTarget(false)
                                .dimColor(R.color.black)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(R.color.white)
                                .textColor(android.R.color.black),
                        TapTarget.forView(findViewById(R.id.question_next), getString(R.string.tap_target_question_next_question_title), getString(R.string.tap_target_question_next_question_des))
                                .cancelable(false)
                                .tintTarget(false)
                                .dimColor(R.color.black)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(R.color.white)
                                .textColor(android.R.color.black)
                ).listener(new TapTargetSequence.Listener() {

            @Override
            public void onSequenceFinish() {
                MySharedPreference.getInstance(QuestionActivity.this).setFirstTimeQuestion();
                init();
            }

            @Override
            public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
            }

            @Override
            public void onSequenceCanceled(TapTarget lastTarget) {
            }
        }).start();
    }


    private void init() {
        if (!Utils.isBoosterValid(MySharedPreference.getInstance(this).getBoosterDate())) {
            MySharedPreference.getInstance(this).setBoosterValue(Float.parseFloat("1"));
            MySharedPreference.getInstance(this).clearCounter(this,false);
        }
        db = Realm.getDefaultInstance();
        data = db.where(QuestionModel.class).equalTo("visible", true)
                .and().equalTo("userAnswer", "-1").findAll();

        music = findViewById(R.id.question_music);
        musicSetting = MySettingsPreference.getInstance(this).getMusic();
        music.setImageResource(musicSetting ? R.drawable.vector_music_on : R.drawable.vector_music_off);

        autoNext = findViewById(R.id.question_autonext);
        autoNextSetting = MySettingsPreference.getInstance(this).getAutoNext();
        autoNext.setImageResource(autoNextSetting ? R.drawable.auto_next_on : R.drawable.auto_next_off);


        next = findViewById(R.id.question_next);
        questionPoints = findViewById(R.id.question_points);
        questionRemain = findViewById(R.id.question_remaining);

        ImageView booster = findViewById(R.id.question_booster);
        if (MySharedPreference.getInstance(this).getBoosterValue() == 1f) {
            booster.setVisibility(View.GONE);
            hasBooster = false;
        } else {
            hasBooster = true;
        }

        question = findViewById(R.id.question_question);
        questionc = findViewById(R.id.question_question_card);
        answer1 = findViewById(R.id.question_answer1);
        answer1c = findViewById(R.id.question_answer1_card);
        answer2 = findViewById(R.id.question_answer2);
        answer2c = findViewById(R.id.question_answer2_card);
        answer3 = findViewById(R.id.question_answer3);
        answer3c = findViewById(R.id.question_answer3_card);
        answer4 = findViewById(R.id.question_answer4);
        answer4c = findViewById(R.id.question_answer4_card);

        score = findViewById(R.id.question_score);
        gameScore = Integer.parseInt(MySharedPreference.getInstance(this).getScore());
        score.setText(String.valueOf(gameScore));


        progressBar = findViewById(R.id.question_progress_bar);
        progressBar.setProgress(progress);
        timeText = findViewById(R.id.question_progress_text);
        downTimer = new CountDownTimer(15000, 1000) {
            @Override
            public void onTick(long l) {
                time -= 1;
                progress -= 6.6;
                timeText.setText(String.valueOf(time));
                progressBar.setProgress(progress);
                if (l < 6000)
                    timeText.setTextColor(getResources().getColor(R.color.random1));
            }

            @Override
            public void onFinish() {
                timeText.setText(String.valueOf(0));
                progressBar.setProgress(0);
                answer1c.setEnabled(false);
                answer2c.setEnabled(false);
                answer3c.setEnabled(false);
                answer4c.setEnabled(false);
                if (autoNextSetting && foreground)
                    nextQuestion();
            }
        };
        nextTimer = new CountDownTimer(2500, 1000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                next.setEnabled(true);
                nextTimer.cancel();
            }
        };
        onClicks();
        nextQuestion();

        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder().setMaxStreams(2).setAudioAttributes(attributes).build();
        correctSound = soundPool.load(this, R.raw.correct, 1);
        wrongSound = soundPool.load(this, R.raw.wrong, 1);
        mediaPlayer = MediaPlayer.create(this, R.raw.game);
        mediaPlayer.setLooping(true);

        findViewById(R.id.question_report).setOnClickListener(v -> report());
    }


    private void playSound(int sound) {
        soundPool.play(sound, 1, 1, 2, 0, 1);
    }

    private void onClicks() {
        music.setOnClickListener(v -> {
            musicSetting = !musicSetting;
            music.setImageResource(musicSetting ? R.drawable.vector_music_on : R.drawable.vector_music_off);
            if (musicSetting) {
                mediaPlayer = MediaPlayer.create(this, R.raw.game);
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            } else {
                mediaPlayer.pause();
                mediaPlayer.stop();
            }
        });

        autoNext.setOnClickListener(v -> {
            autoNextSetting = !autoNextSetting;
            autoNext.setImageResource(autoNextSetting ? R.drawable.auto_next_on : R.drawable.auto_next_off);

            MySettingsPreference.getInstance(this).setAutoNext(autoNextSetting);
        });


        answer1c.setOnClickListener(v -> {
            MySharedPreference.getInstance(QuestionActivity.this).counterIncrease(QuestionActivity.this);
            downTimer.cancel();
            timeText.setText(String.valueOf(0));
            progressBar.setProgress(0);
            answer1c.setEnabled(false);
            answer2c.setEnabled(false);
            answer3c.setEnabled(false);
            answer4c.setEnabled(false);
            String answerId;
            if (shouldRandomize)
                answerId = String.valueOf(randomAnswers.get(0) + 1);
            else
                answerId = String.valueOf(1);

            setAnswer(answerId);
            uploadAnswer(answerId);
            if (answer1.getText().toString().equals(correctAnswer)) {
                answer1c.setCardBackgroundColor(getResources().getColor(R.color.green));
                YoYo.with(Techniques.Tada).duration(500).playOn(answer1c);
                playSound(correctSound);
                uploadScore();

            } else {
                answer1c.setCardBackgroundColor(getResources().getColor(R.color.red));
                YoYo.with(Techniques.Shake).duration(500).playOn(answer1c);
                playSound(wrongSound);

            }
            if (autoNextSetting)
                new Handler().postDelayed(this::nextQuestion, 1000);
        });
        answer2c.setOnClickListener(v -> {
            MySharedPreference.getInstance(QuestionActivity.this).counterIncrease(QuestionActivity.this);
            downTimer.cancel();
            timeText.setText(String.valueOf(0));
            progressBar.setProgress(0);
            downTimer.cancel();
            answer1c.setEnabled(false);
            answer2c.setEnabled(false);
            answer3c.setEnabled(false);
            answer4c.setEnabled(false);
            String answerId;
            if (shouldRandomize)
                answerId = String.valueOf(randomAnswers.get(1) + 1);
            else
                answerId = String.valueOf(2);
            setAnswer(answerId);
            uploadAnswer(answerId);
            if (answer2.getText().toString().equals(correctAnswer)) {
                answer2c.setCardBackgroundColor(getResources().getColor(R.color.green));
                YoYo.with(Techniques.Tada).duration(500).playOn(answer2c);
                playSound(correctSound);
                uploadScore();
            } else {
                answer2c.setCardBackgroundColor(getResources().getColor(R.color.red));
                YoYo.with(Techniques.Shake).duration(500).playOn(answer2c);
                playSound(wrongSound);
            }
            if (autoNextSetting) new Handler().postDelayed(this::nextQuestion, 1000);


        });
        answer3c.setOnClickListener(v -> {
            MySharedPreference.getInstance(QuestionActivity.this).counterIncrease(QuestionActivity.this);
            downTimer.cancel();
            timeText.setText(String.valueOf(0));
            progressBar.setProgress(0);
            answer1c.setEnabled(false);
            answer2c.setEnabled(false);
            answer3c.setEnabled(false);
            answer4c.setEnabled(false);
            String answerId;
            if (shouldRandomize)
                answerId = String.valueOf(randomAnswers.get(2) + 1);
            else
                answerId = String.valueOf(3);
            setAnswer(answerId);
            uploadAnswer(answerId);
            if (answer3.getText().toString().equals(correctAnswer)) {
                answer3c.setCardBackgroundColor(getResources().getColor(R.color.green));
                YoYo.with(Techniques.Tada).duration(500).playOn(answer3c);
                playSound(correctSound);
                uploadScore();
            } else {
                answer3c.setCardBackgroundColor(getResources().getColor(R.color.red));
                YoYo.with(Techniques.Shake).duration(500).playOn(answer3c);
                playSound(wrongSound);
            }
            if (autoNextSetting) new Handler().postDelayed(this::nextQuestion, 1000);


        });
        answer4c.setOnClickListener(v -> {
            MySharedPreference.getInstance(QuestionActivity.this).counterIncrease(QuestionActivity.this);
            downTimer.cancel();
            timeText.setText(String.valueOf(0));
            progressBar.setProgress(0);
            answer1c.setEnabled(false);
            answer2c.setEnabled(false);
            answer3c.setEnabled(false);
            answer4c.setEnabled(false);
            String answerId;
            if (shouldRandomize)
                answerId = String.valueOf(randomAnswers.get(3) + 1);
            else
                answerId = String.valueOf(4);
            setAnswer(answerId);
            uploadAnswer(answerId);
            if (answer4.getText().toString().equals(correctAnswer)) {
                answer4c.setCardBackgroundColor(getResources().getColor(R.color.green));
                YoYo.with(Techniques.Tada).duration(500).playOn(answer4c);
                playSound(correctSound);
                uploadScore();
            } else {
                answer4c.setCardBackgroundColor(getResources().getColor(R.color.red));
                YoYo.with(Techniques.Shake).duration(500).playOn(answer4c);
                playSound(wrongSound);
            }
            if (autoNextSetting) new Handler().postDelayed(this::nextQuestion, 1000);


        });
        next.setOnClickListener(v -> nextQuestion());
        findViewById(R.id.question_close).setOnClickListener(v -> QuestionActivity.this.finish());

    }

    private void enterAnimations() {
        YoYo.with(Techniques.Landing).duration(1500).playOn(questionc);
        YoYo.with(Techniques.Landing).duration(1500).playOn(answer1c);
        YoYo.with(Techniques.Landing).duration(1500).playOn(answer2c);
        YoYo.with(Techniques.Landing).duration(1500).playOn(answer3c);
        YoYo.with(Techniques.Landing).duration(1500).playOn(answer4c);
    }

    private void nextQuestion() {
        if (data.isEmpty()) {
            Utils.createNotification(this, getString(R.string.questions_notification_title), getString(R.string.questions_notification_body), "ir.ghararemaghzha.game.TARGET_NOTIFICATION");
            Toast.makeText(this, getString(R.string.questions_notification_title), Toast.LENGTH_SHORT).show();
            onBackPressed();
            return;
        }
        if (Utils.checkInternet(this)) {
            questionRemain.setText(getString(R.string.question_remaining, String.valueOf(data.size() - 1)));
            model = getRandom();
            downTimer.cancel();
            nextTimer.start();
            enterAnimations();
            answer1c.setEnabled(true);
            answer1c.setCardBackgroundColor(getResources().getColor(R.color.white));
            answer2c.setEnabled(true);
            answer2c.setCardBackgroundColor(getResources().getColor(R.color.white));
            answer3c.setEnabled(true);
            answer3c.setCardBackgroundColor(getResources().getColor(R.color.white));
            answer4c.setEnabled(true);
            answer4c.setCardBackgroundColor(getResources().getColor(R.color.white));
            time = 15;
            progress = 100;
            timeText.setText(String.valueOf(time));
            timeText.setTextColor(getResources().getColor(R.color.black));
            progressBar.setProgress(100);
            randomAnswers = randomNumbers();
            question.setText(model.getQuestionText());

            List<String> answers = new ArrayList<String>() {{
                add(model.getQuestionA1());
                add(model.getQuestionA2());
                add(model.getQuestionA3());
                add(model.getQuestionA4());
            }};
            correctAnswer = answers.get(Integer.parseInt(model.getQuestionCorrect()) - 1);
            shouldRandomize = true;
            for (String s : answers) {
                if (s.contains("گزینه ") || s.contains("هیچکدام") || s.contains("همه موارد")) {
                    shouldRandomize = false;
                    break;
                }
            }
            if (shouldRandomize) {
                answer1.setText(answers.get(randomAnswers.get(0)));
                answer2.setText(answers.get(randomAnswers.get(1)));
                answer3.setText(answers.get(randomAnswers.get(2)));
                answer4.setText(answers.get(randomAnswers.get(3)));
            } else {
                answer1.setText(answers.get(0));
                answer2.setText(answers.get(1));
                answer3.setText(answers.get(2));
                answer4.setText(answers.get(3));
            }
            questionPoints.setText(getString(R.string.question_points, model.getQuestionPoints()));
            downTimer.start();
            setAnswer("0");
            uploadAnswer("0");
            Utils.updateServerQuestions(this, String.valueOf(db.where(QuestionModel.class).equalTo("visible", true).findAll().size()));
        } else {
            Toast.makeText(this, getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }

    private void setAnswer(String userAnswer) {
        String b = "0";
        if (hasBooster) b = "1";
        db.beginTransaction();
        RealmResults<QuestionModel> result = db.where(QuestionModel.class).equalTo("questionId", model.getQuestionId()).findAll();
        Objects.requireNonNull(result.first()).setUserAnswer(userAnswer);
        Objects.requireNonNull(result.first()).setUserBooster(b);
        db.commitTransaction();
    }

    private void uploadAnswer(String userAnswer) {

        String number = MySharedPreference.getInstance(this).getNumber();
        String token = MySharedPreference.getInstance(this).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(QuestionActivity.this, true);
            return;
        }
        String b = "0";
        if (hasBooster) b = "1";

        RetrofitClient.getInstance().getApi()
                .answerQuestion("Bearer " + token, number, model.getQuestionId(), userAnswer, b)
                .enqueue(new Callback<GeneralResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getResult().equals("success")) {
                            db.beginTransaction();
                            RealmResults<QuestionModel> result = db.where(QuestionModel.class).equalTo("questionId", model.getQuestionId()).findAll();
                            Objects.requireNonNull(result.first()).setUploaded(true);
                            db.commitTransaction();

                        } else if (response.code() == 401) {
                            Utils.logout(QuestionActivity.this, true);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                    }
                });
    }

    private List<Integer> randomNumbers() {
        List<Integer> numbers = new ArrayList<>();
        Random rand = new Random();
        while (numbers.size() < 4) {
            int rnd = rand.nextInt(4);
            boolean isAvailable = false;
            for (int x : numbers) {
                if (rnd == x) {
                    isAvailable = true;
                    break;
                }
            }
            if (!isAvailable)
                numbers.add(rnd);
        }

        return numbers;


    }

    private QuestionModel getRandom() {
        Random rand = new Random();
        int currentQuestion = rand.nextInt(data.size());
        return data.get(currentQuestion);
    }

    private void report() {
        String number = MySharedPreference.getInstance(this).getNumber();
        String token = MySharedPreference.getInstance(this).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(QuestionActivity.this, true);
            return;
        }
        RetrofitClient.getInstance().getApi()
                .report("Bearer " + token, number, model.getQuestionId())
                .enqueue(new Callback<GeneralResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getMessage().equals("submitted"))
                            Toast.makeText(QuestionActivity.this, getString(R.string.general_save), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {

                    }
                });
    }

    private void uploadScore() {
        gameScore += Integer.parseInt(model.getQuestionPoints()) * MySharedPreference.getInstance(this).getBoosterValue();
        score.setText(String.valueOf(gameScore));
        YoYo.with(Techniques.Bounce).duration(500).playOn(score);
        MySharedPreference.getInstance(QuestionActivity.this).setScore(String.valueOf(gameScore));
        String number = MySharedPreference.getInstance(this).getNumber();
        String token = MySharedPreference.getInstance(this).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(QuestionActivity.this, true);
            return;
        }
        RetrofitClient.getInstance().getApi()
                .sendScore("Bearer " + token, number, String.valueOf(gameScore))
                .enqueue(new Callback<GeneralResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                        if (response.code() == 401) {
                            Utils.logout(QuestionActivity.this, true);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && musicSetting)
            mediaPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(br,new IntentFilter(GHARAREHMAGHZHA_BROADCAST_END));
        foreground = true;
        if (mediaPlayer != null && musicSetting)
            mediaPlayer.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(br);
        foreground = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        nextTimer.cancel();
        downTimer.cancel();
        if (soundPool != null && mediaPlayer != null) {
            soundPool.release();
            soundPool = null;
            mediaPlayer.release();
            mediaPlayer = null;
        }

    }
}