package compat.intent.api

import android.content.Intent
import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import java.io.Serializable
import kotlin.reflect.KClass

/**
 * @author yuansui
 * @since 2023/3/21
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
internal class Api33Impl : Api by ApiImpl() {

    override fun <T : Serializable> getSerializableExtra(intent: Intent, name: String, clazz: KClass<T>): T? {
        return intent.getSerializableExtra(name, clazz.java)
    }

    override fun <T : Parcelable> getParcelableExtra(intent: Intent, name: String, clazz: KClass<T>): T? {
        return intent.getParcelableExtra(name, clazz.java)
    }

    override fun <T : Parcelable> getParcelableArrayExtra(intent: Intent, name: String, clazz: KClass<T>): Array<T>? {
        return intent.getParcelableArrayExtra(name, clazz.java)
    }

    override fun <T : Parcelable> getParcelableArrayListExtra(
        intent: Intent,
        name: String,
        clazz: KClass<T>
    ): List<T>? {
        return intent.getParcelableArrayListExtra(name, clazz.java)
    }
}