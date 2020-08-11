package ir.ghararemaghzha.game.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.activities.QuestionActivity;
import ir.ghararemaghzha.game.classes.MySharedPreference;
import ir.ghararemaghzha.game.classes.Utils;
import ir.ghararemaghzha.game.data.RetrofitClient;
import ir.ghararemaghzha.game.models.GeneralResponse;
import ir.ghararemaghzha.game.models.HighscoreResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuyFragment extends Fragment {
    private Context context;
    private FragmentActivity activity;

    public BuyFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_buy, container, false);
        context = getContext();
        activity = getActivity();

        init(v);

        return v;
    }

    private void init(View v){

        v.findViewById(R.id.buy_buy).setOnClickListener(w->{
            String number = MySharedPreference.getInstance(activity).getNumber();
            String token = MySharedPreference.getInstance(activity).getAccessToken();
            if (number.isEmpty() || token.isEmpty()) {
                Utils.logout(activity);
                return;
            }
            RetrofitClient.getInstance().getApi()
                    .initBuy("Bearer "+token,number,"3","","","100")
                    .enqueue(new Callback<GeneralResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<GeneralResponse> call,@NonNull  Response<GeneralResponse> response) {
                            if(response.isSuccessful() && response.body()!=null
                                    && response.body().getMerchantId()!=null && !response.body().getMerchantId().isEmpty()){
                                String merchant = response.body().getMerchantId();
                                String url = "https://ghararehmaghzha.ir/api/buy/buy?merchant="+merchant;
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                context.startActivity(i);

                            }else{
                                Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onFailure(@NonNull Call<GeneralResponse> call,@NonNull  Throwable t) {
                            Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show();

                        }
                    });




        });
    }
}