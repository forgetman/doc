package eth.annotation.param

/**
 * 声明参数
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Query(
    /**
     * key值
     *
     * @return key
     */
    val value: String
)