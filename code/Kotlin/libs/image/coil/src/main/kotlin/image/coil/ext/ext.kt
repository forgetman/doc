package image.coil.ext

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix

/**
 * 正片叠底
 * @param mask
 * @param useAlpha 是否让alpha透明度参与到运算中
 */
internal fun Bitmap.multiply(mask: Bitmap?, useAlpha: Boolean = false): Bitmap {
    requireNotNull(mask) { "mask can not be null" }

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
        val m = mask.resize(w.toFloat(), h.toFloat())
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

internal fun Bitmap.resize(w: Float, h: Float): Bitmap {
    val scaleW = w / width
    val scaleH = h / height

    val matrix = Matrix()
    matrix.setScale(scaleW, scaleH)

    val copy = Bitmap.createBitmap(w.toInt(), h.toInt(), configNonNull)
    Canvas(copy).apply {
        drawBitmap(this@resize, matrix, null)
    }
    return copy
}

internal fun Bitmap.applyCanvas(block: Canvas.() -> Unit): Bitmap {
    val dest = if (isMutable) this else {
        copy(configNonNull, true)
    }

    Canvas(dest).apply {
        block()

        // Avoids warnings in M+.
        setBitmap(null)
    }
    return dest
}

internal val Bitmap.configNonNull: Bitmap.Config
    get() = config ?: Bitmap.Config.ARGB_8888