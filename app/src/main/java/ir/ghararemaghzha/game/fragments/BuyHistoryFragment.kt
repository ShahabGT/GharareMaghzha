package ir.ghararemaghzha.game.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textview.MaterialTextView
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.adapters.BuyHistoryAdapter
import ir.ghararemaghzha.game.classes.MySharedPreference
import ir.ghararemaghzha.game.classes.Utils
import ir.ghararemaghzha.game.classes.Utils.Companion.showInternetError
import ir.ghararemaghzha.game.data.Resource
import ir.ghararemaghzha.game.databinding.FragmentBuyHistoryBinding
import ir.ghararemaghzha.game.viewmodels.BuyHistoryViewModel

class BuyHistoryFragment : BaseFragment<BuyHistoryViewModel, FragmentBuyHistoryBinding>() {

    private lateinit var adapter: BuyHistoryAdapter
    private lateinit var number: String
    private lateinit var token: String


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()

        viewModel.buyHistoryResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is Resource.Success -> {

                    if (res.value.result == "success" && res.value.message == "all history") {
                        val data = res.value.data
                        b.buyhistoryLoading.visibility = View.GONE
                        adapter = BuyHistoryAdapter(requireContext(), data)
                        b.buyhistoryRecycler.adapter = adapter

                    } else {
                        b.buyhistoryLoading.visibility = View.GONE
                        b.buyhistoryEmpty.visibility = View.VISIBLE
                    }

                }
                is Resource.Failure -> {

                    if (res.isNetworkError) {
                        showInternetError(requireContext()) { viewModel.getBuyHistory("Bearer $token", number) }
                        Toast.makeText(context, getString(R.string.general_error), Toast.LENGTH_SHORT).show()

                    } else if (res.errorCode == 401) {
                        b.buyhistoryLoading.visibility = View.GONE
                        Utils.logout(requireActivity(), true)
                    }
                }
                is Resource.Loading -> {
                }
            }
        })
    }


    private fun init() {
        requireActivity().findViewById<MaterialTextView>(R.id.toolbar_title).setText(R.string.buyhistory_title)

        b.buyhistoryRecycler.layoutManager = LinearLayoutManager(context)
        b.buyhistoryLoading.visibility = View.VISIBLE

        number = MySharedPreference.getInstance(requireContext()).getNumber()
        token = MySharedPreference.getInstance(requireContext()).getAccessToken()
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(requireActivity(), true)
            return
        }

        viewModel.getBuyHistory("Bearer $token", number)

    }

    override fun getViewModel() = BuyHistoryViewModel::class.java

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentBuyHistoryBinding.inflate(inflater, container, false)
}