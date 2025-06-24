package vector.widget.style

import android.content.res.TypedArray
import android.graphics.PorterDuff
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.core.view.setPadding
import vector.app.androidview.R
import vector.app.ext.view.margin
import vector.app.ext.view.setHeight
import vector.app.ext.view.setWidth
import vector.widget.ext.obtainColorStateList
import vector.widget.ext.obtainDimensionPixelSize
import vector.widget.ext.obtainDrawable
import vector.widget.ext.obtainInt
import vector.widget.ext.obtainLayoutDimension

/**
 * @author yuansui
 * @since 2023/2/19
 */
internal class ViewStyleApplier(view: View) : StyleApplier<View>(view) {

    companion object {
        private const val PORTER_DUFF_MODE_SRC_OVER = 3
        private const val PORTER_DUFF_MODE_SRC_IN = 5
        private const val PORTER_DUFF_MODE_SRC_ATOP = 9
        private const val PORTER_DUFF_MODE_MULTIPLY = 14
        private const val PORTER_DUFF_MODE_SCREEN = 15
        private const val PORTER_DUFF_MODE_ADD = 16
    }

    override fun onInitialize(styleId: Int) {
        context.obtainStyledAttributes(styleId, R.styleable.LibsVectorCoreRedeclare_View).apply {
            setLayout(this)
            setMargin(this)
            setBackground(this)
            setPadding(this)
            setGravity(this)

            recycle()
        }
    }

    private fun setLayout(ta: TypedArray) {
        ta.obtainLayoutDimension(R.styleable.LibsVectorCoreRedeclare_View_android_layout_width) { value ->
            view.setWidth(value)
        }

        ta.obtainLayoutDimension(R.styleable.LibsVectorCoreRedeclare_View_android_layout_height) { value ->
            view.setHeight(value)
        }
    }

    private fun setMargin(ta: TypedArray) {
        var margin: Int? = null
        var marginVertical: Int? = null
        var marginTop: Int? = null
        var marginBottom: Int? = null
        var marginHorizontal: Int? = null
        var marginStart: Int? = null
        var marginEnd: Int? = null

        ta.obtainDimensionPixelSize(R.styleable.LibsVectorCoreRedeclare_View_android_layout_marginStart) { value ->
            marginStart = value
        }
        ta.obtainDimensionPixelSize(R.styleable.LibsVectorCoreRedeclare_View_android_layout_marginLeft) { value ->
            marginStart = value
        }
        ta.obtainDimensionPixelSize(R.styleable.LibsVectorCoreRedeclare_View_android_layout_marginEnd) { value ->
            marginEnd = value
        }
        ta.obtainDimensionPixelSize(R.styleable.LibsVectorCoreRedeclare_View_android_layout_marginRight) { value ->
            marginEnd = value
        }
        ta.obtainDimensionPixelSize(R.styleable.LibsVectorCoreRedeclare_View_android_layout_marginHorizontal) { value ->
            marginHorizontal = value
        }

        ta.obtainDimensionPixelSize(R.styleable.LibsVectorCoreRedeclare_View_android_layout_marginTop) { value ->
            marginTop = value
        }
        ta.obtainDimensionPixelSize(R.styleable.LibsVectorCoreRedeclare_View_android_layout_marginBottom) { value ->
            marginBottom = value
        }
        ta.obtainDimensionPixelSize(R.styleable.LibsVectorCoreRedeclare_View_android_layout_marginVertical) { value ->
            marginVertical = value
        }

        ta.obtainDimensionPixelSize(R.styleable.LibsVectorCoreRedeclare_View_android_layout_margin) { value ->
            margin = value
        }

        if (margin != null) {
            view.margin(margin, margin, margin, margin)
        } else {
            view.margin(
                start = marginHorizontal ?: marginStart,
                top = marginVertical ?: marginTop,
                end = marginHorizontal ?: marginEnd,
                bottom = marginVertical ?: marginBottom
            )
        }
    }

