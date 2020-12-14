package ir.ghararemaghzha.game.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.adapters.BuyHistoryAdapter;
import ir.ghararemaghzha.game.classes.MySharedPreference;
import ir.ghararemaghzha.game.classes.Utils;
import ir.ghararemaghzha.game.data.RetrofitClient;
import ir.ghararemaghzha.game.models.BuyHistoryResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuyHistoryFragment extends Fragment {
    private Context context;
    private FragmentActivity activity;
    private RecyclerView recyclerView;
    private BuyHistoryAdapter adapter;
    private ConstraintLayout loading;
    private ConstraintLayout empty;

    public BuyHistoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_buy_history, container, false);
        context = getContext();
        activity = getActivity();
        init(v);
        return v;
    }


    private void init(View v) {
        ((MaterialTextView) activity.findViewById(R.id.toolbar_title)).setText(R.string.buyhistory_title);

        loading = v.findViewById(R.id.buyhistory_loading);
        empty = v.findViewById(R.id.buyhistory_empty);
        recyclerView = v.findViewById(R.id.buyhistory_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        getData();

    }

    private void getData() {
        String number = MySharedPreference.Companion.getInstance(context).getNumber();
        String token = MySharedPreference.Companion.getInstance(context).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(activity, true);
            return;
        }
        loading.setVisibility(View.VISIBLE);
        RetrofitClient.Companion.getInstance().getApi()
                .getBuyHistory("Bearer " + token, number)
                .enqueue(new Callback<BuyHistoryResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<BuyHistoryResponse> call, @NonNull Response<BuyHistoryResponse> response) {
                        loading.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null && response.body().getResult().equals("success")) {
                            if (response.body().getMessage().equals("all history")) {
                                adapter = new BuyHistoryAdapter(context, response.body().getData());
                                recyclerView.setAdapter(adapter);
                            } else {
                                empty.setVisibility(View.VISIBLE);
                            }
                        } else if (response.code() == 401) {
                            Utils.logout(activity, true);
                        } else {
                            Toast.makeText(context, getString(R.string.general_error), Toast.LENGTH_SHORT).show();
                            empty.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<BuyHistoryResponse> call, @NonNull Throwable t) {
                        loading.setVisibility(View.GONE);
                        Toast.makeText(context, getString(R.string.general_error), Toast.LENGTH_SHORT).show();
                        empty.setVisibility(View.VISIBLE);
                    }
                });
    }
}