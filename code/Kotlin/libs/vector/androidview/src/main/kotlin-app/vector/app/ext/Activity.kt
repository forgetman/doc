@file:Suppress("unused")

package vector.app.ext

import android.app.Activity
import android.graphics.Point
import android.os.Build
import android.view.DisplayCutout
import android.view.Gravity
import android.view.View
import androidx.annotation.AnimRes
import androidx.annotation.RequiresApi
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast
import sugar.ext.isSdkLessThan
import vector.ext.systemBottom
import vector.app.ext.view.doOnApplyWindowInsets
import vector.app.os.IntRes

val Activity.androidContentView: View?
    get() = findViewById(android.R.id.content)

/**
 * 进入全屏显示模式
 */
fun Activity.enterFullScreen() = window.enterFullScreen()

/**
 * 退出全屏显示
 */
fun Activity.quitFullScreen() = window.quitFullScreen()

val Activity.displayCutout: DisplayCutout?
    @RequiresApi(Build.VERSION_CODES.P)
    get() = window.decorView.rootWindowInsets?.displayCutout

typealias OnNavigationStateChangedListener = (isOpen: Boolean, insetBottom: Int) -> Unit

/**
 * 监听全面屏手势是否开启, 导航栏高度会改变
 * 只有sdk30以下的才需要监听, 30以上的在activity已实现自动监听
 * @see [vector.design.ui.activity.BaseActivityEx.adaptWindowBottom]
 */
fun Activity.setOnFullScreenGestureChangedListener(listener: OnNavigationStateChangedListener) {
    if (isSdkLessThan(SdkInt.R_30)) {
        window.decorView.doOnApplyWindowInsets { _, insets, _ ->
            val bottom = insets.systemBottom
            listener(bottom == 0, bottom)
            insets
        }
    }
}

/**
 * 设置activity的window宽匹配屏幕宽度
 * @param gravity  对齐方式. 如 Gravity.CENTER
 */
fun Activity.matchScreenWidth(gravity: Int) {
    val m = windowManager
    // TODO: 待校验
    if (isSdkAtLeast(SdkInt.R_30)) {
        val x = m.currentWindowMetrics.bounds.left
        setWindowAttr(gravity, x)
    } else {
        @Suppress("DEPRECATION")
        val d = m.defaultDisplay
        val point = Point()
        @Suppress("DEPRECATION")
        d.getSize(point)
        setWindowAttr(gravity, point.x)
    }
}

fun Activity.setWindowAttr(gravity: Int = Gravity.CENTER, width: Int = -1, height: Int = -1) {
    val p = window.attributes
    p.width = width
    p.height = height
    p.gravity = gravity
    window.attributes = p
}

fun Activity.setNavigationBarColor(res: IntRes) {
    window.clearAndSetNavigationBarColor(res)
}

fun Activity.withOpenAnim(@AnimRes enter: Int, @AnimRes exit: Int) {
    if (isSdkAtLeast(SdkInt.U_34)) {
        overrideActivityTransition(Activity.OVERRIDE_TRANSITION_OPEN, enter, exit)
    } else {
        @Suppress("DEPRECATION")
        overridePendingTransition(enter, exit)
    }
}