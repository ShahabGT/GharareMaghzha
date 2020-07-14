package ir.ghararemaghzha.game.fragments;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.HintRequest;

import ir.ghararemaghzha.game.R;

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

        //listen to sms reciver
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
}