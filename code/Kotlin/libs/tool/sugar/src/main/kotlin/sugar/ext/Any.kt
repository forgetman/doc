@file:Suppress("unused")

package sugar.ext

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.reflect.KClass

typealias NoArgBlock = () -> Unit
typealias Block<T> = (T) -> Unit

@OptIn(ExperimentalContracts::class)
@Suppress("NOTHING_TO_INLINE")
inline fun <T> T?.isNull(): Boolean {
    contract {
        returns(true) implies (this@isNull == null)
    }
    return this == null
}

@OptIn(ExperimentalContracts::class)
@Suppress("NOTHING_TO_INLINE")
inline fun <T> T?.isNotNull(): Boolean {
    contract {
        returns(true) implies (this@isNotNull != null)
    }
    return this != null
}

inline fun <T> T?.ifNull(block: NoArgBlock) {
    if (this == null) block()
}

inline fun <T> T?.ifNotNull(block: Block<T>) {
    if (this != null) block(this)
}

fun <T> T?.checkNotNull(): T {
    return this ?: throw NullPointerException("The object is null")
}

/*************
 * [doOnNotNull]体系
 * 暂时只支持2~3个变量的判断, 看情况拓展数量
 * 由于[notNull]的判断无法自动转型, 所以参数的调用会使用[!!]来保证语法的正确
 *************/

fun <T1, T2, R> doOnNotNull(t1: T1?, t2: T2?, block: (t1: T1, t2: T2) -> R?): R? {
    return if (notNull(t1, t2)) {
        block(t1!!, t2!!)
    } else null
}

fun <T1, T2, T3, R> doOnNotNull(
    t1: T1?,
    t2: T2?,
    t3: T3?,
    block: (t1: T1, t2: T2, t3: T3) -> R?
): R? {
    return if (notNull(t1, t2, t3)) {
        block(t1!!, t2!!, t3!!)
    } else null
}

internal fun notNull(vararg ts: Any?): Boolean {
    ts.forEach {
        if (it == null) return false
    }
    return true
}

/**
 * 默认只处理成功的情况
 */
inline fun <reified T> Any?.cast(block: (T) -> Unit) {
    if (this != null && this is T) {
        block(this)
    }
}

/**
 * 区分处理结果
 * @param onSuccess 处理成功
 * @param onError 处理错误
 */
inline fun <reified T> Any?.cast(onSuccess: (T) -> Unit, onError: () -> Unit) {
    if (this != null && this is T) {
        onSuccess(this)
    } else {
        onError()
    }
}

/**
 * 寻找对应的父类
 */
fun <T : Any> T.findSuperClass(clazz: Class<T>): Class<in T>? {
    var clz: Class<in T>? = javaClass
    while (clz != null && clz != Any::class.java) {
        if (clz == clazz) {
            break
        }
        clz = clz.superclass
    }
    return clz
}

fun <T : Any> T.findSuperClass(clazz: KClass<T>) = findSuperClass(clazz.java)

fun <T : Any, A : Annotation> T.getAnnotation(
    annotationClass: KClass<A>,
    block: ((A) -> Unit)? = null
): A? {
    val annotation = if (this is Class<*>) {
        this.getAnnotation(annotationClass.java)
    } else {
        javaClass.getAnnotation(annotationClass.java)
    }
    if (annotation != null) block?.invoke(annotation)
    return annotation
}

@Throws(NullPointerException::class)
fun <T> T?.throwIfNull(text: String? = ""): T {
    if (this == null) throw NullPointerException(text) else return this
}

/**
 * 返回自身, 方便声明链式调用
 */
inline fun <T> T.self(action: NoArgBlock): T {
    action()
    return this
}

// 获得T.class
inline fun <reified T> classOf() = T::class.java

// 获得 T.name
inline fun <reified T> nameOf(): String = classOf<T>().name

//获得 T object
inline fun <reified T> instanceOf(vararg parameterTypes: Class<*>): T = T::class.java.getDeclaredConstructor(*parameterTypes).newInstance()