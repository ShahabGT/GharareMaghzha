package ir.ghararemaghzha.game.adapters

import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.models.SliderModel

class MainViewPager(ctx: FragmentActivity, sldr: List<SliderModel>) : PagerAdapter() {
    private val context = ctx
    private val slider = sldr

    override fun getCount(): Int {
        return slider.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val model = slider[position]
        val imageView = ImageView(context)
        imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE

        Glide.with(context)
                .load(context.getString(R.string.slider_image_url, model.sliderPic))
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(imageView)


        imageView.setOnClickListener {
            if (!model.sliderLink.isNullOrEmpty())
                openLinks(model.sliderLink)
        }
        container.addView(imageView)
        return imageView
    }


    private fun openLinks(link: String) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.data = Uri.parse(link)
        context.startActivity(intent)
    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ImageView)
    }
}
