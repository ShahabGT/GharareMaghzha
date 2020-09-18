package ir.ghararemaghzha.game.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import ir.ghararemaghzha.game.R


class ScoreHelperViewPager(count:Int) :PagerAdapter() {

    val c:Int = count


    override fun getCount(): Int {
        return c
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view==`object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layout= when(position){
            0->LayoutInflater.from(container.context).inflate(R.layout.fragment_score1, null) as ConstraintLayout
            1->LayoutInflater.from(container.context).inflate(R.layout.fragment_score2, null) as ConstraintLayout
            2->LayoutInflater.from(container.context).inflate(R.layout.fragment_score3, null) as ConstraintLayout
            else-> throw IllegalStateException("Unexpected value at $position")
        }
        container.addView(layout)
        return layout
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val layout = `object` as ConstraintLayout
        container.removeView(layout)
    }

}
