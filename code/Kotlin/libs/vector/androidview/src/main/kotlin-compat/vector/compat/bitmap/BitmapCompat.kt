package vector.compat.bitmap

import android.graphics.Bitmap
import android.graphics.Canvas
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast
import vector.compat.bitmap.api.Api
import vector.compat.bitmap.api.Api28Impl
import vector.compat.bitmap.api.ApiImpl

object BitmapCompat {

    interface HardwareDrawListener {
        fun onDraw(canvas: Canvas, strategy: RecycleStrategy)
        fun onCompleted(bitmap: Bitmap)
    }

    /**
     * 回收策略
     * @param auto 是否允许自动回收canvas
     */
    class RecycleStrategy(internal val auto: Boolean) {

        fun interface Callback {
            fun onRecycle()
        }

        internal var callback: Callback? = null

        fun recycle() {
            if (auto) return
            callback?.onRecycle()
            callback = null
        }
    }

    private val api: Api = when {
        isSdkAtLeast(SdkInt.P_28) -> Api28Impl()
        else -> ApiImpl()
    }

    fun createHardwareBitmap(
        width: Int,
        height: Int,
        strategy: RecycleStrategy,
        listener: HardwareDrawListener
    ) {
        return api.createHardwareBitmap(width, height, strategy, listener)
    }
}