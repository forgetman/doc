package image.api

import android.widget.ImageView
import androidx.lifecycle.Lifecycle
import image.ImageTransformation
import image.drawable.DrawableResource

fun interface ImageAnimatableCallback {
    fun onAnimatable(animatable: Animatable)
}

interface ImageLoadListener {
    fun onStart() {}
    fun onError(dr: DrawableResource?, error: Throwable) {}
    fun onCancel() {}
    fun onSuccess(dr: DrawableResource?) {}
}

/**
 * @author yuansui
 * @since 2020/11/19
 */
interface ImageApi {

    fun source(any: Any?): ImageApi

    fun placeholder(any: Any?): ImageApi

    fun error(any: Any?): ImageApi

    fun addTransformation(transformation: ImageTransformation): ImageApi

    fun size(width: Int, height: Int): ImageApi

    fun crossFadeDuration(duration: Int): ImageApi

    fun lifecycle(lifecycle: Lifecycle): ImageApi

    fun allowHardware(allow: Boolean): ImageApi

    fun listener(listener: ImageLoadListener): ImageApi

    fun skipMemoryCache(skip: Boolean): ImageApi

    fun animatable(callback: ImageAnimatableCallback): ImageApi

    fun into(v: ImageView)
}

class Animatable {
    companion object {
        /**
         * 永久循环
         */
        const val LOOP_FOREVER = -1
    }

    interface AnimationCallback {
        fun onAnimationStart(dr: DrawableResource?) {}
        fun onAnimationEnd(dr: DrawableResource?) {}
    }

    /**
     * 循环数量
     */
    var loopCount: Int = LOOP_FOREVER

    /**
     * 加载完后自动播放
     */
    var autoPlay: Boolean? = null
    var callback: AnimationCallback? = null
}

class ImageApiBuilder internal constructor() {
    companion object {
        private const val CLASS_NAME = "image.api.impl.ImageApiImpl"
    }

    private var source: Any? = null
    private var crossFadeDuration: Int? = null
    private var holder: Any? = null
    private var error: Any? = null
    private var width: Int? = null
    private var height: Int? = null
    private var lifecycle: Lifecycle? = null
    private var allowHardware: Boolean? = null
    private var listener: ImageLoadListener? = null
    private var skipMemoryCache: Boolean? = null
    private var animatableCallback: ImageAnimatableCallback? = null
    private val transformations = lazy(LazyThreadSafetyMode.NONE) {
        mutableListOf<ImageTransformation>()
    }

    fun source(any: Any?): ImageApiBuilder {
        source = any
        return this
    }

    fun placeholder(any: Any?): ImageApiBuilder {
        holder = any
        return this
    }

    fun error(any: Any?): ImageApiBuilder {
        error = any
        return this
    }

    fun addTransformation(transformation: ImageTransformation): ImageApiBuilder {
        transformations.value.add(transformation)
        return this
    }

    fun size(width: Int, height: Int): ImageApiBuilder {
        this.width = width
        this.height = height
        return this
    }

    fun crossFadeDuration(duration: Int): ImageApiBuilder {
        crossFadeDuration = duration
        return this
    }

    fun lifecycle(lifecycle: Lifecycle): ImageApiBuilder {
        this.lifecycle = lifecycle
        return this
    }

    fun allowHardware(allow: Boolean): ImageApiBuilder {
        this.allowHardware = allow
        return this
    }

    fun listener(listener: ImageLoadListener): ImageApiBuilder {
        this.listener = listener
        return this
    }

    fun skipMemoryCache(skip: Boolean): ImageApiBuilder {
        skipMemoryCache = skip
        return this
    }

    fun animatable(callback: ImageAnimatableCallback): ImageApiBuilder {
        animatableCallback = callback
        return this
    }

    internal fun build(): ImageApi {
        val name = Class.forName(CLASS_NAME)
        val inst = name.getDeclaredConstructor().newInstance() as ImageApi

        inst.source(source)
        inst.placeholder(holder)
        inst.error(error)

        val w = width
        val h = height
        if (w != null && h != null) {
            inst.size(w, h)
        }

        crossFadeDuration?.let {
            inst.crossFadeDuration(it)
        }

        if (transformations.isInitialized()) {
            transformations.value.forEach {
                inst.addTransformation(it)
            }
        }

        lifecycle?.let { inst.lifecycle(it) }
        allowHardware?.let { inst.allowHardware(it) }
        listener?.let { inst.listener(it) }
        skipMemoryCache?.let { inst.skipMemoryCache(it) }
        animatableCallback?.let { inst.animatable(it) }

        return inst
    }
}

fun ImageView.load(block: ImageApiBuilder.() -> Unit) {
    val builder = ImageApiBuilder()
    block(builder)
    builder.build().into(this)
}