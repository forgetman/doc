package eth.annotation.param

/**
 * 地址(部分替换)声明
 * 将命中key值替换为参数
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Path(
    /**
     * 参数的key
     *
     * @return [String] key
     */
    val value: String
)