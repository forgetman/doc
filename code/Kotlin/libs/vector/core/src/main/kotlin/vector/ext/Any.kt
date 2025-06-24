@file:Suppress("unused")

package vector.ext

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import sugar.collection.MapList
import java.lang.reflect.Field

/**
 * 复制属性名称和类都相同的对象
 * @param toCopy 需要复制的对象
 */
inline fun <R : Any, reified T : R> T.copyFields(toCopy: R) {
    val copyFields = toCopy.javaClass.declaredFields
    val fields = MapList<String, Field>()
    var tmpClz: Class<in T>? = javaClass
    while (tmpClz != null && tmpClz != Any::class.java) {
        tmpClz.declaredFields.forEach {
            fields.add(it.name, it)
        }
        tmpClz = tmpClz.superclass
    }

    copyFields.runCatching {
        forEach {
            val f = fields.get(it.name) ?: return@forEach
            f.isAccessible = true
            it.isAccessible = true
            f.set(this, it.get(toCopy))
        }
    }
}

fun postRunnable(looper: Looper? = null, runnable: () -> Unit) {
    if (looper == null) Handler(
        Looper.myLooper() ?: Looper.getMainLooper()
    ).post(runnable) else Handler(looper).post(runnable)
}

internal const val NO_GETTER: String = "Property does not have a getter"
internal const val NO_SETTER: String = "Property does not have a setter"
internal fun noGetter(): Nothing = throw Exception("Property does not have a getter")
internal fun noSetter(): Nothing = throw Exception("Property does not have a setter")

fun LayoutInflater.inflate(@LayoutRes id: Int): View = inflate(id, null)