    private fun setBackground(ta: TypedArray) {
        ta.obtainDrawable(R.styleable.LibsVectorCoreRedeclare_View_android_background, context.theme) { drawable ->
            view.background = drawable
        }
        ta.obtainColorStateList(R.styleable.LibsVectorCoreRedeclare_View_android_backgroundTint, context.theme) { value ->
            ViewCompat.setBackgroundTintList(view, value)
        }
        ta.obtainInt(R.styleable.LibsVectorCoreRedeclare_View_android_backgroundTintMode) { value ->
            val mode: PorterDuff.Mode? = when (value) {
                PORTER_DUFF_MODE_SRC_OVER -> PorterDuff.Mode.SRC_OVER
                PORTER_DUFF_MODE_SRC_IN -> PorterDuff.Mode.SRC_IN
                PORTER_DUFF_MODE_SRC_ATOP -> PorterDuff.Mode.SRC_ATOP
                PORTER_DUFF_MODE_MULTIPLY -> PorterDuff.Mode.MULTIPLY
                PORTER_DUFF_MODE_SCREEN -> PorterDuff.Mode.SCREEN
                PORTER_DUFF_MODE_ADD -> PorterDuff.Mode.ADD
                else -> null
            }
            ViewCompat.setBackgroundTintMode(view, mode)
        }
    }

    private fun setPadding(ta: TypedArray) {
        var padding: Int? = null
        var paddingHorizontal: Int? = null
        var paddingStart: Int? = null
        var paddingEnd: Int? = null
        var paddingVertical: Int? = null
        var paddingTop: Int? = null
        var paddingBottom: Int? = null

        ta.obtainDimensionPixelSize(R.styleable.LibsVectorCoreRedeclare_View_android_paddingStart) { value ->
            paddingStart = value
        }
        ta.obtainDimensionPixelSize(R.styleable.LibsVectorCoreRedeclare_View_android_paddingLeft) { value ->
            paddingStart = value
        }
        ta.obtainDimensionPixelSize(R.styleable.LibsVectorCoreRedeclare_View_android_paddingEnd) { value ->
            paddingEnd = value
        }
        ta.obtainDimensionPixelSize(R.styleable.LibsVectorCoreRedeclare_View_android_paddingRight) { value ->
            paddingEnd = value
        }
        ta.obtainDimensionPixelSize(R.styleable.LibsVectorCoreRedeclare_View_android_paddingHorizontal) { value ->
            paddingHorizontal = value
        }

        ta.obtainDimensionPixelSize(R.styleable.LibsVectorCoreRedeclare_View_android_paddingTop) { value ->
            paddingTop = value
        }
        ta.obtainDimensionPixelSize(R.styleable.LibsVectorCoreRedeclare_View_android_paddingBottom) { value ->
            paddingBottom = value
        }
        ta.obtainDimensionPixelSize(R.styleable.LibsVectorCoreRedeclare_View_android_paddingVertical) { value ->
            paddingVertical = value
        }

        ta.obtainDimensionPixelSize(R.styleable.LibsVectorCoreRedeclare_View_android_padding) { value ->
            padding = value
        }

        val p = padding
        if (p != null) {
            view.setPadding(p)
        } else {
            view.setPadding(
                paddingHorizontal ?: paddingStart ?: view.paddingStart,
                paddingVertical ?: paddingTop ?: view.paddingTop,
                paddingHorizontal ?: paddingEnd ?: view.paddingEnd,
                paddingVertical ?: paddingBottom ?: view.paddingBottom,
            )
        }
    }

    private fun setGravity(ta: TypedArray) {
        ta.obtainInt(R.styleable.LibsVectorCoreRedeclare_View_android_layout_gravity) { value ->
            val params = view.layoutParams ?: return@obtainInt
            if (params is FrameLayout.LayoutParams) {
                params.gravity = value
                view.layoutParams = params
            } else if (params is LinearLayout.LayoutParams) {
                params.gravity = value
                view.layoutParams = params
            }
        }
    }
}