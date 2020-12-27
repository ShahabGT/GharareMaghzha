package ir.ghararemaghzha.game.adapters

import android.app.Activity
import android.content.Context
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.emoji.widget.EmojiTextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import io.realm.kotlin.where
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.classes.DateConverter
import ir.ghararemaghzha.game.classes.MySharedPreference
import ir.ghararemaghzha.game.classes.Utils
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

class ChatAdapter(private val ctx: Context, data: OrderedRealmCollection<MessageModel>) : RealmRecyclerViewAdapter<MessageModel, RecyclerView.ViewHolder>(data, true) {
    private val typeMe = 1
    private val typeOther = 2

    init {
        setHasStableIds(true)
    }

    class MeViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val message: EmojiTextView = v.findViewById(R.id.chat_row_message)
        val date: MaterialTextView = v.findViewById(R.id.chat_row_date)
        val time: MaterialTextView = v.findViewById(R.id.chat_row_time)
        val stat: ImageView = v.findViewById(R.id.chat_row_stat)
    }

    class OtherViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val message: EmojiTextView = v.findViewById(R.id.chat_row_message)
        val date: MaterialTextView = v.findViewById(R.id.chat_row_date)
        val time: MaterialTextView = v.findViewById(R.id.chat_row_time)
    }

    override fun getItemId(position: Int): Long = getItem(position)?.messageId?.toLong() ?: 0

    override fun getItemViewType(position: Int): Int = if (getItem(position)?.sender == "1") typeOther else typeMe


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            if (viewType == typeMe) MeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_chat_me, parent, false))
            else
                OtherViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_chat_other, parent, false))


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = getItem(position)
        if (model != null)
            if (getItemViewType(position) == typeMe) {
                val h = holder as MeViewHolder
                val data = Base64.decode(model.message, Base64.DEFAULT)
                val text = String(data, StandardCharsets.UTF_8)
                h.message.text = text
                val date = model.date
                val dateConverter = DateConverter()
                dateConverter.gregorianToPersian(date.substring(0, 4).toInt(), date.substring(5, 7).toInt(), date.substring(8, 10).toInt())
                h.time.text = date.substring(11, 16)
                h.date.text = "${dateConverter.year.toString().substring(2,4)}/${dateConverter.month}/${dateConverter.day}"
                when (model.stat) {
                    0 -> h.stat.setImageResource(R.drawable.vector_sending)
                    1 -> h.stat.setImageResource(R.drawable.vector_sent)
                    -1 -> h.stat.setImageResource(R.drawable.vector_failed)
                }
                h.stat.setOnClickListener {
                    if (model.stat == -1) {
                        h.stat.setImageResource(R.drawable.vector_sending)
                        val data2 = model.message.toByteArray(StandardCharsets.UTF_8)
                        val body = Base64.encodeToString(data2, Base64.DEFAULT)
                        CoroutineScope(Dispatchers.IO).launch {
                            sendMessage(body, model.messageId, position)
                        }
                    }
                }
            } else {
                val h = holder as OtherViewHolder
                val data = Base64.decode(model.message, Base64.DEFAULT)
                val text = String(data, StandardCharsets.UTF_8)
                h.message.text = text
                val date = model.date
                val dateConverter = DateConverter()
                dateConverter.gregorianToPersian(date.substring(0, 4).toInt(), date.substring(5, 7).toInt(), date.substring(8, 10).toInt())
                h.time.text = date.substring(11, 16)
                h.date.text = "${dateConverter.year.toString().substring(2,4)}/${dateConverter.month}/${dateConverter.day}"
            }

    }

    private suspend fun sendMessage(message: String, key: Int, pos: Int) {
        val number = MySharedPreference.getInstance(ctx).getNumber()
        val token = MySharedPreference.getInstance(ctx).getAccessToken()
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(ctx as Activity, true)
            return
        }
        when (val res = ApiRepository(RemoteDataSource().getApi(NetworkApi::class.java)).sendMessage("Bearer $token", number, message)) {
            is Resource.Success -> {
                withContext(Dispatchers.Main) {
                    val db = Realm.getDefaultInstance()
                    if (res.value.result == "success") {
                        db.beginTransaction()
                        val model = db.where<MessageModel>().equalTo("messageId", key).findFirst()
                        model?.stat = 1
                        db.commitTransaction()
                    } else {
                        db.beginTransaction()
                        val models = db.where<MessageModel>().equalTo("messageId", key).findFirst()
                        models?.stat = -1
                        db.commitTransaction()
                    }
                    notifyItemChanged(pos)
                }
            }
            is Resource.Failure -> {
                if (res.isNetworkError) {
                    withContext(Dispatchers.Main) {
                        val db = Realm.getDefaultInstance()
                        db.beginTransaction()
                        val models = db.where<MessageModel>().equalTo("messageId", key).findFirst()
                        models?.stat = -1
                        db.commitTransaction()
                        notifyItemChanged(pos)
                    }
                } else if (res.errorCode == 401) {
                    withContext(Dispatchers.Main) {
                        Utils.logout(ctx as Activity, true)
                    }
                }
            }
        }
    }
}