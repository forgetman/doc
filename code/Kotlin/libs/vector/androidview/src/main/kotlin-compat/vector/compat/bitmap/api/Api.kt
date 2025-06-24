package vector.compat.bitmap.api

import vector.compat.bitmap.BitmapCompat

/**
 * @author yuansui
 * @since 2023/3/21
 */
internal interface Api {

    fun createHardwareBitmap(
        width: Int,
        height: Int,
        strategy: BitmapCompat.RecycleStrategy,
        listener: BitmapCompat.HardwareDrawListener
    )
}