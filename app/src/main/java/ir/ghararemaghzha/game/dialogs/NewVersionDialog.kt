package ir.ghararemaghzha.game.dialogs

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import ir.ghararemaghzha.game.databinding.DialogNewversionBinding

class NewVersionDialog(ctx: Context) : Dialog(ctx) {

    private lateinit var b:DialogNewversionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = DialogNewversionBinding.inflate(LayoutInflater.from(context))
        setContentView(b.root)
        onClicks()
    }

    private fun onClicks() {
        b.newversionDialogPlaystore.setOnClickListener {
            val appPackageName = context.packageName
            try {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
            } catch (e: Exception) {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
            }
        }

        b.newversionDialogDirect.setOnClickListener{
            val intent =  Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://ghararehmaghzha.ir/download/ghararehmaghzha.apk")
            context.startActivity(intent)
        }
    }
}
