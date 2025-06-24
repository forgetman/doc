package test.ui.activity

import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import com.airbnb.lottie.LottieAnimationView
import test.R
import test.databinding.ActivityLottieBinding
import test.ext.addBackIcon
import vector.app.databinding.activity.SimpleDBActivityEx
import vector.app.ext.bind.bindView

/**
 * @author yuansui
 * @since 2019/3/25
 */
class LottieActivity : SimpleDBActivityEx() {

    private val lottie by bindView<LottieAnimationView>(R.id.lottie)

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = ActivityLottieBinding.inflate(inflater)
        binding.owner = this
        return binding
    }

    override fun initializeSystemBar() {
        appBar.mid.addText("lottie动画")
        appBar.addBackIcon(this)
    }

    override fun initializeContentView() {
        super.initializeContentView()

        lottie.setAnimation(R.raw.r4)
        lottie.playAnimation()
    }
}