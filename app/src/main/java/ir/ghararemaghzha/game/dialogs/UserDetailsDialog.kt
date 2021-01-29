package ir.ghararemaghzha.game.dialogs

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.classes.MySharedPreference
import ir.ghararemaghzha.game.classes.Utils
import ir.ghararemaghzha.game.data.ApiRepository
import ir.ghararemaghzha.game.data.NetworkApi
import ir.ghararemaghzha.game.data.RemoteDataSource
import ir.ghararemaghzha.game.data.Resource
import ir.ghararemaghzha.game.databinding.DialogUserdetailsBinding
import ir.ghararemaghzha.game.viewmodels.UserDetailsViewModel
import ir.ghararemaghzha.game.viewmodels.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserDetailsDialog(ctx: FragmentActivity, private val userId: String) : Dialog(ctx) {

    private lateinit var b: DialogUserdetailsBinding
    private lateinit var number: String
    private lateinit var token: String
    private lateinit var viewModel: UserDetailsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = DialogUserdetailsBinding.inflate(LayoutInflater.from(context))
        val factory = ViewModelFactory(ApiRepository(RemoteDataSource().getApi(NetworkApi::class.java)))
        //  viewModel = ViewModelProvider(ctx, factory).get(UserDetailsViewModel::class.java)
        viewModel = factory.create(UserDetailsViewModel::class.java)
        setContentView(b.root)

        init()
    }


    private fun init() {
        getUserDetails()
        b.detailsRate.rating = 0f
        b.detailsRate.isClickable = false
        b.detailsRate.isScrollable = false
        b.detailsRate.starPadding = 8
        b.detailsAnswersProgress.progress = 0
        b.detailsAnswersProgress.max = 100
        b.detailsQuestionsProgress.progress = 0
        b.detailsQuestionsProgress.max = 100
        b.detailsClose.setOnClickListener { dismiss() }
        b.detailsLoading.visibility = View.VISIBLE

        //  viewModel.getUserDetails("Bearer $token", number, userId)
        CoroutineScope(Dispatchers.IO).launch {
            getData()
        }
    }

    override fun dismiss() {
        super.dismiss()
        b.detailsAnswersProgress.progress = 0
        b.detailsQuestionsProgress.progress = 0
    }

    private fun getUserDetails() {
        number = MySharedPreference.getInstance(context).getNumber()
        token = MySharedPreference.getInstance(context).getAccessToken()
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(context as Activity, true)
        }
    }

    private suspend fun getData() {
        when (val res = ApiRepository(RemoteDataSource().getApi(NetworkApi::class.java)).getUserDetails("Bearer $token", number, userId)) {
            is Resource.Success -> {
                if (res.value.result == "success" && res.value.message == "ok") {
                    withContext(Dispatchers.Main) {
                        b.detailsName2.text = res.value.userData.userName
                        b.detailsCode.text = context.getString(R.string.details_code, res.value.userData.userCode)
                        b.detailsRank2.text = res.value.userData.userRank

                        if (res.value.userData.scoreCount != "-1")
                            b.detailsRateText.text = res.value.userData.scoreCount
                        else
                            b.detailsRateText.text = "0"

                        if (res.value.booster.toInt() < 1000) b.detailsAnswers.text = context.getString(R.string.details_nitro2, res.value.booster) else b.detailsAnswers.text = context.getString(R.string.details_nitro2, "1000")

                        Glide.with(context)
                                .load(context.getString(R.string.avatar_url, res.value.userData.userAvatar))
                                .circleCrop()
                                .placeholder(R.drawable.placeholder)
                                .into(b.detailsAvatar)

                        val totalQuestions = 1000
                        val answeredQuestions: Int = res.value.correct + res.value.incorrect

                        val nitroUsed = if (res.value.booster.toInt() > 1000)
                            1000
                        else
                            res.value.booster.toInt()

                        if (answeredQuestions <= 1000) {
                            b.detailsQuestions.text = context.getString(R.string.details_questions, answeredQuestions, totalQuestions)
                        } else {
                            b.detailsQuestions.text = context.getString(R.string.details_questions, 1000, totalQuestions)
                        }

                        val qPercent: Int
                        val nPercent: Int
                        when {
                            answeredQuestions in 1..1000 -> {
                                qPercent = answeredQuestions * 100 / totalQuestions
                                nPercent = nitroUsed * 100 / 1000
                            }
                            answeredQuestions > 1000 -> {
                                qPercent = 100
                                nPercent = nitroUsed * 100 / 1000
                            }
                            else -> {
                                qPercent = 0
                                nPercent = 0
                            }
                        }

                        b.detailsAnswersProgress.progress = nPercent
                        b.detailsQuestionsProgress.progress = qPercent


                        b.detailsAnswersPercent.text = "%$nPercent"
                        b.detailsQuestionsPercent.text = "%$qPercent"

                        val rate = res.value.rank.toInt()
                        val level1 = res.value.level1.toInt()
                        val level2 = res.value.level2.toInt()
                        val level3 = res.value.level3.toInt()
                        val level4 = res.value.level4.toInt()
                        b.detailsTotal.text = rate.toString()
                        when {
                            rate < level1 -> b.detailsRate.rating = 0f
                            rate < level2 -> b.detailsRate.rating = 1f
                            rate < level3 -> b.detailsRate.rating = 2f
                            rate < level4 -> b.detailsRate.rating = 3f
                            else -> b.detailsRate.rating = 4f
                        }
                        b.detailsLoading.visibility = View.GONE
                    }

                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, context.getString(R.string.general_error), Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                }
            }
            is Resource.Failure -> {
                if (res.isNetworkError) {
                    withContext(Dispatchers.Main) {
                        b.detailsLoading.visibility = View.GONE
                        Toast.makeText(context, context.getString(R.string.general_error), Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                } else if (res.errorCode == 401) {
                    withContext(Dispatchers.Main) {
                        b.detailsLoading.visibility = View.GONE
                        Utils.logout(context as Activity, true)
                    }
                }
            }
            is Resource.Loading -> {
            }
        }
    }
}