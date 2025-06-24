package vector.widget.style

import android.widget.ImageView
import androidx.annotation.StyleRes
import vector.app.androidview.R
import vector.widget.ext.obtainColorStateList
import vector.widget.ext.obtainDrawable
import vector.widget.ext.obtainInt

/**
 * @author yuansui
 * @since 2023/2/20
 */
internal class ImageViewStyleApplier(view: ImageView) : StyleApplier<ImageView>(view) {

    companion object {
        private val SCALE_TYPE_ARRAY = arrayOf(
            ImageView.ScaleType.MATRIX,
            ImageView.ScaleType.FIT_XY,
            ImageView.ScaleType.FIT_START,
            ImageView.ScaleType.FIT_CENTER,
            ImageView.ScaleType.FIT_END,
            ImageView.ScaleType.CENTER,
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE
        )
    }

    override fun applyParent(styleId: Int) {
        val applier = ViewStyleApplier(view)
        applier.applyStyle(styleId)
    }

    override fun onInitialize(styleId: Int) {
        context.obtainStyledAttributes(styleId, R.styleable.LibsVectorCoreRedeclare_ImageView).apply {

            obtainInt(R.styleable.LibsVectorCoreRedeclare_ImageView_android_scaleType) { value ->
                view.scaleType = when (value) {
                    in SCALE_TYPE_ARRAY.indices -> SCALE_TYPE_ARRAY[value]
                    else -> ImageView.ScaleType.FIT_CENTER
                }
            }

            obtainColorStateList(R.styleable.LibsVectorCoreRedeclare_ImageView_android_tint, context.theme) { value ->
                view.imageTintList = value
            }

            obtainDrawable(R.styleable.LibsVectorCoreRedeclare_ImageView_android_src, context.theme) { drawable ->
                view.setImageDrawable(drawable)
            }

            recycle()
        }
    }
}

fun ImageView.applyStyle(@StyleRes styleId: Int) {
    val applier = ImageViewStyleApplier(this)
    applier.applyStyle(styleId)
}