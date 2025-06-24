package catroom.helper

import android.content.Context
import android.media.MediaCodec
import catroom.encoder.VideoEncoder
import com.pedro.common.ConnectChecker
import com.pedro.rtmp.rtmp.RtmpClient
import compat.network.NetworkCompat
import logger.L
import tool.trigger.Trigger
import tool.trigger.constraints.Constraints
import tool.trigger.constraints.NetworkType
import tool.trigger.strategy.BackoffStrategy
import java.nio.ByteBuffer

/**
 * 推流器
 * 基本是参考和使用之前的旧代码
 */
class LiveStreamPushHelper(context: Context, private val vendorId: Int) {

    companion object {
        private const val LOG_TAG = "LiveStreamPushHelper"
    }

    private val trigger = Trigger(context) {
        setTag(LOG_TAG)
        setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
        applyStrategy(BackoffStrategy.Builder().tag(LOG_TAG).build())
    }
    private val triggerListener = Trigger.Listener {
        rtmp.connect(url)
    }

    private var url: String? = null

    private val rtmp = RtmpClient(object : ConnectChecker {

        override fun onConnectionStarted(url: String) {
            L.d(LOG_TAG, "$vendorId onConnectionStarted, url = $url")
        }

        override fun onConnectionSuccess() {
            L.d(LOG_TAG, "$vendorId onConnectionSuccess")
            trigger.reset()
        }

        override fun onConnectionFailed(reason: String) {
            val state = NetworkCompat.getActiveNetworkState(context)
            L.d(LOG_TAG, "$vendorId onConnectionFailed, networkState = $state, reason = $reason")
            trigger.launch()
        }

        override fun onDisconnect() {
            L.d(LOG_TAG, "$vendorId onDisconnect")
        }

        override fun onAuthError() {
        }

        override fun onAuthSuccess() {
        }
    })

    private var spsPpsSetted = false

    private var type: VideoEncoder.Type? = null

    fun start(url: String) {
        L.d(LOG_TAG, "$vendorId start, url = $url")
        this.url = url
        rtmp.setReTries(0)
        rtmp.connect(url)

        trigger.addListener(triggerListener)
    }

    fun setVideoResolution(type: VideoEncoder.Type, width: Int, height: Int, fps: Int) {
        this.type = type
        rtmp.setFps(fps)
        rtmp.setVideoResolution(width, height)
    }

    fun setVideoInfo(sps: ByteBuffer, pps: ByteBuffer?, vps: ByteBuffer?) {
        try {
            rtmp.setVideoInfo(sps, pps, vps)
            spsPpsSetted = true
        } catch (e: Exception) {
            L.e(LOG_TAG, "setVideoInfo", e)
        }
    }

    fun setAudioInfo(sampleRate: Int, isStereo: Boolean) {
        try {
            rtmp.setAudioInfo(sampleRate, isStereo)
        } catch (e: Exception) {
            L.e(LOG_TAG, "setAudioInfo", e)
        }
    }

    fun sendVideoBuffer(buffer: ByteBuffer, info: MediaCodec.BufferInfo) {
        rtmp.sendVideo(buffer, info)
    }

    fun sendAudioBuffer(buffer: ByteBuffer, info: MediaCodec.BufferInfo) {
        rtmp.sendAudio(buffer, info)
    }

    fun stop() {
        L.d(LOG_TAG, "$vendorId stop")
        rtmp.disconnect()

        trigger.removeListener(triggerListener)
        trigger.reset()
    }
}