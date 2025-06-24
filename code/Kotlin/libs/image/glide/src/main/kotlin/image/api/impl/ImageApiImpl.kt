@file:Suppress("unused")

package image.api.impl

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.lifecycle.Lifecycle
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.bumptech.glide.integration.webp.decoder.WebpDrawable
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.*
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import image.ImageTransformation
import image.api.Animatable
import image.api.ImageAnimatableCallback
import image.api.ImageApi
import image.api.ImageLoadListener
import image.drawable.DrawableResource
import image.glide.GlideLoader
import image.glide.GlideRequest
import image.glide.transformation.CircleCrop
import sugar.ext.doOnNotZero
import vector.image.glide.transformation.BlurTransformation
import vector.image.glide.transformation.IrregularCrop

/**
 * @author yuansui
 * @since 2018/2/8
 */
class ImageApiImpl : ImageApi {

    private lateinit var target: ImageView

    private var source: Any? = null
    private var holder: Any? = null
    private var error: Any? = null

    private var crossFadeDuration: Int? = null
    private var withViewLifecycle: Boolean? = null
    private var skipMemoryCache: Boolean? = null

    private var width = 0
    private var height = 0

    private var listener: ImageLoadListener? = null
    private var animatableCallback: ImageAnimatableCallback? = null

    private val transformations = lazy(LazyThreadSafetyMode.NONE) {
        mutableListOf<ImageTransformation>()
    }

    override fun into(v: ImageView) {
        target = v

        GlideLoader.with(v)
            .load(source)
            .adaptListener()
            .adaptCrossFade()
            .adaptPlaceHolder()
            .adaptError()
            .adaptSize()
            .adaptMemory()
            .adaptTransformations()
            .into(v)
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
        TODO("Not yet implemented")
    }

    override fun allowHardware(allow: Boolean): ImageApi {
        TODO("Not yet implemented")
    }

    override fun listener(listener: ImageLoadListener): ImageApi {
        this.listener = listener
        return this
    }

    override fun skipMemoryCache(skip: Boolean): ImageApi {
        skipMemoryCache = skip
        return this
    }

    override fun animatable(callback: ImageAnimatableCallback): ImageApi {
        animatableCallback = callback
        return this
    }

    private fun GlideRequest<Drawable>.adaptListener(): GlideRequest<Drawable> {
        return addListener(object : RequestListener<Drawable> {

            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable?>,
                isFirstResource: Boolean
            ): Boolean {
                listener?.onError(DrawableResource(null, DrawableResource.Type.NONE), e ?: IllegalArgumentException())
                return false
            }

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: Target<Drawable?>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                val animatable = Animatable()

                val res: DrawableResource = when (resource) {
                    is GifDrawable -> {
                        animatableCallback?.onAnimatable(animatable)
                        resource.setLoopCount(animatable.loopCount)

                        val dr = object : DrawableResource(resource, Type.ANIMATABLE) {

                            override fun start() {
                                stop()
                                resource.startFromFirstFrame()
                            }

                            override fun stop() {
                                resource.stop()
                            }

                            override fun resume() {
                                resource.start()
                            }

                            override fun isRunning(): Boolean {
                                return resource.isRunning
                            }
                        }
                        val callback = object : Animatable2Compat.AnimationCallback() {

                            override fun onAnimationStart(drawable: Drawable) {
                                animatable.callback?.onAnimationStart(dr)
                            }

                            override fun onAnimationEnd(drawable: Drawable) {
                                animatable.callback?.onAnimationEnd(dr)
                            }
                        }
                        resource.registerAnimationCallback(callback)

                        dr
                    }

                    is WebpDrawable -> {
                        animatableCallback?.onAnimatable(animatable)
                        resource.loopCount = animatable.loopCount

                        val dr = object : DrawableResource(resource, Type.ANIMATABLE) {

                            override fun start() {
                                stop()
                                resource.startFromFirstFrame()
                            }

                            override fun stop() {
                                resource.stop()
                            }

                            override fun resume() {
                                resource.start()
                            }

                            override fun isRunning(): Boolean {
                                return resource.isRunning
                            }
                        }
                        val callback = object : Animatable2Compat.AnimationCallback() {

                            override fun onAnimationStart(drawable: Drawable) {
                                animatable.callback?.onAnimationStart(dr)
                            }

                            override fun onAnimationEnd(drawable: Drawable) {
                                animatable.callback?.onAnimationEnd(dr)
                            }
                        }
                        resource.registerAnimationCallback(callback)

                        dr
                    }

                    else -> DrawableResource(resource, DrawableResource.Type.BITMAP)
                }

                // return false之后, glide会自动播放动画且不一定是从第一帧
                if (animatable.autoPlay == true) res.start() else res.stop()

                listener?.onSuccess(res)
                return false
            }
        })
    }

    private fun GlideRequest<Drawable>.adaptCrossFade(): GlideRequest<Drawable> {
        val opt = DrawableTransitionOptions.withCrossFade(
            DrawableCrossFadeFactory.Builder(
                crossFadeDuration ?: ConfigApiImpl.defaultCrossFadeDuration
            )
                .setCrossFadeEnabled(true)
                .build()
        )
        return transition(opt)
    }

    private fun GlideRequest<Drawable>.adaptPlaceHolder(): GlideRequest<Drawable> {
        return when (val h = holder) {
            is Int -> if (h > 0) this.placeholder(h) else this
            is Drawable -> placeholder(h)
            else -> this
        }
    }

    private fun GlideRequest<Drawable>.adaptSize(): GlideRequest<Drawable> {
        return doOnNotZero(width, height) { t1, t2 ->
            return@doOnNotZero this.override(t1, t2)
        } ?: this
    }

    private fun GlideRequest<Drawable>.adaptMemory(): GlideRequest<Drawable> {
        skipMemoryCache?.let {
            skipMemoryCache(it)
        }
        return this
    }

    private fun GlideRequest<Drawable>.adaptError(): GlideRequest<Drawable> {
        return when (val e = error) {
            is Drawable -> error(e)
            is Int -> error(e)
            else -> {
                // ignore RequestBuilder case
                ConfigApiImpl.defaultError?.let {
                    error(it)
                } ?: this
            }
        }
    }

    private fun GlideRequest<Drawable>.adaptTransformations(): GlideRequest<Drawable> {
        val btfs = mutableListOf<BitmapTransformation>()

        if (this@ImageApiImpl.transformations.isInitialized()) {
            this@ImageApiImpl.transformations.value.forEach {
                when (it) {
                    is ImageTransformation.Shape.Circle -> {
                        btfs.add(CircleCrop(it.width.toFloat(), it.color))
                    }

                    is ImageTransformation.Shape.RoundCorner -> {
                        btfs.add(RoundedCorners(it.topLeft))
                    }

                    is ImageTransformation.Shape.Irregular -> {
                        btfs.add(IrregularCrop(target.context, it.resId))
                    }

                    is ImageTransformation.Effect.Blur -> {
                        btfs.add(BlurTransformation(target.context, it.radius.toFloat()))
                    }

                    is ImageTransformation.Effect.Gray -> {
//                    list.add(GrayscaleTransformation())
                    }

                    is ImageTransformation.Effect.Multiply -> {
//                    list.add(MultiplyTransformation(target.context, it.mask, it.useAlpha))
                    }
                }
            }
        }

        if (btfs.isEmpty()) return this
        return transform(MultiTransformation(btfs))
    }
}
