package vector.util

import android.app.Activity
import android.app.Service
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import inject.annotation.creator.Creator
import logger.L
import sugar.util.ReflectUtil


/**
 * 注入工具类
 * FIXME 暂时使用java的机制
 *
 * @author yuansui
 * @since 2017/8/2
 */
object InjectUtil {
    private const val CREATOR = "Creator"
    private const val INJECT = "inject"

    fun bind(activity: Activity, intent: Intent? = null) {
        invokeBundle(activity, (intent ?: activity.intent)?.extras)
    }

    fun bind(frag: Fragment) {
        invokeBundle(frag, frag.arguments)
    }

    fun bind(service: Service, i: Intent) {
        invokeBundle(service, i.extras)
    }

    private fun invokeBundle(o: Any, b: Bundle?) {
        if (b == null) return

        val clz = o.javaClass
        if (clz.isAnnotationPresent(Creator::class.java)) {
            try {
                ReflectUtil.getMethod(
                    clz.name + CREATOR,
                    INJECT,
                    clz,
                    Bundle::class.java
                ).invoke(null, o, b)
            } catch (e: Exception) {
                L.e(e)
            }
        }
    }
}
