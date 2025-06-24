package vector.widget.compat.viewpager.transformer

import android.view.View

/**
 * 网上的旋转例子
 */
class RotateDownTransformer : BaseTransformer() {

    companion object {
        private const val ROTATE_MAX = 20.0f
    }

    private var rotate: Float = 0f

    override fun onLeft(v: View, position: Float) {
        v.rotation = 0f
    }

    override fun onTurn(v: View, position: Float) {
        if (position < 0) {
            rotate = ROTATE_MAX * position
            v.pivotX = v.measuredWidth * 0.5f
            v.pivotY = v.measuredHeight.toFloat()
            v.rotation = rotate
        } else {
            rotate = ROTATE_MAX * position
            v.pivotX = v.measuredWidth * 0.5f
            v.pivotY = v.measuredHeight.toFloat()
            v.rotation = rotate
        }
    }

    override fun onRight(v: View, position: Float) {
        v.rotation = 0f
    }
}