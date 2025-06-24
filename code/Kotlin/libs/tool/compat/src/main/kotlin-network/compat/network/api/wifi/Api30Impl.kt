package compat.network.api.wifi

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import androidx.annotation.RequiresApi
import compat.ext.wifi
import compat.network.def.listener.wifi.WifiScanResultListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.math.roundToInt

/**
 * @author yuansui
 * @since 2022/8/1
 *
 * RequiresPermission Manifest.permission.ACCESS_FINE_LOCATION
 */
@SuppressLint("MissingPermission") // Manifest.permission.ACCESS_WIFI_STATE
@RequiresApi(Build.VERSION_CODES.R)
internal class Api30Impl : Api by Api29Impl() {

    private val scanResultJobs by lazy { hashMapOf<WifiScanResultListener, Job>() }
    private val scanCallbacks by lazy { hashMapOf<WifiScanResultListener, WifiManager.ScanResultsCallback>() }

    override fun calculateSignalLevel(context: Context, rssi: Int, numLevels: Int): Int {
        val wifi = context.wifi()
        val max = wifi.maxSignalLevel
        val level = wifi.calculateSignalLevel(rssi)
        val rate = level / max.toFloat()
        return (rate * (numLevels - 1)).roundToInt()
    }


    override fun registerScanResultListener(context: Context, listener: WifiScanResultListener) {
        if (scanCallbacks.containsKey(listener)) return

        val wifi = context.wifi()
        val callback = object : WifiManager.ScanResultsCallback() {
            override fun onScanResultsAvailable() {
                @Suppress("OPT_IN_USAGE")
                val job = flow {
                    emit(getScanResults(context))
                }.flowOn(Dispatchers.IO).onEach {
                    listener.onScanResultChanged(it)
                    scanResultJobs.remove(listener)
                }.flowOn(Dispatchers.Main).launchIn(GlobalScope)

                scanResultJobs[listener] = job
            }
        }
        wifi.registerScanResultsCallback(context.mainExecutor, callback)
        scanCallbacks[listener] = callback

        // 如果不主动扫描的话, 需要用户在WIFI设置里手动刷新才能收到结果
        startScan(context)
    }

    override fun unregisterScanResultListener(context: Context, listener: WifiScanResultListener) {
        scanCallbacks[listener]?.let { callback ->
            context.wifi().unregisterScanResultsCallback(callback)
            scanCallbacks.remove(listener)
        }

        scanResultJobs[listener]?.let { job ->
            job.cancel()
            scanResultJobs.remove(listener)
        }
    }
}