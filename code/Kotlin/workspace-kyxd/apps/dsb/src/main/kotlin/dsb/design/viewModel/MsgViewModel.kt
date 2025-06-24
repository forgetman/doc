package dsb.design.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dsb.design.repo.MessageRepo
import dsb.model.InfoMessage
import eth.ext.bind
import eth.model.Nive
import vector.app.viewmodel.ViewModelEx
import javax.inject.Inject

/**
 * @author yuansui
 * @since 2019/1/21
 */
@HiltViewModel
class MsgViewModel @Inject constructor(private val repo: MessageRepo) : ViewModelEx() {

    private val _data = Nive<List<InfoMessage>>()
    val data: LiveData<List<InfoMessage>> = _data
    private var unreadNumber = 0

    fun getMessage() =
        repo.fetchInfoMessage().bind(_data).launch(viewModelScope)

    override fun onCreate() {
        _data.observe {
            if (unreadNumber == 0) return@observe
            refreshData(it)
        }
    }

    fun updateUnreadNumber(number: Int) {
        unreadNumber = number
        refreshData(data.value ?: return)
    }

    private fun refreshData(list: List<InfoMessage>) {
        list.forEach {
            if (it.type == InfoMessage.Type.SYSTEM) {
                it.unreadNumber.value = unreadNumber
            }
        }
    }
}