package inject.processor.creator;

import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;

import inject.classname.AndroidClassName;
import inject.classname.OtherClassName;
import inject.classname.PrimitiveClassName;

/**
 * @author yuansui
 * @since 2020/9/8
 */
class CommonMethod extends BaseMethod {

    CommonMethod(CreatorProcessor processor, Element annotatedElement) {
        super(processor, annotatedElement);
    }

    MethodSpec constructor() {
        return MethodSpec.constructorBuilder()
                .addModifiers(PRIVATE)
                .build();
    }

    MethodSpec create(String fileName) {
        ClassName clzName = ClassName.get(processor.getPackageName(annotatedElement), fileName);

        /*
          生成create方法
         */
        String simpleName = clzName.simpleName();
        MethodSpec.Builder builder = MethodSpec.methodBuilder("create")
                .addModifiers(PUBLIC, STATIC)
                .addStatement(simpleName + " builder = new " + simpleName + "()")
                .returns(clzName);

        // 添加必须的参数
        for (Element e : requireFields) {
            String paramName = processor.getParamName(e);
            builder.addParameter(processor.createNullableParam(e, paramName));
            builder.addStatement("builder.$N = $N", paramName, paramName);
        }
        builder.addStatement("return builder");

        return builder.build();
    }

    /**
     * 根据optional生成链式调用的方法
     */
    List<MethodSpec> optionalSetters(String fileName) {
        List<MethodSpec> specs = new ArrayList<>();

        for (Element e : optFields) {
            String paramName = processor.getParamName(e);
            MethodSpec method = MethodSpec.methodBuilder(paramName)
                    .addModifiers(PUBLIC)
                    .addParameter(processor.createNullableParam(e, paramName))
//                    .addParameter(getTypeNameBox(e), paramName)
                    .addStatement("this.$N = $N", paramName, paramName)
                    .addStatement("return this")
                    .returns(ClassName.get(processor.getPackageName(annotatedElement), fileName))
                    .build();

            specs.add(method);
        }

        return specs;
    }

    MethodSpec newIntent() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("newIntent")
                .addModifiers(PUBLIC, STATIC)
                .addParameter(processor.createNonNullContext())
                .addStatement("$T intent = new Intent(context, $T.class)", AndroidClassName.INTENT, annotatedTypeName)
                .returns(AndroidClassName.INTENT);

        for (Element e : allFields) {
            String paramName = processor.getParamName(e);
            builder.addParameter(processor.createNullableParam(e, paramName));
        }
        addIntentStatement(builder, allFields);
        builder.addStatement("return intent");

        return builder.build();
    }

    MethodSpec inject() {
        /*
          生成objectHost调用的inject方法
         */
        MethodSpec.Builder builder = MethodSpec.methodBuilder("inject")
                .addModifiers(PUBLIC, STATIC)
                .addParameter(annotatedTypeName, "objectHost")
                .addParameter(AndroidClassName.BUNDLE, "extras");

        builder.beginControlFlow("if (extras == null)")
                .addStatement("return")
                .endControlFlow();

        for (Element e : allFields) {
            String paramName = processor.getParamName(e);
            char[] cs = paramName.toCharArray();

            // 判断是否boolean而且带有is声明, 需要去掉is
            TypeName tn = processor.getTypeName(e);
            if (tn.equals(TypeName.BOOLEAN) && paramName.startsWith("is")) {
                String realName = paramName.substring(2);
                builder.addStatement("objectHost.set$N(($T) extras.get($S))", realName, e.asType(), paramName);
            } else {
                cs[0] -= 32;
                String realName = new String(cs);
                if (tn.box().isBoxedPrimitive()
                        || tn.equals(PrimitiveClassName.STRING)) {
                    // Boolean Byte Short Int Long Char Float Double String Serialize
                    builder.addStatement("objectHost.set$N(($T) extras.get($S))", realName, e.asType(), paramName);
                } else {
                    builder.beginControlFlow("if (extras.containsKey($S))", paramName);

                    builder.addStatement("$T typeToken = new TypeToken<$T>(){}", OtherClassName.TYPE_TOKEN, e.asType());
                    builder.addStatement("$T type = typeToken.getType()", OtherClassName.TYPE);
                    processor.addEson(builder);
                    builder.addStatement("String params = (String) extras.get($S)", paramName);

                    builder.beginControlFlow("try")
                            .addStatement("objectHost.set$N(eson.fromJson(params, type))", realName)
                            .nextControlFlow("catch($T e)", OtherClassName.EXCEPTION)
                            .addStatement("e.printStackTrace()")
                            .endControlFlow();

                    builder.endControlFlow();
                }
            }

        }

        return builder.build();
    }

    private void addIntentStatement(MethodSpec.Builder builder, List<Element> elements) {
        for (Element e : elements) {
            String paramName = processor.getParamName(e);
            builder.beginControlFlow("if ($N != null)", paramName);
            TypeName typeName = processor.getTypeNameBox(e);
            if (typeName.isBoxedPrimitive()
                    || typeName.equals(PrimitiveClassName.STRING)) {
                // Boolean Byte Short Int Long Char Float Double String Serialize
                builder.addStatement("intent.putExtra($S, $N)", paramName, paramName);
            } else {
                processor.addEson(builder);
                builder.beginControlFlow("try");
                builder.addStatement("intent.putExtra($S, eson.toJson($N))", paramName, paramName);
                builder.nextControlFlow("catch($T e)", OtherClassName.EXCEPTION);
                builder.addStatement("e.printStackTrace()");
                builder.endControlFlow();
            }
            builder.endControlFlow();
        }
    }
}
