package image.glide.ext

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix

internal fun Bitmap.applyCanvas(block: Canvas.() -> Unit): Bitmap {
    val dest = if (isMutable) this else {
        copy(config ?: Bitmap.Config.ARGB_8888, true)
    }

    Canvas(dest).apply {
        block()

        // Avoids warnings in M+.
        setBitmap(null)
    }
    return dest
}

internal fun Bitmap.resize(w: Float, h: Float): Bitmap {
    val scaleW = w / width
    val scaleH = h / height

    val matrix = Matrix()
    matrix.setScale(scaleW, scaleH)

    val copy = Bitmap.createBitmap(w.toInt(), h.toInt(), config ?: Bitmap.Config.ARGB_8888)
    Canvas(copy).apply {
        drawBitmap(this@resize, matrix, null)
    }
    return copy
}