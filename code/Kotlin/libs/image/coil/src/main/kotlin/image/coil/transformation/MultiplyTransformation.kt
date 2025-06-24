package image.coil.transformation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import androidx.annotation.DrawableRes
import coil3.size.Size
import coil3.transform.Transformation
import image.coil.ext.applyCanvas
import image.coil.ext.multiply
import sugar.ext.nameOf
import java.lang.ref.WeakReference

/**
 * @author yuansui
 * @since 2020-08-27
 */
internal class MultiplyTransformation(
    context: Context,
    private val mask: Bitmap?,
    private val useAlpha: Boolean = false
) : Transformation() {

    @DrawableRes
    var id: Int = 0

    private val contextRef = WeakReference(context)
    private val context: Context?
        get() = contextRef.get()

    @Suppress("unused")
    constructor(context: Context, @DrawableRes id: Int, useAlpha: Boolean = false) : this(
        context,
        null,
        useAlpha
    ) {
        this.id = id
    }

    override val cacheKey: String = nameOf<MultiplyTransformation>()

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        val output = Bitmap.createBitmap(
            input.width,
            input.height,
            input.config ?: Bitmap.Config.ARGB_8888
        )

        output.applyCanvas {
            if (mask != null) {
                val multiply = input.multiply(mask, useAlpha)
                drawBitmap(multiply, 0f, 0f, paint)
                multiply.recycle()
            } else {
                BitmapFactory.decodeResource(context?.resources, id)?.let {
                    val multiply = input.multiply(it, useAlpha)
                    drawBitmap(multiply, 0f, 0f, paint)
                    multiply.recycle()
                    it.recycle()
                }
            }
        }

        return output
    }
}