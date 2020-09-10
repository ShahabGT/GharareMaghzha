package ir.ghararemaghzha.game.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.Objects;

import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.adapters.BuyAdapter;
import ir.ghararemaghzha.game.classes.BuyInterface;
import ir.ghararemaghzha.game.classes.MySharedPreference;
import ir.ghararemaghzha.game.classes.Utils;
import ir.ghararemaghzha.game.data.RetrofitClient;
import ir.ghararemaghzha.game.dialogs.BuyDialog;
import ir.ghararemaghzha.game.models.GeneralResponse;
import ir.ghararemaghzha.game.models.PlanResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuyFragment extends Fragment {
    private Context context;
    private FragmentActivity activity;
    private ConstraintLayout loading;

    private RecyclerView recyclerView;
    private BuyAdapter adapter;

    public BuyFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_buy, container, false);
        context = getContext();
        activity = getActivity();

        init(v);

        return v;
    }

    private void init(View v) {
        if (!Utils.isBoosterValid(MySharedPreference.getInstance(context).getBoosterDate())) {
            MySharedPreference.getInstance(context).setBoosterValue(Float.parseFloat("1"));
        }
        ((MaterialTextView) activity.findViewById(R.id.toolbar_title)).setText(R.string.buy_title);

        recyclerView = v.findViewById(R.id.buy_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        loading = v.findViewById(R.id.buy_loading);

        getData();

    }


    private void showBuyDialog(String planPrice, String Plan, BuyInterface buyInterface) {
        BuyDialog dialog = new BuyDialog(activity, planPrice, Plan, buyInterface);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void getData() {

        String number = MySharedPreference.getInstance(context).getNumber();
        String token = MySharedPreference.getInstance(context).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(activity, true);
            return;
        }

        RetrofitClient.getInstance().getApi()
                .getPlans("Bearer " + token, number)
                .enqueue(new Callback<PlanResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<PlanResponse> call, @NonNull Response<PlanResponse> response) {
                        loading.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null && response.body().getResult().equals("success")) {
                            adapter = new BuyAdapter(context, response.body().getData(),
                                    (pPlan, pPrice, a, b) ->
                                            showBuyDialog(pPrice, pPlan, (plan, amount, influencerId, influencerAmount) ->
                                                    initBuy(plan, influencerId, influencerAmount, amount))
                            );

                            recyclerView.setAdapter(adapter);
                        } else if (response.code() == 401) {
                            Utils.logout(activity, true);
                        } else {
                            Utils.showInternetError(context, () -> getData());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<PlanResponse> call, @NonNull Throwable t) {
                        loading.setVisibility(View.GONE);
                        Utils.showInternetError(context, () -> getData());

                    }
                });

    }

    private void initBuy(String plan, String influencerId, String influencerAmount, String amount) {
        String number = MySharedPreference.getInstance(activity).getNumber();
        String token = MySharedPreference.getInstance(activity).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(activity, true);
            return;
        }
        loading.setVisibility(View.VISIBLE);
        RetrofitClient.getInstance().getApi()
                .initBuy("Bearer " + token, number, plan, influencerId, influencerAmount, amount)
                .enqueue(new Callback<GeneralResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                        loading.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getMerchantId() != null && !response.body().getMerchantId().isEmpty()) {
                            String merchant = response.body().getMerchantId();
                            String url = "https://ghararehmaghzha.ir/api/buy/buy?merchant=" + merchant;
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            context.startActivity(i);

                        } else if (response.code() == 401) {
                            Utils.logout(activity, true);
                        } else {
                            Toast.makeText(context, context.getString(R.string.general_error), Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                        loading.setVisibility(View.GONE);
                        Toast.makeText(context, context.getString(R.string.general_error), Toast.LENGTH_SHORT).show();

                    }
                });
    }


}