package ir.ghararemaghzha.game.fragments.register

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.*
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.navigation.findNavController
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.auth.api.phone.SmsRetriever
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.classes.Utils
import ir.ghararemaghzha.game.data.Resource
import ir.ghararemaghzha.game.databinding.FragmentRegisterBinding
import ir.ghararemaghzha.game.dialogs.RulesDialog
import ir.ghararemaghzha.game.fragments.BaseFragment
import ir.ghararemaghzha.game.viewmodels.RegisterViewModel

class RegisterFragment : BaseFragment<RegisterViewModel, FragmentRegisterBinding>() {

    private val resolveHint = 521
    private  var loginNumber:String=""
    private lateinit var number: String
    private lateinit var name: String

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (arguments != null)
            loginNumber = requireArguments().getString("number", "")
        init()

        viewModel.registerResponse.observe(viewLifecycleOwner,{res->
            when(res){
                is Resource.Success -> {
                        SmsRetriever.getClient(requireActivity()).startSmsRetriever()
                        val b = Bundle()
                        b.putString("number", number)
                        requireView().findNavController().navigate(R.id.action_registerFragment_to_verifyFragment, b)
                }
                is Resource.Failure -> {

                        b.regVerify.isEnabled = true
                        b.regVerify.setText(R.string.loginfragment_verify)
                        b.regLogin.isEnabled = true
                        if (res.isNetworkError) {
                            Toast.makeText(context, R.string.general_internet_error, Toast.LENGTH_SHORT).show()
                        } else {
                            when (res.errorCode) {
                                409 -> Toast.makeText(context, R.string.registerfragment_conflict, Toast.LENGTH_LONG).show()
                                else -> Toast.makeText(context, R.string.general_error, Toast.LENGTH_SHORT).show()
                            }
                        }

                }
                is Resource.Loading->{}
            }
        })
    }

    private fun init() {
        val tradeMarkText = getString(R.string.registerfragment_rules)
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
        b.regRulesText.text = spannableString
        b.regRulesText.movementMethod = LinkMovementMethod.getInstance()

        if (loginNumber.isEmpty())
            requestHint()
        else
            b.regNumber.setText(loginNumber)

        b.regNumber.doOnTextChanged { s, _, _, _ -> if (s?.length == 11) Utils.hideKeyboard(requireActivity()) }

        onClicks()
    }

    private fun onClicks() {
        b.regVerify.setOnClickListener {
            number =  b.regNumber.text.toString()
            name = b.regName.text.toString()

            if (!b.regRulesCheck.isChecked) {
                Toast.makeText(context, R.string.registerfragment_rules_error, Toast.LENGTH_SHORT).show()
            } else if (number.length < 11 || !number.startsWith("09")) {
                Toast.makeText(context, R.string.general_number_error, Toast.LENGTH_SHORT).show()
            } else if (name.length < 6) {
                Toast.makeText(context, R.string.general_name_error, Toast.LENGTH_SHORT).show()
            } else {
                if (Utils.checkInternet(requireContext())) {
                    Utils.hideKeyboard(requireActivity())
                    b.regVerify.isEnabled = false
                    b.regVerify.text = "..."
                    b.regLogin.isEnabled = false
                    viewModel.register(name,number)
                } else
                    Toast.makeText(context, R.string.general_internet_error, Toast.LENGTH_SHORT).show()
            }
        }
        b.regLogin.setOnClickListener { requireView().findNavController().popBackStack() }
    }

    private fun showRulesDialog() {
        val dialog = RulesDialog(requireActivity())
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.show()
        val window = dialog.window
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    private fun requestHint() {
        val hintRequest = HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build()
        val intent = Credentials.getClient(requireContext()).getHintPickerIntent(hintRequest)
        startIntentSenderForResult(intent.intentSender,
                resolveHint, null, 0, 0, 0, null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == resolveHint) {
            if (resultCode == RESULT_OK) {
                val credential = data?.getParcelableExtra<Credential>(Credential.EXTRA_KEY)
                if (credential != null) {
                    var n = credential.id
                    if (n.startsWith("+98"))
                        n = "0" + n.substring(3)
                    else if (n.startsWith("0098"))
                        n = "0" + n.substring(4)
                    b.regNumber.setText(n)
                }
            }
        }
    }
    override fun getViewModel()=RegisterViewModel::class.java
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?)= FragmentRegisterBinding.inflate(inflater,container,false)
}