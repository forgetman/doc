package vector.widget.compat.viewpager.transformer

import android.view.View
import kotlin.math.abs

/**
 * 透明度效果
 */
class GradientTransformer : BaseTransformer() {

    override fun onLeft(v: View, position: Float) {
        v.alpha = 0f
    }

    override fun onTurn(v: View, position: Float) {
        val alpha = 1 - abs(position)
        v.alpha = alpha
    }

    override fun onRight(v: View, position: Float) {
        v.alpha = 0f
    }
}