package vector.widget.compat.viewpager.transformer

import android.view.View
import kotlin.math.abs

/**
 * 官方层叠效果
 */
class StackTransformer : BaseTransformer() {

    companion object {
        private const val MIN_SCALE = 0.75f
    }

    override fun onLeft(v: View, position: Float) {
        v.alpha = 0f
    }

    override fun onTurn(v: View, position: Float) {
        if (position <= 0) { // [-1,0]
            // Use the default slide transition when moving to the left page
            v.alpha = 1f
            v.translationX = 0f
            v.scaleX = 1f
            v.scaleY = 1f

        } else if (position <= 1) { // (0,1]
            // Fade the page out.
            v.alpha = 1 - position

            val pageWidth = v.width
            // Counteract the default slide transition
            v.translationX = pageWidth * -position

            // Scale the page down (between MIN_SCALE and 1)
            val scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - abs(position))
            v.scaleX = scaleFactor
            v.scaleY = scaleFactor
        }
    }

    override fun onRight(v: View, position: Float) {
        v.alpha = 0f
    }
}