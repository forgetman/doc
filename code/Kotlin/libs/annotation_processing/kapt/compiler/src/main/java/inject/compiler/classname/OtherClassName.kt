package inject.compiler.classname

import com.squareup.kotlinpoet.ClassName

/**
 * @author yuansui
 * @since 2019/11/18
 */
object OtherClassName {
    val TYPE_TOKEN = ClassName("com.google.gson.reflect", "TypeToken")
    val TYPE = ClassName("java.lang.reflect", "Type")
    val EXCEPTION = ClassName("java.lang", "Exception")
}
