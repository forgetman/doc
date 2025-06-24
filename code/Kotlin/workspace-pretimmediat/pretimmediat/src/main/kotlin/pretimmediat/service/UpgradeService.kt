package pretimmediat.service

import android.content.Intent
import coroutine.flow.launchIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import logger.L
import pretimmediat.bus.sendMessage
import pretimmediat.def.Constants
import pretimmediat.network.api.DeviceApi
import pretimmediat.network.createApi
import vector.service.ServiceEx
import vector.util.DeviceIdUtil
import vector.util.PackageUtil

/**
 * 升级服务
 */
class UpgradeService : ServiceEx() {

    companion object {
        private const val LOG_TAG = "UpgradeService"
    }

    private var checkJob: Job? = null

    override fun onHandleIntent(intent: Intent) {
        check()
    }

    private fun check() {
        if (checkJob != null) return

        checkJob = createApi<DeviceApi>().appInfo(
            PackageUtil.appVersionName,
            PackageUtil.appVersionCode.toString(),
            DeviceIdUtil.id
        ).flowOn(Dispatchers.IO).catch { e ->
            L.e(LOG_TAG, "check", e)
        }.onEach { info ->
            L.d(LOG_TAG, "get appInfo, info = $info")
            when (info.forceUpdateFlag) {
                "1" -> {
                    // 强制更新
                    sendMessage(Constants.Bus.FORCE_UPDATE_DIALOG, info)
                }

                "2" -> {
                    // 提示更新
                    sendMessage(Constants.Bus.UPDATE_DIALOG, info)
                }
            }

            checkJob = null
        }.launchIn(this)
    }
}