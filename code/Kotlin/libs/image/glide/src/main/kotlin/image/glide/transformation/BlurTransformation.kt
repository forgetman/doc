package vector.image.glide.transformation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import image.glide.ext.applyCanvas
import image.glide.transformation.BaseBitmapTransformation
import java.lang.ref.WeakReference
import java.security.MessageDigest


/**
 * 模糊
 *
 * @author yuansui
 * @since 2019/4/17
 */
class BlurTransformation constructor(
    context: Context,
    private val radius: Float = DEFAULT_RADIUS,
    private val sampling: Float = DEFAULT_SAMPLING
) : BaseBitmapTransformation() {

    companion object {
        private const val VERSION = 1
        private const val ID = "glide.transformations.BlurTransformation.$VERSION"
        private val ID_BYTES = ID.toByteArray(Key.CHARSET)

        private const val DEFAULT_RADIUS = 10f
        private const val DEFAULT_SAMPLING = 1f
    }

    private val contextRef = WeakReference(context)
    private val context: Context?
        get() = contextRef.get()

    init {
        require(radius in 0.0..25.0) { "radius must be in [0, 25]." }
        require(sampling > 0) { "sampling must be > 0." }
    }

    override fun equals(other: Any?): Boolean {
        return when {
            this === other -> true
            javaClass == other?.javaClass -> true
            other is BlurTransformation -> {
                other.radius == radius && other.sampling == sampling
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

        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        val scaledWidth = (toTransform2.width / sampling).toInt()
        val scaledHeight = (toTransform2.height / sampling).toInt()
        val output = pool.get(scaledWidth, scaledHeight, toTransform2.config)
        output.applyCanvas {
            scale(1 / sampling, 1 / sampling)
            drawBitmap(toTransform2, 0f, 0f, paint)
        }

        TransformationUtils.getBitmapDrawableLock().lock()

        var script: RenderScript? = null
        var tmpInt: Allocation? = null
        var tmpOut: Allocation? = null
        var blur: ScriptIntrinsicBlur? = null
        try {
            script = RenderScript.create(context)
            tmpInt = Allocation.createFromBitmap(
                script,
                output,
                Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT
            )
            tmpOut = Allocation.createTyped(script, tmpInt.type)
            blur = ScriptIntrinsicBlur.create(script, Element.U8_4(script))
            blur.setRadius(radius)
            blur.setInput(tmpInt)
            blur.forEach(tmpOut)
            tmpOut.copyTo(output)
        } finally {
            script?.destroy()
            tmpInt?.destroy()
            tmpOut?.destroy()
            blur?.destroy()

            TransformationUtils.getBitmapDrawableLock().unlock()
        }

        if (toTransform2 != toTransform) {
            pool.put(toTransform2)
        }

        return output
    }
}