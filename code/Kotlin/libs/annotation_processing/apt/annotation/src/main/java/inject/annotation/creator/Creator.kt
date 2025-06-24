package inject.annotation.creator

import kotlin.annotation.AnnotationRetention;
import kotlin.annotation.AnnotationTarget;

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Creator(
    /**
     * 是否需要启用共享元素过场动画
     */
    val withTransition: Boolean = false,
    /**
     * 是否需要创建 startActivityForResult() 相关的方法
     */
    val forResult: Boolean = false
)