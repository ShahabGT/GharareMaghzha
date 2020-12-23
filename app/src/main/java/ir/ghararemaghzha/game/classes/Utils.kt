package ir.ghararemaghzha.game.classes

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.view.Gravity
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.pm.PackageInfoCompat
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import io.realm.Realm
import io.realm.kotlin.where
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.activities.MainActivity
import ir.ghararemaghzha.game.activities.SplashActivity
import ir.ghararemaghzha.game.activities.SupportActivity
import ir.ghararemaghzha.game.classes.Const.FCM_TOPIC
import ir.ghararemaghzha.game.classes.Const.GHARAREHMAGHZHA_BROADCAST_MESSAGE
import ir.ghararemaghzha.game.data.ApiRepository
import ir.ghararemaghzha.game.data.NetworkApi
import ir.ghararemaghzha.game.data.RemoteDataSource
import ir.ghararemaghzha.game.dialogs.GetDataDialog
import ir.ghararemaghzha.game.dialogs.NoInternetDialog
import ir.ghararemaghzha.game.dialogs.TimeDialog
import ir.ghararemaghzha.game.models.MessageModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class Utils {

    companion object {

        @Suppress("DEPRECATION")
        @JvmStatic
        fun checkInternet(ctx: Context): Boolean {
            var result = false
            val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cm.run {
                    cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                        result = when {
                            hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                            hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                            hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                            else -> false
                        }
                    }
                }
            } else {
                cm.run {
                    cm.activeNetworkInfo?.run {
                        if (type == ConnectivityManager.TYPE_WIFI || type == ConnectivityManager.TYPE_MOBILE)
                            result = true
                    }
                }
            }
            return result
        }

        @JvmStatic
        fun currentDate(): String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(Date())

        @JvmStatic
        fun isTimeAcceptable(serverDate: String): Boolean {
            var res = false
            val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
            val systemDate = dateFormat.format(Date())

            val d1 = dateFormat.parse(serverDate)
            val d2 = dateFormat.parse(systemDate)

            if (d1 != null && d2 != null) {
                val server = d1.time
                val system = d2.time
                val diff = server - system
                if (diff >= -60000 && diff <= 60000) {
                    res = true
                }
            }
            return res
        }

        fun getFbToken(ctx: Context): String {
            if (MySharedPreference.getInstance(ctx).getFbToken().isEmpty()) {
                FirebaseMessaging.getInstance().token.addOnCompleteListener { task: Task<String?> ->
                    if (task.isSuccessful) {
                        val token = task.result
                        MySharedPreference.getInstance(ctx).setFbToken(token ?: "")
                    }
                }
            }
            return MySharedPreference.getInstance(ctx).getFbToken()
        }

        fun hideKeyboard(act: Activity) {
            val inputManager = act.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (act.currentFocus != null) {
                inputManager.hideSoftInputFromWindow(act.currentFocus!!.windowToken,
                        InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }

        fun moneySeparator(text: String): String {
            var len = text.length
            return if (len > 3) {
                val res = StringBuilder()
                while (len > 3) {
                    res.insert(0, "," + text.substring(len - 3, len))
                    len -= 3
                }
                res.insert(0, text.substring(0, len))
                res.toString()
            } else {
                text
            }
        }

        fun convertToTimeFormat(millisecond: Long): String {
            val date = Date(millisecond)
            val formatter: DateFormat = SimpleDateFormat("mm:ss", Locale.ENGLISH)
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            return formatter.format(date)
        }

        @JvmStatic
        fun removeNotification(ctx: Context) = (ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(Const.NOTIFICATION_ID)

        @JvmStatic
        fun showTimeError(ctx: Context): TimeDialog {
            val dialog = TimeDialog(ctx)
            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(false)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
            dialog.window?.setGravity(Gravity.CENTER)
            dialog.show()
            val window = dialog.window
            window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            return dialog
        }

        @JvmStatic
        fun showGetDataLoading(ctx: Context): GetDataDialog {
            val dialog = GetDataDialog(ctx)
            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(false)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
            dialog.window?.setGravity(Gravity.CENTER)
            dialog.show()
            val window = dialog.window
            window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            return dialog
        }

        @JvmStatic
        fun showInternetError(ctx: Context, retry: RetryInterface) {
            val dialog = NoInternetDialog(ctx, retry)
            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(false)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
            dialog.window?.setGravity(Gravity.CENTER)
            dialog.show()
            val window = dialog.window
            window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        }

        @JvmStatic
        fun getVersionCode(ctx: Context) = PackageInfoCompat.getLongVersionCode(ctx.packageManager.getPackageInfo(ctx.packageName, 0)).toInt()

        @JvmStatic
        fun logout(act: Activity, showMessage: Boolean) {
            if (showMessage) Toast.makeText(act, act.getString(R.string.access_error), Toast.LENGTH_LONG).show()
            MySharedPreference.getInstance(act).clear()
            FirebaseMessaging.getInstance().unsubscribeFromTopic(FCM_TOPIC)
            val realm = Realm.getDefaultInstance()
            realm.beginTransaction()
            realm.deleteAll()
            realm.commitTransaction()
            act.startActivity(Intent(act, SplashActivity::class.java))
            act.finish()
        }

        fun getNextKey(db: Realm): Int {
            val number = db.where<MessageModel>().max("messageId")
            return if (number != null)
                number.toInt() + 1
            else
                0
        }

        @JvmStatic
        fun isEmailValid(email: String): Boolean {
            var email = email
            var res = false
            val emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
            val pattern = Pattern.compile(emailPattern)
            val matcher = pattern.matcher(email)
            if (matcher.matches()) {
                email = email.toLowerCase(Locale.ROOT)
                res = email.contains("@yahoo.") || email.contains("@gmail.") || email.contains("@aol.") || email.contains("@hotmail.") || email.contains("@ymail.") || email.contains("@live.")
            }
            return res
        }

        fun shareCode(ctx: Context, title: String, number: String = "") {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, title)
                putExtra(Intent.EXTRA_PHONE_NUMBER, number)
            }
            ctx.startActivity(Intent.createChooser(intent, ctx.getString(R.string.general_share)))
        }

        private fun createNotificationChannel(ctx: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name: CharSequence = "Ghararemaghzha Message Notifications"
                val description = "Using this channel to display notification for Ghararemaghzha game"
                val importance = NotificationManager.IMPORTANCE_HIGH
                val notificationChannel = NotificationChannel(Const.CHANNEL_CODE, name, importance)
                notificationChannel.description = description
                val notificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(notificationChannel)
            }
        }

        fun createNotification(ctx: Context, title: String, message: String, clickAction: String) {
            val intent: Intent = if (clickAction == "ir.ghararemaghzha.game.TARGET_NOTIFICATION")
                Intent(ctx, MainActivity::class.java).apply {
                    putExtra(GHARAREHMAGHZHA_BROADCAST_MESSAGE, "new")
                }
            else
                Intent(ctx, SupportActivity::class.java).apply {
                    putExtra(GHARAREHMAGHZHA_BROADCAST_MESSAGE, "chat")
                }

            val pendingIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            createNotificationChannel(ctx)
            val builder = NotificationCompat.Builder(ctx, Const.CHANNEL_CODE)
            builder.setSmallIcon(R.mipmap.ic_launcher)
            builder.setContentTitle(title)
            builder.setLargeIcon(BitmapFactory.decodeResource(ctx.resources, R.mipmap.ic_launcher))
            builder.setContentText(message)
            builder.setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            builder.setStyle(NotificationCompat.BigTextStyle().bigText(message))
            builder.priority = NotificationCompat.PRIORITY_MAX
            builder.setAutoCancel(true)
            builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            builder.setContentIntent(pendingIntent)
            val alarmSound = Uri.parse("android.resource://${ctx.packageName}/${R.raw.intro}")
            builder.setSound(alarmSound, AudioManager.STREAM_NOTIFICATION)
            builder.setVibrate(longArrayOf(2000, 1000, 2000))
            builder.setLights(Color.YELLOW, 1000, 1000)
            val notificationManagerCompat = NotificationManagerCompat.from(ctx)
            notificationManagerCompat.notify(Const.NOTIFICATION_ID, builder.build())
        }

        suspend fun updateScoreBooster(ctx: Context, count: Int) {
            val number = MySharedPreference.getInstance(ctx).getNumber()
            val token = MySharedPreference.getInstance(ctx).getAccessToken()
            if (number.isEmpty() || token.isEmpty()) {
                logout(ctx as Activity, true)
                return
            }
            ApiRepository(RemoteDataSource().getApi(NetworkApi::class.java)).scoreBooster("Bearer $token", number, count)
        }

    }

}