package inject.compiler.method.creator

import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.FunSpec
import inject.compiler.classname.AndroidClassName
import inject.compiler.classname.VectorClassName
import inject.compiler.method.creator.BaseMethod
import inject.compiler.ext.createNonNullParam
import inject.compiler.ext.createNullableContext
import inject.compiler.processor.FormatSpecifiers
import inject.compiler.processor.CreatorProcessor
import javax.lang.model.element.Element

internal class ActivityMethod(
    processor: CreatorProcessor,
    annotatedElement: Element
) : BaseMethod<CreatorProcessor>(processor, annotatedElement) {

    fun start(withTransition: Boolean): FunSpec {
        val builder = FunSpec.builder("start")
            .addParameter(createNullableContext())
            .addStatement("if(context == null) return")

        if (withTransition) {
            builder.addParameter("callback", VectorClassName.ADDITIONAL_OPTIONS)
        }

        // 直接调用之前生成的newIntent()方法
        builder.addStatement(processor.getIntentParamsText(allFields), AndroidClassName.INTENT)

        // 直接调用Launcher, 减少生成的代码量
        if (withTransition) {
            builder.addStatement(
                "${FormatSpecifiers.TYPE}.startActivity(context, intent, null, callback)",
                VectorClassName.LAUNCHER
            )
        } else {
            builder.addStatement(
                "${FormatSpecifiers.TYPE}.startActivity(context, intent, null, null)",
                VectorClassName.LAUNCHER
            )
        }

        return builder.build()
    }

    fun startForResult(): FunSpec {
        val hostName = "objectHost"
        val builder = FunSpec.builder("startForResult")
            .addParameter(hostName, ANY)

        builder.addParameter(createNonNullParam(VectorClassName.ACTIVITY_RESULT_CALLBACK, "callback"))

        builder.addStatement("var context: Context? = null")
            .beginControlFlow("when($hostName)")
            .addStatement("is ${FormatSpecifiers.TYPE} -> context = $hostName", AndroidClassName.ACTIVITY)
            .addStatement("is ${FormatSpecifiers.TYPE} -> context = $hostName.context", AndroidClassName.FRAGMENT)
            .addStatement(
                "else -> throw ${FormatSpecifiers.TYPE}(\"$hostName must be one of activity or fragment\")",
                IllegalArgumentException::class.java
            )
            .endControlFlow()

        builder.beginControlFlow("if(context == null)")
            .addStatement(
                "throw ${FormatSpecifiers.TYPE}(\"$hostName must be one of activity or fragment\")",
                IllegalArgumentException::class.java
            )
            .endControlFlow()

        // 直接调用之前生成的newIntent()方法
        builder.addStatement(processor.getIntentParamsText(allFields), AndroidClassName.INTENT)

        // 直接调用Launcher, 减少生成的代码量
        builder.addStatement(
            "${FormatSpecifiers.TYPE}.registerForActivityResult(objectHost, intent, null, callback)",
            VectorClassName.LAUNCHER
        )

        return builder.build()
    }
}
