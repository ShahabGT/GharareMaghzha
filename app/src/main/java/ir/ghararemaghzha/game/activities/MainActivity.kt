package ir.ghararemaghzha.game.activities

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.firebase.analytics.FirebaseAnalytics
import io.realm.Realm
import io.realm.kotlin.where
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.classes.Const.GHARAREHMAGHZHA_BROADCAST
import ir.ghararemaghzha.game.classes.Const.GHARAREHMAGHZHA_BROADCAST_MESSAGE
import ir.ghararemaghzha.game.classes.Const.GHARAREHMAGHZHA_BROADCAST_REFRESH
import ir.ghararemaghzha.game.classes.MySharedPreference
import ir.ghararemaghzha.game.classes.Utils
import ir.ghararemaghzha.game.data.ApiRepository
import ir.ghararemaghzha.game.data.NetworkApi
import ir.ghararemaghzha.game.data.RemoteDataSource
import ir.ghararemaghzha.game.data.Resource
import ir.ghararemaghzha.game.databinding.ActivityMainBinding
import ir.ghararemaghzha.game.dialogs.GetDataDialog
import ir.ghararemaghzha.game.dialogs.NewVersionDialog
import ir.ghararemaghzha.game.dialogs.RulesDialog
import ir.ghararemaghzha.game.models.MessageModel
import ir.ghararemaghzha.game.models.QuestionModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding
    private var number: String = ""
    private var token: String = ""
    private lateinit var mFirebaseAnalytics: FirebaseAnalytics
    private lateinit var dataDialog: GetDataDialog
    private var doubleBackToExitPressedOnce = false
    private lateinit var db: Realm
    private lateinit var refreshIntent: Intent
    private lateinit var navController: NavController
    private val notificationBroadCast: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateMessages()
            Utils.removeNotification(this@MainActivity)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    private fun getExtraIntent() {
        val newIntent = intent
        if (newIntent != null && newIntent.extras != null) {
            val st = intent.extras?.getString(GHARAREHMAGHZHA_BROADCAST_MESSAGE, "default")
            if (st != null) {
                if (st == "new")
                    b.mainBnv.selectedItemId = R.id.menu_message
                else if (st == "chat")
                    startActivity(Intent(this, SupportActivity::class.java))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(notificationBroadCast)
    }

    override fun onBackPressed() {
        if (b.mainMotion.currentState == R.id.end) {
            b.mainMotion.transitionToStart()
            return
        }
        if (navController.popBackStack()) return
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        doubleBackToExitPressedOnce = true
        Toast.makeText(this, getString(R.string.general_exit), Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        else
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(b.root)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        db = Realm.getDefaultInstance()

        init()
        if (MySharedPreference.getInstance(this).isFirstTime())
            helpInfo()
        else
            firebaseDebug("ok")
    }

    override fun onResume() {
        super.onResume()
        getExtraIntent()
        if (db.isEmpty) {
            MySharedPreference.getInstance(this).also {
                it.clearCounter(false)
                it.setScore(0)
                it.setBoosterValue(1f)
            }.clearCounter(false)
            dataDialog = Utils.showGetDataLoading(this@MainActivity)
            CoroutineScope(Dispatchers.IO).launch {
                getData()
            }
        } else {
            updateMessages()
            CoroutineScope(Dispatchers.IO).launch {
                appOpen()
            }
        }

        setAvatars(this)
        Utils.removeNotification(this)
    }

    private fun init() {
        getUserDetails()
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_container) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupWithNavController(b.mainBnv, navController)
        b.mainBnv.setOnNavigationItemReselectedListener { }
        refreshIntent = Intent(GHARAREHMAGHZHA_BROADCAST_REFRESH)
        doubleBackToExitPressedOnce = false
        onClicks()
        navigationDrawer()
        registerReceiver(notificationBroadCast, IntentFilter(GHARAREHMAGHZHA_BROADCAST))
    }

    private fun getUserDetails() {
        number = MySharedPreference.getInstance(this).getNumber()
        token = MySharedPreference.getInstance(this).getAccessToken()
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(this, true)
        }
    }

    private fun onClicks() {
        b.mainToolbar.toolbarInsta.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.data = Uri.parse(getString(R.string.instagram_url))
            startActivity(intent)
        }
        b.mainToolbar.toolbarAvatar.setOnClickListener { navController.navigate(R.id.action_global_profileEditFragment) }
    }

    private fun navigationDrawer() {
        b.mainDrawer.navigationName.text = MySharedPreference.getInstance(this).getUsername()
        b.mainDrawer.navigationCode.text = getString(R.string.profile_code, MySharedPreference.getInstance(this).getUserCode())
        b.mainDrawer.navigationScore.text = getString(R.string.highscore_score, MySharedPreference.getInstance(this).getScore())
        b.mainDrawer.navigationExit.setOnClickListener { Utils.logout(this, false) }
        b.mainDrawer.navigationBuyhistory.setOnClickListener {
            navController.navigate(R.id.action_global_buyHistoryFragment)
            b.mainMotion.transitionToStart()
        }
        b.mainDrawer.navigationSupport.setOnClickListener {
            startActivity(Intent(this, SupportActivity::class.java))
            b.mainMotion.transitionToStart()
        }
        b.mainDrawer.navigationInvite.setOnClickListener {
            navController.navigate(R.id.action_global_inviteFragment)
            b.mainMotion.transitionToStart()
        }
        b.mainDrawer.navigationSetting.setOnClickListener {
            navController.navigate(R.id.action_global_settingsFragment)
            b.mainMotion.transitionToStart()
        }
        b.mainDrawer.navigationInstagram.setOnClickListener {
            val intent = Intent().also {
                it.action = Intent.ACTION_VIEW
                it.data = Uri.parse(getString(R.string.instagram_url))
            }
            startActivity(intent)
            b.mainMotion.transitionToStart()
        }
        b.mainDrawer.navigationRule.setOnClickListener {
            showRulesDialog()
            b.mainMotion.transitionToStart()
        }
        b.mainDrawer.navigationAbout.setOnClickListener {
            navController.navigate(R.id.action_global_aboutFragment)
            b.mainMotion.transitionToStart()
        }
        b.mainDrawer.navigationHelp.setOnClickListener {
            b.mainMotion.transitionToStart()
            helpInfo()
        }
    }

    fun setAvatars(activity: Activity) {
        Glide.with(activity)
                .load(activity.getString(R.string.avatar_url, MySharedPreference.getInstance(activity).getUserAvatar()))
                .circleCrop()
                .placeholder(R.drawable.placeholder)
                .into(b.mainDrawer.navigationAvatar)
        Glide.with(activity)
                .load(activity.getString(R.string.avatar_url, MySharedPreference.getInstance(activity).getUserAvatar()))
                .circleCrop()
                .placeholder(R.drawable.placeholder)
                .into(b.mainToolbar.toolbarAvatar)
    }

    private fun helpInfo() {
        TapTargetSequence(this)
                .targets(
                        TapTarget.forView(b.mainBnv.rootView.findViewById(R.id.menu_profile), getString(R.string.tap_target_profile_title), getString(R.string.tap_target_profile_des))
                                .cancelable(false)
                                .dimColor(R.color.black)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(R.color.white)
                                .textColor(android.R.color.black),
                        TapTarget.forView(b.mainBnv.rootView.findViewById(R.id.menu_highscore), getString(R.string.tap_target_highscore_title), getString(R.string.tap_target_highscore_des))
                                .cancelable(false)
                                .dimColor(R.color.black)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(R.color.white)
                                .textColor(android.R.color.black),
                        TapTarget.forView(b.mainBnv.rootView.findViewById(R.id.menu_start), getString(R.string.tap_target_start_title), getString(R.string.tap_target_start_des))
                                .cancelable(false)
                                .dimColor(R.color.black)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(R.color.white)
                                .textColor(android.R.color.black),
                        TapTarget.forView(b.mainBnv.rootView.findViewById(R.id.menu_buy), getString(R.string.tap_target_buy_title), getString(R.string.tap_target_buy_des))
                                .cancelable(false)
                                .dimColor(R.color.black)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(R.color.white)
                                .textColor(android.R.color.black),
                        TapTarget.forView(b.mainBnv.rootView.findViewById(R.id.menu_message), getString(R.string.tap_target_message_title), getString(R.string.tap_target_message_des))
                                .cancelable(false)
                                .dimColor(R.color.black)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(R.color.white)
                                .textColor(android.R.color.black),
                        TapTarget.forView(b.mainToolbar.toolbarMenu, getString(R.string.tap_target_menu_title), getString(R.string.tap_target_menu_des))
                                .cancelable(false)
                                .dimColor(R.color.black)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(R.color.white)
                                .textColor(android.R.color.black)
                ).listener(object : TapTargetSequence.Listener {
                    override fun onSequenceFinish() {
                        MySharedPreference.getInstance(this@MainActivity).setFirstTime()
                        firebaseDebug("ok")
                    }

                    override fun onSequenceStep(lastTarget: TapTarget, targetClicked: Boolean) {}
                    override fun onSequenceCanceled(lastTarget: TapTarget) {}
                }).start()
    }

    private fun firebaseDebug(result: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "verify")
        bundle.putString(FirebaseAnalytics.Param.ITEM_VARIANT, result)
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle)
    }

    private fun updateMessages() {
        val size: Int = db.where<MessageModel>().equalTo("sender", "admin").and().equalTo("read", "0".toInt()).findAll().size
        val badgeDrawable = b.mainBnv.getOrCreateBadge(R.id.menu_message)
        badgeDrawable.isVisible = size > 0
        if (MySharedPreference.getInstance(this@MainActivity).getUnreadChats() > 0) {
            b.mainDrawer.mainChatNew.visibility = View.VISIBLE
            b.mainToolbar.toolbarNew.visibility = View.VISIBLE
        } else {
            b.mainDrawer.mainChatNew.visibility = View.GONE
            b.mainToolbar.toolbarNew.visibility = View.GONE
        }
    }

    private fun showNewVersionDialog(urgent: Boolean) {
        val dialog = NewVersionDialog(this)
        if (urgent) {
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
        } else {
            dialog.setCancelable(true)
            dialog.setCanceledOnTouchOutside(true)
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.show()
        val window = dialog.window
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    private fun showRulesDialog() {
        val dialog = RulesDialog(this)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.show()
        val window = dialog.window
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    private suspend fun appOpen() {
        when (val res = ApiRepository(RemoteDataSource().getApi(NetworkApi::class.java)).appOpen("Bearer $token", number)) {
            is Resource.Success -> {
                if (res.value.result == "success") {
                    withContext(Dispatchers.Main) {
                        if (!Utils.isTimeAcceptable(res.value.time)) {
                            Utils.showTimeError(this@MainActivity)
                            return@withContext
                        } else {
                            MySharedPreference.getInstance(this@MainActivity).setDaysPassed(res.value.passed.toInt())
                        }

                        val newSeason = res.value.season.toInt()
                        val oldSeason = MySharedPreference.getInstance(this@MainActivity).getSeason()

                        if (oldSeason == 0) {
                            MySharedPreference.getInstance(this@MainActivity).setSeason(newSeason)
                        } else if (newSeason > oldSeason) {
                            resetForNewSeason(newSeason)
                            return@withContext
                        }

                        val myVersion = Utils.getVersionCode(this@MainActivity)
                        val newVersion: Int = res.value.version.toInt()

                        if (newVersion > myVersion) {
                            if (res.value.versionEssential == "1") {
                                showNewVersionDialog(true)
                                return@withContext
                            } else
                                showNewVersionDialog(false)
                        }

                        val newScore: Int = res.value.scoreCount.toInt()
                        val oldScore = MySharedPreference.getInstance(this@MainActivity).getScore()

                        when {
                            newScore == -1 -> {
                                MySharedPreference.getInstance(this@MainActivity).setScore(0)
                                CoroutineScope(Dispatchers.IO).launch {
                                    uploadScore("0")
                                }
                            }
                            newScore > oldScore -> {
                                MySharedPreference.getInstance(this@MainActivity).setScore(newScore)
                            }
                            oldScore > newScore -> {
                                CoroutineScope(Dispatchers.IO).launch {
                                    uploadScore(oldScore.toString())
                                }
                            }
                        }

                        sendBroadcast(refreshIntent)

                        val serverBooster: Int = res.value.userBooster.toInt()
                        val serverBoosterCount: Int = res.value.scoreBoosterCount.toInt()
                        if (serverBooster > 0 && serverBoosterCount > 0) {
                            MySharedPreference.getInstance(this@MainActivity).setBoosterValue(res.value.boosterValue.toFloat())
                            MySharedPreference.getInstance(this@MainActivity).setCounter(200 - serverBoosterCount)
                        } else {
                            MySharedPreference.getInstance(this@MainActivity).setBoosterValue(1f)
                        }

                        uploadAnswers()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        firebaseDebug("failed")
                        Utils.showInternetError(this@MainActivity) {
                            CoroutineScope(Dispatchers.IO).launch {
                                appOpen()
                            }
                        }
                    }
                }
            }
            is Resource.Failure -> {
                withContext(Dispatchers.Main) {
                    firebaseDebug("failed")
                    if (res.errorCode == 401) {
                        Utils.logout(this@MainActivity, true)
                    } else {
                        Utils.showInternetError(this@MainActivity) {
                            CoroutineScope(Dispatchers.IO).launch {
                                appOpen()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun resetForNewSeason(newSeason: Int) {
        dataDialog = Utils.showGetDataLoading(this@MainActivity)
        MySharedPreference.getInstance(this).setScore(0)
        MySharedPreference.getInstance(this).clearCounter(false)
        MySharedPreference.getInstance(this).setBoosterValue(1f)
        MySharedPreference.getInstance(this).setSeason(newSeason)
        val results = db.where<QuestionModel>().findAll()
        db.executeTransaction { results.deleteAllFromRealm() }
        CoroutineScope(Dispatchers.IO).launch {
            getData()
        }
    }

    private suspend fun uploadScore(score: String) {
        val passed = MySharedPreference.getInstance(this).getDaysPassed()
        if (passed in 0..6) {
            when (val res = ApiRepository(RemoteDataSource().getApi(NetworkApi::class.java)).sendScore("Bearer $token", number, score, MySharedPreference.getInstance(this@MainActivity).getSeason())) {
                is Resource.Failure -> {
                    if (res.errorCode == 401) {
                        withContext(Dispatchers.Main) {
                            Utils.logout(this@MainActivity, true)
                        }
                    }
                }
            }
        }
    }

    private fun uploadAnswers() {
        val passed = MySharedPreference.getInstance(this).getDaysPassed()
        val season = MySharedPreference.getInstance(this).getSeason()
        if (passed in 0..6) {
            val models = db.where<QuestionModel>().notEqualTo("userAnswer", "-1").and().equalTo("uploaded", false).findAll()
            for (model in models) {
                CoroutineScope(Dispatchers.Main).launch {
                    uploadAnswer(model.questionId, model.userAnswer, model.userBooster, season)
                }
            }
        }
    }

    private suspend fun uploadAnswer(questionId: String, userAnswer: String, booster: String, season: Int) {
        when (val res = ApiRepository(RemoteDataSource().getApi(NetworkApi::class.java)).answerQuestion("Bearer $token", number, questionId, userAnswer, booster, season)) {
            is Resource.Success -> {
                if (res.value.result == "success") {
                    withContext(Dispatchers.Main) {
                        db.executeTransaction {
                            val result = it.where<QuestionModel>().equalTo("questionId", questionId).findFirst()
                            result?.uploaded = true
                        }
                    }
                }
            }
            is Resource.Failure -> {
                if (res.errorCode == 401) {
                    withContext(Dispatchers.Main) {
                        Utils.logout(this@MainActivity, true)
                    }
                }
            }
        }
    }

    private suspend fun getData() {
        when (val res = ApiRepository(RemoteDataSource().getApi(NetworkApi::class.java)).getQuestions("Bearer $token", number)) {
            is Resource.Success -> {
                if (res.value.result == "success") {
                    withContext(Dispatchers.Main) {
                        if (res.value.result == "success") {
                            val data: MutableList<QuestionModel> = ArrayList()
                            for (m in res.value.data) {
                                m.uploaded = m.userAnswer != "-1"
                                data.add(m)
                            }
                            db.executeTransaction { it.insertOrUpdate(data) }
                            dataDialog.dismiss()
                            appOpen()
                        } else {
                            Utils.showInternetError(this@MainActivity) {
                                dataDialog = Utils.showGetDataLoading(this@MainActivity)
                                CoroutineScope(Dispatchers.IO).launch {
                                    getData()
                                }
                            }
                        }
                    }
                }
            }
            is Resource.Failure -> {
                withContext(Dispatchers.Main) {
                    dataDialog.dismiss()
                    if (res.errorCode == 401) {
                        Utils.logout(this@MainActivity, true)
                    } else {
                        Utils.showInternetError(this@MainActivity) {
                            dataDialog = Utils.showGetDataLoading(this@MainActivity)
                            CoroutineScope(Dispatchers.IO).launch {
                                getData()
                            }
                        }
                    }
                }
            }
        }
    }
}