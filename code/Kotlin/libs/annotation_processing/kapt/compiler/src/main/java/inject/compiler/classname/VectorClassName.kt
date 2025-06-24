package inject.compiler.classname

import com.squareup.kotlinpoet.ClassName

/**
 * @author yuansui
 * @since 2024/8/22
 */
object VectorClassName {
    val ESON = ClassName("eson", "Eson")
    val LAUNCHER = ClassName("vector.util", "Launcher")
    val ACTIVITY_RESULT_CALLBACK = ClassName("vector.app.delegate", "ActivityResultCallback")
    val ADDITIONAL_OPTIONS = ClassName("vector.util", "AdditionalOptions")
}