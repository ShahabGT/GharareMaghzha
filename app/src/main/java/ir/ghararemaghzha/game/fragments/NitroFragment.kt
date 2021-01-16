package ir.ghararemaghzha.game.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import ir.ghararemaghzha.game.R
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


class NitroFragment : Fragment(R.layout.fragment_nito) {

    private lateinit var price: MaterialTextView
    private lateinit var loading: ConstraintLayout
    private lateinit var title: MaterialTextView
    private lateinit var progressBar: RoundCornerProgressBar
    private lateinit var buy: MaterialButton
    private var amount: String = ""
    private lateinit var act: FragmentActivity
    private lateinit var ctx: Context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = super.onCreateView(inflater, container, savedInstanceState)
        act = requireActivity()
        ctx = requireContext()
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)

    }


    private fun init(v: View) {
        act.findViewById<MaterialTextView>(R.id.toolbar_title).setText(R.string.buy_title)


        buy = v.findViewById(R.id.nitro_buy)
        price = v.findViewById(R.id.nitro_price)
        title = v.findViewById(R.id.nitro_title)
        progressBar = v.findViewById(R.id.nitro_progress)
        loading = v.findViewById(R.id.nitro_loading)

        buy.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                initBuy("7", "", "", amount)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loading.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            getData()
        }
    }

    private suspend fun getData() {
        val number = MySharedPreference.getInstance(ctx).getNumber()
        val token = MySharedPreference.getInstance(ctx).getAccessToken()
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(act, true)
            return
        }
        when (val res = ApiRepository(RemoteDataSource().getApi(NetworkApi::class.java)).getNitro("Bearer $token", number)) {
            is Resource.Success -> {
                if (res.value.result == "success") {
                    withContext(Dispatchers.Main) {
                        title.text = getString(R.string.nitro_title, res.value.planValue)
                        val userPlan = res.value.userPlan.toInt()
                        progressBar.progress = userPlan.toFloat()
                        amount = res.value.planPrice
                        val passed = MySharedPreference.getInstance(ctx).getDaysPassed()
                        val booster = MySharedPreference.getInstance(ctx).getBoosterValue()

                        when {
                            userPlan >= 5 -> {
                                price.text = getString(R.string.nitro_price3)
                                buy.setBackgroundColor(ctx.resources.getColor(R.color.grey))
                                buy.isEnabled = false
                            }
                            booster != 1f -> {
                                price.text = getString(R.string.nitro_price2)
                                buy.setBackgroundColor(ctx.resources.getColor(R.color.grey))
                                buy.isEnabled = false
                            }
                            passed < 0 || passed > 6 -> {
                                price.text = getString(R.string.nitro_price4)
                                buy.setBackgroundColor(ctx.resources.getColor(R.color.grey))
                                buy.isEnabled = false
                            }
                            else -> {
                                price.text = getString(R.string.nitro_price, amount)
                                buy.isEnabled = true
                            }
                        }
                        loading.visibility = View.GONE
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Utils.showInternetError(ctx, object : RetryInterface {
                            override fun retry() {
                                CoroutineScope(Dispatchers.IO).launch {
                                    getData()
                                }
                            }
                        })
                        loading.visibility = View.GONE
                        Toast.makeText(ctx, getString(R.string.general_error), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            is Resource.Failure -> {

                if (res.isNetworkError) {
                    withContext(Dispatchers.Main) {
                        Utils.showInternetError(ctx, object : RetryInterface {
                            override fun retry() {
                                CoroutineScope(Dispatchers.IO).launch {
                                    getData()
                                }
                            }
                        })
                        loading.visibility = View.GONE
                        Toast.makeText(ctx, getString(R.string.general_error), Toast.LENGTH_SHORT).show()
                    }
                } else if (res.errorCode == 401) {
                    withContext(Dispatchers.Main) {
                        loading.visibility = View.GONE
                        Utils.logout(act, true)
                    }
                }

            }
        }
    }

    private suspend fun initBuy(plan: String, influencerId: String, influencerAmount: String, amount: String) {
        val number = MySharedPreference.getInstance(ctx).getNumber()
        val token = MySharedPreference.getInstance(ctx).getAccessToken()
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(act, true)
            return
        }
        when (val res = ApiRepository(RemoteDataSource().getApi(NetworkApi::class.java)).initBuy("Bearer $token", number, plan, influencerId, influencerAmount, amount)) {
            is Resource.Success -> {
                if (res.value.merchantId.isNotEmpty()) {
                    withContext(Dispatchers.Main) {
                        //  loading.visibility = View.GONE
                        val merchant: String = res.value.merchantId
                        val url = "https://ghararehmaghzha.ir/api/buy/buy?merchant=$merchant"
                        val i = Intent(Intent.ACTION_VIEW)
                        i.data = Uri.parse(url)
                        ctx.startActivity(i)
                    }

                } else {
                    withContext(Dispatchers.Main) {
                        loading.visibility = View.GONE
                        Toast.makeText(ctx, getString(R.string.general_error), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            is Resource.Failure -> {

                if (res.isNetworkError) {
                    withContext(Dispatchers.Main) {
                        loading.visibility = View.GONE
                        Toast.makeText(ctx, getString(R.string.general_error), Toast.LENGTH_SHORT).show()
                    }
                } else if (res.errorCode == 401) {
                    withContext(Dispatchers.Main) {
                        loading.visibility = View.GONE
                        Utils.logout(act, true)
                    }
                }

            }
        }
    }


}