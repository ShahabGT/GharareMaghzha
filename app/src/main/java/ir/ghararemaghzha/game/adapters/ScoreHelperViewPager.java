package ir.ghararemaghzha.game.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;

import com.skyfishjy.library.RippleBackground;

import java.util.List;

import ir.ghararemaghzha.game.R;


public class ScoreHelperViewPager extends PagerAdapter {
    private int count;

    public ScoreHelperViewPager(int count) {
        this.count=count;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ConstraintLayout layout;
        switch (position) {
            case 0:
                layout = (ConstraintLayout) LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_score1, null);

                break;
            case 1:
                layout = (ConstraintLayout) LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_score2, null);

                break;
            case 2:
                layout = (ConstraintLayout) LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_score3, null);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + position);
        }
        ((RippleBackground)layout.getViewById(R.id.guide_ripple)).startRippleAnimation();

        container.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ConstraintLayout layout = (ConstraintLayout) object;
        container.removeView(layout);
    }
}
