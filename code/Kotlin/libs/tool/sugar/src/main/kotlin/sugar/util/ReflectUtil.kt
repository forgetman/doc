@file:Suppress("unused")

package sugar.util

import java.lang.reflect.Constructor
import java.lang.reflect.Method
import kotlin.reflect.KClass

/**
 * 反射类
 * FIXME: kotlin本身的反射在混淆以后有问题, 所以暂时使用java的方式
 *
 * @author yuansui
 */
object ReflectUtil {

    /**
     * 通过反射获取类的对象, 只通过public的构造方法获取
     *
     *
     * 通过循环的方式构造对象, 因为构造函数的数量还是非常小的, 不会增加多少开销
     * 无法通过传统的方式封装此方法, 因为需要传两个多变参数
     * ([Class.getConstructor] 和 [Constructor.newInstance]都需要)
     * 而且不能通过Object的getClass获取到对应构造里需要的参数class而导致无法匹配
     *
     *
     * @param clz  目标类
     * @param args 构造参数
     * @param <T>  任意类型
     * @return
    </T> */
    fun <T> newInst(clz: Class<T>?, vararg args: Any?): T? {
        if (clz == null) {
            return null
        }

        var t: T? = null
        val cs = clz.constructors
        for (i in cs.indices) {
            try {
                @Suppress("UNCHECKED_CAST")
                t = cs[i].newInstance(*args) as T
                break
            } catch (e: Exception) {
                continue
            }

        }

        return t
    }

    fun <T : Any> newInst(clz: KClass<T>, vararg args: Any?): T? {
        return newInst(clz.java, *args)
    }


    /**
     * 通过反射获取类的对象, 任意构造方法获取, 包括private
     *
     * @param clz
     * @param args
     * @param <T>
     * @return
    </T> */
    fun <T> newDeclaredInst(clz: Class<T>?, vararg args: Any): T? {
        if (clz == null) {
            return null
        }

        var t: T? = null
        val cs = clz.declaredConstructors
        for (i in cs.indices) {
            try {
                cs[i].isAccessible = true
                @Suppress("UNCHECKED_CAST")
                t = cs[i].newInstance(*args) as T
                break
            } catch (e: Exception) {
                continue
            }

        }
        return t
    }

    @Throws(ClassNotFoundException::class, NoSuchMethodException::class)
    fun getMethod(cls: Class<*>, methodName: String, vararg parameterTypes: Class<*>): Method {
        return cls.getMethod(methodName, *parameterTypes)
    }

    @Throws(ClassNotFoundException::class, NoSuchMethodException::class)
    fun getMethod(cls: KClass<*>, methodName: String, vararg parameterTypes: Class<*>): Method {
        return getMethod(cls.java, methodName, *parameterTypes)
    }

    @Throws(ClassNotFoundException::class, NoSuchMethodException::class)
    fun getMethod(className: String, methodName: String, vararg parameterTypes: Class<*>): Method {
        return getMethod(Class.forName(className), methodName, *parameterTypes)
    }

    @Throws(ClassNotFoundException::class, NoSuchMethodException::class)
    fun getDeclaredMethod(
        cls: Class<*>,
        methodName: String,
        vararg parameterTypes: Class<*>
    ): Method {
        return cls.getDeclaredMethod(methodName, *parameterTypes)
    }

    @Throws(ClassNotFoundException::class, NoSuchMethodException::class)
    fun getDeclaredMethod(
        cls: KClass<*>,
        methodName: String,
        vararg parameterTypes: Class<*>
    ): Method {
        return getDeclaredMethod(cls.java, methodName, *parameterTypes)
    }

    @Throws(ClassNotFoundException::class, NoSuchMethodException::class)
    fun getDeclaredMethod(
        className: String,
        methodName: String,
        vararg parameterTypes: Class<*>
    ): Method {
        return getDeclaredMethod(Class.forName(className), methodName, *parameterTypes)
    }
}

@Throws(NoSuchFieldException::class)
inline fun <reified T> T.setFieldValue(fieldName: String, value: Any) {
    T::class.java.getDeclaredField(fieldName).apply {
        isAccessible = true
        set(this@setFieldValue, value)
    }
}

@Throws(ClassNotFoundException::class, NoSuchMethodException::class)
inline fun <reified T> T.reflectMethod(
    methodName: String,
    vararg parameterTypes: Class<*>
): Method {
    return T::class.java.getMethod(methodName, *parameterTypes)
}

@Throws(ClassNotFoundException::class, NoSuchMethodException::class)
inline fun <reified T> T.reflectDeclaredMethod(
    methodName: String,
    vararg parameterTypes: Class<*>
): Method {
    return T::class.java.getDeclaredMethod(methodName, *parameterTypes)
}

fun <T> Class<T>.newInst(vararg args: Any?): T? {
    return ReflectUtil.newInst(this, args)
}
