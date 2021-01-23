package ir.ghararemaghzha.game.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.textview.MaterialTextView
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.classes.MySharedPreference
import ir.ghararemaghzha.game.classes.Utils
import ir.ghararemaghzha.game.classes.Utils.Companion.shareCode
import ir.ghararemaghzha.game.classes.Utils.Companion.showInternetError
import ir.ghararemaghzha.game.data.Resource
import ir.ghararemaghzha.game.databinding.FragmentInviteBinding
import ir.ghararemaghzha.game.viewmodels.InviteViewModel

class InviteFragment : BaseFragment<InviteViewModel, FragmentInviteBinding>() {
    private lateinit var navController: NavController
    private lateinit var number: String
    private lateinit var token: String


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()

        viewModel.invitesResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is Resource.Success -> {
                    if (res.value.result == "success") {
                        val fCount = res.value.count.toFloat()
                        val remaining = 20 - fCount.toInt()
                        b.inviteCount.text = getString(R.string.invite_star_title, remaining)
                        b.inviteProgress.progress = fCount
                        b.inviteInfoText1.text = getString(R.string.invite_info_text1, res.value.value)
                        b.inviteLoading.visibility = View.GONE

                    } else {
                        showInternetError(requireContext()) { viewModel.getInvites("Bearer $token", number) }
                        Toast.makeText(context, getString(R.string.general_error), Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Failure -> {
                    if (res.isNetworkError) {
                        showInternetError(requireContext()) { viewModel.getInvites("Bearer $token", number) }
                        Toast.makeText(context, getString(R.string.general_error), Toast.LENGTH_SHORT).show()
                    } else if (res.errorCode == 401) {
                        b.inviteLoading.visibility = View.GONE
                        Utils.logout(requireActivity(), true)
                    }
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

    private fun init() {
        requireActivity().findViewById<MaterialTextView>(R.id.toolbar_title).setText(R.string.invite_title)
        b.inviteCode.text = MySharedPreference.getInstance(requireContext()).getUserCode()
        b.inviteShare.setOnClickListener {
            shareCode(requireContext(), getString(R.string.invite_share, MySharedPreference.getInstance(requireContext()).getUserCode()), "")
        }
        b.inviteContacts.setOnClickListener {
            navController.navigate(R.id.action_inviteFragment_to_contactsFragment)
        }
        b.inviteLoading.visibility = View.VISIBLE

        number = MySharedPreference.getInstance(requireContext()).getNumber()
        token = MySharedPreference.getInstance(requireContext()).getAccessToken()
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(requireActivity(), true)
            return
        }

        viewModel.getInvites("Bearer $token", number)

    }

    override fun getViewModel() = InviteViewModel::class.java

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentInviteBinding.inflate(inflater, container, false)
}