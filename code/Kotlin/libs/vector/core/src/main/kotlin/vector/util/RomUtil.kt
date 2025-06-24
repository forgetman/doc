@file:Suppress("unused")

package vector.util

import android.annotation.SuppressLint
import android.os.Build
import android.os.Environment
import android.text.TextUtils
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.Properties

/**
 * TODO: 网上代码, 未验证, 待整理
 */
object RomUtil {
    class RomInfo {
        var name: String? = null
        var version: String? = null
    }

    private val ROM_HUAWEI = arrayOf("huawei")
    private val ROM_VIVO = arrayOf("vivo")
    private val ROM_XIAOMI = arrayOf("xiaomi")
    private val ROM_OPPO = arrayOf("oppo")
    private val ROM_LEECO = arrayOf("leeco", "letv")
    private val ROM_360 = arrayOf("360", "qiku")
    private val ROM_ZTE = arrayOf("zte")
    private val ROM_ONEPLUS = arrayOf("oneplus")
    private val ROM_NUBIA = arrayOf("nubia")
    private val ROM_COOLPAD = arrayOf("coolpad", "yulong")
    private val ROM_LG = arrayOf("lg", "lge")
    private val ROM_GOOGLE = arrayOf("google")
    private val ROM_SAMSUNG = arrayOf("samsung")
    private val ROM_MEIZU = arrayOf("meizu")
    private val ROM_LENOVO = arrayOf("lenovo")
    private val ROM_SMARTISAN = arrayOf("smartisan")
    private val ROM_HTC = arrayOf("htc")
    private val ROM_SONY = arrayOf("sony")
    private val ROM_GIONEE = arrayOf("gionee", "amigo")
    private val ROM_MOTOROLA = arrayOf("motorola")
    private const val VERSION_PROPERTY_HUAWEI = "ro.build.version.emui"
    private const val VERSION_PROPERTY_VIVO = "ro.vivo.os.build.display.id"
    private const val VERSION_PROPERTY_XIAOMI = "ro.build.version.incremental"
    private const val VERSION_PROPERTY_OPPO = "ro.build.version.opporom"
    private const val VERSION_PROPERTY_LEECO = "ro.letv.release.version"
    private const val VERSION_PROPERTY_360 = "ro.build.uiversion"
    private const val VERSION_PROPERTY_ZTE = "ro.build.MiFavor_version"
    private const val VERSION_PROPERTY_ONEPLUS = "ro.rom.version"
    private const val VERSION_PROPERTY_NUBIA = "ro.build.rom.id"
    private const val UNKNOWN = "unknown"

