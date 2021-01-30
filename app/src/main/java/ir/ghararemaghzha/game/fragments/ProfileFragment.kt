package ir.ghararemaghzha.game.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.textview.MaterialTextView
import com.tmall.ultraviewpager.UltraViewPager
import com.tmall.ultraviewpager.transformer.UltraDepthScaleTransformer
import io.realm.Realm
import io.realm.kotlin.where
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.adapters.ProfileViewPager
import ir.ghararemaghzha.game.classes.Const.GHARAREHMAGHZHA_BROADCAST_REFRESH
import ir.ghararemaghzha.game.classes.MySharedPreference
import ir.ghararemaghzha.game.databinding.FragmentProfileBinding
import ir.ghararemaghzha.game.dialogs.UserDetailsDialog
import ir.ghararemaghzha.game.models.ProfileModel
import ir.ghararemaghzha.game.models.QuestionModel
import java.util.*
import kotlin.math.abs

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var b: FragmentProfileBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        b = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return b.root
    }

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    private fun init() {
        requireActivity().findViewById<MaterialTextView>(R.id.toolbar_title).setText(R.string.profile_title)
        updateUI()
        onClicks()
    }

    private fun onClicks() {
        b.profileBuy.setOnClickListener { requireView().findNavController().navigate(R.id.action_menu_profile_to_menu_buy) }
        b.profileEdit.setOnClickListener { requireView().findNavController().navigate(R.id.action_global_profileEditFragment) }
        b.profileStat.setOnClickListener { showDetailsDialog(MySharedPreference.getInstance(requireContext()).getUserId()) }
        b.profileScorehelper.setOnClickListener { requireView().findNavController().navigate(R.id.action_global_scoreHelperFragment) }
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
        val db = Realm.getDefaultInstance()
        val data: MutableList<ProfileModel> = ArrayList()
        data.add(ProfileModel(
                getString(R.string.avatar_url, MySharedPreference.getInstance(requireContext()).getUserAvatar()),
                MySharedPreference.getInstance(requireContext()).getUsername(),
                "امتیاز من: " + MySharedPreference.getInstance(requireContext()).getScore()))
        val passed = MySharedPreference.getInstance(requireContext()).getDaysPassed()
        if (passed < 0) {
            data.add(ProfileModel(R.drawable.profile_time,
                    getString(R.string.profile_time_card_tostart),
                    getString(R.string.profile_time, abs(passed).toString())))
        } else {
            val model = ProfileModel(R.drawable.profile_time, getString(R.string.profile_time_card), "")
            when {
                passed == 6 -> model.subtitle = getString(R.string.profile_time_lastday)

                passed >= 7 -> model.subtitle = getString(R.string.profile_time_end)

                else -> model.subtitle = getString(R.string.profile_time, (7 - passed).toString())
            }

            data.add(model)
        }
        data.add(ProfileModel(R.drawable.profile_total, "سوالات این دوره", db.where<QuestionModel>().findAll().size.toString()))
        data.add(ProfileModel(R.drawable.profile_remain, "سوالات من", db.where<QuestionModel>().equalTo("userAnswer", "-1").findAll().size.toString()))
        initViewPager(data)
    }

    private fun initViewPager(data: List<ProfileModel>) {
        b.profileRecycler.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL)
        b.profileRecycler.setPageTransformer(true, UltraDepthScaleTransformer())
        b.profileRecycler.setMultiScreen(0.6f)
        b.profileRecycler.setItemRatio(1.0)
        b.profileRecycler.setInfiniteLoop(true)
        b.profileRecycler.adapter = ProfileViewPager(requireContext(), data)
        b.profileRecycler.setAutoScroll(4000)
    }

}