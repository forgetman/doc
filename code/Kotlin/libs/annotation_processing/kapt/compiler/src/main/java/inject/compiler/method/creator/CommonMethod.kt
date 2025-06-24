package inject.compiler.method.creator

import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import inject.compiler.classname.AndroidClassName
import inject.compiler.classname.OtherClassName
import inject.compiler.method.creator.BaseMethod
import inject.compiler.ext.addEson
import inject.compiler.ext.asTypeName
import inject.compiler.ext.createNonNullContext
import inject.compiler.ext.createNullableParam
import inject.compiler.ext.getParamName
import inject.compiler.ext.isParcelable
import inject.compiler.ext.isPrimitive
import inject.compiler.ext.isSerializable
import inject.compiler.ext.isString
import inject.compiler.processor.FormatSpecifiers
import inject.compiler.processor.CreatorProcessor
import javax.lang.model.element.Element

/**
 * @author yuansui
 * @since 2020/9/8
 */
internal class CommonMethod(
    processor: CreatorProcessor, annotatedElement: Element
) : BaseMethod<CreatorProcessor>(processor, annotatedElement) {

    fun create(fileName: String): TypeSpec {
        val builder = TypeSpec.companionObjectBuilder()

        val clzName = ClassName(processor.getPackageName(annotatedElement), fileName)

        /*
        生成create方法
       */
        val simpleName = clzName.simpleName
        val createBuilder = FunSpec.builder("create")
            .addStatement("val builder = $simpleName()")
            .returns(clzName)

        // 添加必须的参数
        for (e in requireFields) {
            val paramName = e.getParamName()
            createBuilder.addParameter(createNullableParam(e, paramName))
            createBuilder.addStatement(
                "builder.${FormatSpecifiers.NAME} = ${FormatSpecifiers.NAME}",
                paramName,
                paramName
            )
        }
        createBuilder.addStatement("return builder")

        builder.addFunction(createBuilder.build())

        fun inject(): FunSpec {
            val hostName = "objectHost"
            /*
              生成objectHost调用的inject方法
             */
            val builder = FunSpec.builder("inject")
                .addAnnotation(JvmStatic::class) // 为了让java能反射到这个方法
                .addParameter(hostName, annotatedTypeName)
                .addParameter("extras", AndroidClassName.BUNDLE)

            for (e in allFields) {
                val paramName = e.getParamName()

                // 判断是否boolean而且带有is声明, 需要去掉is
                val typeName = e.asTypeName()
                if ((typeName == BOOLEAN || typeName == BOOLEAN.copy(nullable = true)) && paramName.startsWith("is")) {
                    val realName = paramName.substring(2)
                    builder.addStatement(
                        "$hostName.set${FormatSpecifiers.NAME}((${FormatSpecifiers.TYPE}) extras.get(${FormatSpecifiers.STRING}))",
                        realName,
                        typeName,
                        paramName
                    )
                } else {
                    when {
                        typeName.isPrimitive() -> {
                            // Boolean Byte Short Int Long Char Float Double
                            builder.beginControlFlow("if (extras.containsKey(${FormatSpecifiers.STRING}))", paramName)
                                .addStatement(
                                    "extras.get${FormatSpecifiers.TYPE}(${FormatSpecifiers.STRING}).let { $hostName.${FormatSpecifiers.NAME} = it }",
                                    typeName,
                                    paramName,
                                    paramName
                                )
                                .endControlFlow()
                        }

                        typeName.isString() || typeName.isSerializable() || typeName.isParcelable() -> {
                            // String Serialize Parcelable
                            builder.beginControlFlow("if (extras.containsKey(${FormatSpecifiers.STRING}))", paramName)
                                .addStatement(
                                    "extras.get${FormatSpecifiers.TYPE}(${FormatSpecifiers.STRING})?.let { $hostName.${FormatSpecifiers.NAME} = it }",
                                    typeName,
                                    paramName,
                                    paramName
                                )
                                .endControlFlow()
                        }

                        else -> {
                            builder.beginControlFlow("if (extras.containsKey(${FormatSpecifiers.STRING}))", paramName)

                            builder.addStatement(
                                "val typeToken = object : ${FormatSpecifiers.TYPE}<${FormatSpecifiers.TYPE}>(){}",
                                OtherClassName.TYPE_TOKEN,
                                typeName
                            )
                            builder.addStatement("val type = typeToken.getType()", OtherClassName.TYPE)
                            builder.addEson()
                            builder.addStatement("val params = extras.getString(${FormatSpecifiers.STRING})", paramName)

                            builder.beginControlFlow("try")
                                .addStatement(
                                    "eson.fromJson<${FormatSpecifiers.TYPE}>(params, type)?.let { $hostName.${FormatSpecifiers.NAME} = it }",
                                    typeName,
                                    paramName
                                )
                                .nextControlFlow("catch(e : ${FormatSpecifiers.TYPE})", OtherClassName.EXCEPTION)
                                .addStatement("e.printStackTrace()")
                                .endControlFlow()

                            builder.endControlFlow()
                        }
                    }
                }
            }

            return builder.build()
        }
        builder.addFunction(inject())

        return builder.build()
    }

    /**
     * 根据optional生成链式调用的方法
     */
    fun optionalSetters(fileName: String): List<FunSpec> {
        val specs = mutableListOf<FunSpec>()

        for (e in optFields) {
            val paramName = e.getParamName()
            val method = FunSpec.builder(paramName)
                .addParameter(
                    createNullableParam(
                        e,
                        paramName
                    )
                )
                .addStatement("this.${FormatSpecifiers.NAME} = ${FormatSpecifiers.NAME}", paramName, paramName)
                .addStatement("return this")
                .returns(ClassName(processor.getPackageName(annotatedElement), fileName))
                .build()

            specs.add(method)
        }

        return specs
    }

    fun newIntent(): FunSpec {
        val builder = FunSpec.builder("newIntent")
            .addParameter(createNonNullContext())
            .addStatement("val intent = Intent(context, ${FormatSpecifiers.TYPE}::class.java)", annotatedTypeName)
            .returns(AndroidClassName.INTENT)

        for (e in allFields) {
            val paramName = e.getParamName()
            builder.addParameter(createNullableParam(e, paramName))
        }
        addIntentStatement(builder, allFields)
        builder.addStatement("return intent")

        return builder.build()
    }

    private fun addIntentStatement(builder: FunSpec.Builder, elements: List<Element>) {
        for (e in elements) {
            val paramName = e.getParamName()
            builder.beginControlFlow("if (${FormatSpecifiers.NAME} != null)", paramName)
            val typeName = e.asTypeName()
            /**
             * String和Serializable类型单独处理, 因为kotlin的String同时继承了CharSequence和Serializable, putExtra无法识别具体使用哪个
             */
            when {
                typeName.isString() -> {
                    builder.addStatement(
                        "intent.putExtra(${FormatSpecifiers.STRING}, ${FormatSpecifiers.NAME} as CharSequence?)",
                        paramName,
                        paramName
                    )
                }

                typeName.isSerializable() -> {
                    builder.addStatement(
                        "intent.putExtra(${FormatSpecifiers.STRING}, ${FormatSpecifiers.NAME} as ${FormatSpecifiers.TYPE}?)",
                        paramName,
                        paramName
                    )
                }

                typeName.isPrimitive() || typeName.isParcelable() -> {
                    // Boolean Byte Short Int Long Char Float Double String Serialize
                    builder.addStatement(
                        "intent.putExtra(${FormatSpecifiers.STRING}, ${FormatSpecifiers.NAME})",
                        paramName,
                        paramName
                    )
                }

                else -> {
                    builder.addEson()
                    builder.beginControlFlow("try")
                    builder.addStatement(
                        "intent.putExtra(${FormatSpecifiers.STRING}, eson.toJson(${FormatSpecifiers.NAME}))",
                        paramName,
                        paramName
                    )
                    builder.nextControlFlow("catch(e: ${FormatSpecifiers.TYPE})", OtherClassName.EXCEPTION)
                    builder.addStatement("e.printStackTrace()")
                    builder.endControlFlow()
                }
            }
            builder.endControlFlow()
        }
    }
}
