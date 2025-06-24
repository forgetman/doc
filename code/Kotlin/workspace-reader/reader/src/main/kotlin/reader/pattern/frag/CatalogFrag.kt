package reader.pattern.frag

import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.MediatorLiveData
import live.Live
import live.ext.get
import live.ext.lastIndex
import live.ext.requiredValue
import reader.R
import reader.databinding.FragCatalogBinding
import reader.model.Chapter
import reader.pattern.adapter.ChaptersItemItemBinder
import vector.app.databinding.annotation.LayoutBindingClass
import vector.app.databinding.frag.SimpleDBFragEx
import vector.app.ext.setOnFullScreenGestureChangedListener
import vector.app.fitter.FitStrategy
import vector.app.fitter.Mode
import vector.app.os.dimenRes
import vector.app.os.dp
import vector.app.util.Screen
import vector.app.util.toColor
import vector.bindingadapter.bind.Bind
import vector.compat.notch.NotchInScreenCompat
import vector.widget.databinding.scrollable.ScrollableBind
import vector.widget.databinding.scrollable.binding.trigger.ScrollableBindTrigger
import vector.widget.scrollable.decoration.Decoration

typealias OnChapterSelected = (index: Int) -> Unit

/**
 * 章节目录
 * @author yuansui
 * @since 2018/9/7
 */
@FitStrategy(Mode.FULL_SCREEN)
@LayoutBindingClass<FragCatalogBinding>
class CatalogFrag : SimpleDBFragEx() {

    var bookId: String? = null
    val data = Live<List<Chapter>>()
    var onSelected: OnChapterSelected? = null
    var lastSelectedIndex: Int = 0

    val itemBinder = ChaptersItemItemBinder()
    val decoration by lazy {
        Decoration.linear {
            val margin = R.dimen.margin_level_2.dimenRes.toPx(this@CatalogFrag)
            drawTop = false
            drawBottom = false

            marginStart = margin

            color = R.color.divider.toColor(context)
            size = 0.5f.dp.toPx(context).toInt()
        }
    }
    val trigger = ScrollableBindTrigger.scrollToPosition()

    private val notchMargin = Live<Int?>()
    private val fullScreenMargin = Live<Int?>()
    val topMargin = MediatorLiveData<Int>().apply {
        addSource(notchMargin) {
            val bottom = it ?: 0
            value = bottom + Screen.statusBarHeight + fullScreenMargin.requiredValue()
        }

        addSource(fullScreenMargin) {
            if (it == null) return@addSource
            value = notchMargin.requiredValue() + Screen.statusBarHeight + it
        }
    }

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = FragCatalogBinding.inflate(inflater)
        binding.owner = this
        return binding
    }

    override fun initializeContentView() {
        activity?.let { act ->
            NotchInScreenCompat.applyListener(act) {
                notchMargin.value = it?.bottom
            }

            act.setOnFullScreenGestureChangedListener { _, insetBottom ->
                fullScreenMargin.value = insetBottom
            }
        }
    }

    val onToTopClick = Bind.OnClick {
        trigger.trig(0)
    }

    val onToBottomClick = Bind.OnClick {
        trigger.trig(data.lastIndex)
    }

    fun refresh(new: List<Chapter>) {
        data.value = new
    }

    fun updateSelectedIndex(index: Int, scrollToIndex: Boolean) {
        if (scrollToIndex) trigger.trig(index, false)

        data[lastSelectedIndex]?.focus?.value = false
        data[index]?.focus?.value = true

        lastSelectedIndex = index
    }

    val onItemClick = ScrollableBind.List.OnItemClick { _, position ->
        onSelected?.invoke(position)
        updateSelectedIndex(position, false)
    }
}