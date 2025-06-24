@file:Suppress("unused")

package vector.app.ext

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.core.graphics.drawable.toDrawable
import com.google.android.renderscript.Toolkit
import sugar.ext.safeUse
import vector.compat.media.MediaCompat
import vector.compat.media.OnConflictStrategy
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

const val MAX_DOUBLE = Int.MAX_VALUE.toDouble()
const val MIN_DOUBLE = 1.0

/**
 * Creates a new [Canvas] to draw on this bitmap and executes the specified
 * [block] on the newly created canvas. Example:
 *
 * ```
 * return Bitmap.createBitmap(…).applyCanvas {
 *    drawLine(…)
 *    translate(…)
 *    drawRect(…)
 * }
 * ```
 */
inline fun Bitmap.applyCanvas(block: Canvas.() -> Unit): Bitmap {
    val dest = if (isMutable) this else {
        copy(configNonNull, true)
    }

    Canvas(dest).apply {
        setAntialias()
        block()

        // Avoids warnings in M+.
        setBitmap(null)
    }
    return dest
}

/**
 * 旋转图片，使图片保持正确的方向。
 *
 * @param degrees    要旋转的角度
 * @param isVertical 垂直显示图片
 * @return 旋转后的图片
 */
fun Bitmap.rotate(@IntRange(from = 0, to = 360) degrees: Int, isVertical: Boolean): Bitmap {
    var needRotate = false
    if (isVertical) {
        if (height < width) {
            needRotate = true
        }
    } else {
        if (height > width) {
            needRotate = true
        }
    }

    return if (needRotate) {
        rotate(degrees)
    } else {
        this
    }
}

/**
 * 旋转图片
 *
 * @param degrees
 * @return 不可变的图片
 */
