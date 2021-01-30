package ir.ghararemaghzha.game.fragments

import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.textview.MaterialTextView
import com.tmall.ultraviewpager.UltraViewPager
import com.tmall.ultraviewpager.transformer.UltraScaleTransformer
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.adapters.ScoreHelperViewPager
import ir.ghararemaghzha.game.databinding.FragmentScoreHelperBinding

class ScoreHelperFragment : Fragment(R.layout.fragment_score_helper) {

    private lateinit var b:FragmentScoreHelperBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        b= FragmentScoreHelperBinding.inflate(inflater,container,false)
        return b.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    private fun init() {
        requireActivity().findViewById<MaterialTextView>(R.id.toolbar_title).text=getText(R.string.score_helper_title)
        b.scoreHelperSlider.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL)
        b.scoreHelperSlider.setPageTransformer(true,  UltraScaleTransformer())
        b.scoreHelperSlider.initIndicator()
        b.scoreHelperSlider.indicator
                .setOrientation(UltraViewPager.Orientation.HORIZONTAL)
                .setFocusColor(resources.getColor(R.color.colorPrimaryDark))
                .setNormalColor(resources.getColor(R.color.colorPrimary))
                .setRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5F, resources.displayMetrics).toInt())
        b.scoreHelperSlider.indicator.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM)
        b.scoreHelperSlider.indicator.build()
        b.scoreHelperSlider.setInfiniteLoop(true)
        b.scoreHelperSlider.adapter = ScoreHelperViewPager(requireView().findNavController())
    }
}