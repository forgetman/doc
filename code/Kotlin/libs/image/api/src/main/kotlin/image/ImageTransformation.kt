@file:Suppress("unused")

package image

import android.graphics.Bitmap
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.IntRange

/**
 * @author yuansui
 * @since 2020-08-27
 */
sealed class ImageTransformation {
    sealed class Shape : ImageTransformation() {
        /**
         * 圆形
         * @param color 边框颜色
         * @param width 边框宽度
         */
        class Circle(
            @IntRange(from = 0) val width: Int = 0,
            @ColorInt val color: Int = Color.TRANSPARENT
        ) : Shape()

        /**
         * 圆角
         */
        class RoundCorner(
            @IntRange(from = 0, to = 180) val topLeft: Int,
            val topRight: Int,
            val bottomLeft: Int,
            val bottomRight: Int
        ) : Shape() {
            constructor(@IntRange(from = 0, to = 180) radius: Int = 10) : this(
                radius,
                radius,
                radius,
                radius
            )
        }

        /**
         * 不规则的形状
         * 根据传进来的[resId], 将黑色区域设置为透明, 透明区域保留原图显示
         */
        data class Irregular(@DrawableRes val resId: Int) : Shape()
    }

    sealed class Effect : ImageTransformation() {
        data class Blur(val radius: Int = 10) : Effect()
        object Gray : Effect()
        class Multiply(val mask: Bitmap?, val useAlpha: Boolean = false) : Effect()
    }
}