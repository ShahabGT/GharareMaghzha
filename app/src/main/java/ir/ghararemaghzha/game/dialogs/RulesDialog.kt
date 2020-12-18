package ir.ghararemaghzha.game.dialogs

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.textview.MaterialTextView
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.classes.MySharedPreference
import ir.ghararemaghzha.game.classes.Utils
import ir.ghararemaghzha.game.data.ApiRepository
import ir.ghararemaghzha.game.data.NetworkApi
import ir.ghararemaghzha.game.data.RemoteDataSource
import ir.ghararemaghzha.game.data.Resource
import kotlinx.coroutines.*

class RulesDialog(context: Activity) : Dialog(context) {

    private lateinit var text :MaterialTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_rules)
        text =findViewById(R.id.rules_text)
        findViewById<ImageView>(R.id.rules_close).setOnClickListener { dismiss() }
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
        when (val res = ApiRepository(RemoteDataSource().getApi(NetworkApi::class.java)).info("Bearer $token", number, "rules")) {
            is Resource.Success -> {
                withContext(Dispatchers.Main) {
                    text.text = res.value.data
                }
            }
            is Resource.Failure -> {
                withContext(Dispatchers.Main) {
                    dismiss()
                    Toast.makeText(context, R.string.general_error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}



