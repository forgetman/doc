package com.miaomiaobuyi.miaomiaoservice

import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import catroom.R
import catroom.databinding.ActivityMainBinding
import catroom.encoder.AudioEncoder
import catroom.helper.CameraPreviewHelper
import catroom.manager.LocationManager
import catroom.manager.TrafficManager
import catroom.viewmodel.MainViewModel
import com.serenegiant.widget.AspectRatioSurfaceView
import coroutine.flow.launchIn
import coroutine.flow.state.inverse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import logger.L
import sugar.ext.runOnMainThread
import vector.app.databinding.activity.DBActivityEx
import vector.bindingadapter.bind.Bind
import vector.app.ext.bind.bindView
import vector.ext.toast
import vector.util.DangerousPerm
import vector.util.EasyPermissions
import java.util.Date

/**
 * FIXME: 受限于旧的保活机制的影响(主板默认刷了一个alive_app_name的保活应用), 无法使用正确的activity包名, 只能一直放到这里
 */
@AndroidEntryPoint
class MainActivity : DBActivityEx<MainViewModel>() {

    companion object {
        private const val LOG_TAG = "MainActivity"
    }

    val onOpenLightClick = Bind.OnClick {
        viewModel.turnOnLight()
    }

    val onCloseLightClick = Bind.OnClick {
        viewModel.turnOffLight()
    }

    val onFeedFoodClick = Bind.OnClick {
        viewModel.feedFood()
    }

    val onFeedFreezeDriedClick = Bind.OnClick {
        viewModel.feedFreezeDried()
    }

    val onUploadLogClick = Bind.OnClick {
        L.saveToFile()
        L.upload(Date()) {
            runOnMainThread(this) {
                toast("上传日志结果: $it")
            }
        }
    }

    val onUploadLogAllClick = Bind.OnClick {
        L.saveToFile()
        L.uploadAll {
            runOnMainThread(this) {
                toast("上传所有日志结果: $it")
            }
        }
    }

    val onCheckSpeedClick = Bind.OnClick { v ->
        viewModel.checkNetworkSpeed()
    }

    val onFrontToggleClick = Bind.OnClick {
        viewModel.frontEnabled.inverse()
    }

    val onBackToggleClick = Bind.OnClick {
        viewModel.backEnabled.inverse()
    }

    private val audioEncoder: AudioEncoder = AudioEncoder(this)

    private val cameraFront by bindView<AspectRatioSurfaceView>(R.id.layout_camera_front)
    private val cameraBack by bindView<AspectRatioSurfaceView>(R.id.layout_camera_back)

    private var frontHelper: CameraPreviewHelper? = null
    private var backHelper: CameraPreviewHelper? = null

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        return ActivityMainBinding.inflate(inflater).apply {
            owner = this@MainActivity
            viewModel = this@MainActivity.viewModel
        }
    }

    override fun initializeSystemBar() {
        appBar.mid.addText("猫屋")
    }

    override fun initializeContentView() {
        EasyPermissions.request(
            this,
            DangerousPerm.Bluetooth.Scan(),
            DangerousPerm.Bluetooth.Connect(),
            DangerousPerm.Location(),
            DangerousPerm.Camera(),
            DangerousPerm.Microphone(),
        ) {
            LocationManager.getInstance(this).update()
            viewModel.initBle()
        }

        // 配置前摄像头
        viewModel.frontState.onEach { (enabled, resolution) ->
            L.d(LOG_TAG, "front enabled = $enabled, resolution = $resolution")
            val old = frontHelper
            if (old != null) {
                releaseFront()
                // 延迟两秒, 让上一个任务执行release
                delay(2000)
            }

            if (!enabled) return@onEach

            val url = viewModel.roomInfo.value?.masterLivePushUrl ?: return@onEach
            val new = CameraPreviewHelper(this, 3141, url, cameraFront, audioEncoder)
            new.start(resolution)
            frontHelper = new
        }.launchIn(this)

        // 配置后摄像头
        viewModel.backState.onEach { (enabled, resolution) ->
            L.d(LOG_TAG, "back enabled = $enabled, resolution = $resolution")
            val old = backHelper
            if (old != null) {
                releaseBack()
                // 延迟两秒, 让上一个任务执行release
                delay(2000)
            }

            if (!enabled) return@onEach

            val url = viewModel.roomInfo.value?.sideLivePushUrl ?: return@onEach
            val new = CameraPreviewHelper(this, 7119, url, cameraBack, audioEncoder)
            new.start(resolution)
            backHelper = new
        }.launchIn(this)

        combine(viewModel.frontState, viewModel.backState) { (frontEnabled, _), (backEnabled, _) ->
            frontEnabled || backEnabled
        }.distinctUntilChanged().onEach { enabled ->
            L.d(LOG_TAG, "recorder enabled = $enabled")
            if (enabled) {
                audioEncoder.start()
            } else {
                audioEncoder.stop()
            }
        }.launchIn(this)

        TrafficManager.startMonitor()
    }

    override fun onDestroy() {
        super.onDestroy()

        TrafficManager.stopMonitor()

        audioEncoder.stop()

        releaseFront()
        releaseBack()
    }

    private fun releaseFront() {
        frontHelper?.stop()
        frontHelper = null
    }

    private fun releaseBack() {
        backHelper?.stop()
        backHelper = null
    }
}