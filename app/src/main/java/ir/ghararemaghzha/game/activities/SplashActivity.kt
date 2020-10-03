package ir.ghararemaghzha.game.activities

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.AnimationSet
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
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
        anim()


        mediaPlayer = MediaPlayer.create(this, R.raw.intro)
        mediaPlayer.start()
    }

    private fun anim(){
        val scaleX=ObjectAnimator.ofFloat(findViewById(R.id.splash_logo), "scaleX", 0f, 1.5f,1f)
        val scaleY=ObjectAnimator.ofFloat(findViewById(R.id.splash_logo), "scaleY", 0f, 1.5f,1f)
        val alpha=ObjectAnimator.ofFloat(findViewById(R.id.splash_text), "Alpha", 0f,1f)
        scaleX.duration = 1500
        scaleY.duration = 1500
        alpha.duration=1500
        val x = AnimatorSet()
        x.playTogether(scaleX,scaleY,alpha)
        x.start()
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