package test.ui.activity

import android.graphics.Color
import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import live.Live
import logger.L
import test.R
import test.databinding.ActivityDynamicFragmentBinding
import test.ext.addBackIcon
import test.ui.frag.DynamicFragmentCreator
import test.view.AlphaView
import vector.app.adapter.pager.FragPager
import vector.app.adapter.pager.build
import vector.app.databinding.activity.SimpleDBActivityEx
import vector.app.decor.AppBarStyle
import vector.app.ext.bind.bindView
import vector.app.os.colorInt
import vector.widget.compat.viewpager.transformer.StackTransformer
import vector.widget.databinding.viewpager.ViewPagerBind

/**
 * @author yuansui
 * @since 2019-07-01
 */
class FragStateActivity : SimpleDBActivityEx() {

    private val titles = listOf("1", "2", "3", "4", "5")
    val pager = FragPager.build(titles.size, titles) {
        DynamicFragmentCreator.create(it).get()
    }

    override fun getAppBarStyle(): AppBarStyle {
        return AppBarStyle.FLOATING
    }

    private val alphaView by bindView<AlphaView>(R.id.dynamic_layout_alpha)
    val currentItem = Live<Int>()

    val transformer = StackTransformer()

    val onPageIntent = ViewPagerBind.OnPageIntent {
        L.www("onPageIntent = $it")
    }

    val onPageScrolled =
        ViewPagerBind.OnPageScrolled { _, nextPosition, positionOffset, _ ->
            val nextColor = when (nextPosition) {
                0 -> Color.RED
                1 -> Color.BLUE
                2 -> Color.YELLOW
                3 -> Color.GREEN
                4 -> Color.BLACK
                else -> 0
            }
            alphaView.onChanged(positionOffset, nextColor)
        }

    val onPageSelected = ViewPagerBind.OnPageSelected {
        val nextColor = when (it) {
            0 -> Color.RED
            1 -> Color.BLUE
            2 -> Color.YELLOW
            3 -> Color.GREEN
            4 -> Color.BLACK
            else -> 0
        }
        alphaView.setColor(nextColor)

        L.www("onPageSelected = $it")
    }

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = ActivityDynamicFragmentBinding.inflate(inflater)
        binding.owner = this
        return binding
    }

    override fun initializeSystemBar() {
        appBar.mid.addText {
            text = "首页效果展示"
            textColor = Color.WHITE.colorInt
        }
        appBar.addBackIcon(this)
        appBar.setBackgroundColor(Color.TRANSPARENT)
    }
}