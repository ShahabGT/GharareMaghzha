package ir.ghararemaghzha.game.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.textview.MaterialTextView;
import com.tmall.ultraviewpager.UltraViewPager;
import com.tmall.ultraviewpager.transformer.UltraDepthScaleTransformer;
import com.tmall.ultraviewpager.transformer.UltraScaleTransformer;

import java.util.ArrayList;
import java.util.List;

import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.adapters.ScoreHelperViewPager;


public class ScoreHelperFragment extends Fragment {

    private UltraViewPager ultraViewPager;
    private Context context;
    private FragmentActivity activity;

    public ScoreHelperFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_score_helper, container, false);
        context = getContext();
        activity = getActivity();
        init(v);

        return v;
    }


    private void init(View v) {
        ((MaterialTextView)activity.findViewById(R.id.toolbar_title)).setText(R.string.score_helper_title);

        ultraViewPager = v.findViewById(R.id.score_helper_slider);
        ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL);
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new Score1Fragment());
        fragmentList.add(new Score2Fragment());
        fragmentList.add(new Score3Fragment());
     //   ultraViewPager.setMultiScreen(0.9f);
      //  ultraViewPager.setRatio(0.3f);
     //   ultraViewPager.setMaxHeight(1400);
    //    ultraViewPager.setAutoMeasureHeight(true);
        ultraViewPager.setPageTransformer(true, new UltraScaleTransformer());
        ultraViewPager.initIndicator();
        ultraViewPager.getIndicator()
                .setOrientation(UltraViewPager.Orientation.HORIZONTAL)
                .setFocusColor(getResources().getColor(R.color.colorPrimaryDark))
                .setNormalColor(getResources().getColor(R.color.colorPrimary))
                .setRadius((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
        ultraViewPager.getIndicator().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        ultraViewPager.getIndicator().build();
        ultraViewPager.setInfiniteLoop(true);
        ultraViewPager.setAdapter(new ScoreHelperViewPager(fragmentList));


    }
}