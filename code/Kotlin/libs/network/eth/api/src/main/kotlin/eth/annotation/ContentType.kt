package eth.annotation

import eth.model.ContentTypeValue

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ContentType(val value: ContentTypeValue)