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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.adapters.HighscoreAdapter;
import ir.ghararemaghzha.game.classes.MySharedPreference;
import ir.ghararemaghzha.game.classes.Utils;
import ir.ghararemaghzha.game.data.RetrofitClient;
import ir.ghararemaghzha.game.models.HighscoreResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HighscoreFragment extends Fragment {
    private Context context;
    private FragmentActivity activity;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private HighscoreAdapter adapter;


    public HighscoreFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_highscore, container, false);
        context = getContext();
        activity = getActivity();
        init(v);

        return v;
    }

    private void init(View v) {
        refreshLayout = v.findViewById(R.id.highscore_refresh);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary,R.color.colorPrimaryDark);
        recyclerView = v.findViewById(R.id.highscore_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        getData();

        onClicks();
    }

    private void onClicks(){
        refreshLayout.setOnRefreshListener(()->{
            refreshLayout.setRefreshing(true);
            getData();
        });
    }

    private void getData() {
        String number = MySharedPreference.getInstance(context).getNumber();
        String token = MySharedPreference.getInstance(context).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(activity);
            return;
        }
        RetrofitClient.getInstance().getApi().getHighscoreList("Bearer " + token, number)
                .enqueue(new Callback<HighscoreResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<HighscoreResponse> call, @NonNull Response<HighscoreResponse> response) {
                        refreshLayout.setRefreshing(false);
                        if (response.isSuccessful() && response.body() != null && response.body().getResult().equals("success")) {
                            adapter = new HighscoreAdapter(context,response.body().getData());
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(context, "ok", Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 401) {
                            Utils.logout(activity);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<HighscoreResponse> call, @NonNull Throwable t) {
                        refreshLayout.setRefreshing(false);
                        Utils.showInternetError(context,()->getData());

                    }
                });
    }
}