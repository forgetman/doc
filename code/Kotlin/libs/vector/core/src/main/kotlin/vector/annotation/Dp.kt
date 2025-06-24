package vector.annotation

import androidx.annotation.Dimension

/**
 * @author yuansui
 * @since 2020/9/7
 */
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.FIELD,
    AnnotationTarget.LOCAL_VARIABLE
)
@Retention(AnnotationRetention.SOURCE)
@Dimension(unit = Dimension.DP)
annotation class Dp