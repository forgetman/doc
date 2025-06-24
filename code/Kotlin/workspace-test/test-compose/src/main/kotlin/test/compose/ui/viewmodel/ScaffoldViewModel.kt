package test.compose.ui.viewmodel

import androidx.lifecycle.viewModelScope
import coroutine.flow.timerFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import vector.app.compose.ui.viewmodel.NavigateTarget
import vector.app.compose.ui.viewmodel.ViewModelEx
import java.util.concurrent.TimeUnit

class ScaffoldViewModel() : ViewModelEx() {

    init {
        timerFlow(2, TimeUnit.SECONDS).onEach {
//            navigateTo(NavigationEvent.Back())
            navigate(NavigateTarget.Forward(route = "paging"))
        }.launchIn(viewModelScope)
    }
}