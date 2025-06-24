package vector.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast
import vector.app.ext.view.bindForeground
import vector.app.util.Res

/**
 * 加入了自己的[setForeground]机制, 因为系统的api要求最低23
 * @author yuansui
 * @since 2018/2/8
 */
open class ImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatImageView(context, attrs, defStyle) {

    private var foregroundDrawable: Drawable? = null

    init {
        if (isInEditMode) {
            this.setBackgroundColor(Color.LTGRAY)
        }
    }

    override fun setForeground(foreground: Drawable?) {
        if (bindForeground(foregroundDrawable, foreground)) {
            foregroundDrawable = foreground
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        foregroundDrawable?.setBounds(0, 0, measuredWidth, measuredHeight)
    }

    fun setForeground(@DrawableRes id: Int) {
        foreground = Res.getDrawable(context, id)
    }

    /**
     * 只有[foregroundDrawable]设置了之后才会调用[onDraw]
     * @see [vector.app.ext.view.bindForeground]
     */
    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        foregroundDrawable?.draw(canvas)
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()

        if (foregroundDrawable?.isStateful == true) {
            foregroundDrawable?.state = drawableState
        }
    }

    override fun jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState()

        foregroundDrawable?.jumpToCurrentState()
    }

    override fun verifyDrawable(dr: Drawable): Boolean {
        return super.verifyDrawable(dr) || (dr == foregroundDrawable)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isSdkAtLeast(SdkInt.L_21)) {
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                foregroundDrawable?.setHotspot(
                    event.x,
                    event.y
                )
            }
        }

        return super.onTouchEvent(event)
    }
}