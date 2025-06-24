package vector.receiver

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope

enum class TimeTickUnit {
    MINUTE,
    HOURS
}

class TimeTickReceiver internal constructor(
    private val unit: TimeTickUnit,
    private val count: Long
) : BaseReceiver<TimeTickReceiver>() {

    fun interface Listener {
        fun onChanged(currentTimeMillis: Long)
    }

    private var listener: Listener? = null
    private var realCount: Int = 0

    override val filter: IntentFilter
        get() = IntentFilter(Intent.ACTION_TIME_TICK).apply { addAction(Intent.ACTION_TIME_CHANGED) }

    override fun onReceive(context: Context, intent: Intent) {
        if (isDestroyed()) return

        val curr = System.currentTimeMillis()

        // 如果是手动更改时间的回调, 直接重置状态
        if (intent.action == Intent.ACTION_TIME_CHANGED) {
            listener?.onChanged(curr)
            realCount = 0
            return
        }

        realCount++
        when (unit) {
            TimeTickUnit.MINUTE -> {
                if (realCount >= count) {
                    listener?.onChanged(curr)
                    realCount = 0
                }
            }

            TimeTickUnit.HOURS -> {
                if (realCount >= count * 60) {
                    listener?.onChanged(curr)
                    realCount = 0
                }
            }
        }
    }

    fun observe(context: Context?, owner: LifecycleOwner, listener: Listener) {
        register(context, owner)

        listener.onChanged(System.currentTimeMillis())
        this.listener = listener
    }

    fun observe(context: Context?, scope: CoroutineScope, listener: Listener) {
        register(context, scope)

        listener.onChanged(System.currentTimeMillis())
        this.listener = listener
    }

    fun registerListener(context: Context?, listener: Listener): Boolean {
        this.listener = listener
        listener.onChanged(System.currentTimeMillis())
        return register(context)
    }

    fun unregisterListener(context: Context?): Boolean {
        listener = null
        return unregister(context)
    }
}
