package inject.processor.creator;

import static javax.lang.model.element.Modifier.PUBLIC;

import com.squareup.javapoet.MethodSpec;

import javax.lang.model.element.Element;

import inject.classname.AndroidClassName;
import inject.classname.PrimitiveClassName;
import inject.kotlin.KotlinClassName;

/**
 * @author yuansui
 * @since 2020/10/13
 */
class JobIntentServiceMethod extends BaseMethod {

    JobIntentServiceMethod(CreatorProcessor processor, Element annotatedElement) {
        super(processor, annotatedElement);
    }

    MethodSpec start() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("start")
                .addModifiers(PUBLIC)
                .addParameter(processor.createNullableContext())
                .addParameter(PrimitiveClassName.INT, "jobId")
                .addStatement("if(context == null) return");

        // 直接调用之前生成的newIntent()方法
        builder.addStatement(processor.getIntentParamsText(allFields), AndroidClassName.INTENT);

        // 直接调用Launcher, 减少生成的代码量
        builder.addStatement("$T.startJobIntentService(context, $T.class, jobId, intent)", KotlinClassName.LAUNCHER, annotatedTypeName);

        return builder.build();
    }

}
