package dsb.design.ui.activity

import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import dsb.R
import dsb.SpApp
import dsb.databinding.ActivityNewTipBinding
import dsb.design.ui.adapter.NewTipItemBinder
import lib.base.design.ui.activity.BaseSimpleDBActivity
import live.Live
import vector.ext.startActivity

/**
 * @author yuansui
 * @since 2019/2/21
 */
class NewTipActivity : BaseSimpleDBActivity() {

    val itemBinder = NewTipItemBinder(object : NewTipItemBinder.Listener {
        override fun onClick() {
            if (index.value == data.lastIndex) {
                startActivity<MainActivity>()
                finish()
            }
        }
    })
    val data = listOf(
        R.drawable.new_tip_0,
        R.drawable.new_tip_1,
        R.drawable.new_tip_2
    )
    val index = Live<Int>()

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = ActivityNewTipBinding.inflate(inflater)
        binding.owner = this
        return binding
    }

    override fun flowOfSetup() {
        SpApp.put(SpApp.SHOW_NEW_TIP_ON_230, true)
    }
}