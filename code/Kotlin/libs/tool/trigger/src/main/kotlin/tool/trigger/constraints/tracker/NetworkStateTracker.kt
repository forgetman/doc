package tool.trigger.constraints.tracker

import android.content.Context
import compat.network.NetworkCompat
import compat.network.def.NetworkState
import compat.network.def.listener.NetworkListener
import logger.L

/**
 * 网络状态(有网)
 */
internal class NetworkStateTracker(context: Context) : ConstraintTracker<NetworkState>(context) {

    companion object {
        private const val LOG_TAG = "NetworkStateTracker"
    }

    private val listener = object : NetworkListener {
        override fun onConnectStateChanged(state: NetworkState) {
            L.d(LOG_TAG, "onConnectStateChanged, state: $state")
            this@NetworkStateTracker.state = state
        }
    }

    override val initialState: NetworkState
        get() = NetworkState.Idle

    override fun startTracking() {
        NetworkCompat.registerListener(appContext, listener)
        // 主动获取一次状态
        NetworkCompat.getActiveNetworkState(appContext).let {
            L.d(LOG_TAG, "startTracking, state: $it")
            state = it
        }
    }

    override fun stopTracking() {
        NetworkCompat.unregisterListener(appContext, listener)
    }
}