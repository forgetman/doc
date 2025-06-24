@file:Suppress("unused")

package vector.app.ext

import android.view.Window
import androidx.annotation.ColorInt
import compat.window.NavigationBarCompat
import compat.window.StatusBarCompat
import compat.window.WindowCompat
import vector.app.os.IntRes
import vector.ext.isDark

/**
 * 设置使用沉浸式通知栏
 */
fun Window.flatStatusBar() {
    StatusBarCompat.flat(this)
}

/**
 * 设置底部导航栏颜色
 * 避免和系统的方法重名
 */
fun Window?.clearAndSetNavigationBarColor(res: IntRes) {
    this ?: return
    val color = res.getIntRelatedColor(context) ?: return
    NavigationBarCompat.setColor(this, color)
}

fun Window?.adaptStatusBarTextColorByBackground(@ColorInt bgColor: Int) {
    StatusBarCompat.adaptTextColorByBackground(this ?: return, bgColor)
}

fun Window?.setStatusBarTextColor(@ColorInt textColor: Int) {
    if (this == null) return
    if (textColor.isDark()) {
        StatusBarCompat.setTextColorDark(this)
    } else {
        StatusBarCompat.setTextColorLight(this)
    }
}

fun Window?.setStatusBarColor(@ColorInt color: Int) {
    StatusBarCompat.setColor(this ?: return, color)
}

fun Window.resize(width: Int, height: Int) {
    val attrs = attributes
    attrs.width = width
    attrs.height = height
    attributes = attrs
}

/**
 * 宽度获取及设置
 */
var Window.width: Int
    get() = attributes.width
    set(value) {
        val attrs = attributes
        attrs.width = value
        attributes = attrs
    }

/**
 * 高度获取及设置
 */
var Window.height: Int
    get() = attributes.height
    set(value) {
        val attrs = attributes
        attrs.height = value
        attributes = attrs
    }

fun Window.enterFullScreen() = WindowCompat.enterFullScreen(this)

fun Window.quitFullScreen() = WindowCompat.quitFullScreen(this, true)