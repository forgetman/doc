package eth.annotation.param

/**
 * 声明(头)参数
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Header(
    /**
     * key值
     *
     * @return key
     */
    val value: String
)