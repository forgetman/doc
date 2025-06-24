package eth.annotation


/**
 * 自定义的标签(方便小众的需求)
 *
 * @author : GuoXuan
 * @since : 2019/5/28
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Customizes(
    /**
     * 自定义的键值对
     *
     * @return [StringMap] 键值对的数组
     */
    vararg val value: StringMap
)