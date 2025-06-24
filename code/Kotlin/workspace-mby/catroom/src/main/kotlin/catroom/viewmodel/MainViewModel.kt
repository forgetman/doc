package catroom.viewmodel

import android.app.Application
import android.os.Build
import androidx.lifecycle.viewModelScope
import catroom.bluetooth.BleManager
import catroom.datastore.Properties
import catroom.def.Constants
import catroom.def.Resolution
import catroom.iot.DataTemplateSample
import catroom.iot.callback.DownStreamCallback
import catroom.iot.callback.MqttActionCallback
import catroom.model.CatRoomDown
import catroom.model.CatRoomInfo
import catroom.model.RoomStateWrapper
import catroom.network.api.TestApi
import catroom.network.createApi
import catroom.repo.RoomRepo
import catroom.service.CheckUpgradeService
import com.tencent.iot.hub.device.java.core.common.Status
import compat.network.NetworkCompat
import compat.network.def.NetworkState
import compat.network.def.listener.NetworkListener
import coroutine.flow.state.toFalse
import coroutine.flow.state.toTrue
import dagger.hilt.android.lifecycle.HiltViewModel
import eson.Eson
import eth.ext.asProgressFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.stateIn
import logger.L
import org.json.JSONException
import org.json.JSONObject
import sugar.ext.Console
import tool.trigger.Trigger
import tool.trigger.constraints.Constraints
import tool.trigger.constraints.NetworkType
import tool.trigger.ext.onTrigger
import tool.trigger.strategy.BackoffStrategy
import vector.app.viewmodel.ViewModelEx
import vector.datastore.preference.asEnumFirstFlow
import vector.datastore.preference.putEnum
import vector.datastore.preference.sync
import vector.ext.startServ
import vector.ext.toast
import vector.util.DeviceIdUtil
import vector.util.PackageUtil
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repo: RoomRepo, app: Application) : ViewModelEx(app) {

    companion object {
        private const val LOG_TAG = "MainViewModel"
        private const val JSON_FILE_NAME: String = "struct.json"
    }

    val ble = BleManager()
    val roomStateWrapper = ble.roomState.map {
        RoomStateWrapper(it, PackageUtil.appVersionCode.toInt(), getDeviceString())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val frontIndex = MutableStateFlow(0)
    val backIndex = MutableStateFlow(0)
    val frontEnabled = MutableStateFlow(true)
    val backEnabled = MutableStateFlow(true)

    private val iotReady = MutableStateFlow(false)
    val iotState = iotReady.map { if (it) "已连接" else "未连接" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "未连接")

    private val _roomInfo = MutableStateFlow<CatRoomInfo?>(null)
    val roomInfo = _roomInfo.asStateFlow()

    private val iotService = IotService()

    private val pushReady = combine(iotReady, _roomInfo.map { it != null }) { iot, room ->
        iot && room
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    private val networkState = callbackFlow {
        val listener = object : NetworkListener {
            override fun onConnectStateChanged(state: NetworkState) {
                trySend(state)
            }
        }
        NetworkCompat.registerListener(applicationContext, listener)
        val curr = NetworkCompat.getActiveNetworkState(applicationContext)
        send(curr)

        awaitClose {
            NetworkCompat.unregisterListener(applicationContext, listener)
        }

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), NetworkState.Idle)

    // 前置摄像头状态
    val frontState = combine(
        pushReady.filter { it },
        frontEnabled,
        frontIndex,
        networkState
    ) { _, enabled, index, networkState ->
        L.d(LOG_TAG, "front, enabled = $enabled, index = $index, networkState = $networkState")
        val resolution = Resolution.entries[index]
        Properties.cameraFrontResolution.putEnum(resolution)
        if (networkState is NetworkState.Idle) {
            Pair(false, resolution)
        } else {
            Pair(enabled, resolution)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        Pair(false, Resolution.P480)
    )

    // 后置摄像头状态
    val backState = combine(
        pushReady.filter { it },
        backEnabled,
        backIndex,
        networkState
    ) { _, enabled, index, networkState ->
        L.d(LOG_TAG, "back, enabled = $enabled, index = $index, networkState = $networkState")
        val resolution = Resolution.entries[index]
        Properties.cameraBackResolution.putEnum(resolution)
        if (networkState is NetworkState.Idle) {
            Pair(false, resolution)
        } else {
            Pair(enabled, resolution)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        Pair(false, Resolution.P480)
    )

    private var testingSpeed = false
    val speed = MutableStateFlow("速度: 0M/S")

    override fun onCreate() {
        combine(roomStateWrapper, iotReady) { state, ready ->
            if (ready) state else null
        }.filterNotNull().onEach { wrapper ->
            L.d(LOG_TAG, "roomState = $wrapper")
            val state = wrapper.state
            // 通过iot上报状态
            val property = JSONObject()
            try {
                property.put(Constants.Room.LIGHT_STATUS, state.lightState)
                property.put(Constants.Room.QUANTITY_OF_ELECTRICITY, state.battery)
                property.put(Constants.Room.AMBIENT_TYPE, state.environment)
                property.put(Constants.Room.FOOD_CAT_TYPE, state.food)
                property.put(Constants.Room.FOOD_FREEZE_TYPE, state.freezeDried)
                property.put(Constants.Room.INDUCTION_TYPE, state.inducedState)
                property.put(Constants.Room.APP_VERSION, wrapper.appVersion)
                property.put(Constants.Room.SYSTEM_VERSION, wrapper.systemVersion)
            } catch (e: JSONException) {
                L.e(LOG_TAG, "uploadRoomState", e)
                return@onEach
            }
            val status = iotService.propertyReport(property, null)
            L.d(LOG_TAG, "uploadRoomState, status = $status")
        }.launchIn(viewModelScope)

        fetchRoomInfo()

        Properties.cameraFrontResolution.asEnumFirstFlow<Resolution>().map { resolution ->
            Resolution.entries.indexOf(resolution)
        }.onEach { index ->
            frontIndex.value = index
        }.launchIn(viewModelScope)

        Properties.cameraBackResolution.asEnumFirstFlow<Resolution>().map { resolution ->
            Resolution.entries.indexOf(resolution)
        }.onEach { index ->
            backIndex.value = index
        }.launchIn(viewModelScope)
    }

    fun initBle() {
        ble.init(applicationContext, viewModelScope)
    }

    private fun fetchRoomInfo() = flow {
        emit(Pair(Properties.roomLongitude.getOrNull(), Properties.roomLatitude.getOrNull()))
    }.flatMapConcat { (longitude, latitude) ->
        repo.fetchRoomInfo(
            DeviceIdUtil.id,
            longitude.toString(),
            latitude.toString()
        ).retryWhen { cause, attempt ->
            L.i(LOG_TAG, "fetchRoomInfo, cause = $cause, attempt = $attempt")
            // 不管什么理由失败, 一直重试
            delay(3000)
            true
        }.onEach { info ->
            L.d(LOG_TAG, "fetchRoomInfo = $info")
            _roomInfo.value = info
            Properties.roomName.put(info.roomName)
            iotService.init(info)
        }.catch { e ->
            L.e(LOG_TAG, "fetchRoomInfo", e)
            Properties.clear()
        }.flowOn(Dispatchers.IO)
    }.launchIn(viewModelScope)

    fun turnOnLight() {
        ble.turnOnLight()
    }

    fun turnOffLight() {
        ble.turnOffLight()
    }

    fun feedFood() {
        ble.feedFood()
    }

    fun feedFreezeDried() {
        ble.feedFreezeDried()
    }

    override fun onDestroy() {
        iotService.destroy()
    }

    private fun getDeviceString(): String {
        return (Build.HARDWARE
            + "_" + Build.DEVICE
            + "_" + Build.BOARD
            + "_" + Build.VERSION.RELEASE)
    }

    class IotActionCallback(
        private val sample: DataTemplateSample,
        private val callback: (connected: Boolean) -> Unit
    ) : MqttActionCallback() {

        override fun onConnectCompleted(
            status: Status,
            reconnect: Boolean,
            userContext: Any?,
            msg: String,
            cause: Throwable?
        ) {
            super.onConnectCompleted(status, reconnect, userContext, msg, cause)
            if (status == Status.OK) {
                sample.subscribeTopic()
                callback(true)
            } else {
                // 连接失败
                callback(false)
            }
        }

        override fun onConnectionLost(cause: Throwable) {
            super.onConnectionLost(cause)
            // 意外断开
            callback(false)
        }
    }

    inner class IotService {
        private val trigger = Trigger(applicationContext) {
            setTag(LOG_TAG + "_iot")
            setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            applyStrategy(BackoffStrategy.Builder().tag(LOG_TAG + "_iot").build())
        }

        private val downStreamCallback = object : DownStreamCallback() {

            override fun onControlCallBack(msg: JSONObject): JSONObject {
                L.d(LOG_TAG, "onControlCallBack, msg: $msg")
                val down = Eson.default().fromJson<CatRoomDown>(msg.toString()) ?: return super.onControlCallBack(msg)

                // 处理投喂
                when (down.feeding) {
                    Constants.Room.CAT -> {
                        feedFood()
                    }

                    Constants.Room.FREEZE -> {
                        feedFreezeDried()
                    }
                }

                // 处理灯光
                if (down.shouldLightOn()) {
                    turnOnLight()
                } else if (down.shouldLightOff()) {
                    turnOffLight()
                }

                if (down.shouldReboot()) {
                    Console.writeAsSh("reboot")
                }

                if (down.shouldRestart()) {
                    Console.writeAsSh("am force-stop com.miaomiaobuyi.miaomiaoservice")
                }

                if (down.shouldRenew()) {
                    applicationContext.startServ<CheckUpgradeService>()
                }

                if (down.shouldUploadLog()) {
                    L.upload()
                    iotService.uploadLog()
                }

                if (down.shouldUploadAllLog()) {
                    L.uploadAll()
                    iotService.uploadLog()
                }

                return super.onControlCallBack(msg)
            }
        }

        private lateinit var dataTemplateSample: DataTemplateSample

        fun init(roomInfo: CatRoomInfo) {
            trigger.onTrigger(viewModelScope) {
                dataTemplateSample.connect()
            }

            dataTemplateSample = buildSample(roomInfo).apply { connect() }
        }

        private fun buildSample(info: CatRoomInfo): DataTemplateSample {
            return DataTemplateSample(
                applicationContext,
                null,
                info.iotProductId,
                info.iotDeviceName,
                info.iotDevicePsk,
                JSON_FILE_NAME,
            ).apply {
                setDownStreamCallback(downStreamCallback)
                setMqttActionCallback(IotActionCallback(this) { connected ->
                    if (connected) {
                        iotReady.toTrue()
                        trigger.reset()
                    } else {
                        iotReady.toFalse()
                        trigger.launch()
                    }
                })
            }
        }

        fun disconnect() {
            dataTemplateSample.disconnect()
        }

        fun propertyReport(property: JSONObject, metadata: JSONObject?) =
            dataTemplateSample.propertyReport(property, metadata)

        fun uploadLog() {
            dataTemplateSample.uploadLog()
        }

        fun destroy() {
            disconnect()
            dataTemplateSample.destroy()
        }
    }

    fun checkNetworkSpeed() {
        if (testingSpeed) {
            toast("测速中...")
            return
        }

        fun calcSpeed(lastTime: Long, progress: Float, contentLength: Long): String {
            val diff = System.currentTimeMillis() - lastTime
            return if (diff == 0L) {
                "0M/S"
            } else {
                val speed = contentLength * 1000f / diff * (progress / 100)
                // speed单位是byte/s, 转为M/s
                val speedInM = NumberFormat.getNumberInstance(Locale.SIMPLIFIED_CHINESE)
                    .apply { maximumFractionDigits = 2 }
                    .format((speed / 1024f / 1024f))
                "实时速度: ${speedInM}M/S"
            }
        }

        var startTime = 0L
        var lastTime = 0L
        var lastProgress = 0f
        createApi<TestApi>().checkNetworkSpeed("https://api.miaobue.cn/apk.zip")
            .asProgressFlow()
            .onProgress { progress ->
                L.d(LOG_TAG, "checkNetworkSpeed, progress = $progress")
                this.speed.value = calcSpeed(lastTime, progress.progress - lastProgress, progress.contentLength)
                lastTime = System.currentTimeMillis()
                lastProgress = progress.progress
            }
            .flowOn(Dispatchers.IO)
            .onStart {
                testingSpeed = true
                startTime = System.currentTimeMillis()
                this@MainViewModel.speed.value = "开始测速"
                toast("开始测速")
            }.onEach {
                val diff = System.currentTimeMillis() - startTime
                val speed = it.contentLength * 1000f / diff
                // speed单位是byte/s, 转为M/s
                val speedInM = NumberFormat.getNumberInstance(Locale.SIMPLIFIED_CHINESE)
                    .apply { maximumFractionDigits = 2 }
                    .format((speed / 1024f / 1024f))
                "${speedInM}M/S"
                this.speed.value = "综合速度: $speedInM"
            }.onCompletion {
                testingSpeed = false
                toast("测速完成")
            }.catch { e ->
                L.e(LOG_TAG, "checkNetworkSpeed", e)
            }.flowOn(Dispatchers.Main).launchIn(viewModelScope)
    }
}