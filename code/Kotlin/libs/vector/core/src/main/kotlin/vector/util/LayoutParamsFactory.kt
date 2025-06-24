@file:Suppress("unused", "DEPRECATION")

package vector.util

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.WindowManager
import android.widget.AbsListView
import android.widget.AbsoluteLayout
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.gridlayout.widget.GridLayout

const val MATCH_PARENT = LayoutParams.MATCH_PARENT
const val WRAP_CONTENT = LayoutParams.WRAP_CONTENT
const val HORIZONTAL = 0
const val VERTICAL = 1


object LayoutParamsFactory {
    @JvmStatic
    fun relative(w: Int = MATCH_PARENT, h: Int = WRAP_CONTENT) = RelativeLayout.LayoutParams(w, h)

    @Suppress("DEPRECATION")
    @JvmStatic
    fun abs(w: Int = WRAP_CONTENT, h: Int = WRAP_CONTENT, x: Int = 0, y: Int = 0) =
        AbsoluteLayout.LayoutParams(w, h, x, y)

    @JvmStatic
    fun abs(w: Int = WRAP_CONTENT, h: Int = WRAP_CONTENT, x: Float = 0f, y: Float = 0f) =
        abs(w, h, x.toInt(), y.toInt())

    @JvmStatic
    fun linear(w: Int = MATCH_PARENT, h: Int = WRAP_CONTENT) = LinearLayout.LayoutParams(w, h)

    @JvmStatic
    fun frame(w: Int = MATCH_PARENT, h: Int = WRAP_CONTENT) = FrameLayout.LayoutParams(w, h)

    @JvmStatic
    fun constraint(w: Int = MATCH_PARENT, h: Int = MATCH_PARENT) = ConstraintLayout.LayoutParams(w, h)

    @JvmStatic
    fun constraint(left: Int, top: Int, right: Int, bottom: Int) =
        constraint().apply {
            leftToLeft = left
            topToTop = top
            rightToRight = right
            bottomToBottom = bottom
        }

    @JvmStatic
    fun listView(w: Int = MATCH_PARENT, h: Int = MATCH_PARENT) = AbsListView.LayoutParams(w, h)

    @JvmStatic
    fun scrollView(w: Int = MATCH_PARENT, h: Int = MATCH_PARENT) = ViewGroup.LayoutParams(w, h)

    @JvmStatic
    fun scrollView(w: Float = 0f, h: Float = 0f) = scrollView(w.toInt(), h.toInt())

    @JvmStatic
    fun viewGroup(w: Int = MATCH_PARENT, h: Int = WRAP_CONTENT) = ViewGroup.LayoutParams(w, h)

    @JvmStatic
    fun viewGroupMargin(w: Int = MATCH_PARENT, h: Int = WRAP_CONTENT) = ViewGroup.MarginLayoutParams(w, h)

    @JvmStatic
    fun grid(
        rowSpec: GridLayout.Spec = GridLayout.spec(GridLayout.UNDEFINED),
        columnSpec: GridLayout.Spec = GridLayout.spec(GridLayout.UNDEFINED),
        w: Int = WRAP_CONTENT,
        h: Int = WRAP_CONTENT
    ) = GridLayout.LayoutParams(rowSpec, columnSpec).apply {
        width = w
        height = h
    }

    fun window(w: Int = WRAP_CONTENT, h: Int = WRAP_CONTENT) = WindowManager.LayoutParams().apply {
        width = w
        height = h
    }
}

