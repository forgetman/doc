package inject.compiler.processor

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import inject.annotation.creator.Creator
import inject.annotation.creator.Extra
import inject.compiler.classname.AndroidName
import inject.compiler.method.creator.ActivityMethod
import inject.compiler.method.creator.CommonMethod
import inject.compiler.method.creator.FragmentMethod
import inject.compiler.method.creator.ServiceMethod
import inject.compiler.ext.asTypeName
import inject.compiler.ext.getParamName
import javax.annotation.processing.Processor
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.type.TypeMirror

/**
 * @author yuansui
 * @since 2017/8/14
 */
@AutoService(Processor::class)
class CreatorProcessor : BaseProcessor() {

    override fun getAnnotationClass(): Class<out Annotation> {
        return Creator::class.java
    }

    override fun createTypeSpec(annotatedElement: Element): TypeSpec {
        val commonMethod = CommonMethod(this, annotatedElement)

        val annotatedMirror = annotatedElement.asType()

        val fileName = String.format("%sCreator", annotatedElement.simpleName)
        val builder = TypeSpec.Companion.classBuilder(fileName)
            .primaryConstructor(
                FunSpec.Companion.constructorBuilder()
                    .addModifiers(KModifier.PRIVATE)
                    .build()
            )

        // 添加成员变量
        val all = mutableListOf<Element>()
        getAnnotatedFields(annotatedElement, all)
        for (e in all) {
            val paramName = e.getParamName()
            builder.addProperty(
                PropertySpec.Companion.builder(paramName, e.asTypeName().copy(nullable = true))
                    .addModifiers(KModifier.PRIVATE)
                    .mutable()
                    .initializer("null")
                    .build()
            )
        }

        builder.addType(commonMethod.create(fileName))
        builder.addFunctions(commonMethod.optionalSetters(fileName))

        when {
            isSubtype(annotatedMirror, getElementTypeMirror(AndroidName.ACTIVITY)) -> {
                // activity
                builder.addFunction(commonMethod.newIntent())

                val method = ActivityMethod(this, annotatedElement)
                val c = annotatedElement.getAnnotation<Creator>(Creator::class.java)
                builder.addFunction(method.start(c.withTransition))
                if (c.forResult) {
                    builder.addFunction(method.startForResult())
                }
            }

            isSubtype(annotatedMirror, getElementTypeMirror(AndroidName.FRAGMENT)) -> {
                // fragment
                val method = FragmentMethod(this, annotatedElement)
                builder.addFunction(method.get())
            }

            isSubtype(annotatedMirror, getElementTypeMirror(AndroidName.SERVICE)) -> {
                // Service
                builder.addFunction(commonMethod.newIntent())

                val method = ServiceMethod(this, annotatedElement)
                builder.addFunction(method.start())
                builder.addFunction(method.startForeground())
//                builder.addType(method.stop())
            }
        }

        return builder.build()
    }

    private fun getAnnotatedFields(annotatedElement: Element, all: MutableList<Element>) {
        for (e in annotatedElement.enclosedElements) {
            val a = e.getAnnotation<Extra>(Extra::class.java)
            if (a != null) {
                all.add(e)
            }
        }

        val superTypes: MutableList<out TypeMirror?> = getTypeUtils().directSupertypes(annotatedElement.asType())
        val superClassType: TypeMirror? = if (!superTypes.isEmpty()) superTypes[0] else null
        val superClass = if (superClassType == null) null else getTypeUtils().asElement(superClassType)
        if (superClass != null && superClass.kind == ElementKind.CLASS) {
            getAnnotatedFields(superClass, all)
        }
    }

    fun getElementTypeMirror(name: CharSequence): TypeMirror {
        return getElementUtils().getTypeElement(name).asType()
    }

    fun getIntentParamsText(all: List<Element>): String {
        val sb = StringBuilder()
        sb.append("val intent = newIntent(context")
        for (e in all) {
            val paramName = e.getParamName()
            sb.append(",").append(paramName)
        }
        sb.append(")")
        return sb.toString()
    }
}