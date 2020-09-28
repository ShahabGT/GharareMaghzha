package ir.ghararemaghzha.game.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import ir.ghararemaghzha.game.R

class GetDataDialog(context: Context) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_getdata)
    }
}