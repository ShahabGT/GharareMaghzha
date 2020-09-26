package ir.ghararemaghzha.game.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.adapters.HighscoreAdapter;
import ir.ghararemaghzha.game.classes.MySharedPreference;
import ir.ghararemaghzha.game.classes.Utils;
import ir.ghararemaghzha.game.data.RetrofitClient;
import ir.ghararemaghzha.game.dialogs.UserDetailsDialog;
import ir.ghararemaghzha.game.models.HighscoreModel;
import ir.ghararemaghzha.game.models.HighscoreResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HighscoreFragment extends Fragment {
    private Context context;
    private FragmentActivity activity;
    private ConstraintLayout loading;
    private RecyclerView recyclerView;
    private HighscoreAdapter adapter;
    private List<Integer> randomColors;

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
        randomColors =new ArrayList<>();
        randomColors.add(R.color.random1);
        randomColors.add(R.color.random2);
        randomColors.add(R.color.random3);
        randomColors.add(R.color.random4);
        randomColors.add(R.color.random5);
        randomColors.add(R.color.random6);
        randomColors.add(R.color.random7);
        randomColors.add(R.color.random8);
        randomColors.add(R.color.random9);
        randomColors.add(R.color.random10);
        randomColors.add(R.color.random11);
        ((MaterialTextView)activity.findViewById(R.id.toolbar_title)).setText(R.string.highscore_title);

        recyclerView = v.findViewById(R.id.highscore_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        loading = v.findViewById(R.id.highscore_loading);

        getData();
    }


    private void getData() {
        String number = MySharedPreference.getInstance(context).getNumber();
        String token = MySharedPreference.getInstance(context).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(activity,true);
            return;
        }
        loading.setVisibility(View.VISIBLE);

        RetrofitClient.getInstance().getApi().getHighscoreList("Bearer " + token, number)
                .enqueue(new Callback<HighscoreResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<HighscoreResponse> call, @NonNull Response<HighscoreResponse> response) {
                        loading.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null && response.body().getResult().equals("success")) {

                            boolean showUser=true;
                            List<HighscoreModel> data = response.body().getData();
                            for(int i=0;i<data.size();i++){
                                if (data.get(i).getUserId().equals(response.body().getUser().getUserId())) {
                                    showUser=false;
                                }
                            }

                            adapter = new HighscoreAdapter(activity,data,response.body().getUser(),showUser);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();


                        } else if (response.code() == 401) {
                            Utils.logout(activity,true);
                        }else{
                            Utils.showInternetError(context, () -> getData());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<HighscoreResponse> call, @NonNull Throwable t) {
                        loading.setVisibility(View.GONE);
                        Utils.showInternetError(context, () -> getData());

                    }
                });
    }

    private int getRandomColor() {
        int r = new Random().nextInt(randomColors.size());
        int color= randomColors.get(r);
        randomColors.remove(r);
        return color;
    }

}