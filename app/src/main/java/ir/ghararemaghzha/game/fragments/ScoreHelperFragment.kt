package ir.ghararemaghzha.game.fragments

import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.textview.MaterialTextView
import com.tmall.ultraviewpager.UltraViewPager
import com.tmall.ultraviewpager.transformer.UltraScaleTransformer
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.adapters.ScoreHelperViewPager


class ScoreHelperFragment : Fragment(R.layout.fragment_score_helper) {

    private lateinit var navController:NavController

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
         super.onViewCreated(view, savedInstanceState)
         navController = Navigation.findNavController(view)
         init(view)
     }

    private fun init( v:View) {
        requireActivity().findViewById<MaterialTextView>(R.id.toolbar_title).text=getText(R.string.score_helper_title)

        val ultraViewPager = v.findViewById<UltraViewPager>(R.id.score_helper_slider)
        ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL)
        ultraViewPager.setPageTransformer(true,  UltraScaleTransformer())
        ultraViewPager.initIndicator()
        ultraViewPager.indicator
                .setOrientation(UltraViewPager.Orientation.HORIZONTAL)
                .setFocusColor(resources.getColor(R.color.colorPrimaryDark))
                .setNormalColor(resources.getColor(R.color.colorPrimary))
                .setRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5F, resources.displayMetrics).toInt())
        ultraViewPager.indicator.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM)
        ultraViewPager.indicator.build()
        ultraViewPager.setInfiniteLoop(true)
        ultraViewPager.adapter = ScoreHelperViewPager(3, navController)
    }
}