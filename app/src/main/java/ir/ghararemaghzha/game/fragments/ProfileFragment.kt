package ir.ghararemaghzha.game.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.tmall.ultraviewpager.UltraViewPager
import com.tmall.ultraviewpager.transformer.UltraDepthScaleTransformer
import io.realm.Realm
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.adapters.ProfileViewPager
import ir.ghararemaghzha.game.classes.Const.GHARAREHMAGHZHA_BROADCAST_REFRESH
import ir.ghararemaghzha.game.classes.MySharedPreference
import ir.ghararemaghzha.game.dialogs.UserDetailsDialog
import ir.ghararemaghzha.game.models.ProfileModel
import ir.ghararemaghzha.game.models.QuestionModel
import java.util.*
import kotlin.math.abs

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private lateinit var navController: NavController
    private lateinit var buy: MaterialCardView
    private lateinit var edit: MaterialCardView
    private lateinit var stat: MaterialCardView
    private lateinit var scoreHelper: MaterialCardView
    private lateinit var db: Realm
    private lateinit var v: View

    private val br: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateUI()
        }
    }

    override fun onResume() {
        super.onResume()
        requireContext().registerReceiver(br, IntentFilter(GHARAREHMAGHZHA_BROADCAST_REFRESH))
    }

    override fun onPause() {
        super.onPause()
        requireContext().unregisterReceiver(br)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        v = view
        navController = Navigation.findNavController(view)
        init()
    }

    private fun init() {
        requireActivity().findViewById<MaterialTextView>(R.id.toolbar_title).setText(R.string.profile_title)
        db = Realm.getDefaultInstance()
        buy = v.findViewById(R.id.profile_buy)
        edit = v.findViewById(R.id.profile_edit)
        stat = v.findViewById(R.id.profile_stat)
        scoreHelper = v.findViewById(R.id.profile_scorehelper)
        updateUI()
        onClicks()
    }

    private fun onClicks() {
        buy.setOnClickListener { navController.navigate(R.id.action_menu_profile_to_menu_buy) }
        edit.setOnClickListener { navController.navigate(R.id.action_global_profileEditFragment) }
        stat.setOnClickListener { showDetailsDialog(MySharedPreference.getInstance(requireContext()).getUserId()) }
        scoreHelper.setOnClickListener { navController.navigate(R.id.action_global_scoreHelperFragment) }
    }

    private fun showDetailsDialog(userId: String) {
        val dialog = UserDetailsDialog(requireActivity(), userId)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.show()
        val window = dialog.window
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    private fun updateUI() {
        val data: MutableList<ProfileModel> = ArrayList()
        data.add(ProfileModel(
                requireContext().getString(R.string.avatar_url, MySharedPreference.getInstance(requireContext()).getUserAvatar()),
                MySharedPreference.getInstance(requireContext()).getUsername(),
                "امتیاز من: " + MySharedPreference.getInstance(requireContext()).getScore()))
        val passed = MySharedPreference.getInstance(requireContext()).getDaysPassed()
        if (passed < 0) {
            data.add(ProfileModel(R.drawable.profile_time,
                    requireContext().getString(R.string.profile_time_card_tostart),
                    requireContext().getString(R.string.profile_time, abs(passed).toString())))
        } else {
            val model = ProfileModel(R.drawable.profile_time, requireContext().getString(R.string.profile_time_card), "")
            when {
                passed == 6 -> model.subtitle = requireContext().getString(R.string.profile_time_lastday)

                passed >= 7 -> model.subtitle = requireContext().getString(R.string.profile_time_end)

                else -> model.subtitle = requireContext().getString(R.string.profile_time, (7 - passed).toString())
            }

            data.add(model)
        }
        data.add(ProfileModel(R.drawable.profile_total, "سوالات این دوره", db.where(QuestionModel::class.java).findAll().size.toString()))
        data.add(ProfileModel(R.drawable.profile_remain, "سوالات من", db.where(QuestionModel::class.java)
                .equalTo("userAnswer", "-1").findAll().size.toString()))
        initViewPager(data)
    }


    private fun initViewPager(data: List<ProfileModel>) {
        val ultraViewPager: UltraViewPager = v.findViewById(R.id.profile_recycler)
        ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL)
        ultraViewPager.setPageTransformer(true, UltraDepthScaleTransformer())
        ultraViewPager.setMultiScreen(0.6f)
        ultraViewPager.setItemRatio(1.0)
        ultraViewPager.setInfiniteLoop(true)
        ultraViewPager.adapter = ProfileViewPager(requireContext(), data)
        ultraViewPager.setAutoScroll(4000)
    }

}