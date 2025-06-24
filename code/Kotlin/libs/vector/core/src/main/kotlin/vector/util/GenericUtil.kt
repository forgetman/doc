package vector.util

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.reflect.KClass

/**
 * 泛型工具类
 * FIXME 因为kotlin对泛型的支持暂时不是太好, 暂时利用java的泛型机制进行操作
 *
 * @author yuansui
 */
object GenericUtil {

    fun getClassType(obj: Any, destClz: KClass<*>) = getClassType(obj::class.java, destClz.java)

    fun getClassType(clz: KClass<*>, destClz: KClass<*>) =
        getClassType(clz.java, destClz.java)

    /**
     * 从当前泛型类一直向上找, 直到没有父类或者找到对应的泛型类型
     */
    private fun getClassType(clz: Class<*>, destClz: Class<*>): KClass<*>? {
        var c = clz
        var genType: Type?

        while (c.superclass != null) {
            genType = c.genericSuperclass
            if (genType !is ParameterizedType) {
                c = c.superclass as Class<*>
                continue
            } else {
                val types = genType.actualTypeArguments
                for (type in types) {
                    if (type is Class<*> && destClz.isAssignableFrom(type)) {
                        return type.kotlin
                    }
                }
                val superclass = c.superclass
                if (superclass != null) {
                    c = superclass
                } else {
                    break
                }
            }
        }

        return null
    }

    fun getClassType(clz: KClass<*>, index: Int = 0): KClass<*> = getClassType(clz.java, index)

    private fun getClassType(clz: Class<*>, index: Int): KClass<*> {
        var c = clz
        var genType: Type? = null
        // 有可能子类的上级并不是泛型的直接子类, 那么需要找到最顶层的泛型父类
        while (c.superclass != null) {
            genType = c.genericSuperclass
            if (genType !is ParameterizedType) {
                c = c.superclass as Class<*>
                continue
            } else {
                break
            }
        }

        if (c == Any::class.java) {
            return c.kotlin
        }

        //返回表示此类型实际类型参数的Type对象的数组,数组里放的都是对应类型的Class
        val params = (genType as ParameterizedType).actualTypeArguments
        if (index >= params.size || index < 0) {
            throw RuntimeException("你输入的索引" + if (index < 0) "不能小于0" else "超出了参数的总数")
        }

        return if (params[index] !is Class<*>) {
            Any::class
        } else {
            (params[index] as Class<*>).kotlin
        }
    }
}