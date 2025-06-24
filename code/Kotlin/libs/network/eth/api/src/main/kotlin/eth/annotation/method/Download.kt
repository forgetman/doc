package eth.annotation.method

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Download(val value: String = "", val useCheckPoint: Boolean = false)