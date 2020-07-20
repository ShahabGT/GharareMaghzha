package ir.ghararemaghzha.game.fragments;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.classes.Utils;
import ir.ghararemaghzha.game.data.RetrofitClient;
import ir.ghararemaghzha.game.models.GeneralResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


public class LoginFragment extends Fragment {

    private static final int RESOLVE_HINT = 521;

    private Context context;
    private FragmentActivity activity;
    private MaterialTextView register;
    private TextInputEditText number;
    private MaterialButton verify;


    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        context = getContext();
        activity = getActivity();
        init(v);

        return v;
    }

    private void init(View v) {
        register = v.findViewById(R.id.login_register);
        number = v.findViewById(R.id.login_number);
        verify = v.findViewById(R.id.login_verify);

        try {
            requestHint();
        }catch (Exception e){
            e.printStackTrace();
        }

        onClicks();
    }

    private void onClicks() {
        verify.setOnClickListener(v -> {
            String n = number.getText().toString();
            if (n.length() < 11 || !n.startsWith("09")) {
                Toast.makeText(context, context.getString(R.string.general_number_error), Toast.LENGTH_SHORT).show();
            } else {
                if (Utils.checkInternet(context))
                    doLogin(n);
                else
                    Toast.makeText(context, context.getString(R.string.general_internet_error), Toast.LENGTH_SHORT).show();
            }


        });

        register.setOnClickListener(v -> {
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_left, R.anim.exit_right, R.anim.enter_right, R.anim.exit_left)
                    .add(R.id.register_container, new RegisterFragment())
                    .addToBackStack("login")
                    .commit();
        });


    }

    private void doLogin(String number) {
        verify.setEnabled(false);
        verify.setText("...");
        register.setEnabled(false);
        RetrofitClient.getInstance().getApi()
                .login(number)
                .enqueue(new Callback<GeneralResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                        verify.setEnabled(true);
                        verify.setText(context.getString(R.string.loginfragment_verify));
                        register.setEnabled(true);
                        if (response.isSuccessful() && response.body() != null && response.body().getResult().equals("success")) {
                            SmsRetriever.getClient(activity).startSmsUserConsent("98300077");
                            VerifyFragment verifyFragment = new VerifyFragment();
                            verifyFragment.setNumber(number);
                            activity.getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.enter_left, R.anim.exit_right)
                                    .replace(R.id.register_container, verifyFragment)
                                    .commit();


                        } else if (response.code() == 401) {
                            Toast.makeText(context, context.getResources().getText(R.string.loginfragment_nouser), Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 403) {
                            Toast.makeText(context, context.getResources().getText(R.string.loginfragment_blocked), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, context.getString(R.string.general_error), Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                        verify.setEnabled(true);
                        verify.setText(context.getString(R.string.loginfragment_verify));
                        register.setEnabled(true);
                        Toast.makeText(context, context.getString(R.string.general_error), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void requestHint() throws Exception {
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();
        PendingIntent intent = Credentials.getClient(context).getHintPickerIntent(hintRequest);
        startIntentSenderForResult(intent.getIntentSender(),
                RESOLVE_HINT, null, 0, 0, 0, null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == RESULT_OK && data != null) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                if (credential != null) {
                    String n = credential.getId();
                    if (n.startsWith("+98"))
                        n = "0" + n.substring(3);
                    else if (n.startsWith("0098"))
                        n = "0" + n.substring(4);
                    number.setText(n);
                }
            }
        }
    }
}