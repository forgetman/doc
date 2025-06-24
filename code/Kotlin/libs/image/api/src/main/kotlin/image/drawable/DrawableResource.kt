package image.drawable

import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable

/**
 * @author yuansui
 * @since 2021/8/12
 */
open class DrawableResource(
    val drawable: Drawable?,
    val type: Type,
) : Animatable {

    enum class Type {
        NONE,
        BITMAP,
        ANIMATABLE,
    }

    override fun start() {}
    override fun stop() {}
    override fun isRunning(): Boolean {
        return false
    }

    open fun resume() {}
}