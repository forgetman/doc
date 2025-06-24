package inject.annotation.creator

import java.lang.annotation.Inherited
import kotlin.annotation.AnnotationRetention;
import kotlin.annotation.AnnotationTarget;

/**
 * @see Creator
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.BINARY)
@Inherited
annotation class Extra(
    /**
     * 可选参数, 可以不传, 或者多参数情况下为了方便去使用链式调用
     * 声明的参数不能为private
     * @return 是否不必须传递, false为必传
     */
    val value: Boolean = false
)
