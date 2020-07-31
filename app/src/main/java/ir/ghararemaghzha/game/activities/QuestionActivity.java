package ir.ghararemaghzha.game.activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ir.ghararemaghzha.game.R;

public class QuestionActivity extends AppCompatActivity {
    private int progress=100;
    private int time=20;
    private CountDownTimer downTimer;
    private ProgressBar progressBar;
    private TextView timeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_question);
        init();
    }

    private void init(){
        progressBar = findViewById(R.id.question_progress_bar);
        progressBar.setProgress(progress);
        timeText = findViewById(R.id.question_progress_text);
        downTimer = new CountDownTimer(20000,1000) {
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
        nextQuestion();
    }

    private void nextQuestion() {
        downTimer.cancel();
        time = 20;
        progress = 100;
        timeText.setText(String.valueOf(time));
        progressBar.setProgress(100);
        downTimer.start();
    }
}