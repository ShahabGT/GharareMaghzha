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
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.emoji.widget.EmojiEditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.realm.kotlin.where
import io.realm.Sort
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.adapters.ChatAdapter
import ir.ghararemaghzha.game.classes.Const.GHARAREHMAGHZHA_BROADCAST
import ir.ghararemaghzha.game.classes.MySharedPreference
import ir.ghararemaghzha.game.classes.Utils.*
import ir.ghararemaghzha.game.data.ApiRepository
import ir.ghararemaghzha.game.data.NetworkApi
import ir.ghararemaghzha.game.data.RemoteDataSource
import ir.ghararemaghzha.game.data.Resource
import ir.ghararemaghzha.game.models.MessageModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.charset.StandardCharsets

class SupportActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatAdapter
    private lateinit var message: EmojiEditText
    private lateinit var send: ImageView
    private lateinit var db: Realm
    private var isLoading = false
    private var number: String = ""
    private var token: String = ""

    private val br = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            removeNotification(this@SupportActivity)
            CoroutineScope(Dispatchers.IO).launch {
                if (!isLoading)
                    getChatData()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        setContentView(R.layout.activity_support)
        init()
    }

    private fun getUserDetails() {
        number = MySharedPreference.getInstance(this).getNumber()
        token = MySharedPreference.getInstance(this).getAccessToken()
        if (number.isEmpty() || token.isEmpty()) {
            logout(this, true)
        }
    }

    private fun init() {
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


        recyclerView = findViewById(R.id.chat_recycler)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        recyclerView.layoutManager = layoutManager

        adapter = ChatAdapter(this, db.where<MessageModel>().notEqualTo("sender", "admin").sort("date", Sort.DESCENDING).findAll())
        recyclerView.adapter = adapter
        recyclerView.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom) {
                recyclerView.post {
                    recyclerView.scrollToPosition(0)
                }
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            if (!isLoading)
                getChatData()
        }
        send = findViewById(R.id.chat_send)
        message = findViewById(R.id.chat_text)
        onClicks()
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(br, IntentFilter(GHARAREHMAGHZHA_BROADCAST))
    }


    private fun onClicks() {
        findViewById<ImageView>(R.id.chat_close).setOnClickListener { onBackPressed() }

        send.setOnClickListener {
            val txt = message.text.toString().trim()
            val data = txt.toByteArray(StandardCharsets.UTF_8)
            val body = Base64.encodeToString(data, Base64.DEFAULT)

            if (txt.isNotEmpty()) {
                message.setText("")
                val userId = MySharedPreference.getInstance(this).getUserId()
                val model = MessageModel()
                model.date = currentDate()
                model.stat = 0
                model.message = body
                model.receiver = "1"
                model.read = 1
                model.sender = userId
                model.title = "new"
                val key = getNextKey(db)
                model.messageId = getNextKey(db)
                db.executeTransaction { it.insert(model) }
                CoroutineScope(Dispatchers.IO).launch {
                    sendMessage(body, key)
                }
                recyclerView.scrollToPosition(0)
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
                            401 -> logout(this@SupportActivity, true)
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


    private suspend fun getChatData() {
        isLoading = true
        val lastUpdate = MySharedPreference.getInstance(this).getLastUpdateChat()
        val nowDate = currentDate()

        when (val res = ApiRepository(RemoteDataSource().getApi(NetworkApi::class.java)).getMessages("Bearer $token", number, lastUpdate)) {
            is Resource.Success -> {

                if (res.value.message == "ok") {
                    withContext(Dispatchers.Main) {
                        MySharedPreference.getInstance(this@SupportActivity).setLastUpdateChat(nowDate)
                        val data: MutableCollection<MessageModel> = mutableListOf()
                        for(model in res.value.data){
                            model.stat = 1
                            model.read = 1
                            model.title = "new"
                            model.messageId = getNextKey(db)
                            data.add(model)
                        }
                        db.executeTransaction { it.insertOrUpdate(data) }
                        recyclerView.scrollToPosition(0)
                    }
                }
                isLoading = false
            }
            is Resource.Failure -> {
                withContext(Dispatchers.Main) {
                    isLoading = false
                    if (!res.isNetworkError && res.errorCode == 401) logout(this@SupportActivity, true)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(br)
    }
}