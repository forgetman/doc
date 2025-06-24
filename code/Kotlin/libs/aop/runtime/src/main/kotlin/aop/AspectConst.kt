package aop

/**
 * @author yuansui
 * @since 2018/5/11
 * 其他的只能使用范例模板复制粘贴使用了
 * - 如: WITHIN: "@within(aspect.annotation.注解类名)||@annotation(aspect.annotation.注解类名)";
 */
internal object AspectConst {
    const val START = "execution(@aop.annotation."
    const val END_METHOD = " * *(..))"
    const val END_CONSTRUCTOR = " *.new(..))"
    const val START_SYNTHETIC = "execution(!synthetic * *(..)) && "

    const val ARROW = "⇢ " // \u21E2
    const val SPLIT = ", "
    const val BLANK = " "
}