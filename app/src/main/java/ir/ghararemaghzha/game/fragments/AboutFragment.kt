package ir.ghararemaghzha.game.fragments

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.textview.MaterialTextView
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.classes.MySharedPreference
import ir.ghararemaghzha.game.classes.Utils
import ir.ghararemaghzha.game.data.Resource
import ir.ghararemaghzha.game.databinding.FragmentAboutBinding
import ir.ghararemaghzha.game.viewmodels.AboutViewModel

class AboutFragment : BaseFragment<AboutViewModel, FragmentAboutBinding>() {
    private lateinit var number: String
    private lateinit var token: String

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()

        viewModel.infoResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is Resource.Success -> {
                    b.aboutText.text = res.value.data
                }
                is Resource.Failure -> {
                    Toast.makeText(context, R.string.general_error, Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                }
            }
        })
    }

    private fun init() {
        requireActivity().findViewById<MaterialTextView>(R.id.toolbar_title).setText(R.string.about_title)

        number = MySharedPreference.getInstance(requireContext()).getNumber()
        token = MySharedPreference.getInstance(requireContext()).getAccessToken()
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(requireActivity(), true)
            return
        }

        val tradeMarkText = getString(R.string.about_trademark1)
        val spannableString = SpannableString(tradeMarkText)
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent().also {
                    it.action = Intent.ACTION_VIEW
                    it.data = Uri.parse("http://tajrannoyan.com")
                })
            }

            @Suppress("deprecation")
            override fun updateDrawState(ds: TextPaint) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    ds.color = resources.getColor(R.color.red, null)
                else
                    ds.color = resources.getColor(R.color.red)

                ds.isUnderlineText = true
            }
        }
        spannableString.setSpan(clickableSpan, 37, 55, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        b.aboutTrademark1.text = spannableString
        b.aboutTrademark1.movementMethod = LinkMovementMethod.getInstance()

        val tradeMarkText2 = getString(R.string.about_trademark2)
        val spannableString2 = SpannableString(tradeMarkText2)
        val clickableSpan2: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent().also {
                    it.action = Intent.ACTION_VIEW
                    it.data = Uri.parse("https://shahabazimi.ir")
                })
            }

            @Suppress("deprecation")
            override fun updateDrawState(ds: TextPaint) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    ds.color = resources.getColor(R.color.red, null)
                else
                    ds.color = resources.getColor(R.color.red)
                ds.isUnderlineText = true
            }
        }
        spannableString2.setSpan(clickableSpan2, 36, 46, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        b.aboutTrademark2.text = spannableString2
        b.aboutTrademark2.movementMethod = LinkMovementMethod.getInstance()


        val pInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
        val version = pInfo.versionName
        b.aboutTrademark3.text = getString(R.string.about_trademark3, version)

        viewModel.getInfoHistory("Bearer $token", number)

        onClicks()
    }

    private fun onClicks() {
        b.aboutEmail.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "support@ghararehmaghzha.ir", null)).also {
                it.putExtra(Intent.EXTRA_EMAIL, "support@ghararehmaghzha.ir")
            }
            startActivity(Intent.createChooser(emailIntent, "ارسال ایمیل از طریق"))
        }
        b.aboutTelegram.setOnClickListener { intentAction("https://t.me/ghararehmaghzha") }
        b.aboutInstagram.setOnClickListener { intentAction("https://instagram.com/ghararehmaghzha") }
        b.aboutWebsite.setOnClickListener { intentAction("https://ghararehmaghzha.ir") }
    }

    private fun intentAction(id: String) =
            startActivity(Intent().also {
                it.action = Intent.ACTION_VIEW
                it.data = Uri.parse(id)
            })

    override fun getViewModel() = AboutViewModel::class.java

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentAboutBinding.inflate(inflater, container, false)
}