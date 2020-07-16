package ir.ghararemaghzha.game.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.auth.api.phone.SmsRetriever;

import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.data.RetrofitClient;
import ir.ghararemaghzha.game.models.GeneralResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginFragment extends Fragment {

    private Context context;
    private FragmentActivity activity;


    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        context = getContext();
        activity = getActivity();

        return v;
    }

    private void doLogin(String number) {
        RetrofitClient.getInstance().getApi()
                .login(number)
                .enqueue(new Callback<GeneralResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getResult().equals("success")) {
                            SmsRetriever.getClient(activity).startSmsUserConsent("98300077");
                            VerifyFragment verifyFragment = new VerifyFragment();
                            verifyFragment.setNumber(number);
                            activity.getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.enter_right, R.anim.exit_left)
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
                        Toast.makeText(context, context.getString(R.string.general_error), Toast.LENGTH_SHORT).show();
                    }
                });


    }
}