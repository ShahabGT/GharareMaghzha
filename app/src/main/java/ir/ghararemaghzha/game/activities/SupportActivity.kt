package ir.ghararemaghzha.game.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where
import ir.ghararemaghzha.game.adapters.ChatAdapter
import ir.ghararemaghzha.game.classes.Const.GHARAREHMAGHZHA_BROADCAST
import ir.ghararemaghzha.game.classes.MySharedPreference
import ir.ghararemaghzha.game.classes.Utils
import ir.ghararemaghzha.game.data.ApiRepository
import ir.ghararemaghzha.game.data.NetworkApi
import ir.ghararemaghzha.game.data.RemoteDataSource
import ir.ghararemaghzha.game.data.Resource
import ir.ghararemaghzha.game.databinding.ActivitySupportBinding
import ir.ghararemaghzha.game.models.MessageModel
import ir.ghararemaghzha.game.viewmodels.SupportViewModel
import ir.ghararemaghzha.game.viewmodels.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.charset.StandardCharsets

class SupportActivity : AppCompatActivity() {

    private lateinit var b: ActivitySupportBinding
    private lateinit var viewModel: SupportViewModel
    private lateinit var adapter: ChatAdapter
    private lateinit var db: Realm
    private var isLoading = false
    private lateinit var number: String
    private lateinit var token: String
    private var nowDate: String = ""

    private val br = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Utils.removeNotification(this@SupportActivity)
            if (!isLoading) {
                isLoading = true
                val lastUpdate = MySharedPreference.getInstance(this@SupportActivity).getLastUpdateChat()
                nowDate = Utils.currentDate()
                viewModel.getMessage("Bearer $token", number, lastUpdate)
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivitySupportBinding.inflate(layoutInflater)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        setContentView(b.root)
        init()
    }

    private fun init() {
        viewModel = ViewModelProvider(this, ViewModelFactory(ApiRepository(RemoteDataSource().getApi(NetworkApi::class.java)))).get(SupportViewModel::class.java)
        getUserDetails()
        MySharedPreference.getInstance(this).setUnreadChats(0)
        db = Realm.getDefaultInstance()
        val intent = Intent()
        intent.action = GHARAREHMAGHZHA_BROADCAST
        db.executeTransaction {
            val results = it.where<MessageModel>().notEqualTo("sender", "admin").equalTo("read", "0".toInt()).findAll()
            results.setInt("read", 1)
            sendBroadcast(intent)
        }


        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        b.chatRecycler.layoutManager = layoutManager

        adapter = ChatAdapter(this, db.where<MessageModel>().notEqualTo("sender", "admin").sort("date", Sort.DESCENDING).findAll())
        b.chatRecycler.adapter = adapter
        b.chatRecycler.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom) {
                b.chatRecycler.post {
                    b.chatRecycler.scrollToPosition(0)
                }
            }
        }

        if (!isLoading) {
            isLoading = true
            val lastUpdate = MySharedPreference.getInstance(this).getLastUpdateChat()
            nowDate = Utils.currentDate()
            viewModel.getMessage("Bearer $token", number, lastUpdate)
        }

        responses()
        onClicks()
    }

    private fun responses() {
        viewModel.getMessageResponse.observe(this, { res ->
            isLoading = false
            when (res) {
                is Resource.Success -> {
                    if (res.value.message == "ok") {
                        MySharedPreference.getInstance(this@SupportActivity).setLastUpdateChat(nowDate)
                        val data: MutableCollection<MessageModel> = mutableListOf()
                        var index = Utils.getNextKey(db)
                        for (model in res.value.data) {
                            model.stat = 1
                            model.read = 1
                            model.title = "new"
                            model.messageId = index++
                            data.add(model)
                        }
                        db.executeTransaction { it.insertOrUpdate(data) }
                        b.chatRecycler.scrollToPosition(0)
                    }
                }
                is Resource.Failure -> {
                    if (!res.isNetworkError && res.errorCode == 401) Utils.logout(this@SupportActivity, true)
                }

                is Resource.Loading -> {
                }

            }
        })
    }

    private fun getUserDetails() {
        number = MySharedPreference.getInstance(this).getNumber()
        token = MySharedPreference.getInstance(this).getAccessToken()
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(this, true)
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(br, IntentFilter(GHARAREHMAGHZHA_BROADCAST))
    }


    private fun onClicks() {
        b.chatClose.setOnClickListener { onBackPressed() }

        b.chatSend.setOnClickListener {
            val txt = b.chatText.text.toString().trim()
            val data = txt.toByteArray(StandardCharsets.UTF_8)
            val body = Base64.encodeToString(data, Base64.DEFAULT)

            if (txt.isNotEmpty()) {
                b.chatText.setText("")
                val userId = MySharedPreference.getInstance(this).getUserId()
                val model = MessageModel()
                model.date = Utils.currentDate()
                model.stat = 0
                model.message = body
                model.receiver = "1"
                model.read = 1
                model.sender = userId
                model.title = "new"
                val key = Utils.getNextKey(db)
                model.messageId = Utils.getNextKey(db)
                db.executeTransaction { it.insert(model) }
                CoroutineScope(Dispatchers.IO).launch {
                    sendMessage(body, key)
                }
                b.chatRecycler.scrollToPosition(0)
            }
        }
    }

    private suspend fun sendMessage(message: String, key: Int) {
        when (val res = ApiRepository(RemoteDataSource().getApi(NetworkApi::class.java)).sendMessage("Bearer $token", number, message)) {
            is Resource.Success -> {
                withContext(Dispatchers.Main) {

                    if (res.value.result == "success") {
                        db.executeTransaction {
                            val models = it.where<MessageModel>().equalTo("messageId", key).findFirst()
                            models?.stat = 1
                        }
                    } else {
                        db.executeTransaction {
                            val models = it.where<MessageModel>().equalTo("messageId", key).findFirst()
                            models?.stat = -1
                        }
                    }
                }
            }
            is Resource.Failure -> {
                if (res.isNetworkError) {
                    withContext(Dispatchers.Main) {
                        db.executeTransaction {
                            val models = it.where<MessageModel>().equalTo("messageId", key).findFirst()
                            models?.stat = -1
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        when (res.errorCode) {
                            401 -> Utils.logout(this@SupportActivity, true)
                            else -> {
                                db.executeTransaction {
                                    val models = it.where<MessageModel>().equalTo("messageId", key).findFirst()
                                    models?.stat = -1
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(br)
    }
}