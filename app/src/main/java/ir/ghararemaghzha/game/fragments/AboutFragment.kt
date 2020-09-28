package ir.ghararemaghzha.game.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.textview.MaterialTextView
import ir.ghararemaghzha.game.R
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class AboutFragment : Fragment(R.layout.fragment_about) {
    private lateinit var ctx: Context
    private lateinit var act: FragmentActivity


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ctx = requireContext()
        act = requireActivity()
        init(view)
    }


    private fun init(v: View) {
        (act.findViewById<View>(R.id.toolbar_title) as MaterialTextView).setText(R.string.about_title)
        (v.findViewById(R.id.about_text) as MaterialTextView).text = getAboutText()

        val tradeMark: MaterialTextView = v.findViewById(R.id.about_trademark1)
        val tradeMark2: MaterialTextView = v.findViewById(R.id.about_trademark2)
        val tradeMark3: MaterialTextView = v.findViewById(R.id.about_trademark3)

        val tradeMarkText = ctx.getString(R.string.about_trademark1)
        val spannableString = SpannableString(tradeMarkText)
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent()
                intent.action = Intent.ACTION_VIEW
                intent.data = Uri.parse("http://tajrannoyan.com")
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    ds.color = resources.getColor(R.color.red, null)
                else
                    ds.color = resources.getColor(R.color.red)

                ds.isUnderlineText = true
            }
        }
        spannableString.setSpan(clickableSpan, 37, 55, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tradeMark.text = spannableString
        tradeMark.movementMethod = LinkMovementMethod.getInstance()

        val tradeMarkText2 = ctx.getString(R.string.about_trademark2)
        val spannableString2 = SpannableString(tradeMarkText2)
        val clickableSpan2: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent()
                intent.action = Intent.ACTION_VIEW
                intent.data = Uri.parse("https://shahabazimi.ir")
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    ds.color = resources.getColor(R.color.red, null)
                else
                    ds.color = resources.getColor(R.color.red)
                ds.isUnderlineText = true
            }
        }
        spannableString2.setSpan(clickableSpan2, 36, 46, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tradeMark2.text = spannableString2
        tradeMark2.movementMethod = LinkMovementMethod.getInstance()


        val pInfo = ctx.packageManager.getPackageInfo(ctx.packageName, 0)
        val version = pInfo.versionName
        tradeMark3.text = getString(R.string.about_trademark3, version)

        onClicks(v)
    }

    private fun getAboutText(): String {
        val inputStream = ctx.resources.openRawResource(R.raw.about)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val text = StringBuilder()
        var line: String?
        try {
            while (reader.readLine().also { line = it } != null) {
                text.append(line)
                text.append("\n")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return text.toString()
    }


    private fun onClicks(v: View) {
        (v.findViewById(R.id.about_email) as ImageView).setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "support@ghararehmaghzha.ir", null))
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("ghararehmaghzha@radical-app.ir"))
            startActivity(Intent.createChooser(emailIntent, "ارسال ایمیل از طریق"))
        }
        (v.findViewById(R.id.about_telegram) as ImageView).setOnClickListener { intentAction("https://t.me/ghararehmaghzha") }
        (v.findViewById(R.id.about_instagram) as ImageView).setOnClickListener { intentAction("https://instagram.com/ghararehmaghzha") }
        (v.findViewById(R.id.about_website) as ImageView).setOnClickListener { intentAction("https://ghararehmaghzha.ir") }
    }

    private fun intentAction(id: String) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.data = Uri.parse(id)
        startActivity(intent)
    }
}