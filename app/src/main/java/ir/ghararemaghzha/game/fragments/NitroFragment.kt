package ir.ghararemaghzha.game.fragments

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.classes.BuyInterface
import ir.ghararemaghzha.game.classes.MySharedPreference
import ir.ghararemaghzha.game.classes.RetryInterface
import ir.ghararemaghzha.game.classes.Utils
import ir.ghararemaghzha.game.data.ApiRepository
import ir.ghararemaghzha.game.data.NetworkApi
import ir.ghararemaghzha.game.data.RemoteDataSource
import ir.ghararemaghzha.game.data.Resource
import ir.ghararemaghzha.game.dialogs.BuyDialog
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)

    }

    private fun init(v: View) {
        requireActivity().findViewById<MaterialTextView>(R.id.toolbar_title).setText(R.string.buy_title)


        buy = v.findViewById(R.id.nitro_buy)
        price = v.findViewById(R.id.nitro_price)
        title = v.findViewById(R.id.nitro_title)
        progressBar = v.findViewById(R.id.nitro_progress)
        loading = v.findViewById(R.id.nitro_loading)

        CoroutineScope(Dispatchers.IO).launch {
            getData()
        }

        buy.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                initBuy("7", "", "", amount)
            }
        }
    }

    private fun showBuyDialog(planPrice: String, Plan: String, buyInterface: BuyInterface, isScoreBooster: Boolean) {
        val dialog = BuyDialog(requireActivity(), planPrice, Plan, buyInterface, isScoreBooster)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.show()
        val window = dialog.window
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }


    private suspend fun getData() {
        val number = MySharedPreference.getInstance(requireContext()).getNumber()
        val token = MySharedPreference.getInstance(requireContext()).getAccessToken()
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(requireActivity(), true)
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
                        val passed = MySharedPreference.getInstance(requireContext()).getDaysPassed()
                        val booster = MySharedPreference.getInstance(requireContext()).getBoosterValue()

                        when {
                            userPlan >= 5 -> {
                                price.text = getString(R.string.nitro_price3)
                                buy.setBackgroundColor(requireContext().resources.getColor(R.color.grey))
                                buy.isEnabled = false
                            }
                            booster != 1f -> {
                                price.text = getString(R.string.nitro_price2)
                                buy.setBackgroundColor(requireContext().resources.getColor(R.color.grey))
                                buy.isEnabled = false
                            }
                            passed < 0 || passed > 6 -> {
                                price.text = getString(R.string.nitro_price4)
                                buy.setBackgroundColor(requireContext().resources.getColor(R.color.grey))
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
                        Utils.showInternetError(context, object : RetryInterface {
                            override fun retry() {
                                CoroutineScope(Dispatchers.IO).launch {
                                    getData()
                                }
                            }
                        })
                        loading.visibility = View.GONE
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
                        loading.visibility = View.GONE
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

    private suspend fun initBuy(plan: String, influencerId: String, influencerAmount: String, amount: String) {
        val number = MySharedPreference.getInstance(requireContext()).getNumber()
        val token = MySharedPreference.getInstance(requireContext()).getAccessToken()
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(requireActivity(), true)
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
                        requireContext().startActivity(i)
                    }

                } else {
                    withContext(Dispatchers.Main) {
                        loading.visibility = View.GONE
                        Toast.makeText(context, getString(R.string.general_error), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            is Resource.Failure -> {

                if (res.isNetworkError) {
                    withContext(Dispatchers.Main) {
                        loading.visibility = View.GONE
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