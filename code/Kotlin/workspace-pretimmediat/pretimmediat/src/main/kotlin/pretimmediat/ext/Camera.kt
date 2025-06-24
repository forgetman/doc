package pretimmediat.ext

import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import logger.L
import pretimmediat.model.CameraFormat
import sugar.ext.systemService

private const val LOG_TAG = "Camera"

fun findCameraFormat(context: Context, facing: Int): CameraFormat? {
    val cameraManager = context.systemService<CameraManager>()
    try {
        val cameraIds = cameraManager.cameraIdList
        for (cameraId in cameraIds) {
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)
            val lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING)
            if (lensFacing == facing) {
                return CameraFormat(cameraId, ImageFormat.JPEG)
            }
        }
    } catch (e: CameraAccessException) {
        L.e(LOG_TAG, e)
    }
    return null
}