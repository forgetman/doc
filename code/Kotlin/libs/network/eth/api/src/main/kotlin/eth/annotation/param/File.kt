package eth.annotation.param

/**
 * 声明(上传文件)参数
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class File(
    /**
     * 替换掉默认的key值
     *
     * @return
     */
    val value: String = ""
)