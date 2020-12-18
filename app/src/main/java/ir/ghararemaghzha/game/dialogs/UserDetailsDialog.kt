package ir.ghararemaghzha.game.dialogs

import android.app.Activity
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.google.android.material.textview.MaterialTextView
import com.willy.ratingbar.ScaleRatingBar
import ir.ghararemaghzha.game.R
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

class UserDetailsDialog(ctx: FragmentActivity, private val userId: String) : Dialog(ctx) {

    private lateinit var name: MaterialTextView
    private lateinit var code: MaterialTextView
    private lateinit var rank: MaterialTextView
    private lateinit var score: MaterialTextView
    private lateinit var questions: MaterialTextView
    private lateinit var answers: MaterialTextView
    private lateinit var questionsPercent: MaterialTextView
    private lateinit var answersPercent: MaterialTextView
    private lateinit var rateText: MaterialTextView
    private lateinit var answersProgress: ProgressBar
    private lateinit var questionsProgress: ProgressBar
    private lateinit var avatar: ImageView
    private lateinit var ratingBar: ScaleRatingBar
    private lateinit var loading: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_userdetails)
        init()

    }
    private fun init() {
        loading = findViewById(R.id.details_loading)
        ratingBar = findViewById(R.id.details_rate)
        ratingBar.rating = 0f
        ratingBar.isClickable = false
        ratingBar.isScrollable = false
        ratingBar.starPadding = 8
        rateText = findViewById(R.id.details_total)
        name = findViewById(R.id.details_name2)
        code = findViewById(R.id.details_code)
        rank = findViewById(R.id.details_rank2)
        score = findViewById(R.id.details_rate_text)
        questions = findViewById(R.id.details_questions)
        questionsPercent = findViewById(R.id.details_questions_percent)
        answers = findViewById(R.id.details_answers)
        answersPercent = findViewById(R.id.details_answers_percent)
        avatar = findViewById(R.id.details_avatar)
        answersProgress = findViewById(R.id.details_answers_progress)
        answersProgress.progress = 0
        answersProgress.max = 100
        questionsProgress = findViewById(R.id.details_questions_progress)
        questionsProgress.progress = 0
        questionsProgress.max = 100
        findViewById<ImageView>(R.id.details_close).setOnClickListener { dismiss() }
        loading.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            getData()
        }
    }

    private suspend fun getData() {
        val number = MySharedPreference.getInstance(context).getNumber()
        val token = MySharedPreference.getInstance(context).getAccessToken()
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(context as Activity, true)
            return
        }

        when (val res = ApiRepository(RemoteDataSource().getApi(NetworkApi::class.java)).getUserDetails("Bearer $token", number, userId)) {
            is Resource.Success -> {
                if (res.value.result == "success" && res.value.message == "ok") {
                    withContext(Dispatchers.Main) {
                        name.text = res.value.userData.userName
                        code.text = context.getString(R.string.details_code, res.value.userData.userCode)
                        rank.text = res.value.userData.userRank
                        score.text = res.value.userData.scoreCount
                        if (res.value.booster.toInt() < 1500) answers.text = context.getString(R.string.details_nitro2, res.value.booster) else answers.text = context.getString(R.string.details_nitro2, "1500")

                        Glide.with(context)
                                .load(context.getString(R.string.avatar_url, res.value.userData.userAvatar))
                                .circleCrop()
                                .placeholder(R.drawable.placeholder)
                                .into(avatar)

                        val totalQuestions: Int = res.value.plan.toInt() * 500 + 500
                        val answeredQuestions: Int = res.value.correct + res.value.incorrect
                        val nitroUsed: Int = res.value.booster.toInt()

                        if (answeredQuestions <= 3000) {
                            questions.text = context.getString(R.string.details_questions, answeredQuestions, totalQuestions)
                        } else {
                            questions.text = context.getString(R.string.details_questions, 3000, totalQuestions)
                        }

                        val qPercent: Int
                        val nPercent: Int
                        when {
                            answeredQuestions in 1..3000 -> {
                                qPercent = answeredQuestions * 100 / totalQuestions
                                nPercent = nitroUsed * 100 / 1500
                            }
                            answeredQuestions > 3000 -> {
                                qPercent = answeredQuestions * 100 / totalQuestions
                                nPercent = nitroUsed * 100 / 1500
                            }
                            else -> {
                                qPercent = 0
                                nPercent = 0
                            }
                        }

                        Handler(Looper.getMainLooper()).postDelayed({
                            if (Build.VERSION.SDK_INT >= 24) {
                                answersProgress.setProgress(nPercent, true)
                                questionsProgress.setProgress(qPercent, true)
                            } else {
                                answersProgress.progress = nPercent
                                questionsProgress.progress = qPercent
                            }
                        }, 500)

                        answersPercent.text = "%$nPercent"
                        questionsPercent.text = "%$qPercent"

                        val rate = res.value.rank.toInt()
                        val level1 = res.value.level1.toInt()
                        val level2 = res.value.level2.toInt()
                        val level3 = res.value.level3.toInt()
                        val level4 = res.value.level4.toInt()
                        rateText.text = rate.toString()
                        when {
                            rate < level1 -> ratingBar.rating = 0f
                            rate < level2 -> ratingBar.rating = 1f
                            rate < level3 -> ratingBar.rating = 2f
                            rate < level4 -> ratingBar.rating = 3f
                            else -> ratingBar.rating = 4f
                        }
                        loading.visibility = View.GONE
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
                        loading.visibility = View.GONE
                        Toast.makeText(context, context.getString(R.string.general_error), Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                } else if (res.errorCode == 401) {
                    withContext(Dispatchers.Main) {
                        loading.visibility = View.GONE
                        Utils.logout(context as Activity, true)
                    }
                }
            }
        }
    }
}