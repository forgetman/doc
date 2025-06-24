@file:Suppress("unused")

package vector.app.decor

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.IdRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import vector.app.androidview.R
import vector.app.ext.asStyle
import vector.app.ext.inflate
import vector.app.ext.view.ensureIdExist
import vector.app.ext.view.setOnDebounceClickListener

@Suppress("LeakingThis")
abstract class ErrorViewEx @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var listener: OnClickListener? = null
    abstract val layoutId: Int

    init {
        val v = context.inflate(layoutId)
        v.ensureIdExist()
        addView(v)

        ConstraintSet().asStyle {
            withTheme(v) {
                match()
            }
        }.applyToWithoutCustom(this)
    }

    fun retryWith(v: View?) {
        v.setOnDebounceClickListener {
            listener?.onClick(this)
        }
    }

    fun retryWith(@IdRes id: Int) {
        findViewById<View>(id).setOnDebounceClickListener {
            listener?.onClick(this)
        }
    }
}

class ErrorViewImpl @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ErrorViewEx(context, attrs, defStyleAttr) {

    override val layoutId: Int
        get() = R.layout.layout_net_error

    init {
        retryWith(this)
    }
}

