package image.coil.transformation

import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.BitmapShader
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.Shader
import androidx.annotation.ColorInt
import coil3.decode.DecodeUtils
import coil3.size.Dimension
import coil3.size.Scale
import coil3.size.Size
import coil3.size.isOriginal
import coil3.size.pxOrElse
import coil3.transform.Transformation
import image.coil.ext.applyCanvas
import image.coil.ext.configNonNull
import sugar.ext.nameOf
import kotlin.math.roundToInt

/**
 * @author yuansui
 * @since 2020-08-27
 *
 * 不能直接使用[coil.transform.CircleCropTransformation], 没有scale属性,
 * 如果图片大小不匹配或者imageView不是[android.widget.ImageView.ScaleType.CENTER_CROP]的话会有问题.
 * 参考了[coil.transform.RoundedCornersTransformation]
 */
internal class CircleImageTransformation(
    private val borderWidth: Float,
    @ColorInt private val borderColor: Int
) : Transformation() {

    override val cacheKey: String
        get() = nameOf<CircleImageTransformation>()

    override fun equals(other: Any?) = other is CircleImageTransformation

    override fun hashCode() = javaClass.hashCode()

    override fun toString() = "CircleImageTransformation()"

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        val (outputWidth, outputHeight) = calculateOutputSize(input, size)

        val minSize = minOf(outputWidth, outputHeight)
        val radius = minSize / 2f
        val output = createBitmap(minSize, minSize, input.configNonNull)
        output.applyCanvas {
            drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

            val matrix = Matrix()
            val multiplier = DecodeUtils.computeSizeMultiplier(
                srcWidth = input.width,
                srcHeight = input.height,
                dstWidth = outputWidth,
                dstHeight = outputHeight,
                scale = Scale.FILL
            ).toFloat()
            val dx = (outputWidth - multiplier * input.width) / 2
            val dy = (outputHeight - multiplier * input.height) / 2
            matrix.setTranslate(dx, dy)
            matrix.preScale(multiplier, multiplier)

            val shader = BitmapShader(input, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            shader.setLocalMatrix(matrix)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
            paint.shader = shader

            drawCircle(radius, radius, radius, paint)

            if (borderWidth > 0) {
                val paintBorder = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
                    color = borderColor
                    style = Paint.Style.STROKE
                    strokeWidth = borderWidth
                }

                val borderRadius = radius - borderWidth / 2 // 边框的绘制是线中心到两边

                drawCircle(radius, radius, borderRadius, paintBorder)

                // Avoids warnings in M+.
                setBitmap(null)
            }
        }

        return output
    }

    private fun calculateOutputSize(input: Bitmap, size: Size): Pair<Int, Int> {
        if (size.isOriginal) {
            return input.width to input.height
        }

        val (dstWidth, dstHeight) = size
        if (dstWidth is Dimension.Pixels && dstHeight is Dimension.Pixels) {
            return dstWidth.px to dstHeight.px
        }

        val multiplier = DecodeUtils.computeSizeMultiplier(
            srcWidth = input.width,
            srcHeight = input.height,
            dstWidth = size.width.pxOrElse { Int.MIN_VALUE },
            dstHeight = size.height.pxOrElse { Int.MIN_VALUE },
            scale = Scale.FILL
        )
        val outputWidth = (multiplier * input.width).roundToInt()
        val outputHeight = (multiplier * input.height).roundToInt()
        return outputWidth to outputHeight
    }
}