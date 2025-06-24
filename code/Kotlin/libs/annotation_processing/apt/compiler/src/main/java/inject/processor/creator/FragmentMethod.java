package inject.processor.creator;

import static javax.lang.model.element.Modifier.PUBLIC;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Element;

import inject.classname.AndroidClassName;
import inject.classname.OtherClassName;
import inject.classname.PrimitiveClassName;

/**
 * @author yuansui
 * @since 2020/9/8
 */
class FragmentMethod extends BaseMethod {

    public FragmentMethod(CreatorProcessor processor, Element annotatedElement) {
        super(processor, annotatedElement);
    }

    MethodSpec get() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("get")
                .addModifiers(PUBLIC)
                .addStatement("$T b = new $T()", AndroidClassName.BUNDLE, AndroidClassName.BUNDLE);

        addBundleStatement(builder);

        builder.addStatement("$T frag = new $T()", annotatedTypeName, annotatedTypeName)
                .addStatement("frag.setArguments(b)")
                .addStatement("return frag")
                .returns(annotatedTypeName);

        return builder.build();
    }

    private void addBundleStatement(MethodSpec.Builder builder) {
        for (Element e : allFields) {
            String paramName = processor.getParamName(e);
            builder.beginControlFlow("if ($N != null)", paramName);
            TypeName typeName = processor.getTypeNameBox(e);
            if (typeName.isBoxedPrimitive()) {
                // Long Boolean Integer...
                if (typeName.unbox() == TypeName.INT) {
                    builder.addStatement("b.put$N($S, $N)", "Int", paramName, paramName);
                } else {
                    builder.addStatement("b.put$T($S, $N)", typeName, paramName, paramName);
                }
            } else {
                if (typeName.equals(PrimitiveClassName.STRING)) {
                    builder.addStatement("b.putString($S, $N)", paramName, paramName);
                } else {
                    processor.addEson(builder);
                    builder.beginControlFlow("try");
                    builder.addStatement("b.putString($S, eson.toJson($N))", paramName, paramName);
                    builder.nextControlFlow("catch($T e)", OtherClassName.EXCEPTION);
                    builder.addStatement("e.printStackTrace()");
                    builder.endControlFlow();
                }
            }

            builder.endControlFlow();
        }
    }
}
