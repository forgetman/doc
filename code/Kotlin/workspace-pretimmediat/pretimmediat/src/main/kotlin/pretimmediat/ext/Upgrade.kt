package pretimmediat.ext

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import coroutine.flow.launchIn
import kotlinx.coroutines.flow.flow
import pretimmediat.service.UpgradeService
import vector.ext.startServ

fun Fragment.checkUpgrade() {
    flow {
        context?.startServ<UpgradeService>()
        emit(Unit)
    }.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED).launchIn(this)
}