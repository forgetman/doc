@file:Suppress("unused")

package bluetoothle.central.scanner

import android.content.Context
import android.location.LocationManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import bluetoothle.central.model.ScanResult
import bluetoothle.def.toUUID
import compat.bluetooth.BluetoothCompat
import coroutine.flow.timerFlow
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import logger.L
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat
import no.nordicsemi.android.support.v18.scanner.ScanCallback
import no.nordicsemi.android.support.v18.scanner.ScanRecord
import no.nordicsemi.android.support.v18.scanner.ScanSettings
import sugar.ext.self
import sugar.ext.systemService
import java.util.*
import java.util.concurrent.TimeUnit
import no.nordicsemi.android.support.v18.scanner.ScanResult as NordicsemiScanResult

private typealias StartAction = () -> Unit
private typealias ResultAction = (result: ScanResult) -> Unit
private typealias ScanAction = (result: ScanResult) -> Unit
private typealias EndAction = (results: List<ScanResult>) -> Unit
private typealias ErrorAction = (errorCode: Int) -> Unit

class Scanner private constructor(
    private val context: Context,
    private val address: String?,
    private val uuids: List<UUID>,
    private val timeout: Long = 0L,
    private val listener: Listener,
    lifecycle: Lifecycle?,
    scanMode: ScanMode,
    private val loggable: Boolean
) {

    companion object {
        private const val LOG_TAG = "BleScanner"

        internal fun build(context: Context, action: Builder.() -> Unit): Scanner {
            val builder = Builder()
            action(builder)
            return builder.build(context)
        }

        const val SCAN_FAILED_ALREADY_STARTED = 1

        /**
         * Fails to start scan as app cannot be registered.
         */
        const val SCAN_FAILED_APPLICATION_REGISTRATION_FAILED = 2

        /**
         * Fails to start scan due an internal error
         */
        const val SCAN_FAILED_INTERNAL_ERROR = 3

        /**
         * Fails to start power optimized scan as this feature is not supported.
         */
        const val SCAN_FAILED_FEATURE_UNSUPPORTED = 4

        /**
         * Fails to start scan as it is out of hardware resources.
         */
        const val SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES = 5

        /**
         * Fails to start scan as application tries to scan too frequently.
         */
        const val SCAN_FAILED_SCANNING_TOO_FREQUENTLY = 6

        /**
         *  fails to start scan as gps is disabled
         */
        const val SCAN_FAILED_GPS_DISABLED = 7

        /**
         *  fails to start scan as bluetooth adapter is disabled
         */
        const val SCAN_FAILED_BT_ADAPTER_DISABLED = 8

        /**
         * fails to start scan as bluetooth adapter is close/reopen failed
         */
        const val SCAN_FAILED_BT_ADAPTER_RESET = 9
    }

    interface Listener {
        fun onScanStart() {}
        fun onScanEnd(results: List<ScanResult>) {}
        fun onScan(result: ScanResult) {}
        fun onScanResult(result: ScanResult) {}
        fun onScanError(errorCode: Int) {}
    }

    enum class ScanMode {
        LOW, // 低功率
        BALANCE, // 平衡
        HIGH // 高功率
    }

    private val callback = ScanCallbackImpl()
    private var timeoutJob: Job? = null
    private var delayJob: Job? = null

    private val scanSettings: ScanSettings

    private val scope = MainScope()
    private var started: Boolean = false

    init {
        val settingsBuilder =
            ScanSettings.Builder().setLegacy(false).setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .setUseHardwareFilteringIfSupported(true)
        when (scanMode) {
            ScanMode.LOW -> settingsBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
            ScanMode.BALANCE -> settingsBuilder.setScanMode(ScanSettings.SCAN_MODE_BALANCED)
            ScanMode.HIGH -> settingsBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        }
        scanSettings = settingsBuilder.build()

        lifecycle?.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                stop()
            }
        })
    }

    // 检查定位是否开启
    private fun checkGps(): Boolean {
        val manager = context.systemService<LocationManager>()
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    fun start() {
        if (!BluetoothCompat.isEnabled(context)) {
            L.e(LOG_TAG, "start, 蓝牙没开启")
            listener.onScanError(SCAN_FAILED_BT_ADAPTER_DISABLED)
            return
        }

        if (!checkGps()) {
            L.e(LOG_TAG, "start, gps没开启")
            listener.onScanError(SCAN_FAILED_GPS_DISABLED)
            return
        }

        var delayStartInterval = 0L
        if (started) {
            reset()
            delayStartInterval = 1000L // 中断上一次任务后, 间隔1秒再开始新的任务
        }

        synchronized(this) {
            started = true
        }
        L.d(LOG_TAG, "start")

        delayJob = scope.launch(Dispatchers.Default) {
            delay(delayStartInterval)

            if (timeout != 0L) {
                timeoutJob = timerFlow(timeout, TimeUnit.MILLISECONDS).onEach {
                    stop()
                }.flowOn(Dispatchers.Default).launchIn(this)
            }

            if (!BluetoothCompat.isEnabled(context)) {
                L.e(LOG_TAG, "start delayJob, 蓝牙没开启")
                listener.onScanError(SCAN_FAILED_BT_ADAPTER_DISABLED)
                return@launch
            }

            if (!checkGps()) {
                L.e(LOG_TAG, "start delayJob, gps没开启")
                listener.onScanError(SCAN_FAILED_GPS_DISABLED)
                return@launch
            }

            BluetoothLeScannerCompat.getScanner().startScan(null, scanSettings, callback)

            // 重新判断一下启动状态, 协程异步导致有可能已经stop了之后还走了start
            if (!started) {
                L.d(LOG_TAG, "start, stop() again when state is not started")
                BluetoothLeScannerCompat.getScanner().stopScan(callback)
            }

            withContext(Dispatchers.Main) {
                listener.onScanStart()
            }
        }
    }

    fun stop() {
        if (!started) return

        L.d(LOG_TAG, "stop")
        reset()

        // 停止后发送所有已发现的结果(无过滤)
        val results = callback.results.values.toList()
        runOnMainThread {
            listener.onScanEnd(results)
        }
    }

    private fun reset() {
        L.d(LOG_TAG, "reset")
        synchronized(this) {
            started = false
        }

        timeoutJob?.cancel()
        delayJob?.cancel()
        callback.reset()

        BluetoothLeScannerCompat.getScanner().stopScan(callback)
    }

    class Builder {
        var scanMode: ScanMode = ScanMode.LOW
        var address: String? = null
        var loggable = true
        private var uuids: List<UUID>? = null

        private val listener: Listener = object : Listener {
            override fun onScanStart() {
                startAction?.invoke()
            }

            override fun onScanResult(result: ScanResult) {
                resultAction?.invoke(result)
            }

            override fun onScan(result: ScanResult) {
                scanAction?.invoke(result)
            }

            override fun onScanEnd(results: List<ScanResult>) {
                endAction?.invoke(results)
            }

            override fun onScanError(errorCode: Int) {
                errorAction?.invoke(errorCode)
            }
        }

        private var startAction: StartAction? = null
        private var resultAction: ResultAction? = null
        private var scanAction: ScanAction? = null
        private var endAction: EndAction? = null
        private var errorAction: ErrorAction? = null

        /**
         * 超时, 0表示无超时机制
         */
        var timeout: Long = TimeUnit.SECONDS.toMillis(60) // 默认60秒超时
            set(value) {
                if (value < 0) return
                field = value
            }

        var lifecycle: Lifecycle? = null

        fun uuids(vararg uuidName: String) = self {
            uuids = uuidName.mapNotNull { it.toUUID() }
        }

        fun uuids(vararg uuid: UUID) = self {
            uuids = uuid.toList()
        }

        fun onStart(action: StartAction) {
            startAction = action
        }

        /**
         * 扫描中途返回符合条件的结果(有过滤), 每一台设备都会单独回调一次
         */
        fun onResult(action: ResultAction) {
            resultAction = action
        }

        fun onScan(action: ScanAction) {
            scanAction = action
        }

        /**
         *  结束扫描, 返回所有扫描到的结果(无过滤)
         */
        fun onEnd(action: EndAction) {
            endAction = action
        }

        fun onError(action: ErrorAction) {
            errorAction = action
        }

        internal fun build(context: Context): Scanner {
            return Scanner(
                context, address, uuids ?: emptyList(), timeout, listener, lifecycle, scanMode, loggable
            )
        }
    }

    private inner class ScanCallbackImpl : ScanCallback() {
        val results = hashMapOf<String, ScanResult>()

        fun reset() {
            results.clear()
        }

        override fun onScanResult(callbackType: Int, result: NordicsemiScanResult) {
            val device = result.device
            val record = result.scanRecord ?: return

            val scanResult = ScanResult(device.address, record.deviceName.orEmpty(), result.device, result.rssi)
            scanResult.serviceUuids = record.serviceUuids

            runOnMainThread {
                if (!started) return@runOnMainThread
                listener.onScan(scanResult)
            }

            results[device.address] = scanResult // 无条件替换同一个设备
            val uuidsFilterResult = filterUuids(record)
            val addressFilterResult = filterAddress(scanResult)
            if (uuidsFilterResult && addressFilterResult) {
                runOnMainThread {
                    if (!started) return@runOnMainThread
                    listener.onScanResult(scanResult)
                }
            }

            if (loggable) {
                L.groupBy(
                    "onScanResult callbackType = $callbackType",
                    "mac address = ${device.address}",
                    "deviceName = ${record.deviceName}",
                    "uuids = ${record.serviceUuids}",
                    "rawData = ${record.bytes?.hex()}"
                ).d(LOG_TAG)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            L.e(LOG_TAG, "scan failed error code = $errorCode")
            this@Scanner.reset()

            runOnMainThread {
                if (!started) return@runOnMainThread
                listener.onScanError(errorCode)
            }
        }

        private fun filterAddress(result: ScanResult): Boolean {
            return if (address != null) {
                result.address == address
            } else {
                // 不过滤
                true
            }
        }

        private fun filterUuids(record: ScanRecord): Boolean {
            // 判断是否要过滤uuid
            if (uuids.isEmpty()) {
                // 不过滤
                return true
            } else {
                val matchUuid: Boolean = record.serviceUuids?.find { parcelUuid ->
                    val find: UUID? = uuids.find { uuid ->
                        uuid == parcelUuid.uuid
                    }
                    find != null
                } != null

                if (matchUuid) {
                    L.d(LOG_TAG, "找到对应的 uuid")
                    return true
                }
            }

            return false
        }
    }

    private fun runOnMainThread(block: () -> Unit) {
        scope.launch { block() }
    }

    private fun ByteArray.hex(): String {
        return buildString {
            this@hex.forEach {
                var hex = Integer.toHexString(it and 0xFF)
                if (hex.length == 1) hex = "0$hex"
                append(hex)
            }
        }
    }

    private infix fun Byte.and(other: Int): Int = this.toInt() and other
}
