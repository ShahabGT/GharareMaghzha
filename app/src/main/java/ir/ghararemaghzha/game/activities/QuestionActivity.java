package ir.ghararemaghzha.game.activities;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmResults;
import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.classes.MySharedPreference;
import ir.ghararemaghzha.game.classes.Utils;
import ir.ghararemaghzha.game.data.RetrofitClient;
import ir.ghararemaghzha.game.models.GeneralResponse;
import ir.ghararemaghzha.game.models.QuestionModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuestionActivity extends AppCompatActivity {
    private int progress = 100;
    private int time = 20;
    private CountDownTimer downTimer,nextTimer;
    private ProgressBar progressBar;
    private MaterialTextView timeText, question, answer1, answer2, answer3, answer4, next, score,questionPoints,questionRemain;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_question);
        init();
    }

    private void init() {
        db = Realm.getDefaultInstance();
        data = db.where(QuestionModel.class).equalTo("visible", true).findAll();

        next = findViewById(R.id.question_next);
        questionPoints = findViewById(R.id.question_points);
        questionRemain = findViewById(R.id.question_remaining);

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
        downTimer = new CountDownTimer(20000, 1000) {
            @Override
            public void onTick(long l) {
                time -= 1;
                progress -= 5;
                timeText.setText(String.valueOf(time));
                progressBar.setProgress(progress);
            }

            @Override
            public void onFinish() {
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
    }


    private void playSound(int sound) {
        soundPool.play(sound, 1, 1, 2, 0, 1);
    }

    private void onClicks() {
        answer1c.setOnClickListener(v -> {
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
        });
        answer2c.setOnClickListener(v -> {
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
        });
        answer3c.setOnClickListener(v -> {
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
        });
        answer4c.setOnClickListener(v -> {
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
        if(Utils.checkInternet(this)) {
            if (data.isEmpty()) {
                Toast.makeText(this, getString(R.string.general_noquestions), Toast.LENGTH_SHORT).show();
                QuestionActivity.this.finish();
            }
            questionRemain.setText(getString(R.string.question_remaining, String.valueOf(data.size())));
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
            time = 20;
            progress = 100;
            timeText.setText(String.valueOf(time));
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
                if (s.contains("گزینه ")) {
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
        }else{
            Toast.makeText(this, getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }

    private void setAnswer(String userAnswer) {
        db.beginTransaction();
        RealmResults<QuestionModel> result = db.where(QuestionModel.class).equalTo("questionId", model.getQuestionId()).findAll();
        Objects.requireNonNull(result.first()).setUserAnswer(userAnswer);
        Objects.requireNonNull(result.first()).setVisible(false);
        db.commitTransaction();
    }

    private void uploadAnswer(String userAnswer) {

        String number = MySharedPreference.getInstance(this).getNumber();
        String token = MySharedPreference.getInstance(this).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(QuestionActivity.this);
            return;
        }
        RetrofitClient.getInstance().getApi()
                .answerQuestion("Bearer " + token, number, model.getQuestionId(), userAnswer)
                .enqueue(new Callback<GeneralResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getResult().equals("success")) {
                            db.beginTransaction();
                            RealmResults<QuestionModel> result = db.where(QuestionModel.class).equalTo("questionId", model.getQuestionId()).findAll();
                            Objects.requireNonNull(result.first()).setUploaded(true);
                            db.commitTransaction();

                        }else if (response.code() == 401) {
                            Utils.logout(QuestionActivity.this);
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

    private void uploadScore() {
        gameScore += Integer.parseInt(model.getQuestionPoints());
        score.setText(String.valueOf(gameScore));
        YoYo.with(Techniques.Bounce).duration(500).playOn(score);
        MySharedPreference.getInstance(QuestionActivity.this).setScore(String.valueOf(gameScore));
        String number = MySharedPreference.getInstance(this).getNumber();
        String token = MySharedPreference.getInstance(this).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(QuestionActivity.this);
            return;
        }
        RetrofitClient.getInstance().getApi()
                .sendScore("Bearer " + token, number, String.valueOf(gameScore))
                .enqueue(new Callback<GeneralResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                        if (response.code() == 401) {
                            Utils.logout(QuestionActivity.this);
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
        if (mediaPlayer != null)
            mediaPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null)
            mediaPlayer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPool.release();
        soundPool = null;
        mediaPlayer.release();
        mediaPlayer = null;

    }
}