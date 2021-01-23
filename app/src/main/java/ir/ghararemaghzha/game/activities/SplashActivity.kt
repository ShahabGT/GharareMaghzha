package ir.ghararemaghzha.game.activities

import android.animation.AnimatorSet
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
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.classes.MySharedPreference
import ir.ghararemaghzha.game.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var b: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivitySplashBinding.inflate(layoutInflater)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        else
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(b.root)
        anim()

        mediaPlayer = MediaPlayer.create(this, R.raw.intro)
        mediaPlayer.start()
    }

    private fun anim() {
        val scaleX = ObjectAnimator.ofFloat(b.splashLogo, "scaleX", 0f, 1.5f, 1f)
        val scaleY = ObjectAnimator.ofFloat(b.splashLogo, "scaleY", 0f, 1.5f, 1f)
        val alpha = ObjectAnimator.ofFloat(b.splashText, "Alpha", 0f, 1f)
        scaleX.duration = 1500
        scaleY.duration = 1500
        alpha.duration = 1500
        val x = AnimatorSet()
        x.playTogether(scaleX, scaleY, alpha)
        x.start()
    }

    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed({
            val userId = MySharedPreference.getInstance(this).getUserId()
            val slides = MySharedPreference.getInstance(this).getSlides()
            val intent: Intent

            intent = if (userId.isEmpty())
                Intent(this, RegisterActivity::class.java)
            else if (!slides)
                Intent(this, SlidesActivity::class.java)
            else
                Intent(this, MainActivity::class.java)

            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        }, 2000)
    }

    override fun onBackPressed() {}
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
    }
}