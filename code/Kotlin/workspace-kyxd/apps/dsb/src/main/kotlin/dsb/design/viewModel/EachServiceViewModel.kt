package dsb.design.viewModel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dsb.design.repo.ServiceRepo
import dsb.design.ui.itembinder.Services
import dsb.model.Service
import eth.ext.bind
import eth.model.Nive
import vector.app.viewmodel.ViewModelEx
import javax.inject.Inject

/**
 * @author yuansui
 * @since 2020-06-23
 */
@HiltViewModel
class EachServiceViewModel @Inject constructor(private val repo: ServiceRepo) :
    ViewModelEx() {

    private val services = Nive<List<Service>>()
    val data = MediatorLiveData<List<Any>>().apply {
        addSource(services) {
            // add header
            val list = mutableListOf<Any>()
            list.add(Services.Header())
            list.addAll(it)
            value = list
        }
    }

    fun fetchData() =
        repo.fetchData().bind(services).launch(viewModelScope)
}