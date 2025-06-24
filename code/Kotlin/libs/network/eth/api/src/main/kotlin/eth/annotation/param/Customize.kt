package eth.annotation.param


/**
 * 声明(自定义)参数
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Customize(
    /**
     * 参数的key
     *
     * @return [String] key
     */
    val value: String
)