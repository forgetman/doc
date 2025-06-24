package test.ui.frag

import android.graphics.Color
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.databinding.ViewDataBinding
import inject.annotation.creator.Creator
import inject.annotation.creator.Extra
import live.Live
import test.R
import test.databinding.FragDynamicBinding
import vector.app.databinding.frag.SimpleDBFragEx
import vector.app.ext.bind.bindView

/**
 * @author yuansui
 * @since 2019-07-01
 */
@Creator
class DynamicFragment : SimpleDBFragEx() {

    @Extra
    var index = 0

    private val iv by bindView<ImageView>(R.id.imageView)
    val backColor = Live<Int>()

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = FragDynamicBinding.inflate(inflater)
        binding.owner = this
        return binding
    }

    override fun initializeContentView() {
//        when (index) {
//            0 -> iv.setImageResource(R.drawable.red)
//            1 -> iv.setImageResource(R.drawable.blue)
//            2 -> iv.setImageResource(R.drawable.yellow)
//            3 -> backColor.value = Color.GREEN
//            4 -> iv.setImageResource(R.drawable.black)
//        }
        when (index) {
            0 -> backColor.value = Color.RED
            1 -> backColor.value = Color.BLUE
            2 -> backColor.value = Color.YELLOW
            3 -> backColor.value = Color.GREEN
            4 -> backColor.value = Color.BLACK
        }
    }
}