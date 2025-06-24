package vector.ext

import android.os.BaseBundle
import android.os.Bundle
import android.os.PersistableBundle
import androidx.core.os.bundleOf
import androidx.core.os.persistableBundleOf
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast

/**
 * @author yuansui
 * @since 2020/10/12
 */
/**
 * 最低版本21或以下的时候需要使用, 如果最低版本升级到22或以上, 可以删除
 * @author yuansui
 * @since 2020/10/12
 */
object PersistableBundleCompat {

    fun putBoolean(bundle: PersistableBundle, key: String?, value: Boolean) {
        if (isSdkAtLeast(SdkInt.L1_22)) {
            bundle.putBoolean(key, value)
        } else {
            bundle.putString(key, value.toString())
        }
    }

    fun getBoolean(
        bundle: PersistableBundle,
        key: String?,
        defaultValue: Boolean = false
    ): Boolean {
        return if (isSdkAtLeast(SdkInt.L1_22)) {
            bundle.getBoolean(key, defaultValue)
        } else {
            bundle.getString(key)?.toBoolean() ?: defaultValue
        }
    }

    fun putBooleanArray(bundle: PersistableBundle, key: String?, value: BooleanArray?) {
        if (isSdkAtLeast(SdkInt.L1_22)) {
            bundle.putBooleanArray(key, value)
        } else {
            if (value == null) {
                bundle.putStringArray(key, null)
            } else {
                val array = arrayOfNulls<String>(value.size)
                value.forEachIndexed { index, b ->
                    array[index] = b.toString()
                }
                bundle.putStringArray(key, array)
            }
        }
    }

    fun getBooleanArray(bundle: PersistableBundle, key: String?): BooleanArray? {
        return if (isSdkAtLeast(SdkInt.L1_22)) {
            bundle.getBooleanArray(key)
        } else {
            val array = bundle.getStringArray(key)
            if (array != null) {
                val new = BooleanArray(array.size)
                array.forEachIndexed { index, s ->
                    new[index] = s.toBoolean()
                }
                new
            } else {
                null
            }
        }
    }
}

@Suppress("unused")
fun buildBundle(action: Bundle.() -> Unit) = Bundle().apply(action)

fun <T : BaseBundle> T.toPairs(): List<Pair<String, Any?>> {
    val list = mutableListOf<Pair<String, Any?>>()
    keySet().forEach {
        @Suppress("DEPRECATION")
        list.add(Pair(it, get(it)))
    }
    return list
}

fun PersistableBundle.toBundle() = bundleOf(pairs = toPairs().toTypedArray())

fun PersistableBundle.getBooleanCompat(key: String?, defaultValue: Boolean = false) =
    PersistableBundleCompat.getBoolean(this, key, defaultValue)

fun PersistableBundle.getBooleanArrayCompat(key: String?) =
    PersistableBundleCompat.getBooleanArray(this, key)

/**
 * 参照PersistableBundle的拓展, 把里面Boolean相关的异常使用compat代替
 * 其他异常暂不处理
 * @see [persistableBundleOf]
 */
fun Bundle.toPersistableBundle(): PersistableBundle {
    val bundle = PersistableBundle()
    for ((key, value) in toPairs()) {
        when (value) {
            null -> bundle.putString(key, null) // Any nullable type will suffice.

            // Scalars
            is Boolean -> PersistableBundleCompat.putBoolean(bundle, key, value)
            is Double -> bundle.putDouble(key, value)
            is Int -> bundle.putInt(key, value)
            is Long -> bundle.putLong(key, value)

            // References
            is String -> bundle.putString(key, value)

            // Scalar arrays
            is BooleanArray -> PersistableBundleCompat.putBooleanArray(bundle, key, value)
            is DoubleArray -> bundle.putDoubleArray(key, value)
            is IntArray -> bundle.putIntArray(key, value)
            is LongArray -> bundle.putLongArray(key, value)

            // Reference arrays
            is Array<*> -> {
                val componentType = value::class.java.componentType!!
                @Suppress("UNCHECKED_CAST") // Checked by reflection.
                when {
                    String::class.java.isAssignableFrom(componentType) -> {
                        bundle.putStringArray(key, value as Array<String>)
                    }

                    else -> {
                        val valueType = componentType.canonicalName
                        throw IllegalArgumentException(
                            "Illegal value array type $valueType for key \"$key\""
                        )
                    }
                }
            }

            else -> {
                val valueType = value.javaClass.canonicalName
                throw IllegalArgumentException("Illegal value type $valueType for key \"$key\"")
            }
        }
    }

    return bundle
}