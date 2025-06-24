package compat.intent

import android.content.Intent
import android.os.Parcelable
import compat.intent.api.Api
import compat.intent.api.Api33Impl
import compat.intent.api.ApiImpl
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast
import java.io.Serializable
import kotlin.reflect.KClass

/**
 * @author yuansui
 * @since 2023/3/21
 */
object IntentCompat {

    private val api: Api = when {
        isSdkAtLeast(SdkInt.T_33) -> Api33Impl()
        else -> ApiImpl()
    }

    fun <T : Serializable> getSerializableExtra(intent: Intent, name: String, clazz: KClass<T>): T? =
        api.getSerializableExtra(intent, name, clazz)

    fun <T : Parcelable> getParcelableExtra(intent: Intent, name: String, clazz: KClass<T>): T? =
        api.getParcelableExtra(intent, name, clazz)

    fun <T : Parcelable> getParcelableArrayExtra(intent: Intent, name: String, clazz: KClass<T>): Array<T>? =
        api.getParcelableArrayExtra(intent, name, clazz)

    fun <T : Parcelable> getParcelableArrayListExtra(intent: Intent, name: String, clazz: KClass<T>): List<T>? =
        api.getParcelableArrayListExtra(intent, name, clazz)
}