package ir.ghararemaghzha.game.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
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

import io.realm.Realm;
import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.activities.MainActivity;
import ir.ghararemaghzha.game.classes.MySharedPreference;
import ir.ghararemaghzha.game.classes.Utils;
import ir.ghararemaghzha.game.data.RetrofitClient;
import ir.ghararemaghzha.game.dialogs.GetDataDialog;
import ir.ghararemaghzha.game.models.GeneralResponse;
import ir.ghararemaghzha.game.models.QuestionModel;
import ir.ghararemaghzha.game.models.QuestionResponse;
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

    private String userName;
    private String accessToken;
    private String number;
    private String userId;

    private GetDataDialog dialog;

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

        code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 6)
                    Utils.hideKeyboard(activity);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 6)
                    Utils.hideKeyboard(activity);
            }
        });

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
        Utils.hideKeyboard(activity);
        verify.setEnabled(false);
        verify.setText("...");
        String fbToken = Utils.getFbToken(context);
        RetrofitClient.getInstance().getApi()
                .verification(number, code, fbToken)
                .enqueue(new Callback<GeneralResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getResult().equals("success")) {
                            FirebaseMessaging.getInstance().subscribeToTopic(FCM_TOPIC);
                            userId = response.body().getUserId();
                            userName = response.body().getUserName();
                            accessToken = response.body().getToken();
                            String userCode = response.body().getUserCode();
                            String score = response.body().getUserScore();
                            String plan = response.body().getUserPlan();
                            MySharedPreference.getInstance(context).setNumber(number);
                            MySharedPreference.getInstance(context).setUsername(userName);
                            MySharedPreference.getInstance(context).setAccessToken(accessToken);
                            MySharedPreference.getInstance(context).setUserCode(userCode);
                            MySharedPreference.getInstance(context).setScore(score);
                            MySharedPreference.getInstance(context).setPlan(plan);
                            MySharedPreference.getInstance(context).setUserSex(response.body().getUserSex());
                            MySharedPreference.getInstance(context).setUserBday(response.body().getUserBday());
                            MySharedPreference.getInstance(context).setUserEmail(response.body().getUserEmail());
                            MySharedPreference.getInstance(context).setUserInvite(response.body().getUserInvite());
                            dialog = Utils.showGetDataLoading(context);
                            getQuestions();


                        } else if (response.code() == 401) {
                            verify.setEnabled(true);
                            verify.setText(context.getString(R.string.verify_verify));
                            Toast.makeText(context, context.getString(R.string.verify_wrong_code), Toast.LENGTH_SHORT).show();
                        } else {
                            verify.setEnabled(true);
                            verify.setText(context.getString(R.string.verify_verify));
                            Toast.makeText(context, context.getString(R.string.general_error), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                        verify.setEnabled(true);
                        verify.setText(context.getString(R.string.verify_verify));
                        Toast.makeText(context, context.getString(R.string.general_error), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getQuestions() {
        Realm db = Realm.getDefaultInstance();
        if (number.isEmpty() || accessToken.isEmpty()) {
            Utils.logout(activity,true);
            return;
        }
        RetrofitClient.getInstance().getApi()
                .getQuestions("Bearer " + accessToken, number, "0", "0")
                .enqueue(new Callback<QuestionResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<QuestionResponse> call, @NonNull Response<QuestionResponse> response) {
                        verify.setEnabled(true);
                        verify.setText(context.getString(R.string.verify_verify));
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getResult().equals("success")
                                && !response.body().getMessage().equals("empty")) {
                            for (QuestionModel model : response.body().getData()) {
                                if (model.getUserAnswer().equals("-1")) {
                                    model.setUploaded(false);
                                } else {
                                    model.setUploaded(true);
                                }
                                model.setVisible(false);
                                db.executeTransaction(realm1 -> realm1.insertOrUpdate(model));
                            }
                            db.close();


                            MySharedPreference.getInstance(context).setUserId(userId);
                            Toast.makeText(context, context.getString(R.string.verify_welcome, userName), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            startActivity(new Intent(activity, MainActivity.class));
                            activity.overridePendingTransition(R.anim.enter_right, R.anim.exit_left);
                            activity.finish();
                        } else {
                            Toast.makeText(context, context.getString(R.string.general_error), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<QuestionResponse> call, @NonNull Throwable t) {
                        Toast.makeText(context, context.getString(R.string.general_error), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        verify.setEnabled(true);
                        verify.setText(context.getString(R.string.verify_verify));
                    }
                });


    }


    @Override
    public void onDestroy() {
        context.unregisterReceiver(rec);
        super.onDestroy();
    }
}