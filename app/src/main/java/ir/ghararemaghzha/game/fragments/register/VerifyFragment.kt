package ir.ghararemaghzha.game.fragments.register

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging
import io.realm.Realm
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.activities.SlidesActivity
import ir.ghararemaghzha.game.classes.Const.FCM_TOPIC
import ir.ghararemaghzha.game.classes.MySharedPreference
import ir.ghararemaghzha.game.classes.Utils
import ir.ghararemaghzha.game.data.Resource
import ir.ghararemaghzha.game.databinding.FragmentVerifyBinding
import ir.ghararemaghzha.game.dialogs.GetDataDialog
import ir.ghararemaghzha.game.fragments.BaseFragment
import ir.ghararemaghzha.game.models.QuestionModel
import ir.ghararemaghzha.game.viewmodels.VerifyViewModel

class VerifyFragment : BaseFragment<VerifyViewModel, FragmentVerifyBinding>() {
    private lateinit var mFirebaseAnalytics: FirebaseAnalytics
    private lateinit var timer: CountDownTimer
    private var timerTime = 120000L
    private lateinit var userName: String
    private lateinit var accessToken: String
    private var number: String = ""
    private lateinit var userId: String
    private lateinit var dialog: GetDataDialog

    private val rec = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val c = intent?.extras?.getString("code")
            if (c?.length == 6) {
                b.verifyCode.setText(c)
                if (Utils.checkInternet(requireContext())) {
                    b.verifyVerify.isEnabled = false
                    b.verifyVerify.text = "..."
                    Utils.hideKeyboard(requireActivity())
                    viewModel.verify(number, c, Utils.getFbToken(requireContext()))
                } else
                    Toast.makeText(context, R.string.general_internet_error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        })

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        if (arguments != null)
            number = requireArguments().getString("number", "")

        requireContext().registerReceiver(rec, IntentFilter("codeReceived"))

        init()
        responses()
    }

    private fun responses() {
        viewModel.resendResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is Resource.Success -> {
                    Toast.makeText(context, R.string.general_send, Toast.LENGTH_SHORT).show()
                    timerTime *= 2
                    timer.cancel()
                    initTimer()
                }
                is Resource.Failure -> {
                    if (res.isNetworkError) {
                        Toast.makeText(context, R.string.general_internet_error, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, R.string.general_error, Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> {
                }
            }
        })

        viewModel.verificationResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is Resource.Success -> {
                    FirebaseMessaging.getInstance().subscribeToTopic(FCM_TOPIC)
                    userId = res.value.userId
                    userName = res.value.userName
                    accessToken = res.value.token
                    val userCode = res.value.userCode
                    val score = res.value.userScore.toInt()
                    MySharedPreference.getInstance(requireContext()).also {
                        it.setNumber(number)
                        it.setUsername(userName)
                        it.setAccessToken(accessToken)
                        it.setUserCode(userCode)
                        it.setScore(score)

                        it.setUserSex(res.value.userSex ?: "")
                        it.setUserBirthday(res.value.userBday ?: "")
                        it.setUserEmail(res.value.userEmail ?: "")
                        it.setUserInvite(res.value.userInvite ?: "")
                        if (!res.value.userAvatar.isNullOrEmpty())
                            it.setUserAvatar(res.value.userAvatar)
                    }

                    dialog = Utils.showGetDataLoading(requireContext())
                    viewModel.getQuestions("Bearer $accessToken", number)


                }
                is Resource.Failure -> {

                    b.verifyVerify.isEnabled = true
                    b.verifyVerify.setText(R.string.verify_verify)
                    if (res.isNetworkError) {
                        Toast.makeText(context, R.string.general_internet_error, Toast.LENGTH_SHORT).show()
                    } else {
                        when (res.errorCode) {
                            401 -> {
                                Toast.makeText(context, R.string.verify_wrong_code, Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                Toast.makeText(context, R.string.general_error, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                is Resource.Loading -> {
                }
            }
        })

        viewModel.questionResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is Resource.Success -> {
                    if (res.value.message != "empty") {
                        val data: MutableCollection<QuestionModel> = mutableListOf()
                        for (model in res.value.data) {
                            model.uploaded = model.userAnswer != "-1"
                            data.add(model)
                        }
                        val db = Realm.getDefaultInstance()
                        db.executeTransaction { it.insertOrUpdate(data) }
                        MySharedPreference.getInstance(requireContext()).setUserId(userId)
                        Toast.makeText(context, getString(R.string.verify_welcome, userName), Toast.LENGTH_SHORT).show()
                        logEvent()
                        dialog.dismiss()
                        val intent = Intent(requireActivity(), SlidesActivity::class.java).also {
                            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                    } else {
                        b.verifyVerify.isEnabled = true
                        b.verifyVerify.setText(R.string.verify_verify)
                        dialog.dismiss()
                        Toast.makeText(context, R.string.general_error, Toast.LENGTH_SHORT).show()
                    }

                }
                is Resource.Failure -> {

                    b.verifyVerify.isEnabled = true
                    b.verifyVerify.setText(R.string.verify_verify)
                    dialog.dismiss()
                    if (res.isNetworkError)
                        Toast.makeText(context, R.string.general_internet_error, Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(context, R.string.general_error, Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                }
            }
        })
    }

    private fun init() {
        b.verifyResend.isEnabled = false

        SmsRetriever.getClient(requireActivity()).startSmsUserConsent(null)
        initTimer()
        onClicks()

        b.verifyCode.doOnTextChanged { s, _, _, _ -> if (s?.length == 6) Utils.hideKeyboard(requireActivity()) }
    }

    private fun onClicks() {
        b.verifyResend.setOnClickListener { viewModel.resend(number) }
        b.verifyVerify.setOnClickListener {
            val c = b.verifyCode.text.toString()
            if (c.length < 6 || c.startsWith("0")) {
                Toast.makeText(context, R.string.verify_wrong_code, Toast.LENGTH_SHORT).show()
            } else {
                if (Utils.checkInternet(requireContext())) {
                    Utils.hideKeyboard(requireActivity())
                    b.verifyVerify.isEnabled = false
                    b.verifyVerify.text = "..."
                    viewModel.verify(number, c, Utils.getFbToken(requireContext()))
                } else
                    Toast.makeText(context, R.string.general_internet_error, Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun initTimer() {
        timer = object : CountDownTimer(timerTime, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                b.verifyResend.text = Utils.convertToTimeFormat(millisUntilFinished)
            }

            override fun onFinish() {
                b.verifyResend.isEnabled = true
                b.verifyResend.setText(R.string.verify_resend)
            }
        }
        timer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        requireContext().unregisterReceiver(rec)
    }

    private fun logEvent() {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, userId)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, userName)
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
    }

    override fun getViewModel() = VerifyViewModel::class.java

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentVerifyBinding.inflate(inflater, container, false)
}