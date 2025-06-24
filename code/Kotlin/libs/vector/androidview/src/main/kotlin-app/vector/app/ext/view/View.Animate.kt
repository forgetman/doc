@file:Suppress("unused")

package vector.app.ext.view

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View
import android.view.ViewPropertyAnimator
import androidx.annotation.FloatRange


/**
 * View的属性动画工具
 * @deprecate 暂时保留, 不推荐深度使用
 */

private const val DURATION: Long = 300 // 和ViewAnimator的default duration一致

inline fun View.getAnimatorListener(
    crossinline onStart: (animation: Animator) -> Boolean = { _ -> false },
    crossinline onRepeat: (animation: Animator) -> Boolean = { _ -> false },
    crossinline onCancel: (animation: Animator) -> Boolean = { _ -> false },
    crossinline onEnd: (animation: Animator) -> Boolean = { _ -> false },
): Animator.AnimatorListener {
    return object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) {
            if (onStart(animation)) removeAnimationListener()
        }

        override fun onAnimationEnd(animation: Animator) {
            if (onEnd(animation)) removeAnimationListener()
        }

        override fun onAnimationCancel(animation: Animator) {
            if (onCancel(animation)) removeAnimationListener()
        }

        override fun onAnimationRepeat(animation: Animator) {
            if (onRepeat(animation)) removeAnimationListener()
        }
    }
}

fun View.removeAnimationListener() {
    animate().setListener(null)
}

fun View.fadeOut(duration: Long = DURATION, force: Boolean = false): ViewPropertyAnimator {
    if (force && alpha != 1f) alpha = 1f
    return animateAlpha(0f).setDuration(duration)
}

fun View.fadeIn(duration: Long = DURATION, force: Boolean = false): ViewPropertyAnimator {
    if (force && alpha != 0f) alpha = 0f
    return animateAlpha(1f).setDuration(duration)
}

fun View.rotate(degree: Int, duration: Long = DURATION): ViewPropertyAnimator {
    return animate().setDuration(duration).rotation(degree.toFloat())
}

/**
 * 设置透明度
 */
fun View.animateAlpha(@FloatRange(from = 0.0, to = 1.0) alpha: Float): ViewPropertyAnimator =
    animate().alpha(alpha)


fun View.animateScaleX(scaleX: Float, duration: Long = DURATION): ViewPropertyAnimator =
    animate().setDuration(duration).scaleX(scaleX)

fun View.animateScaleY(scaleY: Float, duration: Long = DURATION): ViewPropertyAnimator =
    animate().setDuration(duration).scaleY(scaleY)

fun View.animateScale(scale: Float, duration: Long = DURATION): ViewPropertyAnimator =
    animate().setDuration(duration).scaleX(scale).scaleY(scale)


/**
 * 增加v的宽度
 *
 * @param destWidth 目标宽度
 * @param duration
 */
fun View.width(destWidth: Int, duration: Long) {
    val widthWrapper = WidthWrapper(this)
    ObjectAnimator.ofInt(widthWrapper, "width", destWidth).setDuration(duration).start()
}

class WidthWrapper(private val targetView: View) {

    var width: Int
        get() = targetView.layoutParams.width
        set(width) {
            targetView.layoutParams.width = width
            targetView.requestLayout()
        }
}

/*********************************
 * 设置位移
 */

fun View.getTranslate(
    x: Float,
    y: Float,
    scale: Float = 1f,
    duration: Long = DURATION
): ViewPropertyAnimator {
    val animator = animate()
    animator.duration = duration
    animator.scaleX(scale)
    animator.scaleY(scale)
    val realX = x - (1.0f - scale) * width / 2
    val realY = y - (1.0f - scale) * height / 2
    animator.x(realX)
    animator.y(realY)

    return animator
}

fun View.setXY(x: Float, y: Float) {
    getTranslate(x, y, 1f, 0)
}

fun View.translationYBy(y: Float, duration: Long = DURATION) {
    animate().setDuration(duration).translationYBy(y).start()
}

fun View.translationY(y: Float, duration: Long = DURATION) {
    animate().setDuration(duration).translationY(y).start()
}
