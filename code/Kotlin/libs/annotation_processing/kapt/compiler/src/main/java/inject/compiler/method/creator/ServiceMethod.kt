package inject.compiler.method.creator

import com.squareup.kotlinpoet.FunSpec
import inject.compiler.classname.AndroidClassName
import inject.compiler.classname.VectorClassName
import inject.compiler.method.creator.BaseMethod
import inject.compiler.ext.createNullableContext
import inject.compiler.processor.FormatSpecifiers
import inject.compiler.processor.CreatorProcessor
import javax.lang.model.element.Element

/**
 * @author yuansui
 * @since 2020/9/8
 */
internal class ServiceMethod(
    processor: CreatorProcessor,
    annotatedElement: Element
) : BaseMethod<CreatorProcessor>(processor, annotatedElement) {

    fun start(): FunSpec {
        val builder = FunSpec.builder("start")
            .addParameter(createNullableContext())
            .addStatement("if(context == null) return")

        // 直接调用之前生成的newIntent()方法
        builder.addStatement(processor.getIntentParamsText(allFields), AndroidClassName.INTENT)

        // 直接调用Launcher, 减少生成的代码量
        builder.addStatement(
            "${FormatSpecifiers.TYPE}.startService(context, intent, null)",
            VectorClassName.LAUNCHER
        )

        return builder.build()
    }

    fun startForeground(): FunSpec {
        val builder: FunSpec.Builder = FunSpec.builder("startForeground")
            .addParameter(createNullableContext())
            .addStatement("if(context == null) return")

        // 直接调用之前生成的newIntent()方法
        builder.addStatement(processor.getIntentParamsText(allFields), AndroidClassName.INTENT)

        // 直接调用Launcher, 减少生成的代码量
        builder.addStatement(
            "${FormatSpecifiers.TYPE}.startForegroundService(context, intent, null)",
            VectorClassName.LAUNCHER
        )

        return builder.build()
    }

    // TODO: 暂时不添加stop方法, 因为只能有一个companion对象, 需要修改Processor的写法, 目前看来不太需要
//    fun stop(): TypeSpec {
//        val builder = TypeSpec.companionObjectBuilder()
//        builder.addFunction(
//            FunSpec.builder("stop")
//                .addParameter(createNullableContext())
//                .addStatement("if(context == null) return")
//                .addStatement(
//                    "val intent = Intent(context, ${FormatSpecifiers.NAME}.class)",
//                    annotatedTypeName
//                )
//                .addStatement("context.stopService(intent)")
//                .build()
//        )
//        return builder.build()
//    }
}
