package ir.ghararemaghzha.game.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.textview.MaterialTextView;
import com.willy.ratingbar.ScaleRatingBar;

import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.classes.MySharedPreference;
import ir.ghararemaghzha.game.classes.Utils;
import ir.ghararemaghzha.game.data.RetrofitClient;
import ir.ghararemaghzha.game.models.UserDetailsResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDetailsDialog extends Dialog {

    private final Activity context;
    private final String userId;
    private MaterialTextView name, code, rank, score, questions, answers, questionsPercent, answersPercent, rateText;
    private ProgressBar answersProgress, questionsProgress;
    private ImageView avatar;
    private ScaleRatingBar ratingBar;

    private ConstraintLayout loading;

    public UserDetailsDialog(@NonNull Activity context, String userId) {
        super(context);
        this.context = context;
        this.userId = userId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_userdetails);
        init();
    }

    private void init() {
        loading = findViewById(R.id.details_loading);

        ratingBar = findViewById(R.id.details_rate);
        ratingBar.setRating(0f);
        ratingBar.setClickable(false);
        ratingBar.setScrollable(false);
        ratingBar.setStarPadding(8);
        rateText = findViewById(R.id.details_total);
        name = findViewById(R.id.details_name2);
        code = findViewById(R.id.details_code);
        rank = findViewById(R.id.details_rank2);
        score = findViewById(R.id.details_rate_text);
        questions = findViewById(R.id.details_questions);
        questionsPercent = findViewById(R.id.details_questions_percent);
        answers = findViewById(R.id.details_answers);
        answersPercent = findViewById(R.id.details_answers_percent);

        avatar = findViewById(R.id.details_avatar);

        answersProgress = findViewById(R.id.details_answers_progress);
        answersProgress.setProgress(0);
        answersProgress.setMax(100);

        questionsProgress = findViewById(R.id.details_questions_progress);
        questionsProgress.setProgress(0);
        questionsProgress.setMax(100);

        findViewById(R.id.details_close).setOnClickListener(v -> dismiss());

        getData();
    }

    private void getData() {
        String number = MySharedPreference.getInstance(context).getNumber();
        String token = MySharedPreference.getInstance(context).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(context, true);
            return;
        }
        RetrofitClient.Companion.getInstance().getApi()
                .getUserDetails("Bearer " + token, number, userId)
                .enqueue(new Callback<UserDetailsResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<UserDetailsResponse> call, @NonNull Response<UserDetailsResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getResult().equals("success")) {
                            if (response.body().getMessage().equals("ok")) {
                                name.setText(response.body().getUserData().getUserName());
                                code.setText(context.getString(R.string.details_code, response.body().getUserData().getUserCode()));
                                rank.setText(response.body().getUserData().getUserRank());
                                score.setText(response.body().getUserData().getScoreCount());
                                if (Integer.parseInt(response.body().getBooster()) < 1500)
                                    answers.setText(context.getString(R.string.details_nitro2, response.body().getBooster()));
                                else
                                    answers.setText(context.getString(R.string.details_nitro2, "1500"));

                                Glide.with(context)
                                        .load(context.getString(R.string.avatar_url, response.body().getUserData().getUserAvatar()))
                                        .circleCrop()
                                        .placeholder(R.drawable.placeholder)
                                        .into(avatar);

                                int totalQuestions = (Integer.parseInt(response.body().getPlan()) * 500) + 500;
                                int answeredQuestions = response.body().getCorrect() + response.body().getIncorrect();
                                int nitroUsed = Integer.parseInt(response.body().getBooster());

                                if (answeredQuestions <= 3000) {
                                    questions.setText(context.getString(R.string.details_questions, answeredQuestions, totalQuestions));
                                } else {
                                    questions.setText(context.getString(R.string.details_questions, 3000, totalQuestions));
                                }

                                int qPercent;
                                int nPercent;
                                if (answeredQuestions > 0 && answeredQuestions <= 3000) {
                                    qPercent = (answeredQuestions * 100) / totalQuestions;
                                    nPercent = (nitroUsed * 100) / 1500;
                                } else if (answeredQuestions > 3000) {
                                    qPercent = (answeredQuestions * 100) / totalQuestions;
                                    nPercent = (nitroUsed * 100) / 1500;
                                } else {
                                    qPercent = 0;
                                    nPercent = 0;
                                }
                                new Handler().postDelayed(() -> {
                                    if (Build.VERSION.SDK_INT >= 24) {
                                        answersProgress.setProgress(nPercent, true);
                                        questionsProgress.setProgress(qPercent, true);
                                    } else {
                                        answersProgress.setProgress(nPercent);
                                        questionsProgress.setProgress(qPercent);
                                    }
                                }, 500);

                                answersPercent.setText("%" + nPercent);

                                questionsPercent.setText("%" + qPercent);

                                int rate = Integer.parseInt(response.body().getRank());
                                int level1 = Integer.parseInt(response.body().getLevel1());
                                int level2 = Integer.parseInt(response.body().getLevel2());
                                int level3 = Integer.parseInt(response.body().getLevel3());
                                int level4 = Integer.parseInt(response.body().getLevel4());
                                rateText.setText( String.valueOf(rate));
                                if (rate < level1) {
                                    ratingBar.setRating(0f);
                                } else if (rate < level2)
                                    ratingBar.setRating(1f);
                                else if (rate < level3)
                                    ratingBar.setRating(2f);
                                else if (rate < level4)
                                    ratingBar.setRating(3f);
                                else
                                    ratingBar.setRating(4f);


                                loading.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(context, context.getString(R.string.general_error), Toast.LENGTH_SHORT).show();
                                dismiss();
                            }
                        } else if (response.code() == 401) {
                            dismiss();
                            Utils.logout(context, true);
                        } else {
                            Toast.makeText(context, context.getString(R.string.general_error), Toast.LENGTH_SHORT).show();
                            dismiss();
                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<UserDetailsResponse> call, @NonNull Throwable t) {
                        Toast.makeText(context, context.getString(R.string.general_error), Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                });


    }

    @Override
    public void dismiss() {
        answersProgress.setProgress(0);
        questionsProgress.setProgress(0);
        super.dismiss();

    }
}
