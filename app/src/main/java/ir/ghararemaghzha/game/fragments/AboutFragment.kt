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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.textview.MaterialTextView
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.classes.MySharedPreference
import ir.ghararemaghzha.game.classes.Utils
import ir.ghararemaghzha.game.data.ApiRepository
import ir.ghararemaghzha.game.data.NetworkApi
import ir.ghararemaghzha.game.data.RemoteDataSource
import ir.ghararemaghzha.game.data.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AboutFragment : Fragment(R.layout.fragment_about) {
    private lateinit var ctx: Context
    private lateinit var act: FragmentActivity
    private lateinit var text: MaterialTextView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ctx = requireContext()
        act = requireActivity()
        init(view)
    }

    private fun init(v: View) {
        act.findViewById<MaterialTextView>(R.id.toolbar_title).setText(R.string.about_title)
        text = v.findViewById(R.id.about_text)
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

        CoroutineScope(Dispatchers.IO).launch {
            getData()
        }

        onClicks(v)
    }

    private suspend fun getData() {
        val number = MySharedPreference.getInstance(ctx).getNumber()
        val token = MySharedPreference.getInstance(ctx).getAccessToken()
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(act, true)
        }
        when (val res = ApiRepository(RemoteDataSource().getApi(NetworkApi::class.java)).info("Bearer $token", number, "about")) {
            is Resource.Success -> {
                withContext(Dispatchers.Main) {
                    text.text = res.value.data
                }
            }
            is Resource.Failure -> {
                withContext(Dispatchers.Main) {
                    Toast.makeText(ctx, R.string.general_error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun onClicks(v: View) {
        v.findViewById<ImageView>(R.id.about_email).setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "support@ghararehmaghzha.ir", null))
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("ghararehmaghzha@radical-app.ir"))
            startActivity(Intent.createChooser(emailIntent, "ارسال ایمیل از طریق"))
        }
        v.findViewById<ImageView>(R.id.about_telegram).setOnClickListener { intentAction("https://t.me/ghararehmaghzha") }
        v.findViewById<ImageView>(R.id.about_instagram).setOnClickListener { intentAction("https://instagram.com/ghararehmaghzha") }
        v.findViewById<ImageView>(R.id.about_website) .setOnClickListener { intentAction("https://ghararehmaghzha.ir") }
    }

    private fun intentAction(id: String) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.data = Uri.parse(id)
        startActivity(intent)
    }
}