package vector.widget.photoview

import android.graphics.RectF
import android.widget.ImageView
import java.io.Serializable

class Info internal constructor(
    rect: RectF,
    imgRect: RectF,
    widgetRect: RectF,
    val degrees: Float,
    val scaleType: ImageView.ScaleType
) : Serializable {

    // 内部图片在整个手机界面的位置
    val rect = RectF()

    // 控件在窗口的位置
    val imgRect = RectF()
    val widgetRect = RectF()

    init {
        this.rect.set(rect)
        this.imgRect.set(imgRect)
        this.widgetRect.set(widgetRect)
    }
}