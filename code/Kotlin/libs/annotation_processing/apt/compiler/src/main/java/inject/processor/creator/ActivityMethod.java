package inject.processor.creator;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;

import javax.lang.model.element.Element;

import inject.classname.AndroidClassName;
import inject.kotlin.KotlinClassName;

import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * @author yuansui
 * @since 2020/9/8
 */
class ActivityMethod extends BaseMethod {

    ActivityMethod(CreatorProcessor processor, Element annotatedElement) {
        super(processor, annotatedElement);
    }

    MethodSpec start(boolean withTransition) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("start")
                .addModifiers(PUBLIC)
                .addParameter(processor.createNullableContext())
                .addStatement("if(context == null) return");

        if (withTransition) {
            ParameterizedTypeName typeName = ParameterizedTypeName.get(KotlinClassName.FUNCTION0,
                    AndroidClassName.ACTIVITY_OPTIONS_COMPAT);
            builder.addParameter(typeName, "callback");
        }

        // 直接调用之前生成的newIntent()方法
        builder.addStatement(processor.getIntentParamsText(allFields), AndroidClassName.INTENT);

        // 直接调用Launcher, 减少生成的代码量
        if (withTransition) {
            builder.addStatement("$T.startActivity(context, intent, null, callback)", KotlinClassName.LAUNCHER);
        } else {
            builder.addStatement("$T.startActivity(context, intent, null, null)", KotlinClassName.LAUNCHER);
        }

        return builder.build();
    }

    MethodSpec startForResult() {
        String hostName = "objectHost";
        MethodSpec.Builder builder = MethodSpec.methodBuilder("startForResult")
                .addModifiers(PUBLIC)
                .addParameter(Object.class, hostName);

        builder.addParameter(processor.createNonNullParam(KotlinClassName.ACTIVITY_RESULT_CALLBACK, "callback"));

        builder.addStatement("Context context = null")
                .beginControlFlow("if ($N instanceof $T)", hostName, AndroidClassName.ACTIVITY)
                .addStatement("context = (Context) $N", hostName)
                .nextControlFlow("else if ($N instanceof $T)", hostName, AndroidClassName.FRAGMENT)
                .addStatement(" context = ((Fragment) $N).getContext()", hostName)
                .nextControlFlow("else")
                .addStatement("throw new $T(\"$N must be one of activity or fragment\")", IllegalArgumentException.class, hostName)
                .endControlFlow();


        // 直接调用之前生成的newIntent()方法
        builder.addStatement(processor.getIntentParamsText(allFields), AndroidClassName.INTENT);

        // 直接调用Launcher, 减少生成的代码量
        builder.addStatement("$T.registerForActivityResult(objectHost, intent, null, callback)", KotlinClassName.LAUNCHER);

        return builder.build();
    }
}
