package ir.ghararemaghzha.game.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.models.ProfileModel

class ProfileViewPager(private val context: Context,private val data: List<ProfileModel>) : PagerAdapter() {

    override fun getCount()= data.size

    override fun isViewFromObject(view: View, `object`: Any)= view == `object`

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layout = LayoutInflater.from(container.context).inflate(R.layout.row_profile, null) as MaterialCardView
        val model = data[position]
        layout.findViewById<MaterialTextView>(R.id.row_profile_title).text = model.title
        layout.findViewById<MaterialTextView>(R.id.row_profile_subtitle).text = model.subtitle
        if(position==0) {
            Glide.with(context)
                    .load(model.image)
                    .circleCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(layout.findViewById(R.id.row_profile_img))
        }else{
            Glide.with(context)
                    .load(model.image)
                    .placeholder(R.drawable.placeholder)
                    .into(layout.findViewById(R.id.row_profile_img))
        }
        container.addView(layout)
        return layout
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val layout = `object` as MaterialCardView
        container.removeView(layout)
    }
}