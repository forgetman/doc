package pretimmediat.viewmodel.inputpiece

import android.app.Application
import kotlinx.coroutines.flow.MutableStateFlow
import pretimmediat.viewmodel.BaseViewModel
import vector.ext.isNotNullOrEmpty

/**
 * @author yuansui
 * @since 2024/6/16
 */
abstract class AbstractPieceViewModel(app: Application) : BaseViewModel(app) {

    protected fun updateIfNeeded(value: String?, stateFlow: MutableStateFlow<String?>): Boolean {
        if (value.isNotNullOrEmpty() && stateFlow.value.isNullOrEmpty()) {
            stateFlow.value = value
            return true
        }
        return false
    }
}