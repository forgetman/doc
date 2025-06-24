package dsb.design.ui.activity.city

import android.graphics.Color
import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import dagger.hilt.android.AndroidEntryPoint
import dsb.Bus
import dsb.EventId
import dsb.databinding.ActivityCityBinding
import dsb.design.ui.adapter.CityAdapter
import dsb.design.viewModel.CityViewModel
import dsb.ext.addBackIcon
import dsb.ext.withViewState
import dsb.model.City
import lib.base.design.ui.activity.BaseDBActivity
import live.Live
import live.ext.get
import vector.bindingadapter.bind.Bind
import vector.ext.setNavigationBarColor
import vector.os.colorInt

/**
 * 选择城市
 *
 * @author yuansui
 * @since 2019/1/22
 */
@AndroidEntryPoint
class CityActivity : BaseDBActivity<CityViewModel>() {

    val adapter = CityAdapter()
    val expand = Live<Boolean>()

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = ActivityCityBinding.inflate(inflater)
        binding.owner = this
        binding.viewModel = viewModel
        return binding
    }

    override fun initializeSystemBar() {
        appBar.addBackIcon(this)
        appBar.mid.addText { text = "选择城市" }
    }

    override fun flowOfSetup() {
        setNavigationBarColor(Color.WHITE.colorInt)

        viewModel.data.observe(this) {
            // 给sideBar赋值
            viewModel.sections.value = it.map { gc ->
                gc.name
            }.toTypedArray()

            expand.value = true
        }

        viewModel.fetchCities().withViewState(this)

        Bus.get().with(this).onMessage(EventId.CHANGE_CITY) {
            finish()
        }

        Bus.get().with(this).onValue<City>(EventId.LOCATION_CITY) {
            viewModel.refreshLocationCity(it.name)
        }
    }

    val onSearchClick = Bind.OnClick { _ ->
        val list = viewModel.data.value ?: return@OnClick

        val cities = mutableListOf<City>()
        list.filter {
            it.name != "热门" && it.name != "定位"
        }.flatMapTo(cities) {
            it.children
        }
        SearchCityActivityCreator.create(cities).start(this)
    }

    val onChildClick = Bind.GroupList.OnChildItemClick { _, groupPosition, childPosition, _ ->
        Bus.get().send(
            EventId.CHANGE_CITY,
            viewModel.data[groupPosition]?.getChildAt(childPosition)
        )
        true
    }

    override fun onRetryClick() {
        viewModel.fetchCities().withViewState(this)
    }
}