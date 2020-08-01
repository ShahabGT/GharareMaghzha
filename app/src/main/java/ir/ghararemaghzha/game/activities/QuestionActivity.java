package ir.ghararemaghzha.game.activities;

import android.content.Context;
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
    private CountDownTimer downTimer;
    private ProgressBar progressBar;
    private MaterialTextView timeText, question, answer1, answer2, answer3, answer4, next;
    private MaterialCardView answer1c, answer2c, answer3c, answer4c, questionc;
    private RealmResults<QuestionModel> data;
    private List<Integer> randomAnswers;
    private String correctAnswer = "";
    private QuestionModel model;
    private Realm db;
    private SoundPool soundPool;
    private MediaPlayer mediaPlayer;
    private int correctSound, wrongSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_question);
        init();
    }

    private void init() {
        db = Realm.getDefaultInstance();
        data = db.where(QuestionModel.class).equalTo("visible", true).findAll();

        next = findViewById(R.id.question_next);

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
            String answerId = String.valueOf(randomAnswers.get(0) + 1);
            setAnswer(answerId);
            uploadAnswer(answerId);
            if (answer1.getText().toString().equals(correctAnswer)) {
                //  currentPoints += Integer.parseInt(model.getQuestionPoints());
                //  points.setText("points: " + currentPoints);
                answer1c.setCardBackgroundColor(getResources().getColor(R.color.green));
                YoYo.with(Techniques.Tada).duration(500).playOn(answer1c);
                playSound(correctSound);

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
            String answerId = String.valueOf(randomAnswers.get(1) + 1);
            setAnswer(answerId);
            uploadAnswer(answerId);
            if (answer2.getText().toString().equals(correctAnswer)) {
                //  currentPoints += Integer.parseInt(model.getQuestionPoints());
                //  points.setText("points: " + currentPoints);
                answer2c.setCardBackgroundColor(getResources().getColor(R.color.green));
                YoYo.with(Techniques.Tada).duration(500).playOn(answer2c);
                playSound(correctSound);
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
            String answerId = String.valueOf(randomAnswers.get(2) + 1);
            setAnswer(answerId);
            uploadAnswer(answerId);
            if (answer3.getText().toString().equals(correctAnswer)) {
                //  currentPoints += Integer.parseInt(model.getQuestionPoints());
                //  points.setText("points: " + currentPoints);
                answer3c.setCardBackgroundColor(getResources().getColor(R.color.green));
                YoYo.with(Techniques.Tada).duration(500).playOn(answer3c);
                playSound(correctSound);
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
            String answerId = String.valueOf(randomAnswers.get(3) + 1);
            setAnswer(answerId);
            uploadAnswer(answerId);
            if (answer4.getText().toString().equals(correctAnswer)) {
                //  currentPoints += Integer.parseInt(model.getQuestionPoints());
                //  points.setText("points: " + currentPoints);
                answer4c.setCardBackgroundColor(getResources().getColor(R.color.green));
                YoYo.with(Techniques.Tada).duration(500).playOn(answer4c);
                playSound(correctSound);
            } else {
                answer4c.setCardBackgroundColor(getResources().getColor(R.color.red));
                YoYo.with(Techniques.Shake).duration(500).playOn(answer4c);
                playSound(wrongSound);
            }
        });
        next.setOnClickListener(v -> nextQuestion());

        questionc.setOnClickListener(v -> {
            YoYo.with(Techniques.Shake).duration(500).playOn(questionc);
            playSound(wrongSound);
        });
    }

    private void enterAnimations() {
        YoYo.with(Techniques.FlipInX).duration(1500).playOn(questionc);
        YoYo.with(Techniques.FlipInX).duration(1500).playOn(answer1c);
        YoYo.with(Techniques.FlipInX).duration(1500).playOn(answer2c);
        YoYo.with(Techniques.FlipInX).duration(1500).playOn(answer3c);
        YoYo.with(Techniques.FlipInX).duration(1500).playOn(answer4c);
    }

    private void nextQuestion() {
        enterAnimations();
        answer1c.setEnabled(true);
        answer1c.setCardBackgroundColor(getResources().getColor(R.color.white));
        answer2c.setEnabled(true);
        answer2c.setCardBackgroundColor(getResources().getColor(R.color.white));
        answer3c.setEnabled(true);
        answer3c.setCardBackgroundColor(getResources().getColor(R.color.white));
        answer4c.setEnabled(true);
        answer4c.setCardBackgroundColor(getResources().getColor(R.color.white));
        downTimer.cancel();
        time = 20;
        progress = 100;
        timeText.setText(String.valueOf(time));
        progressBar.setProgress(100);

        if (data.isEmpty()) {
            Toast.makeText(this, "no questions for now!", Toast.LENGTH_SHORT).show();
            return;
        }
        model = getRandom();
        randomAnswers = randomNumbers();
        question.setText(model.getQuestionText());

        List<String> answers = new ArrayList<String>() {{
            add(model.getQuestionA1());
            add(model.getQuestionA2());
            add(model.getQuestionA3());
            add(model.getQuestionA4());
        }};
        correctAnswer = answers.get(Integer.parseInt(model.getQuestionCorrect()) - 1);
        answer1.setText(answers.get(randomAnswers.get(0)));
        answer2.setText(answers.get(randomAnswers.get(1)));
        answer3.setText(answers.get(randomAnswers.get(2)));
        answer4.setText(answers.get(randomAnswers.get(3)));

        downTimer.start();
        setAnswer("0");
        uploadAnswer("0");

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
    @Override
    protected void onPause() {
        super.onPause();
        if(mediaPlayer!=null)
            mediaPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mediaPlayer!=null)
            mediaPlayer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPool.release();
        soundPool = null;
        mediaPlayer.release();
        mediaPlayer=null;

    }
}