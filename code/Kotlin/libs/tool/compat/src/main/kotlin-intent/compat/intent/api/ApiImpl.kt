package compat.intent.api

import android.content.Intent
import android.os.Parcelable
import java.io.Serializable
import kotlin.reflect.KClass

/**
 * @author yuansui
 * @since 2023/3/21
 */
@Suppress("DEPRECATION", "UNCHECKED_CAST")
internal class ApiImpl : Api {

    override fun <T : Serializable> getSerializableExtra(intent: Intent, name: String, clazz: KClass<T>): T? {
        return intent.getSerializableExtra(name) as? T?
    }

    override fun <T : Parcelable> getParcelableExtra(intent: Intent, name: String, clazz: KClass<T>): T? {
        return intent.getParcelableExtra(name)
    }

    override fun <T : Parcelable> getParcelableArrayExtra(intent: Intent, name: String, clazz: KClass<T>): Array<T>? {
        return intent.getParcelableArrayExtra(name) as? Array<T>?
    }

    override fun <T : Parcelable> getParcelableArrayListExtra(
        intent: Intent,
        name: String,
        clazz: KClass<T>
    ): List<T>? {
        return intent.getParcelableArrayListExtra(name)
    }
}