package dsb.design.ui.activity.city

import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import dsb.Bus
import dsb.EventId
import dsb.databinding.ActivitySearchCityBinding
import dsb.databinding.LayoutCitySearchBarBinding
import dsb.design.ui.itembinder.SearchCityItemBinder
import dsb.ext.addBackIcon
import dsb.model.City
import inject.annotation.creator.Creator
import inject.annotation.creator.Extra
import lib.base.design.ui.activity.BaseSimpleDBActivity
import live.Live
import live.ext.get
import vector.bindingadapter.bind.Bind
import vector.util.LayoutParamsFactory
import vector.util.MATCH

/**
 * @author yuansui
 * @since 2019/1/23
 */
@Creator
class SearchCityActivity : BaseSimpleDBActivity() {

    @Extra
    lateinit var cities: List<City>

    val data = Live<List<City>>()
    val itemBinder = SearchCityItemBinder()

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = ActivitySearchCityBinding.inflate(inflater)
        binding.owner = this
        return binding
    }

    override fun initializeSystemBar() {
        appBar.addBackIcon(this)

        val binding = LayoutCitySearchBarBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.owner = this
        appBar.left.addView(binding.root, LayoutParamsFactory.viewGroup(MATCH, MATCH))
    }

    val textChanged = Bind.Text.TextChanged {
        after { text ->
            if (text.isNullOrEmpty()) {
                data.value = emptyList()
                return@after
            }

            val filterList = cities.filter {
                it.name?.contains(text) == true || it.spell?.contains(text) == true
            }
            data.value = filterList
        }
    }

    val onItemClick = ScrollableBind.List.OnItemClick { position ->
        finish()
        val item = data[position] ?: return@OnItemClick
        Bus.get().send(EventId.CHANGE_CITY, item)
    }
}