package vector.receiver

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.LifecycleOwner

/**
 * @author yuansui
 * @since 2019-05-09
 */
class ScreenOnOffReceiver internal constructor() : BaseReceiver<ScreenOnOffReceiver>() {

    fun interface Listener {
        fun onChanged(on: Boolean)
    }

    private var listener: Listener? = null

    override val filter: IntentFilter
        get() = IntentFilter(Intent.ACTION_SCREEN_ON).apply {
            addAction(Intent.ACTION_SCREEN_OFF)
        }

    override fun onReceive(context: Context, intent: Intent) {
        if (isDestroyed()) return

        if (intent.action == Intent.ACTION_SCREEN_ON) {
            listener?.onChanged(true)
        } else {
            listener?.onChanged(false)
        }
    }

    fun observe(context: Context?, owner: LifecycleOwner, listener: Listener) {
        this.listener = listener
        register(context, owner)
    }

    fun registerListener(context: Context?, listener: Listener): Boolean {
        this.listener = listener
        return register(context)
    }

    fun unregisterListener(context: Context?): Boolean {
        listener = null
        return unregister(context)
    }
}