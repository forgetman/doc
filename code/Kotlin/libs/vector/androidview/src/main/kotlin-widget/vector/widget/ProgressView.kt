package vector.widget

import android.content.Context
import android.util.AttributeSet
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.AppCompatImageView
import vector.app.androidview.R

/**
 * 其实是loading view
 *
 * @author yuansui
 */
class ProgressView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    companion object {
        internal const val DEFAULT_DURATION = 500L
    }

    private var isForcePaused = false // 是否启用过
    private val anim: Animation

    init {
        scaleType = ScaleType.FIT_XY

        anim = AnimationUtils.loadAnimation(context, R.anim.rotate_infinite)
        anim.duration = DEFAULT_DURATION
    }

    fun start() {
        stop()
        startAnimation(anim)
    }

    fun stop() {
        if (animation != null) {
            isForcePaused = false
            clearAnimation()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        if (animation != null) {
            isForcePaused = true
            clearAnimation()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        /**
         * 有时候recyclerView会在移除的时候自动停止动画
         * 所以回来的时候需要自动恢复
         */
        if (isForcePaused) start()
    }

    fun setDuration(duration: Long) {
        anim.duration = duration
    }
}
