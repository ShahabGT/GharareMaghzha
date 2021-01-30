package ir.ghararemaghzha.game.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.classes.Utils
import ir.ghararemaghzha.game.databinding.DialogNointernetBinding

class NoInternetDialog(ctx: Context,private val retry:()->Unit) : Dialog(ctx) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = DialogNointernetBinding.inflate(LayoutInflater.from(context))
        setContentView(b.root)
       b.alertDialogSet.setOnClickListener {
            if (Utils.checkInternet(context)) {
                retry()
                dismiss()
            } else {
                Toast.makeText(context, R.string.general_internet_error, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
