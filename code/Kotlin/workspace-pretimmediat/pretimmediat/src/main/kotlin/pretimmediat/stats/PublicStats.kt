package pretimmediat.stats

import android.app.Application
import android.content.Context
import coroutine.flow.launchForever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import logger.L
import pretimmediat.network.api.StatsApi
import pretimmediat.network.createApi
import pretimmediat.property.Properties
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast
import vector.datastore.preference.sync
import vector.util.DeviceIdUtil
import vector.util.DeviceUtil
import vector.util.PackageUtil

/**
 * 公共平台统计, 老平台打点
 */
@Suppress("DEPRECATION")
class PublicStats : StatsOption {
    companion object {
        private const val LOG_TAG = "PublicStats"
    }

    private lateinit var context: Context

    override fun init(app: Application) {
        context = app
    }

    override fun onEvent(
        eventName: String,
        userId: String?,
        appSsid: String?,
        map: HashMap<String, String>?
    ) {
        val language = if (isSdkAtLeast(SdkInt.N_24)) {
            context.resources.configuration.locales.get(0).language
        } else {
            context.resources.configuration.locale.language
        }
        createApi<StatsApi>().public(
            userId,
            appSsid,
            eventName,
            Properties.location.sync().get(),
            DeviceUtil.manufacturer,
            DeviceUtil.mobileType,
            DeviceIdUtil.id,
            Properties.gaid.sync().get(),
            DeviceIdUtil.id,
            DeviceUtil.systemVersion,
            System.getProperty("http.agent") ?: "",
            language,
            Properties.location.sync().get(),
            PackageUtil.appVersionCode.toString(),
            PackageUtil.appVersionName
        ).flowOn(Dispatchers.IO).catch { e ->
            L.e(LOG_TAG, "onEvent $eventName", e)
        }.launchForever()
    }

}