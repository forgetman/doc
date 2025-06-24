@file:Suppress("unused")

package vector.os.lifecycle

import android.os.Message
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import vector.os.weak.WeakHandler

class LifecycleHandler(owner: LifecycleOwner, action: ((Message) -> Unit)? = null) :
    WeakHandler<LifecycleOwner>(owner, action) {

    private val observer = object : LifecycleEventObserver {

        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                removeCallbacksAndMessages(null)
                value?.lifecycle?.removeObserver(this)
            }
        }
    }

    init {
        owner.lifecycle.addObserver(observer)
    }

    override fun dispatchMessage(msg: Message) {
        if (value?.lifecycle?.currentState != Lifecycle.State.DESTROYED) {
            super.dispatchMessage(msg)
        }
    }
}