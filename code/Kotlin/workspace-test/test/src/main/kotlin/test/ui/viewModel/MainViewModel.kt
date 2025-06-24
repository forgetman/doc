package test.ui.viewModel

import android.app.Application
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import vector.app.viewmodel.ViewModelEx

/**
 * @author yuansui
 * @since 2019/4/18
 */
class MainViewModel(app: Application) : ViewModelEx(app) {

    override fun onCreate() {
        val flow = flow {
            for (i in 1..10) {
                delay(1000)
                emit(i)
            }
        }

//        flow.bindScope(viewModelScope)
//                .subscribe {
//                    L.d("msg = $it")
//                }
    }
}