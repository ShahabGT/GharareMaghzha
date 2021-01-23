package ir.ghararemaghzha.game.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.textview.MaterialTextView
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.classes.MySharedPreference
import ir.ghararemaghzha.game.classes.Utils
import ir.ghararemaghzha.game.data.Resource
import ir.ghararemaghzha.game.databinding.FragmentNitoBinding
import ir.ghararemaghzha.game.viewmodels.NitroViewModel

class NitroFragment : BaseFragment<NitroViewModel, FragmentNitoBinding>() {

    private var amount: String = ""
    private lateinit var number: String
    private lateinit var token: String

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()

        viewModel.initBuyResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is Resource.Success -> {
                    if (res.value.merchantId.isNotEmpty()) {
                        val merchant: String = res.value.merchantId
                        val url = "https://ghararehmaghzha.ir/api/buy/buy?merchant=$merchant"
                        val i = Intent(Intent.ACTION_VIEW).also {
                            it.data = Uri.parse(url)
                        }
                        requireContext().startActivity(i)
                    } else {
                        b.nitroLoading.visibility = View.GONE
                        Toast.makeText(context, getString(R.string.general_error), Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Failure -> {
                    if (res.isNetworkError) {
                        b.nitroLoading.visibility = View.GONE
                        Toast.makeText(context, getString(R.string.general_error), Toast.LENGTH_SHORT).show()
                    } else if (res.errorCode == 401) {
                        b.nitroLoading.visibility = View.GONE
                        Utils.logout(requireActivity(), true)
                    } else {
                        b.nitroLoading.visibility = View.GONE
                        Toast.makeText(context, getString(R.string.general_error), Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> {
                }
            }
        })

        viewModel.nitroResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is Resource.Success -> {
                    if (res.value.result == "success") {
                        b.nitroTitle.text = getString(R.string.nitro_title, res.value.planValue)
                        val userPlan = res.value.userPlan.toInt()
                        b.nitroProgress.progress = userPlan.toFloat()
                        amount = res.value.planPrice
                        val passed = MySharedPreference.getInstance(requireContext()).getDaysPassed()
                        val booster = MySharedPreference.getInstance(requireContext()).getBoosterValue()

                        when {
                            userPlan >= 5 -> {
                                b.nitroPrice.text = getString(R.string.nitro_price3)
                                b.nitroBuy.setBackgroundColor(requireContext().resources.getColor(R.color.grey))
                                b.nitroBuy.isEnabled = false
                            }
                            booster != 1f -> {
                                b.nitroPrice.text = getString(R.string.nitro_price2)
                                b.nitroBuy.setBackgroundColor(requireContext().resources.getColor(R.color.grey))
                                b.nitroBuy.isEnabled = false
                            }
                            passed < 0  -> {
                                b.nitroPrice.text = getString(R.string.nitro_price4)
                                b.nitroBuy.setBackgroundColor(requireContext().resources.getColor(R.color.grey))
                                b.nitroBuy.isEnabled = false
                            }
                            passed > 6 -> {
                            b.nitroPrice.text = getString(R.string.nitro_price5)
                            b.nitroBuy.setBackgroundColor(requireContext().resources.getColor(R.color.grey))
                            b.nitroBuy.isEnabled = false
                        }
                            else -> {
                                b.nitroPrice.text = getString(R.string.nitro_price, amount)
                                b.nitroBuy.isEnabled = true
                            }
                        }
                        b.nitroLoading.visibility = View.GONE

                    } else {
                        Utils.showInternetError(requireContext()){viewModel.getNitro("Bearer $token",number) }
                        b.nitroLoading.visibility = View.GONE
                        Toast.makeText(context, getString(R.string.general_error), Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Failure -> {
                    if (res.isNetworkError) {
                        Utils.showInternetError(requireContext()){viewModel.getNitro("Bearer $token",number)}
                        b.nitroLoading.visibility = View.GONE
                        Toast.makeText(context, getString(R.string.general_error), Toast.LENGTH_SHORT).show()

                    } else if (res.errorCode == 401) {
                        b.nitroLoading.visibility = View.GONE
                        Utils.logout(requireActivity(), true)
                    }else{
                        Utils.showInternetError(requireContext()){viewModel.getNitro("Bearer $token",number) }
                        b.nitroLoading.visibility = View.GONE
                        Toast.makeText(context, getString(R.string.general_error), Toast.LENGTH_SHORT).show()
                    }

                }
                is Resource.Loading->{}
            }
        })
    }


    private fun init() {
        number = MySharedPreference.getInstance(requireContext()).getNumber()
        token = MySharedPreference.getInstance(requireContext()).getAccessToken()
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(requireActivity(), true)
            return
        }


        requireActivity().findViewById<MaterialTextView>(R.id.toolbar_title).setText(R.string.buy_title)

        b.nitroBuy.setOnClickListener {
            viewModel.initBuy("Bearer $token", number, "7", "", "", amount)
        }
    }

    override fun onResume() {
        super.onResume()
        b.nitroLoading.visibility = View.VISIBLE
        viewModel.getNitro("Bearer $token", number)
    }

    override fun getViewModel() = NitroViewModel::class.java

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentNitoBinding.inflate(inflater, container, false)


}