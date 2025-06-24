package catroom.helper

import android.content.Context
import android.hardware.usb.UsbDevice
import android.media.AudioFormat
import android.os.Environment
import android.view.SurfaceHolder
import catroom.BuildConfig
import catroom.def.Resolution
import catroom.encoder.AudioEncoder
import catroom.encoder.BaseEncoder
import catroom.encoder.VideoEncoder
import com.herohan.uvcapp.CameraHelper
import com.herohan.uvcapp.ICameraHelper.StateCallback
import com.herohan.uvcapp.VideoCapture
import com.serenegiant.usb.UVCCamera
import com.serenegiant.usb.UVCParam
import com.serenegiant.widget.AspectRatioSurfaceView
import logger.L
import vector.os.Size
import vector.util.Dir
import java.io.File
import java.nio.ByteBuffer
import kotlin.math.abs

/**
 * @author yuansui
 * @since 2024/7/6
 */
class CameraPreviewHelper(
    private val context: Context,
    private val vendorId: Int,
    private val url: String,
    private val view: AspectRatioSurfaceView,
    private val audioEncoder: AudioEncoder
) {

    companion object {
        private const val LOG_TAG = "CameraPreviewHelper"
    }

    private val helper = CameraHelper()

    private val saveToSdcard = BuildConfig.OUTPUT_TO_SDCARD

    private lateinit var videoEncoder: VideoEncoder
    private var pusher: LiveStreamPushHelper? = null

    private var isRunning = false

    private val stateListener = object : StateCallback {
        override fun onAttach(device: UsbDevice) {
            L.d(LOG_TAG, "onAttach, vid = ${device.vendorId}, id = ${device.deviceId}, pid = ${device.productId}")
            if (device.vendorId == vendorId) {
                helper.selectDevice(device)
            }
        }

        override fun onDeviceOpen(device: UsbDevice, isFirstOpen: Boolean) {
            L.d(LOG_TAG, "onDeviceOpen, device = ${device.vendorId}")
            val param = UVCParam().apply {
                quirks = UVCCamera.UVC_QUIRK_FIX_BANDWIDTH
            }
            helper.openCamera(param)
        }

        override fun onCameraOpen(device: UsbDevice) {
            L.d(LOG_TAG, "onCameraOpen, device = ${device.vendorId}")
            fun findClosestSize(width: Int, height: Int): com.serenegiant.usb.Size? {
                // 在helper.supportedSizeList里查找分辨率最接近的支持的size
                val supportedSizeList = helper.supportedSizeList
                val matchSize = supportedSizeList.find { it.width == width && it.height == height }
                if (matchSize != null) {
                    return matchSize
                }
                val sorted = supportedSizeList.sortedBy { it.width * it.height }
                val target = width * height
                var minDiff = Int.MAX_VALUE
                var closestSize: com.serenegiant.usb.Size? = null
                for (supported in sorted) {
                    val diff = abs(supported.width * supported.height - target)
                    if (diff < minDiff) {
                        minDiff = diff
                        closestSize = supported
                    }
                }
                return closestSize
            }

            val oldSize = helper.previewSize
            val matchSize = findClosestSize(resolution.width, resolution.height) ?: oldSize
            L.d(LOG_TAG, "onCameraOpen, matchSize = $matchSize")
            helper.previewSize = matchSize
            view.setAspectRatio(matchSize.width, matchSize.height)

            videoEncoder = VideoEncoder.Builder()
                .outputSize(Size(matchSize.width, matchSize.height))
                .frameRate(matchSize.fps)
                .type(VideoEncoder.Type.H264)
                .vendorId(vendorId)
                .listener(object : VideoEncoder.Listener {
                    override fun onVideoInfo(
                        sps: ByteBuffer,
                        pps: ByteBuffer?,
                        vps: ByteBuffer?
                    ) {
                        pusher?.setVideoInfo(sps, pps, vps)
                    }
                })
                .build()
                .apply {
                    addListener { data, info ->
                        try {
                            val buffer = ByteBuffer.allocate(data.size)
                            buffer.put(data)
                            buffer.flip()
                            pusher?.sendVideoBuffer(buffer, info)
                        } catch (e: Exception) {
                            L.e(LOG_TAG, "write Encoder", e)
                        }
                    }
                }

            pusher = LiveStreamPushHelper(context, vendorId).apply {
                setVideoResolution(videoEncoder.type, matchSize.width, matchSize.height, matchSize.fps)
                setAudioInfo(AudioEncoder.SAMPLE_RATE, audioEncoder.channelConfig == AudioFormat.CHANNEL_IN_STEREO)
                start(url)
            }

            helper.setFrameCallback({ frame ->
                val data = ByteArray(frame.remaining())
                frame.get(data)
                videoEncoder.encode(data)
            }, UVCCamera.PIXEL_FORMAT_NV12)
            helper.startPreview()

            videoEncoder.start()
            helper.addSurface(view.holder.surface, false)

            if (saveToSdcard) {
                startRecording(matchSize.fps)
            }

            isRunning = true
        }

        override fun onCameraClose(device: UsbDevice) {
            L.d(LOG_TAG, "onCameraClose, device = ${device.vendorId}")
            isRunning = false

            pusher?.stop()
            pusher = null

            helper.stopPreview()

            audioEncoder.removeListener(audioListener)
            videoEncoder.stop()

            if (saveToSdcard) {
                helper.stopRecording()
            }

            helper.removeSurface(view.holder.surface)
        }

        override fun onDeviceClose(device: UsbDevice) {
            L.d(LOG_TAG, "onDeviceClose, device = ${device.vendorId}")
        }

        override fun onDetach(device: UsbDevice) {
            L.d(LOG_TAG, "onDetach, device = ${device.vendorId}")
        }

        override fun onCancel(device: UsbDevice) {
            L.d(LOG_TAG, "onCancel, device = ${device.vendorId}")
        }
    }

    private val audioListener = BaseEncoder.Listener { data, info ->
        try {
            val buffer = ByteBuffer.allocate(data.size)
            buffer.put(data)
            buffer.flip()
            pusher?.sendAudioBuffer(buffer, info)
        } catch (e: Exception) {
            L.e(LOG_TAG, "write Encoder", e)
        }
    }

    private val surfaceHolderCallback = object : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) {
            L.d(LOG_TAG, "surfaceCreated")
            if (isRunning) {
                helper.addSurface(view.holder.surface, false)
                helper.startPreview()
            }
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            L.d(LOG_TAG, "surfaceChanged, width = $width, height = $height")
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            L.d(LOG_TAG, "surfaceDestroyed")
            if (isRunning) {
                helper.removeSurface(view.holder.surface)
                helper.stopPreview()
            }
        }
    }

    private lateinit var resolution: Resolution


    init {
        view.holder.addCallback(surfaceHolderCallback)
    }

    fun start(r: Resolution) {
        this.resolution = r
        audioEncoder.addListener(audioListener)
        helper.setStateCallback(stateListener)
    }

    private fun startRecording(frameRate: Int) {
        helper.videoCaptureConfig = helper.videoCaptureConfig
            .setAudioCaptureEnable(false)
            .setVideoFrameRate(frameRate)
            .setIFrameInterval(10)

        val file = File(
            Dir.External.getFileDir(Environment.DIRECTORY_MOVIES),
            "capture-$vendorId.mp4"
        )
        if (file.exists()) {
            file.delete()
        }
        val options = VideoCapture.OutputFileOptions.Builder(file).build()
        helper.startRecording(options, object : VideoCapture.OnVideoCaptureCallback {
            override fun onStart() {
                L.d(LOG_TAG, "$vendorId, onStart")
            }

            override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
                L.d(LOG_TAG, "$vendorId, onVideoSaved, file = ${outputFileResults.savedUri}")
            }

            override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
                L.d(LOG_TAG, "$vendorId, onError, msg = $message")
            }
        })
    }

    fun stop() {
        helper.release()

        pusher?.stop()
        pusher = null

        view.holder.removeCallback(surfaceHolderCallback)
    }
}


