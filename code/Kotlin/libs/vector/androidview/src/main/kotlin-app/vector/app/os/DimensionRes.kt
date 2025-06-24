package vector.app.os

import android.content.Context
import android.view.View
import androidx.annotation.DimenRes
import androidx.fragment.app.Fragment
import vector.app.config.Config
import vector.app.fitter.DpFitter
import vector.app.fitter.Mode

val Int.dimenRes get() = DimensionRes(this)

/**
 * @author yuansui
 * @since 2021/4/14
 */
class DimensionRes(@DimenRes private val resId: Int) {
    fun toPx(mode: Mode = Config.fit().mode): Int = DpFitter.get(mode).dimenRes(resId)
    fun toPx(context: Context?): Int = DpFitter.get(context).dimenRes(resId)
    fun toPx(fragment: Fragment?): Int = DpFitter.get(fragment?.context).dimenRes(resId)
    fun toPx(view: View?): Int = DpFitter.get(view?.context).dimenRes(resId)
}