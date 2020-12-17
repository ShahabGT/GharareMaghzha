package ir.ghararemaghzha.game.dialogs

import android.app.Activity
import android.app.Dialog
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.classes.BuyInterface
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

class BuyDialog(ctx: FragmentActivity,
                private var amount: String,
                private val plan: String,
                private val buyInterface: BuyInterface,
                private val isScoreBooster: Boolean) : Dialog(ctx) {

    private var influencerId = ""
    private var influencerAmount = ""
    private lateinit var buy: MaterialButton
    private lateinit var cancel: MaterialButton
    private lateinit var giftCodeButton: MaterialButton
    private lateinit var giftCodeCheck: MaterialButton
    private lateinit var tAmount: MaterialTextView
    private lateinit var tTitle: MaterialTextView
    private lateinit var tDiscountAmount: MaterialTextView
    private lateinit var tDiscountTitle: MaterialTextView
    private lateinit var giftCode: EditText
    private lateinit var linearLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_buy)
        init()
    }

    private fun init() {
        buy = findViewById(R.id.buy_dialog_buy)
        cancel = findViewById(R.id.buy_dialog_cancel)
        giftCodeButton = findViewById(R.id.buy_dialog_giftcode_btn)
        giftCodeCheck = findViewById(R.id.buy_dialog_giftcode_check)
        giftCode = findViewById(R.id.buy_dialog_giftcode)
        linearLayout = findViewById(R.id.buy_dialog_linear)
        tTitle = findViewById(R.id.buy_dialog_title)
        tDiscountTitle = findViewById(R.id.buy_dialog_discount_title)
        tAmount = findViewById(R.id.buy_dialog_amount)
        tDiscountAmount = findViewById(R.id.buy_dialog_discount_amount)
        tAmount.text = context.getString(R.string.amount_model, Utils.moneySeparator(amount))
        if (isScoreBooster) {
            giftCodeButton.visibility = View.GONE
        }
        onClicks()
    }

    private fun onClicks() {
        cancel.setOnClickListener { dismiss() }
        giftCodeButton.setOnClickListener { v: View ->
            v.visibility = View.INVISIBLE
            linearLayout.visibility = View.VISIBLE
        }
        giftCodeCheck.setOnClickListener {
            if (!Utils.checkInternet(context))
                Toast.makeText(context, context.getString(R.string.internet_error), Toast.LENGTH_SHORT).show()
            else
                if (giftCode.length() > 2) {
                    giftCode.isEnabled = false
                    giftCodeCheck.isEnabled = false
                    CoroutineScope(Dispatchers.IO).launch { searchInfluencer(giftCode.text.toString()) }
                } else Toast.makeText(context, context.getString(R.string.buy_dialog_giftcode_invalid), Toast.LENGTH_SHORT).show()
        }
        buy.setOnClickListener {
            buyInterface.buy(plan, amount, influencerId, influencerAmount, isScoreBooster)
            dismiss()
        }
    }

    private suspend fun searchInfluencer(giftCode: String) {
        val number = MySharedPreference.getInstance(context).getNumber()
        val token = MySharedPreference.getInstance(context).getAccessToken()
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(context as Activity, true)
            return
        }
        when (val res = ApiRepository(RemoteDataSource().getApi(NetworkApi::class.java)).searchInfluencer("Bearer $token", number, giftCode)) {
            is Resource.Success -> {
                if (res.value.result == "success") {
                    val percent = res.value.influencerAmount.toInt()
                    influencerId = res.value.influencerId
                    influencerAmount = res.value.influencerAmount
                    tTitle.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    tAmount.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG

                    tDiscountTitle.visibility = View.VISIBLE
                    tDiscountTitle.text = context.getString(R.string.buy_dialog_discount_text, percent)
                    amount = "${(amount.toInt() * (100 - percent) / 100)}"
                    tDiscountAmount.visibility = View.VISIBLE
                    tDiscountAmount.text = context.getString(R.string.amount_model, Utils.moneySeparator(amount))

                } else {
                    withContext(Dispatchers.Main) {
                        this@BuyDialog.giftCode.isEnabled = true
                        giftCodeCheck.isEnabled = true
                        Toast.makeText(context, context.getString(R.string.general_error), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            is Resource.Failure -> {
                when {
                    res.isNetworkError -> {
                        withContext(Dispatchers.Main) {
                            this@BuyDialog.giftCode.isEnabled = true
                            giftCodeCheck.isEnabled = true
                            Toast.makeText(context, context.getString(R.string.general_error), Toast.LENGTH_SHORT).show()
                        }
                    }
                    res.errorCode == 401 -> {
                        withContext(Dispatchers.Main) {
                            Utils.logout(context as Activity, true)
                        }
                    }
                    res.errorCode == 404 -> {
                        withContext(Dispatchers.Main) {
                            this@BuyDialog.giftCode.isEnabled = true
                            giftCodeCheck.isEnabled = true
                            Toast.makeText(context, context.getString(R.string.buy_dialog_giftcode_invalid), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}