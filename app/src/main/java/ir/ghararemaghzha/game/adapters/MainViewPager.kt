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

class MainViewPager(private val ctx: FragmentActivity,private val slider: List<SliderModel>) : PagerAdapter() {

    override fun getCount() = slider.size

    override fun isViewFromObject(view: View, `object`: Any) = view == `object`

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val model = slider[position]
        val imageView = ImageView(ctx)
        imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE

        Glide.with(ctx)
                .load(ctx.getString(R.string.slider_image_url, model.sliderPic))
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
        ctx.startActivity(intent)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) = container.removeView(`object` as ImageView)
}
