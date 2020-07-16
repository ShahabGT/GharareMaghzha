package ir.ghararemaghzha.game.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.activities.MainActivity;
import ir.ghararemaghzha.game.classes.MySharedPreference;
import ir.ghararemaghzha.game.classes.Utils;
import ir.ghararemaghzha.game.data.RetrofitClient;
import ir.ghararemaghzha.game.models.GeneralResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ir.ghararemaghzha.game.classes.Utils.convertToTimeFormat;


public class VerifyFragment extends Fragment {

    private Context context;
    private FragmentActivity activity;

    private String number;

    public VerifyFragment() {
    }

    private BroadcastReceiver rec = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                String code = intent.getExtras().getString("code");
                if (code != null && code.length() == 6) {
                    doVerify(code);
                }
            }
        }
    };

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_verify, container, false);
        context = getContext();
        activity = getActivity();
        context.registerReceiver(rec, new IntentFilter("codeReceived"));
        init(v);

        return v;
    }

    private void init(View v) {
        initTimer();

    }
    private void initTimer(){
        CountDownTimer timer = new CountDownTimer(60000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
              //  fResend.setText(convertToTimeFormat(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                try {
                //    fResend.setEnabled(true);
                //    fResend.setText(getString(R.string.code_resend));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    private void doVerify(String code) {
        String fbToken = Utils.getFbToken(context);
        RetrofitClient.getInstance().getApi()
                .verification(number, code, fbToken)
                .enqueue(new Callback<GeneralResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getResult().equals("success")) {
                            String userId = response.body().getUserId();
                            String userName = response.body().getUserName();
                            String accessToken = response.body().getToken();
                            MySharedPreference.getInstance(context).setNumber(number);
                            MySharedPreference.getInstance(context).setUserId(userId);
                            MySharedPreference.getInstance(context).setUsername(userName);
                            MySharedPreference.getInstance(context).setAccessToken(accessToken);
                            Toast.makeText(context, context.getString(R.string.verify_welcome, userName), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(activity, MainActivity.class));
                            activity.overridePendingTransition(R.anim.enter_right, R.anim.exit_left);
                            activity.finish();

                        } else if (response.code() == 401) {
                            Toast.makeText(context, context.getString(R.string.verify_wrong_code), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, context.getString(R.string.general_error), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                        Toast.makeText(context, context.getString(R.string.general_error), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroy() {
        context.unregisterReceiver(rec);
        super.onDestroy();
    }
}