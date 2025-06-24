package test.ui.activity.anim

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnticipateInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import androidx.databinding.ViewDataBinding
import coroutine.flow.intervalFlow
import coroutine.flow.launchIn
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.onEach
import logger.L
import test.R
import test.databinding.ActivityAnimInterpolatorBinding
import test.ext.addBackIcon
import vector.Constants
import vector.app.databinding.activity.SimpleDBActivityEx
import vector.app.ext.bind.bindView
import vector.app.ext.setAntialias
import vector.bindingadapter.bind.Bind
import java.util.concurrent.TimeUnit

typealias OnStop = () -> Unit

/**
 * 插值器效果介绍
 * @author yuansui
 * @since 2019-04-24
 */
class InterpolatorActivity : SimpleDBActivityEx() {

    private val layout by bindView<InterpolatorView>(R.id.interpolator_layout)

    private val interpolators = mutableListOf<Interpolator>(
        LinearInterpolator(),
        AccelerateInterpolator(),
        DecelerateInterpolator(),
        OvershootInterpolator(),
        BounceInterpolator(),
        AnticipateInterpolator()
    )

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = ActivityAnimInterpolatorBinding.inflate(inflater)
        binding.owner = this
        return binding
    }

    override fun initializeSystemBar() {
        appBar.addBackIcon(this)
        appBar.mid.addText("插值器")
    }

    override fun initializeContentView() {
        layout.onStop = {
            stop()
        }

        layout.interpolator = interpolators[0]
    }

    val onChanged = Bind.RadioGroup.OnCheckedChanged { index, _ ->
        layout.interpolator = interpolators[index]
        start()
    }

    private var job: Job? = null
    val onStart = Bind.OnClick {
        start()
    }

    private fun start() {
        job = intervalFlow(MAX_INTERVAL_MICROSECONDS / 60L, TimeUnit.MICROSECONDS)
            .onEach {
                L.d("tick = $it")
                layout.invalidate()
            }.launchIn(this)

//        disposable?.dispose()
//        disposable = Observable.interval(MAX_INTERVAL_MICROSECONDS / 60L, TimeUnit.MICROSECONDS)
////                .applyScheduler()
////                .bindLifecycle(this)
//                .subscribe {
//                    layout.invalidate()
//                }

        layout.start()
    }

    private fun stop() {
//        disposable?.dispose()
        job?.cancel()
    }
}

class InterpolatorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        const val DURATION = 1500f
    }

    var interpolator: Interpolator? = null
    private val paint = Paint().apply {
        color = Color.BLUE
    }

    private var w = 0
    private var h = 0

    private var startTime: Long = 0
    var onStop: OnStop? = null

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        w = measuredWidth
        h = measuredHeight
    }

    override fun onDraw(canvas: Canvas) {
        canvas.setAntialias()

        val interval = System.currentTimeMillis() - startTime
        var timeFactor = interval / DURATION
        if (timeFactor >= Constants.INTERPOLATOR_MAX) {
            onStop?.invoke()
            timeFactor = Constants.INTERPOLATOR_MAX
        }
        val factor = interpolator?.getInterpolation(timeFactor) ?: return
        val position = (w / 2) * factor + w / 4
        L.d("pos = $position")
        L.d("timeFactor = $timeFactor")

        canvas.drawCircle(position, h / 2f, 30f, paint)
    }

    fun start() {
        startTime = System.currentTimeMillis()
    }
}