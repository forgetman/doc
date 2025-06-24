package test.ui.activity.anim

import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import test.databinding.ActivityAnimBinding
import test.ext.addBackIcon
import vector.app.databinding.activity.SimpleDBActivityEx
import vector.bindingadapter.bind.Bind
import vector.ext.startActivity

const val MAX_INTERVAL_MICROSECONDS = 1000000

/**
 * @author yuansui
 * @since 2019-04-24
 */
class AnimActivity : SimpleDBActivityEx() {

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = ActivityAnimBinding.inflate(inflater)
        binding.owner = this
        return binding
    }

    override fun initializeSystemBar() {
        appBar.addBackIcon(this)
        appBar.mid.addText("动画相关")
    }

    val onFrameClick = Bind.OnClick {
        startActivity<FrameActivity>()
    }

    val onInterpolatorClick = Bind.OnClick {
        startActivity<InterpolatorActivity>()
    }
}