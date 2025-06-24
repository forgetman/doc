package test.ui.activity

import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import inject.annotation.creator.Creator
import inject.annotation.creator.Extra
import live.Live
import live.ext.add
import live.ext.get
import live.ext.removeFirst
import logger.L
import test.databinding.ActivityListviewBinding
import test.databinding.LayoutListviewBinding
import test.ext.addBackIcon
import vector.app.databinding.activity.SimpleDBActivityEx
import vector.bindingadapter.bind.Bind
import vector.widget.databinding.scrollable.ScrollableBind
import vector.widget.databinding.scrollable.adapter.DBItemBinder
import vector.widget.databinding.scrollable.adapter.EmptyDBItemBinder
import vector.widget.databinding.scrollable.binding.trigger.ScrollableBindTrigger

/**
 * @author yuansui
 * @since 2019-07-23
 */
@Creator(withTransition = true, forResult = true)
class DiffActivity : SimpleDBActivityEx() {

    val strings = Live<MutableList<String>>()

    @Extra
    var test: Int = 0

    @Extra(true)
    var test2: Int = 0

    @Extra
    var testList = emptyList<Int>()

    private var index = 0

    val positionTrigger = ScrollableBindTrigger.scrollToPosition()

    val itemBinder = StringItemBinder(object : StringItemBinder.Listener {
        override fun onItemClick(item: String) {
            L.www("on click = $item")
        }
    })
    val emptyItemBinder = EmptyItemBinder()
    val onItemClick = ScrollableBind.List.OnItemClick { _, position ->
        L.www("onItemClick = " + strings[position])
    }

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = ActivityListviewBinding.inflate(inflater)
        binding.owner = this
        return binding
    }

    override fun initializeSystemBar() {
        appBar.addBackIcon(this)
    }

    override fun initializeData() {
        val list = mutableListOf<String>()
//        for (i in 0 until 2) {
//            list.add("下标: $i")
//        }
        strings.value = list
    }

    val onPlusClick = Bind.OnClick {
        index++
        strings.add(0, "1111$index")
        positionTrigger.trig(0, false)
    }

    val onReduceClick = Bind.OnClick {
        strings.removeFirst()
    }

    class StringItemBinder(val listener: Listener) : DBItemBinder<String, LayoutListviewBinding>() {

        interface Listener {
            fun onItemClick(item: String)
        }

        override fun onBindBinding(item: String, binding: LayoutListviewBinding, position: Int) {
            binding.item = item
            binding.listener = listener
        }
    }

    class EmptyItemBinder : EmptyDBItemBinder<LayoutListviewBinding>() {

        override fun onBindBinding(binding: LayoutListviewBinding) {
            binding.item = "我是empty"
        }
    }
}