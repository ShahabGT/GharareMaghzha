package ir.ghararemaghzha.game.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.adapters.BuyHistoryAdapter;
import ir.ghararemaghzha.game.classes.MySharedPreference;
import ir.ghararemaghzha.game.classes.Utils;
import ir.ghararemaghzha.game.data.RetrofitClient;
import ir.ghararemaghzha.game.models.BuyHistoryResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuyHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BuyHistoryAdapter adapter;
    private ConstraintLayout loading;
    private ConstraintLayout empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_history);
        init();
    }

    private void init(){
        loading = findViewById(R.id.buyhistory_loading);
        empty = findViewById(R.id.buyhistory_empty);
        recyclerView = findViewById(R.id.buyhistory_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getData();

        findViewById(R.id.buyhistory_close).setOnClickListener(v->onBackPressed());

    }

    private void getData(){
        String number = MySharedPreference.getInstance(this).getNumber();
        String token = MySharedPreference.getInstance(this).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(this,true);
            return;
        }
        loading.setVisibility(View.VISIBLE);
        RetrofitClient.getInstance().getApi()
                .getBuyHistory("Bearer "+token,number)
                .enqueue(new Callback<BuyHistoryResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<BuyHistoryResponse> call,@NonNull  Response<BuyHistoryResponse> response) {
                        loading.setVisibility(View.GONE);
                            if(response.isSuccessful() && response.body()!=null && response.body().getResult().equals("success") ){
                                if(response.body().getMessage().equals("all history")){
                                    adapter = new BuyHistoryAdapter(BuyHistoryActivity.this,response.body().getData());
                                    recyclerView.setAdapter(adapter);
                                }else {
                                    empty.setVisibility(View.VISIBLE);
                                }
                            }else if (response.code() == 401) {
                                Utils.logout(BuyHistoryActivity.this,true);
                            } else {
                                Toast.makeText(BuyHistoryActivity.this, getString(R.string.general_error), Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }
                    }

                    @Override
                    public void onFailure(@NonNull Call<BuyHistoryResponse> call,@NonNull  Throwable t) {
                        loading.setVisibility(View.GONE);
                        Toast.makeText(BuyHistoryActivity.this, getString(R.string.general_error), Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                });
    }
}