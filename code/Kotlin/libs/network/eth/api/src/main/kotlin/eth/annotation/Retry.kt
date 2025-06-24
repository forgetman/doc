package eth.annotation


/**
 * 声明重试的条件
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Retry(
    /**
     * 次数
     */
    val count: Int,
    /**
     * 延时毫秒
     */
    val delay: Long
)