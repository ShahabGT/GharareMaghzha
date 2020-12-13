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
import android.widget.ImageView
import android.widget.ProgressBar
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
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.classes.Const
import ir.ghararemaghzha.game.classes.Const.GHARAREHMAGHZHA_BROADCAST_END
import ir.ghararemaghzha.game.classes.MySettingsPreference
import ir.ghararemaghzha.game.classes.MySharedPreference
import ir.ghararemaghzha.game.classes.Utils
import ir.ghararemaghzha.game.data.ApiRepository
import ir.ghararemaghzha.game.data.NetworkApi
import ir.ghararemaghzha.game.data.RemoteDataSource
import ir.ghararemaghzha.game.data.Resource
import ir.ghararemaghzha.game.models.QuestionModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class QuestionsActivity : AppCompatActivity() {
    private var progress: Double = 100.0
    private var time = 15
    private lateinit var downTimer: CountDownTimer
    private lateinit var nextTimer: CountDownTimer
    private lateinit var progressBar: ProgressBar
    private lateinit var booster: ImageView
    private lateinit var timeText: MaterialTextView
    private lateinit var question: MaterialTextView
    private lateinit var answer1: MaterialTextView
    private lateinit var answer2: MaterialTextView
    private lateinit var answer3: MaterialTextView
    private lateinit var answer4: MaterialTextView
    private lateinit var score: MaterialTextView
    private lateinit var questionPoints: MaterialTextView
    private lateinit var questionRemain: MaterialTextView
    private lateinit var answer1c: MaterialCardView
    private lateinit var answer2c: MaterialCardView
    private lateinit var answer3c: MaterialCardView
    private lateinit var answer4c: MaterialCardView
    private lateinit var questionCard: MaterialCardView
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
    private lateinit var music: ImageView
    private var musicSetting = false
    private var hasBooster = false
    private var foreground = false
    private var number:String=""
    private var token:String=""

    private val br: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Toast.makeText(this@QuestionsActivity, R.string.general_end, Toast.LENGTH_LONG).show()
            onBackPressed()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val window = window
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            window.insetsController?.hide(WindowInsets.Type.statusBars())
         else
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_question)

        if (MySharedPreference.getInstance(this).isFirstTimeQuestion)
            helpInfo()
        else
            init()
    }

    private fun helpInfo() {
        TapTargetSequence(this)
                .targets(
                        TapTarget.forView(findViewById(R.id.question_score_card), getString(R.string.tap_target_question_score_title), getString(R.string.tap_target_question_score_des))
                                .cancelable(false)
                                .tintTarget(false)
                                .dimColor(R.color.black)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(R.color.white)
                                .textColor(android.R.color.black),
                        TapTarget.forView(findViewById(R.id.question_music), getString(R.string.tap_target_question_music_title), getString(R.string.tap_target_question_music_des))
                                .cancelable(false)
                                .dimColor(R.color.black)
                                .tintTarget(false)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(R.color.white)
                                .textColor(android.R.color.black),
                        TapTarget.forView(findViewById(R.id.question_booster), getString(R.string.tap_target_question_booster_title), getString(R.string.tap_target_question_booster_des))
                                .cancelable(false)
                                .tintTarget(false)
                                .dimColor(R.color.black)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(R.color.white)
                                .textColor(android.R.color.black),
                        TapTarget.forView(findViewById(R.id.question_progress_text), getString(R.string.tap_target_question_time_title), getString(R.string.tap_target_question_time_des))
                                .cancelable(false)
                                .tintTarget(false)
                                .dimColor(R.color.black)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(R.color.white)
                                .textColor(android.R.color.black),
                        TapTarget.forView(findViewById(R.id.question_points), getString(R.string.tap_target_question_point_title), getString(R.string.tap_target_question_point_des))
                                .cancelable(false)
                                .tintTarget(false)
                                .dimColor(R.color.black)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(R.color.white)
                                .textColor(android.R.color.black),
                        TapTarget.forView(findViewById(R.id.question_remaining), getString(R.string.tap_target_question_question_title), getString(R.string.tap_target_question_question_des))
                                .cancelable(false)
                                .tintTarget(false)
                                .dimColor(R.color.black)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(R.color.white)
                                .textColor(android.R.color.black),
                        TapTarget.forView(findViewById(R.id.question_report), getString(R.string.tap_target_question_next_report_title), getString(R.string.tap_target_question_next_report_des))
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

    private fun getUserDetails(){
        number = MySharedPreference.getInstance(this).number
        token = MySharedPreference.getInstance(this).accessToken
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(this, true)
        }
    }

    private fun init() {
        getUserDetails()
        db = Realm.getDefaultInstance()
        data = db.where(QuestionModel::class.java).equalTo("visible", true)
                .and().equalTo("userAnswer", "-1").findAll()
        music = findViewById(R.id.question_music)
        musicSetting = MySettingsPreference.getInstance(this).getMusic()
        music.setImageResource(if (musicSetting) R.drawable.vector_music_on else R.drawable.vector_music_off)
        questionPoints = findViewById(R.id.question_points)
        questionRemain = findViewById(R.id.question_remaining)
        booster = findViewById(R.id.question_booster)

        question = findViewById(R.id.question_question)
        questionCard = findViewById(R.id.question_question_card)
        answer1 = findViewById(R.id.question_answer1)
        answer1c = findViewById(R.id.question_answer1_card)
        answer2 = findViewById(R.id.question_answer2)
        answer2c = findViewById(R.id.question_answer2_card)
        answer3 = findViewById(R.id.question_answer3)
        answer3c = findViewById(R.id.question_answer3_card)
        answer4 = findViewById(R.id.question_answer4)
        answer4c = findViewById(R.id.question_answer4_card)
        score = findViewById(R.id.question_score)
        gameScore = MySharedPreference.getInstance(this).score.toInt()
        score.text = gameScore.toString()
        progressBar = findViewById(R.id.question_progress_bar)
        progressBar.progress = progress.toInt()
        timeText = findViewById(R.id.question_progress_text)
        downTimer = object : CountDownTimer(15000, 1000) {
            override fun onTick(l: Long) {
                time -= 1
                progress -= 6.6
                timeText.text = time.toString()
                progressBar.progress = progress.toInt()
                if (l < 6000) timeText.setTextColor(resources.getColor(R.color.random1))
            }

            override fun onFinish() {
                timeText.text = 0.toString()
                progressBar.progress = 0
                answer1c.isEnabled = false
                answer2c.isEnabled = false
                answer3c.isEnabled = false
                answer4c.isEnabled = false
                if (foreground) nextQuestion()
            }
        }
        nextTimer = object : CountDownTimer(2500, 1000) {
            override fun onTick(l: Long) {}
            override fun onFinish() {
                nextTimer.cancel()
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


        findViewById<View>(R.id.question_report).setOnClickListener {
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
        music.setOnClickListener {
            musicSetting = !musicSetting
            music.setImageResource(if (musicSetting) R.drawable.vector_music_on else R.drawable.vector_music_off)
            if (musicSetting) {
                mediaPlayer = MediaPlayer.create(this, R.raw.game)
                mediaPlayer.isLooping = true
                mediaPlayer.start()
            } else {
                mediaPlayer.pause()
                mediaPlayer.stop()
            }
        }
        answer1c.setOnClickListener {
            answer(0,answer1,answer1c)
        }
        answer2c.setOnClickListener {
            answer(1,answer2,answer2c)
        }
        answer3c.setOnClickListener {
            answer(2,answer3,answer3c)
        }
        answer4c.setOnClickListener {
            answer(3,answer4,answer4c)
        }
        findViewById<View>(R.id.question_close).setOnClickListener { this@QuestionsActivity.finish() }
    }

    private fun answer(which:Int,button:MaterialTextView,buttonCard:MaterialCardView){
        if (MySharedPreference.getInstance(this).boosterValue != 1f) MySharedPreference.getInstance(this@QuestionsActivity).counterIncrease()
        downTimer.cancel()
        timeText.text = 0.toString()
        progressBar.progress = 0
        answer1c.isEnabled = false
        answer2c.isEnabled = false
        answer3c.isEnabled = false
        answer4c.isEnabled = false
        val answerId: String = if (shouldRandomize) (randomAnswers[which] + 1).toString() else which.toString()
        setAnswer(answerId)
        val qId = model.questionId
        CoroutineScope(Dispatchers.IO).launch {
            uploadAnswer(answerId,qId)
        }
        if (button.text.toString() == correctAnswer) {
            buttonCard.setCardBackgroundColor(resources.getColor(R.color.green))
            YoYo.with(Techniques.Tada).duration(500).playOn(buttonCard)
            playSound(correctSound)
            val qPoint = model.questionPoints.toInt()
            YoYo.with(Techniques.Bounce).duration(500).playOn(score)
            gameScore += (qPoint * MySharedPreference.getInstance(this).boosterValue).toInt()
            score.text = gameScore.toString()
            MySharedPreference.getInstance(this@QuestionsActivity).score = gameScore.toString()
            CoroutineScope(Dispatchers.IO).launch {
                uploadScore(gameScore.toString())
            }
        } else {
            buttonCard.setCardBackgroundColor(resources.getColor(R.color.red))
            YoYo.with(Techniques.Shake).duration(500).playOn(buttonCard)
            playSound(wrongSound)
        }
        Handler(Looper.getMainLooper()).postDelayed({ nextQuestion() }, 1000)
    }

    private fun enterAnimations() {
        YoYo.with(Techniques.Landing).duration(1500).playOn(questionCard)
        YoYo.with(Techniques.Landing).duration(1500).playOn(answer1c)
        YoYo.with(Techniques.Landing).duration(1500).playOn(answer2c)
        YoYo.with(Techniques.Landing).duration(1500).playOn(answer3c)
        YoYo.with(Techniques.Landing).duration(1500).playOn(answer4c)
    }

    private fun nextQuestion() {
        if (data.isEmpty()) {
            Utils.createNotification(this, getString(R.string.questions_notification_title), getString(R.string.questions_notification_body), "ir.ghararemaghzha.game.TARGET_NOTIFICATION")
            Toast.makeText(this, getString(R.string.questions_notification_title), Toast.LENGTH_SHORT).show()
            onBackPressed()
            return
        }
        if (Utils.checkInternet(this)) {
            if (MySharedPreference.getInstance(this).boosterValue == 1f) {
                booster.visibility = View.GONE
                hasBooster = false
            } else {
                hasBooster = true
            }
            questionRemain.text = getString(R.string.question_remaining, (data.size - 1).toString())
            model = getRandom()
            downTimer.cancel()
            nextTimer.start()
            enterAnimations()
            answer1c.isEnabled = true
            answer1c.setCardBackgroundColor(resources.getColor(R.color.white))
            answer2c.isEnabled = true
            answer2c.setCardBackgroundColor(resources.getColor(R.color.white))
            answer3c.isEnabled = true
            answer3c.setCardBackgroundColor(resources.getColor(R.color.white))
            answer4c.isEnabled = true
            answer4c.setCardBackgroundColor(resources.getColor(R.color.white))
            time = 15
            progress = 100.0
            timeText.text = time.toString()
            timeText.setTextColor(resources.getColor(R.color.black))
            progressBar.progress = 100
            randomAnswers = randomNumbers()
            question.text = model.questionText
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
                answer1.text = answers[randomAnswers[0]]
                answer2.text = answers[randomAnswers[1]]
                answer3.text = answers[randomAnswers[2]]
                answer4.text = answers[randomAnswers[3]]
            } else {
                answer1.text = answers[0]
                answer2.text = answers[1]
                answer3.text = answers[2]
                answer4.text = answers[3]
            }
            questionPoints.text = getString(R.string.question_points, model.questionPoints)
            downTimer.start()
            setAnswer("0")
            val qId = model.questionId
            CoroutineScope(Dispatchers.IO).launch {
                uploadAnswer("0",qId)
            }
            Utils.updateServerQuestions(this, db.where(QuestionModel::class.java).equalTo("visible", true).findAll().size.toString())
        } else {
            Toast.makeText(this, getString(R.string.internet_error), Toast.LENGTH_SHORT).show()
            onBackPressed()
        }
    }

    private fun setAnswer(userAnswer: String) {
        var b = "0"
        if (hasBooster) b = "1"
        db.beginTransaction()
        val result = db.where(QuestionModel::class.java).equalTo("questionId", model.questionId).findFirst()
        result?.userAnswer = userAnswer
        result?.userBooster = b
        db.commitTransaction()
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
        nextTimer.cancel()
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

    private suspend fun uploadAnswer(userAnswer: String,questionId:String) {
        var b = "0"
        if (hasBooster) b = "1"

        when (val res = ApiRepository(RemoteDataSource().getApi(NetworkApi::class.java)).answerQuestion("Bearer $token", number,questionId, userAnswer, b, Const.SEASON)) {
            is Resource.Success -> {
                if (res.value.message == "success") {
                    withContext(Dispatchers.Main) {
                        db.beginTransaction()
                        val result = db.where(QuestionModel::class.java).equalTo("questionId", model.questionId).findFirst()
                        result?.uploaded = true
                        db.commitTransaction()
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

    private suspend fun uploadScore(gameScore:String) {
        when (val res = ApiRepository(RemoteDataSource().getApi(NetworkApi::class.java)).sendScore("Bearer $token", number, gameScore, Const.SEASON)) {
            is Resource.Failure -> {
                if (res.errorCode == 401)
                    withContext(Dispatchers.Main) {
                        Utils.logout(this@QuestionsActivity, true)
                    }
            }
        }
    }
}