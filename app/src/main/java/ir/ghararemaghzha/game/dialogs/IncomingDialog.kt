package ir.ghararemaghzha.game.dialogs

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.navigation.NavController
import com.google.android.material.textview.MaterialTextView
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.activities.SupportActivity
import ir.ghararemaghzha.game.classes.DateConverter
import ir.ghararemaghzha.game.models.MessageModel

class IncomingDialog(ctx: Context, navController: NavController, private val model: MessageModel) : Dialog(ctx) {
    private lateinit var title: MaterialTextView
    private lateinit var body: MaterialTextView
    private lateinit var date: MaterialTextView
    private val nv = navController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_incoming)

        findViewById<MaterialTextView>(R.id.incoming_back).setOnClickListener { dismiss() }

        title = findViewById(R.id.incoming_title)
        body = findViewById(R.id.incoming_body)
        date = findViewById(R.id.incoming_date)

        title.text = model.title
        linkGenerator(model.message.trim(), body)


        val d = model.date
        val dateConverter = DateConverter()
        dateConverter.gregorianToPersian(d.substring(0, 4).toInt(), d.substring(5, 7).toInt(), d.substring(8, 10).toInt())
        val convertedDate = "${dateConverter.year}/${dateConverter.month}/${dateConverter.day} ${d.substring(11, 16)}"
        date.text = convertedDate
    }

    private fun linkGenerator(body: String, view: MaterialTextView) {
        if (body.contains("<link>", true)) {
            val start = body.indexOf("<link>")
            val mainBody = body.substring(0, start - 1)
            val linkBody = when (body.substring(start + 6)) {
                "ScoreHelperFragment" -> "ورود به صفحه نحوه امتیاز گیری"
                "BuyFragment" -> "ورود به صفحه خرید"
                "HighscoreFragment" -> "ورود به صفحه کاربران برتر"
                "StartFragment" -> "ورود به صفحه مسابقه"
                "ProfileEditFragment" -> "ورود به صفحه ویرایش حساب کاربری"
                "SettingsFragment" -> "ورود به صفحه تنظیمات"
                "SupportActivity" -> "ورود به صفحه ارتباط با پشتیبانی"
                else -> "بستن"
            }
            if (linkBody == "بستن") {
                view.text = body
                return
            }
            val finalBody = "$mainBody\n$linkBody"
            val spannableString = SpannableString(finalBody)
            val clickableSpan: ClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    when (linkBody) {
                        "ورود به صفحه نحوه امتیاز گیری" -> nv.navigate(R.id.action_global_scoreHelperFragment)
                        "ورود به صفحه خرید" -> nv.navigate(R.id.action_menu_message_to_menu_buy)
                        "ورود به صفحه کاربران برتر" -> nv.navigate(R.id.action_menu_message_to_menu_highscore)
                        "ورود به صفحه مسابقه" -> nv.navigate(R.id.action_menu_message_to_menu_start)
                        "ورود به صفحه ویرایش حساب کاربری" -> nv.navigate(R.id.action_global_profileEditFragment)
                        "ورود به صفحه تنظیمات" -> nv.navigate(R.id.action_global_settingsFragment)
                        "ورود به صفحه ارتباط با پشتیبانی" -> context.startActivity(Intent(context, SupportActivity::class.java))
                    }
                    dismiss()
                }

                @Suppress("deprecation")
                override fun updateDrawState(ds: TextPaint) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        ds.color = context.resources.getColor(R.color.red, null)
                    else
                        ds.color = context.resources.getColor(R.color.red)
                    ds.isUnderlineText = true
                }
            }
            spannableString.setSpan(clickableSpan, mainBody.length, finalBody.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            view.text = spannableString
            view.movementMethod = LinkMovementMethod.getInstance()
        } else {
            view.text = body
        }
    }
}