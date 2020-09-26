package ir.ghararemaghzha.game.activities

import android.animation.ObjectAnimator
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.classes.MySharedPreference

class SplashActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        setContentView(R.layout.activity_splash)

        ObjectAnimator.ofFloat(findViewById(R.id.splash_logo),"Alpha",0f,1f).setDuration(1300).start()
        ObjectAnimator.ofFloat(findViewById(R.id.splash_text),"Alpha",0f,1f).setDuration(1300).start()



        mediaPlayer = MediaPlayer.create(this, R.raw.intro)
        mediaPlayer.start()
    }

    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed({
            val userId = MySharedPreference.getInstance(this).userId

            if (userId.isEmpty())
                startActivity(Intent(this, RegisterActivity::class.java))
            else
                startActivity(Intent(this, MainActivity::class.java))

            this@SplashActivity.finish()
        }, 2000)

    }

    override fun onBackPressed() {}
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
    }
}