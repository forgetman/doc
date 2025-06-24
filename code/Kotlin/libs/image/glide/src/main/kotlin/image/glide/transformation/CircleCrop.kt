package image.glide.transformation

import android.graphics.Bitmap
import android.graphics.Paint
import androidx.annotation.ColorInt
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import image.glide.ext.applyCanvas
import java.security.MessageDigest
import kotlin.math.min

/**
 * 圆形添加圆形边框
 * @author TangPeng
 * @since 2018/9/18
 */
class CircleCrop constructor(
    private val borderWidth: Float, @ColorInt private val borderColor: Int
) : BaseBitmapTransformation() {

    companion object {
        private const val VERSION = 2
        private const val ID = "vector.glide.transformation.circleCrop.$VERSION"
        private val ID_BYTES = ID.toByteArray(Key.CHARSET)
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap? {
        val result = TransformationUtils.circleCrop(pool, toTransform, outWidth, outHeight)
        if (borderWidth > 0) {
            TransformationUtils.getBitmapDrawableLock().lock()
            try {
                result.applyCanvas {
                    val paint = Paint()
                    paint.color = borderColor
                    paint.style = Paint.Style.STROKE
                    paint.strokeWidth = borderWidth

                    val center = min(outWidth, outHeight) / 2f
                    val radius = center - borderWidth / 2 // 边框的绘制是线中心到两边

                    drawCircle(center, center, radius, paint)
                }
            } finally {
                TransformationUtils.getBitmapDrawableLock().unlock()
            }
        }
        return result
    }

    override fun hashCode(): Int {
        return ID.hashCode()
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES)
    }

    override fun equals(other: Any?): Boolean {
        return when {
            this === other -> true
            javaClass == other?.javaClass -> true
            other is CircleCrop -> {
                other.borderWidth == borderWidth && other.borderColor == borderColor
            }
            else -> false
        }
    }
}