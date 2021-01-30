package ir.ghararemaghzha.game.dialogs

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import ir.ghararemaghzha.game.databinding.DialogTimeBinding

class TimeDialog(ctx:Context) : Dialog(ctx) {

     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         val b = DialogTimeBinding.inflate(LayoutInflater.from(context))
         setContentView(b.root)
         b.alertDialogSet.setOnClickListener{
             context.startActivity(Intent(android.provider.Settings.ACTION_DATE_SETTINGS))
             dismiss()
         }
     }
}
