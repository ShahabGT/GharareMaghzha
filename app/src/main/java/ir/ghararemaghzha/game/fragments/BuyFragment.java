package ir.ghararemaghzha.game.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.classes.MySharedPreference;
import ir.ghararemaghzha.game.classes.Utils;
import ir.ghararemaghzha.game.data.RetrofitClient;
import ir.ghararemaghzha.game.models.PlanModel;
import ir.ghararemaghzha.game.models.PlanResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuyFragment extends Fragment {
    private Context context;
    private FragmentActivity activity;
    private MaterialTextView myCode, myName;
    private ProgressBar loading;
    private MaterialCardView one, two, three, four, five;
    private MaterialTextView onePrice, twoPrice, threePrice, fourPrice, fivePrice;

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
        loading = v.findViewById(R.id.buy_loading);
        myName = v.findViewById(R.id.buy_name);
        myCode = v.findViewById(R.id.buy_code);
        myName.setText(MySharedPreference.getInstance(context).getUsername());
        myCode.setText(context.getString(R.string.profile_code, MySharedPreference.getInstance(context).getUserCode()));

        one = v.findViewById(R.id.buy_one_layout);
        onePrice = v.findViewById(R.id.buy_one_price);
        two = v.findViewById(R.id.buy_two_layout);
        twoPrice = v.findViewById(R.id.buy_two_price);
        three = v.findViewById(R.id.buy_three_layout);
        threePrice = v.findViewById(R.id.buy_three_price);
        four = v.findViewById(R.id.buy_four_layout);
        fourPrice = v.findViewById(R.id.buy_four_price);
        five = v.findViewById(R.id.buy_five_layout);
        fivePrice = v.findViewById(R.id.buy_five_price);
        getData();

//        v.findViewById(R.id.buy_buy).setOnClickListener(w->{
//            String number = MySharedPreference.getInstance(activity).getNumber();
//            String token = MySharedPreference.getInstance(activity).getAccessToken();
//            if (number.isEmpty() || token.isEmpty()) {
//                Utils.logout(activity);
//                return;
//            }
//            RetrofitClient.getInstance().getApi()
//                    .initBuy("Bearer "+token,number,"3","","","100")
//                    .enqueue(new Callback<GeneralResponse>() {
//                        @Override
//                        public void onResponse(@NonNull Call<GeneralResponse> call,@NonNull  Response<GeneralResponse> response) {
//                            if(response.isSuccessful() && response.body()!=null
//                                    && response.body().getMerchantId()!=null && !response.body().getMerchantId().isEmpty()){
//                                String merchant = response.body().getMerchantId();
//                                String url = "https://ghararehmaghzha.ir/api/buy/buy?merchant="+merchant;
//                                Intent i = new Intent(Intent.ACTION_VIEW);
//                                i.setData(Uri.parse(url));
//                                context.startActivity(i);
//
//                            }else{
//                                Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
//                            }
//
//                        }
//
//                        @Override
//                        public void onFailure(@NonNull Call<GeneralResponse> call,@NonNull  Throwable t) {
//                            Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show();
//
//                        }
//                    });
//
//
//
//
//        });
    }

    private void getData() {

        String number = MySharedPreference.getInstance(context).getNumber();
        String token = MySharedPreference.getInstance(context).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(activity);
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
                            List<PlanModel> models = response.body().getData();
                            onePrice.setText(context.getString(R.string.buy_price, models.get(1).getPlanPrice()));
                            twoPrice.setText(context.getString(R.string.buy_price, models.get(2).getPlanPrice()));
                            threePrice.setText(context.getString(R.string.buy_price, models.get(3).getPlanPrice()));
                            fourPrice.setText(context.getString(R.string.buy_price, models.get(4).getPlanPrice()));
                            fivePrice.setText(context.getString(R.string.buy_price, models.get(5).getPlanPrice()));

                        } else if (response.code() == 401) {
                            Utils.logout(activity);
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
}