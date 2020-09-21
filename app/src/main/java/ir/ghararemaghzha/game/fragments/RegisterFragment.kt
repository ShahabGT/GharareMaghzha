package ir.ghararemaghzha.game.fragments

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.classes.Utils
import ir.ghararemaghzha.game.data.ApiRepository
import ir.ghararemaghzha.game.data.NetworkApi
import ir.ghararemaghzha.game.data.RemoteDataSource
import ir.ghararemaghzha.game.data.Resource
import ir.ghararemaghzha.game.dialogs.RulesDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterFragment : Fragment(R.layout.fragment_register) {
    private lateinit var ctx: Context
    private lateinit var act: FragmentActivity
    private val resolveHint = 521
    private lateinit var login: MaterialTextView
    private lateinit var ruleText: MaterialTextView
    private lateinit var ruleCheck: CheckBox
    private lateinit var number: TextInputEditText
    private lateinit var name: TextInputEditText
    private lateinit var verify: MaterialButton

    private var loginNumber: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ctx = requireContext()
        act = requireActivity()
        if (arguments != null)
            loginNumber = requireArguments().getString("number", "")
        init(view)

    }


    private fun init(v: View) {
        name = v.findViewById(R.id.reg_name)
        number = v.findViewById(R.id.reg_number)
        verify = v.findViewById(R.id.reg_verify)
        login = v.findViewById(R.id.reg_login)
        ruleText = v.findViewById(R.id.reg_rules_text)
        ruleCheck = v.findViewById(R.id.reg_rules_check)


        val tradeMarkText = ctx.getString(R.string.registerfragment_rules)
        val spannableString = SpannableString(tradeMarkText)
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                showRulesDialog()
            }

            @Suppress("DEPRECATION")
            override fun updateDrawState(ds: TextPaint) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    ds.color = resources.getColor(R.color.red, null)
                else
                    ds.color = resources.getColor(R.color.red)

                ds.isUnderlineText = true
            }
        }
        spannableString.setSpan(clickableSpan, 0, tradeMarkText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ruleText.text = spannableString
        ruleText.movementMethod = LinkMovementMethod.getInstance()



        if (loginNumber.isEmpty()) {
            try {
                requestHint()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else
            number.setText(loginNumber)

        number.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.length == 11) Utils.hideKeyboard(act)
            }

            override fun afterTextChanged(s: Editable?) {
                if (s!!.length == 11) Utils.hideKeyboard(act)
            }
        })

        onClicks()

    }

    private fun onClicks() {
        verify.setOnClickListener {
            val nu = number.text.toString()
            val na = name.text.toString()

            if(!ruleCheck.isChecked) {
                Toast.makeText(ctx, R.string.registerfragment_rules_error, Toast.LENGTH_SHORT).show()
            }else if (nu.length < 11 || !nu.startsWith("09")) {
                Toast.makeText(ctx, R.string.general_number_error, Toast.LENGTH_SHORT).show()
            } else if (na.length < 6) {
                Toast.makeText(ctx, R.string.general_name_error, Toast.LENGTH_SHORT).show()
            } else {
                if (Utils.checkInternet(ctx)) {
                    Utils.hideKeyboard(act)
                    verify.isEnabled = false
                    verify.text = "..."
                    login.isEnabled = false
                    CoroutineScope(Dispatchers.IO).launch {
                        doRegister(na, nu)
                    }
                } else
                    Toast.makeText(ctx, R.string.general_internet_error, Toast.LENGTH_SHORT).show()


            }

        }

        login.setOnClickListener { view?.findNavController()!!.popBackStack() }


    }

    private fun showRulesDialog() {
        val dialog = RulesDialog(ctx)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.show()
        val window = dialog.window
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    private fun requestHint() {
        val hintRequest = HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build()
        val intent = Credentials.getClient(ctx).getHintPickerIntent(hintRequest)
        startIntentSenderForResult(intent.intentSender,
                resolveHint, null, 0, 0, 0, null)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == resolveHint) {
            if (resultCode == RESULT_OK) {
                val credential = data?.getParcelableExtra<Credential>(Credential.EXTRA_KEY)

                var n = credential!!.id
                if (n.startsWith("+98"))
                    n = "0" + n.substring(3)
                else if (n.startsWith("0098"))
                    n = "0" + n.substring(4)
                number.setText(n)

            }
        }
    }


    private suspend fun doRegister(name: String, number: String) {
        when (val res = ApiRepository(RemoteDataSource().getApi(NetworkApi::class.java)).registerUser(name, number)) {
            is Resource.Success -> {
                withContext(Dispatchers.Main) {
                    SmsRetriever.getClient(act).startSmsRetriever()
                    val b = Bundle()
                    b.putString("number", number)
                    // navController.navigate(R.id.action_registerFragment_to_verifyFragment)
                    view?.findNavController()!!.navigate(R.id.action_registerFragment_to_verifyFragment, b)

                }
            }
            is Resource.Failure -> {
                withContext(Dispatchers.Main) {
                    verify.isEnabled = true
                    verify.setText(R.string.loginfragment_verify)
                    login.isEnabled = true
                    if (res.isNetworkError) {
                        Toast.makeText(ctx, R.string.general_internet_error, Toast.LENGTH_SHORT).show()
                    } else {
                        when (res.errorCode) {
                            409 -> Toast.makeText(ctx, R.string.registerfragment_conflict, Toast.LENGTH_LONG).show()
                            else -> Toast.makeText(ctx, R.string.general_error, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

}