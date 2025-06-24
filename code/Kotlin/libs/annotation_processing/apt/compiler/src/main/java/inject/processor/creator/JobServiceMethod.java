package inject.processor.creator;

import static javax.lang.model.element.Modifier.PUBLIC;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.List;

import javax.lang.model.element.Element;

import inject.classname.AndroidClassName;
import inject.classname.OtherClassName;
import inject.classname.PrimitiveClassName;
import inject.kotlin.KotlinClassName;

/**
 * @author yuansui
 * @since 2020/10/12
 */
class JobServiceMethod extends BaseMethod {

    JobServiceMethod(CreatorProcessor processor, Element annotatedElement) {
        super(processor, annotatedElement);
    }

    MethodSpec start() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("start")
                .addModifiers(PUBLIC)
                .addParameter(processor.createNullableContext())
                .addParameter(PrimitiveClassName.INT, "jobId")
                .addParameter(ParameterizedTypeName.get(KotlinClassName.FUNCTION1,
                        AndroidClassName.JOB_INFO_BUILDER,
                        KotlinClassName.UNIT), "jobAttrs")
                .addStatement("if(context == null) return")
                .addStatement("$T bundle = null", AndroidClassName.BUNDLE);

        addParamsStatement(builder, allFields);

        // 直接调用Launcher, 减少生成的代码量
        builder.addStatement("$T.startJobService(context, $T.class, jobId, bundle, jobAttrs)", KotlinClassName.LAUNCHER, annotatedTypeName);

        return builder.build();
    }

    MethodSpec start2() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("start")
                .addModifiers(PUBLIC)
                .addParameter(processor.createNullableContext())
                .addParameter(PrimitiveClassName.INT, "jobId")
                .addStatement("start(context, jobId, null)");
        return builder.build();
    }

    /**
     * 参数解析
     */
    void addParamsStatement(MethodSpec.Builder builder, List<Element> elements) {
        int size = elements.size();
        if (size == 0) {
            return;
        }

        // 添加 SuppressWarnings, 泛型数组声明会有warning, 只能这么消除
        builder.addAnnotation(AnnotationSpec.builder(SuppressWarnings.class)
                .addMember("value", "$S", "unchecked")
                .build());
        builder.addStatement("$T<$T, $T>[] pairs = new Pair[$L]", KotlinClassName.PAIR, PrimitiveClassName.STRING, PrimitiveClassName.OBJECT, size);

        // 单独加一个eson声明
        processor.addEson(builder);

        for (int i = 0; i < size; ++i) {
            Element e = elements.get(i);
            String paramName = processor.getParamName(e);
            TypeName typeName = processor.getTypeNameBox(e);
            if (typeName.isBoxedPrimitive()
                    || typeName.equals(PrimitiveClassName.STRING)) {
                builder.addStatement("pairs[$L] = new Pair<>($S, $N)", i, paramName, paramName);
            } else {
                builder.beginControlFlow("try")
                        .addStatement("pairs[$L] = new Pair<>($S, eson.toJson($N))", i, paramName, paramName)
                        .nextControlFlow("catch($T e)", OtherClassName.EXCEPTION)
                        .addStatement("e.printStackTrace()")
                        .endControlFlow();
            }
        }

        builder.addStatement("bundle = $T.bundleOf(pairs)", AndroidClassName.BUNDLE_KT);
    }
}
