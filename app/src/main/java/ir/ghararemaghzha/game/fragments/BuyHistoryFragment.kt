package ir.ghararemaghzha.game.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.adapters.BuyHistoryAdapter
import ir.ghararemaghzha.game.classes.MySharedPreference
import ir.ghararemaghzha.game.classes.Utils
import ir.ghararemaghzha.game.data.ApiRepository
import ir.ghararemaghzha.game.data.NetworkApi
import ir.ghararemaghzha.game.data.RemoteDataSource
import ir.ghararemaghzha.game.data.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BuyHistoryFragment : Fragment(R.layout.fragment_buy_history) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BuyHistoryAdapter
    private lateinit var loading: ConstraintLayout
    private lateinit var empty: ConstraintLayout


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    private fun init(v: View) {
        requireActivity().findViewById<MaterialTextView>(R.id.toolbar_title).setText(R.string.buyhistory_title)
        loading = v.findViewById(R.id.buyhistory_loading)
        empty = v.findViewById(R.id.buyhistory_empty)
        recyclerView = v.findViewById(R.id.buyhistory_recycler)
        recyclerView.layoutManager = LinearLayoutManager(context)
        loading.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            getData()

        }
    }

    private suspend fun getData() {
        val number = MySharedPreference.getInstance(requireContext()).getNumber()
        val token = MySharedPreference.getInstance(requireContext()).getAccessToken()
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(requireActivity(), true)
        }

        when (val res = ApiRepository(RemoteDataSource().getApi(NetworkApi::class.java)).getBuyHistory("Bearer $token", number)) {
            is Resource.Success -> {

                if (res.value.result == "success" && res.value.message == "all history") {
                    val data = res.value.data
                    withContext(Dispatchers.Main) {
                        loading.visibility = View.GONE
                        adapter = BuyHistoryAdapter(requireContext(), data)
                        recyclerView.adapter = adapter
                    }
                } else
                    withContext(Dispatchers.Main) {
                        loading.visibility = View.GONE
                        empty.visibility = View.VISIBLE }

            }
            is Resource.Failure -> {

                if (res.isNetworkError) {
                    withContext(Dispatchers.Main) {
                        loading.visibility = View.GONE
                        Toast.makeText(context, getString(R.string.general_error), Toast.LENGTH_SHORT).show()
                        empty.visibility = View.VISIBLE
                    }
                } else if (res.errorCode == 401) {
                    withContext(Dispatchers.Main) {
                        loading.visibility = View.GONE
                        Utils.logout(activity, true)
                    }
                }

            }
        }
    }
}