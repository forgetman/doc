package inject.kotlin;

import com.squareup.javapoet.ClassName;

/**
 * @author yuansui
 * @since 2018/3/27
 */
public interface KotlinClassName {
    ClassName FUNCTION0 = ClassName.get("kotlin.jvm.functions", "Function0");
    ClassName FUNCTION1 = ClassName.get("kotlin.jvm.functions", "Function1");
    ClassName FUNCTION2 = ClassName.get("kotlin.jvm.functions", "Function2");
    ClassName UNIT = ClassName.get("kotlin", "Unit");
    ClassName PAIR = ClassName.get("kotlin", "Pair");

    ClassName ESON = ClassName.get("eson", "Eson");
    ClassName LAUNCHER = ClassName.get("vector.util", "Launcher");
    ClassName ACTIVITY_RESULT_CALLBACK = ClassName.get("vector.app.delegate", "ActivityResultCallback");
}
