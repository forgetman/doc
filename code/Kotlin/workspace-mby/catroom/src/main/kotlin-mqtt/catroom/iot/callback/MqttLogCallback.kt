package catroom.iot.callback

import android.os.Environment
import com.tencent.iot.hub.device.android.core.util.TXLog
import com.tencent.iot.hub.device.java.core.log.TXMqttLogCallBack
import logger.L
import vector.util.Dir
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException

/**
 * 暂时放着
 */
class MqttLogCallback(val productID: String, val devPSK: String) : TXMqttLogCallBack() {

    override fun setSecretKey(): String {
        var secertKey: String = devPSK
        secertKey = if (secertKey.length > 24) secertKey.substring(0, 24) else secertKey
        return secertKey
    }

    override fun printDebug(message: String) {
        printDebug("printDebug, message: $message")
    }

    override fun saveLogOffline(log: String): Boolean {
        //判断SD卡是否可用
        if (Environment.MEDIA_MOUNTED != Environment.getExternalStorageState()) {
            printDebug("saveLogOffline not ready")
            return false
        }

        val logFilePath: String = Dir.Internal.cache.absolutePath + productID + ".log"

        printDebug("Save log to %s, $logFilePath")

        try {
            val wLog = BufferedWriter(FileWriter(File(logFilePath), true))
            wLog.write(log)
            wLog.flush()
            wLog.close()
            return true
        } catch (e: IOException) {
            val logInfo = String.format("Save log to [%s] failed, check the Storage permission!", logFilePath)
            printDebug("saveLogOffline, logInfo: $logInfo")
            L.e("saveLogOffline", e)
            return false
        }
    }

    override fun readOfflineLog(): String? {
        //判断SD卡是否可用
        if (Environment.MEDIA_MOUNTED != Environment.getExternalStorageState()) {
            printDebug("readOfflineLog, not ready")
            return null
        }

        val logFilePath: String = Dir.Internal.cache.absolutePath + productID + ".log"

        TXLog.i("Read log from %s", logFilePath)

        try {
            val logReader = BufferedReader(FileReader(logFilePath))
            val offlineLog = java.lang.StringBuilder()
            var data: Int
            while ((logReader.read().also { data = it }) != -1) {
                offlineLog.append(data.toChar())
            }
            logReader.close()
            return offlineLog.toString()
        } catch (e: IOException) {
            L.e("readOfflineLog", e)
            return null
        }
    }

    override fun delOfflineLog(): Boolean {
        //判断SD卡是否可用
        if (Environment.MEDIA_MOUNTED != Environment.getExternalStorageState()) {
//                mParent.printLogInfo(IoTMqttFragment.TAG, "delOfflineLog not ready", mLogInfoText)
            return false
        }

        val logFilePath: String = Dir.Internal.cache.absolutePath + productID + ".log"

        val file = File(logFilePath)
        if (file.exists() && file.isFile) {
            if (file.delete()) {
                return true
            }
        }
        return false
    }

}