fun Bitmap.rotate(@IntRange(from = 0, to = 360) degrees: Int): Bitmap {
    if (degrees == 0) {
        return this
    }

    val matrix = Matrix()
    matrix.setRotate(degrees.toFloat())

    /**
     * 不建议使用系统的createScaleBitmap, 不是最清晰的
     */
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

/**
 * 根据宽高缩放, 返回的图片是可变的, 可装载入Canvas
 *
 * @param w   目标宽
 * @param h   目标高
 * @return
 */
fun Bitmap.resize(
    @FloatRange(from = MIN_DOUBLE, to = MAX_DOUBLE) w: Float,
    @FloatRange(from = MIN_DOUBLE, to = MAX_DOUBLE) h: Float
): Bitmap {
    val scaleW = w / width
    val scaleH = h / height

    val matrix = Matrix()
    matrix.setScale(scaleW, scaleH)

    return copy(w, h, matrix)
}

fun Bitmap.resize(
    @IntRange(from = 1, to = Long.MAX_VALUE) w: Int,
    @IntRange(from = 1, to = Long.MAX_VALUE) h: Int
): Bitmap {
    val scaleW = w / width.toFloat()
    val scaleH = h / height.toFloat()

    val matrix = Matrix()
    matrix.setScale(scaleW, scaleH)

    return copy(w, h, matrix)
}

/**
 * 根据比例缩放, 返回的图片是可变的, 可装载入Canvas
 *
 * @param scale
 * @return
 */
fun Bitmap.resize(@FloatRange(from = MIN_DOUBLE, to = MAX_DOUBLE) scale: Float): Bitmap {
    var w = width * scale
    var h = height * scale

    if (w < MIN_DOUBLE) {
        w = MIN_DOUBLE.toFloat()
    }

    if (h < MIN_DOUBLE) {
        h = MIN_DOUBLE.toFloat()
    }

    return copy(w, h, Matrix().apply { setScale(scale, scale) })
}

/**
 * 同时缩放和旋转
 *
 * @param scale  比例
 * @param degree 角度
 * @return 不可变的图片(如果角度为0, scale为1, 那么返回的就是原图)
 */
fun Bitmap.resize(
    @FloatRange(from = MIN_DOUBLE, to = MAX_DOUBLE) scale: Float,
    @IntRange(from = 0, to = 360) degree: Int
): Bitmap {

    val matrix = Matrix()
    matrix.preScale(scale, scale)
    matrix.postRotate(degree.toFloat())

    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

/**
 * 根据[matrix]复制一张图片
 */
fun Bitmap.copy(
    w: Int,
    h: Int,
    matrix: Matrix,
    config: Bitmap.Config = Bitmap.Config.ARGB_8888
): Bitmap {
    return Bitmap.createBitmap(w, h, config).applyCanvas {
        drawBitmap(this@copy, matrix, null)
    }
}

fun Bitmap.copy(
    w: Float,
    h: Float,
    matrix: Matrix,
    config: Bitmap.Config = Bitmap.Config.ARGB_8888
): Bitmap {
    return copy(w.toInt(), h.toInt(), matrix, config)
}

fun Bitmap.copy(config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap {
    return copy(config, true)
}

fun Bitmap.safeConfig(): Bitmap.Config {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Avoid short circuiting the sdk check.
        if (Bitmap.Config.RGBA_F16 == config) { // NOPMD
            return Bitmap.Config.RGBA_F16
        }
    }

    return Bitmap.Config.ARGB_8888
}

/**
 * 图片无损转换为bytes
 */
fun Bitmap.toBytes(): ByteArray? {
    return compress(100, Bitmap.CompressFormat.PNG)
}

/**
 * 图片转换成字节数
 *
 * @param quality
 * @param format
 */
fun Bitmap.compress(
    @IntRange(from = 1, to = 100) quality: Int,
    format: Bitmap.CompressFormat
): ByteArray? {
    return ByteArrayOutputStream().safeUse {
        if (compress(format, quality, it)) {
            it.toByteArray()
        } else {
            null
        }
    }
}

/**
 * 转换成圆形图片
 */
fun Bitmap.toCircle(): Bitmap {
    val paint = Paint()
    paint.isAntiAlias = true

    val w = width
    val h = height
    val min = if (w > h) h else w

    return Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888).applyCanvas {
        drawCircle(min / 2f, min / 2f, min / 2f, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        drawBitmap(this@toCircle, 0f, 0f, paint)
    }
}

fun Bitmap?.recycle() {
    if (this != null && !isRecycled) {
        recycle()
    }
}

/**
 * 压缩图片到文件
 *
 * @param toFile    文件
 * @param format  图片格式
 * @param quality 图片质量
 * @return 是否成功保存
 */
fun Bitmap.compress(
    toFile: File,
    @IntRange(from = 1, to = 100) quality: Int,
    format: Bitmap.CompressFormat
): Boolean {
    return FileOutputStream(toFile).safeUse { compress(format, quality, it) } ?: false
}

/**
 * 正片叠底
 * @param mask
 * @param useAlpha 是否让alpha透明度参与到运算中
 */
fun Bitmap.multiply(mask: Bitmap?, useAlpha: Boolean = false): Bitmap {
    requireNotNull(mask) { "mask can not be null" }

    // 系统的做法, 暂时保留代码
//    val new = Bitmap.createBitmap(width, height, config)
//    new.applyCanvas {
//        val p = Paint()
//        p.isFilterBitmap = true
//
////        val layerID = saveLayer(0f, 0f, width.toFloat(), height.toFloat(), p, Canvas.ALL_SAVE_FLAG)
//
//        drawBitmap(this@multiply, 0f, 0f, p)
//
//        p.xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
//
//        drawBitmap(mask, 0f, 0f, p)
////        val m = Matrix()
////        val scaleW = width / mask.width.toFloat()
////        val scaleH = height / mask.height.toFloat()
////        m.setScale(scaleW, scaleH)
////        drawBitmap(mask, m, p)
//
//
////        p.xfermode = null
////        restoreToCount(layerID)
//    }
//    return new
//
    val use: Bitmap = if (isMutable) {
        this
    } else {
        copy(configNonNull, true)
    }

    val w = use.width
    val h = use.height
    val maskW = mask.width
    val maskH = mask.height

    val useMask: Bitmap = if (w != maskW || h != maskH) {
        val m = mask.resize(w, h)
        mask.recycle()
        m
    } else {
        mask
    }

    val pix = IntArray(w * h)
    val maskPix = IntArray(w * h)
    getPixels(pix, 0, w, 0, 0, w, h)
    useMask.getPixels(maskPix, 0, w, 0, 0, w, h)

    var color: Int
    var maskColor: Int

    for (i in pix.indices) {
        color = pix[i]
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)

        maskColor = maskPix[i]
        val rMask = Color.red(maskColor)
        val gMask = Color.green(maskColor)
        val bMask = Color.blue(maskColor)

        val a = Color.alpha(color)
        val aMask = Color.alpha(maskColor)

        if (aMask == 0) {
            // 透明的时候使用原图颜色
            pix[i] = Color.argb(a, r, g, b)
        } else {
            val max = 255
            val rResult = r * rMask / max
            val gResult = g * gMask / max
            val bResult = b * bMask / max
            val aResult = if (useAlpha) a * aMask / max else a

            pix[i] = Color.argb(aResult, rResult, gResult, bResult)
        }
    }

    use.setPixels(pix, 0, w, 0, 0, w, h)
    return use
}

/**
 * 根据蒙版叠加图片, 白色保留黑色过滤, 其他颜色自动根据色级调整透明度
 *
 * @param mask 蒙版图
 * @return 叠加后的图片
 */
fun Bitmap.masking(mask: Bitmap?): Bitmap {
    requireNotNull(mask) { "mask can not be null" }

    val w = width
    val h = height
    val maskW = mask.width
    val maskH = mask.height

    val useMask: Bitmap = if (w != maskW || h != maskH) {
        val m = mask.resize(w, h)
        mask.recycle()
        m
    } else {
        mask
    }

    val pix = IntArray(w * h)
    val maskPixArray = IntArray(w * h)
    getPixels(pix, 0, w, 0, 0, w, h)
    useMask.getPixels(maskPixArray, 0, w, 0, 0, w, h)

    var color: Int
    var red: Int
    var green: Int
    var blue: Int
    var maskColor: Int
    var maskAlpha: Int

    for (i in pix.indices) {
        color = pix[i]
        red = Color.red(color)
        green = Color.green(color)
        blue = Color.blue(color)

        maskColor = maskPixArray[i]
        maskAlpha = Color.red(maskColor) // 对比黑白, rgb都一样的值, 只需要获取其中一个的来当成比例计算就行了
        pix[i] = Color.argb(maskAlpha, red, green, blue)
    }

    mask.setPixels(pix, 0, w, 0, 0, w, h)
    return mask

}

/**
 * 模糊算法
 *
 * @param radius  模糊半径(1-25)
 */
fun Bitmap.blur(@IntRange(from = 1, to = 25) radius: Int = 20): Bitmap {
    return Toolkit.blur(this, radius)
}

/**
 * 将bitmap保存到文件中
 */
fun Bitmap.saveToFile(
    file: File,
    format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
    @IntRange(from = 0, to = 100) quality: Int = 100
): Boolean {
    return file.outputStream().buffered().safeUse {
        compress(format, quality, it)
    } ?: false
}

/**
 * 将bitmap保存到相册中
 */
fun Bitmap.saveToAlbum(
    context: Context,
    format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
    @IntRange(from = 0, to = 100) quality: Int = 100,
    secondaryPath: String? = null,
    displayName: String? = null,
    onConflict: OnConflictStrategy = OnConflictStrategy.DEFAULT
): Boolean {
    return MediaCompat.Image.saveToAlbum(
        context,
        this,
        format,
        quality,
        secondaryPath,
        displayName,
        onConflict
    )
}

fun Bitmap.saveToPrivateAlbum(
    context: Context,
    format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
    @IntRange(from = 0, to = 100) quality: Int = 100,
    secondaryPath: String? = null,
    displayName: String? = null,
    onConflict: OnConflictStrategy = OnConflictStrategy.DEFAULT
): Boolean {
    return MediaCompat.Image.saveToPrivateAlbum(
        context,
        this,
        format,
        quality,
        secondaryPath,
        displayName,
        onConflict
    )
}

/**
 * config每1像素占用的内存大小
 */
@Suppress("DEPRECATION")
val Bitmap.Config?.bytesPerPixel: Int
    get() = when {
        this == Bitmap.Config.ALPHA_8 -> 1
        this == Bitmap.Config.RGB_565 -> 2
        this == Bitmap.Config.ARGB_4444 -> 2
        Build.VERSION.SDK_INT >= 26 && this == Bitmap.Config.RGBA_F16 -> 8
        else -> 4
    }

@Suppress("NOTHING_TO_INLINE")
inline fun Bitmap.toDrawable(context: Context): BitmapDrawable = toDrawable(context.resources)

val Bitmap.configNonNull: Bitmap.Config
    get() = config ?: Bitmap.Config.ARGB_8888