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
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import java.util.List;
import java.util.Objects;
import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.classes.BuyInterface;
import ir.ghararemaghzha.game.classes.MySharedPreference;
import ir.ghararemaghzha.game.classes.Utils;
import ir.ghararemaghzha.game.data.RetrofitClient;
import ir.ghararemaghzha.game.dialogs.BuyDialog;
import ir.ghararemaghzha.game.models.GeneralResponse;
import ir.ghararemaghzha.game.models.PlanModel;
import ir.ghararemaghzha.game.models.PlanResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuyFragment extends Fragment {
    private Context context;
    private FragmentActivity activity;
    private ConstraintLayout loading;
    private MaterialCardView one, two, three, four, five;
    private MaterialTextView onePrice, twoPrice, threePrice, fourPrice, fivePrice;
    private View oneView, twoView, threeView, fourView, fiveView;
    private List<PlanModel> models;

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
        ((MaterialTextView)activity.findViewById(R.id.toolbar_title)).setText(R.string.buy_title);

        loading = v.findViewById(R.id.buy_loading);

        one = v.findViewById(R.id.buy_one_layout);
        onePrice = v.findViewById(R.id.buy_one_price);
        oneView = v.findViewById(R.id.buy_one_disable);

        two = v.findViewById(R.id.buy_two_layout);
        twoPrice = v.findViewById(R.id.buy_two_price);
        twoView = v.findViewById(R.id.buy_two_disable);

        three = v.findViewById(R.id.buy_three_layout);
        threePrice = v.findViewById(R.id.buy_three_price);
        threeView = v.findViewById(R.id.buy_three_disable);

        four = v.findViewById(R.id.buy_four_layout);
        fourPrice = v.findViewById(R.id.buy_four_price);
        fourView = v.findViewById(R.id.buy_four_disable);

        five = v.findViewById(R.id.buy_five_layout);
        fivePrice = v.findViewById(R.id.buy_five_price);
        fiveView = v.findViewById(R.id.buy_five_disable);

        int userPlan = Integer.parseInt(MySharedPreference.getInstance(context).getPlan());
        switch (userPlan){
            case 5:
                oneView.setVisibility(View.VISIBLE);
            case 4:
                twoView.setVisibility(View.VISIBLE);
            case 3:
                threeView.setVisibility(View.VISIBLE);
            case 2:
                fourView.setVisibility(View.VISIBLE);
            case 1:
                fiveView.setVisibility(View.VISIBLE);
        }

        getData();
        onClicks();


    }

    private void onClicks() {
        one.setOnClickListener(v -> showBuyDialog(models.get(1).getPlanPrice(), "1", (plan, amount, influencerId, influencerAmount) -> initBuy(plan, influencerId, influencerAmount, amount)));
        two.setOnClickListener(v -> showBuyDialog(models.get(2).getPlanPrice(), "2", (plan, amount, influencerId, influencerAmount) -> initBuy(plan, influencerId, influencerAmount, amount)));
        three.setOnClickListener(v -> showBuyDialog(models.get(3).getPlanPrice(), "3", (plan, amount, influencerId, influencerAmount) -> initBuy(plan, influencerId, influencerAmount, amount)));
        four.setOnClickListener(v -> showBuyDialog(models.get(4).getPlanPrice(), "4", (plan, amount, influencerId, influencerAmount) -> initBuy(plan, influencerId, influencerAmount, amount)));
        five.setOnClickListener(v -> showBuyDialog(models.get(5).getPlanPrice(), "5", (plan, amount, influencerId, influencerAmount) -> initBuy(plan, influencerId, influencerAmount, amount)));
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
            Utils.logout(activity,true);
            return;
        }
        loading.setVisibility(View.VISIBLE);
        one.setEnabled(false);
        two.setEnabled(false);
        three.setEnabled(false);
        four.setEnabled(false);
        five.setEnabled(false);

        RetrofitClient.getInstance().getApi()
                .getPlans("Bearer " + token, number)
                .enqueue(new Callback<PlanResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<PlanResponse> call, @NonNull Response<PlanResponse> response) {
                        loading.setVisibility(View.GONE);
                        one.setEnabled(true);
                        two.setEnabled(true);
                        three.setEnabled(true);
                        four.setEnabled(true);
                        five.setEnabled(true);
                        if (response.isSuccessful() && response.body() != null && response.body().getResult().equals("success")) {
                            models = response.body().getData();
                            onePrice.setText(context.getString(R.string.buy_price, Utils.moneySeparator(models.get(1).getPlanPrice())));
                            twoPrice.setText(context.getString(R.string.buy_price, Utils.moneySeparator(models.get(2).getPlanPrice())));
                            threePrice.setText(context.getString(R.string.buy_price, Utils.moneySeparator(models.get(3).getPlanPrice())));
                            fourPrice.setText(context.getString(R.string.buy_price, Utils.moneySeparator(models.get(4).getPlanPrice())));
                            fivePrice.setText(context.getString(R.string.buy_price, Utils.moneySeparator(models.get(5).getPlanPrice())));

                        } else if (response.code() == 401) {
                            Utils.logout(activity,true);
                        } else {
                            Utils.showInternetError(context, () -> getData());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<PlanResponse> call, @NonNull Throwable t) {
                        loading.setVisibility(View.GONE);
                        one.setEnabled(true);
                        two.setEnabled(true);
                        three.setEnabled(true);
                        four.setEnabled(true);
                        five.setEnabled(true);
                        Utils.showInternetError(context, () -> getData());

                    }
                });

    }

    private void initBuy(String plan, String influencerId, String influencerAmount, String amount) {
        String number = MySharedPreference.getInstance(activity).getNumber();
        String token = MySharedPreference.getInstance(activity).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(activity,true);
            return;
        }
        loading.setVisibility(View.VISIBLE);
        one.setEnabled(false);
        two.setEnabled(false);
        three.setEnabled(false);
        four.setEnabled(false);
        five.setEnabled(false);
        RetrofitClient.getInstance().getApi()
                .initBuy("Bearer " + token, number, plan, influencerId, influencerAmount, amount)
                .enqueue(new Callback<GeneralResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                        loading.setVisibility(View.GONE);
                        one.setEnabled(true);
                        two.setEnabled(true);
                        three.setEnabled(true);
                        four.setEnabled(true);
                        five.setEnabled(true);
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getMerchantId() != null && !response.body().getMerchantId().isEmpty()) {
                            String merchant = response.body().getMerchantId();
                            String url = "https://ghararehmaghzha.ir/api/buy/buy?merchant=" + merchant;
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            context.startActivity(i);

                        } else if (response.code() == 401) {
                            Utils.logout(activity,true);
                        } else {
                            Toast.makeText(context, context.getString(R.string.general_error), Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                        loading.setVisibility(View.GONE);
                        one.setEnabled(true);
                        two.setEnabled(true);
                        three.setEnabled(true);
                        four.setEnabled(true);
                        five.setEnabled(true);
                        Toast.makeText(context, context.getString(R.string.general_error), Toast.LENGTH_SHORT).show();

                    }
                });
    }


}