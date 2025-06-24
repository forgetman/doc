package catroom.service

import android.content.Intent
import catroom.network.api.RoomApi
import catroom.network.createApi
import coroutine.flow.launchIn
import eth.ext.asProgressFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import logger.L
import sugar.ext.Console
import vector.ext.isNotNullOrEmpty
import vector.service.ServiceEx
import vector.util.DeviceIdUtil
import vector.util.PackageUtil

/**
 * 版本升级服务
 */
class CheckUpgradeService : ServiceEx() {

    companion object {
        private const val LOG_TAG = "CheckUpgradeService"
    }

    private var downloadJob: Job? = null

    @Suppress("OPT_IN_USAGE")
    override fun onHandleIntent(intent: Intent) {
        val version = PackageUtil.appVersionCode.toInt()
        L.d(LOG_TAG, "checkUpgrade, current version = $version")
        downloadJob?.cancel()
        downloadJob = createApi<RoomApi>().checkUpgrade(
            DeviceIdUtil.id,
            version
        ).flatMapConcat { upgrade ->
            L.d(LOG_TAG, "checkUpgrade, result = $upgrade")
            if (upgrade.upgradation == 1 && upgrade.url.isNotNullOrEmpty()) {
                createApi<RoomApi>().download(upgrade.url).asProgressFlow().onProgress { progress ->
                    L.d(LOG_TAG, "checkUpgrade, download progress = ${progress.progress}")
                }
            } else throw Exception("no upgrade")
        }.onEach { result ->
            L.d(LOG_TAG, "checkUpgrade, download success")
            Console.writeAsSh("pm install -r ${result.toFile().absolutePath}")
        }.catch { e ->
            L.e(LOG_TAG, "checkUpgrade", e)
        }.flowOn(Dispatchers.IO).launchIn(this)
    }
}