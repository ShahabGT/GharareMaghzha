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
    private var number:String=""
    private var token:String=""

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
    private fun getUserDetails(){
        number = MySharedPreference.getInstance(this).getNumber()
        token = MySharedPreference.getInstance(this).getAccessToken()
        if (number.isEmpty() || token.isEmpty()) {
            logout(this, true)
        }
    }

    private fun init() {
        getUserDetails()
        MySharedPreference.getInstance(this).setUnreadChats( 0)
        db = Realm.getDefaultInstance()
        val intent = Intent()
        intent.action = GHARAREHMAGHZHA_BROADCAST
        db.executeTransaction {
            val results = it.where(MessageModel::class.java).notEqualTo("sender", "admin").equalTo("read", "0".toInt()).findAll()
            results.setInt("read", 1)
            sendBroadcast(intent)
        }


        recyclerView = findViewById(R.id.chat_recycler)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        recyclerView.layoutManager = layoutManager

        adapter = ChatAdapter(this, db.where(MessageModel::class.java).notEqualTo("sender", "admin").sort("date", Sort.DESCENDING).findAll(), true)
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
                val myDb = Realm.getDefaultInstance()

                if (res.value.result == "success") {
                    myDb.beginTransaction()
                    val models = myDb.where(MessageModel::class.java).equalTo("messageId", key).findAll()
                    models.first()?.stat = 1
                    myDb.commitTransaction()
                } else {
                    myDb.beginTransaction()
                    val models = myDb.where(MessageModel::class.java).equalTo("messageId", key).findAll()
                    models.first()?.stat = -1
                    myDb.commitTransaction()
                }
            }
            is Resource.Failure -> {
                val myDb = Realm.getDefaultInstance()
                if (res.isNetworkError) {
                    myDb.beginTransaction()
                    val models = myDb.where(MessageModel::class.java).equalTo("messageId", key).findAll()
                    models.first()?.stat = -1
                    myDb.commitTransaction()
                } else {
                    when (res.errorCode) {
                        401 -> logout(this, true)
                        else -> {
                            myDb.beginTransaction()
                            val models = myDb.where(MessageModel::class.java).equalTo("messageId", key).findAll()
                            models.first()?.stat = -1
                            myDb.commitTransaction()
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
                    MySharedPreference.getInstance(this).setLastUpdateChat(nowDate)
                    val myDb = Realm.getDefaultInstance()
                    val data:MutableCollection<MessageModel> = mutableListOf()
                    res.value.data.forEach {
                        it.stat = 1
                        it.read = 1
                        it.title="new"
                        it.messageId = getNextKey(myDb)
                        data.add(it)
                    }
                    myDb.executeTransaction { it.insert(data) }

                    withContext(Dispatchers.Main) {
                        recyclerView.scrollToPosition(0)

                    }
                }
                isLoading = false

            }
            is Resource.Failure -> {
                isLoading = false
                if (!res.isNetworkError && res.errorCode == 401) logout(this, true)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(br)

    }
}