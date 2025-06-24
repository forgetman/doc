package test.ui.activity

import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import live.Live
import test.databinding.ActivityWebBinding
import vector.app.databinding.activity.SimpleDBActivityEx

/**
 * @author yuansui
 * @since 2019/3/25
 */
class WebActivity : SimpleDBActivityEx() {

    val url = Live<String>("https://www.cailuw.com/long-post/12712669223870464?h=Xq2lxELr6OrG")

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = ActivityWebBinding.inflate(inflater)
        binding.owner = this
        return binding
    }

    override fun initializeSystemBar() {
        appBar.mid.addText("web")
    }
}