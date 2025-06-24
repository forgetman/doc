package vector.widget.style

import android.content.res.TypedArray
import android.util.TypedValue
import android.widget.TextView
import androidx.annotation.StyleRes
import androidx.core.widget.TextViewCompat
import vector.app.androidview.R
import vector.widget.ext.obtainColorStateList
import vector.widget.ext.obtainDimension
import vector.widget.ext.obtainDimensionPixelSize
import vector.widget.ext.obtainInt

internal class TextStyleApplier(view: TextView) : StyleApplier<TextView>(view) {

    override fun applyParent(styleId: Int) {
        val applier = ViewStyleApplier(view)
        applier.applyStyle(styleId)
    }

    override fun onInitialize(styleId: Int) {
        context.obtainStyledAttributes(styleId, R.styleable.LibsVectorCoreRedeclare_TextView).apply {
            obtainInt(R.styleable.LibsVectorCoreRedeclare_TextView_android_gravity) { value ->
                view.gravity = value
            }

            obtainDimension(R.styleable.LibsVectorCoreRedeclare_TextView_android_textSize) { value ->
                view.setTextSize(TypedValue.COMPLEX_UNIT_PX, value)
            }

            obtainColorStateList(R.styleable.LibsVectorCoreRedeclare_TextView_android_textColor, context.theme) { value ->
                view.setTextColor(value)
            }

            setLine(this)

            recycle()
        }
    }

    private fun setLine(ta: TypedArray) {
        var height: Int? = null
        var extra: Float? = null
        var multiplier: Float? = null

        ta.obtainDimensionPixelSize(R.styleable.LibsVectorCoreRedeclare_TextView_android_lineHeight) { value ->
            height = value
        }

        ta.obtainDimension(R.styleable.LibsVectorCoreRedeclare_TextView_android_lineSpacingExtra) { value ->
            view.setLineSpacing(value, view.lineSpacingMultiplier)
            extra = value
        }

        ta.obtainDimension(R.styleable.LibsVectorCoreRedeclare_TextView_android_lineSpacingMultiplier) { value ->
            view.setLineSpacing(view.lineSpacingExtra, value)
            multiplier = value
        }

        val h = height
        if (h != null) {
            TextViewCompat.setLineHeight(view, h)
        } else {
            view.setLineSpacing(extra ?: view.lineSpacingExtra, multiplier ?: view.lineSpacingMultiplier)
        }
    }
}

fun TextView.applyStyle(@StyleRes styleId: Int) {
    val applier = TextStyleApplier(this)
    applier.applyStyle(styleId)
}