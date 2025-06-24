package eth.annotation

import eth.model.CharsetValue

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Charset(val value: CharsetValue)