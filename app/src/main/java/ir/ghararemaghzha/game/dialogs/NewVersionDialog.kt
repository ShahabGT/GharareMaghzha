package ir.ghararemaghzha.game.dialogs

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import ir.ghararemaghzha.game.R

class NewVersionDialog(ctx: FragmentActivity) : Dialog(ctx) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_newversion)
        onClicks()
    }

    private fun onClicks() {
        findViewById<ImageView>(R.id.newversion_dialog_playstore).setOnClickListener {
            val appPackageName = context.packageName
            try {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
            } catch (e: Exception) {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
            }
        }

        findViewById<ImageView>(R.id.newversion_dialog_direct).setOnClickListener{
            val intent =  Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://ghararehmaghzha.ir/download/ghararehmaghzha.apk")
            context.startActivity(intent)
        }
    }
}
