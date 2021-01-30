package ir.ghararemaghzha.game.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.classes.MySharedPreference
import ir.ghararemaghzha.game.classes.Utils
import ir.ghararemaghzha.game.data.ApiRepository
import ir.ghararemaghzha.game.data.NetworkApi
import ir.ghararemaghzha.game.data.RemoteDataSource
import ir.ghararemaghzha.game.data.Resource
import ir.ghararemaghzha.game.databinding.DialogRulesBinding
import kotlinx.coroutines.*

class RulesDialog(ctx: Context) : Dialog(ctx) {

    private lateinit var b:DialogRulesBinding
    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = DialogRulesBinding.inflate(LayoutInflater.from(context))
        setContentView(b.root)
        job = CoroutineScope(Dispatchers.IO).launch {
            getData()
        }
        b.rulesClose.setOnClickListener { dismiss() }
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
                    b.rulesText.text = res.value.data
                }
            }
            is Resource.Failure -> {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, R.string.general_error, Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }
        }
    }

    override fun dismiss() {
        super.dismiss()
        job.cancel()
    }
}



