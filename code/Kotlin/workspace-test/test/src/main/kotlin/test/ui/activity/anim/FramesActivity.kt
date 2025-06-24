package test.ui.activity.anim

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.ViewDataBinding
import live.Live
import live.ext.div
import live.ext.minus
import live.ext.plus
import live.ext.times
import test.R
import test.databinding.ActivityAnimFramesBinding
import test.ext.addBackIcon
import vector.app.databinding.activity.SimpleDBActivityEx
import vector.app.ext.bind.bindView
import vector.app.ext.drawTextInCenter
import vector.app.ext.setAntialias
import vector.bindingadapter.bind.Bind
import java.util.concurrent.TimeUnit

/**
 * 帧计算
 * @author yuansui
 * @since 2019-04-24
 */
class FrameActivity : SimpleDBActivityEx() {

    private val layout1 by bindView<FrameView>(R.id.frame_layout_1)
    private val layout2 by bindView<FrameView>(R.id.frame_layout_2)
    private val layout3 by bindView<FrameView>(R.id.frame_layout_3)

    val level = Live(10)
    val frame = Live(60L)

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = ActivityAnimFramesBinding.inflate(inflater)
        binding.owner = this
        return binding
    }

    override fun initializeSystemBar() {
        appBar.addBackIcon(this)
        appBar.mid.addText("帧")
    }

    override fun initializeContentView() {
        level.observe(this) {
            layout1.times = it
            layout2.times = it
            layout3.times = it
        }

        frame.observe(this) {
//            disposable?.dispose()
//            disposable = Observable.interval(MAX_INTERVAL_MICROSECONDS / it, TimeUnit.MICROSECONDS)
//                    .applyScheduler()
//                    .bindLifecycle(this)
//                    .subscribe {
//                        layout1.invalidate()
//                        layout2.invalidate()
//                        layout3.invalidate()
//                    }
        }
    }

    val onFrameIncreaseClick = Bind.OnClick {
        var new = frame + 10
        if (new > 100) {
            new = 100
        }
        frame.value = new
    }

    val onFrameReduceClick = Bind.OnClick {
        var new = frame - 10
        if (new < 10) {
            new = 10
        }
        frame.value = new
    }

    val onIncreaseClick = Bind.OnClick {
        var new = level * 10
        if (new > 10000000) {
            new = 10000000
        }
        level.value = new
    }

    val onReduceClick = Bind.OnClick {
        var new = level / 10
        if (new < 10) {
            new = 10
        }
        level.value = new
    }
}

class FrameView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var calcCount = 0
    private var drawCount = 0
    private val paint = Paint().apply {
        color = Color.WHITE
        textSize = 30f
    }

    private var lastFrameTime = System.currentTimeMillis()

    var times = 10

    override fun onDraw(canvas: Canvas) {
        canvas.setAntialias()

        canvas.drawColor(Color.BLUE)

        calcCount++

        val curr = System.currentTimeMillis()
        val interval = curr - lastFrameTime
        if (interval >= TimeUnit.SECONDS.toMillis(1)) {
            drawCount = calcCount
            calcCount = 0
            lastFrameTime = curr
        }


        canvas.drawTextInCenter("FPS = $drawCount", width / 2f, height / 2f, paint)

        for (i in 0..times) {
            "干扰项".plus(i)
        }
    }
}