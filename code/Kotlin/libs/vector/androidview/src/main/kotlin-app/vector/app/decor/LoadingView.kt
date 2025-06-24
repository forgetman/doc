package vector.app.decor

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import vector.app.androidview.R
import vector.app.ext.asStyle
import vector.app.ext.inflate
import vector.app.os.dimenRes
import vector.widget.ProgressView
import vector.widget.ext.obtainDrawable

/**
 * @author yuansui
 * @since 2018/2/26
 */
@SuppressLint("CustomViewStyleable")
@Suppress("LeakingThis")
open class LoadingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val pv: ProgressView

    init {
        val view = context.inflate(R.layout.layout_progress) as ProgressView
        pv = view
        addView(view)

        context.obtainStyledAttributes(attrs, R.styleable.LoadingView).apply {
            val duration = getInt(R.styleable.LoadingView_loading_duration, 1500)
                .toLong()
            view.setDuration(duration)

            val size = getDimension(
                R.styleable.LoadingView_loading_size,
                R.dimen.loading_icon_size.dimenRes.toPx(context).toFloat()
            ).toInt()

            ConstraintSet().asStyle {
                withTheme(view) {
                    alignCenter()
                }
                constrainWidth(view, size)
                constrainHeight(view, size)
            }.applyToWithoutCustom(this@LoadingView)

            recycle()
        }

        context.obtainStyledAttributes(attrs, R.styleable.LibsVectorCoreRedeclare_ImageView).apply {
            obtainDrawable(R.styleable.LibsVectorCoreRedeclare_ImageView_android_src, context.theme) { drawable ->
                view.setImageDrawable(drawable)
            }
            recycle()
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        when (visibility) {
            View.VISIBLE -> start()
            View.INVISIBLE, View.GONE -> stop()
        }
    }

    private fun start() {
        pv.start()
    }

    private fun stop() {
        pv.stop()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        stop()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (visibility == View.VISIBLE) start()
    }

    fun setDuration(duration: Long) {
        pv.setDuration(duration)
    }
}