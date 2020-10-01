package ir.ghararemaghzha.game.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

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

public class InviteFragment extends Fragment {
    private Context context;
    private FragmentActivity activity;
    private MaterialTextView count, value;
    private RoundCornerProgressBar progressBar;
    private float fCount = 0f;
    private ConstraintLayout loading;

    public InviteFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_invite, container, false);
        context = getContext();
        activity = getActivity();

        init(v);
        return v;
    }

    private void init(View v) {
        ((MaterialTextView) activity.findViewById(R.id.toolbar_title)).setText(R.string.invite_title);
        loading = v.findViewById(R.id.invite_loading);
        count = v.findViewById(R.id.invite_count);
        value = v.findViewById(R.id.invite_info_text1);
        progressBar = v.findViewById(R.id.invite_progress);

        ((MaterialTextView) v.findViewById(R.id.invite_code)).setText(MySharedPreference.getInstance(context).getUserCode());
        getData();
        onClicks(v);
    }

    private void onClicks(View v) {
        v.findViewById(R.id.invite_share).setOnClickListener(vv -> Utils.shareCode(activity, getString(R.string.invite_share, MySharedPreference.getInstance(context).getUserCode())));
    }

    private void getData() {
        String number = MySharedPreference.getInstance(context).getNumber();
        String token = MySharedPreference.getInstance(context).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(activity, true);
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
                            fCount = Float.parseFloat(response.body().getCount())+0f;
                            int remaining = 10 - (int) fCount;
                            count.setText(getString(R.string.invite_star_title, remaining));
                            progressBar.setProgress(fCount);
                            value.setText(getString(R.string.invite_info_text1, response.body().getValue()));

                        } else if (response.code() == 401) {
                            Utils.logout(activity, true);
                        } else {
                            Utils.showInternetError(context, () -> getData());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                        loading.setVisibility(View.GONE);
                        Utils.showInternetError(context, () -> getData());

                    }
                });


    }
}