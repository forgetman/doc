@file:Suppress("unused")

package vector.bindingadapter

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import androidx.lifecycle.Lifecycle
import image.ImageTransformation
import image.api.ImageAnimatableCallback
import image.api.ImageLoadListener
import image.api.load
import vector.app.ext.view.recycle
import vector.app.util.toColorStateList
import vector.bindingadapter.trigger.ImageRecycleTrigger
import vector.os.Size

/**
 * @author yuansui
 * @since 2018/1/26
 */
object ImageViewBinding {

    private const val SOURCE = BINDING_PREFIX + "imageView_source"
    private const val ERROR_SOURCE = BINDING_PREFIX + "imageView_error"
    private const val PLACEHOLDER = BINDING_PREFIX + "imageView_placeholder"


    private const val TRANSFORMATIONS = BINDING_PREFIX + "imageView_transformations"
    private const val TRANSFORMATION = BINDING_PREFIX + "imageView_transformation"

    private const val RESIZE = BINDING_PREFIX + "imageView_resize"

    private const val DURATION = BINDING_PREFIX + "imageView_fadeDuration"

    private const val BITMAP = BINDING_PREFIX + "imageView_bitmap"

    private const val TRIGGER_RECYCLE = BINDING_PREFIX + "imageView_trigger_recycle"

    private const val LIFECYCLE = BINDING_PREFIX + "imageView_lifecycle"
    private const val ALLOW_HARDWARE = BINDING_PREFIX + "imageView_allowHardware"

    private const val LISTENER = BINDING_PREFIX + "imageView_listener"
    private const val ANIMATABLE = BINDING_PREFIX + "imageView_animatable"

    private const val TINT_COLOR_RES = BINDING_PREFIX + "imageView_tintColorRes"

    private const val SRC = BINDING_PREFIX + "imageView_src"

    @JvmStatic
    @BindingAdapter(SRC)
    fun setImageResource(view: ImageView, @DrawableRes id: Int) {
        view.setImageResource(id)
    }

    @JvmStatic
    @BindingAdapter(TINT_COLOR_RES)
    fun setTintColorRes(view: ImageView, @ColorRes id: Int) {
        view.imageTintList = id.toColorStateList(view.context)
    }

    @JvmStatic
    @BindingAdapter(
        SOURCE,
        ERROR_SOURCE,
        PLACEHOLDER,
        TRANSFORMATION,
        TRANSFORMATIONS,
        RESIZE,
        DURATION,
        LIFECYCLE,
        ALLOW_HARDWARE,
        LISTENER,
        ANIMATABLE,
        requireAll = false
    )
    fun load(
        view: ImageView,
        source: Any?,
        error: Any?,
        placeholder: Any?,
        transformation: ImageTransformation?,
        transformations: List<ImageTransformation>?,
        resize: Size?,
        duration: Int?,
        lifecycle: Lifecycle?,
        allowHardware: Boolean?,
        listener: ImageLoadListener?,
        animatableCallback: ImageAnimatableCallback?
    ) {
        view.load {
            source(source)
            placeholder(placeholder)
            error(error)

            listener?.let {
                listener(it)
            }

            animatableCallback?.let {
                animatable(it)
            }

            if (duration != null) crossFadeDuration(duration)

            transformation?.let { addTransformation(it) }
            transformations?.forEach {
                addTransformation(it)
            }

            resize?.let { size(it.width, it.height) }

            lifecycle?.let { lifecycle(it) }
            allowHardware?.let { allowHardware(it) }
        }
    }

    @JvmStatic
    @BindingAdapter(BITMAP)
    fun setBitmap(view: ImageView, bitmap: Bitmap?) {
        if (bitmap == null) return
        view.recycle()
        view.setImageBitmap(bitmap)
    }

    @JvmStatic
    @BindingAdapter(TRIGGER_RECYCLE)
    fun setTriggerRecycle(view: ImageView, trigger: ImageRecycleTrigger) {
        trigger.observe {
            view.recycle()
        }
    }
}