package vector.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import vector.os.lifecycle.LifecycleDispatcher

/**
 * @author yuansui
 * @since 2025/1/14
 */
open class LifecycleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), LifecycleOwner {

    private val lifecycleDispatcher = LifecycleDispatcher(this)

    override val lifecycle: Lifecycle
        get() = lifecycleDispatcher.lifecycle

    init {
        lifecycleDispatcher.postDispatchRunnable(Lifecycle.Event.ON_CREATE)
        lifecycleDispatcher.postDispatchRunnable(Lifecycle.Event.ON_START)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        lifecycleDispatcher.postDispatchRunnable(Lifecycle.Event.ON_RESUME)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        lifecycleDispatcher.postDispatchRunnable(Lifecycle.Event.ON_PAUSE)
    }
}