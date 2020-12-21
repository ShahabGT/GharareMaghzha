package ir.ghararemaghzha.game.fragments

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragment : Fragment(R.layout.fragment_login) {

    private val resolveHint = 521

    private lateinit var ctx: Context
    private lateinit var act: FragmentActivity
    private lateinit var register: MaterialTextView
    private lateinit var number: TextInputEditText
    private lateinit var verify: MaterialButton


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ctx = requireContext()
        act = requireActivity()
        init(view)
    }

    private fun init(v: View) {
        register = v.findViewById(R.id.login_register)
        number = v.findViewById(R.id.login_number)
        verify = v.findViewById(R.id.login_verify)

        number.doOnTextChanged { s, _, _, _ ->  if (s?.length == 11) Utils.hideKeyboard(act)  }
        number.doAfterTextChanged { if (it?.length == 11) Utils.hideKeyboard(act) }

        requestHint()
        onClicks()
    }

    private fun onClicks() {
        verify.setOnClickListener {
            val n = number.text.toString()
            if (n.length < 11 || !n.startsWith("09")) {
                Toast.makeText(ctx, R.string.general_number_error, Toast.LENGTH_SHORT).show()
            } else {
                if (Utils.checkInternet(ctx)) {
                    verify.isEnabled = false
                    verify.text = "..."
                    register.isEnabled = false
                    Utils.hideKeyboard(act)
                    CoroutineScope(Dispatchers.IO).launch {
                        doLogin(n)
                    }
                }else
                    Toast.makeText(ctx, R.string.general_internet_error, Toast.LENGTH_SHORT).show()
            }
        }

        register.setOnClickListener {
            view?.findNavController()!!.navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private suspend fun doLogin(number: String) {
        when (val res = ApiRepository(RemoteDataSource().getApi(NetworkApi::class.java)).login(number)) {
            is Resource.Success -> {
                withContext(Dispatchers.Main) {
                    SmsRetriever.getClient(act).startSmsRetriever()
                    val b = Bundle()
                    b.putString("number", number)
                    view?.findNavController()?.navigate(R.id.action_loginFragment_to_verifyFragment,b)
                }
            }
            is Resource.Failure -> {
                withContext(Dispatchers.Main) {
                    verify.isEnabled = true
                    verify.setText(R.string.loginfragment_verify)
                    register.isEnabled = true
                    if (res.isNetworkError) {
                        Toast.makeText(ctx, R.string.general_internet_error, Toast.LENGTH_SHORT).show()
                    } else {
                        when (res.errorCode) {
                            401 -> {
                                Toast.makeText(ctx, R.string.loginfragment_nouser, Toast.LENGTH_SHORT).show()
                                val b = Bundle()
                                b.putString("number", number)
                                view?.findNavController()?.navigate(R.id.action_loginFragment_to_registerFragment,b)

                            }
                            403 -> Toast.makeText(ctx, R.string.loginfragment_blocked, Toast.LENGTH_LONG).show()
                            else -> Toast.makeText(ctx, R.string.general_error, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
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
                    number.setText(n)
                }
            }
        }
    }
}