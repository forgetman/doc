package inject.compiler.processor

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import java.io.IOException
import java.util.Locale
import java.util.regex.Pattern
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic

object FormatSpecifiers {
    const val STRING: String = "%S"
    const val NAME: String = "%N"
    const val TYPE: String = "%T"
    const val VALUE: String = "%L"
}

/**
 * @author yuansui
 * @since 2017/7/28
 */
@Suppress("unused", "OPT_IN_USAGE")
abstract class BaseProcessor : AbstractProcessor() {

    private lateinit var elementUtils: Elements
    private lateinit var typeUtils: Types
    private lateinit var filer: Filer
    private lateinit var messager: Messager

    @Synchronized
    override fun init(env: ProcessingEnvironment) {
        super.init(env)

        elementUtils = env.elementUtils
        typeUtils = env.typeUtils
        filer = env.filer
        messager = env.messager
    }

    override fun process(annotations: MutableSet<out TypeElement?>?, env: RoundEnvironment): Boolean {
        for (annotatedElement in env.getElementsAnnotatedWith(getAnnotationClass())) {
            try {
                val spec = createTypeSpec(annotatedElement)
                brewKotlin(getPackageName(annotatedElement), spec)
            } catch (e: Exception) {
                printErr(
                    annotatedElement,
                    "Could not create builder for %s: %s",
                    annotatedElement.simpleName,
                    e.message
                )
            }
        }
        return false
    }

    @Throws(IOException::class)
    private fun brewKotlin(packageName: String, typeSpec: TypeSpec) {
        val builderFile = FileSpec.get(packageName, typeSpec)
        builderFile.writeTo(getFiler())
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String?> {
        return mutableSetOf(getAnnotationClass().canonicalName)
    }

    abstract fun getAnnotationClass(): Class<out Annotation>

    abstract fun createTypeSpec(annotatedElement: Element): TypeSpec

    fun getElementUtils(): Elements {
        return elementUtils
    }

    fun getTypeUtils(): Types {
        return typeUtils
    }

    fun getFiler(): Filer {
        return filer
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    /**
     * 获取是否有声明的注解
     *
     * @param e 元素
     * @param name 注解名字
     */
    fun hasAnnotation(e: Element, name: String?): Boolean {
        for (annotation in e.annotationMirrors) {
            if (annotation.annotationType.asElement().simpleName.toString() == name) {
                return true
            }
        }
        return false
    }

    fun getPackageName(e: Element): String {
        var e = e
        while (e !is PackageElement) {
            e = e.enclosingElement
        }
        return e.qualifiedName.toString()
    }

    fun <A : Annotation> getParamName(e: Element, value: String?): String {
        var ret = if (value != null && !value.trim { it <= ' ' }.isEmpty()) value else e.simpleName.toString()
        if (ret.length >= 2 && ret.startsWith("m")) {
            if (Pattern.compile("[A-Z]").matcher(ret.substring(1, 2)).matches()) {
                // 去掉m开头和首字母的大写
                val sub = ret.substring(1, 2)
                ret = ret.substring(1)
                ret = ret.replaceFirst(sub.toRegex(), sub.lowercase(Locale.getDefault()))
            }
        }

        return ret
    }

    fun isSubtype(var1: TypeMirror, var2: TypeMirror): Boolean {
        return typeUtils.isSubtype(var1, var2)
    }

    fun print(message: String) {
        messager.printMessage(Diagnostic.Kind.NOTE, message)
    }

    fun printErr(message: String) {
        messager.printMessage(Diagnostic.Kind.ERROR, message)
    }

    fun printErr(e: Element, msg: String, vararg args: Any?) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, *args), e)
    }

    fun upperCase(str: String): String {
        val ch = str.toCharArray()
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (ch[0].code - 32).toChar()
        }
        return String(ch)
    }
}
