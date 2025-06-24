@file:Suppress("unused")

package image.api.impl

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.lifecycle.Lifecycle
import coil3.Image
import coil3.asDrawable
import coil3.gif.onAnimationEnd
import coil3.gif.repeatCount
import coil3.load
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.crossfade
import coil3.request.transformations
import coil3.target.ImageViewTarget
import coil3.transform.RoundedCornersTransformation
import coil3.transform.Transformation
import image.ImageTransformation
import image.api.Animatable
import image.api.ImageAnimatableCallback
import image.api.ImageApi
import image.api.ImageLoadListener
import image.coil.transformation.BlurTransformation
import image.coil.transformation.CircleImageTransformation
import image.coil.transformation.GrayscaleTransformation
import image.coil.transformation.IrregularImageTransformation
import image.coil.transformation.MultiplyTransformation
import image.drawable.DrawableResource
import sugar.ext.doOnNotZero
import sugar.ext.lifecycle
import java.io.File
import android.graphics.drawable.Animatable as AndroidAnimatable

/**
 * @author yuansui
 * @since 2020-08-25
 */
class ImageApiImpl : ImageApi {

    private lateinit var target: ImageView

    private var crossFadeDuration: Int? = null

    private var source: Any? = null
    private var holder: Any? = null
    private var error: Any? = null
    private var width = 0
    private var height = 0
    private var lifecycle: Lifecycle? = null
    private var allowHardware: Boolean? = null
    private var animatableCallback: ImageAnimatableCallback? = null
    private var listener: ImageLoadListener? = null

    private val transformations = lazy(LazyThreadSafetyMode.NONE) {
        mutableListOf<ImageTransformation>()
    }

    override fun into(v: ImageView) {
        if (source == null) return

        target = v

        target.load(source) {
            this.listener(
                onStart = {
                    listener?.onStart()
                },
                onCancel = {
                    listener?.onCancel()
                },
                onError = { _: ImageRequest, result: ErrorResult ->
                    listener?.onError(DrawableResource(null, DrawableResource.Type.NONE), result.throwable)
                },
                onSuccess = { _: ImageRequest, result: SuccessResult ->
                    listener ?: return@listener

                    val dr = when (val drawable = result.image.asDrawable(v.context.resources)) {
                        is AndroidAnimatable -> {
                            object : DrawableResource(drawable, Type.ANIMATABLE) {

                                override fun start() {
                                    drawable.start()
                                }

                                override fun stop() {
                                    drawable.stop()
                                }

                                override fun resume() {
                                    drawable.start()
                                }

                                override fun isRunning(): Boolean {
                                    return drawable.isRunning
                                }
                            }
                        }

                        else -> DrawableResource(drawable, DrawableResource.Type.BITMAP)
                    }

                    listener?.onSuccess(dr)
                })

            adaptTarget()
            adaptCrossFade()
            adaptPlaceholder()
            adaptError()
            adaptSize()
            adaptLifecycle()
            adaptHardware()
            adaptTransformations()
        }
    }

    override fun source(any: Any?): ImageApi {
        source = any
        return this
    }

    override fun placeholder(any: Any?): ImageApi {
        holder = any
        return this
    }

    override fun error(any: Any?): ImageApi {
        error = any
        return this
    }

    override fun addTransformation(transformation: ImageTransformation): ImageApi {
        transformations.value.add(transformation)
        return this
    }

    override fun size(width: Int, height: Int): ImageApi {
        this.width = width
        this.height = height
        return this
    }

    override fun crossFadeDuration(duration: Int): ImageApi {
        crossFadeDuration = duration
        return this
    }

    override fun lifecycle(lifecycle: Lifecycle): ImageApi {
        this.lifecycle = lifecycle
        return this
    }

    override fun allowHardware(allow: Boolean): ImageApi {
        this.allowHardware = allow
        return this
    }

    override fun listener(listener: ImageLoadListener): ImageApi {
        this.listener = listener
        return this
    }

    override fun skipMemoryCache(skip: Boolean): ImageApi {
        return this
    }

    override fun animatable(callback:ImageAnimatableCallback): ImageApi {
        this.animatableCallback = callback
        return this
    }

