package ir.ghararemaghzha.game.dialogs

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.button.MaterialButton
import ir.ghararemaghzha.game.R

 class TimeDialog(ctx:Context) : Dialog(ctx) {

     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         setContentView(R.layout.dialog_time)
         findViewById<MaterialButton>(R.id.alert_dialog_set).setOnClickListener{
             context.startActivity(Intent(android.provider.Settings.ACTION_DATE_SETTINGS))
             dismiss()
         }
     }
}
