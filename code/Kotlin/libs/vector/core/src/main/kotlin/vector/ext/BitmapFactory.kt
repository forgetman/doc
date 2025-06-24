package vector.ext

import android.graphics.BitmapFactory
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sqrt

/**
 * 根据宽高计算simple size
 */
fun BitmapFactory.Options.calcInSampleSize(width: Int, height: Int) =
    calcSimpleSize(this, if (width > height) height else width, width * height)

/**
 * 根据最大像素乘积计算simple size
 */
fun BitmapFactory.Options.calcInSampleSize(maxPixels: Int) =
    calcSimpleSize(this, UN_CONSTRAINED, maxPixels)

private const val UN_CONSTRAINED = -1

/**
 * 根据规范计算simple size
 *
 * @param opts
 * @param minSideLength  宽高里最短的长度
 * @param maxPixels 最大的像素数量
 * @return
 */
private fun calcSimpleSize(opts: BitmapFactory.Options, minSideLength: Int, maxPixels: Int): Int {
    val w = opts.outWidth.toDouble()
    val h = opts.outHeight.toDouble()

    val lowerBound = if (maxPixels == UN_CONSTRAINED) 1 else ceil(sqrt(w * h / maxPixels)).toInt()
    val upperBound = if (minSideLength == UN_CONSTRAINED) 128 else min(
        floor(w / minSideLength),
        floor(h / minSideLength)
    ).toInt()

    val initSize = when {
        upperBound < lowerBound -> lowerBound
        else -> when {
            maxPixels == UN_CONSTRAINED && minSideLength == UN_CONSTRAINED -> 1
            minSideLength == UN_CONSTRAINED -> lowerBound
            else -> upperBound
        }
    }

    var roundedSize: Int
    if (initSize <= 8) {
        roundedSize = 1
        while (roundedSize < initSize) {
            roundedSize = roundedSize shl 1
        }
    } else {
        roundedSize = (initSize + 7) / 8 * 8
    }

    return roundedSize
}
