package eth.annotation.param

/**
 * 地址声明
 * 会根据是否使用了来决定使用动态的url地址还是指定的url地址作为host
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Url