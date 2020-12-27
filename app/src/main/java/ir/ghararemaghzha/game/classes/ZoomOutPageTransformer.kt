package ir.ghararemaghzha.game.classes

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

class ZoomOutPageTransformer : ViewPager2.PageTransformer {
    private  val minScale = 0.85f
    private  val minAlpha = 0.5f
    override fun transformPage(view: View, position: Float) {
        view.apply {
            val pageWidth = width
            val pageHeight = height
            when {
                position < -1 -> { alpha = 0f }
                position <= 1 -> {
                    val scaleFactor = minScale.coerceAtLeast(1 - abs(position))
                    val verticalMargin = pageHeight * (1 - scaleFactor) / 2
                    val horizontalMargin = pageWidth * (1 - scaleFactor) / 2
                    translationX = if (position < 0)
                        horizontalMargin - verticalMargin / 2
                     else
                        horizontalMargin + verticalMargin / 2

                    scaleX = scaleFactor
                    scaleY = scaleFactor
                    alpha = (minAlpha +
                            (((scaleFactor - minScale) / (1 - minScale)) * (1 - minAlpha)))
                }
                else ->
                    alpha = 0f
            }
        }
    }
}