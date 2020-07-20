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

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.activities.MainActivity;
import ir.ghararemaghzha.game.classes.MySharedPreference;
import ir.ghararemaghzha.game.classes.Utils;
import ir.ghararemaghzha.game.data.RetrofitClient;
import ir.ghararemaghzha.game.models.GeneralResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ir.ghararemaghzha.game.classes.Const.FCM_TOPIC;
import static ir.ghararemaghzha.game.classes.Utils.convertToTimeFormat;


public class VerifyFragment extends Fragment {

    private Context context;
    private FragmentActivity activity;
    private MaterialTextView resend;
    private TextInputEditText code;
    private MaterialButton verify;
    private CountDownTimer timer;
    private long timerTime = 120000;

    private String number;

    public VerifyFragment() {
    }

    private BroadcastReceiver rec = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                String c = intent.getExtras().getString("code");
                if (c != null && c.length() == 6) {
                    code.setText(c);

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
        resend = v.findViewById(R.id.verify_resend);
        resend.setEnabled(false);
        code = v.findViewById(R.id.verify_code);
        verify = v.findViewById(R.id.verify_verify);
        SmsRetriever.getClient(activity).startSmsUserConsent("98300077");
        initTimer();
        onClicks();

    }

    private void onClicks() {
        resend.setOnClickListener(v -> {
            doResend();
        });
        verify.setOnClickListener(v -> {
            String c = code.getText().toString();
            if (c.length() < 6 || c.startsWith("0")) {
                Toast.makeText(context, context.getString(R.string.verify_wrong_code), Toast.LENGTH_SHORT).show();
            } else {
                if (Utils.checkInternet(context))
                    doVerify(c);
                else
                    Toast.makeText(context, context.getString(R.string.general_internet_error), Toast.LENGTH_SHORT).show();
            }


        });


    }

    private void initTimer() {
        timer = new CountDownTimer(timerTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                resend.setText(convertToTimeFormat(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                try {
                    resend.setEnabled(true);
                    resend.setText(getString(R.string.verify_resend));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        timer.start();
    }

    private void doResend() {
        RetrofitClient.getInstance().getApi()
                .resend(number)
                .enqueue(new Callback<GeneralResponse>() {
                    @Override
                    public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getResult().equals("success")) {
                            Toast.makeText(context, context.getString(R.string.general_send), Toast.LENGTH_SHORT).show();
                            timerTime *= 2;
                            timer.cancel();
                            initTimer();

                        } else {
                            Toast.makeText(context, context.getString(R.string.general_error), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<GeneralResponse> call, Throwable t) {
                        Toast.makeText(context, context.getString(R.string.general_error), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void doVerify(String code) {
        String fbToken = Utils.getFbToken(context);
        RetrofitClient.getInstance().getApi()
                .verification(number, code, fbToken)
                .enqueue(new Callback<GeneralResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getResult().equals("success")) {
                            FirebaseMessaging.getInstance().subscribeToTopic(FCM_TOPIC);
                            String userId = response.body().getUserId();
                            String userName = response.body().getUserName();
                            String accessToken = response.body().getToken();
                            String userCode = response.body().getUserCode();
                            MySharedPreference.getInstance(context).setNumber(number);
                            MySharedPreference.getInstance(context).setUserId(userId);
                            MySharedPreference.getInstance(context).setUsername(userName);
                            MySharedPreference.getInstance(context).setAccessToken(accessToken);
                            MySharedPreference.getInstance(context).setUserCode(userCode);
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