package pretimmediat.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.text.TextUtils
import logger.L
import sugar.ext.systemService
import vector.ext.isNotNullOrEmpty
import java.io.BufferedReader
import java.io.FileReader
import java.io.InputStreamReader
import java.io.LineNumberReader
import java.io.Reader
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.Enumeration
import java.util.Locale


/**
 * 简单复制的网上代码(无法校验)
 * mac地址获取
 */
@Suppress("SENSELESS_COMPARISON")
object MacUtil {
    fun getMac(context: Context): String {
        val mac: String

        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.M -> {
                mac = getLocalMacAddressFromWifiInfo(context)
            }

            Build.VERSION.SDK_INT < Build.VERSION_CODES.N
                    && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                mac = getMacAddress(context)
            }

            else -> {
                // Android 7.0 以上
                val address = getMacAddress()
                val address2 = getMachineHardwareAddress()
                mac = when {
                    address.isNotNullOrEmpty() -> address
                    address2.isNotNullOrEmpty() -> address2
                    else -> getLocalMacAddressFromBusybox()
                }
            }
        }

        return mac.ifEmpty { "02:00:00:00:00:00" }
    }

    /**
     * 根据wifi信息获取本地mac
     * @param context
     * @return
     */
    @SuppressLint("HardwareIds")
    fun getLocalMacAddressFromWifiInfo(context: Context): String {
        val wifi = context.systemService<WifiManager>()
        val winfo = wifi.connectionInfo
        val mac = winfo.macAddress
        return mac
    }

    /**
     * android 6.0及以上、7.0以下 获取mac地址
     *
     * @param context
     * @return
     */
    fun getMacAddress(context: Context): String {
        // 如果是6.0以下，直接通过wifimanager获取

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            val macAddress0 = getMacAddress0(context)
            if (!TextUtils.isEmpty(macAddress0)) {
                return macAddress0
            }
        }

        var str = ""
        var macSerial = ""
        try {
            val pp = Runtime.getRuntime().exec(
                "cat /sys/class/net/wlan0/address"
            )
            val ir = InputStreamReader(pp.inputStream)
            val input = LineNumberReader(ir)

            while (null != str) {
                str = input.readLine()
                if (str != null) {
                    macSerial = str.trim { it <= ' ' } // 去空格
                    break
                }
            }
        } catch (ex: Exception) {
            L.e("getMacAddress", ex)
        }
        if (macSerial == null || "" == macSerial) {
            try {
                return loadFileAsString("/sys/class/net/eth0/address")
                    .uppercase(Locale.getDefault()).substring(0, 17)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return macSerial
    }

    @SuppressLint("HardwareIds")
    private fun getMacAddress0(context: Context): String {
        if (isAccessWifiStateAuthorized(context)) {
            val wifiMgr =
                context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo: WifiInfo?
            try {
                wifiInfo = wifiMgr.connectionInfo
                return wifiInfo.macAddress
            } catch (e: Exception) {
                L.e(e)
            }
        }
        return ""
    }

    /**
     * Check whether accessing wifi state is permitted
     *
     * @param context
     * @return
     */
    private fun isAccessWifiStateAuthorized(context: Context): Boolean {
        if (PackageManager.PERMISSION_GRANTED == context
                .checkCallingOrSelfPermission("android.permission.ACCESS_WIFI_STATE")
        ) {
            return true
        } else return false
    }

    @Throws(Exception::class)
    private fun loadFileAsString(fileName: String): String {
        val reader = FileReader(fileName)
        val text = loadReaderAsString(reader)
        reader.close()
        return text
    }

    @Throws(Exception::class)
    private fun loadReaderAsString(reader: Reader): String {
        val builder = StringBuilder()
        val buffer = CharArray(4096)
        var readLength: Int = reader.read(buffer)
        while (readLength >= 0) {
            builder.append(buffer, 0, readLength)
            readLength = reader.read(buffer)
        }
        return builder.toString()
    }

    /**
     * 根据IP地址获取MAC地址
     *
     * @return
     */
    private fun getMacAddress(): String? {
        var strMacAddr: String? = null
        try {
            // 获得IpD地址
            val ip = getLocalInetAddress()
            val b = NetworkInterface.getByInetAddress(ip).hardwareAddress
            val buffer = StringBuffer()
            for (i in b.indices) {
                if (i != 0) {
                    buffer.append(':')
                }
                val str = Integer.toHexString(b[i].toInt() and 0xFF)
                buffer.append(if (str.length == 1) "0$str" else str)
            }
            strMacAddr = buffer.toString().uppercase(Locale.getDefault())
        } catch (e: Exception) {
            L.e(e)
        }

        return strMacAddr
    }

    /**
     * 获取移动设备本地IP
     *
     * @return
     */
    private fun getLocalInetAddress(): InetAddress? {
        var ip: InetAddress? = null
        try {
            // 列举
            val en_netInterface = NetworkInterface
                .getNetworkInterfaces()
            while (en_netInterface.hasMoreElements()) { // 是否还有元素
                val ni = en_netInterface
                    .nextElement() as NetworkInterface // 得到下一个元素
                val en_ip = ni.inetAddresses // 得到一个ip地址的列举
                while (en_ip.hasMoreElements()) {
                    ip = en_ip.nextElement()
                    if (!ip.isLoopbackAddress
                        && ip.hostAddress.indexOf(":") == -1
                    ) break
                    else ip = null
                }

                if (ip != null) {
                    break
                }
            }
        } catch (e: SocketException) {
            e.printStackTrace()
        }
        return ip
    }

    /**
     * 获取本地IP
     *
     * @return
     */
    private fun getLocalIpAddress(): String? {
        try {
            val en = NetworkInterface
                .getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf
                    .inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress) {
                        return inetAddress.hostAddress.toString()
                    }
                }
            }
        } catch (ex: SocketException) {
            ex.printStackTrace()
        }
        return null
    }

    /**
     * 获取设备HardwareAddress地址
     *
     * @return
     */
    fun getMachineHardwareAddress(): String? {
        var interfaces: Enumeration<NetworkInterface?>? = null
        try {
            interfaces = NetworkInterface.getNetworkInterfaces()
        } catch (e: SocketException) {
            e.printStackTrace()
        }
        var hardWareAddress: String? = null
        var iF: NetworkInterface? = null
        if (interfaces == null) {
            return null
        }
        while (interfaces.hasMoreElements()) {
            iF = interfaces.nextElement()
            try {
                hardWareAddress = bytesToString(iF!!.hardwareAddress)
                if (hardWareAddress != null) break
            } catch (e: SocketException) {
                e.printStackTrace()
            }
        }
        return hardWareAddress
    }

    /***
     * byte转为String
     *
     * @param bytes
     * @return
     */
    private fun bytesToString(bytes: ByteArray?): String? {
        if (bytes == null || bytes.size == 0) {
            return null
        }
        val buf = java.lang.StringBuilder()
        for (b in bytes) {
            buf.append(String.format("%02X:", b))
        }
        if (buf.length > 0) {
            buf.deleteCharAt(buf.length - 1)
        }
        return buf.toString()
    }

    /**
     * 根据busybox获取本地Mac
     *
     * @return
     */
    fun getLocalMacAddressFromBusybox(): String {
        var result: String? = ""
        var Mac = ""
        result = callCmd("busybox ifconfig", "HWaddr")
        // 如果返回的result == null，则说明网络不可取
        if (result == null) {
            return "网络异常"
        }
        // 对该行数据进行解析
        // 例如：eth0 Link encap:Ethernet HWaddr 00:16:E8:3E:DF:67
        if (result.isNotEmpty() && result.contains("HWaddr")) {
            Mac = result.substring(
                result.indexOf("HWaddr") + 6,
                result.length - 1
            )
            result = Mac
        }
        return result
    }

    private fun callCmd(cmd: String, filter: String): String {
        var result = ""
        var line = ""
        try {
            val proc = Runtime.getRuntime().exec(cmd)
            val `is` = InputStreamReader(proc.inputStream)
            val br = BufferedReader(`is`)

            while ((br.readLine().also { line = it }) != null && !line.contains(filter)
            ) {
                result += line
            }

            result = line
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return result
    }
}