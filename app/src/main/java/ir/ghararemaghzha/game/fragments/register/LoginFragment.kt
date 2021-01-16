package ir.ghararemaghzha.game.fragments.register

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
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
import ir.ghararemaghzha.game.databinding.FragmentLoginBinding
import ir.ghararemaghzha.game.fragments.BaseFragment
import ir.ghararemaghzha.game.viewmodels.LoginViewModel

class LoginFragment : BaseFragment<LoginViewModel, FragmentLoginBinding>() {

    private val resolveHint = 521
    private lateinit var number:String

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()

        viewModel.loginResponse.observe(viewLifecycleOwner, { res->
            when(res){
                is Resource.Success -> {
                        SmsRetriever.getClient(requireActivity()).startSmsRetriever()
                        val b = Bundle()
                        b.putString("number", number)
                        requireView().findNavController().navigate(R.id.action_loginFragment_to_verifyFragment,b)
                }
                is Resource.Failure -> {
                        b.loginVerify.isEnabled = true
                        b.loginVerify.setText(R.string.loginfragment_verify)
                        b.loginRegister.isEnabled = true
                        if (res.isNetworkError) {
                            Toast.makeText(context, R.string.general_internet_error, Toast.LENGTH_SHORT).show()
                        } else {
                            when (res.errorCode) {
                                401 -> {
                                    Toast.makeText(context, R.string.loginfragment_nouser, Toast.LENGTH_SHORT).show()
                                    val b = Bundle()
                                    b.putString("number", number)
                                    view?.findNavController()?.navigate(R.id.action_loginFragment_to_registerFragment,b)

                                }
                                403 -> Toast.makeText(context, R.string.loginfragment_blocked, Toast.LENGTH_LONG).show()
                                else -> Toast.makeText(context, R.string.general_error, Toast.LENGTH_SHORT).show()
                            }
                        }
                }
                Resource.Loading -> {}
            }
        })
    }

    private fun init() {
        b.loginNumber.doOnTextChanged { s, _, _, _ ->  if (s?.length == 11) Utils.hideKeyboard(requireActivity())  }
        requestHint()
        onClicks()
    }

    private fun onClicks() {
        b.loginVerify.setOnClickListener {
            number = b.loginNumber.text.toString()
            if (number.length < 11 || !number.startsWith("09")) {
                Toast.makeText(context, R.string.general_number_error, Toast.LENGTH_SHORT).show()
            } else {
                if (Utils.checkInternet(requireContext())) {
                    b.loginVerify.isEnabled = false
                    b.loginVerify.text = "..."
                    b.loginRegister.isEnabled = false
                    Utils.hideKeyboard(requireActivity())
                    viewModel.login(number)
                }else
                    Toast.makeText(context, R.string.general_internet_error, Toast.LENGTH_SHORT).show()
            }
        }

        b.loginRegister.setOnClickListener {
            requireView().findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
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
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == resolveHint) {
            if (resultCode == RESULT_OK) {
                val credential = data?.getParcelableExtra<Credential>(Credential.EXTRA_KEY)
                if(credential!=null) {
                    var n = credential.id
                    if (n.startsWith("+98"))
                        n = "0" + n.substring(3)
                    else if (n.startsWith("0098"))
                        n = "0" + n.substring(4)
                    b.loginNumber.setText(n)
                }
            }
        }
    }

    override fun getViewModel() = LoginViewModel::class.java

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentLoginBinding.inflate(inflater, container, false)

}