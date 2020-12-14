package ir.ghararemaghzha.game.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.PagerAdapter;
import com.google.android.material.textview.MaterialTextView;
import com.skyfishjy.library.RippleBackground;
import com.tmall.ultraviewpager.UltraViewPager;
import io.realm.Realm;
import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.activities.QuestionsActivity;
import ir.ghararemaghzha.game.adapters.MainViewPager;
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
    private LinearLayout profile, highscore, start;
    private UltraViewPager ultraViewPager;
    private NavController navController;

    private Realm db;

    public StartFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_start, container, false);
        ((RippleBackground)v.findViewById(R.id.start_start_ripple)).startRippleAnimation();
        context = getContext();
        activity = getActivity();
        init(v);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateInfo();
    }

    private void updateInfo() {
        int passed = Integer.parseInt(MySharedPreference.getInstance(context).getDaysPassed());
        if (passed >= 0 && passed < 10)
            info.setText(context.getString(R.string.start_info, String.valueOf(db.where(QuestionModel.class)
                    .equalTo("visible", true)
                    .and().equalTo("userAnswer","-1")
                    .findAll().size())));
        else if (passed < 0)
            info.setText(R.string.start_info_notstarted);
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
        profile = v.findViewById(R.id.start_profile);
        highscore = v.findViewById(R.id.start_highscore);
        start = v.findViewById(R.id.start_start);
        ultraViewPager = v.findViewById(R.id.start_slider);

        onClicks();
        getSlider();
    }

    private void initViewPager(PagerAdapter pagerAdapter, int count) {
        ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL);
        ultraViewPager.setAdapter(pagerAdapter);
        ultraViewPager.setAutoMeasureHeight(true);
        if (count > 1) {
            ultraViewPager.initIndicator();
            ultraViewPager.getIndicator()
                    .setOrientation(UltraViewPager.Orientation.HORIZONTAL)
                    .setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM)
                    .setMargin(0, 0, 0, 16)
                    .setFocusColor(0xFFFCD736)
                    .setNormalColor(0xFFECEFF1)
                    .setRadius((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, context.getResources().getDisplayMetrics()))
                    .build();
            ultraViewPager.setInfiniteLoop(true);
            ultraViewPager.setAutoScroll(5000);
        }
    }

    private void onClicks() {
        profile.setOnClickListener(v ->
                navController.navigate(R.id.action_menu_start_to_menu_profile)
        );
        highscore.setOnClickListener(v ->
                navController.navigate(R.id.action_menu_start_to_menu_highscore)
        );
        start.setOnClickListener(v -> {
            int passed = Integer.parseInt(MySharedPreference.getInstance(activity).getDaysPassed());
            int remaining = db.where(QuestionModel.class).equalTo("userAnswer", "-1").and().equalTo("visible", false).findAll().size();
            int size = db.where(QuestionModel.class).equalTo("visible", true).findAll().size();
            if (size > 0)
                startActivity(new Intent(activity, QuestionsActivity.class));
            else if (remaining == 0)
                Toast.makeText(context, context.getString(R.string.general_noquestions_at_all), Toast.LENGTH_LONG).show();
            else if(passed<0)
                Toast.makeText(context, context.getString(R.string.start_info_notstarted), Toast.LENGTH_LONG).show();
            else if(passed>9)
                Toast.makeText(context, context.getString(R.string.start_info_passed), Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context, context.getString(R.string.general_noquestions), Toast.LENGTH_SHORT).show();
        });

    }

    private void getSlider() {
        RetrofitClient.Companion.getInstance().getApi()
                .getSlider()
                .enqueue(new Callback<SliderResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<SliderResponse> call, @NonNull Response<SliderResponse> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getResult().equals("success") && !response.body().getMessage().equals("empty")) {
                            initViewPager(new MainViewPager(activity, response.body().getData()), response.body().getData().size());
                        }else
                            ultraViewPager.setVisibility(View.GONE);

                    }

                    @Override
                    public void onFailure(@NonNull Call<SliderResponse> call, @NonNull Throwable t) { }
                });
    }
}