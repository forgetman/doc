@file:Suppress("unused")

package vector.app.ext

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PaintFlagsDrawFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF

/**
 * 设置canvas抗锯齿
 */
fun Canvas.setAntialias() {
    if (drawFilter == null) {
        drawFilter = PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    }
}

fun Canvas.removeFilter() {
    drawFilter = null
}

fun Canvas.clear() {
    drawPaint(Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) })
    //        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
}

/**
 * 根据X方向绘制文本
 */
fun Canvas?.drawTextInXAlign(text: String?, x: Float, y: Float, paint: Paint, align: Paint.Align) {
    if (text == null) return
    val newY = y + paint.textSize
    when (align) {
        Paint.Align.LEFT -> drawTextInLeftX(this, text, x, newY, paint)
        Paint.Align.CENTER -> drawTextInCenterX(this, text, x, newY, paint)
        Paint.Align.RIGHT -> drawTextInRightX(this, text, x, newY, paint)
    }
}

fun Canvas?.drawTextInXAlign(text: String?, x: Int, y: Int, paint: Paint, align: Paint.Align) {
    drawTextInXAlign(text, x.toFloat(), y.toFloat(), paint, align)
}

/**
 * 根据中心点绘制文本
 */
fun Canvas?.drawTextInCenter(text: String?, x: Int, y: Int, p: Paint) {
    drawTextInCenter(text, x.toFloat(), y.toFloat(), p)
}

fun Canvas?.drawTextInCenter(text: String?, x: Float, y: Float, p: Paint) {
    if (text == null) return
    val halfSize = p.measureText(text) / 2
    this?.drawText(text, x - halfSize, y + p.textSize / 2, p)
}

/**
 * 只调整X的对齐方式, Y不变
 */
fun Canvas?.drawBitmapInXAlign(
    bitmap: Bitmap?,
    x: Float,
    y: Float,
    paint: Paint?,
    align: Paint.Align
) {
    when (align) {
        Paint.Align.LEFT -> drawBmpInLeftX(this, bitmap, x, y, paint)
        Paint.Align.CENTER -> drawBmpInCenterX(this, bitmap, x, y, paint)
        Paint.Align.RIGHT -> drawBmpInRightX(this, bitmap, x, y, paint)
    }
}

private fun drawBmpInCenterX(canvas: Canvas?, bitmap: Bitmap?, x: Float, y: Float, paint: Paint?) {
    if (bitmap == null) return
    canvas?.drawBitmap(bitmap, x - bitmap.width / 2f, y, paint)
}

private fun drawBmpInLeftX(canvas: Canvas?, bitmap: Bitmap?, x: Float, y: Float, paint: Paint?) {
    if (bitmap == null) return
    canvas?.drawBitmap(bitmap, x, y, paint)
}

private fun drawBmpInRightX(canvas: Canvas?, bitmap: Bitmap?, x: Float, y: Float, paint: Paint?) {
    if (bitmap == null) return
    canvas?.drawBitmap(bitmap, x - bitmap.width, y, paint)
}

/**
 * 把图片缩放后居中画在指定点, X和Y都居中
 *
 * @param bitmap
 * @param x
 * @param y
 * @param paint
 * @param scaleW 1.0f表示不缩放
 * @param scaleH 1.0f表示不缩放
 */
fun Canvas?.drawBitmapScaleInCenter(
    bitmap: Bitmap?,
    x: Float,
    y: Float,
    paint: Paint?,
    scaleW: Float,
    scaleH: Float
) {
    if (bitmap == null) return

    val m = Matrix()
    if (scaleW == 1.0f && scaleH == 1.0f) {
        this?.drawBitmap(bitmap, x - bitmap.width / 2, y - bitmap.height / 2, paint)
        return
    }
    m.setScale(scaleW, scaleH)
    val newWidth = bitmap.width * scaleW
    val newHeight = bitmap.height * scaleH
    m.postTranslate(x - newWidth / 2, y - newHeight / 2)
    this?.drawBitmap(bitmap, m, paint)
}

/**
 * 在canvas上绘制圆角区域
 *
 * @param paint
 * @param radius 半径
 * @param left
 * @param top
 * @param right
 * @param bottom
 */
fun Canvas?.drawRoundRect(
    paint: Paint,
    left: Float,
    top: Float,
    right: Float,
    bottom: Float,
    radius: Float
) {
    val r = RectF(left, top, right, bottom)
    this?.drawRoundRect(paint, radius, r)
}

/**
 * 在canvas上绘制圆角区域本
 *
 * @param paint
 * @param radius
 * @param r
 */
fun Canvas?.drawRoundRect(paint: Paint, radius: Float, r: RectF) {
    this?.drawRoundRect(r, radius, radius, paint)
}

private fun drawTextInLeftX(canvas: Canvas?, text: String?, x: Float, y: Float, paint: Paint) {
    if (text == null) return
    canvas?.drawText(text, x, y, paint)
}

private fun drawTextInCenterX(canvas: Canvas?, text: String?, x: Float, y: Float, paint: Paint) {
    if (text == null) return
    canvas?.drawText(text, x - paint.measureText(text) / 2, y, paint)
}

private fun drawTextInRightX(canvas: Canvas?, text: String?, x: Float, y: Float, paint: Paint) {
    if (text == null) return
    canvas?.drawText(text, x - paint.measureText(text), y, paint)
}