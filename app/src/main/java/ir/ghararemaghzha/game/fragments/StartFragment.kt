package ir.ghararemaghzha.game.fragments

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.viewpager.widget.PagerAdapter
import com.google.android.material.textview.MaterialTextView
import com.tmall.ultraviewpager.UltraViewPager
import io.realm.Realm
import io.realm.kotlin.where
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.activities.QuestionsActivity
import ir.ghararemaghzha.game.adapters.MainViewPager
import ir.ghararemaghzha.game.classes.MySharedPreference
import ir.ghararemaghzha.game.data.Resource
import ir.ghararemaghzha.game.databinding.FragmentStartBinding
import ir.ghararemaghzha.game.models.QuestionModel
import ir.ghararemaghzha.game.viewmodels.StartViewModel

class StartFragment : BaseFragment<StartViewModel, FragmentStartBinding>() {

    private lateinit var navController: NavController
    private lateinit var db: Realm

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()

        viewModel.sliderResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is Resource.Success -> {
                    if (res.value.result == "success" && res.value.message != "empty")
                        initViewPager(MainViewPager(requireActivity(), res.value.data), res.value.data.size)
                    else
                        b.startSlider.visibility = View.GONE
                }
                is Resource.Failure -> {
                    b.startSlider.visibility = View.GONE
                }
                is Resource.Loading -> {
                }
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

    override fun onResume() {
        super.onResume()
        updateInfo()
    }

    private fun init() {
        requireActivity().findViewById<MaterialTextView>(R.id.toolbar_title).setText(R.string.start_title)
        b.startStartRipple.startRippleAnimation()
        db = Realm.getDefaultInstance()
        onClicks()
        viewModel.getSlider()
    }

    private fun updateInfo() {
        val passed = MySharedPreference.getInstance(requireContext()).getDaysPassed()
        when {
            passed in 0..6 -> b.startInfo.text = getString(R.string.start_info, db.where<QuestionModel>()
                    .equalTo("userAnswer", "-1")
                    .findAll().size.toString())
            passed < 0 -> b.startInfo.setText(R.string.start_info_notstarted)
            else -> {
                b.startInfo.text = getString(R.string.start_info_passed)
                b.startStart.isEnabled = false
            }
        }
    }

    private fun initViewPager(pagerAdapter: PagerAdapter, count: Int) {
        b.startSlider.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL)
        b.startSlider.adapter = pagerAdapter
        b.startSlider.setAutoMeasureHeight(true)
        if (count > 1) {
            b.startSlider.initIndicator()
            b.startSlider.indicator
                    .setOrientation(UltraViewPager.Orientation.HORIZONTAL)
                    .setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM)
                    .setMargin(0, 0, 0, 16)
                    .setFocusColor(-0x328ca)
                    .setNormalColor(-0x13100f)
                    .setRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, requireContext().resources.displayMetrics).toInt())
                    .build()
            b.startSlider.setInfiniteLoop(true)
            b.startSlider.setAutoScroll(5000)
        }
    }

    private fun onClicks() {
        b.startProfile.setOnClickListener { navController.navigate(R.id.action_menu_start_to_menu_profile) }
        b.startHighscore.setOnClickListener { navController.navigate(R.id.action_menu_start_to_menu_highscore) }
        b.startStart.setOnClickListener {
            val passed = MySharedPreference.getInstance(requireContext()).getDaysPassed()
            val remaining = db.where<QuestionModel>().equalTo("userAnswer", "-1").findAll().size
            when {
                passed < 0 -> Toast.makeText(context, getString(R.string.start_info_notstarted), Toast.LENGTH_LONG).show()
                passed > 6 -> Toast.makeText(context, getString(R.string.start_info_passed), Toast.LENGTH_LONG).show()
                remaining == 0 -> Toast.makeText(context, getString(R.string.general_noquestions_at_all), Toast.LENGTH_LONG).show()
                remaining > 0 -> startActivity(Intent(activity, QuestionsActivity::class.java))
            }
        }
    }

    override fun getViewModel() = StartViewModel::class.java

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentStartBinding.inflate(inflater, container, false)
}