    private fun ImageRequest.Builder.adaptTransformations() {
        if (!transformations.isInitialized()) return
        val list = mutableListOf<Transformation>()

        transformations.value.forEach {
            when (it) {
                is ImageTransformation.Shape.Circle -> {
                    list.add(CircleImageTransformation(it.width.toFloat(), it.color))
                }

                is ImageTransformation.Shape.RoundCorner -> {
                    val transformation = RoundedCornersTransformation(
                        it.topLeft.toFloat(),
                        it.topRight.toFloat(),
                        it.bottomLeft.toFloat(),
                        it.bottomRight.toFloat()
                    )
                    list.add(transformation)
                }

                is ImageTransformation.Shape.Irregular -> {
                    list.add(IrregularImageTransformation(target.context, it.resId))
                }

                is ImageTransformation.Effect.Blur -> {
                    list.add(BlurTransformation(target.context, it.radius.toFloat()))
                }

                is ImageTransformation.Effect.Gray -> {
                    list.add(GrayscaleTransformation())
                }

                is ImageTransformation.Effect.Multiply -> {
                    list.add(MultiplyTransformation(target.context, it.mask, it.useAlpha))
                }
            }
        }

        transformations(list)
    }

    private fun ImageRequest.Builder.adaptPlaceholder() {
        when (val h = holder ?: return) {
            is Int -> placeholder(h)
            is Drawable -> placeholder(h)
            is File -> {
                val bitmap = BitmapFactory.decodeFile(h.absolutePath) ?: return
                val drawable = BitmapDrawable(target.resources, bitmap)
                placeholder(drawable)
                bitmap.recycle()
            }

            is ByteArray -> {
                val bitmap = BitmapFactory.decodeByteArray(h, 0, h.size) ?: return
                val drawable = BitmapDrawable(target.resources, bitmap)
                placeholder(drawable)
                bitmap.recycle()
            }

            is Bitmap -> {
                placeholder(BitmapDrawable(target.resources, h))
            }
        }
    }

    private fun ImageRequest.Builder.adaptError() {
        when (val e = error ?: return) {
            is Int -> error(e)
            is Drawable -> error(e)
            is File -> {
                val bitmap = BitmapFactory.decodeFile(e.absolutePath) ?: return
                val drawable = BitmapDrawable(target.resources, bitmap)
                error(drawable)
                bitmap.recycle()
            }

            is ByteArray -> {
                val bitmap = BitmapFactory.decodeByteArray(e, 0, e.size) ?: return
                val drawable = BitmapDrawable(target.resources, bitmap)
                error(drawable)
                bitmap.recycle()
            }

            is Bitmap -> {
                error(BitmapDrawable(target.resources, e))
            }
        }
    }

    private fun ImageRequest.Builder.adaptCrossFade() {
        crossFadeDuration?.let {
            crossfade(it)
        }
    }

    private fun ImageRequest.Builder.adaptSize() {
        doOnNotZero(width, height) { t1, t2 ->
            size(t1, t2)
        }
    }

    private fun ImageRequest.Builder.adaptLifecycle() {
        // 绑定指定的生命周期或者view的生命周期, 否则会自动绑定context的生命周期
        val l: Lifecycle = lifecycle ?: target.lifecycle ?: return
        lifecycle(l)
    }

    private fun ImageRequest.Builder.adaptHardware() {
        allowHardware?.let { allow ->
            allowHardware(allow)
        }
    }

    private fun ImageRequest.Builder.adaptTarget() {
        // 先配置animatable属性
        val animatable = Animatable()
        animatableCallback?.let {
            it.onAnimatable(animatable)
            repeatCount(animatable.loopCount)

            onAnimationEnd {
                animatable.callback?.onAnimationEnd(null)
                animatable.callback = null // 必须清除callback, 因为onAnimationEnd会多回调一次(原因未知)
            }
        }

        target(object : ImageViewTarget(target) {
            override fun onSuccess(result: Image) {
                // 模仿super.onSuccess配置, 加入autoPlay设置
                (view.drawable as? AndroidAnimatable)?.stop()
                view.setImageDrawable(result.asDrawable(view.context.resources))
                if (result !is AndroidAnimatable) return
                val lifecycle = lifecycle ?: target.lifecycle
                lifecycle?.addObserver(this)

                animatable.autoPlay?.let { auto ->
                    if (auto) result.start() else result.stop()
                } ?: kotlin.run {
                    if (lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        result.start()
                    } else {
                        updateAnimation()
                    }
                }

            }
        })
    }
}