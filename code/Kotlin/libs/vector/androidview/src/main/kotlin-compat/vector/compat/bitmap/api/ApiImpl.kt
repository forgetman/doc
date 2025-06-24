package vector.compat.bitmap.api

import android.graphics.Canvas
import androidx.core.graphics.createBitmap
import vector.app.ext.setAntialias
import vector.compat.bitmap.BitmapCompat

/**
 * @author yuansui
 * @since 2021/6/23
 */
internal class ApiImpl : Api {

    override fun createHardwareBitmap(
        width: Int,
        height: Int,
        strategy: BitmapCompat.RecycleStrategy,
        listener: BitmapCompat.HardwareDrawListener
    ) {
        val bitmap = createBitmap(width, height)

        if (!strategy.auto) {
            strategy.callback = BitmapCompat.RecycleStrategy.Callback {
                listener.onCompleted(bitmap)
            }
        }

        Canvas(bitmap).apply {
            setAntialias()
            listener.onDraw(this, strategy)

            // Avoids warnings in M+.
            if (strategy.auto) {
                setBitmap(null)
                listener.onCompleted(bitmap)
            }
        }
    }

}