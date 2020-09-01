package ir.ghararemaghzha.game.fragments;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

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


public class RegisterFragment extends Fragment {
    private Context context;
    private FragmentActivity activity;
    private static final int RESOLVE_HINT = 521;
    private MaterialTextView login;
    private TextInputEditText number, name;
    private MaterialButton verify;

    private String loginNumber;
    private NavController navController;


    public RegisterFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        context = getContext();
        activity = getActivity();

        if(getArguments()!=null)
            loginNumber = getArguments().getString("number");

        init(v);

        //SmsRetriever.getClient(getActivity()).startSmsUserConsent("98300077");


        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    private void init(View v) {
        name = v.findViewById(R.id.reg_name);
        number = v.findViewById(R.id.reg_number);
        verify = v.findViewById(R.id.reg_verify);
        login = v.findViewById(R.id.reg_login);

        if(loginNumber==null || loginNumber.isEmpty()) {

            try {
                requestHint();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else
            number.setText(loginNumber);

        number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()==11)
                    Utils.hideKeyboard(activity);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length()==11)
                    Utils.hideKeyboard(activity);
            }
        });
        onClicks();

    }

    private void onClicks() {
        verify.setOnClickListener(v -> {
            String nu = number.getText().toString();
            String na = name.getText().toString();

            if (nu.length() < 11 || !nu.startsWith("09")) {
                Toast.makeText(context, context.getString(R.string.general_number_error), Toast.LENGTH_SHORT).show();
            } else if (na.length() < 6) {
                Toast.makeText(context, context.getString(R.string.general_name_error), Toast.LENGTH_SHORT).show();
            } else {
                if (Utils.checkInternet(context))
                    doRegister(na, nu);
                else
                    Toast.makeText(context, context.getString(R.string.general_internet_error), Toast.LENGTH_SHORT).show();


            }

        });

        login.setOnClickListener(view ->
                navController.popBackStack()
        );


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

    private void doRegister(String name, String number) {
        Utils.hideKeyboard(activity);
        verify.setEnabled(false);
        verify.setText("...");
        login.setEnabled(false);
        RetrofitClient.getInstance().getApi()
                .registerUser(name, number)
                .enqueue(new Callback<GeneralResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                        verify.setEnabled(true);
                        verify.setText(context.getString(R.string.loginfragment_verify));
                        login.setEnabled(true);
                        if (response.isSuccessful() && response.body() != null && response.body().getResult().equals("success")) {
                            SmsRetriever.getClient(activity).startSmsUserConsent("98300077");
                            Bundle b = new Bundle();
                            b.putString("number",number);
                            navController.navigate(R.id.action_registerFragment_to_verifyFragment);

                        } else if (response.code() == 409) {
                            Toast.makeText(context, context.getResources().getText(R.string.registerfragment_conflict), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, context.getString(R.string.general_error), Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                        verify.setEnabled(true);
                        verify.setText(context.getString(R.string.loginfragment_verify));
                        login.setEnabled(true);
                        Toast.makeText(context, context.getString(R.string.general_error), Toast.LENGTH_SHORT).show();
                    }
                });

    }

}