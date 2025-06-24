package eth.annotation

/**
 * 设置host
 * 可以动态替换当前请求的host url
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Host(val value: String)