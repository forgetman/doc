package inject.processor;

import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;

import inject.classname.AndroidClassName;
import inject.classname.AnnotationClassName;

/**
 * @author yuansui
 * @since 2017/7/28
 */

@SuppressWarnings("unused")
public abstract class BaseProcessor extends AbstractProcessor {

    public interface Format {
        String STRING = "$S";
        String NAME = "$N";
        String TYPE = "$T";
        String VALUE = "$L";
    }

    private Elements elementUtils;
    private Types typeUtils;
    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);

        elementUtils = env.getElementUtils();
        typeUtils = env.getTypeUtils();
        filer = env.getFiler();
        messager = env.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        for (Element annotatedElement : env.getElementsAnnotatedWith(getAnnotationClass())) {
            try {
                TypeSpec spec = createTypeSpec(annotatedElement);
                brewJava(getPackageName(annotatedElement), spec);
            } catch (Exception e) {
                printErr(annotatedElement, "Could not create builder for %s: %s", annotatedElement.getSimpleName(), e.getMessage());
            }
        }
        return false;
    }

    private void brewJava(String packageName, TypeSpec typeSpec) throws IOException {
        JavaFile.Builder builder = JavaFile.builder(packageName, typeSpec);
        JavaFile builderFile = builder.build();
        builderFile.writeTo(getFiler());
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(getAnnotationClass().getCanonicalName());
    }

    public abstract Class<? extends Annotation> getAnnotationClass();

    public abstract TypeSpec createTypeSpec(Element annotatedElement);

    public Elements getElementUtils() {
        return elementUtils;
    }

    public Types getTypeUtils() {
        return typeUtils;
    }

    public Filer getFiler() {
        return filer;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * 获取是否有声明的注解
     *
     * @param e 元素
     * @param name 注解名字
     */
    public boolean hasAnnotation(Element e, String name) {
        for (AnnotationMirror annotation : e.getAnnotationMirrors()) {
            if (annotation.getAnnotationType().asElement().getSimpleName().toString().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public String getPackageName(Element e) {
        while (!(e instanceof PackageElement)) {
            e = e.getEnclosingElement();
        }
        return ((PackageElement) e).getQualifiedName().toString();
    }

    public <A extends Annotation> String getParamName(Element e, String val) {
        String ret = val != null && !val.trim().isEmpty() ? val : e.getSimpleName().toString();
        if (ret.length() >= 2 && ret.startsWith("m")) {
            if (Pattern.compile("[A-Z]").matcher(ret.substring(1, 2)).matches()) {
                // 去掉m开头和首字母的大写
                String sub = ret.substring(1, 2);
                ret = ret.substring(1);
                ret = ret.replaceFirst(sub, sub.toLowerCase());
            }
        }

        return ret;
    }

    public ParameterSpec createNonNullParam(Element e, String name) {
        TypeName typeName = getTypeNameBox(e);
        ParameterSpec.Builder builder = ParameterSpec.builder(typeName, name);
        builder.addAnnotation(AnnotationClassName.NON_NULL);
        return builder.build();
    }

    public ParameterSpec createNullableParam(Element e, String name) {
        TypeName typeName = getTypeNameBox(e);
        ParameterSpec.Builder builder = ParameterSpec.builder(typeName, name);
        builder.addAnnotation(AnnotationClassName.NULLABLE);
        return builder.build();
    }

    public ParameterSpec createNonNullParam(ClassName className, String name) {
        ParameterSpec.Builder builder = ParameterSpec.builder(className, name);
        builder.addAnnotation(AnnotationClassName.NON_NULL);
        return builder.build();
    }

    public ParameterSpec createNullableParam(ClassName className, String name) {
        ParameterSpec.Builder builder = ParameterSpec.builder(className, name);
        builder.addAnnotation(AnnotationClassName.NULLABLE);
        return builder.build();
    }

    public ParameterSpec createNonNullContext() {
        return createNonNullParam(AndroidClassName.CONTEXT, "context");
    }

    public ParameterSpec createNullableContext() {
        return createNullableParam(AndroidClassName.CONTEXT, "context");
    }

    public ParameterSpec createNonNullParam(Type typeName, String name) {
        ParameterSpec.Builder builder = ParameterSpec.builder(typeName, name);
        builder.addAnnotation(AnnotationClassName.NON_NULL);
        return builder.build();
    }

    /**
     * 获取类型名称(向上转型)
     */
    public TypeName getTypeNameBox(Element e) {
        return TypeName.get(e.asType()).box();
    }

    /**
     * 获取类型名称
     */
    public TypeName getTypeName(Element e) {
        return TypeName.get(e.asType());
    }

    public boolean isSubtype(TypeMirror var1, TypeMirror var2) {
        return typeUtils.isSubtype(var1, var2);
    }

    public void print(String message) {
        messager.printMessage(Kind.NOTE, message);
    }

    void printErr(String message) {
        messager.printMessage(Kind.ERROR, message);
    }

    void printErr(Element e, String msg, Object... args) {
        messager.printMessage(Kind.ERROR, String.format(msg, args), e);
    }

    public String upperCase(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }
}
