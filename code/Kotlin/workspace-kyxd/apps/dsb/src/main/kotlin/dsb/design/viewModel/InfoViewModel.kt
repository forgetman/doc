package dsb.design.viewModel

import androidx.fragment.app.Fragment
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dsb.design.repo.InfoRepo
import dsb.design.ui.frag.EachInfoFragCreator
import dsb.model.Category
import eth.ext.bind
import eth.model.Nive
import vector.app.adapter.pager.FragPager
import vector.app.adapter.pager.PagerConstructor
import vector.app.adapter.pager.fragPagerListOf
import vector.app.viewmodel.ViewModelEx
import javax.inject.Inject

/**
 * @author yuansui
 * @since 2019/1/17
 */
@HiltViewModel
class InfoViewModel @Inject constructor(private val repo: InfoRepo) : ViewModelEx() {

    val categories = Nive<List<Category>>()

    val pager = MediatorLiveData<FragPager>().apply {
        addSource(categories) { data ->
            val titles = mutableListOf<String>()
            val list = mutableListOf<PagerConstructor<Fragment>>()
            data?.forEach { category ->
                titles.add(category.name.orEmpty())
                list.add { EachInfoFragCreator.create(category.id).get() }
            }

            value = fragPagerListOf(list, titles)
        }
    }

    fun fetchCategory() =
        repo.fetchCategory().bind(categories).launch(viewModelScope)
}