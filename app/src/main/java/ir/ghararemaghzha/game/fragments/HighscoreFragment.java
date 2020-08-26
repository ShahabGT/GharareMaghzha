package ir.ghararemaghzha.game.fragments;

import android.content.Context;
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
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;
import java.util.Objects;

import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.classes.MySharedPreference;
import ir.ghararemaghzha.game.classes.NumberToTextKt;
import ir.ghararemaghzha.game.classes.Utils;
import ir.ghararemaghzha.game.data.RetrofitClient;
import ir.ghararemaghzha.game.dialogs.NoInternetDialog;
import ir.ghararemaghzha.game.dialogs.UserDetailsDialog;
import ir.ghararemaghzha.game.models.HighscoreModel;
import ir.ghararemaghzha.game.models.HighscoreResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HighscoreFragment extends Fragment {
    private Context context;
    private FragmentActivity activity;
    private SwipeRefreshLayout refreshLayout;
    private MaterialCardView firstCard, secondCard, thirdCard, fourthCard, fifthCard, userCard;
    private MaterialTextView firstName, secondName, thirdName, fourthName, fifthName, userName,userRank;
    private MaterialTextView firstScore, secondScore, thirdScore, fourthScore, fifthScore, userScore;
    private ImageView firstAvatar, secondAvatar, thirdAvatar, fourthAvatar, fifthAvatar, userAvatar;
    private ProgressBar loading;


    private List<HighscoreModel> data;
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
        ((MaterialTextView)activity.findViewById(R.id.toolbar_title)).setText(R.string.highscore_title);


        loading = v.findViewById(R.id.highscore_loading);

        refreshLayout = v.findViewById(R.id.highscore_refresh);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);

        firstCard = v.findViewById(R.id.highscore_first_card);
        firstScore = v.findViewById(R.id.highscore_first_score);
        firstName = v.findViewById(R.id.highscore_first_name);
        firstAvatar = v.findViewById(R.id.highscore_first_avatar);

        secondCard = v.findViewById(R.id.highscore_second_card);
        secondScore = v.findViewById(R.id.highscore_second_score);
        secondName = v.findViewById(R.id.highscore_second_name);
        secondAvatar = v.findViewById(R.id.highscore_second_avatar);

        thirdCard = v.findViewById(R.id.highscore_third_card);
        thirdScore = v.findViewById(R.id.highscore_third_score);
        thirdName = v.findViewById(R.id.highscore_third_name);
        thirdAvatar = v.findViewById(R.id.highscore_third_avatar);

        fourthCard = v.findViewById(R.id.highscore_fourth_card);
        fourthScore = v.findViewById(R.id.highscore_fourth_score);
        fourthName = v.findViewById(R.id.highscore_fourth_name);
        fourthAvatar = v.findViewById(R.id.highscore_fourth_avatar);

        fifthCard = v.findViewById(R.id.highscore_fifth_card);
        fifthScore = v.findViewById(R.id.highscore_fifth_score);
        fifthName = v.findViewById(R.id.highscore_fifth_name);
        fifthAvatar = v.findViewById(R.id.highscore_fifth_avatar);

        userCard = v.findViewById(R.id.highscore_user_card);
        userScore = v.findViewById(R.id.highscore_user_score);
        userName = v.findViewById(R.id.highscore_user_name);
        userAvatar = v.findViewById(R.id.highscore_user_avatar);
        userRank = v.findViewById(R.id.highscore_user_rank);

        firstCard.setVisibility(View.INVISIBLE);
        secondCard.setVisibility(View.INVISIBLE);
        thirdCard.setVisibility(View.INVISIBLE);
        fourthCard.setVisibility(View.INVISIBLE);
        fifthCard.setVisibility(View.INVISIBLE);

        getData();
        onClicks();
    }

    private void onClicks() {
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(true);
            getData();
        });

        firstCard.setOnClickListener(v->{
            showDetailsDialog(data.get(0).getUserId());
        });
        secondCard.setOnClickListener(v->{
            showDetailsDialog(data.get(1).getUserId());
        });
        thirdCard.setOnClickListener(v->{
            showDetailsDialog(data.get(2).getUserId());
        });
        fourthCard.setOnClickListener(v->{
            showDetailsDialog(data.get(3).getUserId());
        });
        fifthCard.setOnClickListener(v->{
            showDetailsDialog(data.get(4).getUserId());
        });

    }

    private void showDetailsDialog(String userId){
        UserDetailsDialog dialog = new UserDetailsDialog(activity, userId);
        dialog.setCanceledOnTouchOutside(false);
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

        RetrofitClient.getInstance().getApi().getHighscoreList("Bearer " + token, number)
                .enqueue(new Callback<HighscoreResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<HighscoreResponse> call, @NonNull Response<HighscoreResponse> response) {
                        refreshLayout.setRefreshing(false);
                        loading.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null && response.body().getResult().equals("success")) {
                            firstCard.setVisibility(View.VISIBLE);
                            secondCard.setVisibility(View.VISIBLE);
                            thirdCard.setVisibility(View.VISIBLE);
                            fourthCard.setVisibility(View.VISIBLE);
                            fifthCard.setVisibility(View.VISIBLE);
                            boolean showUser=true;

                            for (HighscoreModel m : response.body().getData())
                                if (m.getUserId().equals(response.body().getUser().getUserId())) {
                                    showUser=false;
                                    break;
                                }
                            data = response.body().getData();
                            switch (data.size()) {
                                case 5:
                                    fifthCard.setVisibility(View.VISIBLE);
                                    fifthName.setText(data.get(4).getUserName());
                                    fifthScore.setText(context.getString(R.string.highscore_score, data.get(4).getScoreCount()));
                                    Glide.with(context)
                                            .load(Uri.parse(context.getString(R.string.avatar_url, data.get(4).getUserId())))
                                            .circleCrop()
                                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                                            .placeholder(R.drawable.placeholder)
                                            .into(fifthAvatar);
                                case 4:
                                    fourthCard.setVisibility(View.VISIBLE);
                                    fourthName.setText(data.get(3).getUserName());
                                    fourthScore.setText(context.getString(R.string.highscore_score, data.get(3).getScoreCount()));
                                    Glide.with(context)
                                            .load(Uri.parse(context.getString(R.string.avatar_url, data.get(3).getUserId())))
                                            .circleCrop()
                                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                                            .placeholder(R.drawable.placeholder)
                                            .into(fourthAvatar);
                                case 3:
                                    thirdCard.setVisibility(View.VISIBLE);
                                    thirdName.setText(data.get(2).getUserName());
                                    thirdScore.setText(context.getString(R.string.highscore_score, data.get(2).getScoreCount()));
                                    Glide.with(context)
                                            .load(Uri.parse(context.getString(R.string.avatar_url, data.get(2).getUserId())))
                                            .circleCrop()
                                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                                            .placeholder(R.drawable.placeholder)
                                            .into(thirdAvatar);
                                case 2:
                                    secondCard.setVisibility(View.VISIBLE);
                                    secondName.setText(data.get(1).getUserName());
                                    secondScore.setText(context.getString(R.string.highscore_score, data.get(1).getScoreCount()));
                                    Glide.with(context)
                                            .load(Uri.parse(context.getString(R.string.avatar_url, data.get(1).getUserId())))
                                            .circleCrop()
                                            .diskCacheStrategy(DiskCacheStrategy.NONE)

                                            .placeholder(R.drawable.placeholder)
                                            .into(secondAvatar);
                                case 1:
                                    firstName.setText(data.get(0).getUserName());
                                    firstScore.setText(context.getString(R.string.highscore_score, data.get(0).getScoreCount()));
                                    Glide.with(context)
                                            .load(Uri.parse(context.getString(R.string.avatar_url, data.get(0).getUserId())))
                                            .circleCrop()
                                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                                            .placeholder(R.drawable.placeholder)
                                            .into(firstAvatar);
                                    break;
                            }
                            if(showUser) {
                                userCard.setVisibility(View.VISIBLE);
                                userName.setText(response.body().getUser().getUserName());
                                userScore.setText(context.getString(R.string.highscore_score, response.body().getUser().getScoreCount()));
                                Glide.with(context)
                                        .load(Uri.parse(context.getString(R.string.avatar_url, response.body().getUser().getUserId())))
                                        .circleCrop()
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .placeholder(R.drawable.placeholder)
                                        .into(userAvatar);
                                userRank.setText(context.getString(R.string.highscore_user_rank, NumberToTextKt.toEnglishFormat(Integer.parseInt(response.body().getUser().getUserRank())).trim()));
                            }

                        } else if (response.code() == 401) {
                            Utils.logout(activity,true);
                        }else{
                            Utils.showInternetError(context, () -> getData());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<HighscoreResponse> call, @NonNull Throwable t) {
                        loading.setVisibility(View.GONE);
                        refreshLayout.setRefreshing(false);
                        Utils.showInternetError(context, () -> getData());

                    }
                });
    }

}