    /**
     * Return whether the rom is made by huawei.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    val isHUAWEI: Boolean
        get() = ROM_HUAWEI[0] == romInfo.name

    /**
     * Return whether the rom is made by vivo.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    val isVIVO: Boolean
        get() = ROM_VIVO[0] == romInfo.name

    /**
     * Return whether the rom is made by xiaomi.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    val isMIUI: Boolean
        get() = ROM_XIAOMI[0] == romInfo.name

    /**
     * Return whether the rom is made by oppo.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    val isOPPO: Boolean
        get() = ROM_OPPO[0] == romInfo.name

    /**
     * Return whether the rom is made by leeco.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    val isLeeco: Boolean
        get() = ROM_LEECO[0] == romInfo.name

    /**
     * Return whether the rom is made by 360.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    fun is360(): Boolean {
        return ROM_360[0] == romInfo.name
    }

    /**
     * Return whether the rom is made by zte.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    val isZte: Boolean
        get() = ROM_ZTE[0] == romInfo.name

    /**
     * Return whether the rom is made by oneplus.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    val isOneplus: Boolean
        get() = ROM_ONEPLUS[0] == romInfo.name

    /**
     * Return whether the rom is made by nubia.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    val isNubia: Boolean
        get() = ROM_NUBIA[0] == romInfo.name

    /**
     * Return whether the rom is made by coolpad.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    val isCoolpad: Boolean
        get() = ROM_COOLPAD[0] == romInfo.name

    /**
     * Return whether the rom is made by lg.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    val isLg: Boolean
        get() = ROM_LG[0] == romInfo.name

    /**
     * Return whether the rom is made by google.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    val isGoogle: Boolean
        get() = ROM_GOOGLE[0] == romInfo.name

    /**
     * Return whether the rom is made by samsung.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    val isSamsung: Boolean
        get() = ROM_SAMSUNG[0] == romInfo.name

    /**
     * Return whether the rom is made by meizu.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    val isMeizu: Boolean
        get() = ROM_MEIZU[0] == romInfo.name

    /**
     * Return whether the rom is made by lenovo.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    val isLenovo: Boolean
        get() = ROM_LENOVO[0] == romInfo.name

    /**
     * Return whether the rom is made by smartisan.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    val isSmartisan: Boolean
        get() = ROM_SMARTISAN[0] == romInfo.name

    /**
     * Return whether the rom is made by htc.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    val isHtc: Boolean
        get() = ROM_HTC[0] == romInfo.name

    /**
     * Return whether the rom is made by sony.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    val isSony: Boolean
        get() = ROM_SONY[0] == romInfo.name

    /**
     * Return whether the rom is made by gionee.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    val isGionee: Boolean
        get() = ROM_GIONEE[0] == romInfo.name

    /**
     * Return whether the rom is made by motorola.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    val isMotorola: Boolean
        get() = ROM_MOTOROLA[0] == romInfo.name

    val romInfo: RomInfo by lazy {
        val info = RomInfo()
        val brand = DeviceUtil.brand
        val manufacturer = DeviceUtil.manufacturer

        when {
            isRightRom(brand, manufacturer, *ROM_HUAWEI) -> {
                info.name = ROM_HUAWEI[0]
                val version = getRomVersion(VERSION_PROPERTY_HUAWEI)
                val temp = version.split("_".toRegex()).toTypedArray()
                if (temp.size > 1) {
                    info.version = temp[1]
                } else {
                    info.version = version
                }
            }

            isRightRom(brand, manufacturer, *ROM_VIVO) -> {
                info.name = ROM_VIVO[0]
                info.version = getRomVersion(VERSION_PROPERTY_VIVO)
            }

            isRightRom(brand, manufacturer, *ROM_XIAOMI) -> {
                info.name = ROM_XIAOMI[0]
                info.version = getRomVersion(VERSION_PROPERTY_XIAOMI)
            }

            isRightRom(brand, manufacturer, *ROM_OPPO) -> {
                info.name = ROM_OPPO[0]
                info.version = getRomVersion(VERSION_PROPERTY_OPPO)
            }

            isRightRom(brand, manufacturer, *ROM_LEECO) -> {
                info.name = ROM_LEECO[0]
                info.version = getRomVersion(VERSION_PROPERTY_LEECO)
            }

            isRightRom(brand, manufacturer, *ROM_360) -> {
                info.name = ROM_360[0]
                info.version = getRomVersion(VERSION_PROPERTY_360)
            }

            isRightRom(brand, manufacturer, *ROM_ZTE) -> {
                info.name = ROM_ZTE[0]
                info.version = getRomVersion(VERSION_PROPERTY_ZTE)
            }

            isRightRom(brand, manufacturer, *ROM_ONEPLUS) -> {
                info.name = ROM_ONEPLUS[0]
                info.version = getRomVersion(VERSION_PROPERTY_ONEPLUS)
            }

            isRightRom(brand, manufacturer, *ROM_NUBIA) -> {
                info.name = ROM_NUBIA[0]
                info.version = getRomVersion(VERSION_PROPERTY_NUBIA)
            }

            else -> {
                when {
                    isRightRom(brand, manufacturer, *ROM_COOLPAD) -> {
                        info.name = ROM_COOLPAD[0]
                    }

                    isRightRom(brand, manufacturer, *ROM_LG) -> {
                        info.name = ROM_LG[0]
                    }

                    isRightRom(brand, manufacturer, *ROM_GOOGLE) -> {
                        info.name = ROM_GOOGLE[0]
                    }

                    isRightRom(brand, manufacturer, *ROM_SAMSUNG) -> {
                        info.name = ROM_SAMSUNG[0]
                    }

                    isRightRom(brand, manufacturer, *ROM_MEIZU) -> {
                        info.name = ROM_MEIZU[0]
                    }

                    isRightRom(brand, manufacturer, *ROM_LENOVO) -> {
                        info.name = ROM_LENOVO[0]
                    }

                    isRightRom(brand, manufacturer, *ROM_SMARTISAN) -> {
                        info.name = ROM_SMARTISAN[0]
                    }

                    isRightRom(brand, manufacturer, *ROM_HTC) -> {
                        info.name = ROM_HTC[0]
                    }

                    isRightRom(brand, manufacturer, *ROM_SONY) -> {
                        info.name = ROM_SONY[0]
                    }

                    isRightRom(brand, manufacturer, *ROM_GIONEE) -> {
                        info.name = ROM_GIONEE[0]
                    }

                    isRightRom(brand, manufacturer, *ROM_MOTOROLA) -> {
                        info.name = ROM_MOTOROLA[0]
                    }

                    else -> {
                        info.name = manufacturer
                    }

                }
                info.version = getRomVersion("")
            }
        }

        info
    }

    private fun isRightRom(brand: String, manufacturer: String, vararg names: String): Boolean {
        for (name in names) {
            if (brand.contains(name) || manufacturer.contains(name)) {
                return true
            }
        }
        return false
    }

    private fun getRomVersion(propertyName: String): String {
        var ret = ""
        if (!TextUtils.isEmpty(propertyName)) {
            ret = getSystemProperty(propertyName)
        }
        if (TextUtils.isEmpty(ret) || ret == UNKNOWN) {
            try {
                val display = Build.DISPLAY
                if (!TextUtils.isEmpty(display)) {
                    ret = display.lowercase()
                }
            } catch (ignore: Throwable) { /**/
            }
        }
        return if (TextUtils.isEmpty(ret)) {
            UNKNOWN
        } else ret
    }

    private fun getSystemProperty(name: String): String {
        var prop = getSystemPropertyByShell(name)
        if (!TextUtils.isEmpty(prop)) {
            return prop
        }
        prop = getSystemPropertyByStream(name)
        if (!TextUtils.isEmpty(prop)) {
            return prop
        }
        return if (Build.VERSION.SDK_INT < 28) {
            getSystemPropertyByReflect(name)
        } else prop
    }

    private fun getSystemPropertyByShell(propName: String): String {
        var input: BufferedReader? = null
        try {
            val p = Runtime.getRuntime().exec("getprop $propName")
            input = BufferedReader(InputStreamReader(p.inputStream), 1024)
            val ret = input.readLine()
            if (ret != null) {
                return ret
            }
        } catch (ignore: IOException) {
        } finally {
            if (input != null) {
                try {
                    input.close()
                } catch (ignore: IOException) { /**/
                }
            }
        }
        return ""
    }

    private fun getSystemPropertyByStream(key: String): String {
        try {
            val prop = Properties()
            val `is` = FileInputStream(
                File(Environment.getRootDirectory(), "build.prop")
            )
            prop.load(`is`)
            return prop.getProperty(key, "")
        } catch (ignore: Exception) { /**/
        }
        return ""
    }

    private fun getSystemPropertyByReflect(key: String): String {
        try {
            @SuppressLint("PrivateApi") val clz = Class.forName("android.os.SystemProperties")
            val getMethod = clz.getMethod("get", String::class.java, String::class.java)
            return getMethod.invoke(clz, key, "") as String
        } catch (e: Exception) { /**/
        }
        return ""
    }

}