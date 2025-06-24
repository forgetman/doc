package vector.widget.compat.viewpager.transformer

import android.view.View
import kotlin.math.abs
import kotlin.math.max

/**
 * 官方放大效果
 */
class ZoomOutTransformer : BaseTransformer() {

    companion object {
        private const val MIN_SCALE = 0.85f
        private const val MIN_ALPHA = 0.5f
    }

    override fun onLeft(v: View, position: Float) {
        v.alpha = 0f
    }

    override fun onTurn(v: View, position: Float) {
        val scaleFactor = max(MIN_SCALE, 1 - abs(position))

        val pageWidth = v.width
        val pageHeight = v.height

        val vertMargin = pageHeight * (1 - scaleFactor) / 2
        val horzMargin = pageWidth * (1 - scaleFactor) / 2
        if (position < 0) {
            v.translationX = horzMargin - vertMargin / 2
        } else {
            v.translationX = -horzMargin + vertMargin / 2
        }

        // Scale the page down (between MIN_SCALE and 1)
        v.scaleX = scaleFactor
        v.scaleY = scaleFactor

        // Fade the page relative to its size.
        v.alpha = MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA)
    }

    override fun onRight(v: View, position: Float) {
        v.alpha = 0f
    }
}