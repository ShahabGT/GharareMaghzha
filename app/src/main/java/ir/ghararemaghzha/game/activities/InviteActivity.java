package ir.ghararemaghzha.game.activities;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.google.android.material.textview.MaterialTextView;
import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.classes.MySharedPreference;
import ir.ghararemaghzha.game.classes.Utils;
import ir.ghararemaghzha.game.data.RetrofitClient;
import ir.ghararemaghzha.game.models.GeneralResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InviteActivity extends AppCompatActivity {

    private MaterialTextView count, value;
    private RoundCornerProgressBar progressBar;
    private float fCount = 0f;
    private ConstraintLayout loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        init();
    }

    private void init() {
        loading = findViewById(R.id.invite_loading);
        count = findViewById(R.id.invite_count);
        value = findViewById(R.id.invite_info_text1);
        progressBar = findViewById(R.id.invite_progress);

        ((MaterialTextView) findViewById(R.id.invite_code)).setText(MySharedPreference.getInstance(this).getUserCode());
        getData();
        onClicks();
    }

    private void onClicks() {
        findViewById(R.id.invite_share).setOnClickListener(v -> Utils.shareCode(InviteActivity.this, getString(R.string.invite_share, MySharedPreference.getInstance(this).getUserCode())));
        findViewById(R.id.invite_close).setOnClickListener(v->onBackPressed());
    }

    private void getData() {
        String number = MySharedPreference.getInstance(this).getNumber();
        String token = MySharedPreference.getInstance(this).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(this, true);
            return;
        }
        loading.setVisibility(View.VISIBLE);

        RetrofitClient.getInstance().getApi()
                .getInvites("Bearer " + token, number)
                .enqueue(new Callback<GeneralResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                        loading.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null && response.body().getResult().equals("success")) {
                            fCount = Float.parseFloat(response.body().getCount());
                            int remaining = 10 - (int) fCount;
                            count.setText(getString(R.string.invite_star_title, remaining));
                            progressBar.setProgress(fCount);
                            value.setText(getString(R.string.invite_info_text1, response.body().getValue()));

                        } else if (response.code() == 401) {
                            Utils.logout(InviteActivity.this, true);
                        } else {
                            Utils.showInternetError(InviteActivity.this, () -> getData());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                        loading.setVisibility(View.GONE);
                        Utils.showInternetError(InviteActivity.this, () -> getData());

                    }
                });


    }
}