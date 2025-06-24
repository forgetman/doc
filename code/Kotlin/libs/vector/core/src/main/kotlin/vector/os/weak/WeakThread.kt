package vector.os.weak

import java.lang.ref.WeakReference

/**
 * @author yuansui
 * @since 2019/4/13 0013
 * TODO: 几乎不怎么使用Thread了. 过一段时间考虑删除
 */
class WeakThread<T>(t: T, private val block: () -> Unit) : Thread() {

    private val ref: WeakReference<T> = WeakReference(t)

    override fun run() {
        if (ref.get() != null) block()
    }
}