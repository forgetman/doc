package vector.os.weak

import android.os.Handler
import android.os.Looper
import android.os.Message
import java.lang.ref.WeakReference

/**
 * androidR以上, handler默认构造不推荐, 原因是如果当前线程没有looper的话, 会throw exception
 * 所以先以直接传入获取looper或者main looper的方式
 * 暂时没有更好的解决方案
 */
open class WeakHandler<T>(t: T, private val action: ((Message) -> Unit)? = null) :
    Handler(Looper.myLooper() ?: Looper.getMainLooper()) {

    private val ref: WeakReference<T> = WeakReference(t)
    protected val value: T?
        get() = ref.get()

    override fun handleMessage(msg: Message) {
        action?.invoke(msg)
    }

    override fun dispatchMessage(msg: Message) {
        if (value != null) super.dispatchMessage(msg)
    }
}