package ir.ghararemaghzha.game.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayoutMediator
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.adapters.SlideAdapter
import ir.ghararemaghzha.game.classes.MySharedPreference
import ir.ghararemaghzha.game.classes.ZoomOutPageTransformer
import ir.ghararemaghzha.game.databinding.ActivitySlidesBinding

class SlidesActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var b: ActivitySlidesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivitySlidesBinding.inflate(layoutInflater)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        else
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(b.root)

        viewPager = findViewById(R.id.slides_viewpager)
        viewPager.setPageTransformer(ZoomOutPageTransformer())

        viewPager.adapter = SlideAdapter(this)
        TabLayoutMediator(findViewById(R.id.slides_tab), viewPager) { _, _ -> }.attach()

        b.slidesNext.setOnClickListener {
            viewPager.setCurrentItem(viewPager.currentItem + 1, true)
        }
        b.slidesPrev.setOnClickListener {
            viewPager.setCurrentItem(viewPager.currentItem - 1, true)
        }
        b.slidesBtn.setOnClickListener {
            MySharedPreference.getInstance(this).setSlides()
            val intent = Intent(this, MainActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        viewPager.registerOnPageChangeCallback(CallBack(b.slidesNext, b.slidesPrev, b.slidesBtn))
    }

    override fun onStop() {
        super.onStop()
        viewPager.unregisterOnPageChangeCallback(CallBack(b.slidesNext, b.slidesPrev, b.slidesBtn))
    }

    class CallBack(private val next: ImageView, private val prev: ImageView, private val btn: MaterialButton) : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            when (position) {
                0 -> {
                    btn.visibility = View.GONE
                    next.visibility = View.VISIBLE
                    prev.visibility = View.GONE
                }
                1, 2 -> {
                    btn.visibility = View.GONE

                    next.visibility = View.VISIBLE
                    prev.visibility = View.VISIBLE
                }
                3 -> {
                    btn.visibility = View.VISIBLE
                    next.visibility = View.GONE
                    prev.visibility = View.VISIBLE
                }
            }
        }
    }
}