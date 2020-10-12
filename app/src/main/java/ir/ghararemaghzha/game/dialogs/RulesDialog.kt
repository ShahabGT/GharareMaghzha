package ir.ghararemaghzha.game.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import com.google.android.material.textview.MaterialTextView
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import ir.ghararemaghzha.game.R

 class RulesDialog(context: Context) : Dialog(context) {

    private val ctx=context


     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         setContentView(R.layout.dialog_rules)
         findViewById<MaterialTextView>(R.id.rules_text).text=getText()
         findViewById<ImageView>(R.id.rules_close).setOnClickListener{dismiss()}
     }



    private fun getText():String{
        val inputStream = ctx.resources.openRawResource(R.raw.rules)
        val reader =  BufferedReader( InputStreamReader(inputStream))
        val text =  StringBuilder()
        var line:String?

        try {
            line = reader.readLine()
            while ( line != null) {
                text.append(line)
                text.append("\n")
                line = reader.readLine()
            }
        }catch ( e :IOException){
            e.printStackTrace()
        }

        return text.toString()
    }
}
