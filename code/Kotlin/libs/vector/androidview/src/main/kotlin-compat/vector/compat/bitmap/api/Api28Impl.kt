package vector.compat.bitmap.api

import android.graphics.Bitmap
import android.graphics.Picture
import android.os.Build
import androidx.annotation.RequiresApi
import vector.compat.bitmap.BitmapCompat

/**
 * @author yuansui
 * @since 2021/6/23
 */
@RequiresApi(Build.VERSION_CODES.P)
internal class Api28Impl : Api {

    override fun createHardwareBitmap(
        width: Int,
        height: Int,
        strategy: BitmapCompat.RecycleStrategy,
        listener: BitmapCompat.HardwareDrawListener
    ) {
        val pic = Picture()
        val canvas = pic.beginRecording(width, height)

        if (strategy.auto) {
            try {
                listener.onDraw(canvas, strategy)
            } finally {
                pic.endRecording()
                listener.onCompleted(Bitmap.createBitmap(pic))
            }
        } else {
            strategy.callback = BitmapCompat.RecycleStrategy.Callback {
                pic.endRecording()
                listener.onCompleted(Bitmap.createBitmap(pic))
            }
            listener.onDraw(canvas, strategy)
        }
    }
}