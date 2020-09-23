package ir.ghararemaghzha.game.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import ir.ghararemaghzha.game.R

class InfoDialog constructor(ctx:Context,text:String) : Dialog(ctx) {
    val myContext = ctx
    val des = text

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_info)
        findViewById<MaterialTextView>(R.id.info_dialog_description).text=des
        findViewById<MaterialButton>(R.id.info_dialog_btn).setOnClickListener { dismiss() }
    }

}