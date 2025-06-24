package inject.processor.creator;

import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.TypeMirror;

import inject.annotation.creator.Extra;

/**
 * @author yuansui
 * @since 2020/9/9
 */
abstract class BaseMethod {
    protected CreatorProcessor processor;
    protected Element annotatedElement;

    protected TypeName annotatedTypeName;

    protected List<Element> allFields = new ArrayList<>();
    protected List<Element> optFields = new ArrayList<>();
    protected List<Element> requireFields = new ArrayList<>();


    BaseMethod(CreatorProcessor processor, Element annotatedElement) {
        this.processor = processor;
        this.annotatedElement = annotatedElement;

        annotatedTypeName = processor.getTypeName(annotatedElement);

        getAnnotatedFields(annotatedElement);
        allFields.addAll(optFields);
        allFields.addAll(requireFields);
    }

    private void getAnnotatedFields(Element annotatedElement) {
        for (Element e : annotatedElement.getEnclosedElements()) {
            Extra a = e.getAnnotation(Extra.class);
            if (a != null) {
                if (a.value()) {
                    optFields.add(e);
                } else {
                    requireFields.add(e);
                }
            }
        }

        List<? extends TypeMirror> superTypes = processor.getTypeUtils().directSupertypes(annotatedElement.asType());
        TypeMirror superClassType = superTypes.size() > 0 ? superTypes.get(0) : null;
        Element superClass = superClassType == null ? null : processor.getTypeUtils().asElement(superClassType);
        if (superClass != null && superClass.getKind() == ElementKind.CLASS) {
            getAnnotatedFields(superClass);
        }
    }
}
