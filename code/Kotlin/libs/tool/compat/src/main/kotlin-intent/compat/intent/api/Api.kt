package compat.intent.api

import android.content.Intent
import android.os.Parcelable
import java.io.Serializable
import kotlin.reflect.KClass

/**
 * @author yuansui
 * @since 2023/3/21
 */
internal interface Api {

    fun <T : Serializable> getSerializableExtra(intent: Intent, name: String, clazz: KClass<T>): T?

    fun <T : Parcelable> getParcelableExtra(intent: Intent, name: String, clazz: KClass<T>): T?

    fun <T : Parcelable> getParcelableArrayExtra(intent: Intent, name: String, clazz: KClass<T>): Array<T>?

    fun <T : Parcelable> getParcelableArrayListExtra(intent: Intent, name: String, clazz: KClass<T>): List<T>?
}