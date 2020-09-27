package ir.ghararemaghzha.game.fragments;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.google.android.material.textview.MaterialTextView;
import com.tmall.ultraviewpager.UltraViewPager;
import com.tmall.ultraviewpager.transformer.UltraScaleTransformer;
import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.adapters.ScoreHelperViewPager;


public class ScoreHelperFragment extends Fragment {

    private FragmentActivity activity;
    private NavController navController;

    public ScoreHelperFragment() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        init(view);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_score_helper, container, false);
        activity = getActivity();
        return v;
    }


    private void init(View v) {
        ((MaterialTextView) activity.findViewById(R.id.toolbar_title)).setText(R.string.score_helper_title);

        UltraViewPager ultraViewPager = v.findViewById(R.id.score_helper_slider);
        ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL);
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
        ultraViewPager.setAdapter(new ScoreHelperViewPager(3, navController));


    }
}