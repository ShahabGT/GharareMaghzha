package ir.ghararemaghzha.game.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.viewpager.widget.PagerAdapter
import com.google.android.material.button.MaterialButton
import com.skyfishjy.library.RippleBackground
import ir.ghararemaghzha.game.R

class ScoreHelperViewPager(private val navController: NavController) : PagerAdapter() {

    override fun getCount() = 3

    override fun isViewFromObject(view: View, `object`: Any) = view == `object`

    @SuppressLint("InflateParams")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layout = when (position) {
            0 -> LayoutInflater.from(container.context).inflate(R.layout.fragment_score1, null) as ConstraintLayout
            1 -> LayoutInflater.from(container.context).inflate(R.layout.fragment_score2, null) as ConstraintLayout
            2 -> LayoutInflater.from(container.context).inflate(R.layout.fragment_score3, null) as ConstraintLayout
            else -> throw IllegalStateException("Unexpected value at $position")
        }
        layout.findViewById<RippleBackground>(R.id.guide_ripple).startRippleAnimation()
        layout.findViewById<MaterialButton>(R.id.score_button).setOnClickListener {
            when (position) {
                0 -> navController.navigate(R.id.action_scoreHelperFragment_to_menu_start)
                1 -> navController.navigate(R.id.action_scoreHelperFragment_to_inviteFragment)
                2 -> navController.navigate(R.id.action_scoreHelperFragment_to_menu_buy)
            }
        }
        container.addView(layout)
        return layout
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) = container.removeView(`object` as ConstraintLayout)
}
