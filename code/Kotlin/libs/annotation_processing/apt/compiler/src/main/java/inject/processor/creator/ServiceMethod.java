package inject.processor.creator;

import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

import com.squareup.javapoet.MethodSpec;

import javax.lang.model.element.Element;

import inject.classname.AndroidClassName;
import inject.kotlin.KotlinClassName;

/**
 * @author yuansui
 * @since 2020/9/8
 */
class ServiceMethod extends BaseMethod {

    ServiceMethod(CreatorProcessor processor, Element annotatedElement) {
        super(processor, annotatedElement);
    }

    MethodSpec start() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("start")
                .addModifiers(PUBLIC)
                .addParameter(processor.createNullableContext())
                .addStatement("if(context == null) return");

        // 直接调用之前生成的newIntent()方法
        builder.addStatement(processor.getIntentParamsText(allFields), AndroidClassName.INTENT);

        // 直接调用Launcher, 减少生成的代码量
        builder.addStatement("$T.startService(context, intent, null)", KotlinClassName.LAUNCHER);

        return builder.build();
    }

    MethodSpec startForeground() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("startForeground")
                .addModifiers(PUBLIC)
                .addParameter(processor.createNullableContext())
                .addStatement("if(context == null) return");

        // 直接调用之前生成的newIntent()方法
        builder.addStatement(processor.getIntentParamsText(allFields), AndroidClassName.INTENT);

        // 直接调用Launcher, 减少生成的代码量
        builder.addStatement("$T.startForegroundService(context, intent, null)", KotlinClassName.LAUNCHER);

        return builder.build();
    }

    MethodSpec stop() {
        return MethodSpec.methodBuilder("stop")
                .addModifiers(PUBLIC, STATIC)
                .addParameter(processor.createNullableContext())
                .addStatement("if(context == null) return")
                .addStatement("$T intent = new Intent(context, $T.class)", AndroidClassName.INTENT, annotatedTypeName)
                .addStatement("context.stopService(intent)")
                .build();
    }

}
