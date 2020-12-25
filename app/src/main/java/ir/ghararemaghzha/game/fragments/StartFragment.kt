package ir.ghararemaghzha.game.fragments

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.viewpager.widget.PagerAdapter
import com.google.android.material.textview.MaterialTextView
import com.skyfishjy.library.RippleBackground
import com.tmall.ultraviewpager.UltraViewPager
import io.realm.Realm
import io.realm.kotlin.where
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.activities.QuestionsActivity
import ir.ghararemaghzha.game.adapters.MainViewPager
import ir.ghararemaghzha.game.classes.MySharedPreference
import ir.ghararemaghzha.game.data.ApiRepository
import ir.ghararemaghzha.game.data.NetworkApi
import ir.ghararemaghzha.game.data.RemoteDataSource
import ir.ghararemaghzha.game.data.Resource
import ir.ghararemaghzha.game.models.QuestionModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StartFragment : Fragment(R.layout.fragment_start) {
    private lateinit var info: MaterialTextView
    private lateinit var profile: LinearLayout
    private lateinit var highscore: LinearLayout
    private lateinit var start: LinearLayout
    private lateinit var ultraViewPager: UltraViewPager
    private lateinit var navController: NavController
    private lateinit var db: Realm

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        view.findViewById<RippleBackground>(R.id.start_start_ripple).startRippleAnimation()
        init(view)
    }

    override fun onResume() {
        super.onResume()
        updateInfo()
    }

    private fun init(v: View) {
        requireActivity().findViewById<MaterialTextView>(R.id.toolbar_title).setText(R.string.start_title)
        db = Realm.getDefaultInstance()
        info = v.findViewById(R.id.start_info)
        profile = v.findViewById(R.id.start_profile)
        highscore = v.findViewById(R.id.start_highscore)
        start = v.findViewById(R.id.start_start)
        ultraViewPager = v.findViewById(R.id.start_slider)
        onClicks()
        CoroutineScope(Dispatchers.IO).launch {
            getSlider()
        }
    }

    private fun updateInfo() {
        val passed = MySharedPreference.getInstance(requireContext()).getDaysPassed()
        when {
            passed in 0..6 -> info.text = requireContext().getString(R.string.start_info, db.where<QuestionModel>()
                    .equalTo("userAnswer", "-1")
                    .findAll().size.toString())
            passed < 0 -> info.setText(R.string.start_info_notstarted)
            else -> {
                info.text = requireContext().getString(R.string.start_info_passed)
                start.isEnabled = false
            }
        }
    }

    private fun initViewPager(pagerAdapter: PagerAdapter, count: Int) {
        ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL)
        ultraViewPager.adapter = pagerAdapter
        ultraViewPager.setAutoMeasureHeight(true)
        if (count > 1) {
            ultraViewPager.initIndicator()
            ultraViewPager.indicator
                    .setOrientation(UltraViewPager.Orientation.HORIZONTAL)
                    .setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM)
                    .setMargin(0, 0, 0, 16)
                    .setFocusColor(-0x328ca)
                    .setNormalColor(-0x13100f)
                    .setRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, requireContext().resources.displayMetrics).toInt())
                    .build()
            ultraViewPager.setInfiniteLoop(true)
            ultraViewPager.setAutoScroll(5000)
        }
    }

    private fun onClicks() {
        profile.setOnClickListener { navController.navigate(R.id.action_menu_start_to_menu_profile) }
        highscore.setOnClickListener { navController.navigate(R.id.action_menu_start_to_menu_highscore) }
        start.setOnClickListener {
            val passed = MySharedPreference.getInstance(requireContext()).getDaysPassed()
            val remaining = db.where<QuestionModel>().equalTo("userAnswer", "-1").findAll().size
            when {
                passed < 0 -> Toast.makeText(context, requireContext().getString(R.string.start_info_notstarted), Toast.LENGTH_LONG).show()
                passed > 6 -> Toast.makeText(context, requireContext().getString(R.string.start_info_passed), Toast.LENGTH_LONG).show()
                remaining == 0 -> Toast.makeText(context, requireContext().getString(R.string.general_noquestions_at_all), Toast.LENGTH_LONG).show()
                remaining > 0 -> startActivity(Intent(activity, QuestionsActivity::class.java))
            }
        }
    }

    private suspend fun getSlider() {
        when (val res = ApiRepository(RemoteDataSource().getApi(NetworkApi::class.java)).getSlider()) {
            is Resource.Success -> {
                if (res.value.result == "success" && res.value.message != "empty")
                    withContext(Dispatchers.Main) {
                        initViewPager(MainViewPager(requireActivity(), res.value.data), res.value.data.size)
                    }
                else
                    withContext(Dispatchers.Main) {
                        ultraViewPager.visibility = View.GONE
                    }
            }
            is Resource.Failure -> {
                withContext(Dispatchers.Main) {
                    ultraViewPager.visibility = View.GONE
                }
            }
        }
    }
}