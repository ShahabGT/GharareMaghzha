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

import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.data.RetrofitClient;
import ir.ghararemaghzha.game.models.GeneralResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


public class RegisterFragment extends Fragment {
    private Context context;
    private FragmentActivity activity;
    private static final int RESOLVE_HINT = 521;


    public RegisterFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        context = getContext();
        activity = getActivity();
        init(v);

        //SmsRetriever.getClient(getActivity()).startSmsUserConsent("98300077");


        return v;
    }

    private void init(View v) {
        try {
            requestHint();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                    String number = credential.getId();
                    if (number.startsWith("+98"))
                        number = "0" + number.substring(3);
                    else if (number.startsWith("0098"))
                        number = "0" + number.substring(4);
                    // fNumber.setText(number);
                }
            }
        }
    }

    private void doRegister(String name, String number) {
        RetrofitClient.getInstance().getApi()
                .registerUser(name, number)
                .enqueue(new Callback<GeneralResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getResult().equals("success")) {
                            SmsRetriever.getClient(activity).startSmsUserConsent("98300077");
                            VerifyFragment verifyFragment = new VerifyFragment();
                            verifyFragment.setNumber(number);
                            activity.getSupportFragmentManager().popBackStack();
                            activity.getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.enter_right, R.anim.exit_left)
                                    .replace(R.id.register_container, verifyFragment)
                                    .commit();

                        } else if (response.code() == 409) {
                            Toast.makeText(context, context.getResources().getText(R.string.registerfragment_conflict), Toast.LENGTH_SHORT).show();
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
}