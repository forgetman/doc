package test.ui.activity.anim

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.ViewDataBinding
import test.databinding.ActivityAnimTypeBinding
import test.ext.addBackIcon
import vector.app.databinding.activity.SimpleDBActivityEx
import vector.app.ext.setAntialias

/**
 * 动画类型: 两种
 * @author yuansui
 * @since 2019-04-24
 */
class TypeActivity : SimpleDBActivityEx() {

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = ActivityAnimTypeBinding.inflate(inflater)
        binding.owner = this
        return binding
    }

    override fun initializeSystemBar() {
        appBar.addBackIcon(this)
        appBar.mid.addText("类型")
    }

    override fun initializeContentView() {
//        Observable.interval(MAX_INTERVAL_MICROSECONDS / 60L, TimeUnit.MICROSECONDS)
//                .applyScheduler()
//                .bindLifecycle(this)
//                .subscribe {
//
//                }
    }
}

class TypeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var w = 0
    private var h = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        w = measuredWidth
        h = measuredHeight
    }

    override fun onDraw(canvas: Canvas) {
        canvas.setAntialias()
    }
}
