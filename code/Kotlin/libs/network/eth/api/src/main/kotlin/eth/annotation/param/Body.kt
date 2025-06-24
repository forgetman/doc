package eth.annotation.param

/**
 * 如果不使用键值对(map), 可以直接传完整的post body
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Body