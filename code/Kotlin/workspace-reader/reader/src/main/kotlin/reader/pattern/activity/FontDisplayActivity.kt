package reader.pattern.activity

import android.graphics.Typeface
import android.view.LayoutInflater
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import coroutine.flow.launchIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import reader.App
import reader.R
import reader.databinding.ActivityFontDisplayBinding
import reader.datastore.Settings
import reader.def.FontType
import reader.ext.addBackIcon
import reader.model.FontDisplay
import reader.pattern.adapter.FontDisplayItemItemBinder
import vector.app.databinding.activity.SimpleDBActivityEx
import vector.app.databinding.annotation.LayoutBindingClass
import vector.app.ext.bind.bindView
import vector.datastore.preference.asEnumFlow
import vector.datastore.preference.putEnum
import vector.widget.databinding.scrollable.ScrollableBind
import kotlin.enums.enumEntries

/**
 * @author yuansui
 * @since 2019-11-14
 */
@LayoutBindingClass<ActivityFontDisplayBinding>
class FontDisplayActivity : SimpleDBActivityEx() {

    val itemBinder = FontDisplayItemItemBinder()
    val data: StateFlow<List<FontDisplay>> = Settings.fontType.asEnumFlow<FontType>().map { type ->
        enumEntries<FontType>().map {
            if (type == it) {
                FontDisplay(it, true)
            } else {
                FontDisplay(it, false)
            }
        }
    }.stateIn(lifecycleScope, SharingStarted.WhileSubscribed(), emptyList())

    private val tv by bindView<TextView>(R.id.tv_display)

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = ActivityFontDisplayBinding.inflate(inflater)
        binding.owner = this
        return binding
    }

    override fun initializeSystemBar() {
        appBar.addBackIcon(this)
        appBar.mid.addText("字体设置")
    }

    override fun initializeContentView() {
        Settings.fontType.asEnumFlow<FontType>().filterNotNull().distinctUntilChanged().onEach {
            tv.typeface = getTypeface(it.path)
        }.flowOn(Dispatchers.Main).launchIn(this)
    }

    val onItemClick = ScrollableBind.List.OnItemClick { _, position ->
        val item = data.value.getOrNull(position) ?: return@OnItemClick
        lifecycleScope.launch {
            Settings.fontType.putEnum(item.type)
        }
    }

    private fun getTypeface(path: String?): Typeface {
        return if (path == null) {
            Typeface.DEFAULT
        } else {
            Typeface.createFromAsset(App.context.assets, path)
        }
    }
}