package ir.ghararemaghzha.game.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging
import io.realm.Realm
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.classes.Const.FCM_TOPIC
import ir.ghararemaghzha.game.classes.MySharedPreference
import ir.ghararemaghzha.game.classes.Utils
import ir.ghararemaghzha.game.classes.Utils.convertToTimeFormat
import ir.ghararemaghzha.game.data.ApiRepository
import ir.ghararemaghzha.game.data.NetworkApi
import ir.ghararemaghzha.game.data.RemoteDataSource
import ir.ghararemaghzha.game.data.Resource
import ir.ghararemaghzha.game.dialogs.GetDataDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VerifyFragment : Fragment(R.layout.fragment_verify) {
    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    private lateinit var ctx: Context
    private lateinit var act: FragmentActivity
    private lateinit var resend: MaterialTextView
    private lateinit var code: TextInputEditText
    private lateinit var verify: MaterialButton
    private lateinit var timer: CountDownTimer
    private var timerTime = 120000L

    private lateinit var userName: String
    private lateinit var accessToken: String
    private var number: String = ""
    private lateinit var userId: String

    private lateinit var dialog: GetDataDialog


    private val rec = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val c = intent!!.extras?.getString("code")
            if (c?.length == 6)
                code.setText(c)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        ctx = requireContext()
        act = requireActivity()
        if (arguments != null)
            number = requireArguments().getString("number", "")
        ctx.registerReceiver(rec, IntentFilter("codeReceived"))
        init(view)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }


    private fun init(v: View) {
        resend = v.findViewById(R.id.verify_resend)
        resend.isEnabled = false
        code = v.findViewById(R.id.verify_code)
        verify = v.findViewById(R.id.verify_verify)
        SmsRetriever.getClient(act).startSmsUserConsent(null)
        initTimer()
        onClicks()

        code.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 6) Utils.hideKeyboard(act)
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 6) Utils.hideKeyboard(act)
            }
        })
    }

    private fun onClicks() {
        resend.setOnClickListener { CoroutineScope(Dispatchers.IO).launch { doResend() } }
        verify.setOnClickListener {
            val c = code.text.toString()
            if (c.length < 6 || c.startsWith("0")) {
                Toast.makeText(ctx, R.string.verify_wrong_code, Toast.LENGTH_SHORT).show()
            } else {
                if (Utils.checkInternet(ctx)) {
                    Utils.hideKeyboard(act)
                    verify.isEnabled = false
                    verify.text = "..."
                    CoroutineScope(Dispatchers.IO).launch {
                        doVerify(c)
                    }
                } else
                    Toast.makeText(ctx, R.string.general_internet_error, Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun initTimer() {
        timer = object : CountDownTimer(timerTime, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                resend.text = convertToTimeFormat(millisUntilFinished)

            }

            override fun onFinish() {
                resend.isEnabled = true
                resend.setText(R.string.verify_resend)

            }
        }
        timer.start()
    }

    private suspend fun doResend() {
        when (val res = ApiRepository(RemoteDataSource().getApi(NetworkApi::class.java))
                .resend(number)) {
            is Resource.Success -> {
                withContext(Dispatchers.Main) {
                    Toast.makeText(ctx, R.string.general_send, Toast.LENGTH_SHORT).show()
                    timerTime *= 2
                    timer.cancel()
                    initTimer()
                }
            }
            is Resource.Failure -> {
                if (res.isNetworkError) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(ctx, R.string.general_internet_error, Toast.LENGTH_SHORT).show()
                    }

                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(ctx, R.string.general_error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private suspend fun doVerify(code: String) {
        val fbToken = Utils.getFbToken(ctx)
        when (val res = ApiRepository(RemoteDataSource().getApi(NetworkApi::class.java)).verification(number, code, fbToken)) {
            is Resource.Success -> {
                FirebaseMessaging.getInstance().subscribeToTopic(FCM_TOPIC)

                userId = res.value.userId
                userName = res.value.userName
                accessToken = res.value.token
                val userCode = res.value.userCode
                val score = res.value.userScore
                val plan = res.value.userPlan

                MySharedPreference.getInstance(ctx).number = number
                MySharedPreference.getInstance(ctx).username = userName
                MySharedPreference.getInstance(ctx).accessToken = accessToken
                MySharedPreference.getInstance(ctx).userCode = userCode
                MySharedPreference.getInstance(ctx).score = score
                MySharedPreference.getInstance(ctx).plan = plan

                MySharedPreference.getInstance(ctx).userSex = res.value.userSex
                MySharedPreference.getInstance(ctx).userBday = res.value.userBday
                MySharedPreference.getInstance(ctx).userEmail = res.value.userEmail
                MySharedPreference.getInstance(ctx).userInvite = res.value.userInvite
                if (!res.value.userAvatar.isNullOrEmpty())
                    MySharedPreference.getInstance(ctx).userAvatar = res.value.userAvatar
                withContext(Dispatchers.Main) {
                    dialog = Utils.showGetDataLoading(ctx)
                }
                getQuestions()


            }
            is Resource.Failure -> {
                withContext(Dispatchers.Main) {
                    verify.isEnabled = true
                    verify.setText(R.string.verify_verify)
                    if (res.isNetworkError) {
                        Toast.makeText(ctx, R.string.general_internet_error, Toast.LENGTH_SHORT).show()
                    } else {
                        when (res.errorCode) {
                            401 -> {
                                Toast.makeText(ctx, R.string.verify_wrong_code, Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                Toast.makeText(ctx, R.string.general_error, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun getQuestions() {
        when (val res = ApiRepository(RemoteDataSource().getApi(NetworkApi::class.java)).getQuestions("Bearer $accessToken", number, "0", "0")) {

            is Resource.Success -> {
                withContext(Dispatchers.Main) {
                    val db = Realm.getDefaultInstance()
                    verify.isEnabled = true
                    verify.setText(R.string.verify_verify)
                    dialog.dismiss()
                    if (res.value.message != "empty") {
                        for (model in res.value.data) {
                            model.isUploaded = model.userAnswer != "-1"
                            model.isVisible = false
                            db.executeTransaction { it.insertOrUpdate(model) }
                        }
                        MySharedPreference.getInstance(ctx).userId = userId
                        Toast.makeText(ctx, ctx.getString(R.string.verify_welcome, userName), Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        logEvent()
                        view?.findNavController()!!.navigate(R.id.action_verifyFragment_to_mainActivity)
                        act.finish()


                    } else {
                        Toast.makeText(ctx, R.string.general_error, Toast.LENGTH_SHORT).show()
                    }

                }


            }
            is Resource.Failure -> {
                withContext(Dispatchers.Main) {
                    verify.isEnabled = true
                    verify.setText(R.string.verify_verify)
                    dialog.dismiss()
                    if (res.isNetworkError) {
                        Toast.makeText(ctx, R.string.general_internet_error, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(ctx, R.string.general_error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ctx.unregisterReceiver(rec)
    }

    private fun logEvent() {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, userId)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, userName)
        mFirebaseAnalytics?.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
    }
}