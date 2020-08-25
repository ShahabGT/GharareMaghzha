package ir.ghararemaghzha.game.classes;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.PagerAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import java.util.List;
import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.models.SliderModel;

public class MainViewPager extends PagerAdapter {
    private FragmentActivity context;
    private List<SliderModel> slider;

    public MainViewPager(FragmentActivity context, List<SliderModel> slider) {
        this.context = context;
        this.slider = slider;


    }

    @Override
    public int getCount() {
        return slider.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        SliderModel response = slider.get(position);

        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        Glide.with(context)
                .load(context.getString(R.string.slider_image_url, response.getSliderPic()))
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(imageView);

        String link = response.getSliderLink();

        imageView.setOnClickListener(v ->openLinks(link));
        container.addView(imageView);
        return imageView;
    }

    private void openLinks(String link) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(link));
        context.startActivity(intent);
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ImageView) object);
    }
}
