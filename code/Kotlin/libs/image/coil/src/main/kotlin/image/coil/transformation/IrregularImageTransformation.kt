package image.coil.transformation

import android.content.Context
import android.graphics.*
import androidx.annotation.DrawableRes
import coil3.size.Size
import coil3.transform.Transformation
import image.coil.ext.applyCanvas
import sugar.ext.nameOf
import java.lang.ref.WeakReference

/**
 * @author yuansui
 * @since 2020-08-27
 */
internal class IrregularImageTransformation(
    context: Context, @DrawableRes val resId: Int
) : Transformation() {

    private val contextRef = WeakReference(context)
    private val context: Context?
        get() = contextRef.get()

    override val cacheKey: String = nameOf<IrregularImageTransformation>()

    override fun equals(other: Any?) = other is IrregularImageTransformation

    override fun hashCode() = javaClass.hashCode()

    override fun toString() = "IrregularImageTransformation()"

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        // 不规则边框的bitmap
        val src = BitmapFactory.decodeResource(context?.resources, resId)
        if (src != null) {
            val output = Bitmap.createBitmap(
                input.width,
                input.height,
                input.config ?: Bitmap.Config.ARGB_8888
            )
            output.applyCanvas {
                drawBitmap(src, 0f, 0f, null)

                val paint = Paint().apply {
                    isAntiAlias = true          //设置抗锯齿
                    style = Paint.Style.FILL    //设置填充样式
                    isDither = true             //设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
                    isFilterBitmap = true       //加快显示速度，本设置项依赖于dither和xfermode的设置
                    xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT) //取得mask之外task之中的bitmap
                }
                drawBitmap(input, 0f, 0f, paint)
            }
            return output
        } else {
            return input
        }
    }
}