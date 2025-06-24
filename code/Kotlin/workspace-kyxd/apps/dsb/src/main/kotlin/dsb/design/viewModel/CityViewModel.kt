package dsb.design.viewModel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dsb.design.repo.CityRepo
import dsb.design.ui.adapter.CityViewType
import dsb.model.GroupCity
import eth.ext.bind
import eth.model.Nive
import live.Live
import live.refresh
import vector.app.viewmodel.ViewModelEx
import vector.bindingadapter.bind.Bind
import javax.inject.Inject

/**
 * @author yuansui
 * @since 2019/1/22
 */
@HiltViewModel
class CityViewModel @Inject constructor(private val repo: CityRepo) : ViewModelEx() {

    val data = Nive<List<GroupCity>>()
    val sections = Live<Array<String>>()
    val groupIndex = Live<Int>()

    val onTouchLetter = Bind.SideBar.OnTouchLetter { index, _, _ ->
        groupIndex.value = index
    }

    fun fetchCities() = repo.fetchCities().bind(data).launch(viewModelScope)

    fun refreshLocationCity(name: String?) {
        val list = data.value ?: return
        if (list[0].name == CityViewType.LOCATION.desc) {
            list[0].children[0].name = name
            data.refresh()
        }
    }
}