package inject.processor.creator;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Processor;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.TypeMirror;

import inject.annotation.creator.Creator;
import inject.annotation.creator.Extra;
import inject.classname.AndroidName;
import inject.classname.VectorName;
import inject.kotlin.KotlinClassName;
import inject.processor.BaseProcessor;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * @author yuansui
 * @since 2017/8/14
 */
@AutoService(Processor.class)
//@Incremental(IncrementalType.AGGREGATING)
public class CreatorProcessor extends BaseProcessor {

    @Override
    public Class<? extends Annotation> getAnnotationClass() {
        return Creator.class;
    }

    @Override
    public TypeSpec createTypeSpec(Element annotatedElement) {
        CommonMethod commonMethod = new CommonMethod(this, annotatedElement);

        TypeMirror annotatedMirror = annotatedElement.asType();

        final String fileName = String.format("%sCreator", annotatedElement.getSimpleName());
        TypeSpec.Builder builder = TypeSpec.classBuilder(fileName)
                .addModifiers(PUBLIC, FINAL);

        // 添加成员变量
        List<Element> all = new ArrayList<>();
        getAnnotatedFields(annotatedElement, all);
        for (Element e : all) {
            String paramName = getParamName(e);
            builder.addField(getTypeNameBox(e), paramName, PRIVATE);
        }

        builder.addMethod(commonMethod.constructor());
        builder.addMethod(commonMethod.create(fileName));
        builder.addMethods(commonMethod.optionalSetters(fileName));

        if (isSubtype(annotatedMirror, getElementTypeMirror(AndroidName.ACTIVITY))) {
            // activity
            builder.addMethod(commonMethod.newIntent());

            ActivityMethod method = new ActivityMethod(this, annotatedElement);
            Creator c = annotatedElement.getAnnotation(Creator.class);
            builder.addMethod(method.start(c.withTransition()));
            if (c.forResult()) {
                builder.addMethod(method.startForResult());
            }
        } else if (isSubtype(annotatedMirror, getElementTypeMirror(AndroidName.FRAGMENT))) {
            // fragment
            FragmentMethod method = new FragmentMethod(this, annotatedElement);
            builder.addMethod(method.get());
        } else if (isSubtype(annotatedMirror, getElementTypeMirror(AndroidName.SERVICE))) {
            // Service
            if (isSubtype(annotatedMirror, getElementTypeMirror(VectorName.SERVICE))) {
                builder.addMethod(commonMethod.newIntent());

                ServiceMethod method = new ServiceMethod(this, annotatedElement);
                builder.addMethod(method.start());
                builder.addMethod(method.startForeground());
                builder.addMethod(method.stop());
            } else if (isSubtype(annotatedMirror, getElementTypeMirror(VectorName.JOB_SERVICE))) {
                JobServiceMethod method = new JobServiceMethod(this, annotatedElement);
                builder.addMethod(method.start());
                builder.addMethod(method.start2());
            } else if (isSubtype(annotatedMirror, getElementTypeMirror(VectorName.JOB_INTENT_SERVICE))) {
                builder.addMethod(commonMethod.newIntent());

                JobIntentServiceMethod method = new JobIntentServiceMethod(this, annotatedElement);
                builder.addMethod(method.start());
            }
        }

        builder.addMethod(commonMethod.inject());

        return builder.build();
    }

    private void getAnnotatedFields(Element annotatedElement, List<Element> all) {
        for (Element e : annotatedElement.getEnclosedElements()) {
            Extra a = e.getAnnotation(Extra.class);
            if (a != null) {
                all.add(e);
            }
        }

        List<? extends TypeMirror> superTypes = getTypeUtils().directSupertypes(annotatedElement.asType());
        TypeMirror superClassType = !superTypes.isEmpty() ? superTypes.get(0) : null;
        Element superClass = superClassType == null ? null : getTypeUtils().asElement(superClassType);
        if (superClass != null && superClass.getKind() == ElementKind.CLASS) {
            getAnnotatedFields(superClass, all);
        }
    }

    TypeMirror getElementTypeMirror(CharSequence name) {
        return getElementUtils().getTypeElement(name).asType();
    }

    String getParamName(Element e) {
        return getParamName(e, null);
    }

    String getIntentParamsText(List<Element> all) {
        StringBuilder sb = new StringBuilder();
        sb.append("$T intent = newIntent(context");
        for (Element e : all) {
            String paramName = getParamName(e);
            sb.append(",").append(paramName);
        }
        sb.append(")");
        return sb.toString();
    }

    void addEson(MethodSpec.Builder builder) {
        builder.addStatement("$T eson = $T.getDefault()", KotlinClassName.ESON, KotlinClassName.ESON);
    }
}
