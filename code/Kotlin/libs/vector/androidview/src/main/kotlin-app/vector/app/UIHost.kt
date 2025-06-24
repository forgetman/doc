package vector.app

import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import vector.app.decor.AppBarStyle

/**
 * UI布局公用
 */
interface UIHost {

    // 不能起名为view, 和[fragment]的view冲突
    val uiView: View?

    fun setBackgroundColor(@ColorInt color: Int) {
        uiView?.setBackgroundColor(color)
    }

    fun setBackgroundResource(@DrawableRes resId: Int) {
        uiView?.setBackgroundResource(resId)
    }

    fun setBackground(background: Drawable) {
        uiView?.background = background
    }

    fun getAppBarStyle(): AppBarStyle {
        return AppBarStyle.LINEAR
    }
}
