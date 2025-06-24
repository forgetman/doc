package compat.ext

import android.content.Intent
import android.os.Parcelable
import compat.intent.IntentCompat
import java.io.Serializable

inline fun <reified T : Serializable> IntentCompat.getSerializableExtra(intent: Intent, name: String): T? =
    getSerializableExtra(intent, name, T::class)

inline fun <reified T : Parcelable> IntentCompat.getParcelableExtra(intent: Intent, name: String): T? =
    getParcelableExtra(intent, name, T::class)

inline fun <reified T : Parcelable> IntentCompat.getParcelableArrayExtra(intent: Intent, name: String): Array<T>? =
    getParcelableArrayExtra(intent, name, T::class)

inline fun <reified T : Parcelable> IntentCompat.getParcelableArrayListExtra(intent: Intent, name: String): List<T>? =
    getParcelableArrayListExtra(intent, name, T::class)