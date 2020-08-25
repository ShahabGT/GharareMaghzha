package ir.ghararemaghzha.game.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.tmall.ultraviewpager.UltraViewPager;

import io.realm.Realm;
import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.activities.MainActivity;
import ir.ghararemaghzha.game.activities.QuestionActivity;
import ir.ghararemaghzha.game.classes.MainViewPager;
import ir.ghararemaghzha.game.classes.MySharedPreference;
import ir.ghararemaghzha.game.classes.Utils;
import ir.ghararemaghzha.game.data.RetrofitClient;
import ir.ghararemaghzha.game.models.QuestionModel;
import ir.ghararemaghzha.game.models.SliderResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class StartFragment extends Fragment {

    private Context context;
    private FragmentActivity activity;
    private MaterialTextView info;
    private MaterialCardView profile, highscore, start;
    private UltraViewPager ultraViewPager;
    private MainViewPager pagerAdapter;

    private Realm db;

    public StartFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_start, container, false);
        context = getContext();
        activity = getActivity();
        init(v);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateInfo();
    }

    private void updateInfo() {
        //  int remain = db.where(QuestionModel.class).equalTo("userAnswer", "-1").findAll().size();
        int passed = Integer.parseInt(MySharedPreference.getInstance(context).getDaysPassed());
        if (passed >= 0 && passed < 10)
            info.setText(context.getString(R.string.start_info, String.valueOf(db.where(QuestionModel.class).equalTo("visible", true).findAll().size())));
        else if (passed < 0)
            info.setText(context.getString(R.string.start_info, String.valueOf(0)));
        else {
            info.setText(context.getString(R.string.start_info_passed));
            start.setEnabled(false);
        }
        Utils.updateServerQuestions(activity, String.valueOf(db.where(QuestionModel.class).equalTo("visible", true).findAll().size()));
    }

    private void init(View v) {
        ((MaterialTextView) activity.findViewById(R.id.toolbar_title)).setText(R.string.start_title);
        db = Realm.getDefaultInstance();
        info = v.findViewById(R.id.start_info);
        profile = v.findViewById(R.id.start_profile_card);
        highscore = v.findViewById(R.id.start_highscore_card);
        start = v.findViewById(R.id.start_start_card);
        ultraViewPager = v.findViewById(R.id.start_slider);
        onClicks();
        getSlider();
    }

    private void initViewPager(PagerAdapter pagerAdapter, int count){
        ultraViewPager.setVisibility(View.VISIBLE);
        ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL);
        ultraViewPager.setAdapter(pagerAdapter);
        ultraViewPager.setAutoMeasureHeight(true);
        if(count>1) {
            ultraViewPager.initIndicator();
            ultraViewPager.getIndicator()
                    .setOrientation(UltraViewPager.Orientation.HORIZONTAL)
                    .setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM)
                    .setMargin(0, 0, 0, 16)
                    .setFocusColor(0xFFFCD736)
                    .setNormalColor(0xFFECEFF1)
                    .setRadius((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()))
                    .build();

            ultraViewPager.setInfiniteLoop(true);
            ultraViewPager.setAutoScroll(2000);
        }
    }

    private void onClicks() {
        profile.setOnClickListener(v -> {
            ImageViewCompat.setImageTintList(MainActivity.buy, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black)));
            ImageViewCompat.setImageTintList(MainActivity.profile, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPrimaryDark)));
            ImageViewCompat.setImageTintList(MainActivity.messages, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black)));
            ImageViewCompat.setImageTintList(MainActivity.highscore, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black)));
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                    .replace(R.id.main_container, new ProfileFragment())
                    .commit();
            MainActivity.whichFragment = 1;
        });
        highscore.setOnClickListener(v -> {
            ImageViewCompat.setImageTintList(MainActivity.buy, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black)));
            ImageViewCompat.setImageTintList(MainActivity.profile, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black)));
            ImageViewCompat.setImageTintList(MainActivity.messages, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black)));
            ImageViewCompat.setImageTintList(MainActivity.highscore, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPrimaryDark)));
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                    .replace(R.id.main_container, new HighscoreFragment())
                    .commit();
            MainActivity.whichFragment = 3;
        });
        start.setOnClickListener(v -> {
            int size = db.where(QuestionModel.class).equalTo("visible", true).findAll().size();
            if (size > 0)
                startActivity(new Intent(activity, QuestionActivity.class));
            else
                Toast.makeText(context, context.getString(R.string.general_noquestions), Toast.LENGTH_SHORT).show();
        });

    }

    private void getSlider(){
        RetrofitClient.getInstance().getApi()
                .getSlider()
                .enqueue(new Callback<SliderResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<SliderResponse> call,@NonNull  Response<SliderResponse> response) {
                        if(response.isSuccessful() && response.body()!=null
                                && response.body().getResult().equals("success") && !response.body().getMessage().equals("empty")){
                            initViewPager(new MainViewPager(activity,response.body().getData()),response.body().getData().size());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<SliderResponse> call,@NonNull  Throwable t) {

                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (db != null) db.close();
    }

}