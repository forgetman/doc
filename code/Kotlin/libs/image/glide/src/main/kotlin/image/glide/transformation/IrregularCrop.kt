package vector.image.glide.transformation

import android.content.Context
import android.graphics.*
import androidx.annotation.DrawableRes
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import image.glide.ext.applyCanvas
import image.glide.ext.resize
import image.glide.transformation.BaseBitmapTransformation
import java.lang.ref.WeakReference
import java.security.MessageDigest


/**
 * 不规则的图片形状
 *
 * @author CaiXiang
 * @since 2019/4/17
 */
class IrregularCrop constructor(
    context: Context, @DrawableRes val resId: Int
) : BaseBitmapTransformation() {

    companion object {
        private const val VERSION = 2
        private const val ID = "glide.transformations.IrregularCrop.$VERSION"
        private val ID_BYTES = ID.toByteArray(Key.CHARSET)
    }

    private val contextRef = WeakReference(context)
    private val context: Context?
        get() = contextRef.get()

    override fun equals(other: Any?): Boolean {
        return when {
            this === other -> true
            javaClass == other?.javaClass -> true
            other is IrregularCrop -> {
                other.resId == resId
            }
            else -> false
        }
    }

    override fun hashCode(): Int {
        return ID.hashCode()
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES)
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        val toTransform2 = getAlphaSafeBitmap(pool, toTransform)

        val result = pool.get(outWidth, outHeight, Bitmap.Config.ARGB_8888)
        // 不规则边框的bitmap
        val src = BitmapFactory.decodeResource(context?.resources, resId)
        if (src != null) {
            TransformationUtils.getBitmapDrawableLock().lock()
            try {
                result.applyCanvas {
                    val mask = src.resize(outWidth.toFloat(), outHeight.toFloat())
                    src.recycle()

                    drawBitmap(mask, 0f, 0f, null)

                    val paint = Paint().apply {
                        isAntiAlias = true          //设置抗锯齿
                        style = Paint.Style.FILL    //设置填充样式
                        isDither = true             //设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
                        isFilterBitmap = true       //加快显示速度，本设置项依赖于dither和xfermode的设置
                        xfermode =
                            PorterDuffXfermode(PorterDuff.Mode.SRC_OUT) //取得mask之外task之中的bitmap
                    }
                    drawBitmap(toTransform, 0f, 0f, paint)
                }
            } finally {
                TransformationUtils.getBitmapDrawableLock().unlock()
            }
        }

        if (toTransform2 != toTransform) {
            pool.put(toTransform2)
        }
        return result
    }

}