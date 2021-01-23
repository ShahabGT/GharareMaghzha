package ir.ghararemaghzha.game.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.*
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import io.realm.Realm
import io.realm.RealmResults
import io.realm.kotlin.where
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.classes.Const.GHARAREHMAGHZHA_BROADCAST_END
import ir.ghararemaghzha.game.classes.MySettingsPreference
import ir.ghararemaghzha.game.classes.MySharedPreference
import ir.ghararemaghzha.game.classes.Utils
import ir.ghararemaghzha.game.data.ApiRepository
import ir.ghararemaghzha.game.data.NetworkApi
import ir.ghararemaghzha.game.data.RemoteDataSource
import ir.ghararemaghzha.game.data.Resource
import ir.ghararemaghzha.game.databinding.ActivityQuestionBinding
import ir.ghararemaghzha.game.models.QuestionModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class QuestionsActivity : AppCompatActivity() {

    private lateinit var b: ActivityQuestionBinding
    private var progress: Double = 100.0
    private var time = 15
    private lateinit var downTimer: CountDownTimer
    private lateinit var data: RealmResults<QuestionModel>
    private lateinit var randomAnswers: List<Int>
    private var correctAnswer = ""
    private lateinit var model: QuestionModel
    private lateinit var db: Realm
    private lateinit var soundPool: SoundPool
    private lateinit var mediaPlayer: MediaPlayer
    private var correctSound = 0
    private var wrongSound: Int = 0
    private var gameScore = 0
    private var shouldRandomize = false
    private var musicSetting = false
    private var hasBooster = false
    private var foreground = false
    private lateinit var number: String
    private lateinit var token: String
    private var season: Int = 0
    private lateinit var nextQuestionHandler: Handler
    private lateinit var nextQuestionRunnable: Runnable
    private val br: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Toast.makeText(this@QuestionsActivity, R.string.general_end, Toast.LENGTH_LONG).show()
            onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityQuestionBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val window = window
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        else
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(b.root)

        if (MySharedPreference.getInstance(this).isFirstTimeQuestion())
            helpInfo()
        else
            init()
    }

    private fun helpInfo() {
        TapTargetSequence(this)
                .targets(
                        TapTarget.forView(b.questionScoreCard, getString(R.string.tap_target_question_score_title), getString(R.string.tap_target_question_score_des))
                                .cancelable(false)
                                .tintTarget(false)
                                .dimColor(R.color.black)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(R.color.white)
                                .textColor(android.R.color.black),
                        TapTarget.forView(b.questionMusic, getString(R.string.tap_target_question_music_title), getString(R.string.tap_target_question_music_des))
                                .cancelable(false)
                                .dimColor(R.color.black)
                                .tintTarget(false)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(R.color.white)
                                .textColor(android.R.color.black),
                        TapTarget.forView(b.questionBooster, getString(R.string.tap_target_question_booster_title), getString(R.string.tap_target_question_booster_des))
                                .cancelable(false)
                                .tintTarget(false)
                                .dimColor(R.color.black)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(R.color.white)
                                .textColor(android.R.color.black),
                        TapTarget.forView(b.questionProgressText, getString(R.string.tap_target_question_time_title), getString(R.string.tap_target_question_time_des))
                                .cancelable(false)
                                .tintTarget(false)
                                .dimColor(R.color.black)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(R.color.white)
                                .textColor(android.R.color.black),
                        TapTarget.forView(b.questionPoints, getString(R.string.tap_target_question_point_title), getString(R.string.tap_target_question_point_des))
                                .cancelable(false)
                                .tintTarget(false)
                                .dimColor(R.color.black)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(R.color.white)
                                .textColor(android.R.color.black),
                        TapTarget.forView(b.questionRemaining, getString(R.string.tap_target_question_question_title), getString(R.string.tap_target_question_question_des))
                                .cancelable(false)
                                .tintTarget(false)
                                .dimColor(R.color.black)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(R.color.white)
                                .textColor(android.R.color.black),
                        TapTarget.forView(b.questionReport, getString(R.string.tap_target_question_next_report_title), getString(R.string.tap_target_question_next_report_des))
                                .cancelable(false)
                                .tintTarget(false)
                                .dimColor(R.color.black)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(R.color.white)
                                .textColor(android.R.color.black)
                ).listener(object : TapTargetSequence.Listener {
                    override fun onSequenceFinish() {
                        MySharedPreference.getInstance(this@QuestionsActivity).setFirstTimeQuestion()
                        init()
                    }

                    override fun onSequenceStep(lastTarget: TapTarget, targetClicked: Boolean) {}
                    override fun onSequenceCanceled(lastTarget: TapTarget) {}
                }).start()
    }

    private fun getUserDetails() {
        number = MySharedPreference.getInstance(this).getNumber()
        token = MySharedPreference.getInstance(this).getAccessToken()
        season = MySharedPreference.getInstance(this).getSeason()
        if (number.isEmpty() || token.isEmpty() || season == 0) {
            Utils.logout(this, true)
        }
    }

    private fun init() {
        nextQuestionHandler = Handler(Looper.getMainLooper())
        nextQuestionRunnable = Runnable { nextQuestion() }
        getUserDetails()
        db = Realm.getDefaultInstance()
        data = db.where(QuestionModel::class.java).equalTo("userAnswer", "-1").findAll()
        musicSetting = MySettingsPreference.getInstance(this).getMusic()
        b.questionMusic.setImageResource(if (musicSetting) R.drawable.vector_music_on else R.drawable.vector_music_off)
        gameScore = MySharedPreference.getInstance(this).getScore()
        b.questionScore.text = gameScore.toString()
        b.questionProgressBar.progress = progress.toInt()
        downTimer = object : CountDownTimer(15000, 1000) {
            override fun onTick(l: Long) {
                time -= 1
                progress -= 6.6
                b.questionProgressText.text = time.toString()
                b.questionProgressBar.progress = progress.toInt()
                if (l < 6000) setViewTextColor(b.questionProgressText, R.color.random1)
            }

            override fun onFinish() {
                b.questionProgressText.text = 0.toString()
                b.questionProgressBar.progress = 0
                b.questionAnswer1Card.isEnabled = false
                b.questionAnswer2Card.isEnabled = false
                b.questionAnswer3Card.isEnabled = false
                b.questionAnswer4Card.isEnabled = false
                if (foreground) nextQuestion()
            }
        }
        onClicks()
        nextQuestion()
        val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        soundPool = SoundPool.Builder().setMaxStreams(2).setAudioAttributes(attributes).build()
        correctSound = soundPool.load(this, R.raw.correct, 1)
        wrongSound = soundPool.load(this, R.raw.wrong, 1)
        mediaPlayer = MediaPlayer.create(this, R.raw.game)
        mediaPlayer.isLooping = true

        b.questionReport.setOnClickListener {
            val qId = model.questionId
            CoroutineScope(Dispatchers.IO).launch {
                report(qId)
            }
        }
    }

    private fun playSound(sound: Int) {
        soundPool.play(sound, 1f, 1f, 2, 0, 1f)
    }

    private fun onClicks() {
        b.questionMusic.setOnClickListener {
            musicSetting = !musicSetting
            b.questionMusic.setImageResource(if (musicSetting) R.drawable.vector_music_on else R.drawable.vector_music_off)
            if (musicSetting) {
                mediaPlayer = MediaPlayer.create(this, R.raw.game)
                mediaPlayer.isLooping = true
                mediaPlayer.start()
            } else {
                mediaPlayer.pause()
                mediaPlayer.stop()
            }
        }
        b.questionAnswer1Card.setOnClickListener {
            answer(0, b.questionAnswer1, b.questionAnswer1Card)
        }
        b.questionAnswer2Card.setOnClickListener {
            answer(1, b.questionAnswer2, b.questionAnswer2Card)
        }
        b.questionAnswer3Card.setOnClickListener {
            answer(2,  b.questionAnswer3, b.questionAnswer3Card)
        }
        b.questionAnswer4Card.setOnClickListener {
            answer(3,  b.questionAnswer4, b.questionAnswer4Card)
        }
        b.questionClose.setOnClickListener { this@QuestionsActivity.finish() }
    }

    private fun answer(which: Int, button: MaterialTextView, buttonCard: MaterialCardView) {
        if (MySharedPreference.getInstance(this).getBoosterValue() != 1f) MySharedPreference.getInstance(this@QuestionsActivity).counterIncrease()
        downTimer.cancel()
        b.questionProgressText.text = 0.toString()
        b.questionProgressBar.progress = 0
        b.questionAnswer1Card.isEnabled = false
        b.questionAnswer2Card.isEnabled = false
        b.questionAnswer3Card.isEnabled = false
        b.questionAnswer4Card.isEnabled = false
        val answerId: String = if (shouldRandomize) (randomAnswers[which] + 1).toString() else which.toString()
        setAnswer(answerId)
        val qId = model.questionId
        CoroutineScope(Dispatchers.IO).launch {
            uploadAnswer(answerId, qId)
        }
        if (button.text.toString() == correctAnswer) {
            setViewColor(buttonCard, R.color.green)
            YoYo.with(Techniques.Tada).duration(500).playOn(buttonCard)
            playSound(correctSound)
            val qPoint = model.questionPoints.toInt()
            YoYo.with(Techniques.Bounce).duration(500).playOn(b.questionScore)
            gameScore += (qPoint * MySharedPreference.getInstance(this).getBoosterValue()).toInt()
            b.questionScore.text = gameScore.toString()
            MySharedPreference.getInstance(this@QuestionsActivity).setScore(gameScore)
            CoroutineScope(Dispatchers.IO).launch {
                uploadScore(gameScore.toString())
            }
        } else {
            setViewColor(buttonCard, R.color.red)
            YoYo.with(Techniques.Shake).duration(500).playOn(buttonCard)
            playSound(wrongSound)
        }
        nextQuestionHandler.postDelayed({ nextQuestionRunnable }, 2000)
    }

    private fun enterAnimations() {
        YoYo.with(Techniques.Landing).duration(1500).playOn(b.questionQuestionCard)
        YoYo.with(Techniques.Landing).duration(1500).playOn(b.questionAnswer1Card)
        YoYo.with(Techniques.Landing).duration(1500).playOn(b.questionAnswer2Card)
        YoYo.with(Techniques.Landing).duration(1500).playOn(b.questionAnswer3Card)
        YoYo.with(Techniques.Landing).duration(1500).playOn(b.questionAnswer4Card)
    }

    private fun nextQuestion() {
        if (data.isEmpty()) {
            Utils.createNotification(this, getString(R.string.questions_notification_title), getString(R.string.questions_notification_body), "ir.ghararemaghzha.game.TARGET_NOTIFICATION")
            Toast.makeText(this, getString(R.string.questions_notification_title), Toast.LENGTH_SHORT).show()
            onBackPressed()
            return
        }
        if (Utils.checkInternet(this)) {
            if (MySharedPreference.getInstance(this).getBoosterValue() == 1f) {
                b.questionBooster.visibility = View.GONE
                hasBooster = false
            } else {
                hasBooster = true
            }
            b.questionRemaining.text = getString(R.string.question_remaining, (data.size - 1).toString())
            model = getRandom()
            downTimer.cancel()
            enterAnimations()
            b.questionAnswer1Card.isEnabled = true
            setViewColor(b.questionAnswer1Card, R.color.white)
            b.questionAnswer2Card.isEnabled = true
            setViewColor(b.questionAnswer2Card, R.color.white)
            b.questionAnswer3Card.isEnabled = true
            setViewColor(b.questionAnswer3Card, R.color.white)
            b.questionAnswer4Card.isEnabled = true
            setViewColor(b.questionAnswer4Card, R.color.white)
            time = 15
            progress = 100.0
            b.questionProgressText.text = time.toString()
            setViewTextColor(b.questionProgressText, R.color.black)
            b.questionProgressBar.progress = 100
            randomAnswers = randomNumbers()
            b.questionQuestion.text = model.questionText
            val answers = listOf(model.questionA1, model.questionA2, model.questionA3, model.questionA4)
            correctAnswer = answers[model.questionCorrect.toInt() - 1]
            shouldRandomize = true
            for (s in answers) {
                if (s.contains("گزینه ") || s.contains("هیچکدام") || s.contains("همه موارد")) {
                    shouldRandomize = false
                    break
                }
            }
            if (shouldRandomize) {
                b.questionAnswer1.text = answers[randomAnswers[0]]
                b.questionAnswer2.text = answers[randomAnswers[1]]
                b.questionAnswer3.text = answers[randomAnswers[2]]
                b.questionAnswer4.text = answers[randomAnswers[3]]
            } else {
                b.questionAnswer1.text = answers[0]
                b.questionAnswer2.text = answers[1]
                b.questionAnswer3.text = answers[2]
                b.questionAnswer4.text = answers[3]
            }
            b.questionPoints.text = getString(R.string.question_points, model.questionPoints)
            downTimer.start()
            setAnswer("0")
            val qId = model.questionId
            CoroutineScope(Dispatchers.IO).launch {
                uploadAnswer("0", qId)
            }
        } else {
            Toast.makeText(this, getString(R.string.internet_error), Toast.LENGTH_SHORT).show()
            onBackPressed()
        }
    }

    @Suppress("deprecation")
    private fun setViewColor(v: MaterialCardView, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            v.setCardBackgroundColor(resources.getColor(color, null))
        else
            v.setCardBackgroundColor(resources.getColor(color))
    }

    @Suppress("deprecation")
    private fun setViewTextColor(v: MaterialTextView, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            v.setTextColor(resources.getColor(color, null))
        else
            v.setTextColor(resources.getColor(color))
    }

    private fun setAnswer(userAnswer: String) {
        val b =  if (hasBooster) "1" else "0"
        db.executeTransaction {
            val result = it.where<QuestionModel>().equalTo("questionId", model.questionId).findFirst()
            result?.userAnswer = userAnswer
            result?.userBooster = b
        }
    }

    private fun randomNumbers(): List<Int> {
        val numbers: MutableList<Int> = ArrayList()
        val rand = Random()
        while (numbers.size < 4) {
            val rnd = rand.nextInt(4)
            var isAvailable = false
            for (x in numbers) {
                if (rnd == x) {
                    isAvailable = true
                    break
                }
            }
            if (!isAvailable) numbers.add(rnd)
        }
        return numbers
    }

    private fun getRandom(): QuestionModel {
        val rand = Random()
        val currentQuestion = rand.nextInt(data.size)
        return data[currentQuestion] ?: QuestionModel()
    }

    override fun onPause() {
        super.onPause()
        if (musicSetting) mediaPlayer.pause()
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(br)
        foreground = false
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(br, IntentFilter(GHARAREHMAGHZHA_BROADCAST_END))
        foreground = true
        if (musicSetting) mediaPlayer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        nextQuestionHandler.removeCallbacks { nextQuestionRunnable }
        downTimer.cancel()
        soundPool.release()
        mediaPlayer.release()
    }

    private suspend fun report(questionId: String) {
        when (val res = ApiRepository(RemoteDataSource().getApi(NetworkApi::class.java)).report("Bearer $token", number, questionId)) {
            is Resource.Success -> {
                if (res.value.message == "submitted")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@QuestionsActivity, getString(R.string.general_save), Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private suspend fun uploadAnswer(userAnswer: String, questionId: String) {
        val b = if (hasBooster)  "1" else "0"

        when (val res = ApiRepository(RemoteDataSource().getApi(NetworkApi::class.java)).answerQuestion("Bearer $token", number, questionId, userAnswer, b, season)) {
            is Resource.Success -> {
                if (res.value.message == "success") {
                    withContext(Dispatchers.Main) {
                        db.executeTransaction {
                            val result = it.where<QuestionModel>().equalTo("questionId", model.questionId).findFirst()
                            result?.uploaded = true
                        }
                    }

                }
            }
            is Resource.Failure -> {
                if (res.errorCode == 401)
                    withContext(Dispatchers.Main) {
                        Utils.logout(this@QuestionsActivity, true)
                    }
            }
        }
    }

    private suspend fun uploadScore(gameScore: String) {
        when (val res = ApiRepository(RemoteDataSource().getApi(NetworkApi::class.java)).sendScore("Bearer $token", number, gameScore, season)) {
            is Resource.Failure -> {
                if (res.errorCode == 401)
                    withContext(Dispatchers.Main) {
                        Utils.logout(this@QuestionsActivity, true)
                    }
            }
        }
    }
}