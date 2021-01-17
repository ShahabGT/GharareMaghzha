package ir.ghararemaghzha.game.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textview.MaterialTextView
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.adapters.HighscoreAdapter
import ir.ghararemaghzha.game.classes.MySharedPreference
import ir.ghararemaghzha.game.classes.Utils
import ir.ghararemaghzha.game.classes.Utils.Companion.showInternetError
import ir.ghararemaghzha.game.data.Resource
import ir.ghararemaghzha.game.databinding.FragmentHighscoreBinding
import ir.ghararemaghzha.game.viewmodels.HighscoreViewModel

class HighscoreFragment : BaseFragment<HighscoreViewModel, FragmentHighscoreBinding>() {

    private lateinit var adapter: HighscoreAdapter
    private lateinit var number: String
    private lateinit var token: String

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()

        viewModel.highscoreResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is Resource.Success -> {
                    if (res.value.result == "success") {
                        val data = res.value.data
                        val user = res.value.user
                        var showUser = true
                        for (i in data)
                            if (i.userId == user.userId) {
                                showUser = false
                                break
                            }

                        adapter = HighscoreAdapter(requireActivity(), data, user, showUser)
                        b.highscoreRecycler.adapter = adapter
                        b.highscoreLoading.visibility = View.GONE

                    } else {
                        showInternetError(requireContext()) { viewModel.getHighscoreList("Bearer $token", number) }
                        Toast.makeText(context, getString(R.string.general_error), Toast.LENGTH_SHORT).show()
                    }
                }

                is Resource.Failure -> {
                    if (res.isNetworkError) {
                        showInternetError(requireContext()) { viewModel.getHighscoreList("Bearer $token", number) }
                        Toast.makeText(context, getString(R.string.general_error), Toast.LENGTH_SHORT).show()
                    } else if (res.errorCode == 401) {
                        b.highscoreLoading.visibility = View.GONE
                        Utils.logout(requireActivity(), true)
                    }
                }
                is Resource.Loading -> {
                }
            }
        })
    }

    private fun init() {
        requireActivity().findViewById<MaterialTextView>(R.id.toolbar_title).setText(R.string.highscore_title)
        b.highscoreRecycler.layoutManager = LinearLayoutManager(context)
        b.highscoreLoading.visibility = View.VISIBLE

        number = MySharedPreference.getInstance(requireContext()).getNumber()
        token = MySharedPreference.getInstance(requireContext()).getAccessToken()
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(requireActivity(), true)
            return
        }

        viewModel.getHighscoreList("Bearer $token", number)
    }

    override fun getViewModel() = HighscoreViewModel::class.java
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentHighscoreBinding.inflate(inflater, container, false)
}