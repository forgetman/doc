package inject.compiler.method.creator

import inject.annotation.creator.Extra
import inject.compiler.ext.asTypeName
import inject.compiler.processor.BaseProcessor
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind

/**
 * @author yuansui
 * @since 2020/9/9
 */
abstract class BaseMethod<PROCESSOR : BaseProcessor>(
    protected val processor: PROCESSOR,
    protected val annotatedElement: Element
) {
    protected val annotatedTypeName = annotatedElement.asTypeName()

    protected val allFields = mutableListOf<Element>()
    protected val optFields = mutableListOf<Element>()
    protected val requireFields = mutableListOf<Element>()

    init {
        getAnnotatedFields(annotatedElement)
        allFields.addAll(optFields)
        allFields.addAll(requireFields)
    }

    private fun getAnnotatedFields(annotatedElement: Element) {
        for (e in annotatedElement.enclosedElements) {
            val a = e.getAnnotation<Extra>(Extra::class.java)
            if (a != null) {
                if (a.value) {
                    optFields.add(e)
                } else {
                    requireFields.add(e)
                }
            }
        }

        val superTypes = processor.getTypeUtils().directSupertypes(annotatedElement.asType())
        val superClassType = if (!superTypes.isEmpty()) superTypes[0] else null
        val superClass = if (superClassType == null) null else processor.getTypeUtils().asElement(superClassType)
        if (superClass != null && superClass.kind == ElementKind.CLASS) {
            getAnnotatedFields(superClass)
        }
    }
}