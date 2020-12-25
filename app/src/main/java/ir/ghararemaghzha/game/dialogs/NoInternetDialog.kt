package ir.ghararemaghzha.game.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.classes.RetryInterface
import ir.ghararemaghzha.game.classes.Utils

class NoInternetDialog(ctx: Context,private val retryInterface: RetryInterface) : Dialog(ctx) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_nointernet)
        findViewById<MaterialButton>(R.id.alert_dialog_set).setOnClickListener {
            if (Utils.checkInternet(context)) {
                retryInterface.retry()
                dismiss()
            } else {
                Toast.makeText(context, R.string.general_internet_error, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
