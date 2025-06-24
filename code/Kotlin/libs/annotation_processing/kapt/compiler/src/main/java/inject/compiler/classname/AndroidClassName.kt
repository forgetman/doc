package inject.compiler.classname

import com.squareup.kotlinpoet.ClassName

object AndroidClassName {
    val ACTIVITY = ClassName("android.app", "Activity")
    val FRAGMENT = ClassName("androidx.fragment.app", "Fragment")
    val CONTEXT = ClassName("android.content", "Context")
    val INTENT = ClassName("android.content", "Intent")
    val BUNDLE = ClassName("android.os", "Bundle")
    val PARCELABLE = ClassName("android.os", "Parcelable")
}