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
import ir.ghararemaghzha.game.adapters.HighscoreAdapter
import ir.ghararemaghzha.game.classes.MySharedPreference
import ir.ghararemaghzha.game.classes.RetryInterface
import ir.ghararemaghzha.game.classes.Utils
import ir.ghararemaghzha.game.data.ApiRepository
import ir.ghararemaghzha.game.data.NetworkApi
import ir.ghararemaghzha.game.data.RemoteDataSource
import ir.ghararemaghzha.game.data.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HighscoreFragment : Fragment(R.layout.fragment_highscore) {
    private lateinit var loading: ConstraintLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HighscoreAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    private fun init(v: View) {
        requireActivity().findViewById<MaterialTextView>(R.id.toolbar_title).setText(R.string.highscore_title)
        recyclerView = v.findViewById(R.id.highscore_recycler)
        recyclerView.layoutManager = LinearLayoutManager(context)
        loading = v.findViewById(R.id.highscore_loading)
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
            return
        }

        when (val res = ApiRepository(RemoteDataSource().getApi(NetworkApi::class.java)).getHighscoreList("Bearer $token", number)) {
            is Resource.Success -> {
                if (res.value.result == "success") {
                    val data = res.value.data
                    val user = res.value.user
                    var showUser = true
                    for (i in data) {
                        if (i.userId == user.userId) {
                            showUser = false
                            break
                        }
                    }
                    withContext(Dispatchers.Main) {
                        adapter = HighscoreAdapter(requireActivity(), data, user, showUser)
                        recyclerView.adapter = adapter
                        loading.visibility = View.GONE
                    }

                } else {
                    withContext(Dispatchers.Main) {
                        Utils.showInternetError(context, object : RetryInterface {
                            override fun retry() {
                                CoroutineScope(Dispatchers.IO).launch {
                                    getData()
                                }
                            }
                        })
                        Toast.makeText(context, getString(R.string.general_error), Toast.LENGTH_SHORT).show()
                    }
                }
            }

            is Resource.Failure -> {

                if (res.isNetworkError) {
                    withContext(Dispatchers.Main) {
                        Utils.showInternetError(context, object : RetryInterface {
                            override fun retry() {
                                CoroutineScope(Dispatchers.IO).launch {
                                    getData()
                                }
                            }
                        })
                        Toast.makeText(context, getString(R.string.general_error), Toast.LENGTH_SHORT).show()
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